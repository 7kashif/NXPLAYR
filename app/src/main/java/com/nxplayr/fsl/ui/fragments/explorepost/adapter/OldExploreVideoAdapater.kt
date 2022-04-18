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
import kotlinx.android.synthetic.main.item_explore_video_grid_detail_activity.view.*
import java.util.*

class OldExploreVideoAdapater(
    val context: Activity,
    val onItemClick: OnItemClick,
    val exploreVideoData: ArrayList<CreatePostData?>?,
    val viewAll: Boolean = false
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == MyUtils.Loder_TYPE) run {

            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)

        } else {

            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_explore_video_grid_detail_activity, parent, false)
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
                if (onItemClick != null)
                    onItemClick.onClicklisneter(holder.adapterPosition)
            }
        }
    }


    class BlogsViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(exploreVideoData: CreatePostData, position: Int, onitemClick: OnItemClick) =
                with(itemView) {

                    txt_name.text = exploreVideoData?.userFirstName+" "+exploreVideoData.userLastName
                    txt_like.text = exploreVideoData?.postLike
                    txt_view.text = exploreVideoData?.postViews
                     if(exploreVideoData!!.userProfilePicture.isNullOrEmpty())
                     {
                         img_userexplore?.setImageResource(R.drawable.profile_pic_placeholder)

                     }
                    else
                    {
                        img_userexplore?.setImageURI(RestClient.image_base_url_users + exploreVideoData!!.userProfilePicture)

                    }

                        if( exploreVideoData!!.postSerializedData!![0].albummedia!![0].albummediaThumbnail.isNullOrEmpty())
                        {
                            if(exploreVideoData!!.postSerializedData!![0].albummedia!![0].albummediaFile.isNullOrEmpty())
                            {
                                img_product.background=context?.resources?.getDrawable(R.color.black)
                            }else{
                                var FileName = RestClient.image_base_url_posts + exploreVideoData!!.postSerializedData!![0].albummedia!![0].albummediaFile
                                FileName = FileName.substring(0, FileName.lastIndexOf("."));
                                img_product.setImageURI(FileName + "_thumb.jpg")
                            }
                        }
                        else
                        {
                            var FileName = RestClient.image_base_url_posts + exploreVideoData!!.postSerializedData!![0].albummedia!![0].albummediaThumbnail
                            img_product.setImageURI(FileName)
                        }


                }

    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int)
    }

}