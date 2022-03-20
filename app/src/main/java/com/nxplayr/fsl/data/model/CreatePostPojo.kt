package com.nxplayr.fsl.data.model
import android.net.Uri
import java.io.File
import java.io.Serializable

data class CreatePostOneMainRVPojo(var itemName: String, var itemImage: Int)
data class CreatePostPrivacyPojo(var name_Privacy: String, var image_Privacy: Int)
data class CreatePostPhotoPojo(var image: Uri?,var imagePath: String?, var isSelectedImage :Boolean= false,
                               var imageDrawable: Int?=-1,
                               var imageName: String="",
                               var fileExtension: String="",
                               var fromVideoImage: String="",
                               var videoThumnailName: String="",
                               var isEdited: Boolean=false,
                               var num:String="0",
                               var videoFile: File?=null,
                               var formType:String="",
                               var transferId: Int = 0,
                               var type: String? = null,
                               var fromServer: String? = null,
                               var width: Int = 0,
                               var height: Int = 0,
                               var file: File? = null,
                               var from: String? = "",
                               var fileSize: String? = "",
                               var isCompress: Boolean = false):Serializable
data class CreatePostVideoPojo(@Transient var videoUri: Uri?, var isSelectedImage :Boolean= false,
                               var imageDrawable: Int?=-1,
                               var imageName: String="",
                               var isEdited: Boolean=false,
                               var num:String="0",
                               var videoFile:File?=null,
                               var formType:String="",
                               var transferId: Int = 0,
                               var type: String? = null,
                               var fromServer: String? = null,
                               var width: Int = 0,
                               var height: Int = 0,
                               var file: File? = null,
                               var from: String? = "",
                               var isCompress: Boolean = false)
data class CreatePostDocumentPojo(var documentName: String, var fileExtension: String )
