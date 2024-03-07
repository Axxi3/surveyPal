package com.example.surveypal.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.surveypal.Adapter.SurveyQuestionContainerAdapter
import com.example.surveypal.DataModels.AttemptedSurveys
import com.example.surveypal.DataModels.Survey
import com.example.surveypal.DataModels.SurveyQuestions
import com.example.surveypal.R
import com.example.surveypal.databinding.FragmentSurveyBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private val TAG="SurveyFragment"
class Survey : Fragment() {
    private lateinit var binding:FragmentSurveyBinding
    private lateinit var adapter: SurveyQuestionContainerAdapter
    private val args:SurveyArgs by navArgs()
    private val selectedOptions: MutableList<String> = mutableListOf()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth:FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSurveyBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore=FirebaseFirestore.getInstance()
        auth=FirebaseAuth.getInstance()
        val surveys: Survey? = activity?.intent?.getParcelableExtra("surveys")
        val surveyID: String? = activity?.intent?.getStringExtra("surveyID")
//        binding.appCompatButton.setOnClickListener {
//            binding.appCompatButton.startAnimation()
//            onSaveButtonClick()
//        }

        args?.let { args ->
            val question = args.Surveys.Questions
            binding.question.text=args.Surveys.Title
            adapter = SurveyQuestionContainerAdapter(requireContext(), emptyList<SurveyQuestions>())
            if (question != null ) {
                var i=0
                sendQuestion(i,question)

                binding.appCompatButton.setOnClickListener {
                    adapter.setOnOptionSelectedListener {Option, index ->
                        selectedOptions.add(index,Option)
                    }

                    Log.d(TAG, "onViewCreated: "+"index ${i} question size ${question.size}")
                    if (i >= question.size-1) {

                        Log.d(TAG, "onViewCreated: Called")
                        binding.appCompatButton.startAnimation()
                        onSaveButtonClick()
                        return@setOnClickListener
                    }
                    i+=1
                    sendQuestion(i,question)

                }


                binding.recyclerView.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                binding.recyclerView.adapter=adapter
            }
        }
    }



    @SuppressLint("NotifyDataSetChanged")
    private fun sendQuestion(i: Int, questionOne: List<SurveyQuestions>) {

        // Update adapter data
        adapter.setData(questionOne[i])
        Log.d(TAG, "sendQuestion: "+ questionOne[i])
        // Notify RecyclerView that data set has changed
        adapter.notifyDataSetChanged()
    }

    fun onSaveButtonClick() {


        // Check if all options are selected
        if (selectedOptions.size == args.Surveys.Questions?.size!! -1) {
            Log.d(TAG, "onSaveButtonClick: "+ selectedOptions.toString())

            saveData(selectedOptions)

        } else {
            // Not all options are selected, display a message to the user
            Toast.makeText(requireContext(), "Please select all options", Toast.LENGTH_SHORT).show()
            binding.appCompatButton.revertAnimation()
        }
    }

    private fun saveData(selectedOptions: MutableList<String>) {
        firestore.collection("users").document(auth.uid!!).collection("Attempted Surveys").add(AttemptedSurveys(selectedOptions.toList(),args.SurveyID))
            .addOnSuccessListener {
                Log.d(TAG, "saveData: Data added SuccessFully")
                binding.appCompatButton.revertAnimation()
                findNavController().navigate(R.id.action_survey_to_survey_complete)

            }.addOnFailureListener {
                Log.d(TAG, "saveData: ${it.message}")
            }
    }
}
