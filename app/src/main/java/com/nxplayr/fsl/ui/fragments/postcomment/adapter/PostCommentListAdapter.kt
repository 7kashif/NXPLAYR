package com.nxplayr.fsl.ui.fragments.postcomment.adapter

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CommentData
import com.nxplayr.fsl.data.model.Postcommentreply
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_postcomment_list.view.*

class PostCommentListAdapter(
    val context: Activity,
    val arrayData: ArrayList<CommentData?>,
    val onItemClick: OnItemClick,
    val type: String,
    val arrayReplyComment: ArrayList<Postcommentreply?>

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
                    .inflate(R.layout.item_postcomment_list, parent, false)
            return ViewHolder(v, context, arrayReplyComment)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder
            // getScrennwidth()

            sessionManager = SessionManager(context)
            if (sessionManager != null && sessionManager?.isLoggedIn()!!) {
                userID = sessionManager?.get_Authenticate_User()?.userID!!
            }

            if (arrayData[holder1.adapterPosition] != null) {
                holder1.bind(
                        holder.adapterPosition,
                        onItemClick,
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
        return arrayData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (arrayData[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ViewHolder(
            itemView: View,
            context: Activity,
            arrayReplyComment: ArrayList<Postcommentreply?>
    ) : RecyclerView.ViewHolder(itemView) {

        var progressComment = itemView.progressComment
        var tvCommenViewAll = itemView.tvCommenViewAll
        var rvViewAllComment = itemView.rvViewAllComment

        fun bind(
            position: Int,
            onitemClick: OnItemClick,
            onClickListner: OnItemClick,
            widthNew: Int,
            heightNew: Int,
            data: CommentData,
            userID: String) =
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

                    if (!data.minutesAgo.isNullOrEmpty()) {
                        tvCommentTime.text = MyUtils.getDisplayableTimeComment(data.minutesAgo.toLong())
                    }

                    if (!data.commentComment.isNullOrEmpty()) {
                        tvCommentuser.text = Constant.decode(data.commentComment)
                    }


                    if (data.postcommentLike.isNullOrEmpty()) {
                        tv_like_count.text = "0"
                    } else {
                        tv_like_count.text = java.lang.Integer.valueOf(data.postcommentLike).toString()
                    }
                    if (data.isCommentLiked.equals("Yes", false)) {
                        img_like.setImageResource(R.drawable.comment_like_icon_selected)
                    } else {
                        img_like.setImageResource(R.drawable.comment_like_icon_unselected)

                    }
                    if (data.postcommentLike.toInt() > 0)
                    {
                        tv_like_count.visibility = View.VISIBLE
                        tv_like_count.text = " ${data.postcommentLike} Likes"

                    }
                    else
                    {
                        tv_like_count.visibility = View.GONE

                    }
                    if (data.postcommentreply.size > 0) {
                        tv_reply_comment.visibility = View.VISIBLE
                        tv_reply_comment.text = "${data.postcommentreply.size} Replies"

                    } else {
                        tv_reply_comment.visibility = View.GONE

                    }

                    if (!userID.isNullOrEmpty() && userID.equals(data.userID, true)) {
                        ivlogoWaterMark.visibility=View.GONE

                    } else {
                        ivlogoWaterMark.setImageResource(R.drawable.more_icon)
                        ivlogoWaterMark.visibility=View.VISIBLE
                    }

                    ivlogoWaterMark.setOnClickListener {

                        onClickListner.onClickListner(position, "connectionType",data)
                        if (!userID.isNullOrEmpty() && userID.equals(data.userID, true)) {
                            onitemClick.onClicklisneter(position, 0, data, itemView, data.commentID, data.commentComment, -1)
                        } else {
                        }
                    }

                    img_like.setOnClickListener {
                        if (position > -1)
                            onitemClick.onClicklisneter(position, 3, data, img_like, data.commentID, data.commentComment, -1)
                    }


                    tv_reply_comment.setOnClickListener {
                        if (position > -1)
                            onitemClick.onClicklisneter(position, 4, data, img_like, data.commentID, data.commentComment, -1)
                    }
                    commentLikeImg.setOnClickListener {
                        tv_reply_comment.performClick()
                    }


                    if (data.isVisibleComment) {
                        if (!data.postcommentreply.isNullOrEmpty()) {
                            if (data.postcommentreply.size > 10) {
                                tvCommenViewAll.visibility = View.GONE
                                line_bottom.visibility = View.GONE
                            } else {
                                tvCommenViewAll.visibility = View.GONE
                                line_bottom.visibility = View.VISIBLE
                            }

                            var linearLayoutManager = LinearLayoutManager(context!!)
                            subCommentsRecyclerView.layoutManager = linearLayoutManager
                            subCommentsRecyclerView.visibility = View.VISIBLE
                            topbaseline.visibility = View.VISIBLE

                            var commentListAdapter = SubCommentsAdapter(
                                    context, data.postcommentreply,
                                    object : SubCommentsAdapter.OnItemClick {
                                        override fun onClicklisneter(
                                                pos: Int,
                                                actionType: String, v3: View, commentReplyId: String, comment: String
                                        ) {
                                            when (actionType) {

                                                "EditReply" -> {
                                                    onitemClick.onClicklisneter(position, 7, data, v3, commentReplyId, comment, pos)
                                                }
                                                "DeleteReply" -> {
                                                    onitemClick.onClicklisneter(position, 6, data, v3, commentReplyId, comment, pos)

                                                }

                                                "3"->{
                                                    if (position > -1)
                                                        onitemClick.onClicklisneter(position, 8, data, img_like,commentReplyId, comment, pos)

                                                }
                                                "4"->{
                                                    if (position > -1)
                                                        onitemClick.onClicklisneter(position, 9, data, img_like, commentReplyId, comment, pos)

                                                }
                                            }
                                        }

                                    }, ""
                            )
                            subCommentsRecyclerView.adapter = commentListAdapter
                            commentListAdapter.notifyDataSetChanged()

                        } else {
                            tvCommenViewAll.visibility = View.GONE
                            line_bottom.visibility = View.GONE
                            subCommentsRecyclerView.visibility = View.GONE
                            topbaseline.visibility = View.GONE
                        }
                    } else {
                        subCommentsRecyclerView.visibility = View.GONE
                        topbaseline.visibility = View.GONE
                    }

                    tvCommenViewAll.setOnClickListener {
                        tvCommenViewAll.visibility = View.GONE
                        line_bottom.visibility = View.GONE
                        onitemClick.onClicklisneter(position, 5, data, progressComment, data.commentID, data.commentComment, -1)

                    }


                }


    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, actionType: Int, comentObj: CommentData, v: View, commentId: String, comment: String, replyPos: Int)
        fun onClickListner(position: Int, from: String, data: CommentData)
    }

}