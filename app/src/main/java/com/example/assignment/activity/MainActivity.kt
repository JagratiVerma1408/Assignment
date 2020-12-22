package com.example.assignment.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.example.assignment.R
import com.example.assignment.database.cart.Database
import com.example.assignment.fragments.FavouriteFragment
import com.example.assignment.fragments.HomeFragment
import com.example.assignment.fragments.HistoryFragment
import com.example.assignment.fragments.ProfileFragment
import com.google.android.material.navigation.NavigationView
import java.lang.Exception
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var txtMobileNumber: TextView
    lateinit var txtName: TextView

    var prevItem = 0

    var previousMenuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DeleteAllItem(this).execute().get()
        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
//        try {
//            val a = sharedPreferences.getBoolean("end",false)
//            if (a)
//                finish()
//        }catch (e: Exception){
//
//        }
//        sharedPreferences.edit().putBoolean("end",true).apply()

        val userName = sharedPreferences.getString("name", "")
        val number: String? = sharedPreferences.getString("mobile_number", "")
        sharedPreferences.edit().putBoolean("order_placed",false).apply()
        sharedPreferences.edit().putInt("quantity", 0).apply()

        setUpToolbar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        openHome()

        val drawerHeader = navigationView.getHeaderView(0)
        txtMobileNumber = drawerHeader.findViewById(R.id.txtMobileNumber)
        txtName = drawerHeader.findViewById(R.id.txtUserName)

        txtMobileNumber.text = number
        txtName.text = userName

        navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem!=null){
                previousMenuItem?.isChecked=false
            }
            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it
            when(it.itemId){
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    prevItem = R.id.profile
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment()
                        )
                        .commit()
                    supportActionBar?.title="Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    prevItem = R.id.favourites
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouriteFragment()
                        )
                        .commit()
                    supportActionBar?.title="Favourites"
                    drawerLayout.closeDrawers()
                }
                R.id.history -> {
                    prevItem = R.id.history
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            HistoryFragment()
                        )
                        .commit()
                    supportActionBar?.title="History"
                    navigationView.setCheckedItem(R.id.history)
                    drawerLayout.closeDrawers()
                }
                R.id.logOut -> {
                    drawerLayout.closeDrawers()
                    val dialogBox = AlertDialog.Builder(this)
                    dialogBox.setTitle("Log Out")
                    dialogBox.setMessage("Do you want to log out")
                    dialogBox.setPositiveButton("Yes"){text, listener->
                        sharedPreferences.edit().clear().apply()
                        sharedPreferences.edit().putBoolean("isLogIn", false).apply()
                        val intent = Intent(this, LogIn::class.java)
                        startActivity(intent)
                        finish()
                    }
                    dialogBox.setNegativeButton("No"){text, listener->
                        navigationView.setCheckedItem(prevItem)
                    }
                    dialogBox.create()
                    dialogBox.show()

                }
            }

            return@setNavigationItemSelectedListener true
        }

    }

    class DeleteAllItem(val context: Context): AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context, Database::class.java, "Cart").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.dao().deleteAll()
            db.close()
            return true
        }
    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id==android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome(){
        prevItem = R.id.home
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title="Home"
        navigationView.setCheckedItem(R.id.home)
    }
    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            !is HomeFragment -> openHome()

            else -> {
                super.onBackPressed()
                finish()
            }
        }

    }
}