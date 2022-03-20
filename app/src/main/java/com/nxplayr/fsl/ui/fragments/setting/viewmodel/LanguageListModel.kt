package com.nxplayr.fsl.ui.fragments.setting.viewmodel

import android.app.Dialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LanguageListModel :ViewModel() {

    lateinit var languageresponse: LiveData<List<String>>
    lateinit var mContext: Context
    lateinit var pbDialog: Dialog
    var isChecked: Boolean = false
    var json: String = ""


    fun  getLanguageList(
            context: Context,
            isShowing: Boolean,
            json: String): LiveData<List<String>> {
        this.json = json

        this.mContext = context

        languageresponse = getNotificationListApi()

        return languageresponse

    }

    private fun getNotificationListApi(): LiveData<List<String>> {
        val data = MutableLiveData<List<String>>()



/*

        var call = RestClient.get()!!.languagelist(json)
        call.enqueue(object : RestCallback<List<LanguagelistPojo>>(mContext) {
            override fun Success(response: Response<List<LanguagelistPojo>>) {
                data.value=response.body()
            }

            override fun failure() {
                data.value=null
            }

        })
*/





        return data
    }
    private fun closePb() {
        pbDialog.dismiss()
    }
    private fun showPb() {
        pbDialog = Dialog(mContext)
        pbDialog.show()

    }


}