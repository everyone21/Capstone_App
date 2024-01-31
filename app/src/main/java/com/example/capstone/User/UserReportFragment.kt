package com.example.capstone.User

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.capstone.R
import com.example.capstone.navigation
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class UserReportFragment : Fragment() {

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

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_report, container, false)

        val db = FirebaseFirestore.getInstance()
        val reportsCollection = db.collection("reports")

        titleEditText = view.findViewById<EditText>(R.id.topicReportInput)
        descriptionEditText = view.findViewById<EditText>(R.id.descriptionReportInput)
        uploadMediaButton = view.findViewById<LinearLayout>(R.id.uploadMediaButton)
        uploadMediaImage = view.findViewById<ImageView>(R.id.uploadMediaImage)
        submitButton = view.findViewById<Button>(R.id.submitButton)

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
                                        val intent = Intent(context, navigation::class.java)
                                        startActivity(intent)
                                        Toast.makeText(context, "Report submitted successfully", Toast.LENGTH_SHORT).show()
                                        activity?.finish()
                                    }
                                    .addOnFailureListener { e ->
                                        // Handle the error
                                        Log.e("Firestore", "Error adding report: $e")
                                        Toast.makeText(context, "Error submitting report: $e", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnCompleteListener {
                                        submissionInProgress = false // Reset the submission flag
                                    }
                            }
                            .addOnFailureListener { e ->
                                // Handle the error in getting the download URL
                                Log.e("Storage", "Error getting download URL: $e")
                                Toast.makeText(context, "Error getting media download URL: $e", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        // Handle the error in uploading media
                        Log.e("Storage", "Error uploading media: $e")
                        Toast.makeText(context, "Error uploading media: $e", Toast.LENGTH_SHORT).show()
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
                        val intent = Intent(context, navigation::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                    .addOnFailureListener { e ->
                        // Handle the error
                        Log.e("MediaUpload", "Error uploading media: $e")
                        Toast.makeText(context, "Error submitting report: $e", Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        submissionInProgress = false
                    }
            }
        }

        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UserReportFragment.PICK_MEDIA_REQUEST && resultCode == Activity.RESULT_OK) {
            mediaUri = data?.data
        }
    }


}