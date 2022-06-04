package com.nxplayr.fsl.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.ui.fragments.useremployment.repository.EmpRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmployementModel : ViewModel() {

    private val empRepo = EmpRepo()
    val successEmployee = empRepo.successEmployee
    val successCompany = empRepo.successCompany
    val successJob = empRepo.successJob
    val failure = empRepo.failure

    fun employeeApi(json: String, from: String) {
        viewModelScope.launch(Dispatchers.IO) {
            empRepo.employeeApi(json, from)
        }
    }

    fun companyApi(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            empRepo.companyApi(json)
        }
    }

    fun jobApi(json: String) {
        viewModelScope.launch(Dispatchers.IO) {
            empRepo.jobApi(json)
        }
    }
}