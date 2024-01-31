package com.example.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.List.Events

class calendarEditAdapter(private val eventsList2: ArrayList<Events>) : RecyclerView.Adapter<calendarEditAdapter.MyViewHolder2>(){



    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun onItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    class MyViewHolder2(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.calendar_edit_view, parent, false)
        return MyViewHolder2(itemView, mListener)
    }

    override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
        val events: Events = eventsList2[position]
        holder.Title.text = events.eventTitle
        holder.where.text = events.eventPlace
        holder.Date.text = events.eventDate
        holder.time.text = events.eventTime

    }

    override fun getItemCount(): Int = eventsList2.size


//    class MyViewHolder2(itemView : View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView){
//        val Title: TextView = itemView.findViewById(R.id.titleTextView)
//        val Desc: TextView = itemView.findViewById(R.id.descriptionTextView)
//        val Date: TextView = itemView.findViewById(R.id.dateTextView)
//
//        init {
//            itemView.setOnClickListener {
//                listener.onItemClick(adapterPosition)
//            }
//        }
//    }


}