package com.example.capstone.LocalShops

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.List.LocalShopArray
import com.example.capstone.LocalshopAdapter
import com.example.capstone.R
import com.example.capstone.navigation
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

class Services : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var recyclerView: RecyclerView
    private lateinit var shoplist : ArrayList<LocalShopArray>
    private lateinit var shopAdapter : LocalshopAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        val addShop: FloatingActionButton = findViewById(R.id.addShop)
        addShop.setOnClickListener {
            val intent = Intent(this, ServicePost::class.java)
            startActivity(intent)
        }

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener {
            // Navigate back to the dashboard
            val intent = Intent(this, navigation::class.java)
            startActivity(intent)
            finish()
        }

        recyclerView = findViewById(R.id.shops)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        shoplist = arrayListOf()
        shopAdapter = LocalshopAdapter(shoplist)
        recyclerView.adapter = shopAdapter
        db = FirebaseFirestore.getInstance()
        db.collection("Shops").whereEqualTo("TypeOfBusiness", "Services")
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                @SuppressLint("NotifyDataSetChanged")
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if (error != null){

                        Log.e("Firestore Error", error.message.toString())
                        return
                    }

                    for (dc:DocumentChange in value?.documentChanges!!){
                        if (dc.type == DocumentChange.Type.ADDED){
                            shoplist.add(dc.document.toObject(LocalShopArray::class.java))
                        }
                    }

                    shopAdapter.notifyDataSetChanged()
                }
            })
    }
}