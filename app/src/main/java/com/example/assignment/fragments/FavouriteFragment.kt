package com.example.assignment.fragments

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.assignment.R
import com.example.assignment.adapter.FavouriteRecyclerAdapter
import com.example.assignment.database.favourites.FavouritesDatabase
import com.example.assignment.database.favourites.FavouritesEntity

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FavouriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var progressLayout: RelativeLayout

    lateinit var recyclerFavourites: RecyclerView

    lateinit var recyclerAdapter: FavouriteRecyclerAdapter

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var restaurantList: List<FavouritesEntity>

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
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)

        progressLayout = view.findViewById(R.id.progressLayout)

        recyclerFavourites = view.findViewById(R.id.recyclerFavourites)

        layoutManager = LinearLayoutManager(activity)

        restaurantList = RetrieveFavourite(activity as Context).execute().get()


        if(restaurantList!=null){
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, restaurantList)
            recyclerFavourites.adapter = recyclerAdapter
            recyclerFavourites.layoutManager = layoutManager

        }else{
            print("Empty")
        }


        return view
    }

    class RetrieveFavourite(val context: Context): AsyncTask<Void, Void, List<FavouritesEntity>>(){
        override fun doInBackground(vararg p0: Void?): List<FavouritesEntity> {
            val db = Room.databaseBuilder(context, FavouritesDatabase::class.java, "Favourites").build()

            return db.favouritesDao().getAllRestaurants()
        }

    }

//    class GetDatabase(a: Activity) : Thread() {
//        var activity: Activity = a
//        override fun run() {
//            val db = Room.databaseBuilder(activity as Context, FavouritesDatabase::class.java, "Favourites").build()
//
//            restaurantList.add(db.favouritesDao().getAllRestaurants())
//        }
//    }
    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}