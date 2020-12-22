package com.example.assignment.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.room.Room
import com.example.assignment.R
import com.example.assignment.database.cart.Database

class OrderPlacedActivity : AppCompatActivity() {
    lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        btnOk = findViewById(R.id.btnOk)
        val sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean("order_placed",true).apply()
        DeleteAllItem(this)
        btnOk.setOnClickListener {
            onBackPressed()
        }
    }
    class DeleteAllItem(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        val db = Room.databaseBuilder(context, Database::class.java, "Cart").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.dao().deleteAll()
            db.close()
            return true
        }
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}