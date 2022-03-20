package com.nxplayr.fsl.ui.fragments.setting.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.HidePostData
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_save_posts_list.view.*

class HidePostsAdapter(val context: Activity,
                       val listPosts: ArrayList<HidePostData?>?,
                       val onItemClick: OnItemClick
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_save_posts_list, parent, false)
            return HidePostsViewHolder(v)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LoaderViewHolder) {

        } else if (holder is HidePostsViewHolder) {
            val holder1 = holder as HidePostsViewHolder
            holder.bind(listPosts!![position], holder1.adapterPosition, onItemClick)
        }

    }

    override fun getItemCount(): Int {

        return listPosts!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (listPosts!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }


    class HidePostsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(listPosts: HidePostData?, position: Int, onItemClick: OnItemClick) = with(itemView) {
            var postDate: String = ""
            if (!listPosts?.postdata.isNullOrEmpty()) {
//                img_savePost.setImageURI(RestClient.image_base_url_users + listPosts!!.postdata[0].userProfilePicture)
                tv_username.text = listPosts!!.postdata[0].userFirstName + " " + listPosts!!.postdata[0].userLastName
                tv_postType.text = listPosts.postdata[0].postMediaType
                tv_details_savePost.text = Constant.decode(listPosts.postdata[0].postDescription)
                postDate = MyUtils.covertTimeToText(listPosts!!.posthideDate!!)
                tv_posthours.text = postDate

            }

            if (!listPosts?.postdata!!.isNullOrEmpty()) {
                if (listPosts?.postdata!![0].postMediaType.equals("Photo")) {
                    img_savePost.setImageURI(RestClient.image_base_url_posts + listPosts!!.postdata[0].postSerializedData[0].albummedia[0].albummediaFile)
                    image_mediaType.setImageDrawable(resources.getDrawable(R.drawable.photo_icon_post))

                } else if (listPosts?.postdata!![0].postMediaType.equals("Video")) {
                    img_savePost.visibility = View.VISIBLE
                    var videoPath = RestClient.image_base_url_posts + listPosts!!.postdata[0].postSerializedData[0].albummedia[0].albummediaThumbnail
                    img_savePost.setImageURI(videoPath)
                    image_mediaType.setImageDrawable(resources.getDrawable(R.drawable.video_camera))

                } else if (listPosts?.postdata!![0].postMediaType.equals("Link")) {
                    img_savePost.setImageURI(RestClient.image_base_url_posts + listPosts!!.postdata[0].postSerializedData[0].albummedia[0].albummediaFile)
                    image_mediaType.setImageDrawable(resources.getDrawable(R.drawable.small_link_icon))

                } else {
                    img_savePost.setImageURI(RestClient.image_base_url_users + listPosts!!.postdata[0].userProfilePicture)
                }
            }

            img_unsvePost.setOnClickListener {
                onItemClick.onClicled(position, "unhidePost")
            }
            itemView.setOnClickListener {
                onItemClick.onClicled(position, "")
            }
        }
       }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }
}