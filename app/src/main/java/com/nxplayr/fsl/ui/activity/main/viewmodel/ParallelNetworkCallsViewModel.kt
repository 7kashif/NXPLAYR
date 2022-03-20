package com.nxplayr.fsl.ui.activity.main.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.UploadImagePojo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class ParallelNetworkCallsViewModel: ViewModel() {
    var context: Activity?=null
    var from: String=""
    var filePart: ArrayList<CreatePostPhotoPojo>?=null
    var json:String=""
     var users :LiveData<List<UploadImagePojo>>?=null
    fun getUploadedList(context: Activity, from: String, filePart: ArrayList<CreatePostPhotoPojo>?, json:String): LiveData<List<UploadImagePojo>> {

        this.from = from
        this.filePart = filePart
        this.context = context
        this.json = json
        users=fetchUsers()
        return users!!
    }
    /*init {
        fetchUsers()
    }*/

    private fun fetchUsers() : LiveData<List<UploadImagePojo>> {

         val users = MutableLiveData<List<UploadImagePojo>>()
         var list:ArrayList<UploadImagePojo>?=ArrayList()
       viewModelScope.launch {
            users.postValue(null)
            try {
                // coroutineScope is needed, else in case of any network error, it will crash
                val waitfor=  CoroutineScope(Dispatchers.IO).async {
                     // var usersFromApiDeferred:List<UploadImagePojo>?=ArrayList()
                    for(it in 0 until filePart?.size!!){


                        Log.e("size",""+it.toString())
                        val filePart = MultipartBody.Part.createFormData(
                                "FileField", filePart!![it]!!.imageName,
                                File(filePart!![it]!!.imagePath)?.asRequestBody("image*//*".toMediaTypeOrNull()))
                        list?.addAll(RestClient.get()!!.uploadAttachment1(filePart!!, "post".toRequestBody("text/plain".toMediaTypeOrNull()), json.toString().toRequestBody("text/plain".toMediaTypeOrNull())))
                    }
                }
                waitfor.await()

                if(waitfor.isCompleted){
                    users.postValue(list)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                //users.postValue("Something Went Wrong")
            }
        }
        return users
    }



}

