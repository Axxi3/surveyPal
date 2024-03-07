package com.example.surveypal.Fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.surveypal.Adapter.AddingQuestionsAdapterRecyclerView
import com.example.surveypal.DataModels.Survey

import com.example.surveypal.DataModels.SurveyQuestions
import com.example.surveypal.R
import com.example.surveypal.databinding.FragmentCreateNewSurveyBinding
import com.example.surveypal.utils.SurveyViewModel
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.common.reflect.TypeToken
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson

private val TAG="Create_new_survey"
class Create_New_Survey : Fragment() {

    private lateinit var MyAdapter:AddingQuestionsAdapterRecyclerView
    private val viewModel: SurveyViewModel by viewModels()
    private lateinit var binding: FragmentCreateNewSurveyBinding
    private val args: Create_New_SurveyArgs by navArgs()
    private val surveyQuestions: MutableList<SurveyQuestions> = mutableListOf()
    private lateinit var auth:FirebaseAuth
    private lateinit var fireStore:FirebaseFirestore
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var imageURL:Uri
    private lateinit var storage:FirebaseStorage



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateNewSurveyBinding.inflate(inflater)
        return binding.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth=FirebaseAuth.getInstance()
        fireStore= FirebaseFirestore.getInstance()
        firebaseDatabase=FirebaseDatabase.getInstance()
        storage=FirebaseStorage.getInstance()

        Glide.with(requireContext()).load(R.drawable.addimage).into(binding.surveyimage)


        binding.surveyimage.setOnClickListener {
            pickanImageFromGallery()
        }


        binding.addQuestions.setOnClickListener {
            findNavController().navigate(R.id.action_create_New_Survey_to_addNewQuestion)
        }
        Log.d(TAG, "onViewCreated: ${args}")
//        args?.let { args ->
//            val question = args.Question
//            val options = args.Options
//            if (question != null && options != null) {
//                val surveyQuestion = SurveyQuestions(question, options)
//                // Add the new SurveyQuestion to the existing list
//                surveyQuestions.add(surveyQuestion)
//            }
//        }


//        viewModel.surveyQuestions.observe(requireActivity()) { surveyQuestions ->
//            // Log the data when it changes
//            Log.d("MainActivity", "Survey Questions: $surveyQuestions")
//        }
        val model = ViewModelProvider(requireActivity()).get(SurveyViewModel::class.java)


        model.surveyQuestions.observe(viewLifecycleOwner, Observer {

            Log.d(TAG, "onViewCreated: " + it.last())
        })

        val AddedQuestions=getSurveyQuestions(requireContext())

MyAdapter= AddingQuestionsAdapterRecyclerView(requireContext(),AddedQuestions)
        binding.QuestionShower.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)

        binding.QuestionShower.adapter=MyAdapter


        binding.done.setOnClickListener {
            if(binding.SurveyName.getTextValue.isEmpty()){
                Toast.makeText(requireContext(), "Survay name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(binding.SurveyDescription.getTextValue.isEmpty()){
                Toast.makeText(requireContext(), "Survay Description cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }




            binding.progressBar2.visibility=View.VISIBLE
            if(binding.SurveyName.getTextValue.isNotEmpty() && binding.SurveyDescription.getTextValue.isNotEmpty() && AddedQuestions.size!== 0){

                if(binding.surveyimage.drawable.constantState == ContextCompat.getDrawable(requireContext(), R.drawable.addimage)?.constantState){
                    addData(AddedQuestions,null)
                }

                uploadImage(AddedQuestions)


        } else {
                Toast.makeText(
                    requireContext(),
                    "Please Write the name and descriptions",
                    Toast.LENGTH_SHORT
                ).show()
                binding.progressBar2.visibility=View.GONE
        }

        }

        binding.cancelButton.setOnClickListener {
            deleteSurveyQuestions(requireContext())
        }


    }

    private fun uploadImage(AddedQuestions: MutableList<SurveyQuestions>) {
        // Convert imageURL to URI
        val fileUri = Uri.parse(imageURL.toString())

        // Get reference to Firebase Storage
        val storageRef = storage.reference

        // Create a reference to 'Surveys_Image/image.jpg'
        val imageRef = storageRef.child("Surveys_Image/${fileUri.lastPathSegment}")

        // Upload file to Firebase Storage
        imageRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                // Image uploaded successfully
                Log.d(TAG, "uploadImage: Image uploaded successfully")




                // Get the download URL of the uploaded image
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    // URL of the uploaded image
                    val downloadUrl = uri.toString()


                    addData(AddedQuestions,downloadUrl)
                    Log.d(TAG, "uploadImage: Download URL: $downloadUrl")

                    // Now you can use the download URL as needed
                }.addOnFailureListener { exception ->
                    // Handle any errors retrieving the download URL
                    Log.e(TAG, "uploadImage: Error getting download URL", exception)
                }
            }
            .addOnFailureListener { exception ->
                // Handle any errors uploading the image
                Log.e(TAG, "uploadImage: Error uploading image", exception)
            }
    }


    private fun pickanImageFromGallery() {
        ImagePicker.with(this)
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            Glide.with(requireContext()).load(data?.data).into(binding.surveyimage)
            Log.d(TAG, "onActivityResult: "+data?.dataString)
            binding.surveyimage.setImageURI(data?.data)
        imageURL= data?.data!!

    }



    fun getSurveyQuestions(context: Context): MutableList<SurveyQuestions> {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val json = sharedPreferences.getString("survey_questions", null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<MutableList<SurveyQuestions>>() {}.type)
        } else {
            mutableListOf()
        }
    }

    private fun addData(AddedQuestions: MutableList<SurveyQuestions>, downloadUrl: String?): Any {
        val fileUri = Uri.parse(imageURL.toString())

        fireStore.collection("users").document(auth.uid!!).collection("Surveys")
            .document().set(Survey(binding.SurveyName.getTextValue,binding.SurveyDescription.getTextValue,AddedQuestions.toList(),auth.uid!!,downloadUrl)).addOnSuccessListener {
                Log.d(TAG, "addData: Data Added SuccessFully")
                addToRealtime(Survey(binding.SurveyName.getTextValue,binding.SurveyDescription.getTextValue,AddedQuestions.toList(),auth.uid!!,downloadUrl))
            }.addOnFailureListener {
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "addData: " +it.message.toString())
            }

return false
    }

    private fun addToRealtime(survey: Survey): Any? {
        // Get a reference to the "Active Surveys" node
        val activeSurveysRef = firebaseDatabase.getReference("Active Surveys")

        // Generate a unique key for the new survey
        val newSurveyRef = activeSurveysRef.push()

        // Set the value of the new survey using the generated key
        newSurveyRef.setValue(survey)
            .addOnSuccessListener {
                Log.d(TAG, "addData: Data Added SuccessFully")
                deleteSurveyQuestions(requireContext())
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), exception.message.toString(), Toast.LENGTH_SHORT).show()
                Log.d(TAG, "addData: ${exception.message}")
            }
        return false
    }


    fun deleteSurveyQuestions(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.remove("survey_questions")
        editor.apply()
        navigate()
    }

    private fun navigate() {
        binding.progressBar2.visibility=View.GONE
        Toast.makeText(requireContext(), "Survey added SuccessFully", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_create_New_Survey_to_homePage)
    }

}