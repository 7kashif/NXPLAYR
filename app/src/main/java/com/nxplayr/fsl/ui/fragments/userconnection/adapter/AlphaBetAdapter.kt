package com.nxplayr.fsl.ui.fragments.userconnection.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_alphanet.view.*

class AlphaBetAdapter (
    val context: Activity,
    val onItemClick: OnItemClick,
    val listData: ArrayList<String>?
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_alphanet, parent, false)
            return LikeViewHolder(v, context)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is LikeViewHolder) {
            holder.bind(listData!!.get(position)!!, holder.adapterPosition, onItemClick)
        }
    }


    override fun getItemCount(): Int {
        return listData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listData!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class LikeViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(
                likeListData: String,
                position: Int,
                onitemClick: OnItemClick
        ) =
                with(itemView) {
                    tv_alphabet.text=likeListData
                    itemView.setOnClickListener { onitemClick?.onClicklisneter(position, "") }
                }
    }


    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String)
    }
}