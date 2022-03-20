package com.nxplayr.fsl.ui.fragments.feed.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.LanguageList
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_content_language_select.view.*


class LanguageListAdapter(val context: Activity,
                          val listPosts: ArrayList<LanguageList?>?,
                          val onItemClick: OnItemClick,
                          var userContentLanguage: String) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var lastCheckedPosition = -1
    var sessionManager: SessionManager = SessionManager(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_content_language_select, parent, false)
            return SavePostsViewHolder(v)
        }

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LoaderViewHolder) {

        } else if (holder is SavePostsViewHolder) {
            val holder1 = holder
            holder1.bind(listPosts!![position], holder1.adapterPosition, onItemClick)

            if (lastCheckedPosition == position) {

                holder1.checkbox_filter.setImageResource(R.drawable.checkbox_experience_selected)
            }else {
                holder1.checkbox_filter.setImageResource(R.drawable.checkbox_experience_unselected)
            }

            holder1.itemView.setOnClickListener {
                if (lastCheckedPosition == holder1.adapterPosition) {
                    lastCheckedPosition = -1
                    listPosts?.get(position)?.status=false
                    if (onItemClick != null)
                        onItemClick!!.onClicled(holder1.adapterPosition,"deSelected")

                } else {
                    lastCheckedPosition = holder1.adapterPosition
                    listPosts?.get(position)?.status=true
                    if (onItemClick != null)
                        onItemClick!!.onClicled(holder1.adapterPosition,"Selected")
                }
               notifyDataSetChanged()
            }

        }

    }

    override fun getItemCount(): Int {

        return listPosts!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listPosts!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class SavePostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox_filter = itemView.findViewById(R.id.checkbox_filter) as ImageView

        fun bind(listPosts: LanguageList?, position: Int, onItemClick: OnItemClick) = with(itemView) {
            tv_language.text = listPosts?.languageName
        }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }
}