package com.example.capstone

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.Adapters.calendarAdapter
import com.example.capstone.Dashboard.dashboardEvents
import com.example.capstone.List.Events
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

class AdminCalendarView : Fragment() {

    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsArray: ArrayList<Events>
    private lateinit var calendarAdapter: calendarAdapter
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_admin_calendar_view, container, false)

        val calendaradd:Button = view.findViewById(R.id.add)
        val calendaredit:Button = view.findViewById(R.id.edit)

        calendaradd.setOnClickListener {
            val add = Intent(requireContext(), calendarAdd::class.java)
            startActivity(add)
        }

        calendaredit.setOnClickListener {
            val edit = Intent(requireContext(), calendarEdit::class.java)
            startActivity(edit)
        }

        recyclerView = view.findViewById(R.id.upcomingEvents)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        eventsArray = arrayListOf()
        calendarAdapter = calendarAdapter(eventsArray)
//        recyclerView.adapter = calendarAdapter

        EventChangeListener()

        return view
    }
    private fun EventChangeListener(){

        db = FirebaseFirestore.getInstance()
        db.collection("EventsAnnouncement").orderBy("eventDate", Query.Direction.DESCENDING)
            .addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if (error != null){

                    Log.e("Firestore Error", error.message.toString())
                    return
                }

                for (dc: DocumentChange in value?.documentChanges!!){
                    if (dc.type == DocumentChange.Type.ADDED){
                        eventsArray.add(dc.document.toObject(Events::class.java))

                    }
                    val adapter = calendarAdapter(eventsArray)
                    recyclerView.adapter = adapter
                    adapter.onItemClickListener(object : calendarAdapter.onItemClickListener {
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

                calendarAdapter.notifyDataSetChanged()
            }


        })

    }

}