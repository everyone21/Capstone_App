package com.example.capstone.Dashboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.capstone.List.Events
import com.example.capstone.R
import com.example.capstone.calendarAdd
import com.example.capstone.calendarEdit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class dashboardEvents : AppCompatActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_events)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        val title: TextView = findViewById(R.id.what)
        val date: TextView = findViewById(R.id.whenday)
        val desc: TextView = findViewById(R.id.where)
        val time: TextView = findViewById(R.id.whentime)
        val image: ImageView = findViewById(R.id.image)
        val edit: TextView = findViewById(R.id.edit)
        val delete: TextView = findViewById(R.id.delete)
        val edLayout: LinearLayout = findViewById(R.id.editDelete)

        val bundle : Bundle?= intent.extras
        val titl = bundle?.getString("title")
        val dat = bundle?.getString("date")
        val descr = bundle?.getString(("place"))
        val tim = bundle?.getString(("time"))
        val imageUrl = bundle?.getString("imageUrl")

        title.text = titl
        date.text = dat
        desc.text = descr
        time.text = tim

        // Load image using Glide
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.announcement_background_placeholder)
            .error(R.drawable.announcement_background_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(image)

        if (auth.currentUser?.uid == "zJWE9QmxXZhMF2bNec5l9XOtNHl1"){
            edLayout.visibility = View.VISIBLE

            edit.setOnClickListener {
                val intent = Intent(this, calendarEdit::class.java)
                intent.putExtra("title", titl)
                intent.putExtra("date", dat)
                intent.putExtra("place", descr)
                intent.putExtra("time", tim)
                intent.putExtra("imageUrl", imageUrl)
                startActivity(intent)
            }
            delete.setOnClickListener {
                val dialog = Dialog(this)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setCancelable(false)
                dialog.setContentView(R.layout.custom_dialog)
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                val yes: Button = dialog.findViewById(R.id.yes)
                val no: Button = dialog.findViewById(R.id.no)
                val text: TextView = dialog.findViewById(R.id.text)
                text.text = "Do you really want to delete this Announcement?"
                yes.setOnClickListener {
                    db.collection("EventsAnnouncement")
                        .whereEqualTo("eventTitle", titl)
                        .whereEqualTo("eventDate", dat)
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                document.reference.delete()
                                Toast.makeText(this, "Successfully Deleted", Toast.LENGTH_SHORT)
                                    .show()

                            }
                            dialog.dismiss()
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            // Handle error
                        }
                }
                no.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }
}