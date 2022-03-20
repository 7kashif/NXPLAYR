package com.nxplayr.fsl.ui.fragments.feed.adapter

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.util.MyUtils
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.nxplayr.fsl.data.api.RestClient

/**
 * Created by ADMIN on 26/12/2017.
 */
class TrendingItemVideoAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder?> {
    var lastPosition = -1
    var context: Activity
    var trendingFeedDatum: CreatePostData? = null
    var postposition = 0
    var exoPlayer: SimpleExoPlayer? = null
    var videoPlayPosition = -1
    var mClickListener: ClickInterface? = null
    var thumbSize = 0

    constructor(context: Activity, trendingFeedDatum: CreatePostData?, postposition: Int) {
        this.context = context
        this.trendingFeedDatum = trendingFeedDatum
        this.postposition = postposition
    }

    constructor(context: Activity) {
        this.context = context
    }

    fun setData(feeddata: CreatePostData?) {
        trendingFeedDatum = feeddata
        thumbSize = if (trendingFeedDatum!!.postSerializedData.size > 4) 4 else trendingFeedDatum!!.postSerializedData.size
        notifyDataSetChanged()
    }

    fun setClickListener(clickListener: ClickInterface?) {
        mClickListener = clickListener
    }

    fun setActivity(context: Activity) {
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MyUtils.TYPE_FULL) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trending_video, parent, false)
            ImgHolder(view)
        } else if (viewType == MyUtils.TYPE_HALF) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trending_video2, parent, false)
            ImgHolder2(view)
        } else if (viewType == MyUtils.TYPE_HALF_H) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_trending_video3, parent, false)
            ImgHolder3(view)
        } else if (viewType == MyUtils.TYPE_QUARTER) {
            val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_trending_video4, parent, false)
            ImgHolder4(view)
        } else {
            null!!
        }
    }


    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return getTypeView(position)
    }

    fun getTypeView(position: Int): Int {
        var size = MyUtils.TEXT_TYPE
        if(!trendingFeedDatum?.postSerializedData.isNullOrEmpty()) {
            if (trendingFeedDatum!!.postSerializedData[0].albummedia.size >= 3)
                size= 3
            else
                size=  trendingFeedDatum!!.postSerializedData[0].albummedia.size
        }
        return when (size) {
            1 -> MyUtils.TYPE_FULL
            2 -> MyUtils.TYPE_HALF
            3 -> {
                MyUtils.TYPE_HALF_H
            }
            4 -> {
                MyUtils.TYPE_QUARTER
            }
            else -> MyUtils.TYPE_FULL
        }
    }

    var isMuteing: Boolean
        get() = MyUtils.isMuteing
        set(muteing) {
            MyUtils.isMuteing = muteing
        }

    interface ClickInterface {
        fun setVolume(isMuting: Boolean)
        fun openVideoDetails(pos: Int)
    }

    inner class ImgHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var galleryImageView: PlayerView? = null
        var thumnail: ImageView? = null
        var playicon: ImageView? = null
        var volume: ImageView?=null
        var pb: ProgressBar? = null
        var selectImage: RelativeLayout? = null
        var ivFullScreen: ImageButton

        init {
            galleryImageView = itemView!!.findViewById<View>(R.id.galleryImageView) as PlayerView
            playicon = itemView!!.findViewById<View>(R.id.playicon) as ImageView
            thumnail = itemView!!.findViewById<View>(R.id.thumnail) as ImageView
            pb = itemView!!.findViewById<View>(R.id.pb) as ProgressBar
//            volume = itemView!!.findViewById<View>(R.id.volume) as ImageView

            ivFullScreen = galleryImageView!!.findViewById<View>(R.id.exo_fullscreen_icon) as ImageButton
            ivFullScreen.visibility = View.VISIBLE
            volume = galleryImageView!!.findViewById<View>(R.id.exo_volume_icon) as ImageButton
            volume?.visibility = View.VISIBLE
        }
    }

    inner class ImgHolder2(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var galleryImageView: PlayerView? = null
        var thumnail: ImageView? = null
        var volume: ImageView? = null
        var pb: ProgressBar? = null
        var thumnail1: ImageView? = null
        var playicon: ImageView? = null
        var selectImage: LinearLayout? = null
    }

    inner class ImgHolder3(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var galleryImageView: PlayerView? = null
        var thumnail: ImageView? = null
        var playicon: ImageView? = null
        var volume: ImageView? = null
        var pb: ProgressBar? = null
        var thumnail1: ImageView? = null
        var thumnail2: ImageView? = null
        var selectImage: LinearLayout? = null
    }

    inner class ImgHolder4(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var galleryImageView: PlayerView? = null
        var thumnail: ImageView? = null
        var playicon: ImageView? = null
        var volume: ImageView? = null
        var pb: ProgressBar? = null
        var thumnail1: ImageView? = null
        var playicon1: ImageView? = null
        var thumnail2: ImageView? = null
        var thumnail3: ImageView? = null
        var selectImage: LinearLayout? = null
        var tvCount: TextView? = null
    }

    override fun onBindViewHolder(holder1: RecyclerView.ViewHolder, position: Int) {
        if (holder1 is ImgHolder) {
            val holder = holder1
            val path = ""
            holder.thumnail!!.visibility = View.GONE
            try {
                if (trendingFeedDatum!!.postSerializedData.size > 0) {
                    if(!trendingFeedDatum!!.postSerializedData[0].albummedia.isNullOrEmpty()&& !trendingFeedDatum?.postSerializedData!![0].albummedia[0].albummediaThumbnail.isNullOrEmpty())
                    {
                        holder.thumnail!!.visibility = View.VISIBLE
                        holder.thumnail!!.setImageURI(Uri.parse(RestClient.image_base_url_posts+trendingFeedDatum?.postSerializedData!![0].albummedia[0].albummediaThumbnail))

                    }
                    if (!trendingFeedDatum!!.postSerializedData[0].albummedia.isNullOrEmpty()&& trendingFeedDatum!!.postSerializedData[0].albummedia[0].isPlaying) {
                        if(!trendingFeedDatum!!.postSerializedData[0].albummedia.isNullOrEmpty()&& !trendingFeedDatum?.postSerializedData!![0].albummedia[0].albummediaThumbnail.isNullOrEmpty())
                        {
                            holder.thumnail!!.visibility = View.VISIBLE
                        }else{
                            holder.thumnail!!.visibility = View.GONE
                        }
                        holder.playicon!!.visibility = View.GONE
                    } else {
                        holder.thumnail!!.visibility = View.VISIBLE
                        holder.playicon!!.visibility = View.VISIBLE
                    }
                    holder.ivFullScreen.setOnClickListener {
                        if (mClickListener != null) {
                            mClickListener!!.openVideoDetails(0)
                        }
                    }
                    holder.galleryImageView!!.setShutterBackgroundColor(Color.TRANSPARENT)
                    if (isMuteing) {
                        holder.volume?.background = context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp)
                    } else {
                        holder.volume?.background = context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp)
                    }
                    holder.volume?.setOnClickListener {
                        if (isMuteing) {
                           isMuteing = false
                            holder.volume?.background = context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp)
                        } else {
                            isMuteing = true
                            holder.volume?.background = context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp)
                        }
                        if (mClickListener != null)
                            mClickListener!!.setVolume(isMuteing)
                    }
                    holder.playicon!!.setOnClickListener {
                        if (mClickListener != null) {
                            mClickListener!!.openVideoDetails(0)
                        }
                    }
                    /*   holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mClickListener != null) {
                                mClickListener.openVideoDetails(0);
                            }

                        }
                    });*/
                }
            } catch (e: Exception) {
            }
        }
        if (holder1 is ImgHolder2) {
            val holder = holder1
            val path = ""
            if (trendingFeedDatum!!.postSerializedData.size > 0) { // path = trendingFeedDatum.getPostSerializedData().get(0).getAlbummediaThumbnail();
                holder.galleryImageView!!.setShutterBackgroundColor(Color.TRANSPARENT)
                if (trendingFeedDatum!!.postSerializedData[0].albummedia[0].isPlaying) {
                    holder.thumnail!!.visibility = View.GONE
                    holder.playicon!!.visibility = View.GONE
                } else {
                    holder.thumnail!!.visibility = View.VISIBLE
                    holder.playicon!!.visibility = View.VISIBLE
                }
            } else {
                holder.thumnail!!.visibility = View.GONE
                holder.playicon!!.visibility = View.VISIBLE
            }
            if (isMuteing) {
                holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp))
            } else {
                holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp))
            }
            holder.volume!!.setOnClickListener {
                if (isMuteing) {
//                    muteing = false
                    holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp))
                } else {
//                    muteing = true
                    holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp))
                }
                if (mClickListener != null) mClickListener!!.setVolume(isMuteing)
            }
            if (trendingFeedDatum!!.postSerializedData.size > 1) { //path = trendingFeedDatum.getPostSerializedData().get(1).getAlbummediaThumbnail();
            }
            holder.selectImage!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openVideoDetails(0)
                }
            }
            holder.thumnail1!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openVideoDetails(1)
                }
            }
        }
        if (holder1 is ImgHolder3) {
            val holder = holder1
            val path = ""
            if (trendingFeedDatum!!.postSerializedData.size > 0) //path = trendingFeedDatum.getPostSerializedData().get(0).getAlbummediaThumbnail();
                holder.galleryImageView!!.setShutterBackgroundColor(Color.TRANSPARENT)
            if (trendingFeedDatum!!.postSerializedData[0].albummedia[0].isPlaying) {
                holder.thumnail!!.visibility = View.GONE
                holder.playicon!!.visibility = View.GONE
            } else {
                holder.thumnail!!.visibility = View.VISIBLE
                holder.playicon!!.visibility = View.VISIBLE
            }
            if (isMuteing) {
                holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp))
            } else {
                holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp))
            }
            holder.volume!!.setOnClickListener {
                if (isMuteing) {
//                    muteing = false
                    holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp))
                } else {
//                    muteing = true
                    holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp))
                }
                if (mClickListener != null) mClickListener!!.setVolume(isMuteing)
            }
            if (trendingFeedDatum!!.postSerializedData.size > 2) { //path = trendingFeedDatum.getPostSerializedData().get(2).getAlbummediaThumbnail();
            }
            holder.selectImage!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openVideoDetails(0)
                }
            }
            holder.thumnail1!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openVideoDetails(1)
                }
            }
            holder.thumnail2!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openVideoDetails(2)
                }
            }
        }
        if (holder1 is ImgHolder4) {
            val holder = holder1
            val path = ""
            if (trendingFeedDatum!!.postSerializedData.size > 0) // path = trendingFeedDatum.getPostSerializedData().get(0).getAlbummediaThumbnail();
                holder.galleryImageView!!.setShutterBackgroundColor(Color.TRANSPARENT)
            if (trendingFeedDatum!!.postSerializedData[0].albummedia[0].isPlaying) {
                holder.thumnail!!.visibility = View.GONE
                holder.playicon!!.visibility = View.GONE
            } else {
                holder.thumnail!!.visibility = View.VISIBLE
                holder.playicon!!.visibility = View.VISIBLE
            }
            if (isMuteing) {
                holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp))
            } else {
                holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp))
            }
            holder.volume!!.setOnClickListener {
                if (isMuteing) {
//                    muteing = false
                    holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_up_black_24dp))
                } else {
//                    muteing = true
                    holder.volume!!.setImageDrawable(context.resources.getDrawable(R.drawable.ic_volume_off_black_24dp))
                }
                if (mClickListener != null) mClickListener!!.setVolume(isMuteing)
            }
            if (trendingFeedDatum!!.postSerializedData.size > 1) { // path = trendingFeedDatum.getPostSerializedData().get(1).getAlbummediaThumbnail();
            }
            if (trendingFeedDatum!!.postSerializedData.size > 2) { // path = trendingFeedDatum.getPostSerializedData().get(2).getAlbummediaThumbnail();
            }
            if (trendingFeedDatum!!.postSerializedData.size > 3) { // path = trendingFeedDatum.getPostSerializedData().get(3).getAlbummediaThumbnail();
            }
            holder.selectImage!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openVideoDetails(0)
                }
            }
            holder.thumnail1!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openVideoDetails(1)
                }
            }
            holder.thumnail2!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openVideoDetails(2)
                }
            }
            holder.thumnail3!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openVideoDetails(3)
                }
            }
            if (trendingFeedDatum!!.postSerializedData.size > 4) {
                holder.tvCount!!.visibility = View.VISIBLE
                val s = trendingFeedDatum!!.postSerializedData[0].albummedia.size - 4
                holder.tvCount!!.text = "+$s"
            }
        }
    }
}