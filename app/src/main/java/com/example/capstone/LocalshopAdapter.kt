package com.example.capstone

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstone.List.LocalShopArray

//class LocalshopAdapter(private val shopList: ArrayList<LocalShopArray>) : RecyclerView.Adapter<LocalshopAdapter.MyViewHolder2>() {
//
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.shop_adapter, parent, false)
//        return MyViewHolder2(itemView)
//    }
//
//    override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
//
//        holder.shopName.text = shopList[position].ShopName
//        holder.shopDes.text = shopList[position].ShopDescription
//        holder.shopLoc.text = shopList[position].ShopLocation
//    }
//
//    override fun getItemCount(): Int = shopList.size
//
//    class MyViewHolder2(itemView : View) : RecyclerView.ViewHolder(itemView){
//        val shopName: TextView = itemView.findViewById(R.id.shopName)
//        val shopDes: TextView = itemView.findViewById(R.id.shopDes)
//        val shopLoc: TextView = itemView.findViewById(R.id.shopLoc)
//
//    }
//
//}

class LocalshopAdapter(private val shopList: List<LocalShopArray>) :
    RecyclerView.Adapter<LocalshopAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.shop_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shop = shopList[position]

        // Bind data to views
        holder.shopName.text = shop.ShopName
        holder.shopDes.text = shop.ShopDescription
        holder.contactNumber.text = shop.contactNumber
        holder.contactEmail.text = shop.contactEmail
        holder.purok.text = shop.ShopPurok
        holder.shopLoc.text = shop.ShopLocation

        // Handle click on mapView
        holder.mapView.setOnClickListener {
            // Navigate to MapsActivity with latitude and longitude
            val intent = Intent(holder.itemView.context, MapsActivityViewLocation::class.java)
            intent.putExtra("latitude", shop.latitude)
            intent.putExtra("longitude", shop.longitude)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shopName: TextView = itemView.findViewById(R.id.shopName)
        val shopDes: TextView = itemView.findViewById(R.id.shopDes)
        val contactNumber: TextView = itemView.findViewById(R.id.contactNumber)
        val contactEmail: TextView = itemView.findViewById(R.id.contactEmail)
        val purok: TextView = itemView.findViewById(R.id.purok)
        val shopLoc: TextView = itemView.findViewById(R.id.shopLoc)
        val mapView: TextView = itemView.findViewById(R.id.mapView)
    }
}
