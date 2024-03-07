package com.example.surveypal.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.surveypal.DataModels.Survey
import com.example.surveypal.DataModels.SurveyQuestions
import com.example.surveypal.R

class SurveyQuestionContainerAdapter(
    private val context: Context,
    private val dataList: List<SurveyQuestions>?
) : RecyclerView.Adapter<SurveyQuestionContainerAdapter.ViewHolder>() {

    private var onOptionSelected: ((String, Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.survey_question_container, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = dataList?.get(position)
        if (data != null) {
            holder.title.text = data.QuestionName

            holder.RadioContainer.removeAllViews() // Clear existing radio buttons

            for (item in data.Options.orEmpty()) {
                val radioButton = RadioButton(context).apply {
                    text = item
                    setOnClickListener {
                        // Invoke the callback with the question name and selected option
                        onOptionSelected?.invoke(item, position)
                    }
                }
                holder.RadioContainer.addView(radioButton)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.textView11)
        val RadioContainer = itemView.findViewById<RadioGroup>(R.id.radioGc)
    }

    // Setter method for the callback
    fun setOnOptionSelectedListener(listener: (String, Int) -> Unit) {
        this.onOptionSelected = listener
    }
}
