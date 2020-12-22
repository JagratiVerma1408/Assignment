package com.example.assignment.database.cart

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface Dao {
    @Insert
    fun insertItem(entity: Entity)

    @Delete
    fun deleteItem(entity: Entity)

    @Query("SELECT * FROM cart")
    fun getAllItems():List<Entity>

    @Query("SELECT * FROM cart WHERE item_id=:itemId")
    fun getItemById(itemId: String): Entity

    @Query("DELETE FROM cart")
    fun deleteAll()
}