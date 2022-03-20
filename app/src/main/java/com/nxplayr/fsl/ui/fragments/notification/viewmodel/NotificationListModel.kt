package com.nxplayr.fsl.ui.fragments.notification.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.NotificationPojo
import retrofit2.Response

class NotificationListModel : ViewModel() {

    var languageresponse: LiveData<List<NotificationPojo>>? = null
    lateinit var mContext: Context
    var json: String? = null

    fun getNotificationList(
            context: Context,
            json: String
    ): LiveData<List<NotificationPojo>> {
        this.json = json
        this.mContext = context
        languageresponse = getNotiicationListApi()
        return languageresponse!!
    }

    private fun getNotiicationListApi(): LiveData<List<NotificationPojo>> {
        val data = MutableLiveData<List<NotificationPojo>>()

        var call = RestClient.get()!!.getNotificationList(json!!)
        call!!.enqueue(object : RestCallback<List<NotificationPojo>>(mContext) {
            override fun Success(response: Response<List<NotificationPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
