package com.nxplayr.fsl.ui.fragments.userfollowers.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FollowingListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_followers_list.view.*


class FollowersAdapter(val context: Activity,
                       val follow_list: ArrayList<FollowingListData?>?,
                       val onItemClick: OnItemClick,
                       val from: Int) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_followers_list, parent, false)
            return FollowersViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LoaderViewHolder) {

        } else if (holder is FollowersViewHolder) {
            val holder1 = holder as FollowersViewHolder
            holder.bind(follow_list!![position], holder1.adapterPosition, onItemClick, from)

        }
    }

    override fun getItemCount(): Int {

        return follow_list!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (follow_list!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class FollowersViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        var sessionManager: SessionManager = SessionManager(context)
        init {
            sessionManager = SessionManager(context)
        }

        fun bind(follow_list: FollowingListData?, adapterPosition: Int, onItemClick: OnItemClick, from: Int) = with(itemView) {

            tv_followersName.text = follow_list!!.userFirstName + " " + follow_list!!.userLastName
            img_followersProile.setImageURI(RestClient.image_base_url_users + follow_list.userProfilePicture)
            img_followersProile.setOnClickListener {
                onItemClick.onClicled(adapterPosition,"otherserProfile")
            }
            if (!follow_list.userID.equals(sessionManager.get_Authenticate_User().userID)) {
                if (from == 0) {
                    if (follow_list.isYouFollowing == "Yes") {
                        layout_icon_follow.visibility = View.GONE
                        layout_icon_following.visibility = View.VISIBLE
                    } else {
                        layout_icon_follow.visibility = View.VISIBLE
                        layout_icon_following.visibility = View.GONE
                    }
                }
                if (from == 1) {
                    if (follow_list.isYouFollowing == "Yes") {
                        layout_icon_follow.visibility = View.GONE
                        layout_icon_following.visibility = View.VISIBLE
                    } else {
                        layout_icon_follow.visibility = View.VISIBLE
                        layout_icon_following.visibility = View.GONE
                    }
                }
            } else {
                layout_icon_follow.visibility = View.GONE
                layout_icon_following.visibility = View.GONE
            }
            if (from == 0 || from == 1) {
                layout_icon_follow.setOnClickListener {
                    onItemClick.onClicled(adapterPosition, "follow")
                    layout_icon_follow.visibility = View.GONE
                    layout_icon_following.visibility = View.VISIBLE
                }
                layout_icon_following.setOnClickListener {
                    onItemClick.onClicled(adapterPosition, "unfollow")
                    layout_icon_follow.visibility = View.VISIBLE
                    layout_icon_following.visibility = View.GONE
                }
            }

        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }
}