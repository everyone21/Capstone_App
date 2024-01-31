package com.example.capstone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore

class AdminCheck : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    private val reportsCollection = db.collection("reports")

    private lateinit var reportsListView: ListView
    private val reportTitles = ArrayList<String>()
    private val reportDates = ArrayList<String>()
    private val reportDescriptions = ArrayList<String>()
    private val reportMediaURLs = ArrayList<String>()
    private lateinit var reportsAdapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_check, container, false)

        reportsListView = view.findViewById(R.id.reportsListView)
        reportsAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            reportTitles
        )
        reportsListView.adapter = reportsAdapter

        reportsListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val intent = Intent(requireContext(), ReportDetails::class.java)
            intent.putExtra("title", reportTitles[position])
            intent.putExtra("date", reportDates[position])
            intent.putExtra("description", reportDescriptions[position])
            intent.putExtra("mediaURL", reportMediaURLs[position])
            startActivity(intent)
        }

        loadReports()



        return view
    }

    private fun loadReports() {
        reportsCollection.whereNotEqualTo("status", "Accepted")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    val title = document.getString("title") ?: ""
                    val date = document.getString("timestamp") ?: ""
                    val description = document.getString("description") ?: ""
                    val mediaURL = document.getString("mediaURL") ?: ""

                    reportTitles.add(title)
                    reportDates.add(date)
                    reportDescriptions.add(description)
                    reportMediaURLs.add(mediaURL)
                }
                reportsAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                // Handle the error here (e.g., log or display an error message)
            }
    }
}