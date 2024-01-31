package com.example.capstone.Reservation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.capstone.R
import com.example.capstone.Reservation.AdminReservationViewItems
import com.example.capstone.navigation
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class adminReservationView : Fragment() {

    private lateinit var currentDate: Date
    private lateinit var dateDisplay: TextView
    private lateinit var appointmentsRef: CollectionReference
    private val INVALID_TIME_ID = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.activity_admin_reservation_view, container, false)

        dateDisplay = view.findViewById(R.id.dateDisplay)
        appointmentsRef = FirebaseFirestore.getInstance().collection("Appointments")

        // Initialize with the current date
        currentDate = Calendar.getInstance().time
        displayDate(currentDate)
        updateAppointmentLimits()

        val previousBtn = view.findViewById<ImageView>(R.id.previousBtn)
        val nextBtn = view.findViewById<ImageView>(R.id.nextBtn)


        previousBtn.setOnClickListener {
            currentDate = getPreviousDate(currentDate)
            displayDate(currentDate)
            updateAppointmentLimits()
        }

        nextBtn.setOnClickListener {
            currentDate = getNextDate(currentDate)
            displayDate(currentDate)
            updateAppointmentLimits()
        }


        val limit1 = view.findViewById<RelativeLayout>(R.id.timeDisplay1)
        val limit2 = view.findViewById<RelativeLayout>(R.id.timeDisplay2)
        val limit3 = view.findViewById<RelativeLayout>(R.id.timeDisplay3)
        val limit4 = view.findViewById<RelativeLayout>(R.id.timeDisplay4)
        val limit5 = view.findViewById<RelativeLayout>(R.id.timeDisplay5)
        val limit6 = view.findViewById<RelativeLayout>(R.id.timeDisplay6)
        val limit7 = view.findViewById<RelativeLayout>(R.id.timeDisplay7)

        limit1.setOnClickListener {
            navigateToAdminReservationViewItems("09:00 AM")
        }
        limit2.setOnClickListener {
            navigateToAdminReservationViewItems("10:00 AM")
        }
        limit3.setOnClickListener {
            navigateToAdminReservationViewItems("11:00 AM")
        }
        limit4.setOnClickListener {
            navigateToAdminReservationViewItems("01:00 PM")
        }
        limit5.setOnClickListener {
            navigateToAdminReservationViewItems("02:00 PM")
        }
        limit6.setOnClickListener {
            navigateToAdminReservationViewItems("03:00 PM")
        }
        limit7.setOnClickListener {
            navigateToAdminReservationViewItems("04:00 PM")
        }

        return view
    }

    private fun getPreviousDate(currentDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DATE, -1)
        return calendar.time
    }

    private fun getNextDate(currentDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DATE, 1)
        return calendar.time
    }

    private fun displayDate(date: Date) {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        dateDisplay.text = dateFormat.format(date)
    }

    private fun updateAppointmentLimits() {
        clearPreviousCounts()

        val formattedDate = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(currentDate)
        Log.d("AppointmentUpdate", "Formatted Date: $formattedDate")

        appointmentsRef.whereEqualTo("date", SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(currentDate))
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
        val limitTextView = view?.findViewById<TextView>(limitId)

        Log.d("AppointmentUpdate", "Updating limit text - ID: $limitId, Count: $count")

        if (limitTextView != null) {
            limitTextView.text = "$count"
            Log.d("AppointmentUpdate", "Limit ID: $limitId, Updated Count: $count")

            if (count > 20) {
                // Delete the appointment if count exceeds 20
                deleteAppointment()
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

    private fun deleteAppointment() {
        // Implement the logic to delete the appointment
        // You might want to notify the user or take other actions here
    }

    private fun navigateToAdminReservationViewItems(selectedTime: String) {
        val intent = Intent(context, AdminReservationViewItems::class.java)
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val currentDateFormatted = dateFormat.format(currentDate)

        intent.putExtra("selectedTime", selectedTime)
        intent.putExtra("selectedDate", currentDateFormatted)
        startActivity(intent)
    }
}