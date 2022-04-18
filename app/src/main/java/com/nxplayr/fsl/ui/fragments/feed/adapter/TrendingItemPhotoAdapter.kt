package com.nxplayr.fsl.ui.fragments.feed.adapter

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.util.customView.WrapContentDraweeView
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager

/**
 * Created by ADMIN on 26/12/2017.
 */
class TrendingItemPhotoAdapter(var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    var lastPosition = -1
    var trendingFeedDatum: CreatePostData? = null
    var sessionManager: SessionManager? = null
    var postposition = 0
    var width = 0
    var targetHeight = 0f
    var mClickListener: ClickInterface? = null
    fun setData(feeddata: CreatePostData, postposition: Int) {
        trendingFeedDatum = feeddata
        this.postposition = postposition
        notifyDataSetChanged()
    }

    fun setActivity(context: Context, postposition: Int) {
        this.context = context
        this.postposition = postposition
        this.postposition = postposition
        sessionManager = SessionManager(context)
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
    }

    fun setClickListener(clickListener: ClickInterface?) {
        mClickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == MyUtils.TYPE_FULL) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_photo_adapter_one, parent, false)
            /*val useruploadImageview = view.findViewById<View>(R.id.useruploadImageview) as WrapContentDraweeView
            useruploadImageview.post { useruploadImageview.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, targetHeight.toInt()) }*/
            ImgHolder(view)
        } else if (viewType == MyUtils.TYPE_HALF) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_photo_adapter_two, parent, false)
            ImgHolder2(view)
        } else if (viewType == MyUtils.TYPE_HALF_H) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.feed_photo_adapter_three, parent, false)
            ImgHolder3(view)
        }
        /*else if (viewType == MyUtils.TYPE_QUARTER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_photo_adapter_four, parent, false)
            ImgHolder4(view)
        } */
        else {
            null!!
        }
    }


    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return getTypeView(position)
    }

    @Throws(IndexOutOfBoundsException::class)
    fun getTypeView(position: Int): Int {
        var size = MyUtils.TEXT_TYPE
        if (!trendingFeedDatum?.postSerializedData.isNullOrEmpty()) {
            if (trendingFeedDatum!!.postSerializedData[0].albummedia.size >= 3)
                size = 3
            else
                size = trendingFeedDatum!!.postSerializedData[0].albummedia.size
        }

        return when (size) {
            1 -> {
                targetHeight = context.resources.getDimension(R.dimen._180sdp)
                if (trendingFeedDatum!!.postSerializedData[0].albummedia.size == 1) {
                    if (trendingFeedDatum!!.postSerializedData[0].albummedia[0].albummediaFile.contains(
                            "*"
                        )
                    ) {
                        val size =
                            trendingFeedDatum!!.postSerializedData[0].albummedia[0].albummediaFile.split(
                                "\\*"
                            ).toTypedArray()
                        val aspectRatio = size[1].toDouble() / size[0].toDouble()
                        targetHeight = (width * aspectRatio).toFloat()
                    }
                }
                MyUtils.TYPE_FULL
            }
            2 -> MyUtils.TYPE_HALF
            3 -> MyUtils.TYPE_HALF_H
            // 4 -> MyUtils.TYPE_QUARTER
            else -> MyUtils.TYPE_FULL
        }
    }

    interface ClickInterface {
        fun openPhotoDetails(pos: Int)
    }

    inner class ImgHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var useruploadImageview =
            itemView?.findViewById(R.id.useruploadImageview) as WrapContentDraweeView
    }

    inner class ImgHolder2(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var useruploadImageview =
            itemView?.findViewById(R.id.useruploadImageview) as SimpleDraweeView
        var useruploadImageviewTwo =
            itemView?.findViewById(R.id.useruploadImageview_two) as SimpleDraweeView
    }

    inner class ImgHolder3(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var useruploadImageview =
            itemView?.findViewById(R.id.useruploadImageview) as SimpleDraweeView
        var useruploadImageviewTwo =
            itemView?.findViewById(R.id.useruploadImageview_two) as SimpleDraweeView
        var useruploadImageviewThree =
            itemView?.findViewById(R.id.useruploadImageview_three) as SimpleDraweeView
        // var tvCount=itemView?.findViewById(R.id.tvCount) as TextView
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImgHolder) {
            val holder1 = holder
            try {
                holder1.useruploadImageview?.setImageURI(
                    RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile
                )
                holder1.itemView.setOnClickListener {
                    if (mClickListener != null) {
                        mClickListener!!.openPhotoDetails(0)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (holder is ImgHolder2) {
            val holder2 = holder

            holder2.useruploadImageview?.setImageURI(
                RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(
                    0
                )?.albummedia?.get(0)?.albummediaFile
            )
            holder2.useruploadImageviewTwo?.setImageURI(
                RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(
                    0
                )?.albummedia?.get(1)?.albummediaFile
            )

            holder2.useruploadImageview!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openPhotoDetails(0)
                }
            }
            holder2.useruploadImageviewTwo!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openPhotoDetails(1)
                }
            }
        }
        if (holder is ImgHolder3) {
            val holder3 = holder
            holder3.useruploadImageview?.setImageURI(
                RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(
                    0
                )?.albummedia?.get(0)?.albummediaFile
            )
            holder3.useruploadImageviewTwo?.setImageURI(
                RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(
                    0
                )?.albummedia?.get(1)?.albummediaFile
            )
            holder3.useruploadImageviewThree?.setImageURI(
                RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(
                    0
                )?.albummedia?.get(2)?.albummediaFile
            )

            holder3.useruploadImageview!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openPhotoDetails(0)
                }
            }
            holder3.useruploadImageviewTwo!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openPhotoDetails(1)
                }
            }
            holder3.useruploadImageviewThree!!.setOnClickListener {
                if (mClickListener != null) {
                    mClickListener!!.openPhotoDetails(2)
                }
            }

            /*if (trendingFeedDatum!!.postSerializedData[0].albummedia.size > 3) {
                holder3.tvCount!!.visibility = View.VISIBLE
                val s = trendingFeedDatum!!.postSerializedData[0].albummedia.size -3
                holder3.tvCount!!.text = "+$s"
            }*/
        }
    }

}