package com.nxplayr.fsl.ui.activity.post.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.CreatePostOneMainRVPojo

class CreatePostOneMainRVAdapter(context: Context,
                                 private val imageTextMainRVCreatePostOnePojo: ArrayList<CreatePostOneMainRVPojo>,
                                 private var itemClick: OnItemClickListener
) :
        RecyclerView.Adapter<CreatePostOneMainRVAdapter.MyViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = inflater.inflate(R.layout.create_post_main_rv_items, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.createPostTextView.text = imageTextMainRVCreatePostOnePojo[position].itemName
        holder.createPostImageView.setImageResource(imageTextMainRVCreatePostOnePojo[position].itemImage)
        holder.bind(imageTextMainRVCreatePostOnePojo, position, itemClick)
    }

    override fun getItemCount(): Int {
        return imageTextMainRVCreatePostOnePojo.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var createPostTextView: TextView = itemView.findViewById(R.id.createPostTextView) as TextView
        var createPostImageView: AppCompatImageView = itemView.findViewById(R.id.createPostImageView) as AppCompatImageView
        private var createPostRvLinearLayout: LinearLayout = itemView.findViewById(R.id.createPostRvLinearLayout) as LinearLayout

        fun bind(imageTextMainRVCreatePostOnePojo: ArrayList<CreatePostOneMainRVPojo>, position: Int, onItemClickListener: OnItemClickListener) {

            itemView.setOnClickListener {
                onItemClickListener.onClicked(imageTextMainRVCreatePostOnePojo, position)
            }
        }
    }

    interface OnItemClickListener {
        fun onClicked(imageTextMainRVCreatePostOnePojo: ArrayList<CreatePostOneMainRVPojo>, position: Int)
    }
}