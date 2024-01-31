package com.example.capstone

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
class ReportDetails : AppCompatActivity() {
    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var mediaImageView: ImageView
    private lateinit var acceptButton: Button
    private lateinit var rejectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_details)

        titleTextView = findViewById(R.id.reportTitleTextView)
        dateTextView = findViewById(R.id.reportDateTextView)
        descriptionTextView = findViewById(R.id.reportDescriptionTextView)
        mediaImageView = findViewById(R.id.mediaImageView)
        acceptButton = findViewById(R.id.acceptButton)
        rejectButton = findViewById(R.id.rejectButton)

        // Update the UI with report details
        val title = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val description = intent.getStringExtra("description")
        val mediaURL = intent.getStringExtra("mediaURL")

        titleTextView.text = title
        dateTextView.text = date
        descriptionTextView.text = description

        if (!mediaURL.isNullOrEmpty()) {
            mediaImageView.visibility = ImageView.VISIBLE
            Glide.with(this)
                .load(mediaURL)
                .into(mediaImageView)
        }

        acceptButton.setOnClickListener {
            // Handle accept button
            updateReportStatus("Accepted")
            val intent = Intent (this, navigation::class.java)
            startActivity(intent)
            finish()
        }

        rejectButton.setOnClickListener {
            // Handle reject button
            deleteReport()

            val intent = Intent (this, adminResponse::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateReportStatus(newStatus: String) {
        val db = FirebaseFirestore.getInstance()
        val reportsCollection = db.collection("reports")

        val title = intent.getStringExtra("title")

        // Update the report status
        reportsCollection.whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val reportId = document.id
                    reportsCollection.document(reportId).update("status", newStatus)
                        .addOnSuccessListener {
                            // Report status updated successfully
                        }
                        .addOnFailureListener { e ->
                            // Handle the error
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }

    private fun deleteReport() {
        val db = FirebaseFirestore.getInstance()
        val reportsCollection = db.collection("reports")

        val title = intent.getStringExtra("title")

        // Delete the report
        reportsCollection.whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val reportId = document.id
                    reportsCollection.document(reportId).delete()
                        .addOnSuccessListener {
                            // Report deleted successfully
                            finish() // Finish the activity after deleting the report
                        }
                        .addOnFailureListener { e ->
                            // Handle the error
                        }
                }
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }
}