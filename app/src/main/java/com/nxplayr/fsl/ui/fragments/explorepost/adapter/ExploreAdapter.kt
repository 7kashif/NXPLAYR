package com.nxplayr.fsl.ui.fragments.explorepost.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.util.MyUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_explore_video_grid_detail_activity.view.*
import kotlinx.android.synthetic.main.item_explore_video_list_detail_activity.view.*

class ExploreAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val exploreData: ArrayList<CreatePostData?>?,
    val viewAll: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MyUtils.GRID_TYPE -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_explore_video_grid_detail_activity, parent, false)
                BlogsViewHolder(v, context)
            }
            MyUtils.LIST_TYPE -> {
                val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_explore_video_list_detail_activity, parent, false)
                BlogsListViewHolder(v, context)
            }
            else -> {
                val view =
                    LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
                LoaderViewHolder(view)
            }
        }
    }

    fun changeView(isListView: Boolean) {
        for (i in 0 until exploreData?.size!!) {
            exploreData[i]?.isListView = isListView
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return exploreData!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            exploreData!![position] == null -> MyUtils.Loder_TYPE
            exploreData[position]?.isListView == true -> MyUtils.LIST_TYPE
            else -> MyUtils.GRID_TYPE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is BlogsViewHolder) {
            holder.bind(exploreData!![position]!!, position, onItemClick)
            holder.itemView.setOnClickListener {
                if (onItemClick != null)
                    onItemClick.onClickLisneter(position)
            }
        } else if (holder is BlogsListViewHolder) {
            holder.bind(exploreData!![position]!!, position, onItemClick)
            holder.itemView.setOnClickListener {
                if (onItemClick != null)
                    onItemClick.onClickLisneter(position)
            }
        }
    }


    class BlogsViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(exploreData: CreatePostData, position: Int, onItemClick: OnItemClick) =
            with(itemView) {

                txt_name.text =
                    exploreData?.userFirstName + " " + exploreData.userLastName
                txt_like.text = exploreData?.postLike
                txt_view.text = exploreData?.postViews
                if (exploreData!!.userProfilePicture.isNullOrEmpty()) {
                    img_userexplore?.setImageResource(R.drawable.profile_pic_placeholder)

                } else {
                    img_userexplore?.setImageURI(RestClient.image_base_url_users + exploreData!!.userProfilePicture)

                }

                if (exploreData.postSerializedData[0].albummedia[0].albummediaThumbnail.isNullOrEmpty()) {
                    if (exploreData.postSerializedData[0].albummedia[0].albummediaFile.isNullOrEmpty()) {
                        img_product.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.black
                            )
                        )
                    } else {
                        var FileName =
                            RestClient.image_base_url_posts + exploreData.postSerializedData[0].albummedia[0].albummediaFile
                        FileName = FileName.substring(0, FileName.lastIndexOf("."));
                        img_product.setImageURI(FileName + "_thumb.jpg")
                    }
                } else {
                    var FileName =
                        RestClient.image_base_url_posts + exploreData.postSerializedData[0].albummedia[0].albummediaThumbnail
                    img_product.setImageURI(FileName)
                }
            }
    }


    class BlogsListViewHolder(itemView: View, context: Activity) :
        RecyclerView.ViewHolder(itemView) {

        fun bind(exploreData: CreatePostData, position: Int, onItemClick: OnItemClick) =
            with(itemView) {

                txt_list_name.text = exploreData.userFirstName
                txt_list_like.text = exploreData.postLike
                txt_list_view.text = exploreData.postViews
                txt_list_hashTag.text = Constant.decode(exploreData.postDescription)


                img_user_list?.setImageURI(RestClient.image_base_url_users + exploreData.userProfilePicture)

                var fileName =
                    RestClient.image_base_url_posts + exploreData.postSerializedData[0].albummedia[0].albummediaFile
                fileName = fileName.substring(0, fileName.lastIndexOf("."));

                Picasso.get().load(fileName + "_thumb.jpg").into(img_video)
            }

    }

    interface OnItemClick {
        fun onClickLisneter(pos: Int)
    }

}