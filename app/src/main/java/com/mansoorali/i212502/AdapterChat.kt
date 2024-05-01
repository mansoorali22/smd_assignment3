package com.mansoorali.i212502

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerDataAdapter(private val items:MutableList<chatMessageModel>)
    :RecyclerView.Adapter<RecyclerDataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.chat_send_recyler,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text= items[position].message
        holder.value.text=items[position].time.toString()
    }
    class ViewHolder (itemView: View):RecyclerView.ViewHolder(itemView){
        val name:TextView=itemView.findViewById(R.id.msg)
        val value:TextView=itemView.findViewById(R.id.time)
    }
}
