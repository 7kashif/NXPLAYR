package com.nxplayr.fsl.ui.fragments.explorepost.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_explore_video_list_detail_activity.view.*
import java.util.*

class ExploreVideoListAdapater(

    val context: Activity,
    val onItemClick: OnItemClick,
    val exploreVideoData: ArrayList<CreatePostData?>?,
    val viewAll: Boolean = false

) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view: View? = null

        if (viewType == MyUtils.Loder_TYPE) run {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)

        } else {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_explore_video_list_detail_activity, parent, false)
            return BlogsViewHolder(v, context)

        }
    }

    override fun getItemCount(): Int {
        return exploreVideoData?.size!!
    }

    override fun getItemViewType(position: Int): Int {
        return if (exploreVideoData!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is BlogsViewHolder) {
            var holder1=holder as BlogsViewHolder
            holder1.bind(exploreVideoData!![position]!!, position, onItemClick)
            holder1.itemView.setOnClickListener {
                if (onItemClick != null)
                    onItemClick.onClicklisneter(holder1.adapterPosition)
            }
        }
    }

    class BlogsViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(exploreVideoData: CreatePostData, position: Int, onitemClick: OnItemClick) =
                with(itemView) {

                    txt_list_name.text = exploreVideoData?.userFirstName
                    txt_list_like.text = exploreVideoData?.postLike
                    txt_list_view.text = exploreVideoData?.postViews

                    img_user_list?.setImageURI(RestClient.image_base_url_users + exploreVideoData!!.userProfilePicture)

                    var FileName = RestClient.image_base_url_posts + exploreVideoData!!.postSerializedData!![0].albummedia!![0].albummediaFile
                    FileName = FileName.substring(0, FileName.lastIndexOf("."));

                    Picasso.get().load(FileName + "_thumb.jpg").into(img_video)
                }

    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int)

    }
}