package com.example.assignment.database.cart

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")

data class Entity(
    @PrimaryKey var item_id : Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "cost_for_one") var price: String,
    @ColumnInfo(name = "restaurant_id") var restaurant_id: Int
)
