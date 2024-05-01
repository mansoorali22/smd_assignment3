package com.mansoorali.i212502

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerChatAdapter(private val items:MutableList<chatMessageModel>)
    :RecyclerView.Adapter<RecyclerChatAdapter.ViewHolder>() {
    private var defulturi:String="https://firebasestorage.googleapis.com/v0/b/mentor-me-6558f.appspot.com/o/Pictures%2Fextra?alt=media&token=bf8bc9bc-f2c8-4faf-bd35-46b5054fd72e"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.chat_recieve_recyler,parent,false)
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
        val pic:com.google.android.material.imageview.ShapeableImageView=itemView.findViewById(R.id.profilepic)
    }
}
