package com.nxplayr.fsl.ui.fragments.setting.viewmodel


import android.app.Dialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.LanguageLabelPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class LanguageLabelModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<LanguageLabelPojo>>
    lateinit var mContext: Context

    var isShowing: Boolean = false
    lateinit var pbDialog: Dialog
    var json: String = ""


    fun getLanguageList(
        context: Context,
        isShowing: Boolean,
        json: String
    ): LiveData<List<LanguageLabelPojo>> {
        this.json = json

        this.mContext = context
        this.isShowing = isShowing
        languageresponse = getNotificationListApi()

        return languageresponse
    }

    private fun getNotificationListApi(): LiveData<List<LanguageLabelPojo>> {

        if (isShowing)
            showPb()
        val data = MutableLiveData<List<LanguageLabelPojo>>()

        viewModelScope.launch(Dispatchers.IO) {
//            var call = RestClient.get()!!.languageLabel(json)
//            call.enqueue(object : RestCallback<List<LanguageLabelPojo>>(mContext) {
//                override fun Success(response: Response<List<LanguageLabelPojo>>) {
//
//                    closePb()
//                    if (mContext != null && !response.body().isNullOrEmpty() && !response.body()
//                            .isNullOrEmpty()
//                    ) {
//
//                        data.value = response.body()
//                    } else
//                        data.value = null
//
//                }
//
//                override fun failure() {
//                    closePb()
//                    data.value = null
//                }
//
//            })
        }
        return data
    }

    private fun closePb() {
        try {
            if (pbDialog != null && pbDialog.isShowing)
                pbDialog.dismiss()
        } catch (e: Exception) {
        }
    }

    private fun showPb() {
        try {
            pbDialog = Dialog(mContext)
            pbDialog.show()
        } catch (e: Exception) {
        }
    }

}


