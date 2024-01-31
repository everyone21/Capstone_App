package com.example.capstone.Dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.example.capstone.LoginPage
import com.example.capstone.R
import com.example.capstone.navigation

class EmergencyContacts : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency_contacts)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            // Navigate back to the dashboard
            val intent = Intent(this, navigation::class.java)
            startActivity(intent)
            finish()
        }

    }
}