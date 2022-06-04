package com.nxplayr.fsl.util

import android.app.Activity
import android.app.Dialog
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.util.Log
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.UploadImagePojo
import com.nxplayr.fsl.util.aws.S3Uploader
import com.vincent.videocompressor.VideoController
import java.io.File
import java.util.*


class UploadVideo {
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
    var thumbList: List<CreatePostPhotoPojo?>? = java.util.ArrayList()
    var s3uploaderObj: S3Uploader? = null

    fun uploadFileVideo(
        context: Activity,
        json: String, folderName: String,
        file: ArrayList<CreatePostPhotoPojo>,
        thumb: ArrayList<CreatePostPhotoPojo>,
        isShowPb: Boolean,
        onUploadFileListener: OnUploadFileListener
    ) {
        this.mActivity = context
        this.onUploadFileListener = onUploadFileListener
        this.json=json
        this.imageList = file
        this.thumbList = thumb
        this.folderName = folderName
        this.isShowPb = isShowPb
        if (isShowPb) {
            pbDialog =
                ProgressHUD(mActivity!!, R.style.CustomBottomSheetDialogTheme, "")
            pbDialog?.show()
        }
        s3uploaderObj = S3Uploader(context)

        MyAsyncTask().execute()
    }

    inner class MyAsyncTask : AsyncTask<Void, Void, Boolean>() {


        override fun doInBackground(vararg arg0: Void): Boolean {
            /*try {
                compressedImage = Compressor(mActivity)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                     .compressToFile(file, "IMG.png")
            } catch (e: Exception) {
                Log.e("exception",e.toString())
            }*/
            val fileName =
                    MyUtils.createFileName(
                            Date(),
                            "Video")
            val file =
                    ImageSaver(mActivity!!).setExternal(true).setFileName(fileName!!).createFile()

           val isCompress: Boolean = VideoController.getInstance().convertVideo(
                   imageList!![0]!!.imagePath,
                   file.absolutePath,
                   VideoController.COMPRESS_QUALITY_MEDIUM
           ) { percent -> Log.d("percent video compress", "" + percent) }
            if (isCompress) {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(file.absolutePath)
                val width: Int = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
                val height: Int = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
                retriever.release()
                val fileName1 =
                        MyUtils.createFileName(
                                Date(),
                                "Video",
                                height, width)
                imageList!![0]!!.imageName=(fileName1!!)
                imageList!![0]!!.imagePath = file.absolutePath
                imageList!![0]!!.videoThumnailName = imageList!![0]!!.imageName.replace(".mp4","_thumb.jpg")
            }



            imageList!![0]!!.isCompress = true

            compressedImage= File(imageList!![0]!!.imagePath)

            val file_size = (file.length() / 1024).toString().toInt()

            val folderName = "post/" + imageList!![0]!!.imageName

            s3uploaderObj!!.initUpload(imageList!![0]!!.imagePath, folderName, "video")
            s3uploaderObj!!.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
                override fun onUploadSuccess(response: String?) {
                    Log.e("setOns3UploadDone", "==========================")
                    Log.d("setOns3UploadDone", "VIDEO = $folderName")
                    Log.d("setOns3UploadDone", "VIDEO = $response")

                    if (compressedImage != null)
                        compressedImage!!.delete()
                    dismissPb()
                    if (response.equals("Success", ignoreCase = true)) {
                        var data = UploadImagePojo()
                        data.fileName = imageList!![0]!!.videoThumnailName
                        data.fileSize = file_size.toString()
                        data.status = response
                        onUploadFileListener!!.onSuccessUpload(data)
                    }
                }

                override fun onUploadError(response: String?) {
                    if (compressedImage != null)
                        compressedImage!!.delete()
                    dismissPb()
                    Log.e("S3Uploader", "Error: $response")
                    onUploadFileListener!!.onFailureUpload(response!!, null)

                }
            })



//            val call = RestClient.get()!!.uploadAttachment(filePart, RequestBody.create("text/plain".toMediaTypeOrNull(), "post"), RequestBody.create("text/plain".toMediaTypeOrNull(), json))
//            call.enqueue(object : retrofit2.Callback<List<UploadImagePojo>> {
//                override fun onFailure(call: Call<List<UploadImagePojo>>, t: Throwable) {
//                    dismissPb()
//                    if (compressedImage != null)
//                        compressedImage!!.delete()
//                    if (mActivity != null)
//                        onUploadFileListener?.onFailureUpload("", null)
//                }
//
//                override fun onResponse(call: Call<List<UploadImagePojo>>, response: Response<List<UploadImagePojo>>) {
//                    Log.e("response", "data" + response.body().toString())
//                    if (compressedImage != null)
//                        compressedImage!!.delete()
//                    dismissPb()
//
//                    if (response.body() != null && response.body()!!.isNotEmpty()) {
//
////                        if (response.body()!![0].status.toString().equals("true",true)) {
//                        if (response.body()!![0].status.toString().equals("true", true)) {
//
//                            if (onUploadFileListener != null) {
//                                // pbDialog.dismiss();
//                                var data = UploadImagePojo()
//                                data.fileName = filePart!![it].imageName
//                                data.status = response
//                                onUploadFileListener!!.onSuccessUpload(data)
//                            }
//                        } else {
//
//                            //   MyUtils.closeProgress();
//
//                            if (onUploadFileListener != null) {
//                                onUploadFileListener!!.onFailureUpload(response.body()!![0].message!!, response.body())
//                            }
//                        }
//                    } else {
//
//
//                        if (onUploadFileListener != null)
//                            onUploadFileListener!!.onFailureUpload("", null)
//                    }
//
//
//                }
//            })
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