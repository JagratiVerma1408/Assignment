package com.example.assignment.database.cart

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities =[Entity::class],version = 1)
abstract class Database: RoomDatabase() {
    abstract fun dao(): Dao
}