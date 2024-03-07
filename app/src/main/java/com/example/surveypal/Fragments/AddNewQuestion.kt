package com.example.surveypal.Fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.surveypal.DataModels.SurveyQuestions
import com.example.surveypal.R
import com.example.surveypal.databinding.FragmentAddNewQuestionBinding
import com.example.surveypal.databinding.NewOptionDialogBinding
import com.example.surveypal.utils.SurveyViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

private val TAG="Add new Questions"
class AddNewQuestion : Fragment() {
private  val ListOfQuestions:MutableList<SurveyQuestions> = mutableListOf()
    private lateinit var binding:FragmentAddNewQuestionBinding
    private lateinit var sheetBinding:NewOptionDialogBinding
    private val options: MutableList<String> = mutableListOf()
    private  val args:AddNewQuestionArgs by navArgs()
    private val viewModel: SurveyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddNewQuestionBinding.inflate(inflater)
        sheetBinding= NewOptionDialogBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var Dialog=BottomSheetDialog(requireContext(),R.layout.new_option_dialog)
        Dialog.setContentView(sheetBinding.root)
        binding.addOptions.setOnClickListener {
           Dialog.show()
        }

        sheetBinding.add.setOnClickListener {
            options.add(sheetBinding.editText2.text.toString())
            Dialog.dismiss()
            Log.d(TAG, "onViewCreated: "+options)
            sheetBinding.editText2.text.clear()
            addOptions()

        }



//        binding.done.setOnClickListener {
//            val newSurveyQuestion = SurveyQuestions(binding.QuestionName.getTextValue, options.toTypedArray())
//            viewModel.surveyQuestions.value?.let { currentSurveyQuestions ->
//                // Create a new list by adding the new SurveyQuestion to the existing list
//                val updatedSurveyQuestions = currentSurveyQuestions.toMutableList().apply {
//                    add(newSurveyQuestion)
//                }
//                // Update the LiveData with the updated list of SurveyQuestions
//                viewModel.surveyQuestions.value = updatedSurveyQuestions
//            }
//            var action: NavDirections = AddNewQuestionDirections.actionAddNewQuestionToCreateNewSurvey()
//
//            val optionsArray = options.toTypedArray()
//
//            args?.let { args ->
//                val question = args.PreviousQuestions
//                if (question == null) {
//                    val arrayQuestion: MutableList<SurveyQuestions> = mutableListOf(newSurveyQuestion)
//                    action = Create_New_SurveyDirections.actionCreateNewSurveyToAddNewQuestion(arrayQuestion.toTypedArray())
//                } else {
//                    val newData = question.toMutableList().apply {
//                        add(newSurveyQuestion)
//                    }
//                    action = AddNewQuestionDirections.actionAddNewQuestionToCreateNewSurvey(newData.toTypedArray())
//                }
//            }
//
//            findNavController().navigate(action)
//
//        }



        binding.done.setOnClickListener {
            if(binding.QuestionName.getTextValue.isEmpty()){
                Toast.makeText(requireContext(), "Please Type Question Name", Toast.LENGTH_SHORT)
                    .show()
            }

            if(options.size<2){
                Toast.makeText(requireContext(), "Please add atleast 2 options", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val newSurveyQuestion = SurveyQuestions(binding.QuestionName.getTextValue, options)

            // Save the new survey question along with any previously added questions to local storage
            saveSurveyQuestions(requireContext(), newSurveyQuestion)
            val surveyQuestions = getSurveyQuestions(requireContext())
            Log.d("SurveyQuestions", surveyQuestions.toString())
            // Navigate to the appropriate destination
            var action: NavDirections = AddNewQuestionDirections.actionAddNewQuestionToCreateNewSurvey()
            // ...
            findNavController().navigate(action)
        }




    }



    // Function to save the list of survey questions to local storage
    fun saveSurveyQuestions(context: Context, newSurveyQuestion: SurveyQuestions) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()

        // Retrieve existing survey questions from local storage
        val existingSurveyQuestions = getSurveyQuestions(context)

        // Add the new survey question to the existing list
        existingSurveyQuestions.add(newSurveyQuestion)

        // Serialize the list of survey questions to JSON
        val json = Gson().toJson(existingSurveyQuestions)

        // Save the updated list back to local storage
        editor.putString("survey_questions", json)
        editor.apply()
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

    private fun addOptions() {
        binding.optionContainer.removeAllViews()
        for (RadioOptions in options){
            val radioButton=RadioButton(requireContext())
            radioButton.text=RadioOptions
            binding.optionContainer.addView(radioButton)
        }
    }
//    override fun onOptionAdded(option: String) {
//        // Log the added option
//        Log.d("NewOptionAdded")
//    }

}