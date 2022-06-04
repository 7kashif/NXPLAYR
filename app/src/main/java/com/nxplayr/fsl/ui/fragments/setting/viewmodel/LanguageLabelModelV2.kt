package com.nxplayr.fsl.ui.fragments.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.fragments.setting.repository.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LanguageLabelModelV2 : ViewModel() {

    private val settingRepo = SettingRepository()
    val usersSuccessLiveData = settingRepo.usersSuccessLiveData
    val usersFailureLiveData = settingRepo.usersFailureLiveData

    fun getLabels(json: String){
        viewModelScope.launch(Dispatchers.IO) {
            println("THREAD : I'm working in thread ${Thread.currentThread().name}")
            settingRepo.getLabels(json)
        }
    }
}


