package com.nxplayr.fsl.data.model

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.util.Log
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.ProgressHUD
import id.zelory.compressor.Compressor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import java.io.File
import java.util.*


class UploadFileModel {
    var pbDialog: Dialog? = null
    var onUploadFileListener: OnUploadFileListener? = null
    var mActivity: Activity? = null
    var uploadFilePath: String = ""
    var fileType: String = ""
    var isShowPb: Boolean = true
    var compressedImage: File? = null
    var file: File? = null
    var folderName: String = ""
    var json: String = ""
    var isImage:Boolean=true

    fun uploadFile(
        context: Activity,
        json: String, folderName: String,  file: File,isShowPb:Boolean, onUploadFileListener: OnUploadFileListener
    ) {
        this.mActivity = context
        this.onUploadFileListener = onUploadFileListener
        this.json=json
        this.file = file
        this.folderName = folderName
        this.isShowPb = isShowPb
        if (isShowPb) {
            pbDialog =
                ProgressHUD(mActivity!!, R.style.CustomBottomSheetDialogTheme,"")
            pbDialog?.show()
        }

        MyAsyncTask().execute()
    }

    inner class MyAsyncTask : AsyncTask<Void, Void, Boolean>() {


        override fun doInBackground(vararg arg0: Void): Boolean {
            try {
                compressedImage = Compressor(mActivity)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                     .compressToFile(file, "IMG.png")
            } catch (e: Exception) {
                Log.e("exception",e.toString())
            }


            Log.e("compressedImage",compressedImage.toString())
            var width=0
            var height=0
            try {
                var retriever = MediaMetadataRetriever()
                retriever.setDataSource(compressedImage?.absolutePath)
                width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
                height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
            } catch (e: Exception) {
                e.printStackTrace()
            }

            var fileSize = MyUtils.getStringSizeLengthFile(compressedImage?.length()!!)


            val filePart = MultipartBody.Part.createFormData(
                "FileField", MyUtils.createFileNameUser(Date(),"",height,width),
                RequestBody.create("image*//*".toMediaTypeOrNull(),compressedImage!!)

            )
            val call = RestClient.get()!!.uploadAttachment(filePart,RequestBody.create("text/plain".toMediaTypeOrNull(), folderName), RequestBody.create("text/plain".toMediaTypeOrNull(), json))

            call.enqueue(object : retrofit2.Callback<List<UploadImagePojo>> {
                override fun onFailure(call: Call<List<UploadImagePojo>>, t: Throwable)
                {
                    dismissPb()
                    if (compressedImage != null)
                        compressedImage!!.delete()
                    if (mActivity != null)
                        onUploadFileListener?.onFailureUpload("", null)
                }

                override fun onResponse(call: Call<List<UploadImagePojo>>, response: Response<List<UploadImagePojo>>) {
                    Log.e("response","data"+response.body().toString())
                    if (compressedImage != null)
                        compressedImage!!.delete()
                    dismissPb()

                    if (response.body() != null && response.body()!!.isNotEmpty()) {

//                        if (response.body()!![0].status.toString().equals("true",true)) {
                        if (response.body()!![0].status.toString().equals("true",true)) {

                            if (onUploadFileListener != null) {
                                // pbDialog.dismiss();
                                response.body()!!.get(0).fileSize=fileSize
                                onUploadFileListener!!.onSuccessUpload(response.body()!!.get(0))
                            }
                        } else {

                            //   MyUtils.closeProgress();

                            if (onUploadFileListener != null) {
                                onUploadFileListener!!.onFailureUpload(response.body()!![0].message!!, response.body())
                            }
                        }
                    } else {


                        if (onUploadFileListener != null)
                            onUploadFileListener!!.onFailureUpload("", null)
                    }


                }
            })
            return true
        }
    }

    private fun dismissPb() {
        if (pbDialog != null)
            pbDialog!!.dismiss()
    }

    interface OnUploadFileListener {
        fun onSuccessUpload(datumList: UploadImagePojo?)

        fun onFailureUpload(msg: String, datumList: List<UploadImagePojo>?)
    }
}