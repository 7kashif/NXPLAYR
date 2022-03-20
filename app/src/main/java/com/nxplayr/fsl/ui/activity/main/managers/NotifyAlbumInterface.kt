package com.nxplayr.fsl.ui.activity.main.managers

import com.nxplayr.fsl.data.model.CreatePostData

/**
 * Created by dhavalkaka on 29/03/2018.
 */
interface NotifyAlbumInterface {
    fun AlbumNotifyData(postId: String, userId: String, from: String, explore_video_list: ArrayList<CreatePostData?>?, postType: String)
}