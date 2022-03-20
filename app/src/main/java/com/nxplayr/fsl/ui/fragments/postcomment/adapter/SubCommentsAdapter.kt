package com.nxplayr.fsl.ui.fragments.postcomment.adapter

import android.content.Context
import android.graphics.Point
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.Postcommentreply
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.subcommentslayout.view.commentLikeImg
import kotlinx.android.synthetic.main.subcommentslayout.view.img_like
import kotlinx.android.synthetic.main.subcommentslayout.view.imv_user_dp_comment
import kotlinx.android.synthetic.main.subcommentslayout.view.ivlogoWaterMark
import kotlinx.android.synthetic.main.subcommentslayout.view.tvCommentTime
import kotlinx.android.synthetic.main.subcommentslayout.view.tvCommentuser
import kotlinx.android.synthetic.main.subcommentslayout.view.tvCommentuserName
import kotlinx.android.synthetic.main.subcommentslayout.view.tv_like_count
import kotlinx.android.synthetic.main.subcommentslayout.view.tv_reply_comment

/**
 * Created by ADMIN on 20/01/2018.
 */
class SubCommentsAdapter(
    val context: Context,
    val arrayData: ArrayList<Postcommentreply>,
    val onItemClick: OnItemClick,
    val type: String

) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var widthNew = 0
    var heightNew = 0
    var userID = ""
    var sessionManager: SessionManager? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)

            return LoaderViewHolder(view)

        } else {
            val v = LayoutInflater.from(parent.context)
                    .inflate(R.layout.subcommentslayout, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder
            // getScrennwidth()

            if (sessionManager != null && sessionManager?.isLoggedIn()!!) {
                userID = sessionManager?.get_Authenticate_User()?.userID!!
            }

            if (arrayData?.get(holder1.adapterPosition) != null) {
                holder1.bind(
                        holder.adapterPosition,
                        onItemClick,
                        widthNew,
                        heightNew,
                        arrayData[holder1.adapterPosition]!!,
                        userID

                )
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayData?.size!!
    }

    /*override fun getItemViewType(position: Int): Int {
        return if (orderdetails[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }*/

    inner class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {

        init {
            sessionManager = SessionManager(context)

        }

        fun bind(
            position: Int,
            onitemClick: OnItemClick,
            widthNew: Int,
            heightNew: Int,
            data: Postcommentreply,
            userID: String

        ) =
                with(itemView) {
                    var imgUri = ""
                    if (!data.userProfilePicture.isNullOrEmpty()) {
                        imgUri = RestClient.image_base_url_users + data.userProfilePicture
                    }
                    imv_user_dp_comment.setImageURI(Uri.parse(imgUri))

                    var userName = ""
                    if (!data.userFirstName.isNullOrEmpty() && !data.userLastName.isNullOrEmpty()) {
                        userName = data.userFirstName + " " + data.userLastName
                    } else if (!data.userFirstName.isNullOrEmpty() && data.userLastName.isNullOrEmpty()) {
                        userName = data.userFirstName + ""
                    } else if (data.userFirstName.isNullOrEmpty() && !data.userLastName.isNullOrEmpty()) {
                        userName = data.userLastName + ""
                    }
                    tvCommentuserName.text = userName

                    img_like.setOnClickListener {
                        if (position > -1)
                            onitemClick.onClicklisneter(
                                position,
                                "3",
                                img_like,
                                data.commentreplyID,
                                data.commentreplyReply
                            )
                    }


                    tv_reply_comment.setOnClickListener {
                        if (position > -1)
                            onitemClick.onClicklisneter(
                                position,
                                "4",
                                img_like,
                                data.commentreplyID,
                                userName
                            )
                    }
                    commentLikeImg.setOnClickListener {
                        tv_reply_comment.performClick()
                    }

                    if (!data.minutesAgo.isNullOrEmpty()) {
                        tvCommentTime.text =
                            MyUtils.getDisplayableTimeComment(data.minutesAgo.toLong())
                    }



                    if (!data.commentreplyReply.isNullOrEmpty()) {
                        tvCommentuser.text = Constant.decode(data.commentreplyReply)
                    }

                    if (data.isCommentLiked.equals("Yes", false)) {
                        img_like.setImageResource(R.drawable.comment_like_icon_selected)
                    } else {
                        img_like.setImageResource(R.drawable.comment_like_icon_unselected)

                    }

                    if (data.replylikedCount?.toInt()!! > 0) {
                        tv_like_count.visibility = View.VISIBLE
                        tv_like_count.text = " ${data.replylikedCount} Likes"

                    } else {
                        tv_like_count.visibility = View.GONE

                    }

                    if (userID.equals(data.userID, false)) {
                        ivlogoWaterMark.visibility = View.GONE
                    } else {
                        ivlogoWaterMark.visibility = View.VISIBLE

                    }

                    ivlogoWaterMark.setOnClickListener {
                    }
                }
    }


    fun showDotMenu(v: View) {
        //init the wrapper with style

    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, actionType: String, v: View, commentReplyId: String, commentReply: String)
    }

    private fun getScrennwidth(): Int {

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val size = Point()
        display.getSize(size)
        val width = size.x
        val height = size.y

        widthNew = (width / 3).toInt()
        heightNew = (height / 4).toInt()

        return height
    }
}