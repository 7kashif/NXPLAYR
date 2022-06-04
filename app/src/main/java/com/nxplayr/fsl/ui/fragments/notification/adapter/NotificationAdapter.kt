package com.nxplayr.fsl.ui.fragments.notification.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.NotificationData
import com.nxplayr.fsl.util.MyUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.item_layout_faq_category.view.*
import kotlinx.android.synthetic.main.item_notification_list.view.*

class NotificationAdapter(
    val context: Activity,
    val follow_list: ArrayList<NotificationData?>?,
    val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification_list, parent, false)

        return NotificationViewHolder(v)
    }

    override fun getItemCount(): Int {

        return follow_list!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is NotificationViewHolder) {
            holder.bind(follow_list!![position], holder.adapterPosition, onItemClick)
        }
    }

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(follow_list: NotificationData?, adapterPosition: Int, onItemClick: OnItemClick) =
            with(itemView) {

                if (!follow_list?.notificationData?.Sender_userProfilePicture.isNullOrEmpty())
                    Picasso.get().load(RestClient.image_base_url_users + follow_list?.notificationData?.Sender_userProfilePicture).into(notificationIconImageview)

                tv_titleNotification.text = follow_list?.notificationTitle
                if (!follow_list?.notificationMessageText.isNullOrEmpty())
                    tv_notificationtext.text =
                        MyUtils.decodePushText(follow_list?.notificationMessageText!!, context)
                if ((!follow_list?.notificationSendDate.isNullOrEmpty()) && (!follow_list?.notificationSendTime.isNullOrEmpty())) {
                    try {
                        var notificationDate = MyUtils.formatDate(
                            follow_list!!.notificationSendDate,
                            "yyyy-MM-dd",
                            "MMM dd yyyy"
                        )
                        var notificationTime = MyUtils.formatDate(
                            follow_list!!.notificationSendTime,
                            "HH:mm:ss",
                            "HH:mm a"
                        )
                        tv_notficationDate.text = notificationDate + ", " + notificationTime
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                image_add_more.setOnClickListener {
                    onItemClick.onClicled(adapterPosition, "delete_notiication", image_add_more)
                }
            }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String, v: View)

    }
}