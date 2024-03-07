package com.example.surveypal.DataModels

data class User(
    var Name:String,
    var email:String,
    var UserID:String,
    var pfp:String
) {
        constructor():this("","","","")
}