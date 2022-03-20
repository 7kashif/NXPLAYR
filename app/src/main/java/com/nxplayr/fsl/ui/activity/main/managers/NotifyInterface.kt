package com.nxplayr.fsl.ui.activity.main.managers

import com.nxplayr.fsl.data.model.CreatePostData


/**
 * Created by dhavalkaka on 29/03/2018.
 */
interface NotifyInterface {
    fun notifyData(
        feedDatum: CreatePostData?,
        isDelete: Boolean,
        isDeleteComment: Boolean,
        postComment: String?,
        commentId:String?
    )
}