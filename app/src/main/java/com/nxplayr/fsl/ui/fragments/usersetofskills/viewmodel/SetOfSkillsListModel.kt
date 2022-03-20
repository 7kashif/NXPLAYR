package com.nxplayr.fsl.ui.fragments.usersetofskills.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SkillsPojo
import retrofit2.Response

class SetOfSkillsListModel : ViewModel() {

    lateinit var languageresponse: LiveData<List<SkillsPojo>>
    lateinit var mContext: Context
    var json: String? = null

    fun getSkillsList(
            context: Context,
            isShowing: Boolean,
            json: String
    ): LiveData<List<SkillsPojo>> {
        this.json = json

        this.mContext = context

        languageresponse = getSkillsListApi()

        return languageresponse
    }

    private fun getSkillsListApi(): LiveData<List<SkillsPojo>> {
        val data = MutableLiveData<List<SkillsPojo>>()

        var call = RestClient.get()!!.getuserSkillProfile(json!!)
        call!!.enqueue(object : RestCallback<List<SkillsPojo>>(mContext) {
            override fun Success(response: Response<List<SkillsPojo>>) {
                data.value = response.body()
            }

            override fun failure() {
                data.value = null
            }

        })

        return data
    }

}
