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
import com.example.surveypal.R
import com.example.surveypal.databinding.FragmentHomePageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private val TAG="HomePage"
class HomePage : Fragment() {
    private lateinit var binding: FragmentHomePageBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adapter:ActiveSureysAdapter
    private lateinit var data: MutableList<Survey>

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
                if (newText.isNullOrEmpty()) {
                    // If newText is null or empty, it means nothing is written in the search view
                    Log.d("SearchQuery", "No text entered")
                    adapter.setData(data)
                    adapter.notifyDataSetChanged()
                } else {
                    // If there is text in the search view, log it and perform the desired action
                    Log.d("SearchQuery", "Text: $newText")
                    getSurveys(newText) // Example: Perform a search based on the entered text
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

                data=surveysList
                    adapter= ActiveSureysAdapter(requireContext(),surveysList,surveysIDs)
                adapter.OnClick= { surveys, surveyID ->
                    
                    if(auth.uid.isNullOrEmpty()){
                        Toast.makeText(
                            requireContext(),
                            "Please Login to Attempt Survey",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        val action = HomePageDirections.actionHomePageToSurvey(surveys, surveyID)
                        findNavController().navigate(action)
                    }



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

        if(search.isNullOrEmpty() || search ==""){
            adapter.setData(data)
            adapter.notifyDataSetChanged()
        }
        val searchData:MutableList<Survey> = mutableListOf()
    if(data!==null) {
        for(result in data){
            if(result.Title.toLowerCase().contains(search.toLowerCase())==true){
                searchData.add(result)
            }

        }

        adapter.setData(searchData)
        adapter.notifyDataSetChanged()
    }

    }



}