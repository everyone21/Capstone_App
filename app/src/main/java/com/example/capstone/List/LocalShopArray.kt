package com.example.capstone.List

//data class LocalShopArray(
//    val ShopName : String ?= null,
//    val ShopDescription : String ?= null,
//    val ShopLocation : String ?= null,
//    val TypeOfBusiness : String ?= null
//)

data class LocalShopArray(
    val ShopName: String = "",
    val ShopDescription: String = "",
    val ShopLocation: String = "",
    val contactNumber: String = "",
    val contactEmail: String = "",
    val ShopPurok: String = "",
    val TypeOfBusiness: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
