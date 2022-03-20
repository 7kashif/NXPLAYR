package com.nxplayr.fsl.ui.fragments.userfootballleague.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.UserLanguageList
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_footballlang_detail.view.*
import java.util.*

class FootballLanguageListAdapter(

    val context: Activity,
    val onItemClick: OnItemClick,
    val exploreVideoData: ArrayList<UserLanguageList?>?,
    val viewAll: Boolean = false,
    var leagueID: String,
    var otheruserID: String?,
    var userID1: String?

) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastCheckedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)

        } else {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_footballlang_detail, parent, false)
            return BlogsViewHolder(v, context)

        }
    }

    override fun getItemCount(): Int {
        return exploreVideoData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (exploreVideoData!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is BlogsViewHolder) {

            holder.bind(exploreVideoData!![position]!!, position, onItemClick)
            holder.itemView.setOnClickListener {
                if(otheruserID.equals(userID1,false))
              {
                onItemClick.onClicklisneter(holder.adapterPosition)
                leagueID = exploreVideoData!![position]!!.leagueID!!
                notifyDataSetChanged()
                }

            }
        }
        if ((exploreVideoData!![position]!!.isSelect)) {
            holder.itemView.btn_select.visibility = View.VISIBLE
            holder.itemView.btn_unselect.visibility = View.GONE
        } else {
            holder.itemView.btn_select.visibility = View.GONE
            holder.itemView.btn_unselect.visibility = View.VISIBLE
        }
        if (leagueID.equals(exploreVideoData!![position]!!.leagueID, false)) {

            holder.itemView.btn_select.visibility = View.VISIBLE
            holder.itemView.btn_unselect.visibility = View.GONE
        } else {
            holder.itemView.btn_select.visibility = View.GONE
            holder.itemView.btn_unselect.visibility = View.VISIBLE
        }
    }


    class BlogsViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(exploreVideoData: UserLanguageList, position: Int, onitemClick: OnItemClick) =
                with(itemView) {

                    tvFootbalLang.text = exploreVideoData?.leagueName

                }

    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int)

    }
}