package com.example.capstone.Reservation

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.List.UserAppointmentData
import com.example.capstone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class UserAppointmentListAdapter(private val appointmentList: List<UserAppointmentData>) :
    RecyclerView.Adapter<UserAppointmentListAdapter.UserAppointmentViewHolder>() {

    inner class UserAppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val firstName: TextView = itemView.findViewById(R.id.textFirstName)
        val lastName: TextView = itemView.findViewById(R.id.textLastName)
        val purpose: TextView = itemView.findViewById(R.id.textPurpose)
        val purok: TextView = itemView.findViewById(R.id.textPurok)
//        val month: TextView = itemView.findViewById(R.id.textMonth)
        val day: TextView = itemView.findViewById(R.id.textDay)
        val time: TextView = itemView.findViewById(R.id.textTime)
        val edit: TextView = itemView.findViewById(R.id.editButton)
        val delete: TextView = itemView.findViewById(R.id.deleteButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAppointmentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_appointment_item, parent, false)
        return UserAppointmentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserAppointmentViewHolder, position: Int) {
        val currentItem = appointmentList[position]

        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid.toString()

        holder.firstName.text = currentItem.firstName
        holder.lastName.text = currentItem.lastName
        holder.purpose.text = currentItem.purpose
        holder.purok.text = currentItem.purok
//        holder.month.text = currentItem.month
        holder.day.text = currentItem.date
        holder.time.text = currentItem.time

        holder.edit.setOnClickListener {
            val intent = Intent(holder.itemView.context, UserEditReservation::class.java)
            intent.putExtra("first_name", currentItem.firstName)
            intent.putExtra("last_name", currentItem.lastName)
            intent.putExtra("date", currentItem.date)
            intent.putExtra("purok", currentItem.purok)
            intent.putExtra("purpose", currentItem.purpose)
            intent.putExtra("time", currentItem.time)
            holder.itemView.context.startActivity(intent)
        }

        holder.delete.setOnClickListener {
            val dialog = Dialog(holder.itemView.context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.custom_dialog)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val yes: Button = dialog.findViewById(R.id.yes)
            val no: Button = dialog.findViewById(R.id.no)
            yes.setOnClickListener {
                db.collection("Appointments")
                    .whereEqualTo("user_email", uid)
                    .whereEqualTo("date", holder.day.text.toString())
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            document.reference.delete()
                            Toast.makeText(holder.itemView.context, "Successfully Deleted", Toast.LENGTH_SHORT)
                                .show()

                        }
                        dialog.dismiss()
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

    override fun getItemCount(): Int {
        return appointmentList.size
    }
}
