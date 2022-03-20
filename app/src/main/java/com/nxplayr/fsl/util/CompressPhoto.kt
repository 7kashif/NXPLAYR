package com.nxplayr.fsl.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.View
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import id.zelory.compressor.Compressor
import java.io.File
import java.io.IOException
import java.util.*

class CompressPhoto(
    var mActivity: Activity,
    imageList: List<CreatePostPhotoPojo?>?,
    userID: String,
    view: View?,
    var onsuccess: OnSuccess,
    var from: String
) {
    var imageList: List<CreatePostPhotoPojo?>? = ArrayList()
    var i = 0
    var userId: String
    var isRunning = false
    var onSuccess:OnSuccess?=null


    init {
        this.imageList = imageList!!
        userId = userID
        this.onSuccess=onsuccess
        MyAsyncTask(onSuccess!!,imageList,userId,mActivity,from).execute()

    }




    interface OnSuccess {
        fun onSuccessUpload(datumList: List<CreatePostPhotoPojo?>)
        fun onFailureUpload(
            msg: String?,
            datumList: List<CreatePostPhotoPojo?>?
        )
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
              if(compressFiles()){
                  if (onSuccess != null) {
                      onSuccess!!.onSuccessUpload(imageList!!)
                  }
              }else{
                  if (onSuccess != null)
                      onSuccess!!.onFailureUpload("", imageList)


              }


              return null
          }

         public fun compressFiles():Boolean {
             var file:Boolean=false
             for (i in imageList?.indices!!) {
                 if (!imageList!![i]!!.isCompress) {
                     if (from.equals("Photo", ignoreCase = true)||from.equals("Link", ignoreCase = true)) {
                         val options = BitmapFactory.Options()
                         options.inJustDecodeBounds = true
                         val file1 = File(imageList[i]!!.imagePath.toString())
                         var file: File? = null
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
                                     "",
                                 imageHeight,imageWidth)
                             imageList[i]!!.imageName=(fileName!!)
                             imageList[i]!!.imagePath = file.absolutePath
                             imageList[i]!!.fileSize = MyUtils.getStringSizeLengthFile(file.length())
                             imageList[i]!!.isCompress = true
                         } catch (e: IOException) {
                             e.printStackTrace()
                         }

                     }
                      file= true
                 } else {
                     file= true
                 }
             }

             return file
         }

      }




}