package com.example.capstone.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.List.Report
import com.example.capstone.databinding.ReportItemBinding

class ReportAdapter(private val reportsList: java.util.ArrayList<Report>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    //    var onItemClick : ((Report) -> Unit)? = null
    private lateinit var mListener: onItemClickListener2

    class ReportViewHolder(val binding: ReportItemBinding, listener: onItemClickListener2) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
    interface onItemClickListener2{
        fun onItemClick(position: Int)
    }

    fun onItemClickListener2(listener: onItemClickListener2){
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = ReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ReportViewHolder(view, mListener)

    }

    override fun getItemCount(): Int {
        return reportsList.size
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report: Report = reportsList[position]

        holder.apply {
            binding.apply {
                titleTextView.text = report.title
                descriptionTextView.text = report.description
                dateTextView.text = report.date
//                 Picasso.get().load(report.mediaURL).into(mediaImageView)
                if (report.mediaURL != null) {
                    mediaImageView.visibility = View.VISIBLE
                    Glide.with(itemView)
                        .load(report.mediaURL)
                        .placeholder(R.drawable.image_icon) // Placeholder image
                        .error(R.drawable.image_icon) // Error image (if loading fails)
                        .into(mediaImageView)
                } else {
                    mediaImageView.visibility = View.GONE
                }
            }


        }
    }
}
