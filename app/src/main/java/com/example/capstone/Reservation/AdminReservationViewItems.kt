package com.example.capstone.Reservation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.List.Appointment
import com.example.capstone.R
import com.example.capstone.bottomMenu.AppointmentsAdapter
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import java.text.SimpleDateFormat

class AdminReservationViewItems : AppCompatActivity() {

    private lateinit var appointmentsRecyclerView: RecyclerView
    private lateinit var appointmentsAdapter: AppointmentsAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_reservation_view_items)

        // Retrieve the selected time from the intent
        val selectedTime = intent.getStringExtra("selectedTime")
        val selectedDate = intent.getStringExtra("selectedDate")

        // Initialize RecyclerView and its adapter
        appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView)
        appointmentsAdapter = AppointmentsAdapter()

        // Set up RecyclerView
        appointmentsRecyclerView.layoutManager = LinearLayoutManager(this)
        appointmentsRecyclerView.adapter = appointmentsAdapter

        // Fetch and display appointments for the selected time
        fetchAndDisplayAppointments(selectedTime, selectedDate)
    }

    private fun fetchAndDisplayAppointments(selectedTime: String?, selectedDate: String?) {
        if (selectedTime.isNullOrEmpty()) {
            Log.e("AdminReservationViewItems", "Selected time is null or empty.")
            return
        }

        val appointmentsRef = FirebaseFirestore.getInstance().collection("Appointments")

        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.time = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).parse(selectedDate)!!

        val selectedFullTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(selectedCalendar.time)

        // Remove leading zero in the hour part of the selected time
        val selectedTimeWithoutLeadingZero = selectedTime.replace("^0".toRegex(), "")
        val selectedFullTimeWithoutLeadingZero = selectedFullTime.replace("^0".toRegex(), "")

        // Query appointments for the selected time and date
        appointmentsRef.whereEqualTo("time", selectedTimeWithoutLeadingZero)
            .whereEqualTo("date", SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(selectedCalendar.time))
//            .whereEqualTo("day", SimpleDateFormat("d", Locale.getDefault()).format(selectedCalendar.time))
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Handle the query results and update the UI with appointment details
                val appointmentsList = mutableListOf<Appointment>()

                for (document in querySnapshot) {
                    val firstName = document.getString("first_name") ?: ""
                    val lastName = document.getString("last_name") ?: ""
                    val purpose = document.getString("purpose") ?: ""
                    val date = document.getString("date") ?: ""
                    val time = document.getString("time") ?: ""
                    val purok = document.getString("purok") ?: ""

                    val appointment = Appointment(firstName, lastName, purpose, date, time, purok)
                    appointmentsList.add(appointment)
                }

                // Update the RecyclerView with the list of appointments
                appointmentsAdapter.setAppointments(appointmentsList)
            }
            .addOnFailureListener { exception ->
                Log.e("AdminReservationViewItems", "Error getting appointments: ", exception)
            }
    }
}