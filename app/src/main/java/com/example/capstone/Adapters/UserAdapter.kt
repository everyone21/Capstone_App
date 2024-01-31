package com.example.capstone.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.List.User
import com.example.capstone.R
import com.google.firebase.auth.FirebaseAuth

class UserAdapter(private var userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun onItemClickListener(listener: onItemClickListener){
        mListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)

        return ViewHolder(view, mListener)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser = userList[position]

        holder.textName.text = currentUser.fName
        holder.textLname.text = currentUser.lName
//        holder.itemView.setOnClickListener{
//            val intent = Intent(context, user_send_message::class.java)
//
//            intent.putExtra("name", currentUser.name)
//            intent.putExtra("uid", currentUser.uid)
//
//            context.startActivity(intent)
//        }

    }

    class ViewHolder(itemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.fname)
        val textLname: TextView = itemView.findViewById(R.id.lname)


        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}