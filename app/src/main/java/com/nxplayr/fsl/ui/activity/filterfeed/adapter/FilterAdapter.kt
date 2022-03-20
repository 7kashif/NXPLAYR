package com.nxplayr.fsl.ui.activity.filterfeed.adapter

import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FilterModel
import kotlinx.android.synthetic.main.item_filter_detail_activity.view.*

class FilterAdapter(

    val context: Activity,
    val onItemClick: OnItemClick,
    val filterList: ArrayList<FilterModel>,
    val viewAll: Boolean = false


) : RecyclerView.Adapter<FilterAdapter.ViewHolder>() {

    var mSelection = 0
    var isVisiable: Boolean = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_filter_detail_activity, parent, false)
        return ViewHolder(v)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(filterList[position], position, onItemClick)

        if (mSelection == position) {
            //holder1.ll_item_filter.setBackgroundColor(context.resources.getColor(R.color.white))
            holder.itemView.txtFilterName.setTextColor(context.resources.getColor(R.color.black))
            holder.itemView.txtFilterName.setBackgroundColor(context.resources.getColor(R.color.colorPrimary))
        } else {
            // holder.ll_item_filter.setBackgroundColor(-1)
            holder.itemView.txtFilterName.setTextColor(context.resources.getColor(R.color.white))
            holder.itemView.txtFilterName.setBackgroundColor(context.resources.getColor(R.color.gray_text_color))

        }

        holder.itemView.setOnClickListener {
            if(holder.adapterPosition>-1) {
                if (mSelection != holder.adapterPosition) {

                    mSelection = holder.adapterPosition
                    if (onItemClick != null)
                        onItemClick.onClicklisneter(
                                holder.adapterPosition,
                                filterList!!.get(holder.adapterPosition)!!.filterName
                        )
                }
                notifyDataSetChanged()
            }
        }
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return filterList.size
    }

    //the class is hodling the list view
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var mContext: Context? = null

        fun bind(filterlist: FilterModel, position: Int, onitemClick: OnItemClick) =
                with(itemView) {

                    txtFilterName.text = filterlist?.filterName
                }

    }

    interface OnItemClick {

        fun onClicklisneter(pos: Int, filterName: String) {

        }
    }

}