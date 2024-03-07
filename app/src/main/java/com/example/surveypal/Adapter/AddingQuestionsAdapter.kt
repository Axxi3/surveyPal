package com.example.surveypal.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.surveypal.DataModels.SurveyQuestions
import com.example.surveypal.R

private val TAG="AddingQuestionsAdapterRecyclerView"
class AddingQuestionsAdapterRecyclerView(private val contexter: Context,private val dataList: List<SurveyQuestions>):
    RecyclerView.Adapter<AddingQuestionsAdapterRecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.added_question_shower, parent, false) // Replace with your item layout
        return ViewHolder(itemView)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d(TAG, "onBindViewHolder: Helloo i am being called")
       val data=dataList[position]
        for (item in data.Options!!){
            val radioButton= RadioButton(contexter)
            radioButton.text=item
            holder.container.addView(radioButton)

        }

        holder.QuestionName.text=data.QuestionName
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {

         val container=itemView.findViewById<RadioGroup>(R.id.questionAddedContainer)
        val QuestionName=itemView.findViewById<TextView>(R.id.nameer)

    }
}