package com.example.surveypal.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.surveypal.R
import com.example.surveypal.databinding.FragmentSurveyCompleteBinding


class survey_complete : Fragment() {
private lateinit var binding:FragmentSurveyCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentSurveyCompleteBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        val handler = Handler(Looper.getMainLooper())

        // Define the task to be executed after 5 seconds
        val runnable = Runnable {
            findNavController().navigate(R.id.action_survey_complete_to_homePage2)



        }

        // Schedule the task to run after 5 seconds
        handler.postDelayed(runnable, 8000L)

        binding.home.setOnClickListener {
            findNavController().popBackStack(R.id.homeFrag, false)

        }

    }


}