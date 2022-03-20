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

class CreatePostPhotoServerAdapter(context: Context, private var imageList: List<Albummedia>): PagerAdapter() {

    var context: Context? = null
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return imageList.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {

        val imageLayout = inflater.inflate(R.layout.sliding_images_layout, view, false)!!
        val selectedImages = imageLayout.findViewById(R.id.selectedImages) as SimpleDraweeView
        val imageViewCancelPhoto= imageLayout.findViewById(R.id.imageViewCancelPhoto) as ImageView
        selectedImages.setImageURI(RestClient.image_base_url_posts+imageList[position].albummediaFile)
        imageViewCancelPhoto.setOnClickListener {
           // itemClick.onDelete(imageList,position)
           // notifyDataSetChanged()
        }

        view.addView(imageLayout,0)
        return imageLayout
    }
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}
    override fun saveState(): Parcelable? {
        return null
    }
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
    interface OnClickToCancelPhoto{
        fun onDelete(imageList: List<Albummedia>, position: Int)
    }
}