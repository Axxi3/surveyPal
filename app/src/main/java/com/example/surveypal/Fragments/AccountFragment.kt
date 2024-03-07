package com.example.surveypal.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.surveypal.Adapter.ActiveSureysAdapter
import com.example.surveypal.DataModels.Survey
import com.example.surveypal.DataModels.SurveyQuestions
import com.example.surveypal.DataModels.User
import com.example.surveypal.LoginActivity
import com.example.surveypal.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

private val TAG="Account fragment"
class AccountFragment : Fragment() {
        private lateinit var binding:FragmentAccountBinding
        private lateinit var auth:FirebaseAuth
    private lateinit var adapter: ActiveSureysAdapter
        private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAccountBinding.inflate(inflater)
       return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth=FirebaseAuth.getInstance()
        firestore=FirebaseFirestore.getInstance()
        Log.d(TAG, "onViewCreated: " + auth.uid.toString())
        if(auth.uid!==null){
            binding.constraintLayout2.visibility=View.GONE
            binding.constraintLayout1.visibility=View.GONE

            binding.loading.visibility=View.VISIBLE
            getData()






        } else {
            binding.constraintLayout1.visibility=View.GONE
            binding.constraintLayout2.visibility=View.VISIBLE
            binding.loading.visibility=View.GONE
        }


        binding.signout.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Successfully logged out", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireActivity(),LoginActivity::class.java))
        }
        binding.signInbutton.setOnClickListener {
          startActivity(Intent(requireActivity(),LoginActivity::class.java))
        }




    }

    private fun getData() {
        binding.progressBar4.visibility=View.VISIBLE

        firestore.collection("users").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user=it.toObject(User::class.java)


                Glide.with(requireContext()).load(user?.pfp).into(binding.profileImage)
                binding.textView4.text=user!!.Name.toString()
                binding.email.text=user!!.email.toString()



                binding.yourSurveyContainer.layoutManager=
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)



                binding.constraintLayout1.visibility=View.VISIBLE
                binding.loading.visibility=View.GONE
            }

        getSurveys()
    }

    private fun getSurveys() {
        // Assuming you have a DatabaseReference pointing to your database
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference

        // Get the current user's ID
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        // If userID is not null, construct and execute the query
        userID?.let { uid ->
            // Construct the query to filter data based on userID
            databaseReference.child("Active Surveys").orderByChild("surveyMakerID").equalTo(uid)
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

                        adapter= ActiveSureysAdapter(requireContext(), surveys,null)
                        binding.yourSurveyContainer.adapter=adapter
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error
                        println("Database Error: ${databaseError.message}")
                    }
                })
        }

        // Hide progress bar after getting surveys
        binding.progressBar4.visibility = View.GONE
    }



}