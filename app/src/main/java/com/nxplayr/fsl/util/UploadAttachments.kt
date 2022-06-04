package com.nxplayr.fsl.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.util.aws.S3Uploader
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfiumCore
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*


class UploadAttachments(
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
    var s3uploaderObj: S3Uploader? = null

    init {
        this.imageList = imageList!!
        userId = userID
        this.onSuccess = onsuccess
        s3uploaderObj = S3Uploader(mActivity)

        MyAsyncTask(onSuccess!!, imageList, userId, mActivity, from).execute()
    }

    inner class MyAsyncTask(
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


            val folderName = "post/" + imageList!![0]!!.imageName
            val filePath = imageList[0]!!.imagePath


            s3uploaderObj!!.initUpload(filePath, folderName, "document")
            s3uploaderObj!!.setOns3UploadDone(object : S3Uploader.S3UploadInterface {
                override fun onUploadSuccess(response: String?) {
                    if (response.equals("Success", ignoreCase = true)) {

                        var image_bitmap = renderToBitmap(mActivity, filePath)
                        if (image_bitmap!!.isNotEmpty()) {
                            var image_path = bitmapToFile(image_bitmap[0])
                            if (!image_path.isNullOrEmpty()) {
                                Log.d("IMAGS", "PATH  --> " + image_path)

                                var fileName = imageList[0]!!.imageName
                                fileName = fileName.replaceAfter(".", "jpg")
                                val folderName = "post/" + fileName

                                s3uploaderObj!!.initUpload(image_path, folderName, "image")
                                s3uploaderObj!!.setOns3UploadDone(object :
                                    S3Uploader.S3UploadInterface {
                                    override fun onUploadSuccess(response: String?) {
                                        if (response.equals("Success", ignoreCase = true)) {
                                            imageList[0]!!.videoThumnailName = fileName
                                            onSuccess.onSuccessUpload(
                                                imageList as List<CreatePostPhotoPojo>?,
                                                fileName
                                            )
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
                            }
                        }

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

            return null
        }

        public fun compressFiles(): File {
            var file1: File? = null
            for (i in imageList?.indices!!) {
                if (!imageList[i]!!.isCompress) {
                    if (from.equals("Document", ignoreCase = true)) {

                        file1 = File(imageList[i]!!.imagePath.toString())
                        try {
                            Log.e(
                                "fileName",
                                imageList[i]!!.imageName + "  " + mActivity.baseContext.cacheDir.absolutePath
                            )
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

    fun renderToBitmap(context: Context?, filePath: String?): List<Bitmap>? {
        val images: MutableList<Bitmap> = ArrayList()
        val pdfiumCore = PdfiumCore(context)
        try {
            val f: File = File(filePath)
            val fd = ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfDocument: PdfDocument = pdfiumCore.newDocument(fd)
            val pageCount = pdfiumCore.getPageCount(pdfDocument)
            if (pageCount > 0) {
                pdfiumCore.openPage(pdfDocument, 0)
                val width = pdfiumCore.getPageWidthPoint(pdfDocument, 0)
                val height = pdfiumCore.getPageHeightPoint(pdfDocument, 0)
                val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                pdfiumCore.renderPageBitmap(pdfDocument, bmp, 0, 0, 0, width, height)
                images.add(bmp)
            }
            pdfiumCore.closeDocument(pdfDocument)
        } catch (e: Exception) {
            //todo with exception
        }
        return images
    }

    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap): String {
        // Get the context wrapper
        val wrapper = ContextWrapper(mActivity)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return file.absolutePath.toString()
    }


    interface OnSuccess {
        fun onSuccessUpload(datumList: List<CreatePostPhotoPojo>?, imageStr: String)
        fun onFailureUpload(msg: String?, datumList: List<CreatePostPhotoPojo>?)
    }

}