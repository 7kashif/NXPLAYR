package com.nxplayr.fsl.ui.fragments.feed.adapter

import android.app.Activity
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
import com.nxplayr.fsl.data.model.CommentData
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_postcomment_list.view.*

class CommentAdapter(
    val context: Activity,
    val arrayData: ArrayList<CommentData>,
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
                    .inflate(R.layout.item_comment_list, parent, false)
            return ViewHolder(v, context)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ViewHolder) {
            val holder1 = holder as ViewHolder
            // getScrennwidth()

            sessionManager = SessionManager(context)
            if (sessionManager != null && sessionManager?.isLoggedIn()!!) {
                userID = sessionManager?.get_Authenticate_User()?.userID!!
            }

            holder1.bind(
                    holder.adapterPosition,
                    onItemClick,
                    onItemClick,
                    widthNew,
                    heightNew,
                    arrayData[holder1.adapterPosition]!!,
                    userID,arrayData?.size

            )
        }
    }

    override fun getItemCount(): Int {
        var size=arrayData.size
        if(size==1){
            size =1
        }else if(size==2){
            size =2
        }else if(size>2){
            size =2
        }
        return size

    }

    override fun getItemViewType(position: Int): Int {
        return if (arrayData[position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ViewHolder(
            itemView: View,
            context: Activity) : RecyclerView.ViewHolder(itemView) {

        public var progressComment = itemView.progressComment
        public var tvCommenViewAll = itemView.tvCommenViewAll
        public var rvViewAllComment = itemView.rvViewAllComment

        fun bind(
            position: Int,
            onitemClick: OnItemClick,
            onClickListner: OnItemClick,
            widthNew: Int,
            heightNew: Int,
            data: CommentData,
            userID: String,
            size: Int) =
                with(itemView) {

                    var imgUri = ""
                    if (!data.userProfilePicture.isNullOrEmpty()) {
                        imgUri = RestClient.image_base_url_users + data.userProfilePicture
                    }
                    imv_user_dp_comment.setImageURI(Uri.parse(imgUri))

                    var userName = ""

                    if (!data?.userFirstName.isNullOrEmpty() && !data?.userLastName.isNullOrEmpty()) {
                        userName = data?.userFirstName + " " + data?.userLastName
                    } else if (!data?.userFirstName.isNullOrEmpty() && data?.userLastName.isNullOrEmpty()) {
                        userName = data?.userFirstName + ""
                    } else if (data?.userFirstName.isNullOrEmpty() && !data?.userLastName.isNullOrEmpty()) {
                        userName = data?.userLastName + ""
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
                    if (data.postcommentLike.toInt() > 0) {
                        tv_like_count.visibility = View.VISIBLE
                        tv_like_count.text = "${data.postcommentLike} Likes"

                    } else {
                        tv_like_count.visibility = View.GONE

                    }
                    if (data.postcommentreply.size > 0) {
                        tv_reply_comment.visibility = View.VISIBLE
                        tv_reply_comment.text = "${data.postcommentreply.size} Replies"

                    } else {
                        tv_reply_comment.visibility = View.GONE

                    }

                    if (!userID.isNullOrEmpty() && userID.equals(data.userID, true)) {
                        ivlogoWaterMark.setImageResource(R.drawable.more_icon)
                    } else {
                        ivlogoWaterMark.setImageResource(R.drawable.more_icon)
                    }

                    ivlogoWaterMark.setOnClickListener {

                        onClickListner.onClickListner(position, "connectionType",data)
                        if (!userID.isNullOrEmpty() && userID.equals(data.userID, true)) {
                            onitemClick.onClicklisneter(position, 0, data, itemView, data.commentID, data.commentComment, -1)
                        } else {

                            /*val wrapper = ContextThemeWrapper(context, R.style.popmenu_style)
                            //init the popup
                            val popup = PopupMenu(wrapper, itemView.ivlogoWaterMark)

                            *//*  The below code in try catch is responsible to display icons*//*
                        if (true) {
                            try {
                                val fields = popup.javaClass.declaredFields
                                for (field in fields) {
                                    if ("mPopup" == field.name) {
                                        field.isAccessible = true
                                        val menuPopupHelper = field.get(popup)
                                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                                        val setForceIcons =
                                            classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                                        setForceIcons.invoke(menuPopupHelper, true)
                                        break
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        //inflate menu
                        popup.menuInflater.inflate(R.menu.popup_with_image, popup.menu)
                        //implement click events

                        popup.menu.getItem(0).title = "Report Comment"
                        popup.menu.getItem(0).setIcon(R.drawable.report_icon_black)
                        popup.menu.getItem(1).isVisible = false
                        popup.menu.getItem(2).isVisible = false
                        popup.menu.getItem(3).isVisible = false

                        popup.setOnMenuItemClickListener { menuItem ->
                            when (menuItem.itemId) {
                                R.id.edit_profile -> {
                                    onitemClick.onClicklisneter(position, 1, data,itemView,data.commentID,data.commentComment,-1)
                                }
                            }
                            true
                        }
                        popup.show()*/
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


                   // if (data.isVisibleComment) {
                           /* if (size > 2) {
                                tvCommenViewAll.visibility = View.VISIBLE
                                rvViewAllComment.visibility = View.VISIBLE
                                line_bottom.visibility = View.GONE
                            } else {
                                tvCommenViewAll.visibility = View.GONE
                                rvViewAllComment.visibility = View.GONE
                                line_bottom.visibility = View.VISIBLE
                            }*/
                            /* var linearLayoutManager = LinearLayoutManager(context!!)
                             subCommentsRecyclerView.layoutManager = linearLayoutManager
                             subCommentsRecyclerView.visibility = View.VISIBLE
                             topbaseline.visibility = View.VISIBLE

                             var commentListAdapter = SubCommentsAdapter(
                                     context, data?.postcommentreply,
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
                                             }
                                         }

                                     }, ""
                             )
                             subCommentsRecyclerView.adapter = commentListAdapter*/
                    /* }
                else {
                         subCommentsRecyclerView.visibility = View.GONE
                         topbaseline.visibility = View.GONE

                     }*/

                    tvCommenViewAll.setOnClickListener {
                        tvCommenViewAll.visibility = View.GONE
                        line_bottom.visibility = View.GONE
                        onitemClick.onClicklisneter(position, 5, data, progressComment, data.commentID, data.commentComment, -1)

                    }
                    itemView.setOnClickListener {
                        if (position > -1)
                            onitemClick.onClicklisneter(position, 3, data, img_like, data.commentID, data.commentComment, -1)

                    }



                }


    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, actionType: Int, comentObj: CommentData, v: View, commentId: String, comment: String, replyPos: Int)
        fun onClickListner(position: Int, from: String, data: CommentData)
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