package com.example.capstone

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class UserReport : AppCompatActivity() {

    private lateinit var fAuth : FirebaseAuth
    private lateinit var titleEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var uploadMediaButton: LinearLayout
    private lateinit var uploadMediaImage: ImageView
    private lateinit var imagePickerActivityResult: ActivityResultLauncher<Intent>
    private lateinit var user : String
    private lateinit var db: FirebaseFirestore
    private var mediaUri: Uri? = null

    private var submissionInProgress = false


    companion object {
        const val PICK_MEDIA_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_report)

        db = FirebaseFirestore.getInstance()

        // trial
        val sdf = SimpleDateFormat("MMMM dd, yyyy / HH:mm:ss aaa", Locale.getDefault())
        val currentDate = sdf.format(Date())
        // end of trial DB

        val reportsCollection = db.collection("reports")
        val checkBox = findViewById<CheckBox>(R.id.checkBox2)
        fAuth = FirebaseAuth.getInstance()

        titleEditText = findViewById<EditText>(R.id.topicReportInput)
        descriptionEditText = findViewById<EditText>(R.id.descriptionReportInput)
        uploadMediaButton = findViewById<LinearLayout>(R.id.uploadMediaButton)
        uploadMediaImage = findViewById<ImageView>(R.id.uploadMediaImage)
        submitButton = findViewById<Button>(R.id.submitButton)




        imagePickerActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the selected media
                mediaUri = result.data?.data
                // Update the UI or do something with the selected media
                uploadMediaImage.setImageResource(R.drawable.selected_image_icon)
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
            user = fAuth.currentUser?.email.toString()

            if (titleEditText.text.isEmpty()) {
                checkFields(titleEditText)
            }
            if (descriptionEditText.text.isEmpty()) {
                checkFields(descriptionEditText)
            } else {

                user = if (checkBox.isChecked) {
                    "Anonymous Sender"
                } else {
                    fAuth.currentUser?.email.toString()
                }

//            val user = fAuth.currentUser?.email.toString()

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
                                        "uid" to fAuth.currentUser?.uid,
                                        "title" to title,
                                        "description" to description,
                                        "mediaURL" to mediaURL,
                                        "timestamp" to currentDate,
                                        "status" to "Pending",
                                        "UserID" to user
                                    )

                                    reportsCollection.add(report)
                                        .addOnSuccessListener { documentReference ->
                                            // Report added successfully
                                            val intent = Intent(this, navigation::class.java)
                                            startActivity(intent)
                                            Toast.makeText(
                                                this,
                                                "Report submitted successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        }
                                        .addOnFailureListener { e ->
                                            // Handle the error
                                            Log.e("Firestore", "Error adding report: $e")
                                            Toast.makeText(
                                                this,
                                                "Error submitting report: $e",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnCompleteListener {
                                            submissionInProgress =
                                                false // Reset the submission flag
                                        }
                                }
                                .addOnFailureListener { e ->
                                    // Handle the error in getting the download URL
                                    Log.e("Storage", "Error getting download URL: $e")
                                    Toast.makeText(
                                        this,
                                        "Error getting media download URL: $e",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            // Handle the error in uploading media
                            Log.e("Storage", "Error uploading media: $e")
                            Toast.makeText(this, "Error uploading media: $e", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    // Media is not selected, you can proceed without media
                    val report = hashMapOf(
                        "uid" to fAuth.currentUser?.uid,
                        "title" to title,
                        "timestamp" to currentDate,
                        "description" to description,
                        "status" to "Pending",
                        "UserID" to user
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
                            Toast.makeText(this, "Error submitting report: $e", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .addOnCompleteListener {
                            submissionInProgress = false
                        }
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

    private fun checkFields(text: EditText){
        titleEditText = findViewById(R.id.topicReportInput)
        descriptionEditText = findViewById(R.id.descriptionReportInput)
        text.error = "Empty"
        Toast.makeText(this, "Please Fill All The Fields", Toast.LENGTH_SHORT).show()

    }
}