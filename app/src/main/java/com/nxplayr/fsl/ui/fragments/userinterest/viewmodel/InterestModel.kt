package com.nxplayr.fsl.ui.fragments.userinterest.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.HobbiesListPojo
import com.nxplayr.fsl.data.model.Interest.InterestResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class InterestModel : ViewModel() {

    lateinit var interestResponse: LiveData<List<InterestResponse>>
    lateinit var mContext: Context
    var json: String? = null

    fun getUserInterest(
        context: Context,
        isShowing: Boolean,
        json: String
    ): LiveData<List<InterestResponse>> {
        this.json = json
        this.mContext = context
        interestResponse = getUserInterestListApi()
        return interestResponse
    }

    private fun getUserInterestListApi(): LiveData<List<InterestResponse>> {
        val data = MutableLiveData<List<InterestResponse>>()
        viewModelScope.launch(Dispatchers.IO) {
//            var call = RestClient.get()!!.userInterest(json!!)
//            call!!.enqueue(object : RestCallback<List<InterestResponse>>(mContext) {
//                override fun Success(response: Response<List<InterestResponse>>) {
//                    data.value = response.body()
//                }
//
//                override fun failure() {
//                    data.value = null
//                }
//            })
        }
        return data
    }

    fun userInterest(
        context: Context,
        json: String
    ): LiveData<List<InterestResponse>> {
        this.json = json
        this.mContext = context
        interestResponse = userInterest(json)
        return interestResponse
    }

    private fun userInterest(json: String): LiveData<List<InterestResponse>> {
        val data = MutableLiveData<List<InterestResponse>>()
        viewModelScope.launch(Dispatchers.IO) {
//            var call = RestClient.get()!!.userInterestFollowUnFollow(json)
//            call!!.enqueue(object : RestCallback<List<InterestResponse>>(mContext) {
//                override fun Success(response: Response<List<InterestResponse>>) {
//                    data.value = response.body()
//                }
//
//                override fun failure() {
//                    data.value = null
//                }
//            })
        }
        return data
    }

}
