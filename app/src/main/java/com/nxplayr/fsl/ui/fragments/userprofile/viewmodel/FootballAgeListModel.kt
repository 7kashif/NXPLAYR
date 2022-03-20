package com.nxplayr.fsl.ui.fragments.userprofile.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FootballAgeCategoryPojo
import retrofit2.Response

class FootballAgeListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<FootballAgeCategoryPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getFootballAgeList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<FootballAgeCategoryPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getFootballListApi()

        return languageresponse
    }

    private fun getFootballListApi(): LiveData<List<FootballAgeCategoryPojo>> {
        val data = MutableLiveData<List<FootballAgeCategoryPojo>>()

        var call = RestClient.get()!!.getFootballAgeCategory(json!!)
        call!!.enqueue(object : RestCallback<List<FootballAgeCategoryPojo>>(mContext) {
            override fun Success(response: Response<List<FootballAgeCategoryPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
