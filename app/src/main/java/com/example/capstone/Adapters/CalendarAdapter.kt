package com.example.capstone.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.List.Events
import com.example.capstone.R

class calendarAdapter(private val eventsList: ArrayList<Events>) : RecyclerView.Adapter<calendarAdapter.MyViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun onItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    class MyViewHolder(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
        val Title: TextView = itemView.findViewById(R.id.titleTextView)
        val where: TextView = itemView.findViewById(R.id.where_)
        val Date: TextView = itemView.findViewById(R.id.dateTextView)
        val time: TextView = itemView.findViewById(R.id.time)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.anouncement_view, parent, false)
        return MyViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.Title.text = eventsList[position].eventTitle
        holder.Date.text = eventsList[position].eventDate
        holder.where.text = eventsList[position].eventPlace
        holder.time.text = eventsList[position].eventTime



    }

    override fun getItemCount(): Int = eventsList.size


//    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
//        val Title: TextView
//        val Desc: TextView
//        val Date: TextView
//
//        init {
//            Title = itemView.findViewById(R.id.titleTextView)
//            Desc = itemView.findViewById(R.id.descriptionTextView)
//            Date = itemView.findViewById(R.id.dateTextView)
//        }
//    }
}