package com.nxplayr.fsl.ui.activity.post.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.facebook.drawee.view.SimpleDraweeView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.Albummedia


class CreatePostVideoServerAdapter(context: Context, private var videoList: List<Albummedia>): PagerAdapter() {
    var context: Context? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return  videoList.size
    }
    override fun instantiateItem(view: ViewGroup, position: Int): Any {

        val videoLayout = inflater.inflate(R.layout.sliding_videos_layout,view, false)
        val selectedVideos= videoLayout.findViewById<SimpleDraweeView>(R.id.selectedVideos)
        val imageViewPlayVideo= videoLayout.findViewById(R.id.imageViewPlayVideo) as ImageView
        val cancelImageView = videoLayout.findViewById(R.id.cancelVideoIV) as ImageView

        selectedVideos.setImageURI(RestClient.image_base_url_posts+videoList[position].albummediaThumbnail)
        cancelImageView.visibility=View.GONE
        imageViewPlayVideo.setOnClickListener {
                //itemClicks.onPlay(videoList,position)
        }
        cancelImageView.setOnClickListener {
            //itemClick.onDelete(videoList,position)
        }

        view.addView(videoLayout, 0)
        return videoLayout
    }



    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
    override fun saveState(): Parcelable? {
        return null
    }
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    interface OnClickToPlayVideo{
        fun onPlay(imageList: List<Albummedia>, position: Int)
    }
    interface OnClickToCancelVideo{
        fun onDelete(imageList: List<Albummedia>, position: Int)
    }

 }

