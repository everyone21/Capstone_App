package com.example.capstone.NotYetAvailable

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.capstone.R

class ReportInformation : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_information)

        val titletxt: TextView = findViewById(R.id.reporttitle)
        val descriptiontxt: TextView = findViewById(R.id.reportdesc)
        val datetxt: TextView = findViewById(R.id.date)
        val image: ImageView = findViewById(R.id.mediaImageView)
        val username: TextView = findViewById(R.id.userEmail)
        val imageFrame: FrameLayout = findViewById(R.id.imageFrame)

        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val description = intent.getStringExtra("description")
        val mediaURL = intent.getStringExtra("mediaURL")
        val userMail = intent.getStringExtra("UserID")


        titletxt.text = title
        datetxt.text = date
        descriptiontxt.text = description
        username.text = userMail

        if (!mediaURL.isNullOrEmpty()) {
            imageFrame.visibility = ImageView.VISIBLE
            Glide.with(this)
                .load(mediaURL)
                .into(image)
        }

    }
}