package com.example.capstone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.Adapters.dashboardEventsAdapter
import com.example.capstone.Dashboard.dashboardEvents
import com.example.capstone.List.Events
import com.example.capstone.LocalShops.FoodnBev
import com.example.capstone.LocalShops.Market
import com.example.capstone.LocalShops.Services
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class dashboard : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsArray: ArrayList<Events>
    private lateinit var dashboardAdapter: dashboardEventsAdapter
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val FnB: CardView = view.findViewById(R.id.food)
        val services: CardView = view.findViewById(R.id.services)
        val stores: CardView = view.findViewById(R.id.store)

//        val cal = Calendar.getInstance()
//        cal.add(Calendar.DATE, -5)
//        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
//        val tomorrow = format.format(cal.time)

        auth = FirebaseAuth.getInstance()

        FnB.setOnClickListener {
            val intent = Intent(context, FoodnBev::class.java)
            startActivity(intent)
//            Toast.makeText(context, tomorrow, Toast.LENGTH_SHORT).show()

        }

        services.setOnClickListener {
            val intent = Intent(context, Services::class.java)
            startActivity(intent)
        }

        stores.setOnClickListener {
            val intent = Intent(context, Market::class.java)
            startActivity(intent)
        }

        recyclerView = view.findViewById(R.id.currentEvents)
        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.setHasFixedSize(true)
        eventsArray = arrayListOf()
        dashboardAdapter = dashboardEventsAdapter(eventsArray)
        recyclerView.adapter = dashboardAdapter

        EventChangeListener()

        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -5)
        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val tomorrow = format.format(cal.time)
        db = FirebaseFirestore.getInstance()

        db.collection("EventsAnnouncement").whereEqualTo("eventDate", tomorrow)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    document.reference.delete()
                }
            }



        return view
    }

    private fun EventChangeListener() {

        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val currentDate = sdf.format(Date())
        db = FirebaseFirestore.getInstance()

        db.collection("EventsAnnouncement").whereEqualTo("eventDate", currentDate)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(
                    value: QuerySnapshot?,
                    error: FirebaseFirestoreException?
                ) {
                    if (error != null) {

                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                        for (dc: DocumentChange in value?.documentChanges!!) {
                            if (dc.type == DocumentChange.Type.ADDED) {
                                eventsArray.add(dc.document.toObject(Events::class.java))

                            }
                            val adapter = dashboardEventsAdapter(eventsArray)
                            recyclerView.adapter = adapter
                            adapter.onItemClickListener(object :
                                dashboardEventsAdapter.onItemClickListener {
                                override fun onItemClick(position: Int) {
                                    val intent = Intent(context, dashboardEvents::class.java)
                                    intent.putExtra("title", eventsArray[position].eventTitle)
                                    intent.putExtra("date", eventsArray[position].eventDate)
                                    intent.putExtra("place", eventsArray[position].eventPlace)
                                    intent.putExtra("time", eventsArray[position].eventTime)
                                    intent.putExtra("imageUrl", eventsArray[position].imageUrl)
                                    startActivity(intent)
                                }
                            })
                        }
                        dashboardAdapter.notifyDataSetChanged()
                }

            })
    }
}