package com.example.capstone.Message_

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.Adapters.UserAdapter
import com.example.capstone.List.User
import com.example.capstone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserInbox : AppCompatActivity() {

    private lateinit var messagesRecyclerView: RecyclerView
    private lateinit var userList : ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var database : DatabaseReference
    private lateinit var auth : FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_inbox)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        val db = database.child("users")

        userList = arrayListOf()
        adapter = UserAdapter(userList)
        messagesRecyclerView = findViewById(R.id.messages)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this)
        messagesRecyclerView.adapter = adapter


        db.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged", "SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()

                for (postSnapshot in snapshot.children) {
                    val currentUser = postSnapshot.getValue(User::class.java)

//                    Log.d("Test", postSnapshot.toString())
                    if (auth.currentUser?.email != currentUser?.email) {
                        userList.add(currentUser!!)
                    }
                    val adapter = UserAdapter(userList)
                    messagesRecyclerView.adapter = adapter
                    adapter.onItemClickListener(object : UserAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@UserInbox, UserSendMessage::class.java)
                            intent.putExtra("fname", userList[position].fName)
                            intent.putExtra("lname", userList[position].lName)
                            intent.putExtra("uid", userList[position].uid)
                            startActivity(intent)
                        }

                    })
                }
                adapter.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}