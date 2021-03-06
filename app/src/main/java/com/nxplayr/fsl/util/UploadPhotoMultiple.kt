package com.nxplayr.fsl.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Log
import android.view.View
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.MultiplefileData
import com.nxplayr.fsl.data.model.MultiplefileUpload
import id.zelory.compressor.Compressor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*

class UploadPhotoMultiple(
    var mActivity: Activity,
    imageList: List<CreatePostPhotoPojo?>?,
    userID: String,
    view: View?,
    var onsuccess: OnSuccess,
    var from: String
) {
    var fileParts: Array<MultipartBody.Part?>?=null
    var imageList: List<CreatePostPhotoPojo?>? = ArrayList()
    var i = 0
    var userId: String
    var isRunning = false
    var onSuccess:OnSuccess?=null


    init {
        this.imageList = imageList!!
        userId = userID
        this.onSuccess=onsuccess
        MyAsyncTask(onSuccess!!,imageList,userId,fileParts,mActivity,from).execute()

    }




    interface OnSuccess {
        fun onSuccessUpload(datumList: List<MultiplefileData?>?)
        fun onFailureUpload(
            msg: String?,
            datumList: List<CreatePostPhotoPojo?>?
        )
    }

     internal class MyAsyncTask(
         val onSuccess: OnSuccess,
         val imageList: List<CreatePostPhotoPojo?>?,
         val userId: String,
         var fileParts: Array<MultipartBody.Part?>?,
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
              compressFiles()
              val jsonArray = JSONArray()
              val jsonObject = JSONObject()
              try {
                  jsonObject.put("loginuserID", userId)
                  jsonObject.put("foldername", "post")
                  jsonObject.put("apiType", RestClient.apiType)
                  jsonObject.put("apiVersion", RestClient.apiVersion)
                  jsonArray.put(jsonObject)
              } catch (e: JSONException) {
                  e.printStackTrace()
              }
              val call: Call<List<MultiplefileUpload>> = RestClient.get()!!.uploadAttachmentArray(fileParts!!, RequestBody.create(
                  "text/plain".toMediaTypeOrNull()!!, jsonArray.toString()))
              // Call<List<FiluploadResponse>> call = RestClient.get().uploadAttachment(filePart, RequestBody.create(MediaType.parse("text/plain"), "foodimages"), RequestBody.create(MediaType.parse("text/plain"), jsonArray.toString()));
              Log.i("System out", "jsonFile upload $jsonArray")
              /*pbDialog = MyUtils.showProgressDialog(mActivity);
          pbDialog.show();*/
// MyUtils.showProgress(mActivity);
              call.enqueue(object : Callback<List<MultiplefileUpload>?> {
                  override fun onResponse(
                      call: Call<List<MultiplefileUpload>?>,
                      response: Response<List<MultiplefileUpload>?>
                  ) {
                      if (response.body() != null && response.body()!!.isNotEmpty())
                      {
                          if (response.body()!![0].status.equals("true", ignoreCase = true)) {
                              if (onSuccess != null) {
                                  onSuccess!!.onSuccessUpload(response.body()!![0].data)
                              }
                          } else {
                              if (onSuccess != null)
                                  onSuccess!!.onFailureUpload(response.body()!![0].message, imageList)
                          }
                      } else {
                          if (onSuccess != null)
                                onSuccess!!.onFailureUpload("", imageList)
                      }
                  }

                  override fun onFailure(
                      call: Call<List<MultiplefileUpload>?>,
                      t: Throwable
                  ) { // Log error here since request failed
                      t.printStackTrace()
                      Log.e("System out", t?.message!!)
                      //  MyUtils.closeProgress();
                      if (onSuccess != null) onSuccess!!.onFailureUpload(t.message, imageList)
                  }
              })
              return null
          }


         private fun filePartArray(pos: Int, path: String?) {
             try {

                 val file = File(path)
                 val fileBody = RequestBody.create("image*//*".toMediaTypeOrNull()!!, file)
                 fileParts?.set(pos, MultipartBody.Part.createFormData(
                     String.format(
                         Locale.ENGLISH,
                         "MultipleFiles[%d]",
                         pos
                     ), file.name, fileBody
                 )
                 )
                 Log.e(
                     "ImagesArray",
                     String.format(Locale.ENGLISH, "MultipleFiles[%d]", pos)
                 )
             } catch (e: Exception) {
             }
         }


         public fun compressFiles() {
             fileParts = arrayOfNulls(imageList!!.size)
             for (i in imageList.indices) {
                 if (!imageList[i]!!.isCompress) {
                     if (from.equals("Photo", ignoreCase = true) ||from.equals("Link", ignoreCase = true)) {
                         val options = BitmapFactory.Options()
                         options.inJustDecodeBounds = true
                         val file1 = File(imageList[i]!!.imagePath.toString())
                         var file: File? = null
                         try {
                             file = Compressor(mActivity)
                                 .setQuality(90)
                                 .setMaxHeight(225)
                                 .setMaxWidth(720)
                                 .setCompressFormat(Bitmap.CompressFormat.PNG)
                                 .compressToFile(file1)
                             BitmapFactory.decodeFile(file.absolutePath, options)
                             val imageHeight = options.outHeight
                             val imageWidth = options.outWidth
                             imageList[i]!!.width = imageWidth
                             imageList[i]!!.height = imageHeight
                             val fileName =
                                 imageHeight.toString() + "X" + imageWidth + "X" + MyUtils.createFileName(
                                     Date(),
                                     ""
                                 )
                             Log.e(
                                 "fileName",
                                 fileName + "  " + mActivity.baseContext.cacheDir.absolutePath
                             )
                             imageList[i]!!.imageName=(fileName)
                             imageList[i]!!.imagePath = file.absolutePath
                             imageList[i]!!.isCompress = true
                             filePartArray(i, imageList[i]!!.imagePath.toString())
                         } catch (e: IOException) {
                             e.printStackTrace()
                         }

                     }
                     else if (from.equals("Video", ignoreCase = true)) {
                         val fileName: String = MyUtils.createFileName(Date(), "Video")!!
                         val file =
                             ImageSaver(mActivity).setExternal(true).setFileName(fileName).createFile()
                         /*val isCompress: Boolean = VideoController.getInstance().convertVideo(
                             imageList[i]!!.sdcardPath,
                             file.absolutePath,
                             VideoController.COMPRESS_QUALITY_LOW
                         ) { percent -> Log.d("percent video compress", "" + percent) }
                         if (isCompress) {
                             imageList[i]!!.imageName=(fileName)
                             imageList[i]!!.sdcardPath = file.absolutePath
                         }*/
                         imageList[i]!!.isCompress = true
                         filePartArray(i, imageList[i]!!.imagePath.toString())
                     }
                 } else {
                     filePartArray(i, imageList[i]!!.imagePath.toString())
                 }
             }
         }

      }




}