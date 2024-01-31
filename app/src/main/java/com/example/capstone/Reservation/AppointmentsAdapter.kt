package com.example.capstone.bottomMenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.List.Appointment
import com.example.capstone.R

class AppointmentsAdapter : RecyclerView.Adapter<AppointmentsAdapter.AppointmentViewHolder>() {
    private var onItemClickListener: ((Appointment) -> Unit)? = null
    private var appointmentsList: List<Appointment> = emptyList()

    fun setAppointments(appointments: List<Appointment>) {
        appointmentsList = appointments
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (Appointment) -> Unit) {
        onItemClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointmentsList[position]
        holder.bind(appointment)

        // Handle click events for the success and unsuccess buttons
        holder.itemView.findViewById<View>(R.id.successButton).setOnClickListener {
            // Update background color for success
            holder.itemView.findViewById<View>(R.id.cardView).setBackgroundColor(
                holder.itemView.context.resources.getColor(R.color.accept)
            )
            Toast.makeText(holder.itemView.context, "Appointment Success", Toast.LENGTH_SHORT).show()
        }

        holder.itemView.findViewById<View>(R.id.unsuccessButton).setOnClickListener {
            // Update background color for unsuccess
            holder.itemView.findViewById<View>(R.id.cardView).setBackgroundColor(
                holder.itemView.context.resources.getColor(R.color.reject)
            )
            Toast.makeText(holder.itemView.context, "Appointment Failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = appointmentsList.size

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.date)
        private val timeTextView: TextView = itemView.findViewById(R.id.time)
        private val nameTextView: TextView = itemView.findViewById(R.id.name)
        private val purposeTv: TextView = itemView.findViewById(R.id.purpose)
        private val purokTv: TextView = itemView.findViewById(R.id.purok)
        val successButton: TextView = itemView.findViewById(R.id.successButton)
        val unsuccessButton: TextView = itemView.findViewById(R.id.unsuccessButton)

        fun bind(appointment: Appointment) {
            val fullName = "${appointment.firstName} ${appointment.lastName}"
            nameTextView.text = fullName
            purposeTv.text = appointment.purpose
            dateTextView.text = appointment.purok
            timeTextView.text = appointment.date
            purokTv.text = appointment.time
        }
    }
}