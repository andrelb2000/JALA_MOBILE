package com.example.aula04_fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/****** RECYCLERVIEW CHANGES *****/
class ListNamesAdapter(private var names: List<String>) : RecyclerView.Adapter<ListNamesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tvListNameItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list_name, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = names[position]
    }

    override fun getItemCount() = names.size

    fun updateData(newNames: List<String>) {
        names = newNames
        notifyDataSetChanged()
    }
}
/********************************/
