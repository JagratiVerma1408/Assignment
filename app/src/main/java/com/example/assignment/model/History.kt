package com.example.assignment.model

data class History (
    val r_id : Int,
    val rName : String,
    val totalCost : Double,
    val date: String,
    val itemDetail: ArrayList<subclass>
)
data class subclass(
    var item_id :Int,
    var item_name :String,
    var price: Int = 0
)