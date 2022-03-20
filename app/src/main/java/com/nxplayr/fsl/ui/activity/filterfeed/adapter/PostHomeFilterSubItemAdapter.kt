package com.nxplayr.fsl.ui.activity.filterfeed.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FilterSubItem
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder

class PostHomeFilterSubItemAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val listData: ArrayList<FilterSubItem>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastCheckedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_filter_home, parent, false)
            return FilterViewHolder(v, context)
        }
    }

    override fun getItemCount(): Int {
        return listData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is FilterViewHolder) {
            val holder1 = holder

            holder.bind(listData!![position]!!, position, onItemClick)
            holder1.ratingPostTypeRB.isChecked= listData?.get(position)?.isSelected!!

            holder1.ratingPostTypeRB.setOnCheckedChangeListener { compoundButton, b ->
                if(compoundButton.isPressed) {
                    listData?.get(holder1.adapterPosition)?.isSelected = b
                    if (onItemClick != null)
                        onItemClick.onClicklisneter(holder1.adapterPosition, "")
                }
            }
        }
    }

    class FilterViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        val ratingPostTypeRB = itemView.findViewById(R.id.ratingPostTypeRB) as CheckBox

        fun bind(albumlistData: FilterSubItem, position: Int, onitemClick: OnItemClick) =
                with(itemView) {
                    ratingPostTypeRB.text = albumlistData!!.title
                }

    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, from: String)

    }
}