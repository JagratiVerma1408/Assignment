package com.example.assignment.fragments


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.adapter.HistoryRecyclerAdapter
import com.example.assignment.model.History
import com.example.assignment.model.subclass
import com.example.assignment.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HistoryFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var sharedPreferences: SharedPreferences

    lateinit var recyclerHistory: RecyclerView

    lateinit var recyclerAdapter: HistoryRecyclerAdapter

    lateinit var notFound: RelativeLayout

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var progressLayout: RelativeLayout

    var restaurantInfoList = arrayListOf<History>()

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
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        sharedPreferences = this.activity!!.getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)

        progressLayout = view.findViewById(R.id.progressLayout)

        recyclerHistory = view.findViewById(R.id.recyclerHistory)

        layoutManager = LinearLayoutManager(activity as Context)

        notFound = view.findViewById(R.id.notFound)

        val queue = Volley.newRequestQueue(activity as Context)

        val userId = sharedPreferences.getString("user_id", "")

        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId/"

        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest =object: JsonObjectRequest(Request.Method.GET, url,null, Response.Listener{
                try {
                    progressLayout.visibility = View.GONE
                    val data1 = it.getJSONObject("data")

                    val success = data1.getBoolean("success")
                    if(success){
                        val data =data1.getJSONArray("data")
                        if(data.length()!=0){
                            for(i in 0 until data.length()) {
                                val restJsonObject = data.getJSONObject(i)
                                val restaurantObject = History(
                                    restJsonObject.getString("order_id").toInt(),
                                    restJsonObject.getString("restaurant_name"),
                                    restJsonObject.getString("total_cost").toDouble(),
                                    restJsonObject.getString("order_placed_at"),
                                    getFoodItems(restJsonObject.getJSONArray("food_items"))
                                )
                                restaurantInfoList.add(restaurantObject)
                            }

                            recyclerAdapter =
                                HistoryRecyclerAdapter(activity as Context, restaurantInfoList)

                            recyclerHistory.adapter = recyclerAdapter

                            recyclerHistory.layoutManager = layoutManager

                            recyclerHistory.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerHistory.context,
                                    (layoutManager as LinearLayoutManager).orientation
                                )
                            )
                        }else{
                            notFound.visibility = View.VISIBLE
                        }

                    }else{
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
        return view
    }

    fun getFoodItems(a: JSONArray): ArrayList<subclass>{
        var b = arrayListOf<subclass>()
        for(i in 0 until a.length()){
            val d = a.getJSONObject(i)
            val c = subclass(
                d.getString("food_item_id").toInt(),
                d.getString("name"),
                d.getString("cost").toInt()
            )
            b.add(c)
        }
        return b
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}