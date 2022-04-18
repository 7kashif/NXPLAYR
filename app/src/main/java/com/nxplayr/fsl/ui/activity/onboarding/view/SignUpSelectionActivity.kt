package com.nxplayr.fsl.ui.activity.onboarding.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.facebook.drawee.generic.RoundingParams
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.tabs.TabLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.google.gson.Gson
import com.nxplayr.fsl.BuildConfig
import com.nxplayr.fsl.R
import com.nxplayr.fsl.R.color.colorPrimary
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.activity.onboarding.adapter.AgeGroupAdapter
import com.nxplayr.fsl.ui.activity.onboarding.adapter.CommonPagerAdapter
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.CountryListModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.FootballAgeGroupModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.ui.fragments.dialogs.ParentGuardianDialog
import com.nxplayr.fsl.ui.fragments.dialogs.PasswordInfoDialog
import com.nxplayr.fsl.util.*
import com.nxplayr.fsl.util.interfaces.DialogListener
import kotlinx.android.synthetic.main.activity_signup_selection.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_select_age.*
import kotlinx.android.synthetic.main.fragment_select_gender.*
import kotlinx.android.synthetic.main.layout_introduce_yourself.*
import kotlinx.android.synthetic.main.layout_select_dialog_camera.*
import kotlinx.android.synthetic.main.layout_upload_picture.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*


@Suppress("DEPRECATION")
class SignUpSelectionActivity : AppCompatActivity(), View.OnClickListener {
    var adapter: CommonPagerAdapter? = null
    var ageGroupAdapter: AgeGroupAdapter? = null
    var list: ArrayList<LanguageListPojo>? = ArrayList()
    var ageGroupListData: ArrayList<FootballAgeGroupListData>? = ArrayList()
    var countryListData: ArrayList<CountryListData>? = ArrayList()
    var countrylist: ArrayList<String>? = ArrayList()
    var mfUser_Profile_Image: File? = null
    var view: View? = null
    var mfUser: File? = null
    var bitmap: Bitmap? = null
    var isSelect: Boolean = false
    var selectModeType: Int = 0
    var colorId: Int? = null
    var sessionManager: SessionManager? = null
    var apputypeID = 0
    var appuroleID = 0
    var footbltypeID = 0
    var userGender = ""
    var agegroupID = 0
    var agegroupFrom = 0
    var profileImageName = ""
    var userName = ""
    var userEmail = ""
    var socialID = ""
    var image: FirebaseVisionImage? = null
    var detector: FirebaseVisionFaceDetector? = null
    private lateinit var signup: SignupModel
    private lateinit var footballTypeListModel: FootballAgeGroupModel
    private lateinit var countryListModel: CountryListModel
    var mDrawableEye: Drawable? = null
    var mDrawableEyeOff: Drawable? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup_selection)
        sessionManager = SessionManager(this@SignUpSelectionActivity)
        if (intent != null) {
            if (intent.hasExtra("userName")) {
                userName = intent?.getStringExtra("userName")!!
            }
            if (intent.hasExtra("userEmail")) {
                userName = intent?.getStringExtra("userEmail")!!
            }
            if (intent.hasExtra("socialID")) {
                socialID = intent?.getStringExtra("socialID")!!
            }
            if (intent != null && intent.hasExtra("selectModeType"))
                selectModeType = intent.getIntExtra("selectModeType", 0)
            if (intent != null && intent.hasExtra("apputypeID"))
                apputypeID = intent.getIntExtra("apputypeID", 0)
            if (intent != null && intent.hasExtra("appuroleID"))
                appuroleID = intent.getIntExtra("appuroleID", 0)
            if (intent != null && intent.hasExtra("footbltypeID"))
                footbltypeID = intent.getIntExtra("footbltypeID", 0)


        }
        setupViewModel()
        setupUI()


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupUI() {
        tvToolbarTitle.visibility = View.GONE
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        if (!MyUtils.fbID.isNullOrEmpty()) {
            socialID = MyUtils.fbID
        }
        if (!MyUtils.user_Email.isNullOrEmpty()) {
            userEmail = MyUtils.user_Email
            emailAddress_edit_text.isEnabled = false
            emailAddress_edit_text.setText(MyUtils.user_Email.toString())
        }
        if (!MyUtils.fbID.isNullOrEmpty()) {
            userName = MyUtils.user_Name
            firstName_edit_text.setText(MyUtils.user_Name.toString())
        }
        setupViewPager(typeSelectionViewpager!!)
        tab_Layout.setupWithViewPager(typeSelectionViewpager)
        typeSelectionViewpager.setOnTouchListener(View.OnTouchListener { v, event -> true })

        setupAgeGroupData()
        setUI()
        setonClick()
        setPasswordIcon()
        getCounrtyList()

        if (typeSelectionViewpager.currentItem == 2) {
            camera_layout.visibility = View.VISIBLE

        } else {
            camera_layout.visibility = View.GONE
        }
        countrylist_edit_text.setOnClickListener(this)

        mobileNumber_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ll_dropdown.visibility = VISIBLE
                if (p0!!.isEmpty()) {
                    ll_dropdown.visibility = View.GONE
                }
            }
        })
        btnRetry.setOnClickListener(this)
        textWatcher()

        tab_Layout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {

                    setTabLayoutBackground()
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {

                }

            })

    }

    private fun setupViewModel() {
        signup = ViewModelProvider(this@SignUpSelectionActivity).get(SignupModel::class.java)
        footballTypeListModel = ViewModelProvider(this@SignUpSelectionActivity).get(
            FootballAgeGroupModel::class.java
        )
        countryListModel =
            ViewModelProvider(this@SignUpSelectionActivity).get(CountryListModel::class.java)

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setUI() {


        when (selectModeType) {
            0 -> {
                colorId = R.color.colorPrimary
                img_user_profile.setActualImageResource(R.drawable.placeholder_profile_blue)
                image_camera_icon.setImageResource(R.drawable.camera_icon_selected)
            }
            1 -> {
                colorId = R.color.yellow
                image_camera_icon.setImageResource(R.drawable.camera_profile_pink)
                img_user_profile.setActualImageResource(R.drawable.placeholder_profile_blue)
            }
            2 -> {
                colorId = R.color.colorAccent
                image_camera_icon.setImageResource(R.drawable.camera_profile_pink)
                img_user_profile.setActualImageResource(R.drawable.placeholder_profile_pink)
            }
        }
        MyUtils.setSelectedModeTypeViewColor(
            this,
            arrayListOf(
                tv_your_age_group,
                tv_profile,
                tv_introduce_yourSelf,
                tv_you_are,
                firstName_textInputLayout,
                firstName_edit_text,
                lastName_textInputLayout,
                lastName_edit_text,
                mobileNumber_textInput,
                mobileNumber_edit_text,
                emailAddress_textInput,
                emailAddress_edit_text,
                password_textInput,
                password_edit_text,
                confirmPassword_textInput,
                confirmPassword_edit_text,
                country_textInput,
                countrylist_edit_text,
                userSignedRefKey_textInput, userSignedRefKey_edit_text,

                ) as ArrayList<View>,
            colorId!!
        )

        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngGenderQue.isNullOrEmpty())
                tv_you_are.text = sessionManager?.LanguageLabel?.lngGenderQue
            if (!sessionManager?.LanguageLabel?.lngGenderQueDetail.isNullOrEmpty())
                tv_set_gender.text = sessionManager?.LanguageLabel?.lngGenderQueDetail
            if (!sessionManager?.LanguageLabel?.lngMale.isNullOrEmpty())
                tv_male.text = sessionManager?.LanguageLabel?.lngMale
            if (!sessionManager?.LanguageLabel?.lngFemale.isNullOrEmpty())
                tv_female.text = sessionManager?.LanguageLabel?.lngFemale
            if (!sessionManager?.LanguageLabel?.lngNext.isNullOrEmpty()) {
                btnSetGender.progressText = sessionManager?.LanguageLabel?.lngNext
                btnSelectAgeAgroup.progressText = sessionManager?.LanguageLabel?.lngNext
                btnNextUserProfile.progressText = sessionManager?.LanguageLabel?.lngNext
            }

            if (!sessionManager?.LanguageLabel?.lngAgeGroupeQue.isNullOrEmpty())
                tv_your_age_group.text = sessionManager?.LanguageLabel?.lngAgeGroupeQue
            if (!sessionManager?.LanguageLabel?.lngAgeGroupeDetail.isNullOrEmpty())
                select_your_age_group.text = sessionManager?.LanguageLabel?.lngAgeGroupeDetail

            if (!sessionManager?.LanguageLabel?.lngProfileQue.isNullOrEmpty())
                tv_profile.text = sessionManager?.LanguageLabel?.lngProfileQue
            if (!sessionManager?.LanguageLabel?.lngProfileDetail.isNullOrEmpty())
                tv_profile_details.text = sessionManager?.LanguageLabel?.lngProfileDetail

            if (!sessionManager?.LanguageLabel?.lngCamera.isNullOrEmpty())
                tv_camera.text = sessionManager?.LanguageLabel?.lngCamera
            if (!sessionManager?.LanguageLabel?.lngGallery.isNullOrEmpty())
                tv_gallery.text = sessionManager?.LanguageLabel?.lngGallery
            if (!sessionManager?.LanguageLabel?.lngClose.isNullOrEmpty())
                btnCloseProfileSelection.progressText = sessionManager?.LanguageLabel?.lngClose

            if (!sessionManager?.LanguageLabel?.lngCreateProfileQue.isNullOrEmpty())
                tv_introduce_yourSelf.text = sessionManager?.LanguageLabel?.lngCreateProfileQue
            if (!sessionManager?.LanguageLabel?.lngCreateProfileDetail.isNullOrEmpty())
                tv_introduce_yourSelf_details.text =
                    sessionManager?.LanguageLabel?.lngCreateProfileDetail
            if (!sessionManager?.LanguageLabel?.lngFirstName.isNullOrEmpty())
                firstName_textInputLayout.hint = sessionManager?.LanguageLabel?.lngFirstName
            if (!sessionManager?.LanguageLabel?.lngFamilyName.isNullOrEmpty())
                lastName_textInputLayout.hint = sessionManager?.LanguageLabel?.lngFamilyName
            if (!sessionManager?.LanguageLabel?.lngMobileNo.isNullOrEmpty())
                mobileNumber_textInput.hint = sessionManager?.LanguageLabel?.lngMobileNo
            if (!sessionManager?.LanguageLabel?.lngEmailAddress.isNullOrEmpty())
                emailAddress_textInput.hint = sessionManager?.LanguageLabel?.lngEmailAddress
            if (!sessionManager?.LanguageLabel?.lngPassword.isNullOrEmpty())
                password_textInput.hint = sessionManager?.LanguageLabel?.lngPassword
            if (!sessionManager?.LanguageLabel?.lngRetypePassword.isNullOrEmpty())
                confirmPassword_textInput.hint = sessionManager?.LanguageLabel?.lngRetypePassword
            if (!sessionManager?.LanguageLabel?.lngReferralCode.isNullOrEmpty())
                userSignedRefKey_textInput.hint = sessionManager?.LanguageLabel?.lngReferralCode
            if (!sessionManager?.LanguageLabel?.lngSubmit.isNullOrEmpty())
                btnSubmit.progressText = sessionManager?.LanguageLabel?.lngSubmit
        }

        info.setColorFilter(
            ContextCompat.getColor(this, colorId!!),
            PorterDuff.Mode.SRC_IN
        )

        btnNextUserProfile.textColor = (resources.getColor(colorId!!))
        btnNextUserProfile.strokeColor = (resources.getColor(colorId!!))
        img_select_male.setColorFilter(
            ContextCompat.getColor(this, colorId!!),
            PorterDuff.Mode.SRC_IN
        )
        img_select_female.setColorFilter(
            ContextCompat.getColor(this, colorId!!),
            PorterDuff.Mode.SRC_IN
        )
        btnSelectAgeAgroup.textColor = (resources.getColor(colorId!!))
        btnSelectAgeAgroup.strokeColor = (resources.getColor(colorId!!))
        btnSetGender.textColor = (resources.getColor(colorId!!))
        btnSetGender.strokeColor = (resources.getColor(colorId!!))
        btnCloseProfileSelection.textColor = (resources.getColor(colorId!!))
        btnCloseProfileSelection.strokeColor = (resources.getColor(colorId!!))

        img_select_gallery.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, colorId!!))
        img_select_camera.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, colorId!!))

        btnSubmit.textColor = resources.getColor(colorId!!)
        btnSubmit.strokeColor = resources.getColor(colorId!!)

        btnSubmit.progressColor = (resources.getColor(colorId!!))

        var mDrawable = ContextCompat.getDrawable(this, R.drawable.dropdown_icon)
        mDrawable?.setColorFilter(ContextCompat.getColor(this, colorId!!), PorterDuff.Mode.SRC_IN)
        countrylist_edit_text.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            mDrawable,
            null
        )

        mDrawableEye =
            ContextCompat.getDrawable(this@SignUpSelectionActivity, R.drawable.show_icon_login)
        mDrawableEyeOff =
            ContextCompat.getDrawable(this@SignUpSelectionActivity, R.drawable.hide_icon_login)


        mDrawableEye?.setColorFilter(
            ContextCompat.getColor(this, colorId!!),
            PorterDuff.Mode.SRC_IN
        )
        mDrawableEyeOff?.setColorFilter(
            ContextCompat.getColor(this, colorId!!),
            PorterDuff.Mode.SRC_IN
        )

        password_edit_text.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            mDrawableEye,
            null
        )
        confirmPassword_edit_text.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            mDrawableEye,
            null
        )
        setTabLayoutBackground()
        imageBorder()
    }

    private fun setonClick() {
        btnSetGender.setOnClickListener(this)
        btnSelectAgeAgroup.setOnClickListener(this)
        img_user_profile.setOnClickListener(this)
        img_select_camera.setOnClickListener(this)
        img_select_male.setOnClickListener(this)
        img_select_female.setOnClickListener(this)
        btnNextUserProfile.setOnClickListener(this)
        img_select_gallery.setOnClickListener(this)
        btnCloseProfileSelection.setOnClickListener(this)
        info.setOnClickListener(this)
        btnSubmit.setOnClickListener(this)
    }

    private fun setupAgeGroupData() {
        val ageGroupLayoutManager = GridLayoutManager(this, 2)
        ageGroupAdapter = AgeGroupAdapter(this, object : AgeGroupAdapter.OnItemClick {
            override fun onClicklisneter(position: Int, name: String) {

                agegroupID = ageGroupListData!![position].agegroupID.toInt()
                agegroupFrom = ageGroupListData!![position].agegroupFrom.toInt()
                for (i in 0 until ageGroupListData!!.size) {
                    ageGroupListData!![i].status1 = i == position

                }
                if (selectModeType == 0) {
                    btnSelectAgeAgroup.backgroundTint = (resources.getColor(colorPrimary))
                    btnSelectAgeAgroup.strokeColor = (resources.getColor(colorPrimary))
                } else if (selectModeType == 1) {
                    btnSelectAgeAgroup.backgroundTint = (resources.getColor(R.color.yellow))
                    btnSelectAgeAgroup.strokeColor = (resources.getColor(R.color.yellow))
                } else if (selectModeType == 2) {
                    btnSelectAgeAgroup.backgroundTint = (resources.getColor(R.color.colorAccent))
                    btnSelectAgeAgroup.strokeColor = (resources.getColor(R.color.colorAccent))
                }
                btnSelectAgeAgroup.textColor = (resources.getColor(R.color.black))
                ageGroupAdapter!!.notifyDataSetChanged()
            }

        }, ageGroupListData!!, selectModeType)
        recyclerview.layoutManager = ageGroupLayoutManager
        recyclerview.adapter = ageGroupAdapter
        ageGroupAdapter?.notifyDataSetChanged()
        getFootballAgeGroupList()

    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = CommonPagerAdapter()
        adapter?.insertViewId(R.id.page_select_gender)
        adapter?.insertViewId(R.id.page_select_age)
        adapter?.insertViewId(R.id.page_upload_picture)
        adapter?.insertViewId(R.id.page_introduce_yourself)
        viewPager.currentItem = 0
        viewPager.offscreenPageLimit = 4
        viewPager.adapter = adapter

    }

    fun setTabLayoutBackground() {
        for (i in 0 until tab_Layout.tabCount) {
            if (tab_Layout.selectedTabPosition == i)
                tab_Layout.getTabAt(i)!!.view.background =
                    (ContextCompat.getDrawable(this, R.drawable.intro_slider_round_selected))
            else
                tab_Layout.getTabAt(i)!!.view.background =
                    (ContextCompat.getDrawable(this, R.drawable.intro_slider_round_unselected))

            tab_Layout.getTabAt(i)!!.view.backgroundTintList =
                ContextCompat.getColorStateList(this, colorId!!)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSetGender -> {
                btnSetGender.strokeColor = resources.getColor(colorId!!)
                if (isSelect)
                    typeSelectionViewpager.currentItem = 1
                else
                    MyUtils.showSnackbar(
                        applicationContext,
                        "Please select Gender",
                        ll_signup_selection_data
                    )
            }
            R.id.btnSelectAgeAgroup -> {
                btnSelectAgeAgroup.strokeColor = resources.getColor(colorId!!)

                if (agegroupID != 0) {
                    if (agegroupFrom <= 15) {
                        val dialog = ParentGuardianDialog(selectModeType,
                            this@SignUpSelectionActivity,
                            object : DialogListener {
                                override fun onOK() {
                                    typeSelectionViewpager.currentItem = 2
                                }
                            })
                        dialog.show()
                    } else {
                        typeSelectionViewpager.currentItem = 2
                    }
                } else
                    MyUtils.showSnackbar(
                        applicationContext,
                        "Please select AgeGroup",
                        ll_signup_selection_data
                    )
            }
            R.id.img_user_profile -> {
                frame_viewpager.visibility = View.VISIBLE
                camera_layout.visibility = VISIBLE
                if (selectModeType == 0) {
                    img_select_camera.setColorFilter(resources.getColor(R.color.transperent))
                }

            }
            R.id.img_select_camera -> {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getCameraPermissionOther()
                    when (selectModeType) {
                        0 -> {
                            img_select_camera.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.camera_icon_selected_popup
                                )
                            )
                            img_select_gallery.setColorFilter(
                                ContextCompat.getColor(
                                    this,
                                    R.color.transperent
                                )
                            )
                            img_select_gallery.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.gallery_icon
                                )
                            )
                        }
                        1 -> {
                            img_select_gallery.background =
                                ContextCompat.getDrawable(this, R.drawable.transparent_circle)
                            img_select_camera.imageTintList =
                                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
                            img_select_camera.background =
                                ContextCompat.getDrawable(this, R.drawable.black_circle)
                            tv_camera.setTextColor(resources.getColor(colorId!!))
                            tv_gallery.setTextColor(resources.getColor(R.color.white))
                        }
                        2 -> {
                            img_select_gallery.background =
                                ContextCompat.getDrawable(this, R.drawable.transparent_circle)
                            img_select_camera.imageTintList =
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.colorAccent
                                    )
                                )
                            img_select_camera.background =
                                ContextCompat.getDrawable(this, R.drawable.black_circle)
                            tv_camera.setTextColor(resources.getColor(colorId!!))
                            tv_gallery.setTextColor(resources.getColor(R.color.white))
                        }

                    }


                } else {
                    takePhotoFromCamera()
                }
            }
            R.id.btnCloseProfileSelection -> {
                btnCloseProfileSelection.strokeColor = resources.getColor(colorId!!)

                frame_viewpager.visibility = VISIBLE
                camera_layout.visibility = View.GONE
                typeSelectionViewpager.currentItem = 2
            }
            R.id.btnNextUserProfile -> {
                btnNextUserProfile.strokeColor = (resources.getColor(colorId!!))

                if (img_user_profile.isSelected) {
                    typeSelectionViewpager.currentItem = 3
                } else {
                    MyUtils.showSnackbar(
                        this@SignUpSelectionActivity,
                        "Please add Profile Image",
                        ll_signup_selection_data
                    )

                }
            }
            R.id.img_select_gallery -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWriteStoragePermissionOther()

                    when (selectModeType) {
                        0 -> {
                            img_select_gallery.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.gallery_icon_selected_popup
                                )
                            )
                            img_select_camera.setColorFilter(resources.getColor(R.color.transperent))
                            img_select_camera.setImageDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.camera_icon_unselected_popup
                                )
                            )
                        }
                        1 -> {
                            img_select_camera.background =
                                ContextCompat.getDrawable(this, R.drawable.transparent_circle)
                            img_select_gallery.imageTintList =
                                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.yellow))
                            img_select_gallery.background =
                                ContextCompat.getDrawable(this, R.drawable.black_circle)
                            tv_gallery.setTextColor(resources.getColor(colorId!!))
                            tv_camera.setTextColor(resources.getColor(R.color.white))
                        }
                        2 -> {
                            img_select_camera.background =
                                ContextCompat.getDrawable(this, R.drawable.transparent_circle)
                            img_select_gallery.imageTintList =
                                ColorStateList.valueOf(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.colorAccent
                                    )
                                )
                            img_select_gallery.background =
                                ContextCompat.getDrawable(this, R.drawable.black_circle)
                            tv_gallery.setTextColor(resources.getColor(colorId!!))
                            tv_camera.setTextColor(resources.getColor(R.color.white))
                        }
                    }
                } else {
                    openGallery()
                }
            }
            R.id.btnSubmit -> {
                btnSubmit.textColor = (resources.getColor(R.color.black))
                btnSubmit.backgroundTint = (resources.getColor(colorId!!))
                chekValidation()
            }
            R.id.btnRetry -> {
                getFootballAgeGroupList()
            }
            R.id.img_select_male -> {
                userGender = "Male"
                when (selectModeType) {
                    0 -> {

                        img_select_female.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.gender_icon_unselected_female
                            )
                        )
                        img_select_male.setColorFilter(resources.getColor(R.color.transperent))
                        img_select_male.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.gender_icon_selected_male
                            )
                        )
                        btnSetGender.backgroundTint = (resources.getColor(colorPrimary))
                        btnSetGender.textColor = (resources.getColor(R.color.black))
                        btnSetGender.strokeColor = (resources.getColor(R.color.colorPrimary))
                        isSelect = true
                    }
                    1 -> {
                        img_select_female.background =
                            ContextCompat.getDrawable(this, R.drawable.transparent_circle)
                        img_select_female.setColorFilter(resources.getColor(R.color.yellow))

                        img_select_male.background =
                            ContextCompat.getDrawable(this, R.drawable.circle_border_yellow_light)
                        img_select_male.setColorFilter(resources.getColor(R.color.black))

                        tv_male.setTextColor(resources.getColor(R.color.yellow))
                        tv_female.setTextColor(resources.getColor(R.color.white))
                        btnSetGender.backgroundTint = (resources.getColor(R.color.yellow))
                        btnSetGender.textColor = (resources.getColor(R.color.black))
                        btnSetGender.strokeColor = (resources.getColor(R.color.yellow))
                        isSelect = true

                    }
                    2 -> {
                        img_select_female.background =
                            ContextCompat.getDrawable(this, R.drawable.transparent_circle)
                        img_select_female.setColorFilter(resources.getColor(R.color.colorAccent))

                        img_select_male.background =
                            ContextCompat.getDrawable(this, R.drawable.circle_border_accent_light)
                        img_select_male.setColorFilter(resources.getColor(R.color.black))

                        tv_female.setTextColor(resources.getColor(R.color.white))
                        tv_male.setTextColor(resources.getColor(R.color.colorAccent))

                        btnSetGender.backgroundTint = (resources.getColor(R.color.colorAccent))
                        btnSetGender.textColor = (resources.getColor(R.color.black))
                        btnSetGender.strokeColor = (resources.getColor(R.color.colorAccent))
                        isSelect = true
                    }
                }
            }
            R.id.img_select_female -> {
                userGender = "Female"
                when (selectModeType) {
                    0 -> {
                        img_select_female.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.gender_icon_selected_female
                            )
                        )
                        img_select_female.setColorFilter(resources.getColor(R.color.transperent))
                        img_select_male.setImageDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.gender_icon_unselected_male
                            )
                        )
                        btnSetGender.backgroundTint = (resources.getColor(colorPrimary))
                        btnSetGender.textColor = (resources.getColor(R.color.black))
                        btnSetGender.strokeColor = (resources.getColor(R.color.colorPrimary))
                        isSelect = true
                    }
                    1 -> {
//
                        img_select_female.background =
                            ContextCompat.getDrawable(this, R.drawable.circle_border_yellow_light)
                        img_select_female.setColorFilter(resources.getColor(R.color.black))

                        img_select_male.background =
                            ContextCompat.getDrawable(this, R.drawable.transparent_circle)
                        img_select_male.setColorFilter(resources.getColor(R.color.yellow))

                        tv_female.setTextColor(resources.getColor(R.color.yellow))
                        tv_male.setTextColor(resources.getColor(R.color.white))

                        btnSetGender.backgroundTint = (resources.getColor(R.color.yellow))
                        btnSetGender.textColor = (resources.getColor(R.color.black))
                        btnSetGender.strokeColor = (resources.getColor(R.color.yellow))
                        isSelect = true
                    }
                    2 -> {
//
                        img_select_female.background =
                            ContextCompat.getDrawable(this, R.drawable.circle_border_accent_light)
                        img_select_female.setColorFilter(resources.getColor(R.color.black))

                        img_select_male.background =
                            ContextCompat.getDrawable(this, R.drawable.transparent_circle)
                        img_select_male.setColorFilter(resources.getColor(R.color.colorAccent))

                        tv_female.setTextColor(resources.getColor(R.color.colorAccent))
                        tv_male.setTextColor(resources.getColor(R.color.white))

                        btnSetGender.backgroundTint = (resources.getColor(R.color.colorAccent))
                        btnSetGender.textColor = (resources.getColor(R.color.black))
                        btnSetGender.strokeColor = (resources.getColor(R.color.colorAccent))
                        isSelect = true
                    }
                }
            }
            R.id.countrylist_edit_text -> {
                PopupMenu(this@SignUpSelectionActivity, v, countrylist!!).showPopUp(object :
                    PopupMenu.OnMenuSelectItemClickListener {
                    override fun onItemClick(item: String, pos: Int) {
                        countrylist_edit_text.setText(item.toString())
                    }
                })
            }
            R.id.info -> {

//                var msg = getString(R.string.password_must)
//
//                if (sessionManager != null && sessionManager?.LanguageLabel != null) {
//                    if (!sessionManager?.LanguageLabel?.lngValidPasswordInfo.isNullOrEmpty())
//                        msg = sessionManager?.LanguageLabel?.lngValidPasswordInfo.toString()
//                }

//                MyUtils.showMessageOK(
//                    this@SignUpSelectionActivity,
//                    msg
//                ) { p0, p1 -> }

                val dialog = PasswordInfoDialog(selectModeType, this)
                dialog.show()

            }
        }
    }

    private fun setPasswordIcon() {

        password_textInput.isHintAnimationEnabled = false
        confirmPassword_textInput.isHintAnimationEnabled = false


        password_edit_text.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= password_edit_text.right - password_edit_text.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                        // your action here
                        password_edit_text.tooglePassWord()

                        return true
                    }
                }
                return false
            }
        })
        confirmPassword_edit_text.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= confirmPassword_edit_text.right - confirmPassword_edit_text.compoundDrawables[DRAWABLE_RIGHT].bounds.width()) {
                        // your action here
                        confirmPassword_edit_text.tooglePassWordConform()

                        return true
                    }
                }
                return false
            }
        })
    }

    fun EditText.tooglePassWord() {
        this.tag = !((this.tag ?: false) as Boolean)
        this.inputType = if (this.tag as Boolean)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        if (this.tag as Boolean) {
            password_edit_text.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                mDrawableEyeOff,
                null
            )

        } else {
            password_edit_text.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                mDrawableEye,
                null
            )
        }

        this.setSelection(this.length())
    }

    fun EditText.tooglePassWordConform() {
        this.tag = !((this.tag ?: false) as Boolean)
        this.inputType = if (this.tag as Boolean)
            InputType.TYPE_TEXT_VARIATION_PASSWORD
        else
            (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)

        if (this.tag as Boolean) {
            confirmPassword_edit_text.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                mDrawableEyeOff,
                null
            )

        } else {
            confirmPassword_edit_text.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                mDrawableEye,
                null
            )
        }

        this.setSelection(this.length())
    }

    fun textWatcher() {
        firstName_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })
        lastName_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })
        mobileNumber_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })
        emailAddress_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })
        password_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })
        confirmPassword_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })

    }

    fun nextButtonEnable() {
        if (validateSignUpInput()) {
            when (selectModeType) {
                0 -> {
                    btnSubmit.backgroundTint = (resources.getColor(R.color.colorPrimary))
                    btnSubmit.textColor = resources.getColor(R.color.black)
                }
                1 -> {
                    btnSubmit.backgroundTint = (resources.getColor(R.color.yellow))
                    btnSubmit.textColor = resources.getColor(R.color.black)
                }
            }

        } else {
            when (selectModeType) {
                0 -> {
                    btnSubmit.strokeColor = (resources.getColor(R.color.colorPrimary))
                    btnSubmit.backgroundTint = (resources.getColor(R.color.transperent1))
                    btnSubmit.textColor = resources.getColor(R.color.colorPrimary)

                }
                1 -> {
                    btnSubmit.strokeColor = (resources.getColor(R.color.yellow))
                    btnSubmit.backgroundTint = (resources.getColor(R.color.transperent1))
                    btnSubmit.textColor = resources.getColor(R.color.yellow)

                }
            }
        }
    }

    fun validateSignUpInput(): Boolean {
        var valid: Boolean = false
        when {
            firstName_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            lastName_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            mobileNumber_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            emailAddress_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            password_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            confirmPassword_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            else -> {
                valid = true
            }
        }
        return valid
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getWriteStoragePermissionOther() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(
                applicationContext,
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
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            this.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1002)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getCameraPermissionOther() {
        val permissionCheck =
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            takePhotoFromCamera()
        } else {
            this.requestPermissions(arrayOf(Manifest.permission.CAMERA), 1003)
        }
    }

    fun openGallery() {
        btnNextUserProfile.progressColor = colorId!!
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1211)
    }

    private fun takePhotoFromCamera() {

        btnNextUserProfile.progressColor = colorId!!
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            mfUser =
                ImageSaver(this@SignUpSelectionActivity).setFileName(
                    MyUtils.CreateFileName(
                        Date(),
                        ""
                    )
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
                        this@SignUpSelectionActivity,
                        applicationContext.packageName + ".fileprovider",
                        mfUser!!
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            mfUser = null
        }
        takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        startActivityForResult(takePictureIntent, 1212)
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
                    val message = "You need to grant access location permission through setting"

                    MyUtils.showMessageOKCancel(this@SignUpSelectionActivity,
                        message,
                        "Use location service?",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            val i = Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                            )
                            startActivity(i)
                        })
                }
            }
            1002 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    val message = "You need to grant access location permission through setting"

                    MyUtils.showMessageOKCancel(this@SignUpSelectionActivity,
                        message,
                        "Use location service?",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            val i = Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                            )
                            startActivity(i)
                        })
                }
            }
            1003 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    takePhotoFromCamera()
                    getCameraPermissionOther()
                } else {
                    val message = "You need to grant access location permission through setting"

                    MyUtils.showMessageOKCancel(this@SignUpSelectionActivity,
                        message,
                        "Use location service?",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            val i = Intent(
                                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                            )
                            startActivity(i)
                        })
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var picturePath: String? = ""
        when (requestCode) {
            1211 -> if (data != null && resultCode == Activity.RESULT_OK && this@SignUpSelectionActivity != null) {
                val imageUri = data.data
                var fileName = MyUtils.createFileName(Date(), "")
                picturePath =
                    MyUtils.getFilePathFromURI(this@SignUpSelectionActivity, imageUri!!, fileName)
                if (picturePath != null) {
                    if (picturePath.contains("https:")) {
                        MyUtils.showSnackbar(
                            this,
                            getString(R.string.please_select_other_profile_pic),
                            ll_signup_selection_data
                        )
                    } else {
                        profileImageName = picturePath
                        mfUser_Profile_Image = File(picturePath)
                        img_user_profile.isSelected = true
                        btnNextUserProfile.bgColor = resources.getColor(colorId!!)
                        btnNextUserProfile.textColor = (resources.getColor(R.color.black))
                        btnNextUserProfile.strokeColor = (resources.getColor(colorId!!))
                        camera_layout.visibility = View.GONE
                        frame_viewpager.visibility = VISIBLE


                        try {
                            detectFace(imageUri, "Gallary", mfUser_Profile_Image!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        // UploadImage(mfUser_Profile_Image!!)
                    }
                } else
                    MyUtils.showSnackbar(
                        this,
                        getString(R.string.please_select_other_profile_pic),
                        ll_signup_selection_data
                    )
            }
            1212 -> {

                if (resultCode == Activity.RESULT_OK) {
                    if (mfUser != null) {
                        try {
                            detectFace(Uri.fromFile(mfUser), "Camera", mfUser!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        // GetCameraTypedata(mfUser!!, "Photo")

                    }
                } else {
//                    MyUtils.showSnackbar(this, "Please select valid data", ll_signup_selection_data)
                }
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun GetCameraTypedata(mUser: File, Tye: String) {
        var mUser = mUser
        var picturePath = ""
        try {
            picturePath = mUser.absolutePath
            //var bm = BitmapFactory.decodeFile(picturePath)
            mUser = File(picturePath)

            Log.e("selectMode", selectModeType.toString())

            img_user_profile.setImageURI(Uri.fromFile(mUser))
            UploadImage(mUser)

            img_user_profile.isSelected = true
            btnNextUserProfile.bgColor = (resources.getColor(colorId!!))
            btnNextUserProfile.strokeColor = (resources.getColor(colorId!!))
            btnNextUserProfile.textColor = (resources.getColor(R.color.black))
            camera_layout.visibility = View.GONE
            frame_viewpager.visibility = VISIBLE

            profileImageName = picturePath
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }

    }

    fun imageBorder() {
        var roundingParams = RoundingParams()
        roundingParams.setBorder(resources.getColor(colorId!!), 3.0f)
        roundingParams.roundAsCircle = true
        img_user_profile.hierarchy.roundingParams = roundingParams
    }

    private fun chekValidation() {
        MyUtils.hideKeyboard1(this@SignUpSelectionActivity)
        when {
            firstName_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_first_name),
                    ll_signup_selection_data
                )
            }
            firstName_edit_text.text.toString().trim().length < 3 -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_valid_first_name),
                    ll_signup_selection_data
                )
            }
            lastName_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_family_name),
                    ll_signup_selection_data
                )
            }
            lastName_edit_text.text.toString().trim().length < 3 -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_valid_family_name),
                    ll_signup_selection_data
                )
            }
            mobileNumber_edit_text!!.text.toString().isEmpty() -> {

                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_mobile_number),
                    ll_signup_selection_data
                )
            }
            mobileNumber_edit_text.text.toString()
                .trim().length < 8 || mobileNumber_edit_text.text.toString().trim().length > 16 -> {

                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_valid_mobile_number),
                    ll_signup_selection_data
                )
            }
            countrylist_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_country_code),
                    ll_signup_selection_data
                )

            }
            emailAddress_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_email_id),
                    ll_signup_selection_data
                )

            }
            (!MyUtils.isValidEmail(emailAddress_edit_text.text.toString())) -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    resources.getString(R.string.valid_email_address),
                    ll_signup_selection_data
                )
            }
            password_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_password),
                    ll_signup_selection_data
                )
            }
            (!MyUtils.isValidPassword(password_edit_text!!.text.toString().trim())) -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    resources.getString(R.string.passwordValidation),
                    ll_signup_selection_data
                )
            }
            confirmPassword_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.please_enter_confirm_password),
                    ll_signup_selection_data
                )
            }
            (!(password_edit_text.text.toString()
                .trim()).equals((confirmPassword_edit_text.text.toString().trim()))) -> {
                MyUtils.showSnackbar(
                    this@SignUpSelectionActivity,
                    getString(R.string.password_and_confirm_password_match),
                    ll_signup_selection_data
                )
                confirmPassword_edit_text.requestFocus()
            }
            /*userSignedRefKey_edit_text!!.text.toString().isEmpty() -> {
                MyUtils.showSnackbar(this@SignUpSelectionActivity, getString(R.string.please_enter_confirm_password), ll_signup_selection_data)
            }*/
            else -> {
                signUp()
            }
        }

    }

    private fun signUp() {
        btnSubmit.startAnimation()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                btnSubmit.endAnimation()
                MyUtils.setViewAndChildrenEnabled(ll_signup_selection_data, true)
                ErrorUtil.errorMethod(ll_signup_selection_data)
                return@OnCompleteListener
            }
            val token = task.result
            val jsonObject = JSONObject()
            val jsonArray = JSONArray()
            try {
                jsonObject.put("languageID", "1")
                jsonObject.put("userBio", "")
                jsonObject.put("apiType", RestClient.apiType)
                jsonObject.put("apiVersion", RestClient.apiVersion)
                jsonObject.put("userDeviceType", RestClient.apiType)
                jsonObject.put("userDeviceID", token)
                jsonObject.put("userDOB", "")
                jsonObject.put("userEmail", emailAddress_edit_text.text.toString())
                jsonObject.put("userFirstName", firstName_edit_text.text.toString().capitalize())
                jsonObject.put("userLastName", lastName_edit_text.text.toString().capitalize())
                jsonObject.put("userGender", userGender)
                jsonObject.put("userMobile", mobileNumber_edit_text.text.toString())
                jsonObject.put("userPassword", password_edit_text.text.toString())
                jsonObject.put("userCountryCode", countrylist_edit_text.text.toString())
                jsonObject.put("userProfilePicture", profileImageName)
                jsonObject.put("userReferKey", "0")
                jsonObject.put("apputypeID", apputypeID)
                jsonObject.put("footbltypeID", footbltypeID)
                jsonObject.put("footbllevelID", "0")
                jsonObject.put("agegroupID", agegroupID)
                jsonObject.put("appuroleID", "1")
                jsonObject.put("loginuserID", "0")
                jsonObject.put("footballagecatID", "0")
                jsonObject.put("userFullname", "")
                jsonObject.put("userHeight", "0")
                jsonObject.put("userWeight", "0")
                jsonObject.put("userCoverPhoto", "")
                if (!MyUtils.fbID.isNullOrEmpty()) {
                    jsonObject.put("userFBID", MyUtils.fbID)
                }
                if (!MyUtils.linkedID.isNullOrEmpty()) {
                    jsonObject.put("userLinkedinID", MyUtils.linkedID)
                }
                jsonObject.put("specialityID", 0)
                jsonObject.put("userNickname", "")
                jsonObject.put("userBestFoot", "")
                jsonObject.put(
                    "userSignedRefKey",
                    userSignedRefKey_edit_text.text.toString().trim()
                )


            } catch (e: JSONException) {
                e.printStackTrace()
            }

            jsonArray.put(jsonObject)

            signup.userRegistration(this, false, jsonArray.toString(), "signup")
                .observe(
                    this@SignUpSelectionActivity,
                    androidx.lifecycle.Observer { loginPojo ->
                        if (loginPojo != null) {
                            btnSubmit.endAnimation()
                            if (loginPojo.get(0).status.equals("true", false)) {

                                try {
                                    StoreSessionManager(loginPojo.get(0).data.get(0))
                                    if (agegroupFrom > loginPojo.get(0).data[0].settings[0].settingsMinorAge.toInt()) {
                                        Handler().postDelayed({
                                            var intent = Intent(
                                                this@SignUpSelectionActivity,
                                                OtpVerificationActivity::class.java
                                            )
                                            intent.putExtra("selectModeType", selectModeType)
                                            intent.putExtra("from", "LoginByOtpVerification")
                                            Log.e("select2", selectModeType.toString())
                                            startActivity(intent)
                                            finishAffinity()
                                        }, 1000)
                                        MyUtils.showSnackbar(
                                            this,
                                            loginPojo.get(0).message,
                                            ll_signup_selection_data
                                        )

                                    } else {
                                        Handler().postDelayed({
                                            var intent = Intent(
                                                this@SignUpSelectionActivity,
                                                ParentInfoActivity::class.java
                                            )
                                            intent.putExtra("selectModeType", selectModeType)
                                            intent.putExtra("from", "LoginByOtpVerification")
                                            intent.putExtra(
                                                "loginuserID",
                                                loginPojo.get(0).data.get(0).userID
                                            )
                                            intent.putExtra(
                                                "countryCode",
                                                countrylist_edit_text.text.toString().trim()
                                            )
                                            Log.e("select2", selectModeType.toString())
                                            startActivity(intent)
                                            finishAffinity()
                                        }, 1000)
                                        MyUtils.showSnackbar(
                                            this,
                                            loginPojo.get(0).message,
                                            ll_signup_selection_data
                                        )

                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                MyUtils.showSnackbar(
                                    this,
                                    loginPojo.get(0).message,
                                    ll_signup_selection_data
                                )
                            }

                        } else {
                            btnSubmit.endAnimation()
                            ErrorUtil.errorMethod(ll_signup_selection_data)
                        }
                    })
        })

    }

    private fun UploadImage(picturePath: File) {
        if (MyUtils.isInternetAvailable(this@SignUpSelectionActivity)) {
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
                this@SignUpSelectionActivity,
                jsonArray.toString(),
                "users",
                picturePath,
                false,
                object : UploadFileModel.OnUploadFileListener {
                    override fun onSuccessUpload(datumList: UploadImagePojo?) {
//                            MyUtils.dismissProgressDialog()
                        profileImageName = datumList?.fileName!!

                    }

                    override fun onFailureUpload(
                        msg: String,
                        datumList: List<UploadImagePojo>?
                    ) {
//                            MyUtils.dismissProgressDialog()


                        try {
                            if (MyUtils.isInternetAvailable(this@SignUpSelectionActivity)) {
                                MyUtils.showSnackbar(
                                    this@SignUpSelectionActivity,
                                    resources.getString(R.string.error_crash_error_message),
                                    ll_signup_selection_data
                                )
                            } else {
                                MyUtils.showSnackbar(
                                    this@SignUpSelectionActivity,
                                    resources.getString(R.string.error_common_network),
                                    ll_signup_selection_data
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }


                })
        } else {
            MyUtils.showSnackbar(
                this@SignUpSelectionActivity,
                resources.getString(R.string.error_common_network),
                ll_signup_selection_data
            )
        }
    }

    private fun StoreSessionManager(uesedata: SignupData?) {

        val gson = Gson()

        val json = gson.toJson(uesedata)
        sessionManager!!.create_login_session(
            json,
            uesedata!!.userMobile,
            "",
            true,
            sessionManager!!.isEmailLogin()
        )

    }

    private fun getFootballAgeGroupList() {
        relativeprogressBar.visibility = VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        var jsonArray = JSONArray()
        jsonArray.put(jsonObject)
        footballTypeListModel.getFootballAgeGroupList(this, false, jsonArray.toString())
            .observe(
                this@SignUpSelectionActivity,
                androidx.lifecycle.Observer { ageListpojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = VISIBLE
                    if (ageListpojo != null) {
                        if (ageListpojo.get(0).status.equals("true", false)) {
                            ageGroupListData?.clear()
                            ageGroupListData?.addAll(ageListpojo.get(0).data)
                            ageGroupAdapter?.notifyDataSetChanged()
                        } else {

                            if (ageGroupListData!!.size == 0) {
                                ll_no_data_found.visibility = VISIBLE
                                recyclerview.visibility = View.GONE

                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = VISIBLE

                            }
                        }

                    } else {
                        errorMethod()
                    }
                })

    }

    private fun getCounrtyList() {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("blankCountryCode", "No")


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        countryListModel.getCountryList(this, false, jsonArray.toString())
            .observe(
                this@SignUpSelectionActivity
            ) { countryListPojo ->
                if (countryListPojo != null) {
                    if (countryListPojo[0].status.equals("true", false)) {
                        countryListData?.addAll(countryListPojo.get(0).data)
                        countrylist = java.util.ArrayList()
                        countrylist!!.clear()
                        for (i in 0 until countryListData!!.size) {
                            countrylist!!.add(countryListData!![i].countryDialCode)

                        }
                        countrylist_edit_text.setText(countryListPojo[0].data[0].countryDialCode)
                    }

                }
            }

    }

    private fun errorMethod() {
        relativeprogressBar.visibility = View.GONE
        try {
            nointernetMainRelativelayout.visibility = VISIBLE
            if (MyUtils.isInternetAvailable(this@SignUpSelectionActivity)) {
                nointernetImageview.setImageDrawable(this@SignUpSelectionActivity.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(this@SignUpSelectionActivity.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && typeSelectionViewpager.currentItem == 1) {
            typeSelectionViewpager.setCurrentItem(0, true)
            return true
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        if (typeSelectionViewpager.currentItem > 0 || (camera_layout.visibility == View.VISIBLE)) {
            typeSelectionViewpager.currentItem = typeSelectionViewpager.currentItem - 1
            camera_layout.visibility = View.GONE
        } else {
            super.onBackPressed()
            this.close()
        }
    }

    private fun close() {
        this.finish()
    }

    private fun detectFace(bitmap: Uri, s: String, mfUser_Profile_Image: File) {
        MyUtils.showProgressDialog(this@SignUpSelectionActivity, "Please wait..")
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
            image = FirebaseVisionImage.fromFilePath(this, bitmap)
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
                            this@SignUpSelectionActivity,
                            "NO FACE DETECT",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                } else {
                    Toast
                        .makeText(
                            this@SignUpSelectionActivity,
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
                        this@SignUpSelectionActivity,
                        "Oops, Something went wrong",
                        Toast.LENGTH_SHORT
                    )
                    .show()
            }
    }

}
