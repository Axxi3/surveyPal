package com.example.surveypal.DataModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Survey(
    var Title: String,
    var description:String,
    var Questions: List<SurveyQuestions>?,
    var SurveyMakerID:String
) : Parcelable {
    constructor() : this("","", listOf(),"")
}


