package com.nxplayr.fsl.ui.fragments.userpasspassportnationality.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CountryListData
import com.nxplayr.fsl.data.model.PassportNationality
import com.nxplayr.fsl.data.model.PassportNationalityPojo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class AddPassportNationalityCallsViewModel: ViewModel() {
    var context: Activity?=null
    var from: String=""
    var countrylist: ArrayList<CountryListData>?=null
    var json:String=""
     var users :LiveData<List<PassportNationalityPojo>>?=null
    var userId:String=""
    var passport: java.util.ArrayList<PassportNationality>?=null
    fun getPassport(context: Activity, from: String, countrylist: ArrayList<CountryListData>?, userID: String, passport: java.util.ArrayList<PassportNationality>?): LiveData<List<PassportNationalityPojo>> {
        this.from = from
        this.countrylist = countrylist
        this.context = context
        this.userId = userID
        this.passport=passport
        users=fetchUsers()
        return users!!
    }
    private fun fetchUsers() : LiveData<List<PassportNationalityPojo>> {

        val data = MutableLiveData<List<PassportNationalityPojo>>()

        var list:ArrayList<PassportNationalityPojo>?=ArrayList()
        var call: List<PassportNationalityPojo>? = null

        viewModelScope.launch {
           // data.postValue(null)
            try {
                // coroutineScope is needed, else in case of any network error, it will crash
                val waitfor=  CoroutineScope(Dispatchers.IO).async {
                    for(it in 0 until countrylist?.size!!)
                    {
                        val jsonArray = JSONArray()
                        val jsonObject = JSONObject()
                        try {
                            jsonObject.put("apiType", RestClient.apiType)
                            jsonObject.put("apiVersion", RestClient.apiVersion)
                            jsonObject.put("languageID", "1")
                            jsonObject.put("loginuserID", userId)
                            jsonObject.put("countryName", countrylist!![it].countryName)
                            jsonObject.put("countryID", countrylist!![it].countryID)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        Log.d("JSONOBJ_PASSOERT", jsonObject.toString())
                        jsonArray.put(jsonObject)
                        when(from) {
                            "Edit" -> {
                                if(!passport.isNullOrEmpty())
                                {
                                    for (i in 0 until passport?.size!!) {
                                        if ( countrylist!![it].countryID.equals(passport?.get(i)?.countryID, false)) {
                                            jsonObject.put("userpassport", passport?.get(i)?.userpassport)
                                            break
                                        }
                                    }
                                }
                                call = RestClient.get()!!.editUserPassport(jsonArray.toString())
                            }
                            "Delete" -> {
                              //  call = RestClient.get()!!.deleteUserPassport(jsonArray.toString())

                            }
                            "Add"->{
                                call = RestClient.get()!!.addUserPassport(jsonArray.toString())

                            }
                        }
                        list?.addAll(call!!)
                    }
                }
                waitfor.await()

                if(waitfor.isCompleted){
                    data.postValue(list)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return data
    }
}



