package com.nxplayr.fsl.util

import android.app.Activity
import android.app.Dialog
import android.util.Log
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.UploadImagePojo
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

import retrofit2.Call
import retrofit2.Response
import java.io.File
import kotlin.collections.ArrayList


class UploadExploreVideo {
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
    var imageList: List<CreatePostPhotoPojo?>? = java.util.ArrayList()

    fun uploadFileVideo(
        context: Activity,
        json: String, folderName: String, file: ArrayList<CreatePostPhotoPojo>, isShowPb:Boolean, onUploadFileListener: OnUploadFileListener
    ) {
        this.mActivity = context
        this.onUploadFileListener = onUploadFileListener
        this.json=json
        this.imageList = file
        this.folderName = folderName
        this.isShowPb = isShowPb
        if (isShowPb) {
            pbDialog =
                ProgressHUD(mActivity!!, R.style.CustomBottomSheetDialogTheme,"")
            pbDialog?.show()
        }

        compressedImage= File(imageList!![0]!!.imagePath)
        Log.e("compressedImage",compressedImage.toString())

        val filePart = MultipartBody.Part.createFormData(
                "FileField", imageList!![0]!!.imageName,
                RequestBody.create("video*//*".toMediaTypeOrNull(),compressedImage!!)

        )
        val call = RestClient.get()!!.uploadAttachment(filePart,RequestBody.create("text/plain".toMediaTypeOrNull(), "post"), RequestBody.create("text/plain".toMediaTypeOrNull(), json))

        call.enqueue(object : retrofit2.Callback<List<UploadImagePojo>> {
            override fun onFailure(call: Call<List<UploadImagePojo>>, t: Throwable)
            {
                dismissPb()
                if (compressedImage != null)
                    compressedImage!!.delete()
                if (mActivity != null)
                    onUploadFileListener?.onFailureUpload("", null)

                t.printStackTrace()
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