package com.nxplayr.fsl.ui.fragments.useremployment.repository

import android.util.Log
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CompanyListPojo
import com.nxplayr.fsl.data.model.Employmentpojo
import com.nxplayr.fsl.data.model.JobFunctionPojo
import com.nxplayr.fsl.util.SingleLiveEvent
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class EmpRepo {

    private var response: Response<MutableList<Employmentpojo>>? = null
    private var response2: Response<MutableList<CompanyListPojo>>? = null
    private var response3: Response<MutableList<JobFunctionPojo>>? = null
    private val TAG: String = "TEST API"
    private val apiService = RestClient.get()
    val successEmployee = SingleLiveEvent<MutableList<Employmentpojo>>()
    val successCompany = SingleLiveEvent<MutableList<CompanyListPojo>>()
    val successJob = SingleLiveEvent<MutableList<JobFunctionPojo>>()
    val failure = SingleLiveEvent<String>()

    suspend fun jobApi(json: String) {

        try {
            response3 = apiService?.userJobFunctionListProfile(json)
            Log.d(TAG, "$response3")
            if (response3 != null) {
                if (response3!!.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response3!!.body()}")
                    successJob.postValue(response3!!.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response3!!.body()}")
                    failure.postValue(response3!!.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun companyApi(json: String) {
        try {
            response2 = apiService?.userCompanyListProfile(json)
            Log.d(TAG, "$response2")
            if (response2 != null) {
                if (response2!!.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response2!!.body()}")
                    successCompany.postValue(response2!!.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response2!!.body()}")
                    failure.postValue(response2!!.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }

    suspend fun employeeApi(json: String, from: String) {
        try {
            when (from) {
                "Add" -> {
                    response = apiService?.userAddEmployementProfile(json)
                }
                "Edit" -> {
                    response = apiService?.userEditEmployementProfile(json)
                }
                "Delete" -> {
                    response = apiService?.userDeleteEmployementProfile(json)
                }
                "List" -> {
                    response = apiService?.userListEmployementProfile(json)
                }
            }
            Log.d(TAG, "$response")
            if (response != null) {
                if (response!!.isSuccessful) {
                    Log.d(TAG, "SUCCESS")
                    Log.d(TAG, "${response!!.body()}")
                    successEmployee.postValue(response!!.body())
                } else {
                    Log.d(TAG, "FAILURE")
                    Log.d(TAG, "${response!!.body()}")
                    failure.postValue(response!!.message())
                }
            }
        } catch (e: UnknownHostException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: SocketTimeoutException) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        } catch (e: Exception) {
            e.message?.let { Log.e(TAG, it) }
            failure.postValue(e.message)
        }
    }
}