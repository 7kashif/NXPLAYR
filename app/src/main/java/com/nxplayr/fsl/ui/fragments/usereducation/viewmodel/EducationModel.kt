package com.nxplayr.fsl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.fragments.usereducation.repository.EducationRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EducationModel : ViewModel() {

    var educationRepo = EducationRepo()
    val successEducation = educationRepo.successEducation
    val failure = educationRepo.failure

    fun educationApi(json: String, from: String) {
        viewModelScope.launch(Dispatchers.IO) {
            educationRepo.educationApi(json, from)
        }
    }
}