package com.nxplayr.fsl.ui.activity.filterfeed.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.PlayerPositionListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class PlayerPositionModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<PlayerPositionListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getPlayerPosList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<PlayerPositionListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getPlayerPosListApi()

        return languageresponse
    }

    private fun getPlayerPosListApi(): LiveData<List<PlayerPositionListPojo>> {
        val data = MutableLiveData<List<PlayerPositionListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            var call = RestClient.get()!!.playerPositionList(json!!)
            call!!.enqueue(object : RestCallback<List<PlayerPositionListPojo>>(mContext) {
                override fun Success(response: Response<List<PlayerPositionListPojo>>) {
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
