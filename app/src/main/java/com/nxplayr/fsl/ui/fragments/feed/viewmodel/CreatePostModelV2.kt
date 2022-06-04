package com.nxplayr.fsl.ui.fragments.feed.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.fragments.feed.repository.FeedRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePostModelV2 : ViewModel() {
    private val feedRepository = FeedRepository()
    val postSuccessLiveData = feedRepository.postSuccessLiveData
    val postFailureLiveData = feedRepository.postFailureLiveData

    fun postFunction(json: String, from: String) {
        viewModelScope.launch(Dispatchers.IO) {
            feedRepository.getPostList(json, from)
        }
    }
}