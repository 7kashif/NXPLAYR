package com.nxplayr.fsl.util

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.content.FileProvider
import com.facebook.common.executors.CallerThreadExecutor
import com.facebook.common.references.CloseableReference
import com.facebook.datasource.DataSource
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber
import com.facebook.imagepipeline.image.CloseableImage
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.nxplayr.fsl.application.MyApplication

public class  DownloadImage(
    var url: String,
    var filename: String,
    var isDownloadOnly: Boolean,
    val mActivity: Activity,var offerId:String=""
) : AsyncTask<Void, Void, String>() {
    override fun doInBackground(vararg p0: Void?): String {
        getBitmapFresco(url, filename,isDownloadOnly)
        return ""
    }


    fun getBitmapFresco(url: String, filename: String,isDownloadOnly:Boolean) {
        val imageRequest = ImageRequestBuilder
            .newBuilderWithSource(Uri.parse(url))
            .setAutoRotateEnabled(true)
            .build()

        val imagePipeline = Fresco.getImagePipeline()
        val dataSource = imagePipeline.fetchDecodedImage(imageRequest, this)

        dataSource.subscribe(object : BaseBitmapDataSubscriber() {
            override fun onFailureImpl(dataSource: DataSource<CloseableReference<CloseableImage>>?) {

                if (dataSource != null) {
                    dataSource!!.close()
                }

            }

            public override fun onNewResultImpl(@Nullable bitmap: Bitmap?) {
                if (dataSource.isFinished && bitmap != null) {
                    Log.d("Bitmap", "has come")
                    var bmp = Bitmap.createBitmap(bitmap)
                    dataSource.close()
                    storeImage(bmp, filename,isDownloadOnly)
                }
            }


        }, CallerThreadExecutor.getInstance())
    }

    fun storeImage(imageData: Bitmap, filename: String, isDownloadOnly:Boolean) {

        // get path to external storage (SD card)
        var handler = Handler(Looper.getMainLooper())
        handler.postDelayed({


            var file=  ImageSaver( mActivity!!).setExternal(true).setFileName(filename).save(imageData)

            if(isDownloadOnly)
                Toast.makeText(MyApplication.instance!!, "Image Saved into device", Toast.LENGTH_LONG).show()
            else {
                var uri:Uri?=null
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                uri=Uri.fromFile(file)
                } else {
              uri=
                        FileProvider.getUriForFile(
                            mActivity,
                            mActivity.applicationContext.packageName + ".fileprovider",
                          file)

                }
                ShareProfile(mActivity).SharingMsgProfile(offerId, uri)
            }


        }, 100)
    }
}
