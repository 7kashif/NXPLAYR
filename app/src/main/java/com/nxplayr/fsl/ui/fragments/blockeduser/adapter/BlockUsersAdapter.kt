package com.nxplayr.fsl.ui.fragments.blockeduser.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.BlockUserData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_block_users_list.view.*

class BlockUsersAdapter(val context: Activity,
                        val list_block_users: ArrayList<BlockUserData?>?,
                        val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_block_users_list, parent, false)
            return BlockUsersViewHolder(v)
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        if (holder is LoaderViewHolder) {

        } else if (holder is BlockUsersViewHolder) {
            val holder1 = holder as BlockUsersViewHolder
            holder.bind(list_block_users!![position], holder1.adapterPosition, onItemClick)
        }

    }

    override fun getItemCount(): Int {

        return list_block_users!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list_block_users!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class BlockUsersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(list_block_users: BlockUserData?, adapterPosition: Int, onItemClick: OnItemClick) = with(itemView) {

            img_blockUserProfile.setImageURI(RestClient.image_base_url_users + list_block_users?.userProfilePicture)
            tv_username.text = list_block_users?.userFirstName + " " + list_block_users?.userLastName

            btn_unblock.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "blockUser")
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }
}