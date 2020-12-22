package com.example.assignment.database.favourites

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Favourites")

data class FavouritesEntity(
    @PrimaryKey var restaurant_id : Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "cost_for_one") var price: String,
    @ColumnInfo(name = "rating") var rating: String,
    @ColumnInfo(name = "image_url") var image: String
)