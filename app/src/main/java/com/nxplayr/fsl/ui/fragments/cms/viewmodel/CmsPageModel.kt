package com.nxplayr.fsl.ui.fragments.cms.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CmsPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response

class CmsPageModel : ViewModel() {

    lateinit var response: LiveData<List<CmsPojo>?>
    lateinit var mContext: Context
    var isShowing: Boolean = false
    var userID: String = ""
    var cmspageConstantCode: String = ""
    var searchkeyword: String = ""

    fun getCmsPage(context: Context, userID: String, cmspageConstantCode: String): LiveData<List<CmsPojo>?> {
        this.userID = userID
        this.cmspageConstantCode = cmspageConstantCode
        this.mContext = context
        response = getApiResponse()
        return response
    }

    private fun getApiResponse(): LiveData<List<CmsPojo>?> {
        val data = MutableLiveData<List<CmsPojo>>()

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("cmspageName", cmspageConstantCode)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        viewModelScope.launch(Dispatchers.IO) {
            var call = RestClient.get()!!.cmsPageContent(jsonArray.toString())
            call.enqueue(object : RestCallback<List<CmsPojo>?>(mContext) {
                override fun Success(response: Response<List<CmsPojo>?>) {
                    data.value = response.body()
                }

                override fun failure() {
                    data.value = null
                }

            })
        }
        return data
    }


}