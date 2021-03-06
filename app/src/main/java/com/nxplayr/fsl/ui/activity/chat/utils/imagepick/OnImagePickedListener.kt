package com.nxplayr.fsl.ui.activity.chat.utils.imagepick

import java.io.File


interface OnImagePickedListener {

    fun onImagePicked(requestCode: Int, file: File)

    fun onImagePickError(requestCode: Int, e: Exception)

    fun onImagePickClosed(requestCode: Int)
}