package com.nxplayr.fsl.ui.fragments.friendrequest.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FriendListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_request_list.view.*

class RequestAdapter(val context: Activity,
                     val list_request: ArrayList<FriendListData?>?,
                     val onItemClick: OnItemClick,
                     var tabposition: Int
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_request_list, parent, false)
            return RequestViewHolder(v)
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LoaderViewHolder) {

        } else if (holder is RequestViewHolder) {
            val holder1 = holder as RequestViewHolder
            holder.bind(list_request?.get(position)!!, holder1.adapterPosition, onItemClick, tabposition)
        }

    }

    override fun getItemCount(): Int {

        return list_request!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list_request!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }


    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(list_request: FriendListData?, adapterPosition: Int, onItemClick: OnItemClick, tabposition: Int) = with(itemView) {

            tv_username.text = list_request!!.userFirstName + " " + list_request.userLastName
            image_save_post.setImageURI(RestClient.image_base_url_users + list_request.userProfilePicture)
            image_save_post.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "otherserProfile")

            }
            if (tabposition == 0) {
                btnDecline.visibility = LinearLayout.VISIBLE
                btnAccept.visibility = LinearLayout.VISIBLE
                btnFriend.visibility = LinearLayout.GONE
                ll_mainFriend.visibility = LinearLayout.GONE
                ll_tab_one.visibility = LinearLayout.VISIBLE
                layout_status.visibility = LinearLayout.GONE
            }
            if (tabposition == 1) {
                btnDecline.visibility = LinearLayout.GONE
                btnAccept.visibility = LinearLayout.GONE
                ll_mainFriend.visibility = LinearLayout.VISIBLE
                btnFriend.visibility = LinearLayout.VISIBLE
                ll_tab_one.visibility = LinearLayout.GONE
                layout_status.visibility = LinearLayout.VISIBLE

                if (!list_request.connectionType[0].conntypeName.isNullOrEmpty()) {
                    if (list_request.connectionType[0].conntypeName.equals("Friends")) {
                        btnFriend.setCompoundDrawablesRelativeWithIntrinsicBounds((R.drawable.friend_icon_small_connection), 0, 0, 0)
                        var mDrawable = resources.getDrawable(R.drawable.friend_icon_small_connection)
                        mDrawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN)
                        btnFriend.setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable, null, null, null)
                    } else if (list_request.connectionType[0].conntypeName.equals("Professionals")) {
                        btnFriend.setCompoundDrawablesRelativeWithIntrinsicBounds((R.drawable.professional_icon_small_connection), 0, 0, 0)
                        var mDrawable = resources.getDrawable(R.drawable.professional_icon_small_connection)
                        mDrawable.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN)
                        btnFriend.setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable, null, null, null)
                    } else if (list_request.connectionType[0].conntypeName.equals("Acquaintances")) {
                        btnFriend.setCompoundDrawablesRelativeWithIntrinsicBounds((R.drawable.acquaintance_icon_small_connection), 0, 0, 0)
                        var mDrawable2 = resources.getDrawable(R.drawable.acquaintance_icon_small_connection)
                        mDrawable2.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), android.graphics.PorterDuff.Mode.SRC_IN)
                        btnFriend.setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable2, null, null, null)
                    } else {
                        btnFriend.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                    }
                }
                if (!list_request.connectionType[0].conntypeName.isNullOrEmpty()) {
                    btnFriend.text = list_request.connectionType[0].conntypeName
                }

            }

            if (tabposition == 0) {
                btnAccept.setOnClickListener {
                    onItemClick.onClicled(adapterPosition, "reqAccept")
                }
                btnDecline.setOnClickListener {
                    onItemClick.onClicled(adapterPosition, "reqReject")
                }
            }
            if (tabposition == 1) {
                ll_mainFriend.setOnClickListener {
                    onItemClick.onClicled(adapterPosition, "changeConneType")
                }
            }

        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }
}