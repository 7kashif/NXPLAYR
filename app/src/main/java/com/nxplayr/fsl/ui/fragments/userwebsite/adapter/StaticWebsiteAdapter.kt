package com.nxplayr.fsl.ui.fragments.userwebsite.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SiteList
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_static_websire_detail.view.*
import java.util.ArrayList


class StaticWebsiteAdapter(

    val context: Activity,
    val onItemClick: OnItemClick,
    val websiteListData: ArrayList<SiteList?>?,
    val viewAll: Boolean = false

) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)

        } else {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_static_websire_detail, parent, false)
            return BlogsViewHolder(v, context)

        }
    }

    override fun getItemCount(): Int {
        return websiteListData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (websiteListData!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is BlogsViewHolder) {

            holder.bind(websiteListData!![position]!!, position, onItemClick)

        }
        holder.itemView.imgOption.setOnClickListener {
            if (onItemClick != null)
                onItemClick.setOnClickListener(holder.adapterPosition)
        }
    }

    class BlogsViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(exploreVideoData: SiteList, position: Int, onitemClick: OnItemClick) =
                with(itemView) {

                    tvTitle.text = exploreVideoData?.userurlName
                    tvWebsite.text = exploreVideoData?.userurlLink

                }

    }

    interface OnItemClick {
        fun setOnClickListener(pos: Int)
    }

    fun removeItem(position: Int) {

        for (i in 0 until websiteListData!!.size) {
            websiteListData.get(position)
        }
        websiteListData!!.removeAt(position)
        notifyDataSetChanged()
    }
}