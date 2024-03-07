package com.example.surveypal.DataModels

import com.example.surveypal.Fragments.Survey

data class AttemptedSurveys(
    var optionsSelected:List<String>,
    var surveyID:String
)
