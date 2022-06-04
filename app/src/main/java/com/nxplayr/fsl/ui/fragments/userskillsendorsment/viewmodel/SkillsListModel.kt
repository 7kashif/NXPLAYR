package com.nxplayr.fsl.ui.fragments.userskillsendorsment.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SkillListPojo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class SkillsListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<SkillListPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getSkillsList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<SkillListPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getSkillsListApi()

        return languageresponse
    }

    private fun getSkillsListApi(): LiveData<List<SkillListPojo>> {
        val data = MutableLiveData<List<SkillListPojo>>()
        viewModelScope.launch(Dispatchers.IO) {
            var call = RestClient.get()!!.userSkillListProfile(json!!)
            call!!.enqueue(object : RestCallback<List<SkillListPojo>>(mContext) {
                override fun Success(response: Response<List<SkillListPojo>>) {
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
