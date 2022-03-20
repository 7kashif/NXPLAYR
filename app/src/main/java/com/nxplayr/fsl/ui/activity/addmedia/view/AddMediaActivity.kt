package com.nxplayr.fsl.ui.activity.addmedia.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.UploadFileModel
import com.nxplayr.fsl.data.model.UploadImagePojo
import com.nxplayr.fsl.util.ImageSaver
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.fragment_add_media.*
import kotlinx.android.synthetic.main.layout_select_dialog_camera.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

class AddMediaActivity : AppCompatActivity(),View.OnClickListener {

    var mfUser: File? = null
    var serverfileName = ""
    var serverfileSize = "0"
    var serverfileNameeducation = ""
    var serverfileSizeeducation = "0"
    var imageMediaType = ""
    var mediaImageName = ""
    var mfUser_Media_Image: File? = null
    var type = ""
    var mediaType = ""
    var from = ""
    var media_link = ""
    var media_image = ""
    var media_title = ""
    var media_descri = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_add_media)



        if (intent != null) {
            type = intent.getStringExtra("type")!!
            mediaType = intent.getStringExtra("mediaType")!!
            from = intent.getStringExtra("from")!!
            if (from.equals("edit") && mediaType.equals("Link")) {
                media_link = intent.getStringExtra("editlinkMedia")!!
            }
            if (from.equals("edit") && mediaType.equals("Image")) {
                media_image = intent.getStringExtra("editMediaImage")!!
                media_title = intent.getStringExtra("editMediaTitle")!!
                media_descri = intent.getStringExtra("editMediaDes")!!
            }
        }

        setupViewModel()
        setupUI()



    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (from.equals("Add") && mediaType.equals("Image")) {
            tvToolbarTitle.text = getString(R.string.add_media)
        } else if (from.equals("Add") && mediaType.equals("Link")) {
            tvToolbarTitle.text = getString(R.string.add_link)
        } else if (from.equals("edit") && mediaType.equals("Image")) {
            tvToolbarTitle.text = getString(R.string.edit_media)
        } else if (from.equals("edit") && mediaType.equals("Link")) {
            tvToolbarTitle.text = getString(R.string.edit_link)
        }

        if (mediaType.equals("Image")) {
            ll_mainMediaLink.visibility = View.GONE
            ll_main_AddMedia.visibility = View.VISIBLE
        } else if (mediaType.equals("Link")) {
            ll_mainMediaLink.visibility = View.VISIBLE
            ll_main_AddMedia.visibility = View.GONE
        }

        if (type.equals("Employement")) {
            if (from.equals("edit") && mediaType.equals("Link")) {
                ll_mainMediaLink.visibility = View.VISIBLE
                ll_main_AddMedia.visibility = View.GONE
                edit_addLink.setText(media_link)
            }
            if (from.equals("edit") && mediaType.equals("Image")) {
                ll_mainMediaLink.visibility = View.GONE
                ll_main_AddMedia.visibility = View.VISIBLE
                layout_media.visibility = View.GONE
                image_addMedia.setImageURI(RestClient.image_base_url_mediaEmp + media_image)
                edit_titleAddMedia.setText(media_title)
                edit_addDescription.setText(media_descri)
            }
        } else if (type.equals("Education")) {
            if (from.equals("edit") && mediaType.equals("Link")) {
                ll_mainMediaLink.visibility = View.VISIBLE
                ll_main_AddMedia.visibility = View.GONE
                edit_addLink.setText(media_link)
            }
            else if (from.equals("edit") && mediaType.equals("Image")) {
                ll_mainMediaLink.visibility = View.GONE
                ll_main_AddMedia.visibility = View.VISIBLE
                layout_media.visibility = View.GONE
                image_addMedia.setImageURI(RestClient.image_base_url_mediaEdu + media_image)
                edit_titleAddMedia.setText(media_title)
                edit_addDescription.setText(media_descri)
            }
        }

        if (!media_link.isNullOrEmpty() || !media_title.isNullOrEmpty() || !media_descri.isNullOrEmpty() || !media_image.isNullOrEmpty()) {
            btn_addImageMedia.backgroundTint = resources.getColor(R.color.colorPrimary)
            btn_addImageMedia.textColor = resources.getColor(R.color.black)
            btn_addImageMedia.strokeColor = resources.getColor(R.color.colorPrimary)
        }

        camera_layout_mediaFile.visibility = View.GONE
        if (from.equals("Add") || image_addMedia == null) {
            layout_media.visibility = View.VISIBLE
        }


        image_addMedia.setOnClickListener(this)

        img_select_camera.setOnClickListener(this)

        img_select_gallery.setOnClickListener (this)

        btnCloseProfileSelection.setOnClickListener(this)
        btn_addImageMedia.setOnClickListener(this)

        edit_titleAddMedia.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btn_addImageMedia.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_addImageMedia.textColor = (resources.getColor(R.color.black))
                btn_addImageMedia.strokeColor = (resources.getColor(R.color.colorPrimary))

                if (p0!!.isEmpty()) {
                    btn_addImageMedia.strokeColor = (resources.getColor(R.color.grayborder1))
                    btn_addImageMedia.backgroundTint = (resources.getColor(R.color.transperent1))
                    btn_addImageMedia.textColor = (resources.getColor(R.color.colorPrimary))
                }
            }

        })

        edit_addLink.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btn_addImageMedia.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_addImageMedia.textColor = (resources.getColor(R.color.black))
                btn_addImageMedia.strokeColor = (resources.getColor(R.color.colorPrimary))

                if (p0!!.isEmpty()) {
                    btn_addImageMedia.strokeColor = (resources.getColor(R.color.grayborder1))
                    btn_addImageMedia.backgroundTint = (resources.getColor(R.color.transperent1))
                    btn_addImageMedia.textColor = (resources.getColor(R.color.colorPrimary))
                }
            }

        })


    }

    private fun setupViewModel() {

    }

    fun checkValidation() {
        MyUtils.hideKeyboard1(this@AddMediaActivity)
        if (type.equals("Employement")) {
            if (mediaType.equals("Image")) {
                if (from.equals("Add") && serverfileName.isNullOrEmpty()) {
                    MyUtils.showSnackbar(this@AddMediaActivity, "Please select media file", ll_mainMediaImage)
                } else if (from.equals("edit") && media_image.isNullOrEmpty()) {
                    MyUtils.showSnackbar(this@AddMediaActivity, "Please select media file", ll_mainMediaImage)
                } else if (TextUtils.isEmpty(edit_titleAddMedia.text.toString().trim())) {
                    MyUtils.showSnackbar(this@AddMediaActivity!!, "Please Enter media Title", ll_mainMediaImage)
                    edit_titleAddMedia.requestFocus()
                } /*else if (TextUtils.isEmpty(edit_addDescription.text.toString().trim())) {
                    MyUtils.showSnackbar(this@AddMediaActivity!!, "Please Enter media Description", ll_mainMediaImage)
                    edit_addDescription.requestFocus()
                }*/ else {
                    uploadedData()
                }
            } else if (mediaType.equals("Link")) {
                if (TextUtils.isEmpty(edit_addLink.text.toString().trim())) {
                    MyUtils.showSnackbar(this@AddMediaActivity, "Please Enter media Link", ll_mainMediaImage)
                    edit_addLink.requestFocus()
                } else if (!MyUtils.isValidURL(edit_addLink.text.toString())) {
                    MyUtils.showSnackbar(this@AddMediaActivity, "Please Enter valid URL", ll_mainMediaImage)
                    edit_addLink.requestFocus()
                } else {
                    uploadedData()
                }
            }
        } else if (type.equals("Education")) {
            if (mediaType.equals("Image")) {
                if (from.equals("Add") && serverfileNameeducation.isNullOrEmpty()) {
                    MyUtils.showSnackbar(this@AddMediaActivity, "Please select media file", ll_mainMediaImage)
                } else if (from.equals("edit") && media_image.isNullOrEmpty()) {
                    MyUtils.showSnackbar(this@AddMediaActivity, "Please select media file", ll_mainMediaImage)
                } else if (TextUtils.isEmpty(edit_titleAddMedia.text.toString().trim())) {
                    MyUtils.showSnackbar(this@AddMediaActivity!!, "Please Enter media Title", ll_mainMediaImage)
                    edit_titleAddMedia.requestFocus()
                } /*else if (TextUtils.isEmpty(edit_addDescription.text.toString().trim())) {
                    MyUtils.showSnackbar(this@AddMediaActivity!!, "Please Enter media Description", ll_mainMediaImage)
                    edit_addDescription.requestFocus()
                } */ else {
                    uploadedData()
                }
            } else if (mediaType.equals("Link")) {
                if (TextUtils.isEmpty(edit_addLink.text.toString().trim())) {
                    MyUtils.showSnackbar(this@AddMediaActivity, "Please Enter media Link", ll_mainMediaImage)
                    edit_addLink.requestFocus()
                } else if (!MyUtils.isValidURL(edit_addLink.text.toString())) {
                    MyUtils.showSnackbar(this@AddMediaActivity, "Please Enter valid URL", ll_mainMediaImage)
                    edit_addLink.requestFocus()
                } else {
                    uploadedData()
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCameraPermissionOther() {
        val permissionCheck = ContextCompat.checkSelfPermission(this@AddMediaActivity, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            takePhotoFromCamera()
        } else {
            this.requestPermissions(arrayOf(Manifest.permission.CAMERA), 1003)
        }
    }

    private fun takePhotoFromCamera() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            mfUser = ImageSaver(this@AddMediaActivity).setFileName(
                    MyUtils.CreateFileName(Date(), "")).setExternal(true)
                    .createFile()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(mfUser)
                )
            } else {
                takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(this@AddMediaActivity, this@AddMediaActivity.packageName + ".fileprovider", mfUser!!)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mfUser = null
        }
        startActivityForResult(takePictureIntent, 1212)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getWriteStoragePermissionOther() {
        val permissionCheck =
                ContextCompat.checkSelfPermission(this@AddMediaActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getReadStoragePermissionOther()
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getReadStoragePermissionOther() {
        val permissionCheck =
                ContextCompat.checkSelfPermission(this@AddMediaActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            this.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1002)
        }
    }

    fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1211)
    }


    @SuppressLint("ResourceAsColor")
    private fun GetCameraTypedata(mUser: File, Tye: String) {
        var mUser = mUser
        var picturePath = ""
        try {
            picturePath = mUser.absolutePath
            //var bm = BitmapFactory.decodeFile(picturePath)
            mUser = File(picturePath)
            image_addMedia.setImageURI(Uri.fromFile(mUser))
            if (type.equals("Employement")) {
                UploadImage(mUser)
            } else if (type.equals("Education")) {
                UploadImageEdu(mfUser!!)
            }
            layout_media.visibility = View.GONE
            camera_layout_mediaFile.visibility = View.GONE

            mediaImageName = picturePath
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getReadStoragePermissionOther()
                } else {
                    MyUtils.showSnackbar(
                            this@AddMediaActivity,
                            getString(R.string.permission_denied),
                            ll_mainMediaImage
                    )
                }
            }
            1002 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    MyUtils.showSnackbar(
                            this@AddMediaActivity,
                            getString(R.string.permission_denied),
                            ll_mainMediaImage
                    )
                }
            }

            1003 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    takePhotoFromCamera()
                    getCameraPermissionOther()
                } else {
                    MyUtils.showSnackbar(
                            this@AddMediaActivity,
                            getString(R.string.permission_denied),
                            ll_mainMediaImage
                    )
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var picturePath: String? = ""
        var picturePathcover: String? = ""
        when (requestCode) {

            1211 -> {

                if (data != null && resultCode == Activity.RESULT_OK && this@AddMediaActivity != null) {
                    var imageUri = data.data
                    var fileName = MyUtils.createFileName(Date(), "")
                    picturePath = MyUtils.getFilePathFromURI(this@AddMediaActivity, imageUri!!, fileName)
                    if (picturePath != null) {
                        if (picturePath.contains("https:")) {
                            MyUtils.showSnackbar(
                                    this@AddMediaActivity,
                                    getString(R.string.please_select_other_profile_pic),
                                    ll_mainMediaImage
                            )
                        } else {
                            mediaImageName = picturePath
                            mfUser_Media_Image = File(picturePath)
                            image_addMedia.setImageURI(imageUri)

                            camera_layout_mediaFile.visibility = View.GONE
                            layout_media.visibility = View.GONE
                            if (type.equals("Employement")) {
                                UploadImage(mfUser_Media_Image!!)
                            } else if (type.equals("Education")) {
                                UploadImageEdu(mfUser_Media_Image!!)
                            }
                        }
                    } else
                        MyUtils.showSnackbar(
                                this@AddMediaActivity,
                                getString(R.string.please_select_other_profile_pic),
                                ll_mainMediaImage
                        )
                } else {

//                    MyUtils.showSnackbar(
//                            this,
//                            getString(R.string.please_select_other_profile_pic),
//                            ll_mainMediaImage
//                    )

                }
            }

            1212 -> {

                if (resultCode == Activity.RESULT_OK) {
                    if (mfUser != null) {
                        GetCameraTypedata(mfUser!!, "Photo")
                    }
                } else {
//                    MyUtils.showSnackbar(this, "Please select valid data", ll_mainMediaImage)
                }
            }
        }
    }


    private fun UploadImage(picturePath: File) {
        if (MyUtils.isInternetAvailable(this@AddMediaActivity!!)) {
//            MyUtils.showProgressDialog(this@SignUpSelectionActivity, "Uploading")

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

            UploadFileModel().uploadFile(
                    this@AddMediaActivity,
                    jsonArray.toString(),
                    "employment",
                    picturePath,
                    false,
                    object : UploadFileModel.OnUploadFileListener {
                        override fun onSuccessUpload(datumList: UploadImagePojo?) {
//                            MyUtils.dismissProgressDialog()
                            serverfileName = datumList?.fileName!!
                            serverfileSize=datumList?.fileSize!!
                        }

                        override fun onFailureUpload(
                                msg: String,
                                datumList: List<UploadImagePojo>?
                        ) {
//                            MyUtils.dismissProgressDialog()


                            try {
                                if (MyUtils.isInternetAvailable(this@AddMediaActivity)) {
                                    MyUtils.showSnackbar(this@AddMediaActivity, resources.getString(R.string.error_crash_error_message), ll_mainMediaImage)
                                } else {
                                    MyUtils.showSnackbar(this@AddMediaActivity, resources.getString(R.string.error_common_network), ll_mainMediaImage)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }


                    })
        } else {
            MyUtils.showSnackbar(this@AddMediaActivity!!, resources.getString(R.string.error_common_network), ll_mainMediaImage)
        }
    }

    private fun UploadImageEdu(picturePath: File) {
        if (MyUtils.isInternetAvailable(this@AddMediaActivity!!)) {
//            MyUtils.showProgressDialog(this@SignUpSelectionActivity, "Uploading")

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

            UploadFileModel().uploadFile(
                    this@AddMediaActivity,
                    jsonArray.toString(),
                    "education",
                    picturePath,
                    false,
                    object : UploadFileModel.OnUploadFileListener {
                        override fun onSuccessUpload(datumList: UploadImagePojo?) {
//                            MyUtils.dismissProgressDialog()
                            serverfileNameeducation = datumList?.fileName!!
                            serverfileSizeeducation=datumList?.fileSize!!
                        }

                        override fun onFailureUpload(
                                msg: String,
                                datumList: List<UploadImagePojo>?
                        ) {
//                            MyUtils.dismissProgressDialog()


                            try {
                                if (MyUtils.isInternetAvailable(this@AddMediaActivity)) {
                                    MyUtils.showSnackbar(this@AddMediaActivity, resources.getString(R.string.error_crash_error_message), ll_mainMediaImage)
                                } else {
                                    MyUtils.showSnackbar(this@AddMediaActivity, resources.getString(R.string.error_common_network), ll_mainMediaImage)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }


                    })
        } else {
            MyUtils.showSnackbar(this@AddMediaActivity!!, resources.getString(R.string.error_common_network), ll_mainMediaImage)
        }
    }

    fun uploadedData() {

        if (type.equals("Employement")) {
            var intent = Intent()
            if (mediaType.equals("Image")) {
                if (from.equals("Add")) {
                    intent.putExtra("fromMedia", "AddMedia")
                    intent.putExtra("title", edit_titleAddMedia.text.toString())
                    intent.putExtra("description", edit_addDescription.text.toString())
                    intent.putExtra("imageMedia", serverfileName)
                    intent.putExtra("imageMediaSize", serverfileSize)
                    intent.putExtra("media_type", "Image")
                }
                if (from.equals("edit")) {
                    intent.putExtra("fromMedia", "EditMedia")
                    intent.putExtra("title", edit_titleAddMedia.text.toString())
                    intent.putExtra("description", edit_addDescription.text.toString())
                    if (!serverfileName.isNullOrEmpty()) {
                        intent.putExtra("imageMedia", serverfileName)
                        intent.putExtra("imageMediaSize", serverfileSize)
                    } else {
                        intent.putExtra("imageMedia", media_image)
                        intent.putExtra("imageMediaSize", serverfileSize)

                    }
                    intent.putExtra("media_type", "Image")
                }
                setResult(1006, intent)
                finish()
                onBackPressed()
            } else if (mediaType.equals("Link")) {
                if (from.equals("Add")) {
                    intent.putExtra("fromMedia", "AddLink")
                    intent.putExtra("link_media", edit_addLink.text.toString())
                    intent.putExtra("media_type", "Link")
                }
                if (from.equals("edit")) {
                    intent.putExtra("fromMedia", "EditLink")
                    intent.putExtra("link_media", edit_addLink.text.toString())
                    intent.putExtra("media_type", "Link")
                }
                setResult(1005, intent)
                finish()
                onBackPressed()
            }
        } else if (type.equals("Education")) {
            var intent = Intent()
            if (mediaType.equals("Image")) {
                if (from.equals("Add")) {
                    intent.putExtra("fromMedia", "AddMedia")
                    intent.putExtra("title", edit_titleAddMedia.text.toString())
                    intent.putExtra("description", edit_addDescription.text.toString())
                    intent.putExtra("imageMedia", serverfileNameeducation)
                    intent.putExtra("serverfileSizeeducation", serverfileSizeeducation)
                    intent.putExtra("media_type", "Image")
                }
                if (from.equals("edit")) {
                    intent.putExtra("fromMedia", "EditMedia")
                    intent.putExtra("title", edit_titleAddMedia.text.toString())
                    intent.putExtra("description", edit_addDescription.text.toString())
                    if (!serverfileNameeducation.isNullOrEmpty()) {
                        intent.putExtra("imageMedia", serverfileNameeducation)
                        intent.putExtra("serverfileSizeeducation", serverfileSizeeducation)
                    } else {
                        intent.putExtra("imageMedia", media_image)
                        intent.putExtra("serverfileSizeeducation", serverfileSizeeducation)

                    }
                    intent.putExtra("media_type", "Image")
                }
                setResult(1008, intent)
                finish()
                onBackPressed()
            } else if (mediaType.equals("Link")) {
                if (from.equals("Add")) {
                    intent.putExtra("fromMedia", "AddLink")
                    intent.putExtra("link_media", edit_addLink.text.toString())
                    intent.putExtra("media_type", "Link")
                }
                if (from.equals("edit")) {
                    intent.putExtra("fromMedia", "EditLink")
                    intent.putExtra("link_media", edit_addLink.text.toString())
                    intent.putExtra("media_type", "Link")
                }
                setResult(1007, intent)
                finish()
                onBackPressed()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.image_addMedia->{
                camera_layout_mediaFile.visibility = View.VISIBLE


            }
            R.id.img_select_camera->{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    getCameraPermissionOther()
                    img_select_camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_selected_popup))
                    img_select_gallery.setColorFilter(resources.getColor(R.color.transperent))
                    img_select_gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon))

                } else {
                    takePhotoFromCamera()
                }
            }
            R.id.img_select_gallery->{
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWriteStoragePermissionOther()
                    img_select_gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon_selected_popup))
                    img_select_camera.setColorFilter(resources.getColor(R.color.transperent))
                    img_select_camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_unselected_popup))

                } else {
                    openGallery()
                }
            }
            R.id.btnCloseProfileSelection->{
                camera_layout_mediaFile.visibility = View.GONE
            }
            R.id.btn_addImageMedia->{

                checkValidation()
            }
        }
    }
}

