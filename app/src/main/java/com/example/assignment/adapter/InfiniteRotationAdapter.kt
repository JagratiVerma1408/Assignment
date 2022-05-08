package com.tomoima.infiniterotation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.assignment.R

/**
 * Created by tomoaki on 2017/08/13.
 */
class InfiniteRotationAdapter(itemList: List<ItemInfo>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list: List<ItemInfo> = listOf(itemList.last()) + itemList + listOf(itemList.first())



    override fun getItemCount() = list.size

    internal class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        @BindView(R.id.page_name)
        lateinit var pageName: TextView
        @BindView(R.id.page_position)
        lateinit var pagePosition: TextView

        init {
            ButterKnife.bind(this,view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as? ItemViewHolder)?.let {
            it.pageName.text = list[position % list.size].page
            it.itemView.setBackgroundColor(list[position % list.size].colorInt)
            it.pagePosition.text =
                it.itemView.resources.getString(R.string.actual_position, position.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(parent?.context)
            .inflate(R.layout.view_simple, parent, false)
        return ItemViewHolder(view)
    }
}