package com.example.capstone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.Adapters.ReportAdapter
import com.example.capstone.List.Report
import com.example.capstone.NotYetAvailable.ReportInformation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Home : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var reportAdapter: ReportAdapter
    private lateinit var reportsList: ArrayList<Report>

    @SuppressLint("NotifyDataSetChanged", "MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val db = FirebaseFirestore.getInstance()
        val reportsCollection = db.collection("reports")
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val add: FloatingActionButton = view.findViewById(R.id.addReport)

        add.setOnClickListener {
            val intent = Intent(context, UserReport::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.postContainer)
        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerView.setHasFixedSize(true)
        reportsList = arrayListOf()
        reportAdapter = ReportAdapter(reportsList)

        // Fetch reports from Firestore and update the adapter
        reportsCollection
            .whereEqualTo("status", "Accepted")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore Error", e.message.toString())

                }
                snapshot?.let { nonNullSnapshot ->
                    for (document in nonNullSnapshot.documents) {
                        val id = document.id
                        val title = document.getString("title") ?: ""
                        val description = document.getString("description") ?: ""
                        val mediaURL = document.getString("mediaURL")
                        val timestamp = document.getString("timestamp")
                        val userID = document.getString("UserID") ?: ""
                        val report = Report(
                            id,
                            title,
                            description,
                            mediaURL,
                            timestamp,
                            userID

                        )
                        reportsList.add(report)

                        reportAdapter = ReportAdapter(reportsList)
                        recyclerView.adapter = reportAdapter
                        reportAdapter.onItemClickListener2(object :
                            ReportAdapter.onItemClickListener2 {
                            override fun onItemClick(position: Int) {
                                val intent = Intent(context, ReportInformation::class.java)
                                intent.putExtra("title", reportsList[position].title)
                                intent.putExtra("date", reportsList[position].date)
                                intent.putExtra("description", reportsList[position].description)
                                intent.putExtra("mediaURL", reportsList[position].mediaURL)
                                intent.putExtra("UserID", reportsList[position].UserID)
                                startActivity(intent)
                            }

                        })
                    }
                    reportAdapter.notifyDataSetChanged()
                }


            }
        return view

    }


//    private fun formatDate(date: Date?): String {
//        date?.let {
//            val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//            return sdf.format(date)
//        }
//        return ""
//    }

}