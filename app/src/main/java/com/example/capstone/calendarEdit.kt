package com.example.capstone

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.List.Events
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

class calendarEdit : AppCompatActivity() {

    private var selectedImage: Uri? = null
    private lateinit var df: DocumentReference
    private lateinit var fStore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var datePicker: Button
    private lateinit var selectedDate: TextView
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var eventId: String
    private lateinit var imageUrl: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar_edit)

        fStore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        datePicker = findViewById(R.id.datePicker)
        selectedDate = findViewById(R.id.selected)

        val title: EditText = findViewById(R.id.eventTitle)
        val desc: EditText = findViewById(R.id.eventDescription)
        val timePicker: TimePicker = findViewById(R.id.time_picker)

        val ttle = intent.getStringExtra("title")
        val date = intent.getStringExtra("date")
        val place = intent.getStringExtra("place")
        val image = intent.getStringExtra("imageUrl")

        eventId = intent.getStringExtra("eventId") ?: ""
        imageUrl = intent.getStringExtra("imageUrl") ?: ""

        title.hint = ttle
        selectedDate.text = date
        desc.hint = place



        val add: Button = findViewById(R.id.add)
        val uploadMediaButton: ImageView = findViewById(R.id.uploadMediaImage)

        val myCalendar = Calendar.getInstance()
        val myFormat = "MMMM dd, yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        val datepicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//            updateLabel(myCalendar)
            selectedDate.text = sdf.format(myCalendar.time)
        }

        datePicker.setOnClickListener {
            DatePickerDialog(
                this, datepicker, myCalendar.get(Calendar.YEAR), myCalendar.get(
                    Calendar.MONTH
                ), myCalendar.get(Calendar.DAY_OF_MONTH)
            )
                .show()
        }

        add.setOnClickListener {
            val c = Calendar.getInstance()

            if (title.text.isEmpty() or desc.text.isEmpty()) {
                emptyField(title)
                emptyField(desc)
            } else {
//            val monthp = datePicker.month + 1
//            val month = monthp.toString()
//            val day = datePicker.dayOfMonth.toString()
//            val year = datePicker.year.toString()
//            val date = "$month/$day/$year"

                val hour = timePicker.hour.toString()
                val min = timePicker.minute.toString()

                val time = if (timePicker.hour >= 12) {
                    "$hour:$min PM"
                } else {
                    "$hour:$min AM"
                }

                fStore.collection("EventsAnnouncement")
                    .whereEqualTo("eventDate", date)
                    .whereEqualTo("eventTitle", ttle)
                    .whereEqualTo("eventPlace", place)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val eventTitle = title.text.toString()
                        val description = desc.text.toString()
                        if (!querySnapshot.isEmpty) {
                            val document =
                                querySnapshot.documents[0] // Assuming there's only one document
                            val eventId = document.id
//                            val events = mapOf(
//                                "eventTitle" to eventTitle,
//                                "eventPlace" to description,
//                                "eventDate" to sdf.format(myCalendar.time),
//                                "eventTime" to time,
//                                "imageUrl" to image.toString()
//                            )
                            fStore.collection("EventsAnnouncement")
                                .document(eventId)
                                .update(
                                    mapOf(
                                        "eventTitle" to eventTitle,
                                        "eventPlace" to description,
                                        "eventDate" to sdf.format(myCalendar.time),
                                        "eventTime" to time,
                                        "imageUrl" to imageUrl // Retain the original image URL
                                    )
                                )
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Announcement Updated", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                                .addOnFailureListener { exception ->
                                    Log.e("calendarEdit", "Error updating Event document", exception)
                                    Toast.makeText(this, "Failed to submit", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Log.e(
                                "calendarEdit",
                                "No Event found with the specified details"
                            )
                            Toast.makeText(this, "No Event found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e(
                            "calendarEdit",
                            "Error querying Event documents",
                            exception
                        )
                        Toast.makeText(this, "Failed to submit", Toast.LENGTH_SHORT).show()
                    }


//                df = fStore.collection("EventsAnnouncement").document()

                // Generate a unique filename for the image
//                val imageFileName = "${UUID.randomUUID()}.jpg"
//
//                // Upload image to Firebase Storage
//                val imageRef: StorageReference = storageReference.child("images/$imageFileName")
//                imageRef.putFile(selectedImage!!)
//                    .addOnSuccessListener {
//                        // Get the download URL of the uploaded image
//                        imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
//                            // Save event details with the imageUrl to Firestore
//                            val events = hashMapOf(
//                                "eventTitle" to eventTitle,
//                                "eventPlace" to description,
//                                "eventDate" to sdf.format(myCalendar.time),
//                                "eventTime" to time,
//                                "imageUrl" to imageUrl.toString()
//                            )
//
//                            df.set(events)
//                            Toast.makeText(this, "Event uploaded", Toast.LENGTH_SHORT).show()
//
//                            finish()
//                        }.addOnFailureListener { exception ->
//                            Toast.makeText(
//                                this,
//                                "Error getting download URL: $exception",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//                    .addOnFailureListener { exception ->
//                        Toast.makeText(
//                            this,
//                            "Error uploading image: $exception",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
            }
        }

        uploadMediaButton.setOnClickListener {
            openImagePicker()
        }

    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImage = data?.data
            val uploadMediaButton: ImageView = findViewById(R.id.uploadMediaImage)

            // Change the icon to a checkmark or any other desired icon
            uploadMediaButton.setImageResource(R.drawable.selected_image_icon)

            // Set a click listener to reset the icon on click
            uploadMediaButton.setOnClickListener {
                // Open the image picker to select a new image
                openImagePicker()

                // Reset the icon to the original upload icon
                uploadMediaButton.setImageResource(R.drawable.image_icon)

                // Remove the click listener to prevent unnecessary icon changes
                uploadMediaButton.setOnClickListener(null)
            }
        }
    }

    private fun emptyField(textField: EditText) {
        if (textField.text.isEmpty()) {
            textField.error = "Empty Field"
        }
    }
}
