package com.nxplayr.fsl.ui.fragments.userprofile.view


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.post.view.CreatePostActivity
import com.nxplayr.fsl.ui.activity.post.view.TransperentActivity
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UploadImagePojo
import com.nxplayr.fsl.fragment.*
import com.nxplayr.fsl.ui.fragments.userprofile.adapter.UserDetailViewPagerAdapter
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.explorepost.view.ExploreFragment
import com.nxplayr.fsl.ui.fragments.feed.view.PostGridViewListFragment
import com.nxplayr.fsl.ui.fragments.userconnection.view.ConnectionsFragment
import com.nxplayr.fsl.ui.fragments.ownprofile.view.UserFeedListsFragment
import com.nxplayr.fsl.ui.fragments.userfollowers.view.FollowersFragment
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.ImageSaver
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.data.model.UploadFileModel
import kotlinx.android.synthetic.main.fragment_explore_main.*
import kotlinx.android.synthetic.main.fragment_photo_gallery.*
import kotlinx.android.synthetic.main.fragment_photo_gallery.viewpager
import kotlinx.android.synthetic.main.fragment_user_detail.*
import kotlinx.android.synthetic.main.layout_select_dialog_camera.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*

@Suppress("DEPRECATION")
class UserDetailFragment : Fragment(), View.OnClickListener {

    var v: View? = null
    var adapter: UserDetailViewPagerAdapter? = null
    var tabposition = String()
    var sessionManager: SessionManager? = null
    var mActivity: AppCompatActivity? = null
    var userData: SignupData? = null
    var fullName = ""
    var userBio = ""
    var fileName = ""
    var serverfileName = ""
    var serverfileCoverPic = ""
    var userId = ""
    var mImageCaptureUri: Uri? = null
    var mfUser: File? = null
    var profileImageName = ""
    var from = ""
    var coverImageName = ""
    var mfUser_Profile_Image: File? = null
    var serverCoverPicName = ""
    var image: FirebaseVisionImage? = null
    var detector: FirebaseVisionFaceDetector? = null
    private lateinit var  loginModel: SignupModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // if (v == null) {
            v = inflater.inflate(R.layout.fragment_user_detail, container, false)
      //  }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupViewModel()
        setupUI()

        if (userData != null) {
            setUserData()
        }
    }
    private fun setupViewModel() {
         loginModel = ViewModelProvider(this@UserDetailFragment).get(SignupModel::class.java)
    }

    private fun setupUI() {
        floating_btn_createpost.visibility = View.GONE
        viewpager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {


            }

            override fun onPageSelected(position: Int) {
                when (position) {
                    1 -> {
                        floating_btn_createpost.visibility = View.VISIBLE
                    }
                    else -> {
                        floating_btn_createpost.visibility = View.GONE
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        edit_image.setOnClickListener(this)

        tv_changeCovorPhoto.setOnClickListener(this)

        img_select_gallery.setOnClickListener(this)

        img_select_camera.setOnClickListener(this)

        btnCloseProfileSelection.setOnClickListener(this)
        image_update_profile.setOnClickListener(this)

        layout_connection.setOnClickListener(this)

        layout_followers.setOnClickListener(this)

        layout_following.setOnClickListener(this)

        setupViewPager(viewpager, "Grid")
        tablayout.setupWithViewPager(viewpager)
        tablayout.tabMode = TabLayout.MODE_SCROLLABLE

        floating_btn_createpost.setOnClickListener(this)

        post_gridIcon.setImageResource(R.drawable.thumb_view_selected)
        post_gridIcon.setOnClickListener(this)
        post_listIcon.setOnClickListener(this)
    }

    private fun setUserData() {
        tv_proflie_user_name.text = userData!!.userFirstName + " " + userData!!.userLastName
        tv_userBio.text = userData?.userBio
        img_user_profile.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
        coverImageProfile.setImageURI(RestClient.image_base_url_users + userData?.userCoverPhoto)

        tv_connections.text = userData!!.totalFriendCount
        tv_followers.text = userData!!.totalFollowerCount
        tv_following.text = userData!!.totalFollowingCount
    }

    private fun setupViewPager(viewpager: ViewPager, from: String) {
        adapter = UserDetailViewPagerAdapter(childFragmentManager, "followersDetails")

        if (from.equals("Grid", false)) {
            adapter?.addFragment(PostGridViewListFragment(), "All")
            adapter?.addFragment(ExploreFragment(), "ProSkills")
            adapter?.addFragment(PostGridViewListFragment(), "Photos")
            adapter?.addFragment(PostGridViewListFragment(), "Videos")
            adapter?.addFragment(PostGridViewListFragment(), "Documents")
            adapter?.addFragment(PostGridViewListFragment(), "Links")
            viewpager.adapter = adapter
            adapter?.notifyDataSetChanged()
        } else if (from.equals("List", false)) {
            adapter?.addFragment(UserFeedListsFragment(), "All")
            adapter?.addFragment(ExploreFragment(), "ProSkills")
            adapter?.addFragment(UserFeedListsFragment(), "Photos")
            adapter?.addFragment(UserFeedListsFragment(), "Videos")
            adapter?.addFragment(UserFeedListsFragment(), "Documents")
            adapter?.addFragment(UserFeedListsFragment(), "Links")
            viewpager.adapter = adapter
            adapter?.notifyDataSetChanged()
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCameraPermissionOther(from: String) {
        val permissionCheck =
            ContextCompat.checkSelfPermission(mActivity!!, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            if (from.equals("profileImage")) {
                takePhotoFromCamera()
            } else {
                takePhotoFromCameraCover()
            }

        } else {
            this.requestPermissions(arrayOf(Manifest.permission.CAMERA), 1003)
        }
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
                        mActivity!!.packageName + ".fileprovider",
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

    private fun takePhotoFromCameraCover() {

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
                        mActivity!!.packageName + ".fileprovider",
                        mfUser!!
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mfUser = null
        }
        startActivityForResult(takePictureIntent, 1414)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getWriteStoragePermissionOther() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(
                mActivity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getReadStoragePermissionOther()
        } else {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1001)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getReadStoragePermissionOther() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(
                mActivity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            openGallery(from)
        } else {
            this.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1002)
        }
    }

    fun openGallery(from: String) {
        if (from.equals("profileImage")) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1211)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1413)
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun GetCameraTypedata(mUser: File, Tye: String) {
        var mUser = mUser
        var picturePath = ""
        try {
            picturePath = mUser.absolutePath
            mUser = File(picturePath)
            img_user_profile.setImageURI(Uri.fromFile(mUser))

            UploadImage(mUser)

            camera_galleryLayout.visibility = View.GONE


            profileImageName = picturePath
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

    }

    @SuppressLint("ResourceAsColor")
    private fun GetCoverCameraTypedata(mUser: File, Tye: String) {
        var mUser = mUser
        var picturePath = ""
        try {
            picturePath = mUser.absolutePath
            //var bm = BitmapFactory.decodeFile(picturePath)
            mUser = File(picturePath)
            coverImageProfile.setImageURI(Uri.fromFile(mUser))

            UploadImage(mUser)

            camera_galleryLayout.visibility = View.GONE

            coverImageName = picturePath
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
                        mActivity!!,
                        getString(R.string.permission_denied),
                        main_content
                    )
                }
            }
            1002 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery(from)
                } else {
                    MyUtils.showSnackbar(
                        mActivity!!,
                        getString(R.string.permission_denied),
                        main_content
                    )
                }
            }

            1003 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    takePhotoFromCamera()
                    getCameraPermissionOther(from)
                } else {
                    MyUtils.showSnackbar(
                        mActivity!!,
                        getString(R.string.permission_denied),
                        main_content
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

                if (data != null && resultCode == Activity.RESULT_OK && this@UserDetailFragment != null) {
                    var imageUri = data.data
                    var fileName = MyUtils.createFileName(Date(), "")
                    picturePath = MyUtils.getFilePathFromURI(mActivity!!, imageUri!!, fileName)
                    if (picturePath != null) {
                        if (picturePath.contains("https:")) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                getString(R.string.please_select_other_profile_pic),
                                main_content
                            )
                        } else {
                            profileImageName = picturePath
                            mfUser_Profile_Image = File(picturePath)

                            camera_galleryLayout.visibility = View.GONE
                            try {
                                detectFace(imageUri, "Gallary", mfUser_Profile_Image!!)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            // UploadImage(mfUser_Profile_Image!!)
                        }
                    } else
                        MyUtils.showSnackbar(
                            mActivity!!,
                            getString(R.string.please_select_other_profile_pic),
                            main_content
                        )
                } else {

//                    MyUtils.showSnackbar(
//                            this,
//                            getString(R.string.please_select_other_profile_pic),
//                            main_content
//                    )

                }
            }
            1413 -> {

                if (data != null && resultCode == Activity.RESULT_OK && this@UserDetailFragment != null) {
                    var imageUri = data.data
                    var fileName = MyUtils.createFileName(Date(), "")
                    picturePathcover = MyUtils.getFilePathFromURI(mActivity!!, imageUri!!, fileName)
                    if (picturePathcover != null) {
                        if (picturePathcover.contains("https:")) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                getString(R.string.please_select_other_profile_pic),
                                main_content
                            )
                        } else {
                            coverImageName = picturePathcover
                            mfUser_Profile_Image = File(picturePathcover)
                            coverImageProfile.setImageURI(imageUri)

                            camera_galleryLayout.visibility = View.GONE
                            UploadImage(mfUser_Profile_Image!!)
                        }
                    } else
                        MyUtils.showSnackbar(
                            mActivity!!,
                            getString(R.string.please_select_other_profile_pic),
                            main_content
                        )
                } else {

//                    MyUtils.showSnackbar(
//                            this,
//                            getString(R.string.please_select_other_profile_pic),
//                            main_content
//                    )

                }
            }
            1212 -> {

                if (resultCode == Activity.RESULT_OK) {
                    if (mfUser != null) {
                        try {
                            camera_galleryLayout.visibility = View.GONE

                            detectFace(Uri.fromFile(mfUser), "Camera", mfUser!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //  GetCameraTypedata(mfUser!!, "Photo")
                    }
                } else {
//                    MyUtils.showSnackbar(this, "Please select valid data", main_content)
                }
            }
            1414 -> {

                if (resultCode == Activity.RESULT_OK) {
                    if (mfUser != null) {
                        GetCoverCameraTypedata(mfUser!!, "Photo")
                    }
                } else {
//                    MyUtils.showSnackbar(this, "Please select valid data", main_content)
                }
            }
        }
    }

    private fun detectFace(bitmap: Uri, s: String, mfUser_Profile_Image: File) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(
                FirebaseVisionFaceDetectorOptions.ACCURATE
            )
            .setLandmarkMode(
                FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS
            )
            .setClassificationMode(
                FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS
            )
            .build()

        // we need to create a FirebaseVisionImage object
        // from the above mentioned image types(bitmap in
        // this case) and pass it to the model.
        try {
            image = FirebaseVisionImage.fromFilePath(mActivity!!, bitmap)
            detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(options)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            MyUtils.dismissProgressDialog()
        }

        // It’s time to prepare our Face Detection model.
        detector!!.detectInImage(image!!)
            .addOnSuccessListener { firebaseVisionFaces ->
                MyUtils.dismissProgressDialog()
                // adding an onSuccess Listener, i.e, in case
// our image is successfully detected, it will
// append it's attribute to the result
// textview in result dialog box.
                var resultText: String? = ""
                var i = 1
                for (face in firebaseVisionFaces) {
                    resultText = resultText + """
                
                FACE NUMBER. $i: 
                """.trimIndent() +
                            ("\nSmile: "
                                    + (face.smilingProbability
                                    * 100) + "%") +
                            ("\nleft eye open: "
                                    + (face.leftEyeOpenProbability
                                    * 100) + "%") +
                            ("\nright eye open "
                                    + (face.rightEyeOpenProbability
                                    * 100) + "%")
                    i++
                }

                // if no face is detected, give a toast
                // message.
                if (firebaseVisionFaces.size == 0) {
                    Toast
                        .makeText(
                            mActivity!!,
                            "NO FACE DETECT",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else {
                    Toast
                        .makeText(
                            mActivity!!,
                            "FACE DETECT",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                    when (s) {
                        "Gallary" -> {
                            img_user_profile.setImageURI(Uri.fromFile(mfUser_Profile_Image))
                            UploadImage(mfUser_Profile_Image)

                        }
                        "Camera" -> {
                            GetCameraTypedata(mfUser_Profile_Image, "Photo")
                        }
                    }

                    /*val bundle = Bundle()
                    bundle.putString(
                            LCOFaceDetection.RESULT_TEXT,
                            resultText)
                    val resultDialog: DialogFragment = ResultDialog()
                    resultDialog.arguments = bundle
                    resultDialog.isCancelable = true
                    resultDialog.show(
                            supportFragmentManager,
                            LCOFaceDetection.RESULT_DIALOG)*/
                }
            } // adding an onfailure listener as well if
            // something goes wrong.
            .addOnFailureListener {
                MyUtils.dismissProgressDialog()
                Toast
                    .makeText(
                        mActivity!!,
                        "Oops, Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                    .show()
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
                "users",
                picturePath,
                false,
                object : UploadFileModel.OnUploadFileListener {
                    override fun onSuccessUpload(datumList: UploadImagePojo?) {
//                            MyUtils.dismissProgressDialog()
                        if (from.equals("profileImage")) {
                            serverfileName = datumList?.fileName!!
                            getUpdateProfilePic(serverfileName)
                        } else {
                            serverCoverPicName = datumList?.fileName!!
                            getUpdateCoverPic(serverCoverPicName)
                        }
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
                                    main_content
                                )
                            } else {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    resources.getString(R.string.error_common_network),
                                    main_content
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
                main_content
            )
        }
    }

    private fun getUpdateProfilePic(serverfileName: String) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("languageID", "1")
            jsonObject.put("userProfilePicture", serverfileName)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("loginuserID", userData?.userID)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
         loginModel.userRegistration(
            mActivity!!,
            false,
            jsonArray.toString(),
            "update_profile_picture"
        )
            .observe(
                this@UserDetailFragment
            ) { loginPojo ->
                if (loginPojo != null) {
                    if (loginPojo.get(0).status.equals("true", true)) {

                        try {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message,
                                main_content
                            )
                            (activity as MainActivity).StoreSessionManager(
                                loginPojo.get(0).data.get(
                                    0
                                )
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            loginPojo.get(0).message,
                            main_content
                        )
                    }

                } else {
                    ErrorUtil.errorMethod(main_content)
                }
            }


    }

    private fun getUpdateCoverPic(serverfileName: String) {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("languageID", "1")
            jsonObject.put("userCoverPhoto", serverfileName)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("loginuserID", userData?.userID)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        loginModel.userRegistration(mActivity!!, false, jsonArray.toString(), "update_cover_photo")
            .observe(
                this@UserDetailFragment,
                { loginPojo ->
                    if (loginPojo != null) {
                        if (loginPojo.get(0).status.equals("true", true)) {
                            try {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    loginPojo.get(0).message,
                                    main_content
                                )
                                (activity as MainActivity).StoreSessionManager(
                                    loginPojo.get(0).data.get(
                                        0
                                    )
                                )

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message,
                                main_content
                            )
                        }

                    } else {
                        ErrorUtil.errorMethod(main_content)
                    }
                })
    }

    fun isPogress(isVisiBle: Boolean) {
        for (frag1 in childFragmentManager.fragments) {
            if (frag1 is ExploreFragment) {
                frag1.isProgress(isVisiBle)
            }

        }
    }

    fun createPost(
        from: String,
        datumList: ArrayList<CreatePostPhotoPojo>,
        stringExtraDes: String,
        stringExtraPrivcy: String,
        Location: String?,
        latitude: String?,
        longitude: String?,
        tag: String?,
        VideoThumb: ArrayList<CreatePostPhotoPojo>,
        radioText: String?,
        connectionTypeIDs: String?
    ) {

        for (frag1 in childFragmentManager.fragments) {
            if (frag1 is ExploreFragment) {
                frag1.createPost(
                    from,
                    datumList,
                    stringExtraDes,
                    stringExtraPrivcy,
                    Location,
                    latitude,
                    longitude,
                    tag,
                    VideoThumb,
                    radioText,
                    connectionTypeIDs
                )
            }

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.edit_image -> {
                from = "profileImage"
                camera_galleryLayout.visibility = View.VISIBLE
                img_select_gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon_unselected_popup))
                img_select_gallery.setColorFilter(resources.getColor(R.color.transperent))
                img_select_camera.setColorFilter(resources.getColor(R.color.transperent))
                img_select_camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_unselected_popup))
            }
            R.id.tv_changeCovorPhoto -> {
                from = "coverImage"
                camera_galleryLayout.visibility = View.VISIBLE
                img_select_gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon_unselected_popup))
                img_select_gallery.setColorFilter(resources.getColor(R.color.transperent))
                img_select_camera.setColorFilter(resources.getColor(R.color.transperent))
                img_select_camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_unselected_popup))
            }
            R.id.img_select_gallery -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWriteStoragePermissionOther()
                    img_select_gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon_selected_popup))
                    img_select_camera.setColorFilter(resources.getColor(R.color.transperent))
                    img_select_camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_unselected_popup))

                } else {
                    openGallery(from)
                }
            }
            R.id.img_select_camera -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    getCameraPermissionOther(from)
                    img_select_camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_selected_popup))
                    img_select_gallery.setColorFilter(resources.getColor(R.color.transperent))
                    img_select_gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon))

                } else {
                    if (from.equals("profileImage")) {
                        takePhotoFromCamera()
                    } else {
                        takePhotoFromCameraCover()
                    }
                }
            }
            R.id.btnCloseProfileSelection -> {
                camera_galleryLayout.visibility = View.GONE
            }
            R.id.image_update_profile -> {
                var bundle = Bundle()
                bundle.putString("type", "NameAndBio")
                bundle.putString("userId", userData?.userID)
                (activity as MainActivity).navigateTo(
                    UpdateUserProfileFragment(),
                    bundle,
                    UpdateUserProfileFragment::class.java.name,
                    true
                )
            }
            R.id.layout_connection -> {
                var bundle = Bundle()
                bundle.putString("fromData", "profile")
                (activity as MainActivity).navigateTo(
                    ConnectionsFragment(),
                    bundle,
                    ConnectionsFragment::class.java.name,
                    true
                )

            }
            R.id.layout_followers -> {
                var bundle = Bundle()
                bundle.putString("fromData", "profile")
                (activity as MainActivity).navigateTo(
                    FollowersFragment(),
                    bundle,
                    FollowersFragment::class.java.name,
                    true
                )

            }
            R.id.layout_following -> {
                var bundle = Bundle()
                bundle.putInt("tabposition", 1)
                bundle.putString("fromData", "profile")
                (activity as MainActivity).navigateTo(
                    FollowersFragment(),
                    bundle,
                    FollowersFragment::class.java.name,
                    true
                )

            }
            R.id.floating_btn_createpost -> {

                when (tablayout.selectedTabPosition) {
                    1 -> {
                        val intent = Intent(mActivity!!, TransperentActivity::class.java)
                        intent.putExtra("From", "Video")
                        intent.putExtra("type", "MyExploreVideo")
                        startActivity(intent)
                        mActivity!!.overridePendingTransition(
                            R.anim.slide_in_up,
                            R.anim.slide_out_up
                        )
                    }
                    else -> {
                        var intent = Intent(mActivity!!, CreatePostActivity::class.java)
                        startActivity(intent)
                        mActivity?.overridePendingTransition(R.anim.slide_up, R.anim.stay)
                    }
                }

            }
            R.id.post_gridIcon -> {
                setupViewPager(viewpager, "Grid")
                post_gridIcon.setImageResource(R.drawable.thumb_view_selected)
                post_listIcon.setImageResource(R.drawable.list_view_unselected)

            }
            R.id.post_listIcon -> {
                setupViewPager(viewpager, "List")
                post_gridIcon.setImageResource(R.drawable.thumb_view_unselected)
                post_listIcon.setImageResource(R.drawable.list_view_selected)

            }
        }
    }


}
