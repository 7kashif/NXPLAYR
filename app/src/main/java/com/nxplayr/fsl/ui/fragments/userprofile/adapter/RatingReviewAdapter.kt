package com.nxplayr.fsl.ui.fragments.userprofile.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.nxplayr.fsl.R

class RatingReviewAdapter (val context: Activity,
                           val list_data: ArrayList<String>?,
                           val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        Fresco.initialize(context)
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_rating_reviewslist, parent, false)

        return ReviewHolder(v)
    }

    override fun getItemCount(): Int {

        return list_data!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReviewHolder) {
            holder.bind(list_data!![position], holder.adapterPosition, onItemClick)
        }
    }


    class ReviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(list_request: String, adapterPosition: Int, onItemClick: OnItemClick) = with(itemView) {

            itemView.setOnClickListener {

            }

        }
    }



    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }
}