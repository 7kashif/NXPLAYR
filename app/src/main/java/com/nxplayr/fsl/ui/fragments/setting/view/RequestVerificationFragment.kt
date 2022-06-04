package com.nxplayr.fsl.ui.fragments.setting.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UploadFileModel
import com.nxplayr.fsl.data.model.UploadImagePojo
import com.nxplayr.fsl.data.model.VerificationCatData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.ui.fragments.bottomsheet.BottomSheetListFragment
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.VerificationCatListModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.ImageSaver
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_request_verification.*
import kotlinx.android.synthetic.main.layout_select_dialog_camera.*
import kotlinx.android.synthetic.main.toolbar1.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

class RequestVerificationFragment : Fragment(), BottomSheetListFragment.SelectLanguage,
    View.OnClickListener {

    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var verificationData: ArrayList<VerificationCatData>? = ArrayList()
    var verificationcatID = ""
    var verificationcatName = ""
    var mfUser: File? = null
    var mediaImageName = ""
    var mfUser_Media_Image: File? = null
    var serverfileSize = ""
    var serverfileName = ""
    private lateinit var loginModel: SignupModelV2
    private lateinit var verificationCatListModel: VerificationCatListModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_verification, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngRequestVerification.isNullOrEmpty())
                tvToolbarTitle1.text = sessionManager?.LanguageLabel?.lngRequestVerification
            if (!sessionManager?.LanguageLabel?.lngApplyInsta.isNullOrEmpty())
                frv_text_title.text = sessionManager?.LanguageLabel?.lngApplyInsta
            if (!sessionManager?.LanguageLabel?.lngApplyInstaDetail.isNullOrEmpty())
                frv_text_desc.text = sessionManager?.LanguageLabel?.lngApplyInstaDetail
            if (!sessionManager?.LanguageLabel?.lngFirstName.isNullOrEmpty())
                fev_input_first_name.hint = sessionManager?.LanguageLabel?.lngFirstName
            if (!sessionManager?.LanguageLabel?.lngLastName.isNullOrEmpty())
                fev_input_last_name.hint = sessionManager?.LanguageLabel?.lngLastName
            if (!sessionManager?.LanguageLabel?.lngKnownAs.isNullOrEmpty())
                fev_input_known_as.hint = sessionManager?.LanguageLabel?.lngKnownAs
            if (!sessionManager?.LanguageLabel?.lngCategory.isNullOrEmpty())
                fev_input_category.hint = sessionManager?.LanguageLabel?.lngCategory
            if (!sessionManager?.LanguageLabel?.lngPhotoId.isNullOrEmpty())
                frv_text_attach_image.text = sessionManager?.LanguageLabel?.lngPhotoId
            if (!sessionManager?.LanguageLabel?.lngChooseFile.isNullOrEmpty())
                frv_text_choose_image.text = sessionManager?.LanguageLabel?.lngChooseFile
            if (!sessionManager?.LanguageLabel?.lngInstaPhotoDetail.isNullOrEmpty())
                description.text = sessionManager?.LanguageLabel?.lngInstaPhotoDetail
            if (!sessionManager?.LanguageLabel?.lngSend.isNullOrEmpty())
                send_.text = sessionManager?.LanguageLabel?.lngSend
            if (!sessionManager?.LanguageLabel?.lngCamera.isNullOrEmpty())
                tv_camera.text = sessionManager?.LanguageLabel?.lngCamera
            if (!sessionManager?.LanguageLabel?.lngGallery.isNullOrEmpty())
                tv_gallery.text = sessionManager?.LanguageLabel?.lngGallery
            if (!sessionManager?.LanguageLabel?.lngClose.isNullOrEmpty())
                btnCloseProfileSelection.progressText = sessionManager?.LanguageLabel?.lngClose
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        if (!userData?.userProfileVerified.isNullOrEmpty() && userData?.userProfileVerified.equals("Requested")) {
            frv_ll_requested_submitted.visibility = View.VISIBLE
            frv_nl_main.visibility = View.GONE
        } else {
            frv_ll_requested_submitted.visibility = View.GONE
            frv_nl_main.visibility = View.VISIBLE
        }

        if (!userData?.userFirstName.isNullOrEmpty()) {
            fev_edit_first_name.setText(userData?.userFirstName)
            fev_edit_last_name.setText(userData?.userLastName)
        }

        (mActivity as MainActivity).setToolBar(toolbar1)
        tvToolbarTitle1?.text = resources.getString(R.string.request_verification)
        llToolbarmain?.visibility = View.INVISIBLE
        toolbar1?.setNavigationIcon(R.drawable.back_arrow_signup)
        toolbar1?.elevation = 4f

        toolbar1?.setNavigationOnClickListener {
            (mActivity as MainActivity).onBackPressed()
        }

        frv_text_choose_image?.setOnClickListener(this)

        frv_btn_send?.setOnClickListener(this)

        fev_edit_category?.setOnClickListener(this)

        img_select_camera.setOnClickListener(this)

        img_select_gallery.setOnClickListener(this)

        btnCloseProfileSelection.setOnClickListener(this)

        frv_nl_main?.visibility = View.VISIBLE
        frv_ll_requested_submitted?.visibility = View.GONE

    }

    private fun setupViewModel() {
        loginModel =
            ViewModelProvider(this@RequestVerificationFragment).get(SignupModelV2::class.java)
        verificationCatListModel = ViewModelProvider(this@RequestVerificationFragment).get(
            VerificationCatListModel::class.java
        )

    }

    private fun checkValidation() {
        MyUtils.hideKeyboard1(mActivity!!)
        when {
            fev_edit_first_name?.text.toString().isNullOrEmpty() -> {
                MyUtils.showSnackbar(
                    mActivity!!,
                    resources.getString(R.string.msg_empty_first_name),
                    frv_rl_main
                )
            }

            fev_edit_last_name?.text.toString().isNullOrEmpty() -> {
                MyUtils.showSnackbar(
                    mActivity!!,
                    resources.getString(R.string.msg_empty_last_name),
                    frv_rl_main
                )
            }

            fev_edit_known_as?.text.toString().isNullOrEmpty() -> {
                MyUtils.showSnackbar(
                    mActivity!!,
                    resources.getString(R.string.msg_empty_known_as),
                    frv_rl_main
                )
            }
            serverfileName.isNullOrEmpty() -> {
                MyUtils.showSnackbar(mActivity!!, "Please select photo Id", frv_rl_main)

            }

            else -> {
                getSendVerification()

            }
        }
    }

    private fun getSendVerification() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("verificationRemarks", verificationcatName)
            jsonObject.put("verificationLastName", fev_edit_last_name.text.toString().trim())
            jsonObject.put("verificationKnownAs", fev_edit_known_as.text.toString().trim())
            jsonObject.put("verificationFirstName", fev_edit_first_name.text.toString().trim())
            jsonObject.put("verificationPhotoIDs", serverfileName)
            jsonObject.put("verificationcatID", verificationcatID)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        loginModel.getSendVerification(jsonArray.toString())
        loginModel.getSendVerification
            .observe(
                this@RequestVerificationFragment,
                androidx.lifecycle.Observer { loginPojo ->
                    if (loginPojo != null) {
                        if (loginPojo.get(0).status.equals("true", true)) {
                            MyUtils.dismissProgressDialog()
                            try {
                                userData?.userProfileVerified = "Requested"
                                StoreSessionManager(userData)
                                Handler().postDelayed({
                                    frv_nl_main?.visibility = View.GONE
                                    frv_ll_requested_submitted?.visibility = View.VISIBLE
                                }, 1000)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message, frv_rl_main)
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(frv_rl_main)
                    }
                })
    }

    private fun StoreSessionManager(uesedata: SignupData?) {

        val gson = Gson()

        val json = gson.toJson(uesedata)
        sessionManager?.create_login_session(
            json,
            uesedata!!.userMobile,
            "",
            true,
            sessionManager!!.isEmailLogin()
        )

    }

    private fun takePhotoFromCamera() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            mfUser = ImageSaver(mActivity!!).setFileName(
                MyUtils.CreateFileName(Date(), "")
            ).setExternal(true)
                .createFile()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(mfUser)
                )
            } else {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    FileProvider.getUriForFile(
                        mActivity!!,
                        mActivity?.packageName + ".fileprovider",
                        mfUser!!
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mfUser = null
        }
        startActivityForResult(takePictureIntent, 1212)
    }

    fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1211)
    }

    private fun getCameraAndStoragePermision() {
        val permissionCheckWriteExternalStorage =
            ContextCompat.checkSelfPermission(
                mActivity as MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        val permissionCheckReadExternalStorage =
            ContextCompat.checkSelfPermission(
                mActivity as MainActivity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        val permissionCheckCamera =
            ContextCompat.checkSelfPermission(
                mActivity as MainActivity,
                Manifest.permission.CAMERA
            )

        if (permissionCheckWriteExternalStorage == PackageManager.PERMISSION_GRANTED &&
            permissionCheckReadExternalStorage == PackageManager.PERMISSION_GRANTED &&
            permissionCheckCamera == PackageManager.PERMISSION_GRANTED
        ) {
            openImageSelection()
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MyUtils.Per_REQUEST_WRITE_EXTERNAL_STORAGE_1 -> if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                grantResults[2] == PackageManager.PERMISSION_GRANTED
            ) {
                openImageSelection()
            } else {
                MyUtils.showMessageYesNo(mActivity!!,
                    resources.getString(R.string.grant_access_location), "",
                    DialogInterface.OnClickListener { dialog, which ->
                        dialog?.dismiss()
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", mActivity!!.packageName, null)
                            startActivity(this)
                        }
                    })
            }
        }
    }

    private fun openImageSelection() {
        camera_layout_mediaFile.visibility = View.VISIBLE
    }

    private fun VerificationCatList() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", "")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        verificationCatListModel.VerificationCatList(mActivity!!, jsonArray.toString())
            .observe(this@RequestVerificationFragment!!,
                { clubListpojo ->
                    MyUtils.dismissProgressDialog()

                    if (clubListpojo != null && clubListpojo.isNotEmpty()) {

                        if (clubListpojo[0].status.equals("true", false)) {

                            verificationData?.clear()
                            verificationData?.addAll(clubListpojo[0].data)
                            openVerificationCatListBottomSheet(clubListpojo[0].data as ArrayList<VerificationCatData>)
                        } else {
                            MyUtils.dismissProgressDialog()

                        }
                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(frv_rl_main)
                    }
                })

    }

    private fun openVerificationCatListBottomSheet(data: ArrayList<VerificationCatData>) {

        var bottomSheetData = ArrayList<String>()
        bottomSheetData.clear()
        for (i in 0 until data.size) {
            bottomSheetData.add(data[i]!!.verificationcatName!!)
        }

        val bottomSheet = BottomSheetListFragment()
        val bundle = Bundle()
        bundle.putString("from", "verificationcatName")
        bundle.putSerializable("data", bottomSheetData)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")
    }

    override fun onLanguageSelect(value: String, from: String) {
        for (i in 0 until verificationData?.size!!) {
            if (value.equals(verificationData?.get(i)?.verificationcatName, false)) {
                verificationcatID = verificationData?.get(i)?.verificationcatID!!
                verificationcatName = verificationData?.get(i)?.verificationcatName!!
                fev_edit_category.setText(verificationData?.get(i)?.verificationcatName)
                break
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var picturePath: String? = ""
        when (requestCode) {

            1211 -> {

                if (data != null && resultCode == Activity.RESULT_OK && mActivity != null) {
                    var imageUri = data.data
                    var fileName = MyUtils.createFileName(Date(), "")
                    picturePath = MyUtils.getFilePathFromURI(mActivity!!, imageUri!!, fileName)
                    if (picturePath != null) {
                        if (picturePath.contains("https:")) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                getString(R.string.please_select_other_profile_pic),
                                frv_rl_main
                            )
                        } else {
                            mediaImageName = picturePath
                            mfUser_Media_Image = File(picturePath)
                            camera_layout_mediaFile.visibility = View.GONE
                            UploadImage(mfUser_Media_Image!!)


                        }
                    } else
                        MyUtils.showSnackbar(
                            mActivity!!,
                            getString(R.string.please_select_other_profile_pic),
                            frv_rl_main
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

    private fun GetCameraTypedata(mUser: File, Tye: String) {
        var mUser = mUser
        var picturePath = ""
        try {
            picturePath = mUser.absolutePath
            //var bm = BitmapFactory.decodeFile(picturePath)
            mUser = File(picturePath)
            UploadImage(mUser)

            camera_layout_mediaFile.visibility = View.GONE

            mediaImageName = picturePath
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

    }

    private fun UploadImage(picturePath: File) {
        if (MyUtils.isInternetAvailable(mActivity!!)) {
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
                mActivity!!,
                jsonArray.toString(),
                "employment",
                picturePath,
                false,
                object : UploadFileModel.OnUploadFileListener {
                    override fun onSuccessUpload(datumList: UploadImagePojo?) {
//                            MyUtils.dismissProgressDialog()
                        serverfileName = datumList?.fileName!!
                        serverfileSize = datumList?.fileSize!!
                        frv_text_choose_image.text = serverfileName
                    }

                    override fun onFailureUpload(
                        msg: String,
                        datumList: List<UploadImagePojo>?
                    ) {
//                            MyUtils.dismissProgressDialog()


                        try {
                            if (MyUtils.isInternetAvailable(mActivity!!)) {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    resources.getString(R.string.error_crash_error_message),
                                    frv_rl_main
                                )
                            } else {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    resources.getString(R.string.error_common_network),
                                    frv_rl_main
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }


                })
        } else {
            MyUtils.showSnackbar(
                mActivity!!,
                resources.getString(R.string.error_common_network),
                frv_rl_main
            )
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.frv_text_choose_image -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getCameraAndStoragePermision()
                } else {
                    openImageSelection()
                }
            }
            R.id.frv_btn_send -> {
                checkValidation()
            }
            R.id.fev_edit_category -> {
                if (verificationData.isNullOrEmpty()) {
                    VerificationCatList()
                } else {
                    openVerificationCatListBottomSheet(verificationData!!)
                }
            }
            R.id.img_select_camera -> {
                takePhotoFromCamera()
            }
            R.id.img_select_gallery -> {
                openGallery()
            }
            R.id.btnCloseProfileSelection -> {
                camera_layout_mediaFile.visibility = View.GONE
            }


        }
    }

}