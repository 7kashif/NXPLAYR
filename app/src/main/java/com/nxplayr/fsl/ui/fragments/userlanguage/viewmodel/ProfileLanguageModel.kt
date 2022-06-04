package com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.fragments.userlanguage.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileLanguageModel : ViewModel() {

    var profileRepo = ProfileRepository()
    val successProfile = profileRepo.successProfile
    val failure = profileRepo.failure

    fun profileApi(json: String, from: String) {
        viewModelScope.launch(Dispatchers.IO) {
            profileRepo.profileApi(json, from)
        }
    }
}