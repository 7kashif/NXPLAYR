package com.nxplayr.fsl.ui.fragments.feed.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.post.view.LinkWebViewActivity
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager


/**
 * Created by ADMIN on 26/12/2017.
 */
class TrendingItemLinkAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    var lastPosition = -1
    var trendingFeedDatum : CreatePostData? = null
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
        return if (viewType == MyUtils.TYPE_FULL)
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_feed_link_photo, parent, false)
            ImgHolder(view)
        }  else {
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
        if(!trendingFeedDatum?.postSerializedData.isNullOrEmpty()) {
            if (trendingFeedDatum!!.postSerializedData[0].albummedia.size >= 1)
                size= 1
            else
                size=  trendingFeedDatum!!.postSerializedData[0].albummedia.size
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
        var useruploadImageview= itemView?.findViewById(R.id.linkImageview) as SimpleDraweeView
        var linktext= itemView?.findViewById(R.id.linktext) as TextView
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImgHolder) {
            var des: Array<String>? = null
            var uri: String = ""
            val holder1= holder

            try {
                holder1.useruploadImageview.setImageURI(RestClient.image_base_url_posts + trendingFeedDatum?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile)

                des = if (!trendingFeedDatum?.postDescription.isNullOrEmpty()) {

                    trendingFeedDatum?.postDescription?.split("||")?.toTypedArray()
                } else {
                    null
                }

                holder1.linktext.text = if (des != null) {
                    if (des.size == 1) {
                        Constant.decode(des[0])
                    } else {
                        des[1] + "\n\n" + if (des[2] != null) des[2] else ""
                    }

                } else {
                    ""
                }
                uri = ""
                if (des!=null && des?.size!! > 1){
                    uri = if (!des!![1]?.isNullOrEmpty())
                    {
                        Uri.parse(des[1]).toString()
                    }
                    else
                    {
                        ""
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder1.itemView.setOnClickListener {
                if (des?.size!! > 1) {
                    Intent(context, LinkWebViewActivity::class.java).apply {

                        putExtra("file", uri.toString())
                        context.startActivity(this)
                    }
                }
            }
        }
    }
}