package com.nxplayr.fsl.ui.fragments.userprofile.view


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.drawee.view.SimpleDraweeView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.fragment.AddEmployementFragment
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.ui.fragments.bottomsheet.PrivacyBottomSheetFragment
import com.nxplayr.fsl.ui.fragments.usereducation.adapter.EducationAdapter
import com.nxplayr.fsl.ui.fragments.usereducation.view.EducationFragment
import com.nxplayr.fsl.ui.fragments.userhashtag.view.AddHashtagsFragment
import com.nxplayr.fsl.ui.fragments.userhashtag.view.HashtagsFragment
import com.nxplayr.fsl.ui.fragments.userhashtag.viewmodel.HashtagsModel
import com.nxplayr.fsl.ui.fragments.userhobbies.view.AddHobbiesFragment
import com.nxplayr.fsl.ui.fragments.userhobbies.view.HobbiesListFragment
import com.nxplayr.fsl.ui.fragments.userinterest.view.AddInterestsFragment
import com.nxplayr.fsl.ui.fragments.userlanguage.adapter.LanguagesAdapter
import com.nxplayr.fsl.ui.fragments.userlanguage.view.AddLanguageFragment
import com.nxplayr.fsl.ui.fragments.userlanguage.view.LanguagesFragment
import com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel.ProfileLanguageModel
import com.nxplayr.fsl.ui.fragments.userprofile.adapter.WorkExperienceAdapter
import com.nxplayr.fsl.ui.fragments.userprofilesummary.view.ProfileSummaryFragment
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.view.AddSkillsEndorsementsFragment
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.view.SkillsEndorsementsFragment
import com.nxplayr.fsl.util.*
import com.nxplayr.fsl.viewmodel.EducationModel
import com.nxplayr.fsl.viewmodel.EmployementModel
import kotlinx.android.synthetic.main.activity_signup_selection.*
import kotlinx.android.synthetic.main.fragment_hashtags.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_select_dialog_camera.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.Serializable
import java.text.ParseException
import java.util.*


class ProfileFragment : Fragment(), View.OnClickListener, PrivacyBottomSheetFragment.selectPrivacy {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var infaltorScheduleMode: LayoutInflater? = null
    var listLanguageData: ArrayList<ProfileLanguageData?>? = ArrayList()
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    val MY_PERMISSIONS_REQUEST_STORAGE = 121
    var i: Int = 0
    var profileImageName = ""
    var coverImageName = ""
    var mfUser: File? = null
    var fileName = ""
    var serverfileName = ""
    var serverCoverPicName = ""
    var mImageCaptureUri: Uri? = null
    var toDate = ""
    var formDate = ""
    var formDateEducation = ""
    var toDateEducation = ""
    var mfUser_Profile_Image: File? = null
    var from = ""
    var language_list: ArrayList<ProfileLanguageData?>? = ArrayList()
    var languagesAdapter: LanguagesAdapter? = null
    var employementList: ArrayList<EmploymentData?>? = ArrayList()
    var workExperienceAdapter: WorkExperienceAdapter? = null
    var education_list: ArrayList<EducationData?>? = ArrayList()
    var educationAdapter: EducationAdapter? = null
    var userId: String = ""
    var otherUserData: SignupData? = null
    var imgUrl = ""
    var image: FirebaseVisionImage? = null
    var detector: FirebaseVisionFaceDetector? = null
    private lateinit var loginModel: SignupModelV2
    private lateinit var getEmployementModel: EmployementModel
    private lateinit var educationModel: EducationModel
    private lateinit var profileModel: ProfileLanguageModel

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngWorkExperience.isNullOrEmpty())
                tv_work_exp_added.text = sessionManager?.LanguageLabel?.lngWorkExperience
            if (!sessionManager?.LanguageLabel?.lngAdd.isNullOrEmpty())
                tv_add_work_experience.text = sessionManager?.LanguageLabel?.lngAdd
            if (!sessionManager?.LanguageLabel?.lngEducation.isNullOrEmpty())
                tv_education.text = sessionManager?.LanguageLabel?.lngEducation
            if (!sessionManager?.LanguageLabel?.lngAdd.isNullOrEmpty())
                tv_add_education.text = sessionManager?.LanguageLabel?.lngAdd
            if (!sessionManager?.LanguageLabel?.lngLanguage.isNullOrEmpty())
                tv_language.text = sessionManager?.LanguageLabel?.lngLanguage
            if (!sessionManager?.LanguageLabel?.lngAdd.isNullOrEmpty())
                tv_add_language.text = sessionManager?.LanguageLabel?.lngAdd
            if (!sessionManager?.LanguageLabel?.lngSkillsEndorsements.isNullOrEmpty())
                tv_skill_added.text = sessionManager?.LanguageLabel?.lngSkillsEndorsements
            if (!sessionManager?.LanguageLabel?.lngAdd.isNullOrEmpty())
                tv_add_skills.text = sessionManager?.LanguageLabel?.lngAdd
            if (!sessionManager?.LanguageLabel?.lngHobbies.isNullOrEmpty())
                tv_hobbies.text = sessionManager?.LanguageLabel?.lngHobbies
            if (!sessionManager?.LanguageLabel?.lngAdd.isNullOrEmpty())
                tv_add_hobbies.text = sessionManager?.LanguageLabel?.lngAdd
            if (!sessionManager?.LanguageLabel?.lngInterest.isNullOrEmpty())
                tv_interests.text = sessionManager?.LanguageLabel?.lngInterest
            if (!sessionManager?.LanguageLabel?.lngAdd.isNullOrEmpty())
                tv_add_interests.text = sessionManager?.LanguageLabel?.lngAdd
            if (!sessionManager?.LanguageLabel?.lngAddInterests.isNullOrEmpty())
                add_interest.text = sessionManager?.LanguageLabel?.lngAddInterests
            if (!sessionManager?.LanguageLabel?.lngHashtags.isNullOrEmpty())
                tv_hashtags.text = sessionManager?.LanguageLabel?.lngHashtags
            if (!sessionManager?.LanguageLabel?.lngAddHashtags.isNullOrEmpty())
                add_hashtag.text = sessionManager?.LanguageLabel?.lngAddHashtags
            if (!sessionManager?.LanguageLabel?.lngContactInformation.isNullOrEmpty())
                tv_contct_information.text = sessionManager?.LanguageLabel?.lngContactInformation
            if (!sessionManager?.LanguageLabel?.lngAdd.isNullOrEmpty())
                tv_add_contct_information.text = sessionManager?.LanguageLabel?.lngAdd
            if (!sessionManager?.LanguageLabel?.lngYourProfile.isNullOrEmpty())
                your_profile.text = sessionManager?.LanguageLabel?.lngYourProfile
            if (!sessionManager?.LanguageLabel?.lngMobileNo.isNullOrEmpty())
                mob_num.text = sessionManager?.LanguageLabel?.lngMobileNo
            if (!sessionManager?.LanguageLabel?.lngEmailAddress.isNullOrEmpty())
                email_adr.text = sessionManager?.LanguageLabel?.lngEmailAddress

            if (!sessionManager?.LanguageLabel?.lngViewMore.isNullOrEmpty())
                tv_view_more.text = sessionManager?.LanguageLabel?.lngViewMore
            if (!sessionManager?.LanguageLabel?.lngAddExperience.isNullOrEmpty())
                add_exp.text = sessionManager?.LanguageLabel?.lngAddExperience
            if (!sessionManager?.LanguageLabel?.lngViewMore.isNullOrEmpty())
                tv_view_more_education.text = sessionManager?.LanguageLabel?.lngViewMore
            if (!sessionManager?.LanguageLabel?.lngAddEducation.isNullOrEmpty())
                add_edu.text = sessionManager?.LanguageLabel?.lngAddEducation
            if (!sessionManager?.LanguageLabel?.lngViewMore.isNullOrEmpty())
                tv_view_more_language.text = sessionManager?.LanguageLabel?.lngViewMore
            if (!sessionManager?.LanguageLabel?.lngAddLanguages.isNullOrEmpty())
                add_lang.text = sessionManager?.LanguageLabel?.lngAddLanguages
            if (!sessionManager?.LanguageLabel?.lngViewMore.isNullOrEmpty())
                tv_view_moreSkills.text = sessionManager?.LanguageLabel?.lngViewMore
            if (!sessionManager?.LanguageLabel?.lngAddSkillsEndorsements.isNullOrEmpty())
                add_skils.text = sessionManager?.LanguageLabel?.lngAddSkillsEndorsements
            if (!sessionManager?.LanguageLabel?.lngViewMore.isNullOrEmpty())
                tv_view_moreHobbies.text = sessionManager?.LanguageLabel?.lngViewMore
            if (!sessionManager?.LanguageLabel?.lngAddHobbies.isNullOrEmpty())
                add_hobies.text = sessionManager?.LanguageLabel?.lngAddHobbies
            if (!sessionManager?.LanguageLabel?.lngViewMore.isNullOrEmpty())
                tv_view_more_interests.text = sessionManager?.LanguageLabel?.lngViewMore
            if (!sessionManager?.LanguageLabel?.lngViewMore.isNullOrEmpty())
                tv_view_more_hashtag.text = sessionManager?.LanguageLabel?.lngViewMore
            if (!sessionManager?.LanguageLabel?.lngChange.isNullOrEmpty())
                tv_changeCoverPhoto.text = sessionManager?.LanguageLabel?.lngChange
            if (!sessionManager?.LanguageLabel?.lngCamera.isNullOrEmpty())
                tv_camera.text = sessionManager?.LanguageLabel?.lngCamera
            if (!sessionManager?.LanguageLabel?.lngGallery.isNullOrEmpty())
                tv_gallery.text = sessionManager?.LanguageLabel?.lngGallery
            if (!sessionManager?.LanguageLabel?.lngClose.isNullOrEmpty())
                btnCloseProfileSelection.progressText = sessionManager?.LanguageLabel?.lngClose
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_profile, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
            if (arguments != null) {
                userId = arguments!!.getString("userID").toString()
            }
        }
        loginModel = ViewModelProvider(this@ProfileFragment).get(SignupModelV2::class.java)
        getEmployementModel = ViewModelProvider(this@ProfileFragment).get(EmployementModel::class.java)
        profileModel = ViewModelProvider(this@ProfileFragment).get(ProfileLanguageModel::class.java)
        educationModel = ViewModelProvider(this@ProfileFragment).get(EducationModel::class.java)

        if (!userId.equals(userData?.userID, false)) {
            llEducation.visibility = View.GONE
            edit_imageProfile.visibility = View.GONE
            tv_changeCoverPhoto.visibility = View.GONE
            rv_Education.visibility = View.GONE
            tv_view_more_education.visibility = View.GONE
            llLanguage.visibility = View.GONE
            rv_language.visibility = View.GONE
            tv_view_more_language.visibility = View.GONE
            llInterests.visibility = View.GONE
            layout_view_interest.visibility = View.GONE
            layout_view_interests.visibility = View.GONE
            tv_view_more_interests.visibility = View.GONE
            llRecommendations.visibility = View.GONE
            layout_view_recommend.visibility = View.GONE
            layout_view_recommendation.visibility = View.GONE
            tv_view_recommendation.visibility = View.GONE
            tv_add_work_experience.visibility = View.GONE
            image_profileSummary.visibility = View.GONE

            layout_view_workExp.isEnabled = false
            layout_view_skillsList.isEnabled = false
            ll_mainHobbiesList.isEnabled = false
            layout_view_hashtags.isEnabled = false

            tv_add_work_experience.visibility = View.GONE
            tv_add_skills.visibility = View.GONE
            layout_add_skills.isEnabled = false
            tv_add_hobbies.visibility = View.GONE
            tv_add_interests.visibility = View.GONE
            tv_add_hashtags.visibility = View.GONE
            tv_add_recommendations.visibility = View.GONE
            tv_add_contct_information.visibility = View.GONE

            getOtherUserProfileData(userId)
        } else {
            setonClick()

            llEducation.visibility = View.VISIBLE
            edit_imageProfile.visibility = View.VISIBLE
            tv_changeCoverPhoto.visibility = View.VISIBLE
            image_profileSummary.visibility = View.VISIBLE
            rv_Education.visibility = View.VISIBLE
            tv_view_more_education.visibility = View.VISIBLE
            llLanguage.visibility = View.VISIBLE
            rv_language.visibility = View.VISIBLE
            tv_view_more_language.visibility = View.VISIBLE
            llInterests.visibility = View.VISIBLE
            layout_view_interest.visibility = View.VISIBLE
            layout_view_interests.visibility = View.VISIBLE
            tv_view_more_interests.visibility = View.GONE

            llRecommendations.visibility = View.GONE
            layout_view_recommend.visibility = View.GONE
            layout_view_recommendation.visibility = View.GONE
            tv_view_recommendation.visibility = View.GONE

            layout_view_workExp.isEnabled = true
            layout_view_skillsList.isEnabled = true
            ll_mainHobbiesList.isEnabled = true
            layout_view_hashtags.isEnabled = true

            tv_add_work_experience.visibility = View.VISIBLE
            tv_add_skills.visibility = View.VISIBLE
            layout_add_skills.isEnabled = true
            tv_add_hobbies.visibility = View.VISIBLE
            tv_add_interests.visibility = View.VISIBLE
            tv_add_hashtags.visibility = View.GONE
            tv_add_recommendations.visibility = View.GONE
            tv_add_contct_information.visibility = View.VISIBLE

            if (userData != null) {
                setUserData(userData!!)
                setLanguageData(userData?.languages!!, userData?.userID!!, userData?.languageID!!)
                setEducationData(userData?.education!!, userData?.userID!!, userData?.languageID!!)
                setInterestData(userData?.education!!, userData?.userID!!, userData?.languageID!!)
            }
        }

        btnRetry.setOnClickListener {
//            ll_mainProfileList.visibility = View.VISIBLE
            if (!userId.equals(userData?.userID, false)) {
                setUserData(otherUserData!!)
            } else {
                setUserData(userData!!)
            }
        }

        layout_view_workExp.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddEmployementFragment(),
                AddEmployementFragment::class.java.name,
                true
            )
        }
        layout_viewEducation.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddEducationFragment(),
                AddEducationFragment::class.java.name,
                true
            )
        }
        layout_view_languages.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddLanguageFragment(),
                AddLanguageFragment::class.java.name,
                true
            )
        }
        layout_view_skillsList.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddSkillsEndorsementsFragment(),
                AddSkillsEndorsementsFragment::class.java.name,
                true
            )

        }
        ll_mainHobbiesList.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddHobbiesFragment(),
                AddHobbiesFragment::class.java.name,
                true
            )
        }
        layout_view_hashtags.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddHashtagsFragment(),
                AddHashtagsFragment::class.java.name,
                true
            )
        }
        tv_add_work_experience.setOnClickListener {
            //            Intent(mActivity, AddEmploymenyActivity::class.java).apply {
//                mActivity?.startActivity(this)
//            }
            (activity as MainActivity).navigateTo(
                AddEmployementFragment(),
                AddEmployementFragment::class.java.name,
                true
            )
        }
        tv_add_education.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddEducationFragment(),
                AddEducationFragment::class.java.name,
                true
            )
        }
        tv_add_language.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddLanguageFragment(),
                AddLanguageFragment::class.java.name,
                true
            )

        }
        tv_add_skills.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddSkillsEndorsementsFragment(),
                AddSkillsEndorsementsFragment::class.java.name,
                true
            )
        }
        layout_add_skills.setOnClickListener {
            (activity as MainActivity).navigateTo(
                SkillsEndorsementsFragment(),
                SkillsEndorsementsFragment::class.java.name,
                true
            )
        }
        tv_add_hobbies.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddHobbiesFragment(),
                AddHobbiesFragment::class.java.name,
                true
            )
        }
        tv_add_interests.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddInterestsFragment.newInstance(2),
                AddInterestsFragment::class.java.name,
                true
            )
        }
        tv_add_hashtags.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddHashtagsFragment(),
                AddHashtagsFragment::class.java.name,
                true
            )
        }
        tv_add_recommendations.setOnClickListener {
            // (activity as MainActivity).navigateTo(RequestRecommendationFragment(), RequestRecommendationFragment::class.java.name, true)
        }
        tv_add_contct_information.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddContactInformationFragment(),
                AddContactInformationFragment::class.java.name,
                true
            )
        }
        tv_view_more.setOnClickListener {
            Bundle().apply {
                putString("userId", userId)
                (activity as MainActivity).navigateTo(
                    WorkExperienceFragment(), this,
                    WorkExperienceFragment::class.java.name, true
                )
            }

        }
        tv_view_more_education.setOnClickListener {
            (activity as MainActivity).navigateTo(
                EducationFragment(),
                EducationFragment::class.java.name,
                true
            )
        }
        tv_view_more_language.setOnClickListener {
            (activity as MainActivity).navigateTo(
                LanguagesFragment(),
                LanguagesFragment::class.java.name,
                true
            )
        }
        tv_view_moreSkills.setOnClickListener {
            Bundle().apply {
                putString("userId", userId)
                (activity as MainActivity).navigateTo(
                    SkillsEndorsementsFragment(), this,
                    SkillsEndorsementsFragment::class.java.name, true
                )
            }
        }
        tv_view_moreHobbies.setOnClickListener {
            Bundle().apply {
                putString("userId", userId)
                (activity as MainActivity).navigateTo(
                    HobbiesListFragment(),
                    this,
                    HobbiesListFragment::class.java.name,
                    true
                )
            }
        }
        tv_view_more_interests.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddInterestsFragment(),
                AddInterestsFragment::class.java.name,
                true
            )
        }
        tv_view_more_hashtag.setOnClickListener {
            Bundle().apply {
                putString("userId", userId)
                (activity as MainActivity).navigateTo(
                    HashtagsFragment(), this,
                    HashtagsFragment::class.java.name, true
                )
            }
        }
        tv_view_recommendation.setOnClickListener {
            // (activity as MainActivity).navigateTo(GivenFragment(), GivenFragment::class.java.name, true)
        }

        loginModel.otherUserProfile
            .observe(viewLifecycleOwner,
                androidx.lifecycle.Observer { loginPojo ->

                    if (loginPojo != null) {
                        MyUtils.dismissProgressDialog()
                        if (loginPojo[0].status.equals("true", true)) {

                            if (loginPojo[0].data.size > 0) {
                                otherUserData = loginPojo[0].data[0]

                                if (otherUserData != null) {
                                    setUserData(otherUserData!!)
                                    //  SetOtherUserData(otherUserData!!)
                                }
                            }
                        } else {
//                            noDatafoundRelativelayout?.visibility = View.VISIBLE
                        }
                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }
                })
        loginModel.userUploadProfilePicture.observe(
            this@ProfileFragment,
            androidx.lifecycle.Observer { loginPojo ->
                if (loginPojo != null) {
//                                MyUtils.dismissProgressDialog()
                    if (loginPojo.get(0).status.equals("true", true)) {

                        try {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message!!,
                                ll_mainProfile
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
                            loginPojo.get(0).message!!,
                            ll_mainProfile
                        )
                    }

                } else {
//                                MyUtils.dismissProgressDialog()
                    ErrorUtil.errorMethod(ll_mainProfile)
                }
            })
        loginModel.userupdateCoverPhoto
            .observe(this@ProfileFragment!!,
                androidx.lifecycle.Observer { loginPojo ->
                    if (loginPojo != null) {
//                                MyUtils.dismissProgressDialog()
                        if (loginPojo.get(0).status.equals("true", true)) {
                            try {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    loginPojo.get(0).message!!,
                                    ll_mainProfile
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
                                loginPojo.get(0).message!!,
                                ll_mainProfile
                            )
                        }

                    } else {
//                                MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(ll_mainProfile)
                    }
                })

    }

    private fun setInterestData(
        education: java.util.ArrayList<EducationData>,
        userID: String,
        languageID: String
    ) {


    }

    private fun setonClick() {
        img_select_camera.setOnClickListener(this)
        img_select_gallery.setOnClickListener(this)
        btnCloseProfileSelection.setOnClickListener(this)
        edit_imageProfile.setOnClickListener(this)
        tv_changeCoverPhoto.setOnClickListener(this)
        image_profileSummary.setOnClickListener(this)
        tv_yourProfile.setOnClickListener(this)

    }

    private fun deleteHashtags(userhashtagID: String, hashView: View) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("userhashtagID", userhashtagID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        Log.e("data", jsonArray.toString())
        val hashtagsModel = ViewModelProvider(this).get(HashtagsModel::class.java)
        hashtagsModel.getHashtagsList(mActivity!!, false, jsonArray.toString(), "Delete")
            .observe(
                mActivity!!
            ) { hashtagpojo ->
                MyUtils.dismissProgressDialog()
                if (hashtagpojo != null && hashtagpojo.isNotEmpty()) {

                    if (hashtagpojo[0].status.equals("true", false)) {
                        val userData = sessionManager!!.userData
                        if (userData!!.hashtags.size > 0) {
                            for (i in 0 until userData.hashtags.size) {
                                if (userhashtagID == userData.hashtags[i].userhashtagID) {
                                    userData.hashtags.removeAt(i)
                                    sessionManager!!.userData = userData
                                    layout_view_hashtag.removeView(hashView)
                                }
                            }
                        }
                        if (userData.hashtags.size == 0) {
                            layout_view_hashtags.visibility = View.VISIBLE
                            layout_view_hashtag.visibility = View.GONE
                            tv_view_more_hashtag.visibility = View.GONE
                        }
                    } else {
                        if (activity != null && activity is MainActivity)
                            MyUtils.showSnackbar(
                                mActivity!!,
                                hashtagpojo.get(0).message, ll_mainHashtagsList
                            )
                    }

                } else {
                    ErrorUtil.errorMethod(ll_mainHashtagsList)
                }
            }
    }


    fun getOtherUserProfileData(userId: String) {
        MyUtils.showProgressDialog(mActivity!!, "")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userId)
            jsonObject.put("otherUserID", this.userId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        loginModel.otherUserProfile(jsonArray.toString())

    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.edit_imageProfile -> {
                from = "profileImage"
                gallery_layout.visibility = View.VISIBLE
                img_select_camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_unselected_popup))
                img_select_camera.setColorFilter(resources.getColor(R.color.transperent))
                img_select_gallery.setColorFilter(resources.getColor(R.color.transperent))
                img_select_gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon))

            }
            R.id.tv_changeCoverPhoto -> {
                from = "coverImage"
                gallery_layout.visibility = View.VISIBLE
                img_select_camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_unselected_popup))
                img_select_camera.setColorFilter(resources.getColor(R.color.transperent))
                img_select_gallery.setColorFilter(resources.getColor(R.color.transperent))
                img_select_gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon))
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
            R.id.btnCloseProfileSelection -> {
                gallery_layout.visibility = View.GONE
            }
            R.id.image_profileSummary -> {
                (activity as MainActivity).navigateTo(
                    ProfileSummaryFragment(),
                    ProfileSummaryFragment::class.java.name,
                    true
                )
            }
            R.id.tv_yourProfile -> {
                getPrivacy()
            }
        }
    }

    @JvmName("setUserData1")
    private fun setUserData(userData: SignupData) {

        proflie_user_name?.text = userData?.userFirstName + " " + userData?.userLastName
        tv_userProileBio.text = userData?.userBio
        tv_desc.text =
            userData?.userTitle + " | " + userData?.userPosition + " | " + userData?.userOrganisation
        tv_desc2.text = userData?.userWhoAmI

        if (userData?.userOVerified.equals("yes", true)) {
            verified.visibility = View.VISIBLE
        } else {
            verified.visibility = View.GONE
        }

        profile_userImage.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
        image_Coverprofile.setImageURI(RestClient.image_base_url_users + userData?.userCoverPhoto)

        tv_mobileNum.text = userData!!.userCountryCode + " " + userData?.userMobile
        tv_emailAddress.text = userData?.userEmail

        setEmployementData(userData?.employement, userData?.userID, userData?.languageID)

        layout_view_interest.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddInterestsFragment(),
                AddInterestsFragment::class.java.name,
                true
            )
        }

        layout_view_recommend.setOnClickListener {
            //  (activity as MainActivity).navigateTo(RequestRecommendationFragment(), RequestRecommendationFragment::class.java.name, true)
        }


        if (!userData?.hashtags.isNullOrEmpty()) {
            layout_view_hashtag.removeAllViews()

            layout_view_hashtags.visibility = View.GONE
            layout_view_hashtag.visibility = View.VISIBLE
            tv_view_more_hashtag.visibility = View.VISIBLE

            for (i in 0 until userData!!.hashtags.size) {
                tv_view_more_hashtag.visibility = View.VISIBLE
                infaltorScheduleMode =
                    activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
                var convertView = infaltorScheduleMode?.inflate(R.layout.layout_hashtag, null)

                var tv_hashtagsName = convertView?.findViewById(R.id.tv_hashtagsName) as TextView
                var btn_following = convertView.findViewById(R.id.btn_following) as SimpleDraweeView
                btn_following.visibility = View.VISIBLE

                if (i == 0 || i == 1) {
                    tv_hashtagsName.text = userData.hashtags[i].hashtagName
                    layout_view_hashtag.addView(convertView)
                }

                btn_following.setOnClickListener {
                    deleteHashtags(userData.hashtags[i].userhashtagID, convertView)
                }

            }
        } else {
            layout_view_hashtags.visibility = View.VISIBLE
            layout_view_hashtag.visibility = View.GONE
            tv_view_more_hashtag.visibility = View.GONE
        }

        if (!userData?.skills.isNullOrEmpty()) {

            layout_view_skillsList.visibility = View.GONE
            ll_mainAddSkills.visibility = View.VISIBLE
            tv_view_moreSkills.visibility = View.VISIBLE
            ll_mainAddSkills.removeAllViews()
            for (i in 0 until userData!!.skills.size) {
                infaltorScheduleMode =
                    activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
                var convertView = infaltorScheduleMode?.inflate(
                    com.nxplayr.fsl.R.layout.list_item_skill_layout,
                    null
                )

                var tv_skill = convertView?.findViewById(R.id.skills_nameTv) as TextView
                if (i == 0 || i == 1) {
                    tv_skill.text = userData!!.skills[i].skillName
                    ll_mainAddSkills.addView(convertView)
                }
            }
        } else {
            layout_view_skillsList.visibility = View.VISIBLE
            ll_mainAddSkills.visibility = View.GONE
            tv_view_moreSkills.visibility = View.GONE
        }
        if (!userData?.hobbies.isNullOrEmpty()) {

            ll_mainHobbiesList.visibility = View.GONE
            ll_mainAddHobbies.visibility = View.VISIBLE
            tv_view_moreHobbies.visibility = View.VISIBLE
            ll_mainAddHobbies.removeAllViews()
            for (i in 0 until userData!!.hobbies.size) {
                infaltorScheduleMode =
                    activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
                var convertView = infaltorScheduleMode?.inflate(
                    com.nxplayr.fsl.R.layout.list_item_skill_layout,
                    null
                )

                var tv_skill = convertView?.findViewById(R.id.skills_nameTv) as TextView
                if (i == 0 || i == 1) {
                    tv_skill.text = userData!!.hobbies[i].hobbyName
                    ll_mainAddHobbies.addView(convertView)
                }
            }
        } else {
            ll_mainHobbiesList.visibility = View.VISIBLE
            ll_mainAddHobbies.visibility = View.GONE
            tv_view_moreHobbies.visibility = View.GONE
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
            //var bm = BitmapFactory.decodeFile(picturePath)
            mUser = File(picturePath)
            profile_userImage.setImageURI(Uri.fromFile(mUser))

            UploadImage(mUser)

            gallery_layout.visibility = View.GONE


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
            image_Coverprofile.setImageURI(Uri.fromFile(mUser))

            UploadImage(mUser)

            gallery_layout.visibility = View.GONE

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
                        ll_signup_selection_data
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
                        ll_signup_selection_data
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
                        ll_signup_selection_data
                    )
                }
            }
            153 -> {
                writePermission()
            }
            152 -> {
                writePermission()
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var picturePath: String? = ""
        var picturePathcover: String? = ""
        when (requestCode) {

            1211 -> {

                if (data != null && resultCode == Activity.RESULT_OK && this@ProfileFragment != null) {
                    var imageUri = data.data
                    var fileName = MyUtils.createFileName(Date(), "")
                    picturePath = MyUtils.getFilePathFromURI(mActivity!!, imageUri!!, fileName)
                    if (picturePath != null) {
                        if (picturePath.contains("https:")) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                getString(R.string.please_select_other_profile_pic),
                                ll_signup_selection_data
                            )
                        } else {
                            profileImageName = picturePath
                            mfUser_Profile_Image = File(picturePath)
                            gallery_layout.visibility = View.GONE
                            try {
                                detectFace(imageUri, "Gallary", mfUser_Profile_Image!!)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            //UploadImage(mfUser_Profile_Image!!)
                        }
                    } else
                        MyUtils.showSnackbar(
                            mActivity!!,
                            getString(R.string.please_select_other_profile_pic),
                            ll_signup_selection_data
                        )
                } else {

//                    MyUtils.showSnackbar(
//                            this,
//                            getString(R.string.please_select_other_profile_pic),
//                            ll_signup_selection_data
//                    )

                }
            }

            1413 -> {

                if (data != null && resultCode == Activity.RESULT_OK && this@ProfileFragment != null) {
                    var imageUri = data.data
                    var fileName = MyUtils.createFileName(Date(), "")
                    picturePathcover = MyUtils.getFilePathFromURI(mActivity!!, imageUri!!, fileName)
                    if (picturePathcover != null) {
                        if (picturePathcover.contains("https:")) {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                getString(R.string.please_select_other_profile_pic),
                                ll_signup_selection_data
                            )
                        } else {
                            coverImageName = picturePathcover
                            mfUser_Profile_Image = File(picturePathcover)
                            image_Coverprofile.setImageURI(imageUri)

                            gallery_layout.visibility = View.GONE
                            UploadImage(mfUser_Profile_Image!!)
                        }
                    } else
                        MyUtils.showSnackbar(
                            mActivity!!,
                            getString(R.string.please_select_other_profile_pic),
                            ll_signup_selection_data
                        )
                } else {

//                    MyUtils.showSnackbar(
//                            this,
//                            getString(R.string.please_select_other_profile_pic),
//                            ll_signup_selection_data
//                    )

                }
            }
            1212 -> {

                if (resultCode == Activity.RESULT_OK) {
                    if (mfUser != null) {
                        gallery_layout.visibility = View.GONE
                        try {
                            detectFace(Uri.fromFile(mfUser), "Camera", mfUser!!)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //  GetCameraTypedata(mfUser!!, "Photo")
                    }
                } else {
//                    MyUtils.showSnackbar(this, "Please select valid data", ll_signup_selection_data)
                }
            }
            1414 -> {

                if (resultCode == Activity.RESULT_OK) {
                    if (mfUser != null) {
                        GetCoverCameraTypedata(mfUser!!, "Photo")
                    }
                } else {
//                    MyUtils.showSnackbar(this, "Please select valid data", ll_signup_selection_data)
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
                            profile_userImage.setImageURI(Uri.fromFile(mfUser_Profile_Image))
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
                                    ll_signup_selection_data
                                )
                            } else {
                                MyUtils.showSnackbar(
                                    mActivity!!,
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
                mActivity!!,
                resources.getString(R.string.error_common_network),
                ll_signup_selection_data
            )
        }
    }


    private fun getUpdateProfilePic(serverfileName: String) {
//        MyUtils.showProgressDialog(mActivity!!, "Please wait")
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

        loginModel.userUploadProfilePicture(jsonArray.toString())

    }


    private fun getUpdateCoverPic(serverfileName: String) {
//        MyUtils.showProgressDialog(mActivity!!, "Please wait")
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

        loginModel.userupdateCoverPhoto(jsonArray.toString())

    }


    fun setEmployementData(
        employement: java.util.ArrayList<EmploymentData>,
        userID: String,
        languageID: String
    ) {
        if (!employement!!.isNullOrEmpty()) {
            rv_employement.visibility = View.VISIBLE
            tv_view_more.visibility = View.VISIBLE
            layout_view_workExp.visibility = View.GONE

            workExperienceAdapter = WorkExperienceAdapter(
                mActivity!!,
                employementList!!,
                object : WorkExperienceAdapter.OnItemClick {
                    override fun onClicled(
                        position: Int,
                        from: String,
                        v: View,
                        empData: EmploymentData?
                    ) {
                        when (from) {

                        }
                    }
                },
                "emp_profile",
                userId
            )
            rv_employement.layoutManager = LinearLayoutManager(activity)
            rv_employement.setHasFixedSize(true)
            rv_employement.adapter = workExperienceAdapter
            workExperienceAdapter!!.notifyDataSetChanged()
            getEmploymentList(userID, languageID)
        } else {
            rv_employement.visibility = View.GONE
            tv_view_more.visibility = View.GONE
            layout_view_workExp.visibility = View.VISIBLE
        }
    }

    private fun getEmploymentList(userID: String, languageID: String) {

        relativeprogressBar.visibility = View.VISIBLE
        nointernetMainRelativelayout.visibility = View.GONE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", userID)
            jsonObject.put("languageID", languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        getEmployementModel.employeeApi(jsonArray.toString(), "List")
        getEmployementModel.successEmployee
            .observe(viewLifecycleOwner,
                androidx.lifecycle.Observer { employementPojo ->

                    relativeprogressBar.visibility = View.GONE
                    rv_employement.visibility = View.VISIBLE
                    ll_mainProfileList.visibility = View.VISIBLE


                    if (employementPojo != null && employementPojo.isNotEmpty()) {
                        if (employementPojo[0].status.equals("true", true)) {
                            employementList?.clear()
                            employementList?.addAll(employementPojo!![0]!!.data!!)
                            workExperienceAdapter?.notifyDataSetChanged()

                        } else {
                            if (employementList!!.size == 0) {
                                rv_employement.visibility = View.GONE
                            } else {
                                rv_employement.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                        ll_mainProfileList.visibility = View.GONE

                    }
                })

    }

    fun setEducationData(
        education: java.util.ArrayList<EducationData>,
        userID: String,
        languageID: String
    ) {
        if (!education!!.isNullOrEmpty()) {
            rv_Education.visibility = View.VISIBLE
            tv_view_more_education.visibility = View.VISIBLE
            layout_viewEducation.visibility = View.GONE

            educationAdapter = EducationAdapter(
                activity as MainActivity,
                education_list,
                object : EducationAdapter.OnItemClick {
                    override fun onClicled(position: Int, from: String, v: View) {
                        when (from) {

                        }
                    }
                },
                "edu_profile"
            )
            rv_Education.layoutManager = LinearLayoutManager(activity)
            rv_Education.setHasFixedSize(true)
            rv_Education.adapter = educationAdapter
            educationAdapter!!.notifyDataSetChanged()
            getEducationList(userID, languageID)
        } else {
            rv_Education.visibility = View.GONE
            tv_view_more_education.visibility = View.GONE
            layout_viewEducation.visibility = View.VISIBLE
        }
    }


    private fun getEducationList(userID: String, languageID: String) {

        relativeprogressBar.visibility = View.VISIBLE
        nointernetMainRelativelayout.visibility = View.GONE
//        ll_mainProfileList.visibility = View.VISIBLE


        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userID)
            jsonObject.put("languageID", languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        educationModel.educationApi(jsonArray.toString(), "List")
        educationModel.successEducation
            .observe(viewLifecycleOwner,
                androidx.lifecycle.Observer { educationPojo ->

                    relativeprogressBar.visibility = View.GONE
                    rv_Education.visibility = View.VISIBLE
                    ll_mainProfileList.visibility = View.VISIBLE


                    if (educationPojo != null && educationPojo.isNotEmpty()) {
                        if (educationPojo[0].status.equals("true", true)) {
                            education_list?.clear()
                            education_list?.addAll(educationPojo!![0]!!.data!!)
                            educationAdapter?.notifyDataSetChanged()

                        } else {

                            if (education_list!!.size == 0) {
                                rv_Education.visibility = View.GONE

                            } else {
                                rv_Education.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                        ll_mainProfileList.visibility = View.GONE

                    }
                })
    }


    fun setLanguageData(
        languages: java.util.ArrayList<ProfileLanguageData>,
        userID: String,
        languageID: String
    ) {
        if (!languages!!.isNullOrEmpty()) {
            rv_language.visibility = View.VISIBLE
            tv_view_more_language.visibility = View.VISIBLE
            layout_view_languages.visibility = View.GONE


            languagesAdapter = LanguagesAdapter(
                activity as MainActivity,
                language_list,
                object : LanguagesAdapter.OnItemClick {
                    override fun onClicled(position: Int, from: String, v: View) {
                        when (from) {

                        }
                    }
                },
                "profile"
            )
            rv_language.layoutManager = LinearLayoutManager(activity)
            rv_language.setHasFixedSize(true)
            rv_language.adapter = languagesAdapter
            languagesAdapter!!.notifyDataSetChanged()
            getLanguageList(userID, languageID)
        } else {
            rv_language.visibility = View.GONE
            tv_view_more_language.visibility = View.GONE
            layout_view_languages.visibility = View.VISIBLE
        }
    }


    private fun getLanguageList(userID: String, languageID: String) {

        relativeprogressBar.visibility = View.VISIBLE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", userID)
            jsonObject.put("languageID", languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        profileModel.profileApi(jsonArray.toString(), "List")
        profileModel.successProfile
            .observe(viewLifecycleOwner) { languagesPojo ->

                relativeprogressBar.visibility = View.GONE
                rv_language.visibility = View.VISIBLE
                ll_mainProfileList.visibility = View.VISIBLE


                if (languagesPojo != null && languagesPojo.isNotEmpty()) {
                    if (languagesPojo[0].status.equals("true", true)) {
                        language_list?.clear()
                        language_list?.addAll(languagesPojo!![0]!!.data)
                        languagesAdapter?.notifyDataSetChanged()

                    } else {

                        if (language_list!!.size == 0) {
                            rv_language.visibility = View.GONE
                        } else {
                            rv_language.visibility = View.VISIBLE
                        }
                    }
                } else {
                    relativeprogressBar.visibility = View.GONE
                    ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    ll_mainProfileList.visibility = View.GONE

                }
            }
    }


    private fun getPrivacy() {
        val privacyList: ArrayList<CreatePostPrivacyPojo> = ArrayList()
        privacyList.clear()
        // privacyList.add(CreatePostPrivacyPojo("Copy", R.drawable.popup_report_icon))
        privacyList.add(CreatePostPrivacyPojo("Share Via", R.drawable.popup_share_via_icon))
        openBottomSheet(privacyList)
    }

    private fun openBottomSheet(data: ArrayList<CreatePostPrivacyPojo>) {

        val bottomSheet = PrivacyBottomSheetFragment()
        val bundle = Bundle()
        bundle.putSerializable("data", data as Serializable)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")

    }


    override fun setPrivacy(data: CreatePostPrivacyPojo, position: Int) {
        imgUrl = RestClient.image_base_url_users + userData?.userProfilePicture
        readPermission()
    }

    private fun readPermission() {

        try {
            if (ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                (this@ProfileFragment).requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    153
                )

            } else {
                writePermission()
            }

        } catch (e: ActivityNotFoundException) {
            Log.e("call", "Call failed", e)
        }

    }

    private fun writePermission() {

        try {
            if (ActivityCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                (this@ProfileFragment).requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    152
                )

            } else {
                mActivity!!.runOnUiThread {
                    DownloadImage(
                        imgUrl,
                        MyUtils.createFileName(Date(), "").toString(),
                        false,
                        mActivity!!,
                        userData?.userID!!
                    ).execute()
                }
            }

        } catch (e: ActivityNotFoundException) {
            Log.e("call", "Call failed", e)
        }

    }

    fun onLanguageUpdate() {
        language_list?.clear()
        language_list?.addAll(userData!!.languages)
        languagesAdapter?.notifyDataSetChanged()
    }


}
