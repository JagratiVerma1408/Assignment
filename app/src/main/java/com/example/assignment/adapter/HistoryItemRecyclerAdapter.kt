package com.example.assignment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignment.R
import com.example.assignment.model.History
import com.example.assignment.model.subclass

class HistoryItemRecyclerAdapter (var context: Context, var itemList: ArrayList<subclass>): RecyclerView.Adapter<HistoryItemRecyclerAdapter.HistoryItemViewHolder>(){
    class HistoryItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtItemName: TextView = view.findViewById(R.id.txtItemName)
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_history_food_items, parent, false)
        return HistoryItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        val a = itemList[position]
        holder.txtItemName.text = a.item_name
        holder.txtItemPrice.text = a.price.toString()
    }
}