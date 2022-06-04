package com.nxplayr.fsl.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.nxplayr.fsl.data.api.RestCallback
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.UploadImagePojo
import com.nxplayr.fsl.util.aws.S3Uploader
import id.zelory.compressor.Compressor
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

class UploadLinkPhoto(
    var mActivity: Activity,
    imageList: List<CreatePostPhotoPojo>?,
    fileName: String,
    view: View?,
    var onsuccess: OnSuccess,
    var from: String
) {
    var imageList: List<CreatePostPhotoPojo>? = ArrayList()
    var i = 0
    var fileNameStr: String
    var isRunning = false
    var onSuccess: OnSuccess? = null
    var s3uploaderObj: S3Uploader? = null


    init {
        this.imageList = imageList!!
        fileNameStr = fileName
        this.onSuccess = onsuccess
        s3uploaderObj = S3Uploader(mActivity)
        MyAsyncTask(onSuccess!!, imageList, fileNameStr, mActivity, from).execute()
    }


    interface OnSuccess {
        fun onSuccessUpload(datumList: List<CreatePostPhotoPojo>?)
        fun onFailureUpload(msg: String?, datumList: List<CreatePostPhotoPojo>?)
    }

    inner class MyAsyncTask(
        val onSuccess: OnSuccess,
        val imageList: List<CreatePostPhotoPojo?>?,
        val fileNameStr: String,
        val mActivity: Activity,
        val from: String

    ) : AsyncTask<Void?, Void?, Void?>() {

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

            val folderName = "post/" + fileNameStr
            val filePath = imageList?.get(0)!!.imagePath

            s3uploaderObj!!.initUpload(filePath, folderName, "image")
            s3uploaderObj!!.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
                override fun onUploadSuccess(response: String?) {
                    Log.d("setOns3UploadDone", "VIDEO THUMB = $folderName")
                    Log.d("setOns3UploadDone", "VIDEO THUMB = $response")
                    if (response.equals("Success", ignoreCase = true)) {
                        onSuccess.onSuccessUpload(imageList as List<CreatePostPhotoPojo>?)
                    }
                }

                override fun onUploadError(response: String?) {
                    Log.e("S3Uploader", "Error: $response")
                    onSuccess.onFailureUpload(
                        response,
                        (imageList as List<CreatePostPhotoPojo>?)!!
                    )

                }
            })


//            val call = RestClient.get()!!.uploadAttachment(filePart, RequestBody.create("text/plain".toMediaTypeOrNull(), "post"), RequestBody.create("text/plain".toMediaTypeOrNull(), jsonArray.toString()))
//            call.enqueue(object : RestCallback<List<UploadImagePojo>>(mActivity) {
//                override fun Success(response: Response<List<UploadImagePojo>>) {
//                    Log.e("response", "data" + response.body().toString())
//                    if (compressedImage != null)
//                        compressedImage!!.delete()
//                    if (response.body() != null && response.body()!!.isNotEmpty()) {
//                        if (response.body()!![0].status.toString().equals("true", true)) {
//
//                            if (onSuccess != null) {
//                                onSuccess!!.onSuccessUpload(imageList as List<CreatePostPhotoPojo>?)
//                            }
//                        } else {
//                            if (onSuccess != null) {
//                                onSuccess!!.onFailureUpload(response.body()!![0].message!!, (imageList as List<CreatePostPhotoPojo>?)!!)
//                            }
//                        }
//                    } else {
//
//
//                        if (onSuccess != null)
//                            onSuccess!!.onFailureUpload("", null)
//                    }                }
//
//                override fun failure() {
//                    if (compressedImage != null)
//                        compressedImage!!.delete()
//                    if (mActivity != null)
//                        onSuccess?.onFailureUpload("", null)                }
//            })

            return null
        }

        public fun compressFiles(): File {
            var file: File? = null
            for (i in imageList?.indices!!) {
                if (!imageList!![i]!!.isCompress) {
                    if (
                        from.equals("Link", ignoreCase = true)
                        || from.equals("Video", ignoreCase = true)
                        || from.equals("Photo", ignoreCase = true)
                        || from.equals("Document", ignoreCase = true)
                    ) {
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        val file1 = File(imageList[i]!!.imagePath.toString())
                        try {
                            file = Compressor(mActivity)
                                .setQuality(90)
                                .setMaxHeight(225)
                                .setMaxWidth(720)
                                .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                .compressToFile(file1)
                            BitmapFactory.decodeFile(file.absolutePath, options)
                            val imageHeight = options.outHeight
                            val imageWidth = options.outWidth
                            imageList[i]!!.width = imageWidth
                            imageList[i]!!.height = imageHeight
                            val fileName =
                                MyUtils.createFileName(
                                    Date(),
                                    "Thumb",
                                    imageHeight, imageWidth
                                )

                            Log.e(
                                "fileName",
                                fileName + "  " + mActivity.baseContext.cacheDir.absolutePath
                            )
                            imageList[i]!!.imageName = (fileName!!)
                            imageList[i]!!.imagePath = file.absolutePath
                            imageList[i]!!.isCompress = true
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }
                } else {
                    file = null
                }
            }

            return file!!
        }

    }


}