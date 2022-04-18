package com.nxplayr.fsl.ui.activity.post.view

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Location
import android.media.ThumbnailUtils
import android.net.ParseException
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.chand.progressbutton.ProgressButton
import com.darsh.multipleimageselect.activities.AlbumSelectActivity
import com.darsh.multipleimageselect.helpers.Constants
import com.darsh.multipleimageselect.models.Image
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.backends.pipeline.PipelineDraweeController
import com.facebook.imagepipeline.request.ImageRequestBuilder
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.adapter.*
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.activity.*
import com.nxplayr.fsl.ui.activity.addhashtag.view.AddHashtagsActivity
import com.nxplayr.fsl.ui.activity.filterimage.view.EditImageActivityTwo
import com.nxplayr.fsl.ui.activity.fullscreenvideo.view.PlayVideoActivity
import com.nxplayr.fsl.ui.activity.post.adapter.*
import com.nxplayr.fsl.ui.fragments.bottomsheet.PrivacyBottomSheetFragment
import com.nxplayr.fsl.ui.fragments.bottomsheet.SelectPhotoBottomSheetFragment
import com.nxplayr.fsl.ui.fragments.feed.adapter.TrendingItemLinkAdapter
import com.nxplayr.fsl.ui.fragments.feed.adapter.TrendingItemPhotoAdapter
import com.nxplayr.fsl.ui.fragments.feed.adapter.TrendingItemVideoAdapter
import com.nxplayr.fsl.ui.fragments.feed.viewmodel.CreatePostModel
import com.nxplayr.fsl.util.*
import com.nxplayr.fsl.ui.activity.post.viewmodel.CheckStoragetModel
import kotlinx.android.synthetic.main.activity_create_post_two.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_explore_main.*
import kotlinx.android.synthetic.main.item_feed_list.*
import kotlinx.android.synthetic.main.item_post_imagetype.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.tvToolbarTitle
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


@Suppress(
    "DEPRECATED_IDENTITY_EQUALS",
    "DEPRECATION",
    "RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS"
)
class CreatePostActivityTwo : AppCompatActivity(), PrivacyBottomSheetFragment.selectPrivacy,
    View.OnClickListener {

    private val currentApiVersion = Build.VERSION.SDK_INT
    private val permissionsRequestStorage = 99
    private var imageList: ArrayList<CreatePostPhotoPojo> = ArrayList()
    private var videoListThumnail: ArrayList<CreatePostPhotoPojo> = ArrayList()
    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private var listFromPastActivity = ""
    private var placeFromCreatePostSelectPlace = ""
    private var stringFromSelectPlaceActivity = ""
    private var extension: String? = ""
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var latitude = ""
    var longitude = ""
    var Location = ""
    var type = ""
    var url = ""
    var bitmapImage: Bitmap? = null
    private val REQUEST_CODE_LOCATION_PERMISSIONS = 6
    private var locationProvider: LocationProvider? = null
    var LATITUDE: Double? = null
    var LONGITUDE: Double? = null
    var isLocationGot: Boolean = false
    var postData: CreatePostData? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var trendingItemVideoAdapter: TrendingItemVideoAdapter? = null
    var trendingItemPhotoAdapter: TrendingItemPhotoAdapter? = null
    var trendingItemLinkAdapter: TrendingItemLinkAdapter? = null
    var postprocessor: BlurPostprocessor? = null
    var fromType = ""
    var type1 = ""
    var radioText = "Videos"
    var filterImage = ""
    var connectionTypeIDs = ""
    private lateinit var createPostModel: CreatePostModel
    private lateinit var checkStoragetModel: CheckStoragetModel

    private val mYourBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            MyUtils.finishActivity(this@CreatePostActivityTwo, true)
        }
    }
    var postDes = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post_two)
        sessionManager = SessionManager(this@CreatePostActivityTwo)
        userData = sessionManager?.get_Authenticate_User()

        if (intent != null) {
            if (intent.hasExtra("fromType")) {
                fromType = intent?.getStringExtra("fromType")!!
            }
            if (intent.hasExtra("postData")) {

                postData = (intent?.getSerializableExtra("postData") as CreatePostData?)!!

                if (postData != null) {
                    setData()
                }
            }
            if (intent.hasExtra("postDataShare")) {
                postData = (intent?.getSerializableExtra("postDataShare") as CreatePostData?)!!
                if (postData != null) {
                    setShareData()

                }
            }
            listFromPastActivity = if (intent != null && intent.hasExtra("From"))
                intent?.extras!!.getString("From", "")
            else ""
            type = if (intent != null && intent.hasExtra("type"))
                intent?.extras!!.getString("type", "")
            else ""
            type1 = if (intent != null && intent.hasExtra("type1"))
                intent?.extras!!.getString("type1", "")
            else ""

            stringFromSelectPlaceActivity = if (intent != null && intent.hasExtra("Place"))
                intent?.extras!!.getString("Place", "")
            else ""

            latitude = if (intent != null && intent.hasExtra("latitude"))
                intent?.extras!!.getString("latitude", "") else ""

            longitude = if (intent != null && intent.hasExtra("longitude"))
                intent?.extras!!.getString("longitude", "") else ""

            Location = if (intent != null && intent.hasExtra("Location"))
                intent?.extras!!.getString("Location", "") else ""

            placeFromCreatePostSelectPlace = if (intent != null && intent.hasExtra("Location"))
                intent?.extras!!.getString("Location", "")
            else ""

        }

        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.text = resources.getString(R.string.create_post)

        toolbar.setNavigationIcon(R.drawable.back_arrow_signup)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        imageViewProfilePicture.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
        textViewProfileName.text = userData?.userFirstName + " " + userData?.userLastName
        textViewCreatePostPrivacy.setOnClickListener(this)
        ll_add_location_post.setOnClickListener(this)

        tvAddHashTag.setOnClickListener(this)

        LocalBroadcastManager.getInstance(this@CreatePostActivityTwo)
            .registerReceiver(mYourBroadcastReceiver, IntentFilter("CreatePost"))

        when (type1) {
            "MyExploreVideo" -> {
                exploreTtrick.visibility = View.VISIBLE
            }
            else -> {
                exploreTtrick.visibility = View.GONE

            }
        }
        openActivityContent(listFromPastActivity)
        openActivityForPlace(stringFromSelectPlaceActivity)

        menuToolbarItem.setOnClickListener(this)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId) as RadioButton
            radioText = radioButton.text.toString()
        }
    }

    private fun setupViewModel() {
        createPostModel =
            ViewModelProvider(this@CreatePostActivityTwo).get(CreatePostModel::class.java)
        checkStoragetModel =
            ViewModelProvider(this@CreatePostActivityTwo).get(CheckStoragetModel::class.java)

    }

    private fun editExplorePost() {
        MyUtils.showProgressDialog(this@CreatePostActivityTwo, resources.getString(R.string.wait))

        var c = Calendar.getInstance()

        var df = SimpleDateFormat("yyyy-MM-dd")
        var formattedDate = df.format(c.time)

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        val jsonObjectpostSerializedData = JSONObject()
        val jsonArraypostSerializedData = JSONArray()


        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)

            jsonObject.put("postDescription", Constant.encode(createPostEditText.tagText.trim()))
            jsonObject.put("postLanguage", "en")
            jsonObject.put("postID", postData?.postID)


            jsonObject.put("postCategory", "")
            when (textViewCreatePostPrivacy.text.toString().trim()) {
                "Public" -> {
                    jsonObject.put("postPrivacyType", "Public")
                }
                "Connections" -> {
                    jsonObject.put("postPrivacyType", "Connection")
                }
                "Groups" -> {
                    jsonObject.put("postPrivacyType", "Group")
                }
            }
            jsonObject.put("postUploadDate", formattedDate)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when (postData?.postMediaType) {
            "Video" -> {
                if (!postData?.postLocation.isNullOrEmpty()) {
                    jsonObject.put("postLocation", postData?.postLocation)

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!postData?.postLatitude.isNullOrEmpty()) {
                    jsonObject.put("postLatitude", postData?.postLatitude)

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!postData?.postLongitude.isNullOrEmpty()) {
                    jsonObject.put("postLongitude", postData?.postLongitude)

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }

                when (radioText) {
                    "Videos" -> {
                        jsonObject.put("postType", "ExploreVideos")

                    }
                    "Tricks" -> {
                        jsonObject.put("postType", "ExploreTricks")
                    }
                }
                jsonObject.put("postMediaType", "Video")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!postData?.postSerializedData!![0].albummedia.isNullOrEmpty()) {
                        for (i in 0 until postData?.postSerializedData!![0].albummedia.size) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put(
                                    "albummediaFile",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFile
                                )
                                jsonObjectAlbummedia.put(
                                    "albummediaThumbnail",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaThumbnail
                                )
                                jsonObjectAlbummedia.put("albummediaFileType", "Video")
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFileSize
                                )

                                jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                    jsonObject.put("postSerializedData", jsonArraypostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        jsonObject.put("postLanguage", "en")
        jsonArray.put(jsonObject)
        val createPostModel =
            ViewModelProviders.of(this@CreatePostActivityTwo).get(CreatePostModel::class.java)
        createPostModel.apiFunction(this@CreatePostActivityTwo, jsonArray.toString(), fromType)
            .observe(this@CreatePostActivityTwo,
                androidx.lifecycle.Observer { response ->
                    if (!response.isNullOrEmpty()) {
                        MyUtils.dismissProgressDialog()
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(this@CreatePostActivityTwo)
                            val intent2 = Intent("CreatePost")
                            intent2.putExtra("from", "MyExploreVideoEdit")
                            intent2.putExtra("postType", response!![0].data!![0]!!.postType)
                            intent2.putExtra(
                                "explore_video_list",
                                response!![0].data as Serializable
                            )
                            intent2.putExtra("pos", 0)
                            intent2.putExtra("msg", response[0].message)
                            LocalBroadcastManager.getInstance(this@CreatePostActivityTwo)
                                .sendBroadcast(intent2)

                            MyUtils.finishActivity(this@CreatePostActivityTwo, true)
                        } else {
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo)) {
                                if (!response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbar(
                                        this@CreatePostActivityTwo,
                                        response[0].message,
                                        mainLinearLayoutCreatePostTwo
                                    )
                                }

                            } else {
                                MyUtils.showSnackbar(
                                    this@CreatePostActivityTwo,
                                    resources.getString(R.string.error_common_network),
                                    mainLinearLayoutCreatePostTwo
                                )
                            }
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo)) {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_crash_error_message),
                                mainLinearLayoutCreatePostTwo
                            )
                        } else {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_common_network),
                                mainLinearLayoutCreatePostTwo
                            )
                        }
                    }
                })
    }

    private fun setShareData() {
        menuToolbarItem.visibility = View.VISIBLE
        lluserMain.visibility = View.VISIBLE

        tv_name?.text = "${postData?.userFirstName} ${postData?.userLastName}"
        tv_like_count?.text = postData?.postLike
        tv_chat_count?.text = postData?.postComment
        tv_views_count?.text = postData?.postViews
        userImage?.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
        feed_userImg?.setImageURI(RestClient.image_base_url_users + postData?.userProfilePicture)
        ll_share?.visibility = View.VISIBLE
        feed_userImg_share?.setImageURI(RestClient.image_base_url_users + postData?.originalUserProfilePicture)
        tv_name_share?.text = "${postData?.originalUserFirstName} ${postData?.originalUserLastName}"
        blurImage?.visibility = View.VISIBLE
        bluroverlay?.visibility = View.VISIBLE
        postprocessor = BlurPostprocessor(this@CreatePostActivityTwo, 25, 10)

        try {
            val request = ImageRequestBuilder.newBuilderWithSource(
                Uri.parse(
                    RestClient.image_base_url_posts + postData?.postSerializedData?.get(0)?.albummedia?.get(
                        0
                    )?.albummediaFile
                )
            )
                .setPostprocessor(postprocessor)
                .build()
            val controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(blurImage?.controller)
                .build() as PipelineDraweeController
            blurImage?.controller = controller
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (!postData!!.postLocation.isNullOrEmpty()) {
            ll_add_location?.visibility = View.VISIBLE
            tvAddLocation?.visibility = View.VISIBLE
            tvAddLocation?.text = postData?.postLocation
        } else {
            ll_add_location?.visibility = View.GONE
            tvAddLocation?.visibility = View.GONE
        }
        ll_share?.visibility = View.GONE
        setPostDescription(tv_details, postData?.postDescription)
        ll_comment_view?.visibility = View.GONE
        ll_count_view?.visibility = View.GONE
        setFeedData1(
            tv_like_count!!,
            tv_chat_count!!,
            tv_views_count!!,
            img_like!!,
            btn_views!!,
            btn_chat!!,
            btn_share!!,
            btnLike!!
        )

        when (postData?.postMediaType) {
            "Photo" -> {
                linearLayoutManager = LinearLayoutManager(this@CreatePostActivityTwo)
                trendingItemPhotoAdapter = TrendingItemPhotoAdapter(this@CreatePostActivityTwo)
                trendingItemPhotoAdapter!!.setData(postData!!, 0)
                recycle_view_feedhome?.layoutManager = linearLayoutManager
                recycle_view_feedhome?.adapter = trendingItemPhotoAdapter
                recycle_view_feedhome?.setHasFixedSize(true)
            }
            "Document" -> {

            }
            "Link" -> {

                linearLayoutManager = LinearLayoutManager(this@CreatePostActivityTwo)
                trendingItemLinkAdapter = TrendingItemLinkAdapter(this@CreatePostActivityTwo)
                trendingItemLinkAdapter!!.setData(postData!!, 0)
                recycle_view_feedhome?.layoutManager = linearLayoutManager
                recycle_view_feedhome?.adapter = trendingItemLinkAdapter
                recycle_view_feedhome?.setHasFixedSize(true)
            }
            "Video" -> {
                linearLayoutManager = LinearLayoutManager(this@CreatePostActivityTwo)
                trendingItemVideoAdapter = TrendingItemVideoAdapter(this@CreatePostActivityTwo)
                trendingItemVideoAdapter!!.setData(postData)
                recycle_view_feedhome?.layoutManager = linearLayoutManager
                recycle_view_feedhome?.adapter = trendingItemVideoAdapter
                val animator: RecyclerView.ItemAnimator = recycle_view_feedhome?.itemAnimator!!
                if (animator is SimpleItemAnimator) {
                    animator.supportsChangeAnimations = false
                }
            }
        }
    }

    fun setPostDescription(postdescriptionTextview: PostDesTextView, postDescription: String?) {
        if (postData?.postDescription?.isNotEmpty()!!) {
            postdescriptionTextview.visibility = View.VISIBLE
            val postdescription: String = Constant.decode(postDescription)
            postdescriptionTextview.text = postdescription
            postdescriptionTextview.doResizeTextView(
                postdescriptionTextview,
                3,
                "...See More",
                true
            )
            postdescriptionTextview.setCustomEventListener(object :
                PostDesTextView.OnCustomEventListener {


                override fun onViewMore(viewMore: Boolean) {
                }

                override fun onFriendTagClick(friendsId: String?) {
                }
            })
        } else {
            postdescriptionTextview.visibility = View.GONE
        }
    }

    private fun setFeedData1(
        userliketextview: TextView,
        usercommenttextview: TextView,
        userviewtextview: TextView,
        userlikeimageview: ImageView,
        userviewpostImageview: ImageView,
        usercommentpostImageview: ImageView,
        shareImageView: ImageView,
        btnLike: ImageView
    ) {
        if (postData?.youpostShared.equals("Yes", ignoreCase = true)) {
            shareImageView.setImageResource(R.drawable.post_share_icon_selected)
        } else {
            shareImageView.setImageResource(R.drawable.post_share_icon_unselected)
        }
        userliketextview.text = postData?.postLike
        usercommenttextview.text = postData?.postComment
        userviewtextview.text = postData?.postViews
        if (postData?.youpostLiked.equals("Yes", ignoreCase = true)) {
            userlikeimageview.setImageDrawable(resources.getDrawable(R.drawable.like_post_icon_selected))
            btnLike.setImageDrawable(resources.getDrawable(R.drawable.like_post_icon_selected))
        } else {
            userlikeimageview.setImageDrawable(resources.getDrawable(R.drawable.like_post_icon_unselected))
            btnLike.setImageDrawable(resources.getDrawable(R.drawable.like_post_icon_unselected))
        }
        if (postData?.postComment!!.toInt() >= 1) {
            usercommentpostImageview.setImageDrawable(resources.getDrawable(R.drawable.comment_replies_icon_selected))
        } else {
            usercommentpostImageview.setImageDrawable(resources.getDrawable(R.drawable.comment_replies_icon_unselected))
        }

        if (postData?.youpostViews.equals("Yes", ignoreCase = true)) {
            userviewpostImageview.setImageDrawable(resources.getDrawable(R.drawable.post_view_icon_selected))
        } else {
            userviewpostImageview.setImageDrawable(resources.getDrawable(R.drawable.post_view_icon_unselected))
        }
        try {
            //  homeposttimetextview.text = Constant.formatDate(postData?.postCreatedDate, "yyyy-MM-dd hh:mm:ss", "MMM dd 'at' hh:mm aaa")
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun setData() {
        menuToolbarItem.visibility = View.VISIBLE
        textViewCreatePostPrivacy.text = postData?.postPrivacyType
        if (!postData?.connectionTypeIDs.isNullOrEmpty()) {
            connectionTypeIDs = postData?.connectionTypeIDs!!
        }

        if (!postData?.postLocation.isNullOrEmpty()) {
            tvAddLocationPost.text = postData?.postLocation
            ll_add_location_post.visibility = View.VISIBLE
        } else {
            ll_add_location_post.visibility = View.VISIBLE
        }
        when (postData?.postMediaType) {
            "Photo" -> {
                createPostEditText.setEditData(Constant.decode(postData?.postDescription!!))

                initPhotoServerViewPager()
            }
            "Video" -> {
                createPostEditText.setEditData(Constant.decode(postData?.postDescription!!))

                videoViewPagerLinearLayout.visibility = View.VISIBLE
                initVideoServerViewPager()
            }
            "Document" -> {
                createPostEditText.setEditData(Constant.decode(postData?.postDescription!!))

                val extension =
                    postData?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.substring(
                        postData?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile?.lastIndexOf(
                            "."
                        )!!
                    )
                imageList.add(
                    CreatePostPhotoPojo(
                        null,
                        "",
                        false,
                        null,
                        postData?.postSerializedData?.get(0)?.albummedia?.get(0)?.albummediaFile!!,
                        extension!!
                    )
                )
                documentViewLinearLayout.visibility = View.VISIBLE
                initDocumentRV()
            }
            "Link" -> {
                menuToolbarItem.visibility = View.VISIBLE
                createPostEditText.visibility = View.VISIBLE
                var des = if (!postData?.postDescription.isNullOrEmpty()) {

                    postData?.postDescription?.split("||")?.toTypedArray()
                } else {
                    null
                }
                linktext.visibility = View.VISIBLE
                linktext.text = if (des != null) {
                    if (des.size == 1) {
                        Constant.decode(des[0])
                    } else {
                        des[1] + "\n\n" + if (des[2] != null) des[2] else ""
                    }

                } else {
                    ""
                }

                createPostEditText.setEditData(Constant.decode(des!![0]))

                if (des != null && des?.size!! > 1) {
                    url = des[1].toString()
                }
                if (des != null && des?.size!! > 2 && des[2] != null) {
                    postDes = des[2]

                }

                imageViewCancelCameraImage.visibility = View.GONE
                imageViewCameraCaptured.visibility = View.GONE
                singleImageLinearLayout?.visibility = View.VISIBLE
                linkImageview?.visibility = View.VISIBLE
                linkImageview.setImageURI(
                    RestClient.image_base_url_posts + postData?.postSerializedData?.get(
                        0
                    )?.albummedia?.get(0)?.albummediaFile
                )

            }
        }
    }

    fun uploadPostVideo(s: String, stringFromSelectPlaceActivity: String) {

        if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo)) {
            // MyUtils.showProgressDialog(this@CreatePostActivityTwo, "Uploading")
            MyUtils.showProgressDialog(this@CreatePostActivityTwo, "Uploading")


            val jsonArray = JSONArray()
            val jsonObject = JSONObject()
            try {
                jsonObject.put("loginuserID", userData?.userID)
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            jsonArray.put(jsonObject)

            UploadVideo().uploadFileVideo(
                this@CreatePostActivityTwo,
                jsonArray.toString(),
                "post",
                imageList,
                false,
                object : UploadVideo.OnUploadFileListener {


                    override fun onSuccessUpload(datumList: UploadImagePojo?) {
                        MyUtils.dismissProgressDialog()
                        uploadPostVideoThumb(
                            "Uploaded",
                            listFromPastActivity,
                            datumList?.fileName!!
                        )
                    }

                    override fun onFailureUpload(msg: String, datumList: List<UploadImagePojo>?) {
                        MyUtils.dismissProgressDialog()
                        //  ErrorUtil.errorMethod(ll_mainProfile)
                    }

                })
        } else {
            MyUtils.dismissProgressDialog()
            //   ErrorUtil.errorMethod(ll_mainProfile)
        }

    }

    fun uploadPostVideoThumb(s: String, stringFromSelectPlaceActivity: String, fileName: String) {

        if (videoListThumnail != null && videoListThumnail.size > 0) {
            MyUtils.showProgressDialog(this@CreatePostActivityTwo, "Uploading")
            UploadLinkPhoto(
                this@CreatePostActivityTwo,
                videoListThumnail,
                userData?.userID!!,
                mainLinearLayoutCreatePostTwo,
                object : UploadLinkPhoto.OnSuccess {
                    override fun onSuccessUpload(datumList: List<CreatePostPhotoPojo>?) {
                        MyUtils.dismissProgressDialog()

                        if (datumList != null && datumList.isNotEmpty()) {
                            datumList[0].videoThumnailName = fileName
                            createPost(
                                stringFromSelectPlaceActivity,
                                (datumList as ArrayList<CreatePostPhotoPojo>?)!!
                            )

                        }
                    }

                    override fun onFailureUpload(
                        msg: String?,
                        datumList: List<CreatePostPhotoPojo>?
                    ) {
                        MyUtils.dismissProgressDialog()

                        if (datumList != null && datumList.size > 0) {
                            // numberOfImage!!.clear()
                            //numberOfImage?.addAll(datumList)
                        }
                        if (msg?.length!! > 0) {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                msg,
                                mainLinearLayoutCreatePostTwo
                            )
                        } else
                            if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo))
                                MyUtils.showSnackbar(
                                    this@CreatePostActivityTwo,
                                    resources.getString(R.string.error_crash_error_message),
                                    mainLinearLayoutCreatePostTwo
                                ) else MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_common_network),
                                mainLinearLayoutCreatePostTwo
                            )
                    }

                }, "Link"
            )


        }

    }

    fun uploadExploreVideoThumb(s: String, imageList: ArrayList<CreatePostPhotoPojo>) {

        if (videoListThumnail.size > 0) {
            val intent2 = Intent("CreatePost")
            intent2.putExtra("from", "MyExploreVideo")
            intent2.putExtra("Location", tvAddLocationPost.text.toString().trim())
            intent2.putExtra("latitude", latitude)
            intent2.putExtra("longitude", longitude)
            intent2.putExtra("postDescription", createPostEditText.tagText.toString().trim())
            when (textViewCreatePostPrivacy.text.toString().trim()) {
                "Public" -> {
                    intent2.putExtra("postPrivacyType", "Public")

                }
                "Connections" -> {
                    intent2.putExtra("postPrivacyType", "Connection")

                }
                "Groups" -> {
                    intent2.putExtra("postPrivacyType", "Group")

                }
            }
            intent2.putExtra("connectionTypeIDs", connectionTypeIDs)
            intent2.putExtra("videoListThumnail", videoListThumnail)
            intent2.putExtra("radioText", radioText)
            intent2.putExtra("tag", if (!hashTag(
                    createPostEditText.tagText.trim(),
                    this@CreatePostActivityTwo
                ).isNullOrEmpty()
            ) {
                hashTag(createPostEditText.tagText, this@CreatePostActivityTwo).joinToString {
                    it.toString().trim()
                }
            } else {
                ""
            })
            intent2.putExtra("datumList", imageList as Serializable)
            intent2.putExtra("msg", "")
            LocalBroadcastManager.getInstance(this@CreatePostActivityTwo)
                .sendBroadcast(intent2)


        } else {
            MyUtils.showSnackbar(
                this@CreatePostActivityTwo!!,
                "Please go back and Select Video",
                ll_main_explore
            )

        }

    }

    fun uploadPostPhoto(s: String, stringFromSelectPlaceActivity: String) {

        if (imageList != null && imageList.size > 0) {
            CompressPhoto(
                this@CreatePostActivityTwo,
                imageList,
                userData?.userID!!,
                mainLinearLayoutCreatePostTwo,
                object : CompressPhoto.OnSuccess {
                    override fun onSuccessUpload(datumList: List<CreatePostPhotoPojo?>) {
                        if (datumList != null && datumList.isNotEmpty()) {
                            when (stringFromSelectPlaceActivity) {
                                "Photo" -> {
                                    var hashText = if (!hashTag(
                                            createPostEditText.tagText.trim(),
                                            this@CreatePostActivityTwo
                                        ).isNullOrEmpty()
                                    ) {
                                        hashTag(
                                            createPostEditText.tagText.trim(),
                                            this@CreatePostActivityTwo
                                        ).joinToString {
                                            it.toString().trim()
                                        }
                                    } else {
                                        ""
                                    }
                                    val intent2 = Intent("CreatePost")
                                    intent2.putExtra("from", listFromPastActivity)
                                    intent2.putExtra(
                                        "Location",
                                        tvAddLocationPost.text.toString().trim()
                                    )
                                    intent2.putExtra("latitude", latitude)
                                    intent2.putExtra("longitude", longitude)
                                    intent2.putExtra(
                                        "postDescription",
                                        createPostEditText.tagText.toString().trim()
                                    )
                                    when (textViewCreatePostPrivacy.text.toString().trim()) {
                                        "Public" -> {
                                            intent2.putExtra("postPrivacyType", "Public")

                                        }
                                        "Connections" -> {
                                            intent2.putExtra("postPrivacyType", "Connection")

                                        }
                                        "Groups" -> {
                                            intent2.putExtra("postPrivacyType", "Group")

                                        }
                                    }
                                    intent2.putExtra("connectionTypeIDs", connectionTypeIDs)
                                    intent2.putExtra("tag", hashText)
                                    intent2.putExtra("datumList", datumList as Serializable)
                                    intent2.putExtra("msg", "")
                                    LocalBroadcastManager.getInstance(this@CreatePostActivityTwo)
                                        .sendBroadcast(intent2)

                                }

                            }


                        }
                    }

                    override fun onFailureUpload(
                        msg: String?,
                        datumList: List<CreatePostPhotoPojo?>?
                    ) {
                        MyUtils.dismissProgressDialog()

                        if (datumList != null && datumList.size > 0) {
                            // numberOfImage!!.clear()
                            //numberOfImage?.addAll(datumList)
                        }
                        if (msg?.length!! > 0) {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                msg,
                                mainLinearLayoutCreatePostTwo
                            )
                        } else if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo))
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_crash_error_message),
                                mainLinearLayoutCreatePostTwo
                            ) else MyUtils.showSnackbar(
                            this@CreatePostActivityTwo,
                            resources.getString(R.string.error_common_network),
                            mainLinearLayoutCreatePostTwo
                        )
                    }


                }, stringFromSelectPlaceActivity
            )


        }

    }

    fun uploadPostLink(s: String, stringFromSelectPlaceActivity: String) {

        if (imageList != null && imageList.size > 0) {
            UploadLinkPhoto(
                this@CreatePostActivityTwo,
                imageList,
                userData?.userID!!,
                mainLinearLayoutCreatePostTwo,
                object : UploadLinkPhoto.OnSuccess {
                    override fun onSuccessUpload(datumList: List<CreatePostPhotoPojo>?) {
                        if (datumList != null && datumList.isNotEmpty()) {
                            when (stringFromSelectPlaceActivity) {
                                "Link" -> {
                                    createPost(
                                        listFromPastActivity,
                                        (datumList as ArrayList<CreatePostPhotoPojo>?)!!
                                    )
                                }
                            }


                        }
                    }

                    override fun onFailureUpload(
                        msg: String?,
                        datumList: List<CreatePostPhotoPojo>?
                    ) {
                        MyUtils.dismissProgressDialog()

                        if (datumList != null && datumList.size > 0) {
                            // numberOfImage!!.clear()
                            //numberOfImage?.addAll(datumList)
                        }
                        if (msg?.length!! > 0) {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                msg,
                                mainLinearLayoutCreatePostTwo
                            )
                        } else
                            if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo))
                                MyUtils.showSnackbar(
                                    this@CreatePostActivityTwo,
                                    resources.getString(R.string.error_crash_error_message),
                                    mainLinearLayoutCreatePostTwo
                                ) else MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_common_network),
                                mainLinearLayoutCreatePostTwo
                            )
                    }

                }, listFromPastActivity
            )
        }

    }

    fun uploadPostDocument(s: String, stringFromSelectPlaceActivity: String) {

        if (imageList != null && imageList.size > 0) {
            UploadDocument(
                this@CreatePostActivityTwo,
                imageList,
                userData?.userID!!,
                mainLinearLayoutCreatePostTwo,
                object : UploadDocument.OnSuccess {
                    override fun onSuccessUpload(datumList: List<CreatePostPhotoPojo>?) {
                        if (datumList != null && datumList.isNotEmpty()) {
                            when (stringFromSelectPlaceActivity) {
                                "Document" -> {
                                    createPost(
                                        listFromPastActivity,
                                        (datumList as ArrayList<CreatePostPhotoPojo>?)!!
                                    )
                                }
                            }


                        }
                    }

                    override fun onFailureUpload(
                        msg: String?,
                        datumList: List<CreatePostPhotoPojo>?
                    ) {
                        MyUtils.dismissProgressDialog()

                        if (datumList != null && datumList.size > 0) {
                            // numberOfImage!!.clear()
                            //numberOfImage?.addAll(datumList)
                        }
                        if (msg?.length!! > 0) {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                msg,
                                mainLinearLayoutCreatePostTwo
                            )
                        } else
                            if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo))
                                MyUtils.showSnackbar(
                                    this@CreatePostActivityTwo,
                                    resources.getString(R.string.error_crash_error_message),
                                    mainLinearLayoutCreatePostTwo
                                ) else MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_common_network),
                                mainLinearLayoutCreatePostTwo
                            )
                    }

                }, listFromPastActivity
            )
        }

    }

    private fun openActivityContent(string: String) {

        when (string) {
            "Text" -> {
                openCreatePostForText()
            }
            "Document" -> {
                openCreatePostForDocument()
            }
            "Connection" -> {
                //openCreatePostForConnection()
            }
            "Photo" -> {
                when (type) {
                    "Gallery" -> {
                        val intent =
                            Intent(this@CreatePostActivityTwo, AlbumSelectActivity::class.java)
                        //set limit on number of images that can be selected, default is 10
                        intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5)
                        startActivityForResult(intent, 1211)

                    }
                    "CameraPhoto" -> {
                        try {
                            var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(intent, 1212)
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@CreatePostActivityTwo, "Please install a File Manager.",
                                Toast.LENGTH_SHORT
                            ).show()
                            e.printStackTrace()
                        }
                    }

                }
            }
            "Get VIP" -> {
                //openCreatePostForGetVIP()
            }
            "Video" -> {
                when (type) {
                    "Gallery" -> {
                        val intent =
                            Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                        intent.type = "video/*"
                        try {
                            // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                            intent.action = Intent.ACTION_GET_CONTENT
                            startActivityForResult(
                                Intent.createChooser(intent, "Select Video"),
                                1214
                            )
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@CreatePostActivityTwo, "Please install a File Manager.",
                                Toast.LENGTH_SHORT
                            ).show()
                            e.printStackTrace()
                        }
                    }
                    "CameraPhoto" -> {
                        try {
                            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                            intent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true)
                            startActivityForResult(intent, 1215)
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@CreatePostActivityTwo, "Please install a File Manager.",
                                Toast.LENGTH_SHORT
                            ).show()
                            e.printStackTrace()
                        }
                    }
                }
            }
            "Link" -> {
                if (intent != null) {
                    if (intent?.hasExtra("bitmapImage")!!) {
                        bitmapImage = intent?.getParcelableExtra("bitmapImage")!!
                        //val matrix = Matrix()
                        //matrix.postRotate(180F)
                        menuToolbarItem.visibility = View.VISIBLE
                        createPostEditText.visibility = View.VISIBLE
                        //val bitmap2 = Bitmap.createBitmap(bitmapImage!!, 0, 0, bitmapImage?.width!!, bitmapImage?.height!!, matrix, true)
                        if (intent.hasExtra("postDes")) {
                            postDes = intent.getStringExtra("postDes")!!
                            linktext.visibility = View.VISIBLE
                        }
                        if (intent.hasExtra("url")) {
                            url = intent.getStringExtra("url")!!

                        }

                        linktext.text = url + "\n\n" + postDes

                        imageViewCancelCameraImage.visibility = View.GONE
                        var saveBitmap1 = savebitmap(bitmapImage!!)
                        imageUri = Uri.parse(saveBitmap1.absolutePath)
                        singleImageLinearLayout?.visibility = View.VISIBLE

                        imageList.add(
                            CreatePostPhotoPojo(
                                imageUri, saveBitmap1.absolutePath, false,
                                null,
                                ""
                            )
                        )
                        imageViewCameraCaptured.setImageBitmap(bitmapImage)


                    }

                }
                //openCreatePostForLink()
            }
            "Donate" -> {
                // openCreatePostForDonate()
            }
        }

    }

    private fun openActivityForPlace(string: String) {
        when (string) {
            "Place" -> {
                getPlace()

            }
        }
    }

    private fun getPlace() {

        textViewPlaceAT.visibility = View.VISIBLE
        tvAddLocationPost.visibility = View.VISIBLE
        tvAddLocationPost.text = Location

        // ***************************selected location latlong -->
        val locationAddress = SelectedLocationLatLong()
        locationAddress.getAddressFromLocation(
            placeFromCreatePostSelectPlace,
            applicationContext, LocationHandler()
        )
        //***************************to change place
        createPostEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                val textFromCreatePostEditText = createPostEditText.text.toString().trim()
                if (textFromCreatePostEditText.isNotEmpty()) {
                    menuToolbarItem.visibility = View.VISIBLE
                } else {
                    menuToolbarItem.visibility = View.INVISIBLE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    @SuppressLint("HandlerLeak")
    private inner class LocationHandler : Handler() {
        override fun handleMessage(message: Message) {
            val locationAddress: String?
            when (message.what) {
                1 -> {
                    val bundle = message.data
                    locationAddress = bundle.getString("address")
                }
                else -> locationAddress = null
            }
            //Toast.makeText(this@CreatePostActivityTwo, locationAddress, Toast.LENGTH_LONG).show()
        }
    }

    private fun openCreatePostForText() {

        createPostEditText.visibility = View.VISIBLE
        menuToolbarItem.visibility = View.INVISIBLE

        createPostEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

                val textFromCreatePostEditText = createPostEditText.text.toString().trim()
                if (textFromCreatePostEditText.isNotEmpty()) {
                    menuToolbarItem.visibility = View.VISIBLE
                } else {
                    menuToolbarItem.visibility = View.INVISIBLE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

    }

    private fun openCreatePostForDocument() {
        val intent = Intent()
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        val mimeTypes = arrayOf(
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",// 00 doc & docx
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation", //01 .ppt && .pptx
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",//02 .xls & .xlsx
            "application/pdf"//03 .pdf
        )
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        intent.action = Intent.ACTION_OPEN_DOCUMENT
        startActivityForResult(intent, 1213)

    }

    @SuppressLint("WrongConstant", "Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        when (requestCode) {

            LocationProvider.CONNECTION_FAILURE_RESOLUTION_REQUEST -> {
                connectLocation()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, intentData)
            }

        }
        when (resultCode) {
            115 -> {
                if (intentData != null) {
                    var hashtagName = intentData.getStringExtra("add_hashtag")
                    createPostEditText.setText(hashtagName)
                }
            }
            1002 -> {
                listFromPastActivity = if (intentData != null && intentData.hasExtra("From"))
                    intentData.extras!!.getString("From", "")
                else ""

                stringFromSelectPlaceActivity =
                    if (intentData != null && intentData.hasExtra("Place"))
                        intentData.extras!!.getString("Place", "")
                    else ""

                latitude = if (intentData != null && intentData.hasExtra("latitude"))
                    intentData.extras!!.getDouble("latitude", 0.00).toString() else ""

                longitude = if (intentData != null && intentData.hasExtra("longitude"))
                    intentData.extras!!.getDouble("longitude", 0.00).toString() else ""

                Location = if (intentData != null && intentData.hasExtra("Location"))
                    intentData.extras!!.getString("Location", "") else ""

                placeFromCreatePostSelectPlace =
                    if (intentData != null && intentData.hasExtra("LocationAddress"))
                        intentData.extras!!.getString("LocationAddress", "") else ""


                openActivityForPlace(stringFromSelectPlaceActivity)

            }
            701 -> {
                var mSaveImageUri = intentData?.getStringExtra("mSaveImageUri")
                var posEditImage = intentData?.getIntExtra("posEditImage", -1)
                imageUri = Uri.parse(mSaveImageUri)

                try {
                    imageList.set(
                        posEditImage!!, CreatePostPhotoPojo(
                            imageUri, imageUri?.path, false,
                            null,
                            ""
                        )
                    )
                    initPhotoViewPager()


                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            800 -> {
                textViewCreatePostPrivacy.text = intentData?.getStringExtra("postPrivacyType")
                connectionTypeIDs = intentData?.getStringExtra("connectionTypeIDs")!!

            }
            else -> {
                super.onActivityResult(requestCode, resultCode, intentData)
            }

        }
        when (listFromPastActivity) {
            "Photo" -> {
                if (requestCode == 1211) {

                    if (resultCode == RESULT_OK) {

                        photoViewPagerLinearLayout.visibility = View.VISIBLE
                        val images =
                            intentData!!.getParcelableArrayListExtra<Image>(Constants.INTENT_EXTRA_IMAGES)

                        imageUri = intentData.data
                        try {
                            for (i in images!!.indices) {
                                imageList.add(
                                    CreatePostPhotoPojo(
                                        imageUri, images[i].path, false,
                                        null,
                                        ""
                                    )
                                )
                            }
//                            indicatorForPhotos.visibility = View.VISIBLE

                            initPhotoViewPager()


                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {

                        MyUtils.finishActivity(this@CreatePostActivityTwo, true)

                    }


                } else if (requestCode == 1212) {
                    if (intentData != null) {

                        singleImageLinearLayout.visibility = View.VISIBLE
                        menuToolbarItem.visibility = View.VISIBLE

                        try {
                            val bitmap: Bitmap = intentData.extras!!.get("data") as Bitmap
                            val matrix = Matrix()
                            matrix.postRotate(90F)
                            val bitmap2 = Bitmap.createBitmap(
                                bitmap,
                                0,
                                0,
                                bitmap.width,
                                bitmap.height,
                                matrix,
                                true
                            )
                            imageUri = Uri.parse(savebitmap(bitmap2).absolutePath)
                            imageList.add(
                                CreatePostPhotoPojo(
                                    imageUri, savebitmap(bitmap2).absolutePath, false,
                                    null,
                                    ""
                                )
                            )

//                            indicatorForPhotos.visibility = View.VISIBLE

                            initPhotoViewPager()

                            lylEdit.visibility = View.VISIBLE
                            imageViewCameraCaptured.setImageBitmap(bitmap2)

                            lylEdit.setOnClickListener {
                                var i_image = Intent(
                                    this@CreatePostActivityTwo,
                                    EditImageActivityTwo::class.java
                                )
                                i_image.putExtra("image_path", imageList[0].imagePath)
                                i_image.putExtra("posEditImage", 0)
                                startActivityForResult(i_image, 701)
                            }


                        } catch (e: Resources.NotFoundException) {
                            e.printStackTrace()
                        }
                    } else {
                        MyUtils.showSnackbar(
                            this,
                            "Please select valid data",
                            mainLinearLayoutCreatePostTwo
                        )
                        MyUtils.finishActivity(this@CreatePostActivityTwo, true)
                    }

                }
            }
            "Document" -> if (requestCode == 1213) {
                if (resultCode == RESULT_OK) {
                    when {
                        intentData!!.clipData != null -> {
                            val count = intentData.clipData?.itemCount
                            var currentItem = 0

                            while (currentItem < count!!) {

                                val uriFileExtension =
                                    intentData.clipData?.getItemAt(currentItem)!!.uri
                                val cursor: Cursor? = contentResolver.query(
                                    uriFileExtension!!, null,
                                    null, null,
                                    null, null
                                )
                                var displayName = ""
                                currentItem += 1

                                try {
                                    cursor?.use {
                                        if (it.moveToFirst()) {
                                            displayName =
                                                it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                            // Log.i("Tag", "Display Name: $displayName")

                                            val sizeIndex: Int =
                                                it.getColumnIndex(OpenableColumns.SIZE)
                                            //                            val size: String = if (!it.isNull(sizeIndex)) { it.getString(sizeIndex) }
                                            //                            else { "Unknown" }
                                            val contentResolver = contentResolver
                                            val mimeTypeMap = MimeTypeMap.getSingleton()
                                            extension = mimeTypeMap.getExtensionFromMimeType(
                                                contentResolver.getType(uriFileExtension)
                                            )
                                        }
                                    }
                                    imageList.add(
                                        CreatePostPhotoPojo(
                                            uriFileExtension, uriFileExtension.path, false,
                                            null,
                                            displayName, extension!!
                                        )
                                    )
                                    documentViewLinearLayout.visibility = View.VISIBLE
                                    initDocumentRV()

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                        }
//for single document
                        intentData.data != null -> {


                            val uriFileExtension = intentData.data
                            /*var PATH_FILE = uriFileExtension.toString();
                                if (PATH_FILE.substring(0, 1).equals("c")) { // ES
                                    PATH_FILE = "file://" + PATH_FILE.substring(28,PATH_FILE.length);
                                }
                                if (PATH_FILE.toLowerCase().startsWith("file://")) {
                                    PATH_FILE = (File(URI.create(PATH_FILE))).getAbsolutePath();
                                }*/
                            val cursor: Cursor? = contentResolver.query(
                                uriFileExtension!!, null,
                                null, null,
                                null, null
                            )

                            var displayName = ""
                            try {
                                cursor?.use {
                                    if (it.moveToFirst()) {
                                        displayName =
                                            it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                                    }
                                }
                                val contentResolver = contentResolver
                                val mimeTypeMap = MimeTypeMap.getSingleton()
                                extension = mimeTypeMap.getExtensionFromMimeType(
                                    contentResolver.getType(uriFileExtension)
                                )
                                var fileNameFile: File? = null
                                var fileNameString = ""

                                if (getPathVideo(uriFileExtension) != null) {
                                    fileNameFile = File(getPathVideo(uriFileExtension))
                                    fileNameString = getPathVideo(uriFileExtension)!!
                                } else {
                                    if (getPathFromInputStreamUriDocument(
                                            uriFileExtension,
                                            extension,
                                            displayName
                                        ) != null
                                    ) {
                                        fileNameFile = File(
                                            getPathFromInputStreamUriDocument(
                                                uriFileExtension,
                                                extension,
                                                displayName
                                            )
                                        )
                                        fileNameString = getPathFromInputStreamUriDocument(
                                            uriFileExtension,
                                            extension,
                                            displayName
                                        )
                                    }
                                }

                                Log.e("file", "" + fileNameFile + "\n\n" + fileNameString)
                                imageList.add(
                                    CreatePostPhotoPojo(
                                        uriFileExtension, fileNameString, false,
                                        null,
                                        fileNameFile?.name!!, extension!!
                                    )
                                )
                                documentViewLinearLayout.visibility = View.VISIBLE
                                initDocumentRV()

                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        else -> {
                            MyUtils.showSnackbar(
                                this,
                                "Please select valid data",
                                mainLinearLayoutCreatePostTwo
                            )
                            MyUtils.finishActivity(this@CreatePostActivityTwo, true)
                        }

                    }
                } else {
                    MyUtils.showSnackbar(
                        this,
                        "Please select valid data",
                        mainLinearLayoutCreatePostTwo
                    )
                    MyUtils.finishActivity(this@CreatePostActivityTwo, true)

                }
            }
            "Video" -> {
                if (requestCode == 1214 || requestCode == 1215) {
                    if (requestCode == 1214) {

                        if (resultCode == RESULT_OK) {
                            when {
                                intentData?.data != null -> {
                                    videoViewPagerLinearLayout.visibility = View.VISIBLE
                                    videoUri = intentData.data
                                    var fileNameFile: File? = null
                                    var fileNameString = ""

                                    if (getPathVideo(videoUri!!) != null) {
                                        fileNameFile = File(getPathVideo(videoUri!!))
                                        fileNameString = getPathVideo(videoUri!!)!!
                                    } else {
                                        if (getPathFromInputStreamUriVideo(videoUri!!) != null) {
                                            fileNameFile =
                                                File(getPathFromInputStreamUriVideo(videoUri!!))
                                            fileNameString =
                                                getPathFromInputStreamUriVideo(videoUri!!)
                                        }
                                    }

                                    try {
                                        if (fileNameString != null) {
                                            val fileName1: String =
                                                MyUtils.createFileName(Date(), "Image")!!

                                            var thumb = ThumbnailUtils.createVideoThumbnail(
                                                fileNameString,
                                                MediaStore.Images.Thumbnails.MINI_KIND
                                            )

                                            val file1 =
                                                ImageSaver(this@CreatePostActivityTwo).setExternal(
                                                    true
                                                ).setFileName(fileName1)
                                                    .save(thumb!!)
                                            imageList.clear()
                                            imageList.add(
                                                CreatePostPhotoPojo(
                                                    videoUri, fileNameString, false,
                                                    null, "", "VideoFile"
                                                )
                                            )
                                            videoListThumnail.clear()
                                            videoListThumnail.add(
                                                CreatePostPhotoPojo(
                                                    Uri.parse(file1.path),
                                                    file1.absolutePath,
                                                    false,
                                                    null,
                                                    file1.name,
                                                    "ImageFile"
                                                )
                                            )
//                                        indicatorForVideo.visibility = View.GONE

                                            initVideoViewPager()
                                        } else {
                                            MyUtils.showSnackbar(
                                                this,
                                                "Please select Video",
                                                mainLinearLayoutCreatePostTwo
                                            )
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                }
                                else -> {
                                    MyUtils.showSnackbar(
                                        this,
                                        "Please select valid data",
                                        mainLinearLayoutCreatePostTwo
                                    )
                                    MyUtils.finishActivity(this@CreatePostActivityTwo, true)
                                }
                            }

                        } else {
                            MyUtils.showSnackbar(
                                this,
                                "Please select valid data",
                                mainLinearLayoutCreatePostTwo
                            )
                            MyUtils.finishActivity(this@CreatePostActivityTwo, true)

                        }
                    } else if (requestCode == 1215) {
                        videoViewPagerLinearLayout.visibility = View.VISIBLE
                        if (intentData != null) {

                            try {
                                val videoUri = intentData.data
                                var fileNameFile: File? = null
                                var fileNameString = ""

                                if (getPathVideo(videoUri!!) != null) {
                                    fileNameFile = File(getPathVideo(videoUri))
                                    fileNameString = getPathVideo(videoUri)!!
                                } else {
                                    if (getPathFromInputStreamUriVideo(videoUri) != null) {
                                        fileNameFile =
                                            File(getPathFromInputStreamUriVideo(videoUri))
                                        fileNameString = getPathFromInputStreamUriVideo(videoUri)
                                    }
                                }
                                // val fileName1: String = MyUtils.createFileName(Date(), "Image")!!


                                var thumb = getVidioThumbnail(fileNameFile.toString())
                                val fileName1: String = MyUtils.createFileName(Date(), "Image")!!
                                val file1 =
                                    ImageSaver(this@CreatePostActivityTwo).setExternal(true)
                                        .setFileName(fileName1)
                                        .save(thumb!!)

                                imageList.clear()
                                imageList.add(CreatePostPhotoPojo(videoUri, fileNameString))
                                videoListThumnail.clear()
                                videoListThumnail.add(
                                    CreatePostPhotoPojo(
                                        Uri.parse(file1.path), file1.absolutePath, false,
                                        null, file1.name, "ImageFile"
                                    )
                                )

                                initVideoViewPager()

                            } catch (e: Resources.NotFoundException) {
                                e.printStackTrace()
                            }
                        } else {
                            MyUtils.showSnackbar(
                                this,
                                "Please select valid data",
                                mainLinearLayoutCreatePostTwo
                            )
                            MyUtils.finishActivity(this@CreatePostActivityTwo, true)


                        }
                    } else {
                        MyUtils.showSnackbar(
                            this,
                            "Please select valid data",
                            mainLinearLayoutCreatePostTwo
                        )
                        MyUtils.finishActivity(this@CreatePostActivityTwo, true)

                    }
                }
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, intentData)
            }
        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            permissionsRequestStorage -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (listFromPastActivity == "Photo") {
                        getImageOrVideoFromCameraOrGalleryBottomSheet(listFromPastActivity)
                    } else if (listFromPastActivity == "Video") {
                        getImageOrVideoFromCameraOrGalleryBottomSheet(listFromPastActivity)
                    }

                } else {

                    Toast.makeText(
                        this,
                        resources.getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            REQUEST_CODE_LOCATION_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connectLocation()
                } else {

                    Toast.makeText(
                        this,
                        resources.getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) + ContextCompat
                .checkSelfPermission(
                    this.applicationContext!!,
                    Manifest.permission.CAMERA
                ) + ContextCompat
                .checkSelfPermission(
                    this.applicationContext!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) !== PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) ||
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission
                            .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission
                            .WRITE_EXTERNAL_STORAGE
                    ),
                    permissionsRequestStorage
                )

            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission
                            .READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    permissionsRequestStorage
                )
            }
            return false
        } else {
            getImageOrVideoFromCameraOrGalleryBottomSheet(listFromPastActivity)
            return true
        }
    }

    private fun getImageOrVideoFromCameraOrGalleryBottomSheet(string: String) {
        val selectPhotoBottomSheetFragment = SelectPhotoBottomSheetFragment()
        val bundle = Bundle()
        selectPhotoBottomSheetFragment.isCancelable = false
        bundle.putString("Typeof", "Image")
        selectPhotoBottomSheetFragment.arguments = bundle
        selectPhotoBottomSheetFragment.show(supportFragmentManager, "")


        if (string == "Photo") {
            selectPhotoBottomSheetFragment.setListener(object :
                SelectPhotoBottomSheetFragment.OnItemSelectedListener {
                override fun onselectoption(SelectName: String) {
                    when (SelectName) {
                        "Gallery" -> {
                            val intent =
                                Intent(this@CreatePostActivityTwo, AlbumSelectActivity::class.java)
                            //set limit on number of images that can be selected, default is 10
                            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5)
                            startActivityForResult(intent, 1211)

                            /* val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"*/

        try {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1211)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                    this@CreatePostActivityTwo, "Please install a File Manager.",
                    Toast.LENGTH_SHORT
            ).show()
            e.printStackTrace()
        }*/

                        }
                        "CameraPhoto" -> {
                            try {
                                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, 1212)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@CreatePostActivityTwo, "Please install a File Manager.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
        } else if (string == "Video") {
            selectPhotoBottomSheetFragment.setListener(object :
                SelectPhotoBottomSheetFragment.OnItemSelectedListener {
                @SuppressLint("ObsoleteSdkInt")
                override fun onselectoption(SelectName: String) {
                    when (SelectName) {
                        "Gallery" -> {
                            val intent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                            )
                            intent.type = "video/*"
                            try {
                                // intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                                intent.action = Intent.ACTION_GET_CONTENT
                                startActivityForResult(
                                    Intent.createChooser(intent, "Select Video"),
                                    1214
                                )
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@CreatePostActivityTwo, "Please install a File Manager.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                e.printStackTrace()
                            }
                        }
                        "CameraPhoto" -> {
                            try {
                                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                                intent.putExtra(MediaStore.EXTRA_FULL_SCREEN, true)
                                startActivityForResult(intent, 1215)
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@CreatePostActivityTwo, "Please install a File Manager.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                e.printStackTrace()
                            }
                        }
                    }
                }
            })
        }
    }

    //privacy section start
    private fun getPrivacy() {
        val privacyList: ArrayList<CreatePostPrivacyPojo> = ArrayList()
        privacyList.clear()
        privacyList.add(CreatePostPrivacyPojo("Public", R.drawable.public_icon))
        privacyList.add(CreatePostPrivacyPojo("Connections", R.drawable.connection_icon))
        privacyList.add(CreatePostPrivacyPojo("Groups", R.drawable.group_icon))
        openBottomSheet(privacyList)
    }

    private fun openBottomSheet(data: ArrayList<CreatePostPrivacyPojo>) {

        val bottomSheet = PrivacyBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", data as Serializable)
        bottomSheet.arguments = bundle
        bottomSheet.show(supportFragmentManager, "List")
    }

    override fun setPrivacy(data: CreatePostPrivacyPojo, position: Int) {
        textViewCreatePostPrivacy.text = data.name_Privacy
    }

    private fun initPhotoViewPager() {
        var currentPage = 0
        menuToolbarItem.visibility = View.VISIBLE
        selectedPhotoViewPager!!.adapter = CreatePostPhotoAdapter(this, this.imageList,
            object : CreatePostPhotoAdapter.OnClickToCancelPhoto {
                override fun onDelete(imageList: ArrayList<CreatePostPhotoPojo>, position: Int) {
                    if (imageList.size == 1) {
                        imageList.removeAt(position)
                        selectedPhotoViewPager!!.adapter?.notifyDataSetChanged()
                    } else {
                        imageList.removeAt(position)
                        selectedPhotoViewPager!!.adapter?.notifyDataSetChanged()

                    }
                }

                override fun onEditImage(imageList: ArrayList<CreatePostPhotoPojo>, position: Int) {
                    var i_image =
                        Intent(this@CreatePostActivityTwo, EditImageActivityTwo::class.java)
                    i_image.putExtra("image_path", imageList[position].imagePath)
                    i_image.putExtra("posEditImage", position)
                    startActivityForResult(i_image, 701)
                }
            })

        val density = resources.displayMetrics.density

        val numberOfPages = imageList.size

        Runnable {
            if (currentPage == numberOfPages) {
                currentPage = 0
            }
            selectedPhotoViewPager!!.setCurrentItem(currentPage++, true)
        }

    }

    private fun initPhotoServerViewPager() {
        var currentPage = 0
        menuToolbarItem.visibility = View.VISIBLE
        photoViewPagerLinearLayout.visibility = View.VISIBLE
        selectedPhotoViewPager!!.adapter =
            CreatePostPhotoServerAdapter(this, postData?.postSerializedData!![0].albummedia)

        val density = resources.displayMetrics.density

        val numberOfPages = postData?.postSerializedData!![0].albummedia.size

        Runnable {
            if (currentPage == numberOfPages) {
                currentPage = 0
            }
            selectedPhotoViewPager!!.setCurrentItem(currentPage++, true)
        }

    }

    @SuppressLint("WrongConstant")
    private fun initDocumentRV() {

        var adapter: CreatePostDocumentAdapter? = null
        menuToolbarItem.visibility = View.VISIBLE
        adapter = CreatePostDocumentAdapter(
            this,
            imageList,
            object : CreatePostDocumentAdapter.OnClickToOpenListener {
                override fun onClickToOpen(
                    documentArrayList: ArrayList<CreatePostPhotoPojo>,
                    position: Int
                ) {

                    //to open document file
                }

            },
            object : CreatePostDocumentAdapter.OnItemClickListener {
                override fun onClicked(
                    documentArrayList: ArrayList<CreatePostPhotoPojo>,
                    position: Int
                ) {

                    documentArrayList.removeAt(position)
                    adapter!!.notifyDataSetChanged()
                    if (documentArrayList.isNullOrEmpty()) {
                        menuToolbarItem.visibility = View.GONE
                        onBackPressed()
                    }
                }
            })
        createPostDocumentCard!!.layoutManager = GridLayoutManager(
            applicationContext, 2, GridLayoutManager.VERTICAL,
            false
        )
        createPostDocumentCard!!.adapter = adapter

    }

    private fun initVideoViewPager() {
        menuToolbarItem.visibility = View.VISIBLE
        var currentVideoPage = 0
        selectedVideoViewPager.adapter = CreatePostVideoAdapter(
            this,
            imageList,
            object : CreatePostVideoAdapter.OnClickToPlayVideo {
                override fun onPlay(videoList: ArrayList<CreatePostPhotoPojo>, position: Int) {
                    val intent = Intent(this@CreatePostActivityTwo, PlayVideoActivity::class.java)
                    intent.putExtra("Video", videoList[position].image.toString())
                    startActivity(intent)
                }

            },
            object : CreatePostVideoAdapter.OnClickToCancelVideo {
                override fun onDelete(videoList: ArrayList<CreatePostPhotoPojo>, position: Int) {

                    if (videoList.size == 1) {
                        videoList.removeAt(position)
                        selectedVideoViewPager.adapter?.notifyDataSetChanged()
                        MyUtils.finishActivity(this@CreatePostActivityTwo, true)
                    } else {
                        videoList.removeAt(position)
                        selectedVideoViewPager.adapter?.notifyDataSetChanged()
                    }
                }
            })
        val numberOfVideoPages = imageList.size

        Runnable {
            if (currentVideoPage == numberOfVideoPages) {
                currentVideoPage = 0
            }
            selectedVideoViewPager!!.setCurrentItem(currentVideoPage++, true)
        }
    }

    private fun initVideoServerViewPager() {
        menuToolbarItem.visibility = View.VISIBLE
        var currentVideoPage = 0
        selectedVideoViewPager.adapter =
            CreatePostVideoServerAdapter(this, postData?.postSerializedData!![0].albummedia)
        val numberOfVideoPages = imageList.size
        Runnable {
            if (currentVideoPage == numberOfVideoPages) {
                currentVideoPage = 0
            }
            selectedVideoViewPager!!.setCurrentItem(currentVideoPage++, true)
        }
    }

    fun createPost(
        listFromPastActivity: String,
        datumList: ArrayList<CreatePostPhotoPojo>

    ) {
        MyUtils.showProgressDialog(this@CreatePostActivityTwo, resources.getString(R.string.wait))

        var c = Calendar.getInstance()

        var df = SimpleDateFormat("yyyy-MM-dd")
        var formattedDate = df.format(c.time)

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        val jsonObjectpostSerializedData = JSONObject()
        val jsonArraypostSerializedData = JSONArray()

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("postHashTags",
                if (!hashTag(
                        createPostEditText.tagText.trim(),
                        this@CreatePostActivityTwo
                    ).isNullOrEmpty()
                ) {
                    hashTag(createPostEditText.tagText, this@CreatePostActivityTwo).joinToString {
                        it.toString().trim()
                    }
                } else {
                    ""
                })
            jsonObject.put("postCategory", "")
            when (textViewCreatePostPrivacy.text.toString().trim()) {
                "Public" -> {
                    jsonObject.put("postPrivacyType", "Public")
                }
                "Connections" -> {
                    jsonObject.put("postPrivacyType", "Connection")
                }
                "Groups" -> {
                    jsonObject.put("postPrivacyType", "Group")
                }
            }
            jsonObject.put("postUploadDate", formattedDate)
            jsonObject.put("connectiontypeIDs", connectionTypeIDs)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when (listFromPastActivity) {
            "images" -> {
                jsonObject.put(
                    "postDescription",
                    Constant.encode(createPostEditText.tagText.trim())
                )
                jsonObject.put("postLanguage", "en")

                if (!Location.isNullOrEmpty()) {
                    jsonObject.put("postLocation", Location)

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!latitude.isNullOrEmpty()) {
                    jsonObject.put("postLatitude", latitude)

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!longitude.isNullOrEmpty()) {
                    jsonObject.put("postLongitude", longitude)

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }


                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Photo")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!datumList.isNullOrEmpty()) {
                        for (i in 0 until datumList.size) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put("albummediaFile", datumList[i].imageName)
                                jsonObjectAlbummedia.put("albummediaThumbnail", "")
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    datumList[i]!!.fileSize
                                )
                                jsonObjectAlbummedia.put("albummediaFileType", "Photo")
                                jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                    jsonObject.put("postSerializedData", jsonArraypostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "Link" -> {
                jsonObject.put(
                    "postDescription",
                    Constant.encode(
                        createPostEditText.text.toString().trim()
                    ) + "||${(url + "||" + postDes)}"
                )
                jsonObject.put("postLanguage", "en")
                if (!Location.isNullOrEmpty()) {
                    jsonObject.put("postLocation", Location)

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!latitude.isNullOrEmpty()) {
                    jsonObject.put("postLatitude", latitude)

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!longitude.isNullOrEmpty()) {
                    jsonObject.put("postLongitude", longitude)

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }

                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Link")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!datumList.isNullOrEmpty()) {
                        for (i in 0 until datumList.size) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put("albummediaFile", datumList[i].imageName)
                                jsonObjectAlbummedia.put("albummediaThumbnail", "")
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    datumList[i]!!.fileSize
                                )
                                jsonObjectAlbummedia.put("albummediaFileType", "Link")
                                jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                    jsonObject.put("postSerializedData", jsonArraypostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "Document" -> {
                jsonObject.put(
                    "postDescription",
                    Constant.encode(createPostEditText.text.toString().trim())
                )
                jsonObject.put("postLanguage", "en")
                if (!Location.isNullOrEmpty()) {
                    jsonObject.put("postLocation", Location)

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!latitude.isNullOrEmpty()) {
                    jsonObject.put("postLatitude", latitude)

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!longitude.isNullOrEmpty()) {
                    jsonObject.put("postLongitude", longitude)

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }

                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Document")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!datumList.isNullOrEmpty()) {
                        for (i in 0 until datumList.size) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put("albummediaFile", datumList[i].imageName)
                                jsonObjectAlbummedia.put("albummediaThumbnail", "")
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    datumList[i]!!.fileSize
                                )

                                jsonObjectAlbummedia.put("albummediaFileType", "Document")
                                jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                    jsonObject.put("postSerializedData", jsonArraypostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "Video" -> {
                jsonObject.put(
                    "postDescription",
                    Constant.encode(createPostEditText.tagText.trim())
                )
                jsonObject.put("postLanguage", "en")
                if (!Location.isNullOrEmpty()) {
                    jsonObject.put("postLocation", Location)

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!latitude.isNullOrEmpty()) {
                    jsonObject.put("postLatitude", latitude)

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!longitude.isNullOrEmpty()) {
                    jsonObject.put("postLongitude", longitude)

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }

                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Video")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!datumList.isNullOrEmpty()) {
                        for (i in 0 until imageList.size) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put(
                                    "albummediaFile",
                                    datumList[i].videoThumnailName
                                )
                                jsonObjectAlbummedia.put(
                                    "albummediaThumbnail",
                                    datumList[i].imageName
                                )
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    datumList[i]!!.fileSize
                                )

                                jsonObjectAlbummedia.put("albummediaFileType", "Video")
                                jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                    jsonObject.put("postSerializedData", jsonArraypostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "Text" -> {
                jsonObject.put(
                    "postDescription",
                    Constant.encode(createPostEditText.tagText.trim())
                )
                jsonObject.put("postLanguage", "en")
                jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())
                jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())
                jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Text")
                jsonObject.put("postSerializedData", JSONArray())
            }
            "Place" -> {
                jsonObject.put(
                    "postDescription",
                    Constant.encode(createPostEditText.tagText.trim())
                )
                jsonObject.put("postLanguage", "en")
                jsonObject.put("postLatitude", latitude)
                jsonObject.put("postLongitude", longitude)
                jsonObject.put("postLocation", Location)
                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Place")
                jsonObject.put("postSerializedData", JSONArray())
            }
        }

        jsonArray.put(jsonObject)
        Log.e("jsonArray", "" + jsonArray.toString())
        createPostModel.apiFunction(this@CreatePostActivityTwo, jsonArray.toString(), "")
            .observe(
                this@CreatePostActivityTwo
            ) { response ->
                if (!response.isNullOrEmpty()) {
                    MyUtils.dismissProgressDialog()
                    if (response[0].status.equals("true", true)) {
                        MyUtils.hideKeyboard1(this@CreatePostActivityTwo)
                        MyUtils.showSnackbar(
                            this@CreatePostActivityTwo,
                            response[0].message,
                            mainLinearLayoutCreatePostTwo
                        )

                        val intent2 = Intent("CreatePost")
                        intent2.putExtra("from", listFromPastActivity)
                        intent2.putExtra("msg", response[0].message)
                        LocalBroadcastManager.getInstance(this@CreatePostActivityTwo)
                            .sendBroadcast(intent2)


                    } else {
                        //No data and no internet
                        if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo)) {
                            if (!response[0].message.isNullOrEmpty()) {
                                MyUtils.showSnackbar(
                                    this@CreatePostActivityTwo,
                                    response[0].message,
                                    mainLinearLayoutCreatePostTwo
                                )
                            }

                        } else {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_common_network),
                                mainLinearLayoutCreatePostTwo
                            )
                        }
                    }

                } else {
                    MyUtils.dismissProgressDialog()
                    if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo)) {
                        MyUtils.showSnackbar(
                            this@CreatePostActivityTwo,
                            resources.getString(R.string.error_crash_error_message),
                            mainLinearLayoutCreatePostTwo
                        )
                    } else {
                        MyUtils.showSnackbar(
                            this@CreatePostActivityTwo,
                            resources.getString(R.string.error_common_network),
                            mainLinearLayoutCreatePostTwo
                        )
                    }
                }
            }
    }


    fun editPost(fromType: String) {
        MyUtils.showProgressDialog(this@CreatePostActivityTwo, resources.getString(R.string.wait))

        var c = Calendar.getInstance()

        var df = SimpleDateFormat("yyyy-MM-dd")
        var formattedDate = df.format(c.time)

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        val jsonObjectpostSerializedData = JSONObject()
        val jsonArraypostSerializedData = JSONArray()


        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            when (fromType) {
                "postShare" -> {
                    jsonObject.put("originalPostID", postData?.postID)
                    jsonObject.put("originaluserID", postData?.userID)
                    jsonObject.put(
                        "postWriteSomething",
                        Constant.encode(createPostEditText.tagText.trim())
                    )
                    jsonObject.put("postDescription", postData?.postDescription)
                    jsonObject.put("postLanguage", "en")

                }
                "editPost" -> {
                    jsonObject.put(
                        "postDescription",
                        Constant.encode(createPostEditText.tagText.trim())
                    )
                    jsonObject.put("postLanguage", "en")
                    jsonObject.put("postID", postData?.postID)

                }
            }
            jsonObject.put("postCategory", "")
            when (textViewCreatePostPrivacy.text.toString().trim()) {
                "Public" -> {
                    jsonObject.put("postPrivacyType", "Public")
                }
                "Connections" -> {
                    jsonObject.put("postPrivacyType", "Connection")
                }
                "Groups" -> {
                    jsonObject.put("postPrivacyType", "Group")
                }
            }
            jsonObject.put("postUploadDate", formattedDate)
            jsonObject.put("connectiontypeIDs", connectionTypeIDs)

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when (postData?.postMediaType) {
            "Photo" -> {
                if (!Location.isNullOrEmpty()) {
                    jsonObject.put("postLocation", Location)

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!latitude.isNullOrEmpty()) {
                    jsonObject.put("postLatitude", latitude)

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!longitude.isNullOrEmpty()) {
                    jsonObject.put("postLongitude", longitude)

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }


                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Photo")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!postData?.postSerializedData!![0].albummedia.isNullOrEmpty()) {
                        for (i in 0 until postData?.postSerializedData!![0].albummedia.size) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put(
                                    "albummediaFile",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFile
                                )
                                jsonObjectAlbummedia.put("albummediaThumbnail", "")
                                jsonObjectAlbummedia.put("albummediaFileType", "Photo")
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFileSize
                                )

                                jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                    jsonObject.put("postSerializedData", jsonArraypostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "Link" -> {
                jsonObject.put(
                    "postDescription",
                    Constant.encode(
                        createPostEditText.text.toString().trim()
                    ) + "||${(url + "||" + postDes)}"
                )
                jsonObject.put("postLanguage", "en")
                if (!Location.isNullOrEmpty()) {
                    jsonObject.put("postLocation", Location)

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!latitude.isNullOrEmpty()) {
                    jsonObject.put("postLatitude", latitude)

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!longitude.isNullOrEmpty()) {
                    jsonObject.put("postLongitude", longitude)

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }

                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Link")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!postData?.postSerializedData!![0].albummedia.isNullOrEmpty()) {
                        for (i in postData?.postSerializedData!![0].albummedia.indices) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put(
                                    "albummediaFile",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFile
                                )
                                jsonObjectAlbummedia.put("albummediaThumbnail", "")
                                jsonObjectAlbummedia.put("albummediaFileType", "Link")
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFileSize
                                )

                                jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                    jsonObject.put("postSerializedData", jsonArraypostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "Video" -> {
                if (!Location.isNullOrEmpty()) {
                    jsonObject.put("postLocation", Location)

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!latitude.isNullOrEmpty()) {
                    jsonObject.put("postLatitude", latitude)

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!longitude.isNullOrEmpty()) {
                    jsonObject.put("postLongitude", longitude)

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }

                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Video")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!postData?.postSerializedData!![0].albummedia.isNullOrEmpty()) {
                        for (i in 0 until postData?.postSerializedData!![0].albummedia.size) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put(
                                    "albummediaFile",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFile
                                )
                                jsonObjectAlbummedia.put(
                                    "albummediaThumbnail",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaThumbnail
                                )
                                jsonObjectAlbummedia.put("albummediaFileType", "Video")
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFileSize
                                )

                                jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                    jsonObject.put("postSerializedData", jsonArraypostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "Text" -> {
                jsonObject.put(
                    "postDescription",
                    Constant.encode(createPostEditText.tagText.trim())
                )
                jsonObject.put("postLanguage", "en")
                jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())
                jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())
                jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Text")
                jsonObject.put("postSerializedData", JSONArray())
            }
            "Place" -> {
                jsonObject.put(
                    "postDescription",
                    Constant.encode(createPostEditText.tagText.trim())
                )
                jsonObject.put("postLanguage", "en")
                jsonObject.put("postLatitude", latitude)
                jsonObject.put("postLongitude", longitude)
                jsonObject.put("postLocation", Location)
                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Place")
                jsonObject.put("postSerializedData", JSONArray())
            }
            "Document" -> {
                if (!Location.isNullOrEmpty()) {
                    jsonObject.put("postLocation", Location)

                } else {
                    // jsonObject.put("postLocation", MyUtils.currentLocation.toString())
                    jsonObject.put("postLocation", "")

                }
                if (!latitude.isNullOrEmpty()) {
                    jsonObject.put("postLatitude", latitude)

                } else {
                    jsonObject.put("postLatitude", MyUtils.currentLattitude.toString())

                }
                if (!longitude.isNullOrEmpty()) {
                    jsonObject.put("postLongitude", longitude)

                } else {
                    jsonObject.put("postLongitude", MyUtils.currentLongtiude.toString())

                }

                jsonObject.put("postType", "Social")
                jsonObject.put("postMediaType", "Document")
                try {
                    jsonObjectpostSerializedData.put("albumName", "")
                    jsonObjectpostSerializedData.put("albumType", "Media")
                    val jsonArrayAlbummedia = JSONArray()
                    if (!postData?.postSerializedData!![0].albummedia.isNullOrEmpty()) {
                        for (i in 0 until imageList.size) {
                            val jsonObjectAlbummedia = JSONObject()
                            try {
                                jsonObjectAlbummedia.put(
                                    "albummediaFile",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFile
                                )
                                jsonObjectAlbummedia.put("albummediaThumbnail", "")
                                jsonObjectAlbummedia.put("albummediaFileType", "Document")
                                jsonObjectAlbummedia.put(
                                    "albummediaFileSize",
                                    postData?.postSerializedData!![0].albummedia[i].albummediaFileSize
                                )

                                jsonArrayAlbummedia.put(i, jsonObjectAlbummedia)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                    jsonObjectpostSerializedData.put("albummedia", jsonArrayAlbummedia)
                    jsonArraypostSerializedData.put(jsonObjectpostSerializedData)
                    jsonObject.put("postSerializedData", jsonArraypostSerializedData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        jsonObject.put("postLanguage", "en")
        jsonArray.put(jsonObject)
        createPostModel.apiFunction(this@CreatePostActivityTwo, jsonArray.toString(), fromType)
            .observe(this@CreatePostActivityTwo,
                { response ->
                    if (!response.isNullOrEmpty()) {
                        MyUtils.dismissProgressDialog()
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(this@CreatePostActivityTwo)
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                response[0].message,
                                mainLinearLayoutCreatePostTwo
                            )

                            val intent2 = Intent("CreatePost")
                            intent2.putExtra("from", listFromPastActivity)
                            intent2.putExtra("msg", response[0].message)
                            LocalBroadcastManager.getInstance(this@CreatePostActivityTwo)
                                .sendBroadcast(intent2)


                        } else {
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo)) {
                                if (!response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbar(
                                        this@CreatePostActivityTwo,
                                        response[0].message,
                                        mainLinearLayoutCreatePostTwo
                                    )
                                }

                            } else {
                                MyUtils.showSnackbar(
                                    this@CreatePostActivityTwo,
                                    resources.getString(R.string.error_common_network),
                                    mainLinearLayoutCreatePostTwo
                                )
                            }
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo)) {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_crash_error_message),
                                mainLinearLayoutCreatePostTwo
                            )
                        } else {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_common_network),
                                mainLinearLayoutCreatePostTwo
                            )
                        }
                    }
                })
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        MyUtils.showMessageOKCancel(this,
            resources.getString(R.string.are_you_sure_you_want_to_discard_post),
            resources.getString(R.string.discard_title),
            DialogInterface.OnClickListener { _, _ ->

                MyUtils.finishActivity(this@CreatePostActivityTwo, true)


            })
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@CreatePostActivityTwo)
            .unregisterReceiver(mYourBroadcastReceiver)

    }

    fun getPathVideo(pictureUri: Uri): String? {
        var cursor: Cursor? = null
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = this@CreatePostActivityTwo.contentResolver
                .query(pictureUri, proj, null, null, null)

            val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()

            if (File(cursor?.getString(column_index!!)) != null) {
                return cursor?.getString(column_index!!)!!
            }
        } finally {
            if (cursor != null) {
                cursor.close()
            }
            return null
        }
    }

    private fun getPathFromInputStreamUriVideo(uri: Uri): String {

        var inputStream: InputStream? = null
        var filePath = ""

        if (uri.authority != null) {

            try {
                inputStream = this.contentResolver.openInputStream(uri)
                val photoFile = createTemporalFileFromVideo(inputStream)
                filePath = photoFile!!.path

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return filePath
    }

    private fun getPathFromInputStreamUriDocument(
        uri: Uri,
        extension: String?,
        displayName: String
    ): String {

        var inputStream: InputStream? = null
        var filePath = ""

        if (uri.authority != null) {

            try {
                inputStream = this.contentResolver.openInputStream(uri)
                val photoFile = createTemporalFileFromDocument(inputStream, extension, displayName)
                filePath = photoFile!!.path

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
        return filePath
    }

    //    @Throws(IOException.class)
    @Throws(IOException::class)
    private fun createTemporalFileFromVideo(inputStream: InputStream?): File? {
        //    throw new IOException();

        if (inputStream != null) {
            var read1 = 0
            //            byte[] buffer = new ByteArray(8 * 1024);
            val buffer = ByteArray(1024)

            val targetFile = createTemporalFileVideo()

            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(targetFile)
            } catch (e: FileNotFoundException) {

            }

            while (true) {
                try {
                    read1 = inputStream.read(buffer)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (read1 == -1) break
                try {
                    outputStream!!.write(buffer, 0, read1)
                } catch (e: FileNotFoundException) {

                }

            }

            outputStream!!.flush()

            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return targetFile
        } else
            return null
    }

    @Throws(IOException::class)
    private fun createTemporalFileFromDocument(
        inputStream: InputStream?,
        extension: String?,
        displayName: String
    ): File? {
        //    throw new IOException();

        if (inputStream != null) {
            var read1 = 0
            //            byte[] buffer = new ByteArray(8 * 1024);
            val buffer = ByteArray(1024)

            val targetFile = createTemporalFileDocument(extension, displayName)

            var outputStream: FileOutputStream? = null
            try {
                outputStream = FileOutputStream(targetFile)
            } catch (e: FileNotFoundException) {

            }

            while (true) {
                try {
                    read1 = inputStream.read(buffer)
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (read1 == -1) break
                try {
                    outputStream!!.write(buffer, 0, read1)
                } catch (e: FileNotFoundException) {

                }

            }

            outputStream!!.flush()

            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return targetFile
        } else
            return null
    }

    @Throws(IOException::class)
    fun savebitmap(bmp: Bitmap): File {
        var f: File? = null
        val fo: FileOutputStream
        try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val bytes = ByteArrayOutputStream()

            bmp.compress(Bitmap.CompressFormat.JPEG, 60, bytes)
            val cw = ContextWrapper(applicationContext)
            f = File(
                cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + "$timeStamp.jpg"
            )
            f?.createNewFile()
            fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return f!!
    }

    private fun createTemporalFileVideo(): File {

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val cw = ContextWrapper(applicationContext)
        val path = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()

        return File(path, "vid_$timeStamp.mp4")
    }

    private fun createTemporalFileDocument(extension: String?, displayName: String): File {

        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val cw = ContextWrapper(applicationContext)
        val path = cw.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()

        return File(path, "${timeStamp}post.${extension}")
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionLocation(): Boolean {

        if (!addPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val message = getString(R.string.grant_access_location)

            MyUtils.showMessageOKCancel(this@CreatePostActivityTwo,
                message,
                "Use Location Service?",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_LOCATION_PERMISSIONS
                    )
                })

        } else {
            connectLocation()
        }
        return true
    }

    private fun addPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) !== PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun connectLocation() {
        MyUtils.showProgressDialog(this@CreatePostActivityTwo, "Please wait..")
        locationProvider =
            LocationProvider(this@CreatePostActivityTwo, LocationProvider.HIGH_ACCURACY,
                object : LocationProvider.CurrentLocationCallback {
                    override fun handleNewLocation(location: Location) {
                        locationProvider?.disconnect()
                        MyUtils.dismissProgressDialog()
                        if (location != null) {
                            Log.d("currentLocation", location.toString())
                            LATITUDE = location.latitude
                            LONGITUDE = location.longitude
                            isLocationGot = true
                            selectPlace()
                        }
                    }
                })

        locationProvider!!.connect()
    }

    private fun selectPlace() {
        val intent = Intent(this@CreatePostActivityTwo, CreatePostSelectPlaceActivity::class.java)
        intent.putExtra("From", listFromPastActivity)
        intent.putExtra("latitude", LATITUDE)
        intent.putExtra("longitude", LONGITUDE)
        startActivityForResult(intent, 1002)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }


    fun hashTag(
        text: String,
        mContext: Context?
    ): ArrayList<String> {
        var string = ArrayList<String>()

        var text = text
        if (text.contains("<Shase>")) {
            text = text.replace("<Shase>".toRegex(), "#")
            text = text.replace("<Chase>".toRegex(), ",")
        }
        val ssb = SpannableStringBuilder(text)
        val MY_PATTERN = Pattern.compile("#(\\w+)")
        val mat = MY_PATTERN.matcher(text)
        while (mat.find()) {

            Log.e("match1", mat.group())
            string.add(mat.group().trim())
        }

        return string
    }

    fun getVidioThumbnail(path: String?): Bitmap? {
        var bitmap: Bitmap? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            bitmap = ThumbnailUtils.createVideoThumbnail(
                path!!,
                MediaStore.Images.Thumbnails.MICRO_KIND
            )!!
            if (bitmap != null) {
                return bitmap
            }
        }
        // MediaMetadataRetriever is available on API Level 8 but is hidden until API Level 10
        var clazz: Class<*>? = null
        var instance: Any? = null
        try {
            clazz = Class.forName("android.media.MediaMetadataRetriever")
            instance = clazz.newInstance()
            val method: Method = clazz.getMethod("setDataSource", String::class.java)
            method.invoke(instance, path)
            // The method name changes between API Level 9 and 10.
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD) {
                bitmap = clazz.getMethod("captureFrame").invoke(instance) as Bitmap
            } else {
                val data = clazz.getMethod("getEmbeddedPicture").invoke(instance) as ByteArray
                if (data != null) {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                }
                if (bitmap == null) {
                    bitmap = clazz.getMethod("getFrameAtTime").invoke(instance) as Bitmap
                }
            }
        } catch (e: java.lang.Exception) {
            bitmap = null
        } finally {
            try {
                if (instance != null) {
                    clazz!!.getMethod("release").invoke(instance)
                }
            } catch (ignored: java.lang.Exception) {
            }
        }
        return bitmap
    }


    fun getCheckStorage() {
        MyUtils.showProgressDialog(this@CreatePostActivityTwo, resources.getString(R.string.wait))
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        checkStoragetModel.getCheckStorage(this@CreatePostActivityTwo, jsonArray.toString())
            .observe(this@CreatePostActivityTwo,
                { response ->
                    if (!response.isNullOrEmpty()) {
                        MyUtils.dismissProgressDialog()
                        if (response[0].status.equals("true", true)) {
                            MyUtils.hideKeyboard1(this@CreatePostActivityTwo)
                            showDialog()
                        } else {
                            //No data and no internet
                            if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo)) {
                                if (!response[0].message.isNullOrEmpty()) {
                                    MyUtils.showSnackbar(
                                        this@CreatePostActivityTwo,
                                        response[0].message,
                                        mainLinearLayoutCreatePostTwo
                                    )
                                }

                            } else {
                                MyUtils.showSnackbar(
                                    this@CreatePostActivityTwo,
                                    resources.getString(R.string.error_common_network),
                                    mainLinearLayoutCreatePostTwo
                                )
                            }
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        if (MyUtils.isInternetAvailable(this@CreatePostActivityTwo)) {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_crash_error_message),
                                mainLinearLayoutCreatePostTwo
                            )
                        } else {
                            MyUtils.showSnackbar(
                                this@CreatePostActivityTwo,
                                resources.getString(R.string.error_common_network),
                                mainLinearLayoutCreatePostTwo
                            )
                        }
                    }
                })
    }

    private fun showDialog() {

        var dialogs = Dialog(this@CreatePostActivityTwo)

        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogs.setCancelable(true)
        dialogs.setContentView(R.layout.storage_purchase_plan)

        var lp = WindowManager.LayoutParams()

        lp.copyFrom(dialogs.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.y = 250
        lp.gravity = Gravity.CENTER;

        dialogs.window?.attributes = lp


        val btn_current_gb = dialogs.findViewById<ProgressButton>(R.id.btn_current_gb)
        val txt_current_gb = dialogs.findViewById<TextView>(R.id.txt_current_gb)
        val recyclerview = dialogs.findViewById<RecyclerView>(R.id.recyclerview)
        val btn_confirm = dialogs.findViewById<ProgressButton>(R.id.btn_confirm)
        val btn_cancel = dialogs.findViewById<ProgressButton>(R.id.btn_cancel)
        var storagePurchaseAdapter: StoragePurchaseAdapter? = null
        var storagePurchaseItem: ArrayList<String>? = ArrayList()
        btn_cancel.setOnClickListener {
            dialogs?.dismiss()
        }
        storagePurchaseAdapter = StoragePurchaseAdapter(
            this@CreatePostActivityTwo,
            storagePurchaseItem!!,
            object : StoragePurchaseAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int) {

                }

            })

        storagePurchaseItem.add("abc")
        storagePurchaseItem.add("abc1")
        storagePurchaseItem.add("abc2")

        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(this@CreatePostActivityTwo)
        recyclerview.adapter = storagePurchaseAdapter
        storagePurchaseAdapter?.notifyDataSetChanged()

        dialogs.show()

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.textViewCreatePostPrivacy -> {
                Intent(this@CreatePostActivityTwo!!, PrivacyActivity::class.java).apply {
                    if (postData != null) {
                        putExtra("postPrivacyType", postData?.postPrivacyType)
                    }
                    startActivityForResult(this, 800)
                }
            }
            R.id.ll_add_location_post -> {
                ll_add_location_post.isEnabled = false
                if (currentApiVersion >= Build.VERSION_CODES.M) {
                    permissionLocation()
                } else {
                    connectLocation()
                }
                ll_add_location_post.isEnabled = true
            }
            R.id.tvAddHashTag -> {
                Intent(this, AddHashtagsActivity()::class.java).apply {
                    putExtra("from", "Add")
                    startActivityForResult(this, 115)
                }
            }
            R.id.menuToolbarItem -> {
                // getCheckStorage()
                if (postData != null) {

                    when (postData?.postMediaType) {

                        "Video" -> {
                            if (postData != null) {
                                if (postData?.postType.equals("ExploreVideos")) {
                                    editExplorePost()
                                } else if (postData?.postType.equals("ExploreTricks")) {
                                    editExplorePost()
                                } else {
                                    if (postData != null) {
                                        editPost(fromType)
                                    }
                                }
                            }

                        }
                        "Photo" -> {
                            if (postData != null) {
                                editPost(fromType)
                            }
                        }
                        "Link" -> {
                            if (postData != null) {
                                editPost(fromType)
                            }
                        }
                        "Document" -> {
                            if (postData != null) {
                                editPost(fromType)
                            }
                        }
                    }
                } else {
                    when (listFromPastActivity) {
                        "Text" -> {
                            createPost(listFromPastActivity, imageList)

                        }
                        "Video" -> {
                            if (postData != null) {
                                editPost(fromType)
                            } else {
                                if (!imageList.isNullOrEmpty()) {
                                    if (!type1.isNullOrEmpty() && type1.equals(
                                            "MyExploreVideo",
                                            false
                                        )
                                    ) {
                                        uploadExploreVideoThumb("Uploaded", imageList)
                                    } else {
                                        uploadPostVideo("Uploaded", listFromPastActivity)

                                    }
                                }
                            }

                        }
                        "Photo" -> {
                            if (postData != null) {
                                editPost(fromType)
                            } else {
                                if (!imageList.isNullOrEmpty()) {
                                    uploadPostPhoto("Uploaded", listFromPastActivity)
                                }
                            }
                        }
                        "Link" -> {
                            if (postData != null) {
                                editPost(fromType)
                            } else {
                                if (!imageList.isNullOrEmpty()) {
                                    uploadPostLink("Uploaded", listFromPastActivity)
                                }

                            }

                        }
                        "Document" -> {
                            if (postData != null) {
                                editPost(fromType)
                            } else {
                                if (!imageList.isNullOrEmpty()) {
                                    uploadPostDocument("Uploaded", listFromPastActivity)
                                }

                            }

                        }
                    }
                }

            }

        }
    }

}
