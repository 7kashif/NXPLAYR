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
import com.nxplayr.fsl.data.model.SuggestedFriendData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_add_connection_list.view.*

class AddConnectionListAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val listData: ArrayList<SuggestedFriendData?>?
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), SectionIndexer, Filterable {
    private var connectionListFiltered: List<SuggestedFriendData?>?


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_add_connection_list, parent, false)
            return AddConnectionHolder(v, context)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is AddConnectionHolder) {
            val holder1 = holder as AddConnectionHolder
            holder.bind(connectionListFiltered!![position], holder1.adapterPosition, onItemClick)
        }
    }

    override fun getItemCount(): Int {
        return connectionListFiltered!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (connectionListFiltered!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class AddConnectionHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(
            list_connection: SuggestedFriendData?,
            position: Int,
            onitemClick: OnItemClick
        ) =
                with(itemView) {

                    tv_name.text = list_connection!!.userFirstName
                    tv_mobile_num.text = list_connection!!.userMobile
                    connection_listImageview.setImageURI(RestClient.image_base_url_users + list_connection.userProfilePicture)

                    if (list_connection.isYouFollowing == "Yes") {
                        image_add_following.visibility = View.VISIBLE
                        image_add_follower.visibility = View.GONE
                        image_add_friend.visibility = View.VISIBLE
                    } else {
                        image_add_following.visibility = View.GONE
                        image_add_follower.visibility = View.VISIBLE
                        image_add_friend.visibility = View.VISIBLE
                    }

                    image_add_follower.setOnClickListener {
                        onitemClick.onClicklisneter(position, "follow", list_connection, image_add_follower)
                        image_add_follower.visibility = View.GONE
                        image_add_following.visibility = View.VISIBLE
                    }

                    image_add_following.setOnClickListener {
                        onitemClick.onClicklisneter(position, "unfollow", list_connection, image_add_following)
                        image_add_following.visibility = View.GONE
                        image_add_follower.visibility = View.VISIBLE
                    }

                    image_add_friend.setOnClickListener {
                        onitemClick.onClicklisneter(position, "addFriend", list_connection, image_add_friend)
                    }
                    connection_listImageview.setOnClickListener {
                        onitemClick.onClicklisneter(position, "connection", list_connection, connection_listImageview)

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
        for (i in 0 until listData!!.size) {
            val l = listData!![i]

            var firstChar = l!!.userFirstName
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
                    val filteredList: MutableList<SuggestedFriendData?> = ArrayList<SuggestedFriendData?>()
                    for (row in listData!!) {

                        if (row!!.userFirstName.toLowerCase().contains(charString.toLowerCase()) || row!!.userMobile.contains(charSequence)) {
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
                connectionListFiltered = filterResults.values as ArrayList<SuggestedFriendData?>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, name: String, connectionData: SuggestedFriendData?, v: View)
    }


    init {
        connectionListFiltered = listData!!
    }

}