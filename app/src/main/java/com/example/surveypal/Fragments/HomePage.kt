package com.example.surveypal.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surveypal.Adapter.ActiveSureysAdapter
import com.example.surveypal.DataModels.Survey
import com.example.surveypal.DataModels.SurveyQuestions
import com.example.surveypal.R
import com.example.surveypal.databinding.FragmentHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private val TAG="HomePage"
class HomePage : Fragment() {
    private lateinit var binding: FragmentHomePageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter:ActiveSureysAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomePageBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth=FirebaseAuth.getInstance()
        database=FirebaseDatabase.getInstance()

        binding.addsurvey.setOnClickListener {
            if(auth.uid!==null) {
                findNavController().navigate(R.id.action_homePage_to_create_New_Survey)
            } else {
                Toast.makeText(requireContext(), "Please Login To add surveys", Toast.LENGTH_SHORT).show()
            }

        }





        // Assuming searchView is your SearchView instance
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // This method is called when the user submits the query (e.g., presses the search button)
                return true // Return true to indicate that the query has been handled
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // This method is called when the text in the search view changes
                if (!newText.isNullOrEmpty()) {
                    // If there is text in the search view, log it
                    Log.d("SearchQuery", "Text: $newText")

                    getSurveys(newText)
                }
                return true // Return true to indicate that the query has been handled
            }
        })


        binding.progressBar3.visibility=View.VISIBLE
        getData()



          }

    private fun getData() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Active Surveys")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val surveysList: MutableList<Survey> = mutableListOf()
                val surveysIDs: MutableList<String> = mutableListOf()
                for (surveySnapshot in dataSnapshot.children) {
                    val survey = surveySnapshot.getValue(Survey::class.java)
                    if (survey != null) {
                        surveysList.add(survey)
                        surveysIDs.add(surveySnapshot.key!!)
                    }
                }
                    adapter= ActiveSureysAdapter(requireContext(),surveysList,surveysIDs)
                adapter.OnClick= { surveys, surveyID ->


                    val action = HomePageDirections.actionHomePageToSurvey(surveys, surveyID)
                    findNavController().navigate(action)
                }

                binding.surveysContainer.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                binding.surveysContainer.adapter=adapter
                binding.progressBar3.visibility=View.GONE

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
                Log.d(TAG, "onCancelled: ${databaseError.message}")
            }
        })


    }


    private fun getSurveys(search:String) {

        binding.progressBar3.visibility=View.VISIBLE
        // Assuming you have a DatabaseReference pointing to your database
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

        // Get the current user's ID
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        // If userID is not null, construct and execute the query
        userID?.let { uid ->
            // Construct the query to filter data based on userID
            databaseReference.child("Active Surveys").orderByChild("title").equalTo(search)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // Iterate through the dataSnapshot to access the filtered data
                        val surveys: MutableList<Survey> = mutableListOf()

                        for (snapshot in dataSnapshot.children) {
                            // Extract survey data
                            val surveyID = snapshot.key
                            val surveyTitle = snapshot.child("title").getValue(String::class.java)
                            val surveyDescription = snapshot.child("description").getValue(String::class.java)

                            // Process survey questions
                            val questions: MutableList<SurveyQuestions> = mutableListOf()
                            for (questionSnapshot in snapshot.child("questions").children) {
                                val questionName = questionSnapshot.key
                                val options: MutableList<String> = mutableListOf()
                                questionSnapshot.child("options").children.forEach {
                                    options.add(it.getValue(String::class.java)!!)
                                }
                                val surveyQuestion = SurveyQuestions(questionName!!, options)
                                questions.add(surveyQuestion)
                            }

                            // Create a Survey object and add it to the list
                            val survey = Survey(surveyTitle!!, surveyDescription!!, questions, surveyID!!)
                            surveys.add(survey)
                        }

                        // Here you can use the `surveys` list as needed
                        // For example, you can pass it to a ViewModel or update UI components
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error
                        println("Database Error: ${databaseError.message}")
                    }
                })
        }

        // Hide progress bar after getting surveys
        binding.progressBar3.visibility = View.GONE
    }



}