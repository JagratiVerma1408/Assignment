package com.example.assignment.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.adapter.MenuRecyclerAdapter
import com.example.assignment.database.cart.Database
import com.example.assignment.fragments.MenuFragment
import com.example.assignment.model.menu
import com.example.assignment.util.ConnectionManager
import org.json.JSONException

class OrderActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var sharedPreferences: SharedPreferences

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerMenu: RecyclerView
    lateinit var btnCart: Button

    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager

    var menuList = arrayListOf<menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
//        val end = sharedPreferences.getBoolean("end", false)
//        System.out.println(end)
//        if (end){
//            finish()
//        }


        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        recyclerMenu = findViewById(R.id.recyclerMenu)
        btnCart = findViewById(R.id.btnProceedToCart)
        layoutManager = LinearLayoutManager(this)

        val restId = intent?.extras?.getString("restaurant_id")
        val resName = intent?.extras?.getString("restaurant_name")

        val queue = Volley.newRequestQueue(this as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restId"

        btnCart.visibility = View.GONE

        if (ConnectionManager().checkConnectivity(this as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progressLayout.visibility = View.GONE
                        val data1 = it.getJSONObject("data")

                        val success = data1.getBoolean("success")
                        if (success) {
                            val data = data1.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restJsonObject = data.getJSONObject(i)
                                val menuObject = menu(
                                    restJsonObject.getString("id").toInt(),
                                    restJsonObject.getString("name"),
                                    restJsonObject.getString("cost_for_one"),
                                    restJsonObject.getString("restaurant_id").toInt()
                                )
                                menuList.add(menuObject)

                                recyclerAdapter =
                                    MenuRecyclerAdapter(this as Context, menuList)

                                recyclerMenu.adapter = recyclerAdapter

                                recyclerMenu.layoutManager = layoutManager

                            }

                        } else {
                            print("Error")
                            Toast.makeText(
                                this as Context,
                                "Error in Connection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        print(e)
                        Toast.makeText(
                            this,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this, "Volley error occurred!!!", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "48e7c5ea4d1964"
                        return headers
                    }
                }
            queue.add(jsonObjectRequest)
        } else {
            Toast.makeText(this as Context, "Internet Connection not Found", Toast.LENGTH_LONG)
                .show()
        }

        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("restaurant_id", restId)
            intent.putExtra("restaurant_name", resName)
            startActivity(intent)
            finish()
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

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        val name = intent.extras?.getString("restaurant_name")
        supportActionBar?.title = name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cart,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }else if(id == R.id.action_cart){
            val sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
            val t = sharedPreferences.getInt("quantity",0)
            if (t>0){
                val restId = intent.extras?.getString("restaurant_id")
                val resName = intent.extras?.getString("restaurant_name")
                val intent = Intent(this, CartActivity::class.java)
                intent.putExtra("restaurant_id", restId)
                intent.putExtra("restaurant_name", resName)
                startActivity(intent)
            }else{
                Toast.makeText(this,"Cart Empty",Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        DeleteAllItem(this).execute().get()
        super.onBackPressed()
    }

    override fun onResume() {
        sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
        val end = sharedPreferences.getBoolean("order_placed",false)
        if (end){
            onBackPressed()
            finish()
        }
        super.onResume()
    }
}


