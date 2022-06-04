package com.nxplayr.fsl.ui.fragments.feed.adapter

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.facebook.drawee.view.SimpleDraweeView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager

/**
 * Created by ADMIN on 26/12/2017.
 */
class TrendingItemDocumentAdapter(var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    var lastPosition = -1
    var trendingFeedDatum: CreatePostData? = null
    var sessionManager: SessionManager? = null
    var width = 0
    var targetHeight = 0f
    var mClickListener: ClickInterface? = null
    var postposition = 0

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
                .inflate(R.layout.item_feed_link_photo, parent, false)

            ImgHolder(view)
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
                MyUtils.TYPE_FULL
            }
            2 -> MyUtils.TYPE_HALF
            3 -> MyUtils.TYPE_HALF_H
            else -> MyUtils.TYPE_FULL
        }
    }

    interface ClickInterface {
        fun openPhotoDetails(pos: Int)
    }

    inner class ImgHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        //  var useruploadDocumentWebview = itemView?.findViewById(R.id.webView) as WebView

        var useruploadImageview = itemView?.findViewById(R.id.linkImageview) as SimpleDraweeView
        var linktext = itemView?.findViewById(R.id.linktext) as TextView
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImgHolder) {
            val holder1 = holder
            //   holder1. useruploadDocumentWebview.getSettings().setLoadsImagesAutomatically(true);
            if (!trendingFeedDatum?.postSerializedData.isNullOrEmpty() && trendingFeedDatum?.postSerializedData?.get(
                    0
                )?.albummedia?.get(0)?.albummediaFile?.contains(".pdf")!!
            ) {
                if (!trendingFeedDatum?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaThumbnail.isNullOrEmpty()) {
                    holder1.useruploadImageview?.setImageURI(
                        RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(
                            0
                        )?.albummedia?.get(0)?.albummediaThumbnail
                    )
                } else {
                    holder1.useruploadImageview.setImageResource(R.drawable.pdf_icon)
                }
            } else if (!trendingFeedDatum?.postSerializedData.isNullOrEmpty() && trendingFeedDatum?.postSerializedData?.get(
                    0
                )?.albummedia?.get(0)?.albummediaFile?.contains(".docx")!!
            ) {
                if (!trendingFeedDatum?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaThumbnail.isNullOrEmpty()) {
                    holder1.useruploadImageview?.setImageURI(
                        RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(
                            0
                        )?.albummedia?.get(0)?.albummediaThumbnail
                    )
                } else {
                    holder1.useruploadImageview.setImageResource(R.drawable.word_file_icon)
                }
            } else if (!trendingFeedDatum?.postSerializedData.isNullOrEmpty() && trendingFeedDatum?.postSerializedData?.get(
                    0
                )?.albummedia?.get(0)?.albummediaFile?.contains(".xlsx")!!
            ) {
                if (!trendingFeedDatum?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaThumbnail.isNullOrEmpty()) {
                    holder1.useruploadImageview?.setImageURI(
                        RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(
                            0
                        )?.albummedia?.get(0)?.albummediaThumbnail
                    )
                } else {
                    holder1.useruploadImageview.setImageResource(R.drawable.word_file_icon)
                }
            } else if (!trendingFeedDatum?.postSerializedData.isNullOrEmpty() && trendingFeedDatum?.postSerializedData?.get(
                    0
                )?.albummedia?.get(0)?.albummediaFile?.contains(".pptx")!!
            ) {
                if (!trendingFeedDatum?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaThumbnail.isNullOrEmpty()) {
                    holder1.useruploadImageview?.setImageURI(
                        RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(
                            0
                        )?.albummedia?.get(0)?.albummediaThumbnail
                    )
                } else {
                    holder1.useruploadImageview.setImageResource(R.drawable.ppt_icon)
                }
            }
            if (!trendingFeedDatum?.postSerializedData.isNullOrEmpty()) {
                holder1.linktext.text =
                    trendingFeedDatum?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile
            }
            holder1.itemView.setOnClickListener {
                mClickListener!!.openPhotoDetails(0)
            }
        }
        /*holder1.useruploadDocumentWebview.getSettings().setJavaScriptEnabled(true);
        holder1.useruploadDocumentWebview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        holder1.useruploadDocumentWebview.loadUrl("http://docs.google.com/gview?embedded=true&url="+RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile);*/

    }
}


