package com.nxplayr.fsl.ui.fragments.userhashtag.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.Hashtags
import kotlinx.android.synthetic.main.layout_hashtag.view.*

class HashtagAdapter(
    val context: Activity,
    val listData: ArrayList<Hashtags?>?
    , val onItemClick: OnItemClick, val otheruserId: String, val userID: String?
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_hashtag, parent, false)
        return HashtagViewHolder(v)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is HashtagViewHolder) {
            holder.bind(listData!![position]!!, holder.adapterPosition, onItemClick,otheruserId,userID)

        }


    }

    class HashtagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(hashtagsList: Hashtags,
                 adapterPosition: Int,
                 onItemClick: OnItemClick,
                 otheruserId: String,
                 userID: String?) = with(itemView) {
            if(otheruserId.equals(userID)){

            btn_deleteHashtag.visibility = View.VISIBLE
            }else{
                btn_deleteHashtag.visibility = View.GONE

            }
            tv_hashtagsName.text = hashtagsList.hashtagName

            itemView.setOnClickListener {
                if(otheruserId.equals(userID)){
                    onItemClick.onClicled(adapterPosition, "")

                }

            }

        }


    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }

}
