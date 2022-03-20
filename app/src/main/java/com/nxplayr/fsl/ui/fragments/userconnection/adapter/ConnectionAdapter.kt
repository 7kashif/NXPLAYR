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
import kotlinx.android.synthetic.main.item_connection_list.view.*

class ConnectionAdapter(
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
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_connection_list, parent, false)
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
                    if (from.equals("profile")) {
                        add_more_connection.visibility = View.VISIBLE
                        ll_otherConnect.visibility = View.GONE
                        tv_connectionType.visibility = View.VISIBLE
                        tv_connectionType.text = list_connection!!.connectionType[0].conntypeName

                    } else if (from.equals("otherUser")) {
                        if (!list_connection!!.userID.equals(sessionManager.get_Authenticate_User().userID)) {
                            add_more_connection.visibility = View.GONE
                            ll_otherConnect.visibility = View.VISIBLE
                            tv_connectionType.visibility = View.GONE

                            if (list_connection?.isYouFollowing == "Yes") {
                                img_followUser.visibility = View.GONE
                                img_followingUser.visibility = View.VISIBLE
                            } else {
                                img_followUser.visibility = View.VISIBLE
                                img_followingUser.visibility = View.GONE
                            }
                            if (list_connection!!.isYourFriend == "Yes" || list_connection.isYouSentRequest == "Yes") {
                                ll_mainFriendConnec.visibility = View.VISIBLE
                                img_addFriendList.visibility = View.GONE
                                btnFriendConnect.text = list_connection!!.connectionType[0].conntypeName
                            } else {
                                ll_mainFriendConnec.visibility = View.GONE
                                img_addFriendList.visibility = View.VISIBLE
                            }
                        } else {
                            ll_otherConnect.visibility = View.GONE
                        }
                    }

                    connection_listImageview.setImageURI(RestClient.image_base_url_users + list_connection!!.userProfilePicture)

                    add_more_connection.setOnClickListener { onitemClick.onClicklisneter(position, "", list_connection, add_more_connection) }

                    ll_mainFriendConnec.setOnClickListener { onitemClick.onClicklisneter(position, "changeConnecType", list_connection, ll_mainFriendConnec) }

                    img_followUser.setOnClickListener { onitemClick.onClicklisneter(position, "FollowUser", list_connection, img_followUser) }

                    img_followingUser.setOnClickListener { onitemClick.onClicklisneter(position, "UnfollowUser", list_connection, img_followingUser) }

                    img_addFriendList.setOnClickListener {
                        onitemClick.onClicklisneter(position, "addFriend", list_connection, img_addFriendList)
                    }
                    tv_connectionType.setOnClickListener {
                        onitemClick.onClicklisneter(position, "connectionType", list_connection, tv_connectionType)

                    }
                    itemView.setOnClickListener {
                        onitemClick.onClicklisneter(position, "profile", list_connection, img_addFriendList)

                    }


                    if (!list_connection!!.connectionType[0].conntypeName.isNullOrEmpty()) {

                        if (list_connection.connectionType[0].conntypeName.equals("Friends")) {
                            tv_connectionType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.friend_icon_small_connection, 0, 0, 0)
                            btnFriendConnect.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.friend_icon_small_connection, 0, 0, 0)
                        } else if (list_connection.connectionType[0].conntypeName.equals("Acquaintances")) {
                            tv_connectionType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.acquaintance_icon_small_connection, 0, 0, 0)
                            btnFriendConnect.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.acquaintance_icon_small_connection, 0, 0, 0)
                        } else if (list_connection.connectionType[0].conntypeName.equals("Professionals")) {
                            tv_connectionType.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.professional_icon_small_connection, 0, 0, 0)
                            btnFriendConnect.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.professional_icon_small_connection, 0, 0, 0)
                        } else {
                            tv_connectionType.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0)
                        }
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

                        if (row!!.userFirstName.toLowerCase().contains(charString.toLowerCase()) || row!!.userFirstName.contains(charSequence)) {
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
        fun onClicklisneter(pos: Int, name: String, connectionData: FriendListData, v: View)
    }

    init {
        connectionListFiltered = listData!!
    }
}