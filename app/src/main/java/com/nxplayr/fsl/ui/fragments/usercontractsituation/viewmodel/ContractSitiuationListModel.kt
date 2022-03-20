package com.nxplayr.fsl.ui.fragments.usercontractsituation.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ContractSitiuation
import retrofit2.Response

class ContractSitiuationListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<ContractSitiuation>>
    lateinit var mContext: Context
    var json: String?=null
    var s: String?=null
    fun getContractSitiuationList(
            context: Context,
            isShowing: Boolean,
            json: String,
            s: String
    ): LiveData<List<ContractSitiuation>> {
        this.json = json

        this.mContext = context
        this.s = s

        languageresponse = getContractSitiuationListApi()

        return languageresponse
    }

    private fun getContractSitiuationListApi(): LiveData<List<ContractSitiuation>> {
        val data = MutableLiveData<List<ContractSitiuation>>()

        var call = RestClient.get()!!.getcontractsituationslist(json!!)
        call!!.enqueue(object : RestCallback<List<ContractSitiuation>>(mContext) {
            override fun Success(response: Response<List<ContractSitiuation>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}