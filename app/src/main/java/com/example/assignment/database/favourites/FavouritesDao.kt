package com.example.assignment.database.favourites
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavouritesDao {
    @Insert
    fun insertRestaurant(favouritesEntity: FavouritesEntity)

    @Delete
    fun deleteRestaurant(favouritesEntity: FavouritesEntity)

    @Query("SELECT * FROM favourites")
    fun getAllRestaurants():List<FavouritesEntity>

    @Query("SELECT * FROM favourites WHERE restaurant_id=:restaurantId")
    fun getRestaurantById(restaurantId: String): FavouritesEntity
}