package com.example.surveypal.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.surveypal.DataModels.Survey
import com.example.surveypal.R

class ActiveSureysAdapter(
    private val contexter: Context,
    private val dataList: List<Survey>?,
    private val surveysIDs: MutableList<String>?
):
    RecyclerView.Adapter<ActiveSureysAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.surveys, parent, false) // Replace with your item layout
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data= dataList?.get(position)
        if(data!= null){
            holder.title.text=data.Title
            holder.des.text=data.description

        }
        holder.itemView.setOnClickListener {
            data?.let { it1 -> OnClick?.invoke(it1, surveysIDs!![position]) }
        }

    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
            val title=itemView.findViewById<TextView>(R.id.title)
        val des=itemView.findViewById<TextView>(R.id.des)
    }

    var OnClick :((Survey,String)->Unit)?=null


}