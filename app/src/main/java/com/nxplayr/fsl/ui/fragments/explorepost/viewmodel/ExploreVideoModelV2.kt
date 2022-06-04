package com.nxplayr.fsl.ui.fragments.explorepost.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.fragments.explorepost.repository.ExploreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExploreVideoModelV2 : ViewModel() {

    private val exploreRepository = ExploreRepository()
    val exploreSuccessLiveData = exploreRepository.exploreSuccessLiveData
    val bannerSuccessLiveData = exploreRepository.bannerSuccessLiveData

    fun getVideos(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            exploreRepository.getVideos(json)
        }
    }

    fun getBanners(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            exploreRepository.getBanners(json)
        }
    }
}