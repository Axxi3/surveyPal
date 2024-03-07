package com.example.surveypal.DataModels

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SurveyQuestions(
    var QuestionName: String,
    var Options: List<String>?,

) : Parcelable {
    constructor() : this("", listOf())
}


