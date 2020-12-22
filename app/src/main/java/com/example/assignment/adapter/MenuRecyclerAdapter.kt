package com.example.assignment.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.assignment.R
import com.example.assignment.database.cart.Database
import com.example.assignment.database.cart.Entity
import com.example.assignment.model.menu

class MenuRecyclerAdapter(var context: Context, var itemList: List<menu>): RecyclerView.Adapter<MenuRecyclerAdapter.MenuViewHolder>() {
    class MenuViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtSNO: TextView = view.findViewById(R.id.txtSNo)
        val txtName: TextView = view.findViewById(R.id.txtItemName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val btnAdd: Button = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): MenuViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_menu_single_row, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val s = position + 1
        holder.txtSNO.text = s.toString()
        holder.txtName.text = itemList[position].name
        holder.txtPrice.text = "Rs. " + itemList[position].cost_for_one
        val cart_item = Entity(itemList[position].id,
        itemList[position].name,
        itemList[position].cost_for_one,
        itemList[position].restaurant_id)
        val sharedPreferences = context.getSharedPreferences("Runner Preferences", Context.MODE_PRIVATE)

        var t = sharedPreferences.getInt("quantity",0)
        holder.btnAdd.setOnClickListener {
            if (holder.btnAdd.text == "Remove"){
                DBAsyncTask(context, cart_item, 3).execute().get()
                holder.btnAdd.text = "Add"
                if (t>0){
                    t-=1
                    sharedPreferences.edit().putInt("quantity",t).apply()
                }
            }else{
                DBAsyncTask(context, cart_item, 2).execute().get()
                holder.btnAdd.text = "Remove"
                t+=1
                sharedPreferences.edit().putInt("quantity",t).apply()
            }
        }
    }
    class DBAsyncTask(val context: Context, val entity: Entity, val mode: Int): AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    val db = Room.databaseBuilder(context, Database::class.java, "Cart").build()
                    val fav: Entity? = db.dao().getItemById((entity.item_id.toString()))
                    db.close()
                    return fav != null
                }
                2 -> {
                    val db = Room.databaseBuilder(context, Database::class.java, "Cart").build()
                    db.dao().insertItem(entity)
                    db.close()
                    return true
                }
                3 -> {
                    val db = Room.databaseBuilder(context, Database::class.java, "Cart").build()
                    db.dao().deleteItem(entity)
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}