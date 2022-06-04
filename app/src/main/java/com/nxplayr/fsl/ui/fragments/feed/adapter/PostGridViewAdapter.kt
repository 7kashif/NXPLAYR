package com.nxplayr.fsl.ui.fragments.feed.adapter

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.Model
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.util.BlurPostprocessor
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_post_imagetype.view.*

class PostGridViewAdapter(
    val context: Activity,
    val postlist: ArrayList<CreatePostData?>,
    val onItemClick: OnItemClick
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder?

        if (viewType === Model.Loder_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            viewHolder = LoaderViewHolder(view)
        } else if (viewType == Model.IMAGE_TYPE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post_imagetype, parent, false)
            viewHolder = PostsViewHolder(view, context)
        } else if (viewType == Model.Video_TYPE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post_imagetype, parent, false)
            viewHolder = PostsVideoViewHolder(view, context)
        } else if (viewType == Model.Link_TYPE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post_imagetype, parent, false)
            viewHolder = PostsLinkViewHolder(view, context)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post_imagetype, parent, false)
            viewHolder = PostsViewHolder(view, context)

        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        return postlist.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (postlist.get(position) == null)
            Model.Loder_TYPE else getTypeView(position)
    }

    fun getTypeView(position: Int): Int {
        var r = -1
        if (postlist.get(position) != null) {
            val type: String = postlist.get(position)?.postType!!
            when (type) {
                "Social" -> {
                    if (postlist.get(position)?.postMediaType.equals("Photo", false)) {
                        r = Model.IMAGE_TYPE
                    } else if (postlist.get(position)?.postMediaType.equals("Video", false)) {
                        r = Model.Video_TYPE
                    } else if (postlist.get(position)?.postMediaType.equals("Link", false)) {
                        r = Model.Link_TYPE
                    }
                }
            }
        } else {
            r = Model.Loder_TYPE
        }
        return r
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is PostsViewHolder) {
            val holder1 = holder
            holder1.bind(postlist[position], holder1.adapterPosition, onItemClick)
        } else if (holder is PostsVideoViewHolder) {
            val holder1 = holder
            holder1.bind(postlist[position], holder1.adapterPosition, onItemClick)
        } else if (holder is PostsLinkViewHolder) {
            val holder1 = holder
            holder1.bind(postlist[position], holder1.adapterPosition, onItemClick)
        }
    }

    class PostsViewHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        fun bind(listPosts: CreatePostData?, position: Int, onItemClick: OnItemClick) =
            with(itemView) {
                var postprocessor: BlurPostprocessor? = null
                if (listPosts != null) {
                    if (!listPosts.postSerializedData.isNullOrEmpty() && !listPosts.postSerializedData[0].albummedia.isNullOrEmpty()) {
                        if (!listPosts.postSerializedData[0].albummedia[0].albummediaFile.isNullOrEmpty()) {
                            img_postImageType1.setImageURI(RestClient.image_base_url_posts + listPosts.postSerializedData[0].albummedia[0].albummediaFile)
                            img_postImageType?.visibility = View.VISIBLE
                            img_postImageType1?.visibility = View.VISIBLE
                            bluroverlay.visibility = View.VISIBLE
                            img_placeolder.visibility = View.GONE
                            postprocessor = BlurPostprocessor(context, 25, 10)
                            try {
                                var imagePath =
                                    RestClient.image_base_url_posts + listPosts.postSerializedData.get(
                                        0
                                    ).albummedia.get(0).albummediaFile
                                val request =
                                    ImageRequestBuilder.newBuilderWithSource(Uri.parse(imagePath))
                                        .setPostprocessor(postprocessor)
                                        .build()
                                val controller = Fresco.newDraweeControllerBuilder()
                                    .setImageRequest(request)
                                    .setOldController(img_postImageType?.controller)
                                    .build() as PipelineDraweeController
                                img_postImageType?.controller = controller


                                when (listPosts.postMediaType) {
                                    "Photo" -> {
                                        bluroverlay.visibility = View.VISIBLE
                                        img_postImageType1.visibility = View.VISIBLE
                                        img_placeolder.visibility = View.GONE
                                        img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_photo_nobg)

                                    }
                                    "Video" -> {
                                        bluroverlay.visibility = View.VISIBLE
                                        img_postImageType1.visibility = View.VISIBLE
                                        img_placeolder.visibility = View.GONE
                                        img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_video_nobg)


                                    }
                                    "Document" -> {
                                        bluroverlay.visibility = View.VISIBLE
                                        img_postImageType1.visibility = View.VISIBLE
                                        img_placeolder.visibility = View.GONE
                                        img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_photo_nobg)


                                    }
                                    "Link" -> {
                                        bluroverlay.visibility = View.VISIBLE
                                        img_postImageType1.visibility = View.VISIBLE
                                        img_placeolder.visibility = View.GONE
                                        img_placeolder.setImageResource(R.drawable.small_link_icon)
                                        img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_link_nobg)


                                    }
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            when (listPosts.postMediaType) {
                                "Photo" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.GONE
                                    img_placeolder.visibility = View.VISIBLE
                                    img_placeolder.setImageResource(R.drawable.create_post_photo_nobg)

                                }
                                "Video" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.GONE
                                    img_placeolder.visibility = View.VISIBLE
                                    img_placeolder.setImageResource(R.drawable.create_post_video_nobg)


                                }
                                "Document" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.GONE
                                    img_placeolder.visibility = View.VISIBLE
                                    img_placeolder.setImageResource(R.drawable.create_post_photo_nobg)

                                }
                                "Link" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.GONE
                                    img_placeolder.visibility = View.VISIBLE
                                    img_placeolder.setImageResource(R.drawable.create_post_link_nobg)


                                }
                            }

                        }
                    }
                    tv_feedType.text = listPosts.postMediaType

                    if (!listPosts.postDescription.isNullOrEmpty()) {
                        tv_feedText.visibility = View.VISIBLE
                        tv_feedText.text = Constant.decode(listPosts.postDescription)
                        tv_feedText.doResizeTextView(tv_feedText, 3, "...See More", true)
                    } else {
                        tv_feedText.visibility = View.INVISIBLE
                    }
                }

                itemView.setOnClickListener {
                    onItemClick.onClicled(position, "All")
                }
            }
    }

    class PostsVideoViewHolder(itemView: View, context: Activity) :
        RecyclerView.ViewHolder(itemView) {
        fun bind(listPosts: CreatePostData?, position: Int, onItemClick: OnItemClick) =
            with(itemView) {
                if (listPosts != null) {
                    tv_feedType.text = listPosts.postMediaType
                    if (!listPosts.postDescription.isNullOrEmpty()) {
                        tv_feedText.visibility = View.VISIBLE
                        tv_feedText.text = Constant.decode(listPosts.postDescription)
                        tv_feedText.doResizeTextView(tv_feedText, 3, "...See More", true)

                    } else {
                        tv_feedText.visibility = View.INVISIBLE
                    }

                    var videoPath =
                        if (!listPosts.postSerializedData[0].albummedia.isNullOrEmpty() && !listPosts.postSerializedData[0].albummedia[0].albummediaThumbnail.isNullOrEmpty()) {
                            RestClient.image_base_url_posts + listPosts.postSerializedData[0].albummedia[0].albummediaThumbnail
                        } else {
                            ""
                        }
                    if (!videoPath.isNullOrEmpty()) {
                        img_postImageType?.visibility = View.VISIBLE
                        bluroverlay.visibility = View.VISIBLE
                        img_postImageType1.setImageURI(videoPath)
                        img_postImageType1.visibility = View.VISIBLE
                        img_placeolder.visibility = View.GONE
                        when (listPosts.postMediaType) {
                            "Photo" -> {
                                bluroverlay.visibility = View.VISIBLE
                                img_postImageType1.visibility = View.VISIBLE
                                img_placeolder.visibility = View.GONE
                                img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_photo_nobg)

                            }
                            "Video" -> {
                                bluroverlay.visibility = View.VISIBLE
                                img_postImageType1.visibility = View.VISIBLE
                                img_placeolder.visibility = View.GONE
                                img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_video_nobg)


                            }
                            "Document" -> {
                                bluroverlay.visibility = View.VISIBLE
                                img_postImageType1.visibility = View.VISIBLE
                                img_placeolder.visibility = View.GONE
                                img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_photo_nobg)


                            }
                            "Link" -> {
                                bluroverlay.visibility = View.VISIBLE
                                img_postImageType1.visibility = View.VISIBLE
                                img_placeolder.visibility = View.GONE
                                img_placeolder.setImageResource(R.drawable.small_link_icon)
                                img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_link_nobg)


                            }
                        }

                    } else {
                        when (listPosts.postMediaType) {
                            "Photo" -> {
                                bluroverlay.visibility = View.VISIBLE
                                img_postImageType1.visibility = View.GONE
                                img_placeolder.visibility = View.VISIBLE
                                img_placeolder.setImageResource(R.drawable.create_post_photo_nobg)

                            }
                            "Video" -> {
                                bluroverlay.visibility = View.VISIBLE
                                img_postImageType1.visibility = View.GONE
                                img_placeolder.visibility = View.VISIBLE
                                img_placeolder.setImageResource(R.drawable.create_post_video_nobg)


                            }
                            "Document" -> {
                                bluroverlay.visibility = View.VISIBLE
                                img_postImageType1.visibility = View.GONE
                                img_placeolder.visibility = View.VISIBLE
                                img_placeolder.setImageResource(R.drawable.create_post_photo_nobg)

                            }
                            "Link" -> {
                                bluroverlay.visibility = View.VISIBLE
                                img_postImageType1.visibility = View.GONE
                                img_placeolder.visibility = View.VISIBLE
                                img_placeolder.setImageResource(R.drawable.create_post_link_nobg)


                            }
                        }
                    }

                }
                itemView.setOnClickListener {
                    onItemClick.onClicled(position, "All")
                }
            }
    }

    class PostsLinkViewHolder(itemView: View, context: Activity) :
        RecyclerView.ViewHolder(itemView) {
        var postprocessor: BlurPostprocessor? = null

        fun bind(listPosts: CreatePostData?, position: Int, onItemClick: OnItemClick) =
            with(itemView) {

                if (listPosts != null) {
                    if (!listPosts.postSerializedData[0].albummedia.isNullOrEmpty()) {
                        if (!listPosts.postSerializedData[0].albummedia[0].albummediaFile.isNullOrEmpty()) {
                            img_postImageType1.setImageURI(RestClient.image_base_url_posts + listPosts.postSerializedData[0].albummedia[0].albummediaFile)
                            tv_feedType.text = listPosts.postMediaType
                            img_postImageType1.visibility = View.VISIBLE
                            img_placeolder.visibility = View.GONE
                            img_postImageType?.visibility = View.VISIBLE
                            bluroverlay.visibility = View.VISIBLE
                            postprocessor = BlurPostprocessor(context, 25, 10)
                            try {
                                val request = ImageRequestBuilder.newBuilderWithSource(
                                    Uri.parse(
                                        RestClient.image_base_url_posts + listPosts.postSerializedData.get(
                                            0
                                        ).albummedia.get(0).albummediaFile
                                    )
                                )
                                    .setPostprocessor(postprocessor)
                                    .build()
                                val controller = Fresco.newDraweeControllerBuilder()
                                    .setImageRequest(request)
                                    .setOldController(img_postImageType?.controller)
                                    .build() as PipelineDraweeController
                                img_postImageType?.controller = controller
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            when (listPosts.postMediaType) {
                                "Photo" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.VISIBLE
                                    img_placeolder.visibility = View.GONE
                                    img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_photo_nobg)

                                }
                                "Video" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.VISIBLE
                                    img_placeolder.visibility = View.GONE
                                    img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_video_nobg)


                                }
                                "Document" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.VISIBLE
                                    img_placeolder.visibility = View.GONE
                                    img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_photo_nobg)


                                }
                                "Link" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.VISIBLE
                                    img_placeolder.visibility = View.GONE
                                    img_placeolder.setImageResource(R.drawable.small_link_icon)
                                    img_postImageType1.hierarchy.setPlaceholderImage(R.drawable.create_post_link_nobg)


                                }
                            }

                        } else {
                            when (listPosts.postMediaType) {
                                "Photo" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.GONE
                                    img_placeolder.visibility = View.VISIBLE
                                    img_placeolder.setImageResource(R.drawable.create_post_photo_nobg)

                                }
                                "Video" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.GONE
                                    img_placeolder.visibility = View.VISIBLE
                                    img_placeolder.setImageResource(R.drawable.create_post_video_nobg)


                                }
                                "Document" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.GONE
                                    img_placeolder.visibility = View.VISIBLE
                                    img_placeolder.setImageResource(R.drawable.create_post_photo_nobg)

                                }
                                "Link" -> {
                                    bluroverlay.visibility = View.VISIBLE
                                    img_postImageType1.visibility = View.GONE
                                    img_placeolder.visibility = View.VISIBLE
                                    img_placeolder.setImageResource(R.drawable.create_post_link_nobg)


                                }
                            }

                        }
                    }

                    icon_feedType.setImageResource(R.drawable.small_link_icon)
                    tv_feedType.text = listPosts.postMediaType
                    if (!listPosts.postDescription.isNullOrEmpty()) {
                        tv_feedText.visibility = View.VISIBLE
                        var des = listPosts.postDescription?.split("||")?.toTypedArray()
                        val postdescription: String = if (!des.isNullOrEmpty()) {
                            Constant.decode(des!![0])
                        } else {
                            ""
                        }
                        if (!postdescription.isNullOrEmpty()) {
                            tv_feedText.displayFulltext(postdescription)
                            tv_feedText?.visibility = View.VISIBLE

                        } else {
                            tv_feedText?.visibility = View.GONE
                        }


                        tv_feedText.doResizeTextView(tv_feedText, 10, "...See More", true)
                    } else {
                        tv_feedText.visibility = View.INVISIBLE
                    }
                }
                itemView.setOnClickListener {
                    onItemClick.onClicled(position, "All")
                }
            }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)

    }
}