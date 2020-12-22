package com.example.assignment.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.adapter.CartRecyclerAdapter
import com.example.assignment.adapter.HistoryRecyclerAdapter
import com.example.assignment.database.cart.Database
import com.example.assignment.database.cart.Entity
import com.example.assignment.model.History
import com.example.assignment.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar

    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var txtRestaurantName: TextView
    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var btnPlaceOrder: Button

    lateinit var sharedPreferences: SharedPreferences

    lateinit var itemList: List<Entity>

    var sum = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        toolbar = findViewById(R.id.toolbar)
        setUpToolbar()

        val resName = intent?.extras?.getString("restaurant_name")

        recyclerCart = findViewById(R.id.recyclerCart)
        layoutManager = LinearLayoutManager(this)
        itemList = RetrieveItem(this).execute().get()
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        txtRestaurantName = findViewById(R.id.txtRestaurantName)

        txtRestaurantName.text = resName


        for (i in itemList){
            sum+=i.price.toInt()
        }
        btnPlaceOrder.text = getString(R.string.place_order_total_rs) + sum.toString() + ")"

        recyclerAdapter = CartRecyclerAdapter(this, itemList)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager

        val queue = Volley.newRequestQueue(this)

        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        btnPlaceOrder.setOnClickListener {
            val params = getParams()
            System.out.println(params)
            if(ConnectionManager().checkConnectivity(this as Context)){
                val jsonObjectRequest =object: JsonObjectRequest(Request.Method.POST, url,params, Response.Listener{
                    try {
                        val data1 = it.getJSONObject("data")
                        System.out.println(data1)

                        val success = data1.getBoolean("success")
                        if(success)
                        {
                            val intent = Intent(this, OrderPlacedActivity::class.java)
                            startActivity(intent)
                        }else{
                            val msg = data1.getString("errorMessage")
                            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: JSONException){
                        Toast.makeText(this, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this, "Volley error occurred!!!", Toast.LENGTH_SHORT).show()
                }){
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "48e7c5ea4d1964"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }else{
                Toast.makeText(this, "Internet Connection not Found", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id==android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
    class RetrieveItem(val context: Context): AsyncTask<Void, Void, List<Entity>>(){
        val db = Room.databaseBuilder(context, Database::class.java, "Cart").build()
        override fun doInBackground(vararg p0: Void?): List<Entity> {
            val a = db.dao().getAllItems()
            db.close()
            return a
        }
    }
    fun getParams(): JSONObject {
        var params = JSONObject()
        sharedPreferences = getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "")
        val resId = intent?.extras?.getString("restaurant_id")
        params.put("user_id",userId)
        params.put("restaurant_id",resId)
        params.put("total_cost",sum.toString())
        var paramsList = JSONArray()
        for(i in itemList){
            val test = JSONObject()
            test.put("food_item_id",i.item_id.toString())
            paramsList.put(test)
        }
        params.put("food",paramsList)
        return params
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}