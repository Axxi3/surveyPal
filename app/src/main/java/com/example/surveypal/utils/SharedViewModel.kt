package com.example.surveypal.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.surveypal.DataModels.SurveyQuestions

class SurveyViewModel : ViewModel() {
    val surveyQuestions: MutableLiveData<List<SurveyQuestions>> = MutableLiveData()
}