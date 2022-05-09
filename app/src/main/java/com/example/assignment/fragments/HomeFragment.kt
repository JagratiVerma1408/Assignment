package com.example.assignment.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.assignment.R
import com.example.assignment.adapter.HomeRecyclerAdapter
import com.example.assignment.model.restaurant
import com.example.assignment.util.ConnectionManager
import org.json.JSONException
import java.util.*


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {

    private val images = arrayOf<Int>(
        R.drawable.download1,
        R.drawable.download2,
        R.drawable.download3,
        R.drawable.download4,
        R.drawable.images1,
        R.drawable.images2
    )


    lateinit var recyclerHome: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: HomeRecyclerAdapter

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar

    var ratingComparator = Comparator<restaurant>{ r1, r2->
        if(r1.rating.compareTo(r2.rating, true)==0){
            r1.name.compareTo(r2.name, true)
        }else{
            r1.rating.compareTo(r2.rating, true)
        }
    }
    var priceComparator = Comparator<restaurant>{ r1, r2->
        if(r1.price.compareTo(r2.price, true)==0){
            r1.name.compareTo(r2.name, true)
        }else{
            r1.price.compareTo(r2.price, true)
        }
    }

    var restaurantInfoList = arrayListOf<restaurant>()

    private var param1: String? = null
    private var param2: String? = null

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
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        recyclerHome = view.findViewById(R.id.recyclerHome)

        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        
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
                            val restaurantObject = restaurant(
                                restJsonObject.getString("id"),
                                restJsonObject.getString("name"),
                                restJsonObject.getString("cost_for_one"),
                                restJsonObject.getString("rating"),
                                restJsonObject.getString("image_url")
                            )
                            restaurantInfoList.add(restaurantObject)

                            recyclerAdapter =
                                HomeRecyclerAdapter(activity as Context, restaurantInfoList)

                            recyclerHome.adapter = recyclerAdapter

                            recyclerHome.layoutManager = layoutManager

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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id== R.id.action_sort){
            val dialogBox = AlertDialog.Builder(activity as Context)
            val type = arrayOf("Rating", "Cost(low to high)", "Cost(high to low)")
            var checkItem = 0
            dialogBox.setTitle("Sort")
            dialogBox.setSingleChoiceItems(type, checkItem){dialog, listener->checkItem = listener
            }
            dialogBox.setPositiveButton("Ok"){_,_->
                if (checkItem==0){
                    Collections.sort(restaurantInfoList, ratingComparator)
                    restaurantInfoList.reverse()
                }else{
                    Collections.sort(restaurantInfoList, priceComparator)
                    if (checkItem==2){
                        restaurantInfoList.reverse()
                    }
                }
                recyclerAdapter.notifyDataSetChanged()
            }
            dialogBox.setNegativeButton("Cancel"){_,_->
            }
            dialogBox.create()
            dialogBox.show()
//            Collections.sort(restaurantInfoList, ratingComparator)
//            restaurantInfoList.reverse()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
