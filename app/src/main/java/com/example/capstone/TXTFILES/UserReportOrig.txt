package com.example.capstone

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import androidx.activity.result.ActivityResultLauncher

class UserReport : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var uploadMediaButton: LinearLayout
    private lateinit var uploadMediaImage: ImageView
    private lateinit var imagePickerActivityResult: ActivityResultLauncher<Intent>
    private var mediaUri: Uri? = null
    private var submissionInProgress = false

    companion object {
        const val PICK_MEDIA_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_report)

        val db = FirebaseFirestore.getInstance()
        val reportsCollection = db.collection("reports")
        val backButton = findViewById<Button>(R.id.backBtn)

        titleEditText = findViewById<EditText>(R.id.topicReportInput)
        descriptionEditText = findViewById<EditText>(R.id.descriptionReportInput)
        uploadMediaButton = findViewById<LinearLayout>(R.id.uploadMediaButton)
        uploadMediaImage = findViewById<ImageView>(R.id.uploadMediaImage)
        submitButton = findViewById<Button>(R.id.submitButton)

        backButton.setOnClickListener {
            val intent = Intent(this, navigation::class.java)
            startActivity(intent)
        }

        imagePickerActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the selected media
                mediaUri = result.data?.data
                // Update the UI or do something with the selected media
                uploadMediaImage.setImageResource(R.drawable.baseline_cloud_done_24)
            }
        }

        uploadMediaButton.setOnClickListener {
            // Open a file picker or camera app to select image
            val mediaIntent = Intent(Intent.ACTION_PICK)
            mediaIntent.type = "image/*"
            imagePickerActivityResult.launch(mediaIntent)
        }

        submitButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()

            // Prevent double submission
            if (submissionInProgress) {
                return@setOnClickListener
            }

            if (mediaUri != null) {
                submissionInProgress = true

                val storageRef = FirebaseStorage.getInstance().reference
                val mediaFileName = UUID.randomUUID().toString()
                val mediaRef = storageRef.child(mediaFileName)

                mediaRef.putFile(mediaUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        // Media uploaded successfully; get the download URL
                        mediaRef.downloadUrl
                            .addOnSuccessListener { uri ->
                                val mediaURL = uri.toString()
                                // Create a new report document and add it to Firestore
                                val report = hashMapOf(
                                    "title" to title,
                                    "description" to description,
                                    "mediaURL" to mediaURL,
                                    "timestamp" to FieldValue.serverTimestamp(),
                                    "status" to "Pending"
                                )

                                reportsCollection.add(report)
                                    .addOnSuccessListener { documentReference ->
                                        // Report added successfully
                                        val intent = Intent(this, navigation::class.java)
                                        startActivity(intent)
                                        Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle the error
                                        Log.e("Firestore", "Error adding report: $e")
                                        Toast.makeText(this, "Error submitting report: $e", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnCompleteListener {
                                        submissionInProgress = false // Reset the submission flag
                                    }
                            }
                            .addOnFailureListener { e ->
                                // Handle the error in getting the download URL
                                Log.e("Storage", "Error getting download URL: $e")
                                Toast.makeText(this, "Error getting media download URL: $e", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        // Handle the error in uploading media
                        Log.e("Storage", "Error uploading media: $e")
                        Toast.makeText(this, "Error uploading media: $e", Toast.LENGTH_SHORT).show()
                    }
            } else {
                // Media is not selected, you can proceed without media
                val report = hashMapOf(
                    "title" to title,
                    "timestamp" to FieldValue.serverTimestamp(),
                    "description" to description,
                    "status" to "Pending"
                )
                submissionInProgress = true

                // Add the report to Firestore
                reportsCollection.add(report)
                    .addOnSuccessListener { documentReference ->
                        val intent = Intent(this, navigation::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Handle the error
                        Log.e("MediaUpload", "Error uploading media: $e")
                        Toast.makeText(this, "Error submitting report: $e", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        submissionInProgress = false
                    }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_MEDIA_REQUEST && resultCode == Activity.RESULT_OK) {
            mediaUri = data?.data
        }
    }

}