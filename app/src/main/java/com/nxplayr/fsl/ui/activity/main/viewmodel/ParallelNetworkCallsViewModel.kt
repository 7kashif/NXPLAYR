package com.nxplayr.fsl.ui.activity.main.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.UploadImagePojo
import com.nxplayr.fsl.util.aws.S3Uploader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ParallelNetworkCallsViewModel : ViewModel() {

    var context: Activity? = null
    var from: String = ""
    var filePart: ArrayList<CreatePostPhotoPojo>? = null
    var json: String = ""
    var users: LiveData<List<UploadImagePojo>>? = null
    var s3uploaderObj: S3Uploader? = null

    fun getUploadedList(
        context: Activity,
        from: String,
        filePart: ArrayList<CreatePostPhotoPojo>?,
        json: String
    ): LiveData<List<UploadImagePojo>> {
        this.from = from
        this.filePart = filePart
        this.context = context
        this.json = json
        users = fetchUsers()
        s3uploaderObj = S3Uploader(context)
        return users!!
    }

    private fun fetchUsers(): LiveData<List<UploadImagePojo>> {

        val users = MutableLiveData<List<UploadImagePojo>>()
        var list: ArrayList<UploadImagePojo>? = ArrayList()
        viewModelScope.launch(Dispatchers.IO) {
            users.postValue(null)
            try {
                // coroutineScope is needed, else in case of any network error, it will crash
                val waitfor = CoroutineScope(Dispatchers.IO).async {
                    for (it in 0 until filePart?.size!!) {

                        val folderName = "post/" + filePart!![it].imageName
                        val filePath = filePart!![it].imagePath

                        Log.e("S3Uploader", "" + it.toString())
                        Log.e("S3Uploader", "folderName : " + folderName)
                        Log.e("S3Uploader", "filePath : " + filePath)

                        s3uploaderObj!!.initUpload(filePath, folderName, "image")
                        s3uploaderObj!!.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
                            override fun onUploadSuccess(response: String?) {
                                if (response.equals("Success", ignoreCase = true)) {
                                    var data = UploadImagePojo()
                                    data.fileName = filePart!![it].imageName
                                    data.status = response
                                    list?.add(data)
                                }
                            }

                            override fun onUploadError(response: String?) {
                                Log.e("S3Uploader", "Error: $response")
                            }
                        })

//                        val filePart =
//                            filePart!![it].imagePath?.let { it1 -> File(it1).asRequestBody("image*//*".toMediaTypeOrNull()) }
//                                ?.let { it2 ->
//                                    MultipartBody.Part.createFormData(
//                                        "FileField", filePart!![it].imageName,
//                                        it2
//                                    )
//                                }
//                        list?.addAll(RestClient.get()!!.uploadAttachment1(filePart!!, "post".toRequestBody("text/plain".toMediaTypeOrNull()), json.toString().toRequestBody("text/plain".toMediaTypeOrNull())))


                    }
                }
                waitfor.await()

                if (waitfor.isCompleted) {
                    users.postValue(list)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return users
    }

}

