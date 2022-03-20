package com.nxplayr.fsl.ui.fragments.userinterest.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.layout_list_addinterests.view.*


class AddInterestsAdapter(
        val context: Activity,

        var listData: ArrayList<String>?,
        val onItemClick: OnItemClick

) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_list_addinterests, parent, false)
        return AddIntViewHolder(v, context)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is AddIntViewHolder) {
            listData?.get(position)?.let { holder.bind(it, holder.adapterPosition, onItemClick) }

        }


    }

    class AddIntViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(likeListData: String, position: Int, onItemClick: OnItemClick) = with(itemView) {
            tv_followers_name.text = likeListData
            tv_coach_club.text = likeListData
            tv_followers.text = likeListData

        }


    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }


}




