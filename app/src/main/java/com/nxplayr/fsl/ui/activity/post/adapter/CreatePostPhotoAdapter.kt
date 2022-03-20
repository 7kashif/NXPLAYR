package com.nxplayr.fsl.ui.activity.post.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.facebook.drawee.view.SimpleDraweeView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo

class CreatePostPhotoAdapter(context: Context,
                             private var imageList: ArrayList<CreatePostPhotoPojo>,
                             private var itemClick: OnClickToCancelPhoto
): PagerAdapter() {

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
        val lylEdit = imageLayout.findViewById(R.id.lylEdit) as LinearLayout

        selectedImages.setImageURI("file://" +imageList[position].imagePath)
        imageViewCancelPhoto.setOnClickListener {
            itemClick.onDelete(imageList,position)
            notifyDataSetChanged()
        }

        lylEdit.setOnClickListener {

            itemClick.onEditImage(imageList,position)
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
        fun onDelete(imageList: ArrayList<CreatePostPhotoPojo>, position: Int)
        fun onEditImage(imageList: ArrayList<CreatePostPhotoPojo>, position: Int)
    }

    fun updateAdapter(){
        notifyDataSetChanged();
    }

}