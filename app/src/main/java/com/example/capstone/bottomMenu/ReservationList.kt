package com.example.capstone.bottomMenu

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.R
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.capstone.Home
import com.example.capstone.List.UserAppointmentData
import com.example.capstone.Reservation.UserAppointmentListAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class ReservationList : Fragment() {
    private lateinit var appointmentList: RecyclerView
    private lateinit var appointBtn: FloatingActionButton
    private lateinit var backBtn: ImageButton
    private lateinit var fStore: FirebaseFirestore
    private lateinit var adapter: UserAppointmentListAdapter
    private val appointments = mutableListOf<UserAppointmentData>()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.activity_reservation_list, container, false)

        fStore = FirebaseFirestore.getInstance()


        appointBtn = view.findViewById(R.id.AppointBtn)
        appointBtn.setOnClickListener {
            val intent = Intent(context, Reservation::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView and adapter
        appointmentList = view.findViewById(R.id.AppointmentList)
        adapter = UserAppointmentListAdapter(appointments)
        appointmentList.layoutManager = LinearLayoutManager(context)
        appointmentList.adapter = adapter

        // Fetch user appointments and update the RecyclerView
        fetchUserAppointments()

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchUserAppointments() {
        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUser != null) {
            val userEmail = currentUser

            // TODO: Query Firestore for user appointments using the user's email
            fStore.collection("Appointments")
                .whereEqualTo("user_email", userEmail) // Adjust the field and condition as per your Firestore data
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val querySnapshot: QuerySnapshot? = task.result
                        if (querySnapshot != null && !querySnapshot.isEmpty) {
                            for (document in querySnapshot.documents) {
                                val data = document.data
                                if (data != null) {
                                    val firstName = data["first_name"] as String
                                    val lastName = data["last_name"] as String
                                    val purpose = data["purpose"] as String
                                    val purok = data["purok"] as String
                                    val date = data["date"] as String
                                    val time = data["time"] as String

                                    val userAppointment = UserAppointmentData(
                                        firstName,
                                        lastName,
                                        purpose,
                                        purok,
                                        date,
                                        time
                                    )
                                    appointments.add(userAppointment)
                                }
                            }
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        // Handle the error
                    }
                }
        } else {
            // User is not authenticated, handle as needed
        }
    }
}