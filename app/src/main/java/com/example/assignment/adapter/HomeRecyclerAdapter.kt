package com.example.assignment.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.assignment.R
import com.example.assignment.activity.OrderActivity
import com.example.assignment.database.favourites.FavouritesDatabase
import com.example.assignment.database.favourites.FavouritesEntity
import com.example.assignment.model.restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(var context: Context, var itemList: ArrayList<restaurant>): RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>(){
    class HomeViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val imgImage: ImageView = view.findViewById(R.id.imgRestaurantImage)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val imgFav: ImageView = view.findViewById(R.id.imgFav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_singe_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.name
        holder.txtPrice.text = restaurant.price
        holder.txtRating.text = restaurant.rating

        Picasso.get().load(restaurant.image).into(holder.imgImage)

        val fav = FavouritesEntity(
            restaurant.id.toInt(),
            restaurant.name,
            restaurant.price,
            restaurant.rating,
            restaurant.image
        )

        var isFav: Boolean = DBAsyncTask(context, fav,1).execute().get()
        if (isFav){
            holder.imgFav.setImageResource(R.drawable.ic_fav_select)
        }else{
            holder.imgFav.setImageResource(R.drawable.ic_favourites)
        }
        holder.llContent.setOnClickListener{
            val intent = Intent(context, OrderActivity::class.java)
            val sharedPreferences = context.getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)
            sharedPreferences.edit().putBoolean("end",false).apply()
            intent.putExtra("restaurant_id", restaurant.id)
            intent.putExtra("restaurant_name", restaurant.name)
            context.startActivity(intent)
        }
        holder.imgFav.setOnClickListener{
            if (isFav){
                holder.imgFav.setImageResource(R.drawable.ic_favourites)
                val a = DBAsyncTask(context, fav, 3).execute().get()
                isFav = false

            }else{
                holder.imgFav.setImageResource(R.drawable.ic_fav_select)
                val a = DBAsyncTask(context, fav, 2).execute().get()
                isFav = true
            }

        }

    }
    class DBAsyncTask(val context: Context, val favouritesEntity: FavouritesEntity, val mode: Int): AsyncTask<Void, Void, Boolean>(){


        override fun doInBackground(vararg p0: Void?): Boolean {
            when(mode){
                1->{
                    val db= Room.databaseBuilder(context, FavouritesDatabase::class.java,"Favourites").build()
                    val fav: FavouritesEntity? = db.favouritesDao().getRestaurantById((favouritesEntity.restaurant_id.toString()))
                    db.close()
                    return fav!=null
                }
                2->{
                    val db= Room.databaseBuilder(context, FavouritesDatabase::class.java,"Favourites").build()
                    db.favouritesDao().insertRestaurant(favouritesEntity)
                    db.close()
                    return true
                }
                3->{
                    val db= Room.databaseBuilder(context, FavouritesDatabase::class.java,"Favourites").build()
                    db.favouritesDao().deleteRestaurant(favouritesEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}