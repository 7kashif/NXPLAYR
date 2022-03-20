package com.nxplayr.fsl.ui.fragments.userconnection.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.SectionIndexer
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FriendListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_chat_connection_list.view.*

class ChatConnectionAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val listData: ArrayList<FriendListData?>?,
    val tabPosition: Int,
    var from: String
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), SectionIndexer, Filterable {
    private var connectionListFiltered: List<FriendListData?>?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_connection_list, parent, false)
            return ConnectionHolder(v, context)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ConnectionHolder) {
            holder.bind(connectionListFiltered!!.get(position), holder.adapterPosition, onItemClick, tabPosition, from)
        }
    }

    override fun getItemCount(): Int {
        return connectionListFiltered!!.size

    }

    override fun getItemViewType(position: Int): Int {
        return if (connectionListFiltered!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ConnectionHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        var sessionManager: SessionManager = SessionManager(context)

        init {
            sessionManager = SessionManager(context)
        }

        fun bind(list_connection: FriendListData?, position: Int, onitemClick: OnItemClick, tabPosition: Int, from: String) =
                with(itemView) {
                    tv_connectionUserName.text = list_connection?.userFirstName

                    connection_listImageview.setImageURI(RestClient.image_base_url_users + list_connection!!.userProfilePicture)
                    tv_connectionType.text = list_connection.connectionType[0].conntypeName

                    if (!list_connection.connectionType[0].conntypeName.isNullOrEmpty()) {

                        if (list_connection.connectionType[0].conntypeName.equals("Friends")) {
                            tv_connectionType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.friend_icon_small_connection, 0, 0, 0)
                        } else if (list_connection.connectionType[0].conntypeName.equals("Acquaintances")) {
                            tv_connectionType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.acquaintance_icon_small_connection, 0, 0, 0)
                        } else if (list_connection.connectionType[0].conntypeName.equals("Professionals")) {
                            tv_connectionType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.professional_icon_small_connection, 0, 0, 0)
                        } else {
                            tv_connectionType.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                        }
                    }

                    itemView.setOnClickListener {
                        onitemClick.onClicklisneter(position,"chat")
                    }

                }
    }

    override fun getSections(): Array<Any>? {
        return null
    }

    override fun getSectionForPosition(position: Int): Int {
        return 0
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        if (sectionIndex == 35) {
            return 0
        }
        for (i in 0 until connectionListFiltered!!.size) {
            val l = connectionListFiltered!![i]
            val firstChar = l.toString().get(0)
//            val firstChar = l.toUpperCase().get(0)
            if (firstChar.toInt() == sectionIndex) {
                return i
            }
        }
        return -1
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                connectionListFiltered = if (charString.isEmpty()) {
                    listData!!
                } else {
                    val filteredList: MutableList<FriendListData?> = ArrayList<FriendListData?>()
                    for (row in listData!!) {

                        if (row!!.userFirstName.toLowerCase().contains(charString.toLowerCase()) || row.userFirstName.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = connectionListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                connectionListFiltered = filterResults.values as ArrayList<FriendListData?>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String)
    }

    init {
        connectionListFiltered = listData!!
    }
}