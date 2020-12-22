package com.example.assignment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.model.History
import kotlinx.android.synthetic.main.recycler_history_single_row.view.*

class HistoryRecyclerAdapter(var context: Context, var itemList: ArrayList<History>): RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(view: View): RecyclerView.ViewHolder(view){
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtDate: TextView = view.findViewById(R.id.txtDate)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerRestaurant)

        lateinit var layoutManager1: RecyclerView.LayoutManager

        lateinit var recyclerItemAdapter: HistoryItemRecyclerAdapter


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_single_row, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun getDate(a: String): String{
        var s = ""
        for (i in a){
            if (i==' ') break

            if(i=='-'){
                s+="\\"
            }
            else{
                s+=i
            }
        }
        return s
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val a = itemList[position]
        holder.txtRestaurantName.text = a.rName
        holder.txtDate.text = getDate(a.date)

        holder.recyclerItemAdapter = HistoryItemRecyclerAdapter(context, a.itemDetail)

        holder.layoutManager1 = LinearLayoutManager(context)

        holder.recyclerView.layoutManager = holder.layoutManager1

        holder.recyclerView.adapter = holder.recyclerItemAdapter

    }
}