package com.nxplayr.fsl.ui.fragments.explorepost.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.facebook.drawee.view.SimpleDraweeView
import com.nxplayr.fsl.ui.activity.fullscreenvideo.view.FullScreenVideo
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.postcomment.view.PostCommentListFragment
import com.nxplayr.fsl.data.model.CommentData
import com.nxplayr.fsl.data.model.CreatePostData
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
import com.nxplayr.fsl.ui.fragments.feed.adapter.CommentAdapter
import com.nxplayr.fsl.ui.fragments.feed.view.HashTagPostListFragment
import com.nxplayr.fsl.util.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.exo_playback_control_view.view.*
import kotlinx.android.synthetic.main.explore_video_detail_page_activity.view.*
import java.util.*

class ExploreVideoDetailAdapter(
    val context: Activity,
    val onItemClick: OnItemClick,
    val exploreList: ArrayList<CreatePostData?>?,
    val viewAll: Boolean = false
) : PagerAdapter() {

    private val inflater: LayoutInflater
    private var isMuteing = false
    var exoPlayer: SimpleExoPlayer? = null
    var videoPlayPosition = -1
    var cache: SimpleCache? = null
    var sessionManager: SessionManager? = null

    init {
        inflater = LayoutInflater.from(context)
        sessionManager = SessionManager(context)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun getCount(): Int {
        return exploreList?.size!!
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {

        val imageLayout =
            inflater.inflate(R.layout.explore_video_detail_page_activity, view, false)!!


        val txt_userName = imageLayout.findViewById(R.id.txt_userName) as TextView
        val txt_like = imageLayout.findViewById(R.id.txt_like) as TextView
        val txt_views = imageLayout.findViewById(R.id.txt_views) as TextView
        val txt_comments = imageLayout.findViewById(R.id.txt_comments) as TextView
        val txt_hashtag = imageLayout.findViewById(R.id.txt_hashtag) as PostDesTextView

        val ll_like = imageLayout.findViewById(R.id.ll_like) as LinearLayoutCompat
        val ll_comment = imageLayout.findViewById(R.id.ll_comment) as LinearLayoutCompat
        val ll_view = imageLayout.findViewById(R.id.ll_view) as LinearLayoutCompat
        val layout_share = imageLayout.findViewById(R.id.layout_share) as LinearLayoutCompat
        val ll_more = imageLayout.findViewById(R.id.ll_more) as LinearLayoutCompat

        val img_thumbnail = imageLayout.findViewById(R.id.img_thumbnail) as AppCompatImageView
        val img_userProfile = imageLayout.findViewById(R.id.img_userProfile) as SimpleDraweeView
        val img_cUserProfile = imageLayout.findViewById(R.id.img_cUserProfile) as SimpleDraweeView
        val img_unselected = imageLayout.findViewById(R.id.img_unselected) as ImageView
        val img_selected = imageLayout.findViewById(R.id.img_selected) as ImageView
        val img_play = imageLayout.findViewById(R.id.img_play) as ImageView
        val playerView = imageLayout.findViewById(R.id.playerView) as PlayerView
        val exo_player_progress_bar =
            imageLayout.findViewById(R.id.exo_player_progress_bar) as ProgressBar
        val rcCommentList = imageLayout.findViewById(R.id.rcCommentList) as RecyclerView
        val txt_write_comment = imageLayout.findViewById(R.id.txt_write_comment) as EditText
        val img_send_arrow = imageLayout.findViewById(R.id.img_send_arrow) as ImageView
        val tvCommenViewAll = imageLayout.findViewById(R.id.tvCommenViewAll) as TextView

        playerView.exo_volume_icon.visibility = View.GONE

        if (exploreList!![position]!!.youpostLiked.equals("No")) {

            imageLayout.img_unselected.visibility = View.VISIBLE
            imageLayout.img_selected.visibility = View.GONE
        } else {

            imageLayout.img_unselected.visibility = View.GONE
            imageLayout.img_selected.visibility = View.VISIBLE
        }

        txt_userName.text =
            exploreList.get(position)!!.userFirstName + " " + exploreList.get(position)!!.userLastName
        txt_like.text = exploreList.get(position)!!.postLike
        txt_views.text = exploreList.get(position)!!.postViews
        txt_comments.text = exploreList.get(position)!!.postComment
        if (exploreList[position]!!.userProfilePicture.isNullOrEmpty()) {
            img_userProfile.setImageResource(R.drawable.profile_pic_placeholder)
            img_cUserProfile.setImageResource(R.drawable.profile_pic_placeholder)

        } else {
            img_userProfile.setImageURI(RestClient.image_base_url_users + exploreList.get(position)!!.userProfilePicture)
            img_cUserProfile.setImageURI(RestClient.image_base_url_users + sessionManager?.get_Authenticate_User()?.userProfilePicture)

        }
        img_thumbnail.visibility = View.GONE
        if (exploreList[position]!!.postSerializedData.size > 0) {
            if (!exploreList[position]!!.postSerializedData[0].albummedia.isNullOrEmpty()) {
                img_thumbnail.visibility = View.VISIBLE

                if (exploreList.get(position)!!.postSerializedData[0].albummedia[0].albummediaThumbnail.isNullOrEmpty()) {
                    var FileName =
                        RestClient.image_base_url_posts + exploreList.get(position)!!.postSerializedData[0].albummedia[0].albummediaFile
                    FileName = FileName.substring(0, FileName.lastIndexOf("."))
                    Picasso.get().load(FileName + "_thumb.jpg").into(img_thumbnail)
                } else {
                    var FileName =
                        RestClient.image_base_url_posts + exploreList.get(position)!!.postSerializedData[0].albummedia[0].albummediaThumbnail
                    Picasso.get().load(FileName).into(img_thumbnail)
                }

            }
            if (!exploreList[position]!!.postSerializedData[0].albummedia.isNullOrEmpty() && exploreList[position]!!.postSerializedData[0].albummedia[0].isPlaying) {
                if (!exploreList[position]!!.postSerializedData[0].albummedia.isNullOrEmpty() && !exploreList[position]?.postSerializedData!![0].albummedia[0].albummediaThumbnail.isNullOrEmpty()) {
                    img_thumbnail.visibility = View.VISIBLE
                } else {
                    img_thumbnail.visibility = View.GONE
                }
                img_play.visibility = View.GONE
            } else {
                img_thumbnail.visibility = View.VISIBLE
                img_play.visibility = View.VISIBLE
            }
            playerView.setShutterBackgroundColor(Color.TRANSPARENT)
            if (isMuteing) {
                playerView.playerView.exo_volume_icon.background =
                    context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp)
            } else {
                playerView.playerView.exo_volume_icon.background =
                    context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp)
            }
            playerView.playerView.exo_volume_icon?.setOnClickListener {
                if (isMuteing) {
//                      muteing = false
                    playerView.playerView.exo_volume_icon?.background =
                        context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp)
                } else {
//                        muteing = true
                    playerView.playerView.exo_volume_icon.background =
                        context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp)
                }
                setMuteing(isMuteing)
            }

        }
        if (exploreList[position]!!.postDescription.isNullOrEmpty()) {

            txt_hashtag.visibility = View.GONE

        } else {

            txt_hashtag.text = Constant.decode(exploreList[position]!!.postDescription)
            txt_hashtag.doResizeTextView(txt_hashtag, 3, "...See More", true)
            txt_hashtag.setHashClickListener(object : PostDesTextView.OnHashEventListener {
                override fun onHashTagClick(friendsId: String?) {
                    var hashTagPostListFragment = HashTagPostListFragment()
                    Bundle().apply {
                        putString("hashTag", friendsId)
                        putString("postType", exploreList[position]!!.postType)
                        hashTagPostListFragment.arguments = this
                    }
                    (context as MainActivity).navigateTo(
                        hashTagPostListFragment,
                        hashTagPostListFragment::class.java.name,
                        true
                    )
                }

            })
        }

        imageLayout.ll_like.setOnClickListener {
            onItemClick.onClicklisneter(position)
        }
        imageLayout.ll_comment.setOnClickListener {
            onItemClick.onClickCommnetlisneter(position)
        }
        imageLayout.ll_view.setOnClickListener {
            onItemClick.onClickViewlisneter(position)
        }
        imageLayout.layout_share.setOnClickListener {
            onItemClick.onClickSharelisneter(position)
        }
        imageLayout.ll_more.setOnClickListener {
            onItemClick.onClickMorelisneter(position)
        }
        img_unselected.setOnClickListener {
            onItemClick.onClickUnselectlisneter(position)
        }
        img_selected.setOnClickListener {
            onItemClick.onClickSelectlisneter(position)
        }
        imageLayout.img_play.setOnClickListener {
            playAvailableVideos(
                position,
                playerView,
                img_thumbnail,
                playerView.exo_volume_icon,
                exo_player_progress_bar,
                img_play,
                exploreList
            )
            // onItemClick.onClickPlaylisneter(position,exploreList?.get(position)?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile!!)
        }

        playerView.exo_fullscreen_icon.setOnClickListener {
            val i = Intent(context, FullScreenVideo::class.java)
            i.putExtra(
                "videouri",
                RestClient.image_base_url_posts + exploreList.get(position)?.postSerializedData!![0].albummedia[0].albummediaFile
            )
            context.startActivity(i)
            context.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)

            playerView.exo_volume_icon.visibility = View.GONE
            playerView.setShutterBackgroundColor(Color.TRANSPARENT)
            img_thumbnail.visibility = View.VISIBLE
            img_play.visibility = View.VISIBLE
        }
        img_send_arrow.setOnClickListener {
            onItemClick.onComment(
                position,
                "sendComment",
                txt_write_comment,
                txt_write_comment.text.toString().trim()
            )
        }
        tvCommenViewAll.setOnClickListener {
            var postCommentListFragment = PostCommentListFragment()
            Bundle().apply {
                putString("postId", exploreList.get(position)!!.postID)
                putString("postUserID", exploreList.get(position)!!.userID)
                putSerializable("feedData", exploreList.get(position)!!)
                postCommentListFragment.arguments = this
            }
            (context as MainActivity).navigateTo(
                postCommentListFragment,
                postCommentListFragment::class.simpleName!!,
                true
            )
        }
        if (!exploreList.get(position)?.postCommentList.isNullOrEmpty()) {
            if (exploreList.get(position)?.postCommentList!!.size >= 2) {

                tvCommenViewAll.visibility = View.VISIBLE
            } else {

                tvCommenViewAll.visibility = View.GONE
            }
        } else {
            tvCommenViewAll.visibility = View.GONE
        }
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rcCommentList.layoutManager = linearLayoutManager

        val postCommetnAdapter = CommentAdapter(
            context, exploreList.get(position)?.postCommentList!!,
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
                        putString("postId", exploreList.get(position)!!.postID)
                        putString("postUserID", exploreList.get(position)!!.userID)
                        putSerializable("feedData", exploreList.get(position)!!)
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
                        putString("postId", exploreList.get(position)!!.postID)
                        putString("postUserID", exploreList.get(position)!!.userID)
                        putSerializable("feedData", exploreList.get(position)!!)
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
        rcCommentList.setHasFixedSize(true)
        rcCommentList.adapter = postCommetnAdapter

        view.addView(
            imageLayout,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        return imageLayout
    }

    fun playAvailableVideos(
        position: Int,
        playerView: PlayerView,
        thumnail: ImageView,
        volume: View?,
        progressBar: ProgressBar,
        playIcon: ImageView,
        feedList: ArrayList<CreatePostData?>?
    ) {
        try {
            pausePlayer(position)
            if (!exploreList!![position]!!.postSerializedData[0].albummedia[0].isPlaying) {
                videoPlayPosition = position
                pausePlayer()
                exploreList[position]!!.postSerializedData[0].albummedia[0].isPlaying = true
                playVideo(
                    position,
                    playerView,
                    thumnail,
                    volume,
                    progressBar,
                    playIcon,
                    feedList
                )
            }


            /*} else
            {
                if(feedlist[i]!!.postSerializedData[0].albummedia[0].isPlaying) {
                    feedlist[i]!!.postSerializedData[0].albummedia[0].isPlaying=(false);
                    videoPlayPosition=i
                    pausePlayer()
                }
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun playVideo(
        videoPlayPosition: Int,
        playerView: PlayerView,
        thumnail: ImageView,
        volume: View?,
        progressBar: ProgressBar,
        playIcon: ImageView,
        feedlist: ArrayList<CreatePostData?>?
    ) {

        context.runOnUiThread {
            val mHandler = Handler(Looper.getMainLooper())
            mHandler.post(object : Runnable {
                override fun run() {
                    var videoURI: Uri? = null

                    videoURI = Uri.parse(
                        RestClient.image_base_url_posts + feedlist?.get(videoPlayPosition)!!.postSerializedData?.get(
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
                        feedlist.get(videoPlayPosition)?.postSerializedData?.get(0)!!.albummedia?.get(
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
        /* for(i in 0 until feedList.size)
                           {
                               if(feedList.get(i)!=null&&feedList.get(i).postSerializedData!![0]!!.albummedia!=null&& feedList.get(
                                       i
                                   ).postSerializedData!![0]!!.albummedia.isNotEmpty()
                               )
                                   feedList.get(i).postSerializedData!![0]!!.albummedia[0].isPlaying=(false);
                           }*/
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
                for (i in exploreList!!.indices) {
                    if (exploreList.get(i) != null && !exploreList.get(i)?.postSerializedData.isNullOrEmpty() && !exploreList.get(
                            i
                        )?.postSerializedData?.get(0)?.albummedia.isNullOrEmpty()
                    )
                        if (exploreList[i]!!.postSerializedData[0].albummedia[0].isPlaying) {
                            videoPlayPosition = i
                            break
                        }
                }

                if (videoPlayPosition > -1) {
                    if (exoPlayer != null)
                        exploreList[videoPlayPosition]!!.postSerializedData[0].albummedia[0].duration =
                            (exoPlayer?.currentPosition!!)
                    exploreList[videoPlayPosition]!!.postSerializedData[0].albummedia[0].isPlaying =
                        false
                    notifyDataSetChanged()
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
                    for (i in exploreList!!.indices) {
                        if (exploreList.get(i) != null && exploreList.get(i)!!.postSerializedData[0].albummedia != null && exploreList?.get(
                                i
                            )!!.postSerializedData!![0].albummedia.isNotEmpty()
                        )
                            if (exploreList[i]!!.postSerializedData[0].albummedia[0].isPlaying && positionNot != i) {
                                videoPlayPosition = i
                                break
                            }
                    }

                    if (videoPlayPosition > -1) {
                        if (exoPlayer != null)
                            exploreList.get(videoPlayPosition)!!.postSerializedData[0].albummedia[0].duration =
                                (
                                        exoPlayer?.currentPosition!!
                                        )
                        exploreList.get(videoPlayPosition)!!.postSerializedData[0].albummedia[0].isPlaying =
                            (false)
                        notifyDataSetChanged()
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

    interface OnItemClick {
        fun onClicklisneter(pos: Int)
        fun onClickCommnetlisneter(pos: Int)
        fun onClickViewlisneter(pos: Int)
        fun onClickSharelisneter(pos: Int)
        fun onClickMorelisneter(pos: Int)
        fun onClickUnselectlisneter(pos: Int)
        fun onClickSelectlisneter(pos: Int)
        fun onClickPlaylisneter(pos: Int, videofile: String)
        fun onComment(pos: Int, from: String, editText: EditText, commentcomment: String)
    }
}

