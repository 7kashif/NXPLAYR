package com.nxplayr.fsl.ui.fragments.userconnection.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ChatListData
import com.nxplayr.fsl.data.model.FriendListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_send_message_list_adapter.view.*

class SendMessageAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val listData: ArrayList<ChatListData?>?
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var smsListFiltered: List<ChatListData?>?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_send_message_list_adapter, parent, false)
            return MessageViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is MessageViewHolder) {
            holder.bind(smsListFiltered!![position], holder.adapterPosition, onItemClick)
        }
    }

    override fun getItemCount(): Int {
        return smsListFiltered!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (smsListFiltered!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    @Suppress("DEPRECATION")
    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(sms_list: ChatListData?, position: Int, onitemClick: OnItemClick) =
                with(itemView) {

                    tv_user_name.text = sms_list!!.userFirstName + " " + sms_list!!.userLastName
                    image_sendmsg.setImageURI(RestClient.image_base_url_users + sms_list.userProfilePicture)

                    itemView.setOnClickListener {
                          onitemClick.onClicklisneter(position,sms_list!!.userFirstName + " " + sms_list!!.userLastName)
                    }

                }
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                smsListFiltered = if (charString.isEmpty()) {
                    listData!!
                } else {
                    val filteredList: MutableList<ChatListData?> = ArrayList<ChatListData?>()
                    for (row in listData!!) {

                        if (row!!.userFirstName?.toLowerCase()?.contains(charString.toLowerCase())!! || row!!.userLastName?.contains(charSequence)!!) {
                            filteredList.add(row)
                        }
                    }
                    filteredList

                }
                val filterResults = FilterResults()
                filterResults.values = smsListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                smsListFiltered = filterResults.values as ArrayList<ChatListData?>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String)
    }

    init {
        smsListFiltered = listData!!
    }
}