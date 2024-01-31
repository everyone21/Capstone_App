package com.example.capstone.bottomMenu

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.capstone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Reservation : AppCompatActivity() {

    private lateinit var spinner : Spinner
    private lateinit var purokSpinner: Spinner
//    private lateinit var monthSpinner: Spinner
//    private lateinit var daySpinner: Spinner
//    private lateinit var timeSpinner: Spinner
    private lateinit var firstName : EditText
    private lateinit var lastName : EditText
    private lateinit var submit : Button
    private lateinit var df: DocumentReference
    private lateinit var fStore: FirebaseFirestore
    private lateinit var currentUserEmail: String
    private lateinit var currentDate: Date
    private lateinit var auth : FirebaseAuth
    private lateinit var appointmentsRef: CollectionReference
    private val INVALID_TIME_ID = -1
    private lateinit var picked : TextView
    private lateinit var picker : FrameLayout
    private lateinit var timeLayout: LinearLayout

    private lateinit var time1 : RelativeLayout
    private lateinit var time2 : RelativeLayout
    private lateinit var time3 : RelativeLayout
    private lateinit var time4 : RelativeLayout
    private lateinit var time5 : RelativeLayout
    private lateinit var time6 : RelativeLayout
    private lateinit var time7 : RelativeLayout
    private lateinit var time : String


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservation)

        fStore = FirebaseFirestore.getInstance()
        currentDate = Calendar.getInstance().time
        auth = FirebaseAuth.getInstance()

        currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
        appointmentsRef = FirebaseFirestore.getInstance().collection("Appointments")

        val otherFrame: FrameLayout = findViewById(R.id.others)
        val otherEditText: EditText = findViewById(R.id.otherPurpose)

        spinner = findViewById(R.id.purpose_spinner)
        val adapter = ArrayAdapter.createFromResource(this, R.array.purpose, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                // Check if the selected item is "Others"
                if (position == adapter.count - 1) {
                    // If "Others" is selected, show the EditText
                    otherFrame.visibility = View.VISIBLE
                } else {
                    // If any other item is selected, hide the EditText
                    otherFrame.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // This method is not being called when the Spinner's item is changed,
                // but we should add a body to this method for consistency.
                otherFrame.visibility = View.GONE
            }
        }


        purokSpinner = findViewById(R.id.purok_spinner)
        val purokAdapter = ArrayAdapter.createFromResource(this, R.array.Puroks, android.R.layout.simple_spinner_item)
        purokAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        purokSpinner.adapter = purokAdapter

//        monthSpinner = findViewById(R.id.monthSpinner)
//        val monthAdapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item)
//        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        monthSpinner.adapter = monthAdapter
//
//        daySpinner = findViewById(R.id.daySpinner)
//        val dayAdapter = ArrayAdapter.createFromResource(this, R.array.Day, android.R.layout.simple_spinner_item)
//        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        daySpinner.adapter = dayAdapter

//        timeSpinner = findViewById(R.id.timeSpinner)
//        val timeAdapter = ArrayAdapter.createFromResource(this, R.array.time, android.R.layout.simple_spinner_item)
//        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        timeSpinner.adapter = timeAdapter

        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        submit = findViewById(R.id.submit)
        picker = findViewById(R.id.pickDate)
        picked = findViewById(R.id.picked)
        timeLayout = findViewById(R.id.timeLayout)


        time1 = findViewById(R.id.timeDisplay1)
        time2 = findViewById(R.id.timeDisplay2)
        time3 = findViewById(R.id.timeDisplay3)
        time4 = findViewById(R.id.timeDisplay4)
        time5 = findViewById(R.id.timeDisplay5)
        time6 = findViewById(R.id.timeDisplay6)
        time7 = findViewById(R.id.timeDisplay7)

        val myCalendar = Calendar.getInstance()
        val myFormat = "MMMM dd, yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())

        val datepicker = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//            updateLabel(myCalendar)
            picked.text = sdf.format(myCalendar.time)
            timeLayout.visibility = View.VISIBLE
        }


        picker.setOnClickListener{
            DatePickerDialog(this, datepicker, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH))
                .show()
        }

        time1.setOnClickListener {
            time = "9:00 AM"
            Toast.makeText(this, "Selected $time", Toast.LENGTH_SHORT).show()
            time1.setBackgroundColor(Color.GREEN)
        }
        time2.setOnClickListener {
            time = "10:00 AM"
            Toast.makeText(this, "Selected $time", Toast.LENGTH_SHORT).show()
            time2.setBackgroundColor(Color.GREEN)
        }
        time3.setOnClickListener {
            time = "11:00 AM"
            Toast.makeText(this, "Selected $time", Toast.LENGTH_SHORT).show()
            time3.setBackgroundColor(Color.GREEN)
        }
        time4.setOnClickListener {
            time = "1:00 PM"
            Toast.makeText(this, "Selected $time", Toast.LENGTH_SHORT).show()
            time4.setBackgroundColor(Color.GREEN)
        }
        time5.setOnClickListener {
            time = "2:00 PM"
            Toast.makeText(this, "Selected $time", Toast.LENGTH_SHORT).show()
            time5.setBackgroundColor(Color.GREEN)
        }
        time6.setOnClickListener {
            time = "3:00 PM"
            Toast.makeText(this, "Selected $time", Toast.LENGTH_SHORT).show()
            time6.setBackgroundColor(Color.GREEN)
        }
        time7.setOnClickListener {
            time = "4:00 PM"
            Toast.makeText(this, "Selected $time", Toast.LENGTH_SHORT).show()
            time7.setBackgroundColor(Color.GREEN)
        }

        updateAppointmentLimits()


        submit.setOnClickListener {

            val fname = firstName.text.toString()
            val lname = lastName.text.toString()
            var purp = spinner.selectedItem.toString()

            purp = if (purp == "Other Purpose"){
                otherEditText.text.toString()
            }else{
                purp
            }

            // add function to check if user have purok endorsement

            val purok = purokSpinner.selectedItem.toString()
//            val month = monthSpinner.selectedItem.toString()
//            val day = daySpinner.selectedItem.toString()

            val db =  fStore.collection("Appointments")
                .whereEqualTo("user_email", auth.currentUser?.uid) // Adjust the field and condition as per your Firestore data
                .get()



            if (firstName.text.isEmpty() or lastName.text.isEmpty()){
                emptyField(firstName)
                emptyField(lastName)
            }
//            if ((month == "Month") or (day == "Day")) {
//                Toast.makeText(this, "Please Select Date", Toast.LENGTH_SHORT).show()
//            }
            else {

                df = fStore.collection("Appointments").document()
                val appointment = hashMapOf(
                    "user_email" to auth.currentUser?.uid,
                    "first_name" to fname,
                    "last_name" to lname,
                    "purpose" to purp,
                    "purok" to purok,
                    "date" to sdf.format(myCalendar.time),
                    "time" to time
                )
                df.set(appointment)
                Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun updateAppointmentLimits() {
        clearPreviousCounts()


        val formattedDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(currentDate)
        Log.d("AppointmentUpdate", "Formatted Date: $formattedDate")

        appointmentsRef.whereEqualTo("date", SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(currentDate))
//            .whereEqualTo("day", SimpleDateFormat("d", Locale.getDefault()).format(currentDate))
            .get()
            .addOnSuccessListener { querySnapshot ->
                val countMap = mutableMapOf<String, Long>()

                for (document in querySnapshot) {
                    // Log the entire document snapshot for debugging
                    val appointmentData = document.data
                    Log.d("AppointmentUpdate", "Document Snapshot: $appointmentData")

                    // Ensure the "time" field is present in the document
                    if (document.contains("time")) {
                        val time = document.getString("time") ?: ""
                        val count = countMap.getOrDefault(time, 0) + 1 //disregard error, works totally fine
                        countMap[time] = count

                        // Update the count on UI
                        updateLimitText(getLimitIdForTime(time), count)
                    } else {
                        Log.e("AppointmentUpdate", "Document is missing the 'time' field")
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("AppointmentUpdate", "Error getting documents: ", exception)
            }
    }

    private fun getCurrentDay(): String {
        return SimpleDateFormat("d", Locale.getDefault()).format(currentDate)
    }

    private fun getLimitIdForTime(time: String): Int {
        try {
            val calendar = Calendar.getInstance()
            calendar.time = SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(time)!!

            // Round down the time to the nearest hour
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            // Format the parsed and rounded time for logging
            val roundedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(calendar.time)

            return when (roundedTime) {
                "09:00 AM" -> R.id.limit1
                "10:00 AM" -> R.id.limit2
                "11:00 AM" -> R.id.limit3
                "01:00 PM" -> R.id.limit4
                "02:00 PM" -> R.id.limit5
                "03:00 PM" -> R.id.limit6
                "04:00 PM" -> R.id.limit7
                else -> {
                    Log.e("AppointmentUpdate", "Invalid Time Format: $roundedTime")
                    INVALID_TIME_ID
                }
            }
        } catch (e: Exception) {
            Log.e("AppointmentUpdate", "Error parsing time: $time", e)
            return INVALID_TIME_ID
        }
    }

    private fun updateLimitText(limitId: Int, count: Long) {
        val limitTextView = findViewById<TextView>(limitId)

        Log.d("AppointmentUpdate", "Updating limit text - ID: $limitId, Count: $count")

        if (limitTextView != null) {
            limitTextView.text = "$count"
            Log.d("AppointmentUpdate", "Limit ID: $limitId, Updated Count: $count")

            if (count > 20) {
                // Delete the appointment if count exceeds 20

            }
        } else {
            Log.e("AppointmentUpdate", "TextView with ID $limitId not found.")
        }
    }

    private fun clearPreviousCounts() {
        // Clear the previous counts on UI
        val allLimits = intArrayOf(R.id.limit1, R.id.limit2, R.id.limit3, R.id.limit4, R.id.limit5, R.id.limit6, R.id.limit7)
        for (limitId in allLimits) {
            updateLimitText(limitId, 0)
        }
    }

    private fun limitReached(){

    }

    private fun emptyField(textField: EditText){
        if (textField.text.isEmpty()){
            textField.error = "Empty Field"
        }
    }
}