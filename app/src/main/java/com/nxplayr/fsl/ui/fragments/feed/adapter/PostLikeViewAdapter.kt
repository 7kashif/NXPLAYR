package com.nxplayr.fsl.ui.fragments.feed.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.PostLikeViewListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_followers_list.view.*


class PostLikeViewAdapter(val context: Activity,
                          val follow_list: ArrayList<PostLikeViewListData?>?,
                          val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_followers_list, parent, false)
            return  FollowersViewHolder(v,context)
        }
    }

    override fun getItemCount(): Int {

        return follow_list?.size!!
    }
    override fun getItemViewType(position: Int): Int {
        return if (follow_list!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is FollowersViewHolder) {
            var holder1=holder as FollowersViewHolder
            holder1.bind(follow_list!![position], holder.adapterPosition, onItemClick)
        }

    }

    class FollowersViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
         var sessionManager:SessionManager?=null
          var userData: SignupData?=null
          init {
              sessionManager= SessionManager(context)
              userData=sessionManager?.get_Authenticate_User()
          }
        fun bind(follow_list: PostLikeViewListData?, adapterPosition: Int, onItemClick: OnItemClick) = with(itemView) {

            tv_followersName.text = follow_list!!.userFirstName + " " + follow_list!!.userLastName
            img_followersProile.setImageURI(RestClient.image_base_url_users + follow_list.userProfilePicture)
              if(userData?.userID?.equals(follow_list.userID)!!)
              {
                  layout_icon_follow.visibility = View.GONE
                  layout_icon_following.visibility = View.GONE
              }
            else{
                  if (follow_list.isYouFollowing == "Yes") {
                      layout_icon_follow.visibility = View.GONE
                      layout_icon_following.visibility = View.VISIBLE
                  } else {
                      layout_icon_follow.visibility = View.VISIBLE
                      layout_icon_following.visibility = View.GONE
                  }
              }


                layout_icon_follow.setOnClickListener {
                    onItemClick.onClicled(adapterPosition, "follow")
                }
                layout_icon_following.setOnClickListener {
                    onItemClick.onClicled(adapterPosition, "unfollow")

               }


        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }
}