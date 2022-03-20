package com.nxplayr.fsl.ui.activity.filterfeed.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FilterSubItem
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_filter_sub_item.view.*

class FilterSubItemAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val listData: ArrayList<FilterSubItem?>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastCheckedPosition = -1
    private var multiSelect = false
    private val selectedItems = arrayListOf<FilterSubItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_filter_sub_item, parent, false)
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
            holder1.checkbox_filter.isChecked= listData?.get(position)?.isSelected!!

            holder1.checkbox_filter.setOnCheckedChangeListener { compoundButton, b ->
                if(compoundButton.isPressed) {
                    listData?.get(holder1.adapterPosition)?.isSelected = b
                    if (onItemClick != null)
                        onItemClick.onClicklisneter(holder1.adapterPosition, "")
                }
            }
        }
    }

    class FilterViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        val checkbox_filter = itemView.findViewById(R.id.checkbox_filter) as CheckBox
        val ll_sub_item = itemView.findViewById(R.id.ll_sub_item) as LinearLayout

        fun bind(albumlistData: FilterSubItem, position: Int, onitemClick: OnItemClick) =
                with(itemView) {
                    tvUp.text = albumlistData!!.title
                }

    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, from: String)

    }
}