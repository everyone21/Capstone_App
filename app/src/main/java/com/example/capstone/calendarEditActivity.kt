package com.example.capstone

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

class calendarEditActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_edit2)

        db = FirebaseFirestore.getInstance()


        val title: TextView = findViewById(R.id.what)
        val date: TextView = findViewById(R.id.when_)
        val desc: TextView = findViewById(R.id.where_1)
        val time: TextView = findViewById(R.id.time)
        val update: Button = findViewById(R.id.updt)

        val bundle : Bundle?= intent.extras
        val titl = bundle?.getString("title")
        val dat = bundle?.getString("date")
        val descr = bundle?.getString(("place"))
        val tim = bundle?.getString(("time"))


        title.text = titl
        date.text = dat
        desc.text = descr
        time.text = tim

        val a = title.toString()
        update.setOnClickListener {
            val upTitle = title.text.toString()
            val upDate= date.text.toString()
            val upDesc = desc.text.toString()
            val upTime = time.text.toString()

            val updateMap = mapOf(
                "eventDate" to upDate,
                "eventPlace" to upDesc,
                "eventTime" to upTime,
                "eventTitle" to upTitle
            )
            db.collection("EventsAnnouncement").document(a).update(updateMap)
            Toast.makeText(this, "Updated Successfully", Toast.LENGTH_SHORT).show()
        }

        }
}