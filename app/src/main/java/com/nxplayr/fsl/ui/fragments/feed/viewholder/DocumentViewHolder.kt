package com.nxplayr.fsl.ui.fragments.feed.viewholder

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.fragments.feed.adapter.CommentAdapter
import com.nxplayr.fsl.ui.fragments.feed.adapter.TrendingItemDocumentAdapter
import com.nxplayr.fsl.util.BlurPostprocessor
import kotlinx.android.synthetic.main.item_feed_list.view.*

/**
 * Created by dhavalkaka on 12/02/2018.
 */
class DocumentViewHolder(itemView: View?, var context: Activity) : RecyclerView.ViewHolder(itemView!!)
{
    val feed_userImg = itemView?.feed_userImg
    val tv_name = itemView?.tv_name
    val homeUserProfession = itemView?.homeUserProfession
    val btnLike = itemView?.btnLike
    val homePhotoVideoTime = itemView?.homePhotoVideoTime
    val tv_details = itemView?.tv_details
    val img_like = itemView?.img_like
    val tv_like_count = itemView?.tv_like_count
    val btn_chat = itemView?.btn_chat
    val tv_chat_count = itemView?.tv_chat_count
    val btn_views = itemView?.btn_views
    val tv_views_count = itemView?.tv_views_count
    val btn_share = itemView?.btn_share
    val tv_share = itemView?.tv_share
    val btn_more = itemView?.btn_more
    val userImage = itemView?.userImage
    val emoji = itemView?.emoji
    val blurImage = itemView?.blurImage
    val recycle_view_feedhome = itemView?.recycle_view_feedhome
    var linearLayoutManager : LinearLayoutManager?=null
    var trendingItemPhotoAdapter: TrendingItemDocumentAdapter? = null
    val ll_like = itemView?.ll_like
    val ll_comment = itemView?.ll_comment
    val ll_mainUserDetail = itemView?.ll_mainUserDetail
    val ll_userName = itemView?.ll_userName
    val ll_view = itemView?.ll_view
    val ll_more = itemView?.ll_more
    val bluroverlay = itemView?.bluroverlay
    val ll_add_location = itemView?.ll_add_location
    val tvAddLocation = itemView?.tvAddLocation
    val layout_share = itemView?.layout_share
    val ll_share = itemView?.ll_share
    val feed_userImg_share = itemView?.feed_userImg_share
    val tv_name_share = itemView?.tv_name_share
    val homeUserProfession_share = itemView?.homeUserProfession_share
    val homePhotoVideoTime_share = itemView?.homePhotoVideoTime_share
    val tv_details_share = itemView?.tv_details_share
    val tvAddLocation_share = itemView?.tvAddLocation_share
    val ll_add_location_share = itemView?.ll_add_location_share
    var target: Target?=null
    var postprocessor: BlurPostprocessor?=null
    val ll_comment_list = itemView?.ll_comment_list
    val edit_post_comment = itemView?.edit_post_comment
    val recyclerview_comment = itemView?.recyclerview_comment
    var commentListAdapter: CommentAdapter? = null
    private var linearLayoutManagerComment: LinearLayoutManager? = null
    val tvCommenViewAll = itemView?.tvCommenViewAll
    val tv_see_transalation = itemView?.tv_see_transalation
    val tv_details_translate = itemView?.tv_details_translate
    val ll_translate = itemView?.ll_translate
    val transalteProgressview = itemView?.transalteProgressview

    init {
        
        blurImage?.visibility=View.VISIBLE
        bluroverlay?.visibility=View.VISIBLE
        context=itemView?.context as Activity
        postprocessor= BlurPostprocessor(context, 30,10)
        linearLayoutManagerComment = LinearLayoutManager(context!!)
        recyclerview_comment?.layoutManager = linearLayoutManagerComment

        linearLayoutManager = LinearLayoutManager(context)
        trendingItemPhotoAdapter= TrendingItemDocumentAdapter(context)
        recycle_view_feedhome?.layoutManager = linearLayoutManager
        recycle_view_feedhome?.adapter = trendingItemPhotoAdapter
        recycle_view_feedhome?.setHasFixedSize(true)
    }

}