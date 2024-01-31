package com.example.capstone.Message_

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.Adapters.MessageAdapter
import com.example.capstone.List.Message
import com.example.capstone.R
import com.example.capstone.navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserSendMessage : AppCompatActivity() {

    private lateinit var chatRecyclerValue: RecyclerView
    private lateinit var messageBox : EditText
    private lateinit var sendBtn : ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var fStore : FirebaseFirestore
    private lateinit var db: DatabaseReference

    var receiverRoom: String? = null
    var senderRoom: String? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_send_message)

        fStore = FirebaseFirestore.getInstance()
        val userData = fStore.collection("users")
        val name = intent.getStringExtra("name")


        val receiverUid = "zJWE9QmxXZhMF2bNec5l9XOtNHl1"
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            // Navigate back to the dashboard
            val intent = Intent(this, navigation::class.java)
            startActivity(intent)
            finish()
        }

        db = FirebaseDatabase.getInstance().reference
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        chatRecyclerValue = findViewById(R.id.messages)
        messageBox = findViewById(R.id.typing)
        sendBtn = findViewById(R.id.sendBtn)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)
        chatRecyclerValue.layoutManager = LinearLayoutManager(this)
        chatRecyclerValue.adapter = messageAdapter

        db.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener{
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for (postSnapshot in snapshot.children){
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)

                    }
                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        sendBtn.setOnClickListener{
            val sdf = SimpleDateFormat("MMMM dd, yyyy / HH:mm:ss aaa", Locale.getDefault())
            val currentDate = sdf.format(Date())

            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid, currentDate)

            db.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    db.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            messageBox.setText("")

        }

    }
}