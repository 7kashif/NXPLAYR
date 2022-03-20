package com.nxplayr.fsl.util

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.UploadImagePojo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*

class UploadDocument(
    var mActivity: Activity,
    imageList: List<CreatePostPhotoPojo>?,
    userID: String,
    view: View?,
    var onsuccess: OnSuccess,
    var from: String
) {
    var imageList: List<CreatePostPhotoPojo>? = ArrayList()
    var i = 0
    var userId: String
    var isRunning = false
    var onSuccess: OnSuccess? = null


    init {
        this.imageList = imageList!!
        userId = userID
        this.onSuccess = onsuccess
        MyAsyncTask(onSuccess!!, imageList, userId, mActivity, from).execute()

    }


    interface OnSuccess {
        fun onSuccessUpload(datumList: List<CreatePostPhotoPojo>?)
        fun onFailureUpload(msg: String?, datumList: List<CreatePostPhotoPojo>?)
    }

    internal class MyAsyncTask(
        val onSuccess: OnSuccess,
        val imageList: List<CreatePostPhotoPojo?>?,
        val userId: String,
        val mActivity: Activity,
        val from: String

    ) : AsyncTask<Void?, Void?, Void?>() {
        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

        }

        override fun onPreExecute() {
            super.onPreExecute()

        }

        override fun doInBackground(vararg params: Void?): Void? {

            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            try {
                jsonObject.put("loginuserID", "0")
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            jsonArray.put(jsonObject)
            var compressedImage = compressFiles()
            Log.e("compressedImage", compressedImage.toString())
            val filePart = MultipartBody.Part.createFormData(
                    "FileField",imageList!![0]!!.imageName,
                    RequestBody.create("image*//*".toMediaTypeOrNull(), compressedImage!!)
            )
            val call = RestClient.get()!!.uploadAttachment(filePart, RequestBody.create("text/plain".toMediaTypeOrNull(), "post"), RequestBody.create("text/plain".toMediaTypeOrNull(), jsonArray.toString()))
            call.enqueue(object : RestCallback<List<UploadImagePojo>>(mActivity) {
                override fun Success(response: Response<List<UploadImagePojo>>) {
                    Log.e("response", "data" + response.body().toString())
                    if (compressedImage != null)
                        compressedImage!!.delete()
                    if (response.body() != null && response.body()!!.isNotEmpty()) {
                        if (response.body()!![0].status.toString().equals("true", true)) {

                            if (onSuccess != null) {
                                onSuccess!!.onSuccessUpload(imageList as List<CreatePostPhotoPojo>?)
                            }
                        } else {
                            if (onSuccess != null) {
                                onSuccess!!.onFailureUpload(response.body()!![0].message!!, (imageList as List<CreatePostPhotoPojo>?)!!)
                            }
                        }
                    } else {


                        if (onSuccess != null)
                            onSuccess!!.onFailureUpload("", null)
                    }                }

                override fun failure() {
                    if (compressedImage != null)
                        compressedImage!!.delete()
                    if (mActivity != null)
                        onSuccess?.onFailureUpload("", null)                }
            })

            return null
        }
        public fun compressFiles(): File {
            var file1: File? = null
            for (i in imageList?.indices!!) {
                if (!imageList[i]!!.isCompress) {
                    if (from.equals("Document", ignoreCase = true)) {

                         file1 = File(imageList[i]!!.imagePath.toString())
                        try {
                            Log.e("fileName", imageList[i]!!.imageName + "  " + mActivity.baseContext.cacheDir.absolutePath)
                            imageList[i]!!.imageName = (imageList[i]!!.imageName!!)
                            imageList[i]!!.imagePath = file1.absolutePath
                            imageList[i]!!.isCompress = true
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    file1 = null
                }
            }

            return file1!!
        }
    }


}