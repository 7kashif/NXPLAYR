package com.nxplayr.fsl.ui.fragments.cms.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.FaqCategoryPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class FaqCategoryListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<FaqCategoryPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getFaqCategoryList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<FaqCategoryPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getFaqCategoryListApi()

        return languageresponse
    }

    private fun getFaqCategoryListApi(): LiveData<List<FaqCategoryPojo>> {
        val data = MutableLiveData<List<FaqCategoryPojo>>()

        viewModelScope.launch(Dispatchers.IO) {
        var call = RestClient.get()!!.faqCategoryList(json!!)
        call!!.enqueue(object : RestCallback<List<FaqCategoryPojo>>(mContext) {
            override fun Success(response: Response<List<FaqCategoryPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })}

        return data
    }

}
