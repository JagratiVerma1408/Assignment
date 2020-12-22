package com.example.assignment.fragments

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.activity.CartActivity
import com.example.assignment.adapter.MenuRecyclerAdapter
import com.example.assignment.database.cart.Database
import com.example.assignment.database.cart.Entity
import com.example.assignment.model.menu
import com.example.assignment.util.ConnectionManager
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerMenu: RecyclerView
    lateinit var btnCart: Button

    lateinit var recyclerAdapter: MenuRecyclerAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager


    var menuList = arrayListOf<menu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        recyclerMenu = view.findViewById(R.id.recyclerMenu)
        btnCart = view.findViewById(R.id.btnProceedToCart)
        layoutManager = LinearLayoutManager(activity)

        val restId = activity?.intent?.extras?.getString("restaurant_id")
        val resName = activity?.intent?.extras?.getString("restaurant_name")

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restId"

        btnCart.visibility = View.GONE

        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest =object: JsonObjectRequest(Request.Method.GET, url,null, Response.Listener{
                try {
                    progressLayout.visibility = View.GONE
                    val data1 = it.getJSONObject("data")

                    val success = data1.getBoolean("success")
                    if(success){
                        val data =data1.getJSONArray("data")
                        for(i in 0 until data.length()) {
                            val restJsonObject = data.getJSONObject(i)
                            val menuObject = menu(
                                restJsonObject.getString("id").toInt(),
                                restJsonObject.getString("name"),
                                restJsonObject.getString("cost_for_one"),
                                restJsonObject.getString("restaurant_id").toInt()
                            )
                            menuList.add(menuObject)

                            recyclerAdapter =
                                MenuRecyclerAdapter(activity as Context, menuList)

                            recyclerMenu.adapter = recyclerAdapter

                            recyclerMenu.layoutManager = layoutManager

                        }

                    }else{
                        print("Error")
                        Toast.makeText(activity as Context, "Error in Connection", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    print(e)
                    Toast.makeText(context, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener {
                Toast.makeText(context, "Volley error occurred!!!", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(activity as Context, "Internet Connection not Found", Toast.LENGTH_LONG).show()
        }

//        recyclerMenu.setOnClickListener {
//            println("Inside")
//            val sharedPreferences = activity!!.getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
//            val t = sharedPreferences.getInt("quantity",0)
//            if (t>0)
//                btnCart.visibility = View.VISIBLE
//            else
//                btnCart.visibility = View.GONE
//        }

        btnCart.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("restaurant_id", restId)
            intent.putExtra("restaurant_name",resName)
            context?.startActivity(intent)
            activity!!.finish()
        }

        return view
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_cart, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_cart){
            val sharedPreferences = activity!!.getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
            val t = sharedPreferences.getInt("quantity",0)
            if (t>0)
                btnCart.visibility = View.VISIBLE
            else
                btnCart.visibility = View.GONE
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
    class DeleteAllItem(val context: Context): AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context, Database::class.java, "Cart").build()
        override fun doInBackground(vararg p0: Void?): Boolean {
            db.dao().deleteAll()
            db.close()
            return true
        }
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}