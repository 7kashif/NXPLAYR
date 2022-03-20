package com.nxplayr.fsl.ui.fragments.feed.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.net.ParseException
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.gson.Gson
import com.nxplayr.fsl.*
import com.nxplayr.fsl.ui.activity.post.view.DocumentActivity
import com.nxplayr.fsl.ui.activity.fullscreenvideo.view.FullScreenVideo
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.fullscreenvideo.view.PhotoGallaryView
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.fragments.feed.view.HashTagPostListFragment
import com.nxplayr.fsl.ui.fragments.postcomment.view.PostCommentListFragment
import com.nxplayr.fsl.ui.fragments.feed.view.PostViewLikeListFragment
import com.nxplayr.fsl.data.model.FeedModel
import com.nxplayr.fsl.data.model.Model
import com.nxplayr.fsl.ui.fragments.feedlikeviewlist.viewmodel.PostLike
import com.nxplayr.fsl.ui.fragments.postcomment.viewmodel.PostView
import com.nxplayr.fsl.ui.fragments.feed.viewholder.*
import com.nxplayr.fsl.util.*
import com.nxplayr.fsl.util.PostDesTextView.OnCustomEventListener
import com.squareup.okhttp.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.Serializable


@Suppress("DEPRECATION")
class HomeFeedListAdapter(
    val context: Activity,
    val feedlist: ArrayList<CreatePostData?>,
    val onItemClick: OnItemClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var exoPlayer: SimpleExoPlayer? = null
    var videoPlayPosition = -1
    var cache: SimpleCache? = null
    var sessionManager: SessionManager = SessionManager(context)
    var isExpandPosition: Int = -1
    var userId: String? = ""
    var userData: SignupData? = null
    var loginUserId = ""

    init {
        loginUserId = sessionManager?.get_Authenticate_User().userID
        userData = sessionManager.get_Authenticate_User()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null

        when (viewType) {
            Model.Loder_TYPE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
                viewHolder = LoaderViewHolder(view)

            }
            Model.IMAGE_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_feed_list, parent, false)
                viewHolder = ImageViewHolder(view, context)

            }
            Model.Video_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_feed_list, parent, false)
                viewHolder = VideoViewHolder(view, context)
            }
            Model.Link_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_feed_list, parent, false)
                viewHolder = LinkViewHolder(view, context)
            }
            Model.Document_TYPE -> {
                val view =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_feed_list, parent, false)
                viewHolder = DocumentViewHolder(view, context)
            }else-> {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_feed_list, parent, false)
            viewHolder = LinkViewHolder(view, context)

        }
        }
        return viewHolder!!

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

            val loaderViewHolder = holder
            loaderViewHolder.mProgressBar?.visibility = View.VISIBLE
            return


        }
        else if (holder is ImageViewHolder) {
            val holder1 = holder
            holder1.tv_name?.text =
                "${feedlist.get(position)?.userFirstName} ${feedlist.get(position)?.userLastName}"
            var postDate: String = ""
            try {
                postDate =
                    MyUtils.getDisplayableTime(feedlist.get(position)?.postCreatedMinutesAgo!!.toLong())
            } catch (e: Exception) {
            }
            if (!feedlist.get(position)?.postDescription.isNullOrEmpty()) {
                if (userData?.userContentLanguageCode.equals(feedlist.get(position)?.postLanguage)) {
                    holder1.tv_see_transalation?.visibility = View.GONE
                } else if (userData?.userContentLanguageCode.isNullOrEmpty()) {
                    holder1.tv_see_transalation?.visibility = View.GONE
                } else {

                    holder1.tv_see_transalation?.visibility = View.VISIBLE
                }
            }

            holder1.ll_translate?.setOnClickListener {
                getTranslate(
                    holder1.ll_translate,
                    holder1.transalteProgressview,
                    holder1.tv_details_translate,
                    holder1.tv_see_transalation,
                    feedlist.get(position)?.postDescription,
                    userData?.userContentLanguageCode,
                    feedlist.get(position)?.postLanguage
                )
            }
            holder1.tv_see_transalation?.setOnClickListener {
                holder1.ll_translate?.performClick()
            }

            holder1.homePhotoVideoTime?.text = "${feedlist.get(position)?.postMediaType}${postDate}"
            holder1.tv_like_count?.text = feedlist.get(position)?.postLike
            holder1.tv_chat_count?.text = feedlist.get(position)?.postComment
            holder1.tv_views_count?.text = feedlist.get(position)?.postViews
            holder1.userImage?.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
            holder1.feed_userImg?.setImageURI(
                RestClient.image_base_url_users + feedlist.get(
                    position
                )?.userProfilePicture
            )
            if (feedlist.get(position)?.originaluserID.equals("0")) {
                holder1.ll_share?.visibility = View.GONE
                setPostDescription(
                    holder1.tv_details!!,
                    holder1.adapterPosition,
                    if (feedlist.get(position)?.postDescription.isNullOrEmpty()) "" else Constant.decode(
                        feedlist.get(position)?.postDescription
                    )
                )

                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location?.visibility = View.GONE
                    holder1.tvAddLocation?.visibility = View.GONE
                }
                holder1.ll_add_location_share?.visibility = View.GONE
                holder1.tvAddLocation_share?.visibility = View.GONE
            } else {
                holder1.ll_share?.visibility = View.VISIBLE
                holder1.feed_userImg_share?.setImageURI(
                    RestClient.image_base_url_users + feedlist.get(
                        position
                    )?.originalUserProfilePicture
                )
                holder1.tv_name_share?.text =
                    "${feedlist.get(position)?.originalUserFirstName} ${feedlist.get(position)?.originalUserLastName}"
                var postDateOriginal: String = ""
                try {
                    postDateOriginal =
                        MyUtils.getDisplayableTime(feedlist[position]?.orgPostPostCreatedMinutesAgo?.toLong()!!)
                } catch (e: Exception) {
                }
                holder1.homePhotoVideoTime_share?.text =
                    "${feedlist.get(position)?.postMediaType}${postDateOriginal}"
                if (!feedlist.get(position)?.postWriteSomething.isNullOrEmpty()) {
                    setPostDescription(
                        holder1.tv_details!!,
                        holder1.adapterPosition,
                        Constant.decode(feedlist.get(position)?.postWriteSomething)
                    )

                }
                if (!feedlist.get(position)?.postDescription.isNullOrEmpty()) {
                    setPostDescriptionOriginal(
                        holder1.tv_details_share!!,
                        holder1.adapterPosition,
                        Constant.decode(feedlist.get(position)?.postDescription)
                    )
                }

                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location_share?.visibility = View.GONE
                    holder1.tvAddLocation_share?.visibility = View.GONE
                }
                holder1.ll_add_location?.visibility = View.GONE
                holder1.tvAddLocation?.visibility = View.GONE
            }
            if (isExpandPosition == position) {

                holder1.ll_comment_list?.visibility = View.VISIBLE

            } else {

                holder1.ll_comment_list?.visibility = View.GONE
            }
            if (!feedlist.get(position)?.postCommentList.isNullOrEmpty()) {
                if (feedlist.get(position)?.postCommentList!!.size >= 2) {

                    holder1.tvCommenViewAll?.visibility = View.VISIBLE
                } else {

                    holder1.tvCommenViewAll?.visibility = View.GONE
                }
            } else {
                holder1.tvCommenViewAll?.visibility = View.GONE
            }
            holder1.commentListAdapter = CommentAdapter(
                context!!, feedlist.get(position)?.postCommentList!!,
                object : CommentAdapter.OnItemClick {
                    override fun onClicklisneter(
                        pos: Int,
                        actionType: Int,
                        comentObj: CommentData,
                        v1: View,
                        commentId: String,
                        comment: String,
                        replyPos: Int
                    ) {
                        var postCommentListFragment = PostCommentListFragment()
                        Bundle().apply {
                            putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                            putString(
                                "postUserID",
                                feedlist.get(holder.getAdapterPosition())!!.userID
                            )
                            putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                            postCommentListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            postCommentListFragment,
                            postCommentListFragment::class.simpleName!!,
                            true
                        )

                    }

                    override fun onClickListner(position: Int, from: String, data: CommentData) {
                        var postCommentListFragment = PostCommentListFragment()
                        Bundle().apply {
                            putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                            putString(
                                "postUserID",
                                feedlist.get(holder.getAdapterPosition())!!.userID
                            )
                            putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                            postCommentListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            postCommentListFragment,
                            postCommentListFragment::class.simpleName!!,
                            true
                        )


                    }

                }, ""
            )
            holder1.recyclerview_comment?.adapter = holder1.commentListAdapter
            try {
                val request = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(
                        RestClient.image_base_url_posts + feedlist.get(position)?.postSerializedData?.get(
                            0
                        )?.albummedia?.get(0)?.albummediaFile
                    )
                )
                    .setPostprocessor(holder1.postprocessor)
                    .build()
                val controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder1.blurImage?.controller)
                    .build() as PipelineDraweeController
                holder1.blurImage?.controller = controller
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder1.trendingItemPhotoAdapter?.setActivity(context, position)
            holder1.trendingItemPhotoAdapter?.setData(feedlist[position]!!, position)
            holder1.trendingItemPhotoAdapter?.setClickListener(object :
                TrendingItemPhotoAdapter.ClickInterface {
                override fun openPhotoDetails(pos: Int) {

                    Intent(context, PhotoGallaryView::class.java).apply {
                        putExtra(
                            "photoUri",
                            feedlist[position]!!.postSerializedData[0].albummedia as Serializable
                        )
                        context.startActivity(this)
                    }
                    /*if (holder.getAdapterPosition() > -1)
                        setPostViewsCount(holder.getAdapterPosition())
                    if (feedlist[holder.getAdapterPosition()]!!.postSerializedData[0].albummedia.size == 1) {
                        val i = Bundle()
                        i.putInt("pos", pos)
                        i.putString("postId", feedlist[holder.getAdapterPosition()]!!.postID)
                        i.putSerializable("feed", feedlist[holder.getAdapterPosition()])
                        i.putString("viewcounter", feedlist[holder.getAdapterPosition()]!!.postViews)
                        i.putString("action", "singlepohto")
                        (mContext as HomeActivity).setfrgment(PhotoGalleryFragment(), i)
                    } else {
                        val i = Bundle()
                        i.putInt("pos", pos)
                        i.putString("postId", feedlist[holder.getAdapterPosition()]!!.postID)
                        i.putString("fileName", feedlist[holder.getAdapterPosition()]!!.postSerializedData[0].albummedia[pos].albummediaFile)
                        i.putSerializable("feed", feedlist[holder.getAdapterPosition()])
                        i.putString("viewcounter", feedlist[holder.getAdapterPosition()]!!.postViews)
                        (mContext as HomeActivity).setfrgment(PhotoDetailsFragment(), i)
                    }*/
                }
            })




            setFeedData1(
                holder1.tv_like_count!!,
                holder1.tv_chat_count!!,
                holder1.tv_views_count!!,
                holder1.img_like!!,
                holder1.btn_views!!,
                holder1.btn_chat!!,
                holder1.btn_share!!,
                holder1.btnLike!!,
                holder1.adapterPosition
            )

            holder1.btnLike?.setOnClickListener {
                if (holder1.getAdapterPosition() > -1) {
                    feedLike1(
                        holder.img_like!!,
                        holder.btnLike!!,
                        feedlist.get(holder.getAdapterPosition())!!,
                        holder.getAdapterPosition(),
                        holder1.tv_like_count!!
                    );
                }
            }

            holder1.ll_view?.setOnClickListener {
                var postViewLikeListFragment = PostViewLikeListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("from", "View")
                    postViewLikeListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postViewLikeListFragment,
                    postViewLikeListFragment::class.simpleName!!,
                    true
                )
            }
            holder1.ll_like?.setOnClickListener {
                var postViewLikeListFragment = PostViewLikeListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("from", "Like")
                    postViewLikeListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postViewLikeListFragment,
                    postViewLikeListFragment::class.simpleName!!,
                    true
                )

            }
            holder1.ll_more?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "from_more_icon",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.layout_share?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "list_share",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.ll_comment?.setOnClickListener {
                if (isExpandPosition == holder.adapterPosition) {
                    isExpandPosition = -1
                } else {
                    isExpandPosition = holder.adapterPosition
                    /*if (onItemClick != null)
                        onItemClick!!.onClicklisneter(holder.adapterPosition)*/
                }
                notifyDataSetChanged()
            }
            holder1.emoji?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "sendComment",
                    feedlist.get(holder.adapterPosition)!!,
                    holder1.edit_post_comment?.text.toString().trim(),
                    holder1.edit_post_comment as View
                )

            }

            holder1.ll_mainUserDetail?.setOnClickListener {
                onItemClick.onClicled(
                    holder1.adapterPosition,
                    "OtherUserProfile",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.ll_userName?.setOnClickListener {
                onItemClick.onClicled(
                    holder1.adapterPosition,
                    "other_user_profile",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.tvCommenViewAll?.setOnClickListener {
                var postCommentListFragment = PostCommentListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("postUserID", feedlist.get(holder.getAdapterPosition())!!.userID)
                    putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                    postCommentListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postCommentListFragment,
                    postCommentListFragment::class.simpleName!!,
                    true
                )
            }
        }
        else if (holder is VideoViewHolder) {
            val holder1 = holder
            holder1.tv_name?.text =
                "${feedlist.get(position)?.userFirstName} ${feedlist.get(position)?.userLastName}"
            var postDate: String = ""
            try {
                postDate =
                    MyUtils.getDisplayableTime(feedlist.get(position)?.postCreatedMinutesAgo!!.toLong())
            } catch (e: Exception) {
            }

            if (!feedlist.get(position)?.postDescription.isNullOrEmpty()) {
                if (userData?.userContentLanguageCode.equals(feedlist.get(position)?.postLanguage)) {
                    holder1.tv_see_transalation?.visibility = View.GONE
                } else if (userData?.userContentLanguageCode.isNullOrEmpty()) {
                    holder1.tv_see_transalation?.visibility = View.GONE
                } else {

                    holder1.tv_see_transalation?.visibility = View.VISIBLE
                }
            }

            holder1.ll_translate?.setOnClickListener {
                getTranslate(
                    holder1.ll_translate,
                    holder1.transalteProgressview,
                    holder1.tv_details_translate,
                    holder1.tv_see_transalation,
                    feedlist.get(position)?.postDescription,
                    userData?.userContentLanguageCode,
                    feedlist.get(position)?.postLanguage
                )
            }
            holder1.tv_see_transalation?.setOnClickListener {
                holder1.ll_translate?.performClick()
            }
            holder1.homePhotoVideoTime?.text = "${feedlist.get(position)?.postMediaType}${postDate}"
            holder1.tv_like_count?.text = feedlist.get(position)?.postLike
            holder1.tv_chat_count?.text = feedlist.get(position)?.postComment
            holder1.tv_views_count?.text = feedlist.get(position)?.postViews
            holder1.userImage?.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
            holder1.feed_userImg?.setImageURI(
                RestClient.image_base_url_users + feedlist.get(
                    position
                )?.userProfilePicture
            )

            holder1.trendingItemVideoAdapter!!.setActivity(context)
            holder1.trendingItemVideoAdapter!!.setData(feedlist[position])
            holder1.trendingItemVideoAdapter!!.setClickListener(object :
                TrendingItemVideoAdapter.ClickInterface {
                override fun setVolume(isMuting: Boolean) {
                    muteVideo(isMuting)
                }

                override fun openVideoDetails(pos: Int) {


                    /*Intent i = new Intent(mActivity, VideoViewActivity.class);
                    i.putExtra("youtubeUrl", RestClient.ChallengesUrl + suggestedChallengesList.get(holder.getAdapterPosition()).getChallengeVideoImage());
                    mActivity.startActivity(i);
                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);*/
                    val i: Intent = Intent(context, FullScreenVideo::class.java)
                    i.putExtra(
                        "videouri",
                        RestClient.image_base_url_posts + feedlist.get(holder1.adapterPosition)!!.postSerializedData.get(
                            0
                        ).albummedia[0].albummediaFile
                    )
                    context.startActivity(i)
                    context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    /*if (holder.getAdapterPosition() > -1) setPostViewsCount(holder.getAdapterPosition())
                    pausePlayer()
                    if (feedlist[holder.getAdapterPosition()].getAlbummediatranscode().size() === 1) {
                        //    getAlbumMediaPostDetails(feedlist.get(holder.getAdapterPosition()).getPostID(),holder.linearlayoutmain,holder.getAdapterPosition());
                        val i = Intent(mContext, FullscreenVideoActivity::class.java)
                        val videoURI: String = AWSConfiguration.baseUrl + AWSConfiguration.videoPath.toString() + "/" + feedlist[holder.getAdapterPosition()].getAlbummediatranscode().get(0).getAlbummediaFile()
                        i.putExtra("videouri", videoURI)
                        i.putExtra("Seek", feedlist[holder.getAdapterPosition()].getAlbummediatranscode().get(0).getDuration())
                        mContext.startActivityForResult(i, 21)
                        mContext.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    } else {
                        val i = Bundle()
                        i.putInt("postion", pos)
                        i.putSerializable("feed", feedlist[holder.getAdapterPosition()])
                        i.putInt("postposition", holder.getAdapterPosition())
                        (mContext as HomeActivity).setfrgment(VideoDetailsFragment(), i)
                    }*/
                }
            })
            if (isExpandPosition == position) {

                holder1.ll_comment_list?.visibility = View.VISIBLE

            } else {

                holder1.ll_comment_list?.visibility = View.GONE
            }
            if (!feedlist.get(position)?.postCommentList.isNullOrEmpty()) {
                if (feedlist.get(position)?.postCommentList!!.size >= 2) {

                    holder1.tvCommenViewAll?.visibility = View.VISIBLE
                } else {

                    holder1.tvCommenViewAll?.visibility = View.GONE
                }
            } else {
                holder1.tvCommenViewAll?.visibility = View.GONE
            }
            holder1.commentListAdapter = CommentAdapter(
                context!!, feedlist.get(position)?.postCommentList!!,
                object : CommentAdapter.OnItemClick {
                    override fun onClicklisneter(
                        pos: Int,
                        actionType: Int,
                        comentObj: CommentData,
                        v1: View,
                        commentId: String,
                        comment: String,
                        replyPos: Int
                    ) {
                        var postCommentListFragment = PostCommentListFragment()
                        Bundle().apply {
                            putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                            putString(
                                "postUserID",
                                feedlist.get(holder.getAdapterPosition())!!.userID
                            )
                            putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                            postCommentListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            postCommentListFragment,
                            postCommentListFragment::class.simpleName!!,
                            true
                        )

                    }

                    override fun onClickListner(position: Int, from: String, data: CommentData) {
                        var postCommentListFragment = PostCommentListFragment()
                        Bundle().apply {
                            putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                            putString(
                                "postUserID",
                                feedlist.get(holder.getAdapterPosition())!!.userID
                            )
                            putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                            postCommentListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            postCommentListFragment,
                            postCommentListFragment::class.simpleName!!,
                            true
                        )


                    }

                }, ""
            )
            holder1.recyclerview_comment?.adapter = holder1.commentListAdapter
            if (feedlist.get(position)?.originaluserID.equals("0")) {
                holder1.ll_share?.visibility = View.GONE
                setPostDescription(
                    holder1.tv_details!!,
                    holder1.adapterPosition,
                    Constant.decode(feedlist.get(position)?.postDescription)
                )
                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location_share?.visibility = View.GONE
                    holder1.tvAddLocation_share?.visibility = View.GONE
                }
                holder1.ll_add_location?.visibility = View.GONE
                holder1.tvAddLocation?.visibility = View.GONE
            } else {
                holder1.ll_share?.visibility = View.VISIBLE
                holder1.feed_userImg_share?.setImageURI(
                    RestClient.image_base_url_users + feedlist.get(
                        position
                    )?.originalUserProfilePicture
                )
                holder1.tv_name_share?.text =
                    "${feedlist.get(position)?.originalUserFirstName} ${feedlist.get(position)?.originalUserLastName}"
                var postDateOriginal: String = ""
                try {
                    postDateOriginal =
                        MyUtils.getDisplayableTime(feedlist.get(position)?.orgPostPostCreatedMinutesAgo!!.toLong())
                } catch (e: Exception) {
                }
                holder1.homePhotoVideoTime_share?.text =
                    "${feedlist.get(position)?.postMediaType}${postDateOriginal}"
                setPostDescription(
                    holder1.tv_details_share!!,
                    holder1.adapterPosition,
                    if (feedlist.get(position)?.postWriteSomething.isNullOrEmpty()) "" else Constant.decode(
                        feedlist.get(position)?.postWriteSomething
                    )
                )
                setPostDescriptionOriginal(
                    holder1.tv_details!!,
                    holder1.adapterPosition,
                    if (feedlist.get(position)?.postDescription.isNullOrEmpty()) "" else Constant.decode(
                        feedlist.get(position)?.postDescription
                    )
                )

                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location?.visibility = View.GONE
                    holder1.tvAddLocation?.visibility = View.GONE
                }
                holder1.ll_add_location_share?.visibility = View.GONE
                holder1.tvAddLocation_share?.visibility = View.GONE
            }
            setFeedData1(
                holder1.tv_like_count!!,
                holder1.tv_chat_count!!,
                holder1.tv_views_count!!,
                holder1.img_like!!,
                holder1.btn_views!!,
                holder1.btn_chat!!,
                holder1.btn_share!!,
                holder1.btnLike!!,
                holder1.adapterPosition
            )

            holder1.btnLike?.setOnClickListener {
                if (holder1.getAdapterPosition() > -1) {
                    feedLike1(
                        holder.img_like!!,
                        holder.btnLike!!,
                        feedlist.get(holder.getAdapterPosition())!!,
                        holder.getAdapterPosition(),
                        holder1.tv_like_count!!
                    );
                }
            }
            holder1.ll_view?.setOnClickListener {
                var postViewLikeListFragment = PostViewLikeListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("from", "View")
                    postViewLikeListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postViewLikeListFragment,
                    postViewLikeListFragment::class.simpleName!!,
                    true
                )
            }
            holder1.ll_like?.setOnClickListener {
                var postViewLikeListFragment = PostViewLikeListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("from", "Like")
                    postViewLikeListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postViewLikeListFragment,
                    postViewLikeListFragment::class.simpleName!!,
                    true
                )

            }
            holder1.ll_more?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "from_more_icon",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.layout_share?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "list_share",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.ll_comment?.setOnClickListener {
                if (isExpandPosition == holder.adapterPosition) {
                    isExpandPosition = -1
                } else {
                    isExpandPosition = holder.adapterPosition
                    /*if (onItemClick != null)
                        onItemClick!!.onClicklisneter(holder.adapterPosition)*/
                }
                notifyDataSetChanged()
            }
            holder1.emoji?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "sendComment",
                    feedlist.get(holder.adapterPosition)!!,
                    holder1.edit_post_comment?.text.toString().trim(),
                    holder1.edit_post_comment as View
                )

            }
            holder1.ll_mainUserDetail?.setOnClickListener {
                onItemClick.onClicled(
                    holder1.adapterPosition,
                    "OtherUserProfile",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.ll_userName?.setOnClickListener {
                onItemClick.onClicled(
                    holder1.adapterPosition,
                    "other_user_profile",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.tvCommenViewAll?.setOnClickListener {
                var postCommentListFragment = PostCommentListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("postUserID", feedlist.get(holder.getAdapterPosition())!!.userID)
                    putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                    postCommentListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postCommentListFragment,
                    postCommentListFragment::class.simpleName!!,
                    true
                )
            }
            if (feedlist.get(position)?.originaluserID.equals("0")) {
                holder1.ll_share?.visibility = View.GONE
                setPostDescription(
                    holder1.tv_details!!,
                    holder1.adapterPosition,
                    Constant.decode(feedlist.get(position)?.postDescription)
                )
                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location_share?.visibility = View.GONE
                    holder1.tvAddLocation_share?.visibility = View.GONE
                }
                holder1.ll_add_location?.visibility = View.GONE
                holder1.tvAddLocation?.visibility = View.GONE
            } else {
                holder1.ll_share?.visibility = View.VISIBLE
                holder1.feed_userImg_share?.setImageURI(
                    RestClient.image_base_url_users + feedlist.get(
                        position
                    )?.originalUserProfilePicture
                )
                holder1.tv_name_share?.text =
                    "${feedlist.get(position)?.originalUserFirstName} ${feedlist.get(position)?.originalUserLastName}"
                var postDateOriginal: String = ""
                try {
                    postDateOriginal =
                        MyUtils.getDisplayableTime(feedlist.get(position)?.orgPostPostCreatedMinutesAgo!!.toLong())
                } catch (e: Exception) {
                }
                holder1.homePhotoVideoTime_share?.text =
                    "${feedlist.get(position)?.postMediaType}${postDateOriginal}"
                setPostDescription(
                    holder1.tv_details_share!!,
                    holder1.adapterPosition,
                    Constant.decode(feedlist.get(position)?.postWriteSomething)
                )
                setPostDescriptionOriginal(
                    holder1.tv_details!!,
                    holder1.adapterPosition,
                    Constant.decode(feedlist.get(position)?.postDescription)
                )

                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location?.visibility = View.GONE
                    holder1.tvAddLocation?.visibility = View.GONE
                }
                holder1.ll_add_location_share?.visibility = View.GONE
                holder1.tvAddLocation_share?.visibility = View.GONE
            }
        }
        else if (holder is LinkViewHolder) {
            val holder1 = holder
            holder1.tv_name?.text =
                "${feedlist.get(position)?.userFirstName} ${feedlist.get(position)?.userLastName}"
            var postDate: String = ""
            try {
                postDate =
                    MyUtils.getDisplayableTime(feedlist.get(position)?.postCreatedMinutesAgo!!.toLong())
            } catch (e: Exception) {
            }

            holder1.ll_translate?.setOnClickListener {
                getTranslate(
                    holder1.ll_translate,
                    holder1.transalteProgressview,
                    holder1.tv_details_translate,
                    holder1.tv_see_transalation,
                    feedlist.get(position)?.postDescription,
                    userData?.userContentLanguageCode,
                    feedlist.get(position)?.postLanguage
                )
            }
            holder1.tv_see_transalation?.setOnClickListener {
                holder1.ll_translate?.performClick()
            }
            holder1.homePhotoVideoTime?.text = "${feedlist.get(position)?.postMediaType}${postDate}"
            holder1.tv_like_count?.text = feedlist.get(position)?.postLike
            holder1.tv_chat_count?.text = feedlist.get(position)?.postComment
            holder1.tv_views_count?.text = feedlist.get(position)?.postViews
            holder1.userImage?.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
            holder1.feed_userImg?.setImageURI(
                RestClient.image_base_url_users + feedlist.get(
                    position
                )?.userProfilePicture
            )
            if (isExpandPosition == position) {

                holder1.ll_comment_list?.visibility = View.VISIBLE

            } else {

                holder1.ll_comment_list?.visibility = View.GONE
            }
            if (!feedlist.get(position)?.postCommentList.isNullOrEmpty()) {
                if (feedlist.get(position)?.postCommentList!!.size >= 2) {

                    holder1.tvCommenViewAll?.visibility = View.VISIBLE
                } else {

                    holder1.tvCommenViewAll?.visibility = View.GONE
                }
            } else {
                holder1.tvCommenViewAll?.visibility = View.GONE
            }
            holder1.commentListAdapter = CommentAdapter(
                context!!, feedlist.get(position)?.postCommentList!!,
                object : CommentAdapter.OnItemClick {
                    override fun onClicklisneter(
                        pos: Int,
                        actionType: Int,
                        comentObj: CommentData,
                        v1: View,
                        commentId: String,
                        comment: String,
                        replyPos: Int
                    ) {
                        var postCommentListFragment = PostCommentListFragment()
                        Bundle().apply {
                            putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                            putString(
                                "postUserID",
                                feedlist.get(holder.getAdapterPosition())!!.userID
                            )
                            putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                            postCommentListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            postCommentListFragment,
                            postCommentListFragment::class.simpleName!!,
                            true
                        )

                    }

                    override fun onClickListner(position: Int, from: String, data: CommentData) {
                        var postCommentListFragment = PostCommentListFragment()
                        Bundle().apply {
                            putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                            putString(
                                "postUserID",
                                feedlist.get(holder.getAdapterPosition())!!.userID
                            )
                            putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                            postCommentListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            postCommentListFragment,
                            postCommentListFragment::class.simpleName!!,
                            true
                        )


                    }

                }, ""
            )
            holder1.recyclerview_comment?.adapter = holder1.commentListAdapter
            try {
                val request = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(
                        RestClient.image_base_url_posts + feedlist.get(position)?.postSerializedData?.get(
                            0
                        )?.albummedia?.get(0)?.albummediaFile
                    )
                )
                    .setPostprocessor(holder1.postprocessor)
                    .build()
                val controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder1.blurImage?.controller)
                    .build() as PipelineDraweeController
                holder1.blurImage?.controller = controller
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder1.trendingItemPhotoAdapter?.setActivity(context, position)
            holder1.trendingItemPhotoAdapter?.setData(feedlist[position]!!, position)
            holder1.trendingItemPhotoAdapter?.setClickListener(object :
                TrendingItemLinkAdapter.ClickInterface {
                override fun openPhotoDetails(pos: Int) {
                    /*if (holder.getAdapterPosition() > -1)
                        setPostViewsCount(holder.getAdapterPosition())
                    if (feedlist[holder.getAdapterPosition()]!!.postSerializedData[0].albummedia.size == 1) {
                        val i = Bundle()
                        i.putInt("pos", pos)
                        i.putString("postId", feedlist[holder.getAdapterPosition()]!!.postID)
                        i.putSerializable("feed", feedlist[holder.getAdapterPosition()])
                        i.putString("viewcounter", feedlist[holder.getAdapterPosition()]!!.postViews)
                        i.putString("action", "singlepohto")
                        (mContext as HomeActivity).setfrgment(PhotoGalleryFragment(), i)
                    } else {
                        val i = Bundle()
                        i.putInt("pos", pos)
                        i.putString("postId", feedlist[holder.getAdapterPosition()]!!.postID)
                        i.putString("fileName", feedlist[holder.getAdapterPosition()]!!.postSerializedData[0].albummedia[pos].albummediaFile)
                        i.putSerializable("feed", feedlist[holder.getAdapterPosition()])
                        i.putString("viewcounter", feedlist[holder.getAdapterPosition()]!!.postViews)
                        (mContext as HomeActivity).setfrgment(PhotoDetailsFragment(), i)
                    }*/
                }
            })
            if (feedlist.get(position)?.originaluserID.equals("0")) {
                holder1.ll_share?.visibility = View.GONE

                var des = if (!feedlist.get(position)?.postDescription.isNullOrEmpty()) {
                    feedlist.get(position)?.postDescription?.split("||")?.toTypedArray()
                } else {
                    null
                }
                val postdescription: String = if (!des.isNullOrEmpty()) {
                    Constant.decode(des!![0])
                } else {
                    ""
                }
                if (!postdescription.isNullOrEmpty()) {
                    if (userData?.userContentLanguageCode.equals(feedlist.get(position)?.postLanguage)) {
                        holder1.tv_see_transalation?.visibility = View.GONE
                    } else if (userData?.userContentLanguageCode.isNullOrEmpty()) {
                        holder1.tv_see_transalation?.visibility = View.GONE
                    } else {
                        holder1.tv_see_transalation?.visibility = View.VISIBLE
                    }
                } else {
                    holder1.tv_see_transalation?.visibility = View.GONE
                }
                setPostDescription(holder1.tv_details!!, holder1.adapterPosition, postdescription)
                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location?.visibility = View.GONE
                    holder1.tvAddLocation?.visibility = View.GONE
                }
                holder1.ll_add_location_share?.visibility = View.GONE
                holder1.tvAddLocation_share?.visibility = View.GONE
            } else {
                holder1.ll_share?.visibility = View.VISIBLE
                holder1.feed_userImg_share?.setImageURI(
                    RestClient.image_base_url_users + feedlist.get(
                        position
                    )?.originalUserProfilePicture
                )
                holder1.tv_name_share?.text =
                    "${feedlist.get(position)?.originalUserFirstName} ${feedlist.get(position)?.originalUserLastName}"
                var postDateOriginal: String = ""
                try {
                    postDateOriginal =
                        MyUtils.getDisplayableTime(feedlist.get(position)?.orgPostPostCreatedMinutesAgo!!.toLong())
                } catch (e: Exception) {
                }
                holder1.homePhotoVideoTime_share?.text =
                    "${feedlist.get(position)?.postMediaType}${postDateOriginal}"

                var des = if (!feedlist.get(position)?.postDescription.isNullOrEmpty()) {
                    feedlist.get(position)?.postDescription?.split("||")?.toTypedArray()
                } else {
                    null
                }
                val postdescription: String = if (!des.isNullOrEmpty()) {
                    Constant.decode(des!![0])
                } else {
                    ""
                }

                var desWrite = if (!feedlist.get(position)?.postDescription.isNullOrEmpty()) {
                    feedlist.get(position)?.postWriteSomething?.split("||")?.toTypedArray()
                } else {
                    null
                }
                val postdescriptionWrite: String = if (!desWrite.isNullOrEmpty()) {
                    Constant.decode(desWrite!![0])
                } else {
                    ""
                }

                if (!postdescriptionWrite.isNullOrEmpty()) {
                    if (userData?.userContentLanguageCode.equals(feedlist.get(position)?.postLanguage)) {
                        holder1.tv_see_transalation?.visibility = View.GONE
                    } else if (userData?.userContentLanguageCode.isNullOrEmpty()) {
                        holder1.tv_see_transalation?.visibility = View.GONE
                    } else {
                        holder1.tv_see_transalation?.visibility = View.VISIBLE
                    }
                } else {
                    holder1.tv_see_transalation?.visibility = View.GONE
                }

                setPostDescription(
                    holder1.tv_details_share!!,
                    holder1.adapterPosition,
                    postdescriptionWrite
                )
                setPostDescriptionOriginal(
                    holder1.tv_details!!,
                    holder1.adapterPosition,
                    postdescription
                )

                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location_share?.visibility = View.GONE
                    holder1.tvAddLocation_share?.visibility = View.GONE
                }
                holder1.ll_add_location?.visibility = View.GONE
                holder1.tvAddLocation?.visibility = View.GONE
            }
            setFeedData1(
                holder1.tv_like_count!!,
                holder1.tv_chat_count!!,
                holder1.tv_views_count!!,
                holder1.img_like!!,
                holder1.btn_views!!,
                holder1.btn_chat!!,
                holder1.btn_share!!,
                holder1.btnLike!!,
                holder1.adapterPosition
            )

            holder1.btnLike?.setOnClickListener {
                if (holder1.getAdapterPosition() > -1) {
                    feedLike1(
                        holder.img_like!!,
                        holder.btnLike!!,
                        feedlist.get(holder.getAdapterPosition())!!,
                        holder.getAdapterPosition(),
                        holder1.tv_like_count!!
                    );
                }
            }

            holder1.ll_view?.setOnClickListener {
                var postViewLikeListFragment = PostViewLikeListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("from", "View")
                    postViewLikeListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postViewLikeListFragment,
                    postViewLikeListFragment::class.simpleName!!,
                    true
                )
            }
            holder1.ll_like?.setOnClickListener {
                var postViewLikeListFragment = PostViewLikeListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("from", "Like")
                    postViewLikeListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postViewLikeListFragment,
                    postViewLikeListFragment::class.simpleName!!,
                    true
                )

            }
            holder1.ll_more?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "from_more_icon",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.layout_share?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "list_share",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.ll_comment?.setOnClickListener {
                if (isExpandPosition == holder.adapterPosition) {
                    isExpandPosition = -1
                } else {
                    isExpandPosition = holder.adapterPosition
                    /*if (onItemClick != null)
                        onItemClick!!.onClicklisneter(holder.adapterPosition)*/
                }
                notifyDataSetChanged()
            }
            holder1.emoji?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "sendComment",
                    feedlist.get(holder.adapterPosition)!!,
                    holder1.edit_post_comment?.text.toString().trim(),
                    holder1.edit_post_comment as View
                )

            }
            holder1.ll_mainUserDetail?.setOnClickListener {
                onItemClick.onClicled(
                    holder1.adapterPosition,
                    "OtherUserProfile",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.ll_userName?.setOnClickListener {
                onItemClick.onClicled(
                    holder1.adapterPosition,
                    "other_user_profile",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.tvCommenViewAll?.setOnClickListener {
                var postCommentListFragment = PostCommentListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("postUserID", feedlist.get(holder.getAdapterPosition())!!.userID)
                    putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                    postCommentListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postCommentListFragment,
                    postCommentListFragment::class.simpleName!!,
                    true
                )
            }
        }
        else if (holder is DocumentViewHolder) {
            val holder1 = holder
            holder1.tv_name?.text =
                "${feedlist.get(position)?.userFirstName} ${feedlist.get(position)?.userLastName}"
            var postDate: String = ""
            try {
                postDate =
                    MyUtils.getDisplayableTime(feedlist.get(position)?.postCreatedMinutesAgo!!.toLong())
            } catch (e: Exception) {
            }

            if (!feedlist.get(position)?.postDescription.isNullOrEmpty()) {
                if (userData?.userContentLanguageCode.equals(feedlist.get(position)?.postLanguage)) {
                    holder1.tv_see_transalation?.visibility = View.GONE
                } else if (userData?.userContentLanguageCode.isNullOrEmpty()) {
                    holder1.tv_see_transalation?.visibility = View.GONE
                } else {

                    holder1.tv_see_transalation?.visibility = View.VISIBLE
                }
            }
            holder1.ll_translate?.setOnClickListener {
                getTranslate(
                    holder1.ll_translate,
                    holder1.transalteProgressview,
                    holder1.tv_details_translate,
                    holder1.tv_see_transalation,
                    feedlist.get(position)?.postDescription,
                    userData?.userContentLanguageCode,
                    feedlist.get(position)?.postLanguage
                )
            }
            holder1.tv_see_transalation?.setOnClickListener {
                holder1.ll_translate?.performClick()
            }

            holder1.homePhotoVideoTime?.text = "${feedlist.get(position)?.postMediaType}${postDate}"
            holder1.tv_like_count?.text = feedlist.get(position)?.postLike
            holder1.tv_chat_count?.text = feedlist.get(position)?.postComment
            holder1.tv_views_count?.text = feedlist.get(position)?.postViews
            holder1.userImage?.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
            holder1.feed_userImg?.setImageURI(
                RestClient.image_base_url_users + feedlist.get(
                    position
                )?.userProfilePicture
            )
            if (isExpandPosition == position) {

                holder1.ll_comment_list?.visibility = View.VISIBLE

            } else {

                holder1.ll_comment_list?.visibility = View.GONE
            }
            if (!feedlist.get(position)?.postCommentList.isNullOrEmpty()) {
                if (feedlist.get(position)?.postCommentList!!.size >= 2) {

                    holder1.tvCommenViewAll?.visibility = View.VISIBLE
                } else {

                    holder1.tvCommenViewAll?.visibility = View.GONE
                }
            } else {
                holder1.tvCommenViewAll?.visibility = View.GONE
            }
            holder1.commentListAdapter = CommentAdapter(
                context!!, feedlist.get(position)?.postCommentList!!,
                object : CommentAdapter.OnItemClick {
                    override fun onClicklisneter(
                        pos: Int,
                        actionType: Int,
                        comentObj: CommentData,
                        v1: View,
                        commentId: String,
                        comment: String,
                        replyPos: Int
                    ) {
                        var postCommentListFragment = PostCommentListFragment()
                        Bundle().apply {
                            putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                            putString(
                                "postUserID",
                                feedlist.get(holder.getAdapterPosition())!!.userID
                            )
                            putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                            postCommentListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            postCommentListFragment,
                            postCommentListFragment::class.simpleName!!,
                            true
                        )

                    }

                    override fun onClickListner(position: Int, from: String, data: CommentData) {
                        var postCommentListFragment = PostCommentListFragment()
                        Bundle().apply {
                            putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                            putString(
                                "postUserID",
                                feedlist.get(holder.getAdapterPosition())!!.userID
                            )
                            putSerializable("feedData", feedlist.get(holder.getAdapterPosition())!!)
                            postCommentListFragment.arguments = this
                        }
                        (context as MainActivity).navigateTo(
                            postCommentListFragment,
                            postCommentListFragment::class.simpleName!!,
                            true
                        )


                    }

                }, ""
            )
            holder1.recyclerview_comment?.adapter = holder1.commentListAdapter
            try {
                val request = ImageRequestBuilder.newBuilderWithSource(
                    Uri.parse(
                        RestClient.image_base_url_posts + feedlist.get(position)?.postSerializedData?.get(
                            0
                        )?.albummedia?.get(0)?.albummediaFile
                    )
                )
                    .setPostprocessor(holder1.postprocessor)
                    .build()
                val controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(holder1.blurImage?.controller)
                    .build() as PipelineDraweeController
                holder1.blurImage?.controller = controller
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder1.trendingItemPhotoAdapter?.setActivity(context, position)
            holder1.trendingItemPhotoAdapter?.setData(feedlist[position]!!, position)
            holder1.trendingItemPhotoAdapter?.setClickListener(object :
                TrendingItemDocumentAdapter.ClickInterface {
                override fun openPhotoDetails(pos: Int) {
                    Intent(context, DocumentActivity::class.java).apply {
                        putExtra(
                            "file",
                            RestClient.image_base_url_posts + feedlist.get(position)?.postSerializedData?.get(
                                0
                            )?.albummedia?.get(0)?.albummediaFile
                        )
                        context.startActivity(this)
                    }
//                    try{
//                        var intent =  Intent(Intent.ACTION_VIEW);
//                        if (feedlist.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.toString().contains(".doc") || feedlist.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.toString()?.contains(".docx")!!) {
//                            // Word document
//                            intent.setDataAndType(uri, "application/msword");
//                        } else if (feedlist.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.toString()!!.contains(".pdf")) {
//                            // PDF file
//                            intent.setDataAndType(uri, "application/pdf");
//                        } else if (feedlist.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.toString()!!.contains(".ppt") || feedlist.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.toString()!!.contains(".pptx")) {
//                            // Powerpoint file
//                            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
//                        } else if (feedlist.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.toString()!!.contains(".xls") || feedlist.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.toString()?.contains(".xlsx")!!) {
//                            // Excel file
//                            intent.setDataAndType(uri, "application/vnd.ms-excel");
//                        } else if (feedlist.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.toString()?.contains(".txt")!!) {
//                            // Text file
//                            intent.setDataAndType(uri, "text/plain");
//                        } else {
//                            intent.setDataAndType(uri, "*/*");
//                        }
//
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        context.startActivity(intent);
//                    } catch (e: ActivityNotFoundException) {
//                        Toast.makeText(context, "No application found which can open the file", Toast.LENGTH_SHORT).show();
//
//                    }
                }
            })
            if (feedlist.get(position)?.originaluserID.equals("0")) {
                holder1.ll_share?.visibility = View.GONE
                setPostDescription(
                    holder1.tv_details!!,
                    holder1.adapterPosition,
                    if (feedlist.get(position)?.postDescription.isNullOrEmpty()) "" else Constant.decode(
                        feedlist.get(position)?.postDescription
                    )
                )

                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.visibility = View.VISIBLE
                    holder1.tvAddLocation?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location?.visibility = View.GONE
                    holder1.tvAddLocation?.visibility = View.GONE
                }
                holder1.ll_add_location_share?.visibility = View.GONE
                holder1.tvAddLocation_share?.visibility = View.GONE
            } else {
                holder1.ll_share?.visibility = View.VISIBLE
                holder1.feed_userImg_share?.setImageURI(
                    RestClient.image_base_url_users + feedlist.get(
                        position
                    )?.originalUserProfilePicture
                )
                holder1.tv_name_share?.text =
                    "${feedlist.get(position)?.originalUserFirstName} ${feedlist.get(position)?.originalUserLastName}"
                var postDateOriginal: String = ""
                try {
                    postDateOriginal =
                        MyUtils.getDisplayableTime(feedlist.get(position)?.orgPostPostCreatedMinutesAgo!!.toLong())
                } catch (e: Exception) {
                }
                holder1.homePhotoVideoTime_share?.text =
                    "${feedlist.get(position)?.postMediaType}${postDateOriginal}"

                setPostDescription(
                    holder1.tv_details_share!!,
                    holder1.adapterPosition,
                    if (feedlist.get(position)?.postWriteSomething.isNullOrEmpty()) "" else Constant.decode(
                        feedlist.get(position)?.postWriteSomething
                    )
                )
                setPostDescriptionOriginal(
                    holder1.tv_details!!,
                    holder1.adapterPosition,
                    if (feedlist.get(position)?.postDescription.isNullOrEmpty()) "" else Constant.decode(
                        feedlist.get(position)?.postDescription
                    )
                )

                if (!feedlist.get(position)!!.postLocation.isNullOrEmpty()) {
                    holder1.ll_add_location_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.visibility = View.VISIBLE
                    holder1.tvAddLocation_share?.text = feedlist.get(position)!!.postLocation
                } else {
                    holder1.ll_add_location_share?.visibility = View.GONE
                    holder1.tvAddLocation_share?.visibility = View.GONE
                }
                holder1.ll_add_location?.visibility = View.GONE
                holder1.tvAddLocation?.visibility = View.GONE
            }
            setFeedData1(
                holder1.tv_like_count!!,
                holder1.tv_chat_count!!,
                holder1.tv_views_count!!,
                holder1.img_like!!,
                holder1.btn_views!!,
                holder1.btn_chat!!,
                holder1.btn_share!!,
                holder1.btnLike!!,
                holder1.adapterPosition
            )

            holder1.btnLike?.setOnClickListener {
                if (holder1.getAdapterPosition() > -1) {
                    feedLike1(
                        holder.img_like!!,
                        holder.btnLike!!,
                        feedlist.get(holder.getAdapterPosition())!!,
                        holder.getAdapterPosition(),
                        holder1.tv_like_count!!
                    );
                }
            }

            holder1.ll_view?.setOnClickListener {
                var postViewLikeListFragment = PostViewLikeListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("from", "View")
                    postViewLikeListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postViewLikeListFragment,
                    postViewLikeListFragment::class.simpleName!!,
                    true
                )
            }
            holder1.ll_like?.setOnClickListener {
                var postViewLikeListFragment = PostViewLikeListFragment()
                Bundle().apply {
                    putString("postId", feedlist.get(holder.getAdapterPosition())!!.postID)
                    putString("from", "Like")
                    postViewLikeListFragment.arguments = this
                }
                (context as MainActivity).navigateTo(
                    postViewLikeListFragment,
                    postViewLikeListFragment::class.simpleName!!,
                    true
                )

            }
            holder1.ll_more?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "from_more_icon",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.layout_share?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "list_share",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.ll_comment?.setOnClickListener {
                if (isExpandPosition == holder.adapterPosition) {
                    isExpandPosition = -1
                } else {
                    isExpandPosition = holder.adapterPosition
                    /*if (onItemClick != null)
                        onItemClick!!.onClicklisneter(holder.adapterPosition)*/
                }
                notifyDataSetChanged()
            }
            holder1.emoji?.setOnClickListener {
                onItemClick.onClicled(
                    holder1?.adapterPosition,
                    "sendComment",
                    feedlist.get(holder.adapterPosition)!!,
                    holder1.edit_post_comment?.text.toString().trim(),
                    holder1.edit_post_comment as View
                )

            }
            holder1.ll_mainUserDetail?.setOnClickListener {
                onItemClick.onClicled(
                    holder1.adapterPosition,
                    "OtherUserProfile",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
            holder1.ll_userName?.setOnClickListener {
                onItemClick.onClicled(
                    holder1.adapterPosition,
                    "other_user_profile",
                    feedlist.get(holder.adapterPosition)!!,
                    "",
                    holder1.edit_post_comment as View
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return feedlist.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (feedlist.get(position) == null)
            Model.Loder_TYPE else getTypeView(position)
    }

    fun getTypeView(position: Int): Int {
        var r = -1
        if (feedlist.get(position) != null) {
            val type: String = feedlist.get(position)?.postType!!
            when (type) {
                "Social" -> {
                    if (feedlist.get(position)?.postMediaType.equals("Photo", false)) {
                        r = Model.IMAGE_TYPE
                    } else if (feedlist.get(position)?.postMediaType.equals("Video", false)) {
                        r = Model.Video_TYPE
                    } else if (feedlist.get(position)?.postMediaType.equals("Link", false)) {
                        r = Model.Link_TYPE
                    } else if (feedlist.get(position)?.postMediaType.equals("Text", false)) {
                        r = Model.TEXT_TYPE
                    } else if (feedlist.get(position)?.postMediaType.equals("Place", false)) {
                        r = Model.CheckIn_TYPE
                    } else if (feedlist.get(position)?.postMediaType.equals("Document", false)) {
                        r = Model.Document_TYPE
                    }
                }
                /*  "Social" -> {

                  }*/
                "FriendSuggestion" -> r = Model.Friend_Suggestion_TYPE
                "BusinessSuggestion" -> r = Model.Business_Suggestion_TYPE
//                "Otheruserprofile" -> r = Model.other_user_profile
                "MyProfile" -> r = Model.MyProfile_TYPE
                "Loder" -> r = Model.Loder_TYPE
            }
        } else {
            r = Model.Loder_TYPE
        }
        return r
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    private fun getTranslate(
        llTranslate: LinearLayout,
        transalteProgressview: ContentLoadingProgressBar?,
        tvDetailsTranslate: PostDesTextView?,
        tvSeeTransalation: PostDesTextView?,
        postDescription: String?,
        userContentLanguageCode: String?,
        postLanguage: String?
    ) {
        transalteProgressview?.visibility = View.VISIBLE
        val client = OkHttpClient()

        val mediaType = MediaType.parse("application/x-www-form-urlencoded")
        val body = RequestBody.create(
            mediaType,
            "q=${postDescription}&source=${postLanguage}&target=${userContentLanguageCode}"
        )
        val request = Request.Builder()
            .url("https://google-translate1.p.rapidapi.com/language/translate/v2")
            .post(body)
            .addHeader("content-type", "application/x-www-form-urlencoded")
            .addHeader("accept-encoding", "application/gzip")
            .addHeader("x-rapidapi-key", "e7d2e9034amshfea646ee7ac91e3p149891jsneb840f2518fa")
            .addHeader("x-rapidapi-host", "google-translate1.p.rapidapi.com")
            .build()



        doAsync {

            var response = client.newCall(request).execute()

            uiThread {
                transalteProgressview?.visibility = View.GONE
                if (response.isSuccessful) {
                    if (response.code() == 200) {
                        val object1 = JSONObject(response.body().string())
                        Log.e("data", "" + object1.getString("data"))
                    } else {
                        ErrorUtil.errorMethod(llTranslate)
                    }
                } else {
                    ErrorUtil.errorMethod(llTranslate)
                }

            }
        }


        /*var request = ServiceBuilder.buildService(RestApi::class.java)

            val call = request.getTranslate(body,"application/x-www-form-urlencoded","application/gzip","b266e424b1mshb7761796981bc8ep1d6cf9jsn3be623878d53","google-translate1.p.rapidapi.com")

            call.enqueue(object : retrofit2.Callback<GoogleTranslateObject> {

                override fun onResponse(call: retrofit2.Call<GoogleTranslateObject>, response: retrofit2.Response<GoogleTranslateObject>) {
                    transalteProgressview?.visibility=View.GONE
                    if (response.isSuccessful){
                        Log.e("translate",""+response.body()?.data?.translations!![0].translatedText)
                    }
                }
                override fun onFailure(call: retrofit2.Call<GoogleTranslateObject>, t: Throwable) {
                    ErrorUtil.errorMethod(llTranslate)

                }
            })*/

    }

    interface OnItemClick {
        fun onClicled(
            position: Int,
            from: String,
            data: CreatePostData,
            sendComment: String,
            view: View
        )

    }

    fun setPostDescription(
        postdescriptionTextview: PostDesTextView,
        position: Int,
        postDescription: String?
    ) {
        if (postDescription!!.isNotEmpty()) {
            postdescriptionTextview?.visibility = View.VISIBLE
            val postdescription: String = postDescription
            postdescriptionTextview.displayFulltext(postdescription)
            postdescriptionTextview.tag = position
            postdescriptionTextview.doResizeTextView(
                postdescriptionTextview,
                3,
                "...See More",
                true
            )

            postdescriptionTextview.setCustomEventListener(object : OnCustomEventListener {
                override fun onViewMore(viewMore: Boolean) {
                }

                override fun onFriendTagClick(friendsId: String?) {
                    // openProfilefregment(friendsId)
                }
            })
            postdescriptionTextview.setHashClickListener(object :
                PostDesTextView.OnHashEventListener {
                override fun onHashTagClick(friendsId: String?) {
                    var hashTagPostListFragment = HashTagPostListFragment()
                    Bundle().apply {
                        putString("hashTag", friendsId)
                        putString("postType",feedlist[position]!!.postType)

                        hashTagPostListFragment.arguments = this
                    }
                    (context as MainActivity).navigateTo(
                        hashTagPostListFragment,
                        hashTagPostListFragment::class.java.name,
                        true
                    )
                }

            })

        } else {
            postdescriptionTextview?.visibility = View.GONE
        }
    }

    fun setPostDescriptionOriginal(
        postdescriptionTextview: PostDesTextView,
        position: Int,
        postWriteSomething: String?
    ) {
        if (postWriteSomething!!.isNotEmpty()) {
            postdescriptionTextview?.visibility = View.VISIBLE
            val postdescription: String = postWriteSomething

            postdescriptionTextview.displayFulltext(postdescription)
            postdescriptionTextview.tag = position
            postdescriptionTextview.doResizeTextView(
                postdescriptionTextview,
                3,
                "...See More",
                true
            )

            postdescriptionTextview.setCustomEventListener(object : OnCustomEventListener {
                override fun onViewMore(viewMore: Boolean) {

                }

                override fun onFriendTagClick(friendsId: String?) {

                }
            })
            postdescriptionTextview.setHashClickListener(object :
                PostDesTextView.OnHashEventListener {
                override fun onHashTagClick(friendsId: String?) {
                    var hashTagPostListFragment = HashTagPostListFragment()
                    Bundle().apply {
                        putString("hashTag", friendsId)
                        putString("postType",feedlist[position]!!.postType)
                        hashTagPostListFragment.arguments = this
                    }
                    (context as MainActivity).navigateTo(
                        hashTagPostListFragment,
                        hashTagPostListFragment::class.java.name,
                        true
                    )
                }

            })
        } else {
            postdescriptionTextview?.visibility = View.GONE
        }
    }

    fun playAvailableVideos(newState: Int, recyclerView: RecyclerView?) {


        if (newState == 0 && recyclerView != null && recyclerView.layoutManager != null) {
            val firstVisiblePosition =
                (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            val lastVisiblePosition =
                (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()


            val rvRect = Rect()
            recyclerView.getGlobalVisibleRect(rvRect)
            var isAutoPlay = true
            /*if (settingsManager.get_DefaultSettings() != null && settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata() != null && settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata().size() > 0 && settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata().get(
                    0
                ).getUserMediaSettings() != null
            ) {

                if (settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata().get(0).getUserMediaSettings().equalsIgnoreCase(
                        mContext.getString(R.string.neverautoplayvideos)
                    )
                )
                    isAutoPlay = false
                else if (settingsManager.get_DefaultSettings().getGeneralaccountsettingsdata().get(0).getUserMediaSettings().equalsIgnoreCase(
                        mContext.getString(R.string.onwifionly)
                    ) && !Constant.checkNetworkConnectionType("WIFI", mContext)
                ) {

                    isAutoPlay = false


                }


            }*/

            if (firstVisiblePosition >= 0 && newState == 0 && isAutoPlay) {

                var isVideoView = false
                for (i in firstVisiblePosition until lastVisiblePosition) {


                    val holder = recyclerView.findViewHolderForAdapterPosition(i)
                    try {
                        if (i >= 0 && holder != null && holder is VideoViewHolder && feedlist?.size!! > 0) {

                            val rowRect = Rect()
                            recyclerView.layoutManager?.findViewByPosition(i)
                                ?.getGlobalVisibleRect(rowRect)

                            var percentFirst: Int
                            if (rowRect.bottom >= rvRect.bottom) {
                                val visibleHeightFirst = rvRect.bottom - rowRect.top
                                percentFirst =
                                    visibleHeightFirst * 100 / recyclerView.layoutManager?.findViewByPosition(
                                        i
                                    )?.height!!
                            } else {
                                val visibleHeightFirst = rowRect.bottom - rvRect.top
                                percentFirst =
                                    visibleHeightFirst * 100 / recyclerView.layoutManager?.findViewByPosition(
                                        i
                                    )?.height!!
                            }

                            if (percentFirst > 100)
                                percentFirst = 100

                            if (percentFirst >= 50) {

                                isVideoView = true
                                pausePlayer(i)
                                if (!feedlist[i]!!.postSerializedData[0].albummedia[0].isPlaying) {
                                    videoPlayPosition = i
                                    pausePlayer();
                                    feedlist[i]!!.postSerializedData[0].albummedia[0].isPlaying =
                                        true


                                    val cvh = holder as VideoViewHolder?
                                    val viewHolder1 =
                                        cvh?.recycle_view_feedhome?.findViewHolderForAdapterPosition(
                                            0
                                        )!!

                                    if (viewHolder1 is TrendingItemVideoAdapter.ImgHolder) {
                                        val holder1 =
                                            viewHolder1 as TrendingItemVideoAdapter.ImgHolder
                                        playVideo(
                                            i,
                                            holder1.galleryImageView!!,
                                            holder1.thumnail!!,
                                            holder1.thumnail!!/*holder1.volume!!*/,
                                            holder1.pb!!,
                                            holder1.playicon!!,
                                            feedlist
                                        )
                                    }

                                }

                                break

                            } else {
                                if (feedlist[i]!!.postSerializedData[0].albummedia[0].isPlaying) {
                                    feedlist[i]!!.postSerializedData[0].albummedia[0].isPlaying =
                                        (false);
                                    videoPlayPosition = i
                                    pausePlayer()
                                }


                            }


                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
                if (!isVideoView)
                    pausePlayer()
            }
        }
    }

    fun playVideo(
        videoPlayPosition: Int,
        playerView: PlayerView,
        thumnail: ImageView,
        volume: View?,
        progressBar: ProgressBar,
        playIcon: ImageView,
        feedList: ArrayList<CreatePostData?>?
    ) {

        context.runOnUiThread {
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.post(object : Runnable {
                override fun run() {
                    var videoURI: Uri? = null

                    videoURI = Uri.parse(
                        RestClient.image_base_url_posts + feedlist?.get(videoPlayPosition)!!.postSerializedData.get(
                            0
                        ).albummedia[0].albummediaFile
                    )
                    val trackSelectionFactory: TrackSelection.Factory
                    trackSelectionFactory = AdaptiveTrackSelection.Factory()

                    var trackSelector = DefaultTrackSelector(trackSelectionFactory)

                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)

                    val cacheDataSourceFactory = CacheDataSourceFactory1(context, 5 * 1024 * 1024)

                    val mediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
                        .createMediaSource(videoURI)

                    stopPlayer()
                    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector)
                    playerView.setShutterBackgroundColor(Color.TRANSPARENT)
                    playerView.player = exoPlayer

                    if (volume != null) {
                        /* if (!isMuteing()) {

                                         if (volume is ImageButton)
                                             volume.background = context.resources
                                                 .getDrawable(R.drawable.ic_volume_up_black_24dp)
                                         else
                                             (volume as ImageView).setImageDrawable(
                                                 context.resources.getDrawable(
                                                     R.drawable.ic_volume_up_black_24dp
                                                 )
                                             )

                                     } else {
                                         if (volume is ImageButton)
                                             volume.background = context.resources
                                                 .getDrawable(R.drawable.ic_volume_off_black_24dp)
                                         else
                                             (volume as ImageView).setImageDrawable(
                                                 context.resources.getDrawable(
                                                     R.drawable.ic_volume_off_black_24dp
                                                 )
                                             )
                                     }*/
                    }

                    exoPlayer?.prepare(mediaSource)
                    exoPlayer?.repeatMode = ExoPlayer.REPEAT_MODE_OFF
                    muteVideo(isMuteing())

                    (exoPlayer as SimpleExoPlayer).seekTo(
                        feedlist?.get(videoPlayPosition)?.postSerializedData?.get(0)!!.albummedia.get(
                            0
                        ).duration
                    )

                    exoPlayer?.playWhenReady = true

                    progressBar.visibility = View.VISIBLE

                    if (playIcon.visibility == View.VISIBLE)
                        playIcon.visibility = View.GONE


                    //  exoPlayer.seekTo(selectVideoList.get(pos).getSeekTo());
                    exoPlayer?.addListener(object : Player.EventListener {

                        override fun onLoadingChanged(isLoading: Boolean) {

                        }

                        override fun onPlayerStateChanged(
                            playWhenReady: Boolean,
                            playbackState: Int
                        ) {

                            when (playbackState) {
                                Player.STATE_BUFFERING ->
                                    progressBar.visibility = View.VISIBLE


                                Player.STATE_ENDED -> if (exoPlayer != null) {
                                    exoPlayer?.seekTo(0)
                                    thumnail.visibility = View.VISIBLE
                                    progressBar.visibility = View.GONE
                                    playIcon.visibility = View.VISIBLE
                                    pausePlayer()

                                }
                                Player.STATE_IDLE -> {
                                }

                                Player.STATE_READY -> {
                                    progressBar.visibility = View.GONE
                                    thumnail.visibility = View.GONE
                                    progressBar.visibility = View.GONE
                                    if (playIcon.visibility == View.VISIBLE)
                                        playIcon.visibility = View.GONE
                                }
                                else -> {
                                }
                            }
                        }

                        override fun onRepeatModeChanged(repeatMode: Int) {

                        }

                        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

                        }

                        override fun onPositionDiscontinuity(reason: Int) {

                        }

                        override fun onSeekProcessed() {

                        }
                    })


                }

            })
        }


    }

    fun stopPlayer() {
        if (exoPlayer != null) {
            exoPlayer?.stop(true)
            exoPlayer?.release()
            exoPlayer = null
        }


    }

    fun pausePlayer() {
        try {
            context.runOnUiThread(Runnable {

                videoPlayPosition = -1
                for (i in feedlist!!.indices) {
                    if (feedlist.get(i) != null && !feedlist.get(i)?.postSerializedData.isNullOrEmpty() && !feedlist.get(
                            i
                        )?.postSerializedData?.get(0)?.albummedia.isNullOrEmpty()
                    )
                        if (feedlist[i]!!.postSerializedData[0].albummedia[0].isPlaying) {
                            videoPlayPosition = i
                            break
                        }
                }

                if (videoPlayPosition > -1) {
                    if (exoPlayer != null)
                        feedlist[videoPlayPosition]!!.postSerializedData[0].albummedia[0].duration =
                            (exoPlayer?.currentPosition!!)
                    feedlist[videoPlayPosition]!!.postSerializedData[0].albummedia[0].isPlaying =
                        false
                    notifyItemChanged(videoPlayPosition)
                    videoPlayPosition = -1

                }
                if (exoPlayer != null) {
                    exoPlayer?.stop(true)
                    exoPlayer?.release()
                    exoPlayer = null
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            stopPlayer()
        }

    }

    fun pausePlayer(positionNot: Int) {


        try {
            context.runOnUiThread(Runnable {
                Handler().post {
                    videoPlayPosition = -1
                    for (i in feedlist!!.indices) {
                        if (feedlist.get(i) != null && !feedlist.get(i)!!.postSerializedData.isNullOrEmpty() && feedlist.get(
                                i
                            )!!.postSerializedData[0].albummedia != null && feedlist.get(
                                i
                            )!!.postSerializedData[0].albummedia.isNotEmpty()
                        )
                            if (feedlist[i]!!.postSerializedData[0].albummedia[0].isPlaying && positionNot != i) {
                                videoPlayPosition = i
                                break
                            }
                    }

                    if (videoPlayPosition > -1) {
                        if (exoPlayer != null)
                            feedlist.get(videoPlayPosition)!!.postSerializedData[0].albummedia[0].duration =
                                (
                                        exoPlayer?.currentPosition!!
                                        )
                        feedlist.get(videoPlayPosition)!!.postSerializedData[0].albummedia[0].isPlaying =
                            (false)
                        notifyItemChanged(videoPlayPosition)
                        videoPlayPosition = -1


                        if (exoPlayer != null) {
                            exoPlayer?.stop(true)
                            exoPlayer?.release()
                            exoPlayer = null
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            stopPlayer()
        }

    }

    fun isMuteing(): Boolean {
        return MyUtils.isMuteing
    }

    fun setMuteing(muteing: Boolean) {
        MyUtils.isMuteing = muteing
    }

    fun muteVideo(isMute: Boolean) {
        if (exoPlayer != null) {
            if (isMute)
                exoPlayer?.volume = 0f
            else
                exoPlayer?.volume = 1f
            setMuteing(isMute)
        }
    }

    fun setAdapterPosition(firstPos: Int, lastPos: Int) {
        try {
            if (sessionManager != null && sessionManager.isLoggedIn()) {
                userId = loginUserId
                for (i in firstPos..lastPos) {
                    if (feedlist.size > 0 && feedlist[i] != null) {
                        if (feedlist[i]!!.postType.contains(FeedModel.TEXT_TYPE) || feedlist[i]!!.postType.contains(
                                FeedModel.Check_IN
                            )
                        ) {
                            setPostViewsCount(i)
                        }
                        if (feedlist[firstPos]!!.postType.contains(FeedModel.Media_Type2)) {
                            if (feedlist[firstPos]!!.postMediaType.contains(FeedModel.Photo_TYPE) && feedlist[firstPos]!!.postSerializedData.size > 0 && feedlist[firstPos]!!.postSerializedData[0].albummedia.isNotEmpty()) {
                                setPostViewsCount(i)
                            }
                            if (feedlist[firstPos]!!.postMediaType.contains(FeedModel.Video_TYPE) && feedlist[firstPos]!!.postSerializedData.size > 0 && feedlist[firstPos]!!.postSerializedData[0].albummedia.isNotEmpty()) {
                                setPostViewsCount(i)
                            }
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }

    fun setPostViewsCount(feedPos: Int) {
        try {
            if (!feedlist[feedPos]!!.youpostViews.contains("Yes")) {
                feedlist[feedPos]!!.youpostViews = "Yes"
                var count = 0
                try {
                    count = Integer.valueOf(feedlist[feedPos]!!.postViews) + 1
                } catch (e: java.lang.Exception) {
                }
                feedlist[feedPos]!!.postViews = count.toString()
                notifyItemChanged(feedPos)
                val json = "[{" +
                        "  \"postID\": \"" + feedlist[feedPos]!!.postID + "\"," +
                        "  \"loginuserID\": \"" + userId + "\"," +
                        "  \"languageID\": \"" + userData?.languageID + "\"," +
                        "  \"apiType\": \"" + Constant.api_type + "\"," +
                        "  \"apiVersion\": \"" + Constant.version + "\"" +
                        "}]"
                Log.d("System out", "Views$json")
                val postView: PostView = ViewModelProviders.of(context as FragmentActivity).get(
                    PostView::class.java
                )
                postView.postview(context, json)
                    ?.observe(context, object : Observer<List<PostViewPojo?>?> {
                        override fun onChanged(postViewPojos: List<PostViewPojo?>?) {
                            if (postViewPojos!![0]!!.status.equals("true", false)) {
                            } else {
                                if (!feedlist?.isNullOrEmpty()) {
                                    for (i in feedlist.indices) {
                                        if (feedlist[i]!!.postID.equals(
                                                postViewPojos[0]!!.feedDatum?.postID,
                                                ignoreCase = true
                                            )
                                        ) {
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    })
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun setFeedData1(
        userliketextview: TextView,
        usercommenttextview: TextView,
        userviewtextview: TextView,
        userlikeimageview: ImageView,
        userviewpostImageview: ImageView,
        usercommentpostImageview: ImageView,
        shareImageView: ImageView,
        btnLike: ImageView,
        position: Int
    ) {
        if (feedlist[position]!!.youpostShared.equals("Yes", ignoreCase = true)) {
            shareImageView.setImageResource(R.drawable.post_share_icon_selected)
        } else {
            shareImageView.setImageResource(R.drawable.post_share_icon_unselected)
        }
        userliketextview.text = feedlist[position]!!.postLike
        usercommenttextview.text = feedlist[position]!!.postComment
        userviewtextview.text = feedlist[position]!!.postViews
        if (feedlist[position]!!.youpostLiked.equals("Yes", ignoreCase = true)) {
            userlikeimageview.setImageDrawable(context.resources.getDrawable(R.drawable.like_post_icon_selected))
            btnLike.setImageDrawable(context.resources.getDrawable(R.drawable.like_post_icon_selected))
        } else {
            userlikeimageview.setImageDrawable(context.resources.getDrawable(R.drawable.like_post_icon_unselected))
            btnLike.setImageDrawable(context.resources.getDrawable(R.drawable.like_post_icon_unselected))
        }
        /*if (feedlist[position]!!.postComment.toInt() >= 1) {
            usercommentpostImageview.setImageDrawable(context.resources.getDrawable(R.drawable.comment_replies_icon_selected))
        } else {
            usercommentpostImageview.setImageDrawable(context.resources.getDrawable(R.drawable.comment_replies_icon_unselected))
        }*/

        if (feedlist[position]!!.youpostViews.equals("Yes", ignoreCase = true)) {
            userviewpostImageview.setImageDrawable(
                context.getResources().getDrawable(R.drawable.post_view_icon_selected)
            )
        } else {
            userviewpostImageview.setImageDrawable(
                context.getResources().getDrawable(R.drawable.post_view_icon_unselected)
            )
        }
        try {
            //  homeposttimetextview.text = Constant.formatDate(feedlist[position]!!.postCreatedDate, "yyyy-MM-dd hh:mm:ss", "MMM dd 'at' hh:mm aaa")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }


    fun feedLike1(
        likeButtonAnim: ImageView,
        btnLike: ImageView,
        feedDatum: CreatePostData,
        feedPosition: Int,
        likeTextView: TextView
    ) {
        val action: String
        if (feedDatum.youpostLiked.equals("No", false)) {
            feedDatum.youpostLiked = ("Yes")
            var count = 0
            try {
                count = Integer.valueOf(feedDatum.postLike) + 1
            } catch (e: java.lang.Exception) {
            }
            feedDatum.postLike = (count.toString())
            likeButtonAnim.setImageResource(R.drawable.like_post_icon_selected)
            btnLike.setImageResource(R.drawable.like_post_icon_selected)
            likeTextView.text = feedDatum.postLike
            action = "Add"
        } else {
            feedDatum.youpostLiked = ("No")
            var count = 0
            try {
                count = Integer.valueOf(feedDatum.postLike) - 1
            } catch (e: java.lang.Exception) {
            }
            feedDatum.postLike = (count.toString())
            likeButtonAnim.setImageResource(R.drawable.like_post_icon_unselected)
            btnLike.setImageResource(R.drawable.like_post_icon_unselected)
            likeTextView.text = feedDatum.postLike

            action = "Remove"
        }
        val json = Gson().toJson(feedDatum)
        val trendingFeedDatum: CreatePostData = Gson().fromJson(json, CreatePostData::class.java)
        setPostLike(feedDatum.postID, action, feedPosition, trendingFeedDatum, likeButtonAnim)
    }

    fun setPostLike(
        postId: String,
        action: String,
        pos: Int,
        feedDatum: CreatePostData?,
        likeButtonAnim: ImageView?
    ) {
        val json = "[{" +
                "  \"postID\": \"" + postId + "\"," +
                "  \"loginuserID\": \"" + loginUserId + "\"," +
                "  \"action\": \"" + action + "\"," +
                "  \"apiType\": \"" + RestClient.apiType + "\"," +
                "  \"apiVersion\": \"" + RestClient.apiVersion + "\"" +
                "}]"
        Log.d("likeParameter", json)
        val postLike: PostLike =
            ViewModelProviders.of(context as FragmentActivity).get(PostLike::class.java)
        postLike.postLike(context, json, false, postId, action, feedDatum)
            ?.observe(context as FragmentActivity, Observer<List<PostlikePojo>> { postlikePojos ->
                if (context != null) {
                    if (postlikePojos[0].status.equals("true")) {
                        //  feedlist.get(pos).setPostLike(String.valueOf(postlikePojos.get(0).getData().));
                    } else {
                        for (i in feedlist.indices) {
                            if (feedlist[i]!!.postID.equals(
                                    postlikePojos[0].feedDatum?.postID,
                                    ignoreCase = true
                                )
                            ) {
                                feedlist[i] = postlikePojos[0].feedDatum
                                notifyDataSetChanged()
                                break
                            }
                        }
                    }
                }
            })
    }

}


