package com.nxplayr.fsl.ui.fragments.userconnection.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FriendListData
import java.util.*

/**
 * Created by dhavalkaka on 03/02/2018.
 */
class FriendListAdapter( private val mContext: Context,
                         private val itemLayout: Int,
                         private var dataList:ArrayList<FriendListData?>?
) : ArrayAdapter<Any?>(mContext, itemLayout, dataList!! as List<Any>) {
    private val listFilter = ListFilter()
    private var dataListAllItems: ArrayList<FriendListData?>? = null
    override fun getCount(): Int {
        return dataList!!.size
    }

    override fun getItem(position: Int): String? {
        return dataList!![position]!!.userFirstName!!.trim { it <= ' ' } +dataList!![position]!!.userLastName
    }

    override fun getView(
            position: Int,
            view: View?,
            parent: ViewGroup
    ): View {
        var view = view
        if (view == null) {
            view = LayoutInflater.from(parent.context)
                    .inflate(itemLayout, parent, false)
        }
        val ivFriendsPhoto: ImageView =
                view!!.findViewById<View>(R.id.friendprofileImageview) as SimpleDraweeView
        val tvFriendName =
                view.findViewById<View>(R.id.friendprofilenameTextview) as TextView
        tvFriendName.text =
                dataList!![position]!!.userFirstName!!.trim { it <= ' ' } + " " + dataList!![position]!!.userLastName
        ivFriendsPhoto.setImageURI(Uri.parse(RestClient.image_base_url_users + dataList!![position]!!.userProfilePicture))
        return view
    }

    /*@NonNull
      @Override
      public Filter getFilter() {
          return listFilter;
      }
  */
    inner class ListFilter : Filter() {
        private val lock = Any()
        override fun performFiltering(prefix: CharSequence): FilterResults {
            val results = FilterResults()
            if (dataListAllItems == null) {
                synchronized(lock) { dataListAllItems = ArrayList(dataList!!) }
            }
            if (prefix == null || prefix.length == 0) {
                synchronized(lock) {
                    results.values = dataListAllItems
                    results.count = dataListAllItems!!.size
                }
            } else if (prefix.toString().startsWith("@")) {
                val searchStrLowerCase = prefix.toString().toLowerCase()
                val matchValues = ArrayList<FriendListData>()
                for (dataItem in dataListAllItems!!) {
                    if (dataItem?.userFirstName!!.toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(dataItem)
                    }
                }
                results.values = matchValues
                results.count = matchValues.size
            } else {
                val searchStrLowerCase = prefix.toString().toLowerCase()
                val matchValues = ArrayList<FriendListData>()
                for (dataItem in dataListAllItems!!) {
                    if (dataItem?.userFirstName.toString().toLowerCase().contains(searchStrLowerCase)) {
                        matchValues.add(dataItem!!)
                    }
                }
                results.values = matchValues
                results.count = matchValues.size
            }
            return results
        }

        override fun publishResults(
                constraint: CharSequence,
                results: FilterResults
        ) {
            dataList = if (results.values != null) {
                results.values as ArrayList<FriendListData?>?
            } else {
                null
            }
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

}