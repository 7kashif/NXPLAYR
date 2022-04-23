package com.nxplayr.fsl.ui.activity.main.view

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.application.MyApplication
import com.nxplayr.fsl.application.USER_DEFAULT_PASSWORD
import com.nxplayr.fsl.base.BaseActivity
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.fragment.*
import com.nxplayr.fsl.ui.activity.main.managers.NavigationHost
import com.nxplayr.fsl.ui.activity.main.managers.NotifyAlbumInterface
import com.nxplayr.fsl.ui.activity.main.managers.NotifyInterface
import com.nxplayr.fsl.ui.activity.main.viewmodel.ParallelNetworkCallsViewModel
import com.nxplayr.fsl.ui.activity.onboarding.view.SignInActivity
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.ui.activity.chat.managers.DialogsManager
import com.nxplayr.fsl.ui.activity.chat.ui.activity.ChatActivity
import com.nxplayr.fsl.ui.activity.chat.ui.activity.DialogsActivity
import com.nxplayr.fsl.ui.activity.chat.utils.SharedPrefsHelper
import com.nxplayr.fsl.ui.activity.chat.utils.chat.ChatHelper
import com.nxplayr.fsl.ui.fragments.explorepost.view.ExploreVideoDetailFragment
import com.nxplayr.fsl.ui.fragments.feed.viewmodel.CreatePostModel
import com.nxplayr.fsl.ui.fragments.invite.view.InviteMainFragment
import com.nxplayr.fsl.ui.fragments.main.view.BusinessMainFragment
import com.nxplayr.fsl.ui.fragments.main.view.HomeMainFragment
import com.nxplayr.fsl.ui.fragments.ownprofile.view.ProfileMainFragment
import com.nxplayr.fsl.ui.fragments.userconnection.view.AddConnectionsFragment
import com.nxplayr.fsl.ui.fragments.usereducation.view.EducationFragment
import com.nxplayr.fsl.ui.fragments.userhashtag.view.AddHashtagsFragment
import com.nxplayr.fsl.ui.fragments.userhashtag.view.HashtagsFragment
import com.nxplayr.fsl.ui.fragments.userhobbies.view.AddHobbiesFragment
import com.nxplayr.fsl.ui.fragments.userhobbies.view.HobbiesListFragment
import com.nxplayr.fsl.ui.fragments.userlanguage.view.AddLanguageFragment
import com.nxplayr.fsl.ui.fragments.userprofile.view.AddEducationFragment
import com.nxplayr.fsl.ui.fragments.userprofile.view.ProfileFragment
import com.nxplayr.fsl.ui.fragments.userprofile.view.WorkExperienceFragment
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.view.AddSkillsEndorsementsFragment
import com.nxplayr.fsl.ui.fragments.userskillsendorsment.view.SkillsEndorsementsFragment
import com.nxplayr.fsl.util.Constant
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.quickblox.auth.session.QBSettings
import com.quickblox.chat.QBRestChatService
import com.quickblox.chat.model.QBChatDialog
import com.quickblox.core.QBEntityCallback
import com.quickblox.core.exception.QBResponseException
import com.quickblox.core.exception.QBRuntimeException
import com.quickblox.core.helper.StringifyArrayList
import com.quickblox.messages.QBPushNotifications
import com.quickblox.messages.model.QBEnvironment
import com.quickblox.messages.model.QBNotificationChannel
import com.quickblox.messages.model.QBSubscription
import com.quickblox.users.QBUsers
import com.quickblox.users.model.QBUser
import kotlinx.android.synthetic.main.activity_create_post_two.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_comment_like_list.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity :  BaseActivity(), NotifyInterface, NavigationHost, NotifyAlbumInterface, AddEmployementFragment.EmployementUpdateListener, AddEducationFragment.EducationUpdateListener,
    AddLanguageFragment.LanguageUpdateListener, AddHashtagsFragment.HashtagsUpdateListener, AddHobbiesFragment.HobbiesUpdateListener, AddSkillsEndorsementsFragment.SkillsUpdateListener {


    private var doubleBackToExitPressedOnce = false
    var pushType = ""
    private var snackBarParent: View? = null
    var selectModeType: Int = 0
    var datumList: ArrayList<CreatePostPhotoPojo> ?= null
    var datumList1: ArrayList<UploadImagePojo>? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private var currentUser: QBUser? = null
    private lateinit var  viewModel: ParallelNetworkCallsViewModel
    private lateinit var  createPostModel: CreatePostModel
    private lateinit var  sendProductViewDone: SignupModel


    companion object {
        private const val REQUEST_SELECT_PEOPLE = 174
        private const val REQUEST_DIALOG_ID_FOR_UPDATE = 165
        private const val PLAY_SERVICES_REQUEST_CODE = 9000

    }

    private val mYourBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // now you can call all your fragments method here
            if (intent.hasExtra("from"))
                when (intent.getStringExtra("from")) {
                    "Text", "Video", "Place", "Link", "Document" -> {
                        // if (getCurrentFragment() is HomeMainFragment) {
                        datumList = ArrayList()
                        (getCurrentFragment() as HomeMainFragment).getUpdated(intent.getStringExtra("from")!!, datumList!!, "", "")
                        /*}else{
                            navigateTo(HomeMainFragment(),HomeMainFragment::class.simpleName!!,true)
                        }*/
                    }
                    "Photo" -> {
                        if (intent.hasExtra("datumList")) {
                            if (getCurrentFragment() is HomeMainFragment) {
                                datumList = ArrayList()
                                datumList?.clear()!!
                                datumList?.addAll((intent.getSerializableExtra("datumList") as ArrayList<CreatePostPhotoPojo>))
                                if (!datumList.isNullOrEmpty()) {
                                    createPost(intent.getStringExtra("from")!!, datumList!!, intent.getStringExtra("postDescription")!!, intent.getStringExtra("postPrivacyType")!!, intent.getStringExtra("Location"), intent.getStringExtra("latitude"), intent.getStringExtra("longitude"),intent.getStringExtra("tag"),intent.getStringExtra("connectionTypeIDs"))
                                }
                            }else{
                                datumList = ArrayList()
                                datumList?.clear()
                                datumList?.addAll((intent.getSerializableExtra("datumList") as ArrayList<CreatePostPhotoPojo>))
                                navigateTo(HomeMainFragment(),HomeMainFragment::class.simpleName!!,true)
                                if (!datumList.isNullOrEmpty()) {
                                    createPost(intent.getStringExtra("from")!!, datumList!!, intent.getStringExtra("postDescription")!!, intent.getStringExtra("postPrivacyType")!!, intent.getStringExtra("Location"), intent.getStringExtra("latitude"), intent.getStringExtra("longitude"), intent.getStringExtra("tag"),intent.getStringExtra("connectionTypeIDs"))

                                }
                            }
                        }
                    }
                    "Connection"->{
                        navigateTo(AddConnectionsFragment(),AddConnectionsFragment::class.simpleName!!,true)
                    }
                    "Invite"->{
                        navigateTo(InviteMainFragment(),InviteMainFragment::class.simpleName!!,true)
                    }
                    "MyExploreVideo"->{
                        if (intent.hasExtra("datumList")) {
                            if (getCurrentFragment() is ProfileMainFragment) {
                                datumList = ArrayList()
                                datumList?.clear()!!
                                datumList?.addAll((intent.getSerializableExtra("datumList")!! as ArrayList<CreatePostPhotoPojo>))
                                if (!datumList.isNullOrEmpty())
                                {
                                    (getCurrentFragment() as ProfileMainFragment).createPost(intent.getStringExtra("from")!!, datumList!!, intent.getStringExtra("postDescription")!!, intent.getStringExtra("postPrivacyType")!!, intent.getStringExtra("Location"), intent.getStringExtra("latitude"), intent.getStringExtra("longitude"),intent.getStringExtra("tag"),intent.getSerializableExtra("videoListThumnail") as ArrayList<CreatePostPhotoPojo>,intent.getStringExtra("radioText"),intent.getStringExtra("connectionTypeIDs"))
                                }
                            }else{
                                datumList = ArrayList()
                                datumList?.clear()!!
                                datumList?.addAll((intent.getSerializableExtra("datumList")!! as ArrayList<CreatePostPhotoPojo>))
                                navigateTo(HomeMainFragment(),HomeMainFragment::class.simpleName!!,true)
                                if (!datumList.isNullOrEmpty()) {
                                    (getCurrentFragment() as ProfileMainFragment). createPost(intent.getStringExtra("from")!!, datumList!!, intent.getStringExtra("postDescription")!!, intent.getStringExtra("postPrivacyType")!!, intent.getStringExtra("Location"), intent.getStringExtra("latitude"), intent.getStringExtra("longitude"), intent.getStringExtra("tag"),intent.getSerializableExtra("videoListThumnail") as ArrayList<CreatePostPhotoPojo>,intent.getStringExtra("radioText"),intent.getStringExtra("connectionTypeIDs"))

                                }
                            }
                        }
                    }
                    "MyExploreVideoEdit"->{
                        var exploreDetailFragment = ExploreVideoDetailFragment()
                        Bundle().apply {
                            putInt("pos", intent.getIntExtra("pos",0))
                            putSerializable("explore_video_list", intent.getSerializableExtra("explore_video_list"))
                            putString("postType",intent.getStringExtra("postType"))
                            exploreDetailFragment.arguments = this

                        }
                        navigateTo(exploreDetailFragment,exploreDetailFragment::class.java.name,true)
                    }
                    else -> {
                        if (getCurrentFragment() is HomeMainFragment) {
                            datumList = ArrayList()
                            (getCurrentFragment() as HomeMainFragment).getUpdated(intent.getStringExtra("from")!!, datumList!!, "", "")
                        }
                    }
                }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val actionBar = supportActionBar
        actionBar?.elevation = 0F
        LocalBroadcastManager.getInstance(this@MainActivity)
            .registerReceiver(mYourBroadcastReceiver, IntentFilter("CreatePost"))

        sessionManager = SessionManager(this@MainActivity)

        if(sessionManager?.get_Authenticate_User()!=null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (intent != null && intent.hasExtra("Push"))
            pushType = intent.getStringExtra("Push")!!

        if (intent != null)
            selectModeType = intent.getIntExtra("selectModeType", 0)
        Log.e("selectMode2", selectModeType.toString())
        updatePushToken()

        setupViewModel()
        setupUI()



    }

    private fun setupUI() {
        if(userData!=null && !userData?.apputypeID.isNullOrEmpty())
        {
            selectModeType = userData?.apputypeID!!.toInt()
        }
        if (userData != null && userData?.userQBoxID.isNullOrEmpty()) {
            //Register User
            quickBlockChatRegistration()
        } else if (userData != null && !userData?.userQBoxID.isNullOrEmpty()) {
            //Login into QuickBlock
//            if (MyUtils.isLoginForQuickBlock)
            quickBlockChatLogin()
        } else {
            //Register User
            quickBlockChatRegistration()
        }
        when(selectModeType){
            1->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, HomeMainFragment(), HomeMainFragment::class.java.name)
                    .addToBackStack(null)
                    .commit()
            }
            2->{
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, BusinessMainFragment(), BusinessMainFragment::class.java.name)
                    .addToBackStack(null)
                    .commit()

            }
        }
        addViewSnackBar()
    }

    private fun setupViewModel() {
         viewModel = ViewModelProvider(this@MainActivity).get(ParallelNetworkCallsViewModel::class.java)
         createPostModel = ViewModelProvider(this@MainActivity).get(CreatePostModel::class.java)
         sendProductViewDone = ViewModelProvider(this@MainActivity).get(SignupModel::class.java)
    }


    fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.container)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (getCurrentFragment() is HomeMainFragment) {
            getCurrentFragment()!!.onActivityResult(requestCode, resultCode, data)
        }

    }

    fun logOut() {

        sessionManager!!.clear_login_session()
        if (sessionManager?.getSelectedLanguage() == null)
            sessionManager?.setSelectedLanguage(
                LanguageListData(
                    "20180213170741.png",
                    "1",
                    "English",
                    "en"
                )
            )
        MyUtils.startActivity(
            this@MainActivity,
            SignInActivity::class.java,
            true
        )
    }

    fun showexit() {

        if (doubleBackToExitPressedOnce) {

            setResult(Activity.RESULT_CANCELED)
            finishAffinity()
            return
        }

        doubleBackToExitPressedOnce = true

        showSnackBar("To exit, press back again.")

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 3000)

    }

    override fun onBackPressed() {
        if (selectModeType == 1) {
            if (getCurrentFragment() == null) {
                super.onBackPressed()

            } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawers()
            } else if (getCurrentFragment() is HomeMainFragment) {
                showexit()
            } else if (getCurrentFragment() is BusinessMainFragment) {
                showexit()
            } else if (getCurrentFragment() is ExploreVideoDetailFragment) {
                var homeMainFragment=HomeMainFragment()
                Bundle().apply {
                    putString("fromBack","exploreVideoDetailFragment")
                    homeMainFragment.arguments=this
                }
                navigateTo(homeMainFragment,homeMainFragment::class.java.name,true)
            } else {
                if (supportFragmentManager.backStackEntryCount >= 1) {

                    val f = supportFragmentManager.findFragmentById(R.id.container)
                    if (supportFragmentManager.backStackEntryCount == 1) {
                        if (f != null && f is HomeMainFragment) {
                            showexit()
                        } else {
                            super.onBackPressed()
                        }

                    } else
                        supportFragmentManager.popBackStack()
                } else {
                    showexit()
                }
            }

        }
        else if (selectModeType == 2) {
            if (getCurrentFragment() == null) {
                super.onBackPressed()

            } else if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.closeDrawers()
            } else if (getCurrentFragment() is ExploreVideoDetailFragment) {
                navigateTo(HomeMainFragment(), HomeMainFragment::class.simpleName!!, true)

            }else if (getCurrentFragment() is BusinessMainFragment) {
                showexit()
            }  else {
                if (supportFragmentManager.backStackEntryCount >= 1) {

                    val f = supportFragmentManager.findFragmentById(R.id.container)
                    if (supportFragmentManager.backStackEntryCount == 1) {
                        /*if (f != null && f is MarketPlaceMainFragment) {
                            showexit()
                        } else {*/
                            super.onBackPressed()
                       // }

                    } else
                        supportFragmentManager.popBackStack()
                } else {
                    showexit()
                }
            }
        }
    }

    fun showSnackBar(message: String) {
        if ((snackBarParent != null) and !isFinishing)
            Snackbar.make(this.snackBarParent!!, message, Snackbar.LENGTH_LONG).show()

    }

    fun errorMethod() {
        // relativeprogressBar.visibility = View.GONE
        try {
            if (!MyUtils.isInternetAvailable(MyApplication.instance)) {
                showSnackBar(resources.getString(R.string.error_common_network))
            } else {
                showSnackBar(resources.getString(R.string.error_crash_error_message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun addViewSnackBar() {
        snackBarParent = View(this)
        val layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 200)
        layoutParams.gravity = Gravity.BOTTOM
        snackBarParent!!.layoutParams = layoutParams
        container.addView(snackBarParent)
    }

    override fun navigateTo(fragment: Fragment, tag: String, addToBackstack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            .replace(R.id.container, fragment, tag)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }
        transaction.commitAllowingStateLoss()
//        transaction.commitAllowingStateLoss()
    }

    override fun navigateTo(fragment: Fragment, bundle: Bundle, tag: String, addToBackstack: Boolean) {

        fragment.arguments = bundle

        val transaction = supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            .replace(R.id.container, fragment, tag)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
//        transaction.commitAllowingStateLoss()
    }

    override fun navigateToBusiness(fragment: Fragment, string: String, addToBackstack: Boolean) {
        val transaction = supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, string)

        if (addToBackstack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    fun StoreSessionManager(uesedata: SignupData?) {

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

    override fun onHashtagsUpdate() {
        val setHashtagList =
            supportFragmentManager.findFragmentByTag(HashtagsFragment::class.java.name) as HashtagsFragment?
        if (setHashtagList != null) {
//            setMeetingSchedule.updateAddresss()
        }
    }

    override fun onHobbiesUpdate() {
        val setHobbiesList =
            supportFragmentManager.findFragmentByTag(HobbiesListFragment::class.java.name) as HobbiesListFragment?
        if (setHobbiesList != null) {
            setHobbiesList.updateHobbies()
        }
    }

    override fun onSkillsUpdate() {
        val setSkillsList =
            supportFragmentManager.findFragmentByTag(SkillsEndorsementsFragment::class.java.name) as SkillsEndorsementsFragment?
        if (setSkillsList != null) {
//            setSkillsList.updateSkills()
        }
    }

    override fun onEmployementUpdate() {
        val setEmployementList =
            supportFragmentManager.findFragmentByTag(WorkExperienceFragment::class.java.name) as WorkExperienceFragment?
        if (setEmployementList != null) {
//            setEmployementList.updateEducationList()
        }
    }

    override fun onEducationUpdate() {
        val setEducationtList =
            supportFragmentManager.findFragmentByTag(EducationFragment::class.java.name) as EducationFragment?
        if (setEducationtList != null) {
//            setEducationtList.updateEducationList()
        }
    }

    override fun onLanguageUpdate() {
        val setLanguageData =
            supportFragmentManager.findFragmentByTag(ProfileFragment::class.java.name) as ProfileFragment?
        if (setLanguageData != null) {
            setLanguageData.onLanguageUpdate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@MainActivity)
            .unregisterReceiver(mYourBroadcastReceiver)
    }

    fun createPost(from: String, datumList: ArrayList<CreatePostPhotoPojo>, stringExtraDes: String, stringExtraPrivcy: String, Location: String?, latitude: String?, longitude: String?, tag: String?,connectionTypeIDs:String?) {
        if (getCurrentFragment() is HomeMainFragment) {
            (getCurrentFragment() as HomeMainFragment).isPogress(true)

        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID!!)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        var isvalisf: LiveData<List<UploadImagePojo>>? = null

        isvalisf = viewModel.getUploadedList(this@MainActivity, "images", datumList, jsonArray.toString())
        isvalisf.observe(this@MainActivity, Observer {

            if (!it.isNullOrEmpty()) {
                if (datumList1.isNullOrEmpty()) {
                    datumList1 = ArrayList()
                    datumList1?.addAll(it)
                    Log.e("createPost2", "" + it)
                    MyTask1(from,datumList,
                        stringExtraDes,
                        stringExtraPrivcy,
                        Location,
                        latitude,
                        longitude, tag,connectionTypeIDs).cancel(true)
                    MyTask1(from, datumList, stringExtraDes, stringExtraPrivcy,Location,latitude,longitude,tag,connectionTypeIDs).execute(50)
                }


            }
        })


    }

    inner class MyTask1(var from: String, var datumList: ArrayList<CreatePostPhotoPojo>, var stringExtraDes: String, var stringExtraPrivcy: String, var location: String?, var latitude: String?, var longitude: String?, var tag: String?,var connectionTypeIDs:String?) : AsyncTask<Int?, Int?, String>() {
        var count = 1

        override fun doInBackground(vararg p0: Int?): String {

            count = 1
            while (count <= p0[0]!!) {
                try {
                    Thread.sleep(500)
                    publishProgress(count)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                count++

            }
            return "Task Completed."
        }

        override fun onPostExecute(result: String) {
            Log.e("result", result)
            if (result.equals("Task Completed.", false)) {

                Log.e("result", "result create")
                getCreatePost("images", datumList, stringExtraDes, stringExtraPrivcy,
                    location,latitude,longitude,tag,connectionTypeIDs)

            }


        }

        override fun onPreExecute() {
            Log.e("result", "Task Starting...")
            if (getCurrentFragment() is HomeMainFragment) {
                if (getCurrentFragment() is HomeMainFragment) {
                    (getCurrentFragment() as HomeMainFragment).isPogress(true)
                }
            }

        }

        override fun onProgressUpdate(vararg values: Int?) {
            Log.e("result", "Running")
            if (getCurrentFragment() is HomeMainFragment) {
                (getCurrentFragment() as HomeMainFragment).isPogress(true)
                (getCurrentFragment() as HomeMainFragment).progressBar.progress = values[0]!!

            }


        }
    }

    private fun getCreatePost(listFromPastActivity: String,
                              datumList: ArrayList<CreatePostPhotoPojo>,
                              stringExtraDes: String,
                              stringExtraPrivcy: String,
                              location: String?,
                              latitude: String?,
                              longitude: String?, tag: String?,connectionTypeIDs:String?

    ) {

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
            jsonObject.put("postDescription", Constant.encode(stringExtraDes))
            jsonObject.put("postLanguage","en")
            jsonObject.put("postCategory", "")
            jsonObject.put("connectiontypeIDs", connectionTypeIDs)
            jsonObject.put("postPrivacyType", stringExtraPrivcy)
            jsonObject.put("postUploadDate", formattedDate)
            jsonObject.put("postHashTags", tag)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        when (listFromPastActivity) {
            "images" -> {
                jsonObject.put("postLatitude", latitude)
                jsonObject.put("postLongitude", longitude)
                jsonObject.put("postLocation", location)
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
                                jsonObjectAlbummedia.put("albummediaFileSize", datumList[i].fileSize)
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

        }

        jsonArray.put(jsonObject)
        createPostModel.apiFunction(this@MainActivity, jsonArray.toString(),"")
            .observe(this@MainActivity,
                { response ->
                    if (!response.isNullOrEmpty()) {

                        if (response[0].status.equals("true", true)) {
                            if (getCurrentFragment() is HomeMainFragment) {
                                (getCurrentFragment() as HomeMainFragment).isPogress(false)
                                (getCurrentFragment() as HomeMainFragment).ll_uploading_progress.visibility = View.GONE
                                (getCurrentFragment() as HomeMainFragment).progressBar.progress = 0
                                datumList1?.clear()
                                (getCurrentFragment() as HomeMainFragment).getPostList(listFromPastActivity, response[0].data, stringExtraDes, stringExtraPrivcy)
                            }
                            MyTask1(listFromPastActivity,datumList,
                                stringExtraDes,
                                stringExtraPrivcy,
                                location,
                                latitude,
                                longitude, tag,connectionTypeIDs).cancel(true)


                        } else {
                            //No data and no internet
                            MyTask1(listFromPastActivity,datumList,
                                stringExtraDes,
                                stringExtraPrivcy,
                                location,
                                latitude,
                                longitude, tag,connectionTypeIDs).cancel(true)
                            if (MyUtils.isInternetAvailable(this@MainActivity)) {

                                if (!response[0].message.isNullOrEmpty()) {
                                    if (getCurrentFragment() is HomeMainFragment) {
                                        (getCurrentFragment() as HomeMainFragment).ll_uploading_progress.visibility = View.GONE
                                    }
                                    MyUtils.showSnackbar(
                                        this@MainActivity,
                                        response[0].message,
                                        mDrawerLayout
                                    )
                                }

                            } else {
                                ll_uploading_progress.visibility = View.GONE

                                MyUtils.showSnackbar(
                                    this@MainActivity,
                                    resources.getString(R.string.error_common_network),
                                    mDrawerLayout
                                )
                            }
                        }

                    } else {
                        MyTask1(listFromPastActivity,datumList,
                            stringExtraDes,
                            stringExtraPrivcy,
                            location,
                            latitude,
                            longitude, tag,connectionTypeIDs).cancel(true)
                        if (getCurrentFragment() is HomeMainFragment) {
                            (getCurrentFragment() as HomeMainFragment).ll_uploading_progress.visibility = View.GONE
                        }
                        if (MyUtils.isInternetAvailable(this@MainActivity)) {
                            MyUtils.showSnackbar(
                                this@MainActivity,
                                resources.getString(R.string.error_crash_error_message), mDrawerLayout
                            )
                        } else {
                            MyUtils.showSnackbar(
                                this@MainActivity,
                                resources.getString(R.string.error_common_network), mDrawerLayout
                            )
                        }
                    }
                })
    }

    fun setToolBar(toolbar: Toolbar?) {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun AlbumNotifyData(postId: String, userId: String, from: String, explore_video_list: ArrayList<CreatePostData?>?, postType: String) {
        val frag: HomeMainFragment? = supportFragmentManager.findFragmentByTag(
            HomeMainFragment::class.java.name
        ) as HomeMainFragment?

        if (frag != null) {

            frag.AlbumNotifyData(postId, userId, from,explore_video_list,postType)
        }
    }

    private fun quickBlockChatRegistration() {
        val userMobile = userData?.userMobile
        val userName = userData?.userFirstName+" "+userData?.userLastName
        val userEmail = userData?.userEmail
        val userPassword = USER_DEFAULT_PASSWORD

        val qbUser = QBUser()
        val userTags = StringifyArrayList<String>()
        userTags.add("UserTag")
        qbUser.login = userEmail
        qbUser.password = userPassword
        qbUser.fullName = userName
        qbUser.email = userEmail
        qbUser.tags = userTags
        qbUser.phone = userMobile
        try {
            QBUsers.signUp(qbUser).performAsync(object : QBEntityCallback<QBUser> {
                override fun onSuccess(qbUser: QBUser, bundle: Bundle?) {
                    userData?.userQBoxID = qbUser.id.toString()
                    StoreSessionManager(userData!!)

                    sessionManager?.isQBUser(true)
                    qbUser.password = userPassword
                    sessionManager?.saveQbUser(qbUser)
                    SharedPrefsHelper.saveQbUser(qbUser)
                    senQuickBlockIdToServer(qbUser.id,qbUser,userPassword)

                    Handler().postDelayed({
                        quickBlockChatLogin()
                    }, 500)
                }

                override fun onError(error: QBResponseException) {
                    quickBlockChatLogin()
                }
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        } catch (e: QBRuntimeException) {
            e.printStackTrace()
        } catch (e: QBResponseException) {
            e.printStackTrace()
        }
    }

    private fun senQuickBlockIdToServer(id: Int, qbUser: QBUser, userPassword: String) {
        if (sessionManager!!.isLoggedIn()) {
            userData = sessionManager?.get_Authenticate_User()
        }

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        try {
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("userQBoxID", id)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {

        }
        sendProductViewDone.userRegistration(this@MainActivity,false, jsonArray.toString(), "quickblockdetails")
            .observe(this@MainActivity,
                { sendViewResponse: List<SignupPojo>? ->
                    if (!sendViewResponse.isNullOrEmpty() && sendViewResponse[0].status.equals
                            ("true", true)
                    ) {
                        if (!sendViewResponse[0].data.isNullOrEmpty()) {
                            sessionManager?.clear_login_session()

                            sessionManager?.isQBUser(true)
                            qbUser.password = userPassword
                            sessionManager?.saveQbUser(qbUser)
                            SharedPrefsHelper.saveQbUser(qbUser)
                            StoreSessionManager(sendViewResponse[0].data[0])
                            FirebaseInstanceId.getInstance().instanceId
                                .addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        return@OnCompleteListener
                                    }
                                    val newtoken = task.result?.token
                                })
                        }
                    } else {

                    }
                })
    }

    fun quickBlockChatLogin() {
//        MyUtils.showProgress(this@MainActivity)

        var userEamil = ""
        userEamil = userData?.userEmail!!

        val userPassword = USER_DEFAULT_PASSWORD

        val qbUser = QBUser(userEamil, userPassword)
        QBUsers.signIn(qbUser).performAsync(object : QBEntityCallback<QBUser> {
            override fun onSuccess(qbUser: QBUser, bundle: Bundle?) {
                Log.d("QB User Login", "Login Success user can chat with designer " + qbUser.id)
                sessionManager?.isQBUser(true)
                qbUser.password = userPassword
                sessionManager?.saveQbUser(qbUser)
                SharedPrefsHelper.saveQbUser(qbUser)
                MyUtils.isLoginForQuickBlock = true

                if (userData?.userQBoxID.isNullOrEmpty())
                    senQuickBlockIdToServer(qbUser.id, qbUser,userPassword)

                FirebaseInstanceId.getInstance().instanceId
                    .addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            return@OnCompleteListener
                        }
                        val newtoken = task.result?.token
                        subscribeToPushNotifications(qbUser.id.toString(), newtoken)
                    })
                Handler().postDelayed({
                    // if (isNewUser)
                    updateQBProfile()
                }, 500)
            }

            override fun onError(e: QBResponseException) {
//                MyUtils.closeProgress()
                sessionManager?.isQBUser(false)
                Log.d("QB User Login", "Login failed user can not chat with designer " + qbUser.id)
            }
        })
    }

    fun subscribeToPushNotifications(registrationID: String, deviceId1: String?) {
        val subscription = QBSubscription(QBNotificationChannel.GCM)
        subscription.environment = QBEnvironment.PRODUCTION
        subscription.deviceUdid = deviceId1
        subscription.registrationID = deviceId1
        QBPushNotifications.createSubscription(subscription).performAsync(object :
            QBEntityCallback<ArrayList<QBSubscription>> {
            override fun onSuccess(p0: ArrayList<QBSubscription>?, p1: Bundle?) {
                QBSettings.getInstance().isEnablePushNotification
                if (deviceId1.isNullOrEmpty())
                    logoutREST()
            }

            override fun onError(p0: QBResponseException?) {
                if (deviceId1.isNullOrEmpty())
                    logoutREST()
            }
        })
    }

    fun updateQBProfile() {
        var user: QBUser?=null
        try {
            user = QBUser()
            user.id = sessionManager?.getQbUser()?.id
            user.fullName = userData?.userFirstName+" "+userData?.userLastName
            user.email = userData?.userEmail
            user.phone = userData?.userMobile
        } catch (e: Exception) {
        }
        var userCustomData = ""

        if (!(userData?.userFirstName!! + " " + userData?.userLastName).isNullOrEmpty()) {
            userCustomData = userCustomData + "profilename: " + userData?.userFirstName+" "+userData?.userLastName +
                    ", "
        }

        if (!userData?.userProfilePicture.isNullOrEmpty()) {
            userCustomData =
                userCustomData + "profilepicture: ${RestClient.image_base_url_users}" + userData?.userProfilePicture +
                        ", "
        }

        if (!userData?.userHomeCountryName.isNullOrEmpty()) {
            userCustomData = userCustomData + "countryname: " + userData?.userHomeCountryName +
                    ", "
        }

        user?.fullName = userCustomData

        /**
         * facebook id = profile picture
         * website id = country name
         * customData id = speciality + proffesion name
         * */
        QBUsers.updateUser(user).performAsync(object : QBEntityCallback<QBUser> {
            override fun onSuccess(usersByTags: QBUser, params: Bundle) {
//                MyUtils.closeProgress()
                Log.d("Success QB update", "Profile updated successfully")
            }

            override fun onError(e: QBResponseException) {
//                MyUtils.closeProgress()
                Log.d("Error while QB update", e.toString())
            }
        })
    }

    fun loginForQuickBlockChat(qbUser: QBUser, qbID: String) {
        MyUtils.showProgressDialog(this@MainActivity,"Please wait..")
        sessionManager?.saveQbUser(qbUser)
        SharedPrefsHelper.saveQbUser(qbUser)
        if (qbUser != null) {
            ChatHelper.loginToChat(qbUser, object : QBEntityCallback<Void> {
                override fun onSuccess(result: Void?, bundle: Bundle?) {
                    currentUser = ChatHelper.getCurrentUser()
                    MyUtils.isLoginForQuickBlockChat = true
                    createDialogGroup(java.lang.Integer.valueOf(qbID))
                    getQBUser(qbID, 0)
                    Log.d(
                        "QB User Chat Login", "Chat Login success user can chat with designer"
                                + qbUser.id
                    )
                }

                override fun onError(e: QBResponseException) {
                    MyUtils.dismissProgressDialog()
                    if (MyUtils.isInternetAvailable(this@MainActivity)) {
                    } else {
                        MyUtils.showSnackbar(
                            this@MainActivity,
                            resources.getString(R.string.error_common_network),mDrawerLayout
                        )
                    }
                    Log.d("Quick Block Login fail", "$e.toString()")
                }
            })
        }
    }

    fun getQBUser(userQuickBlockId: String, i: Int) {
        if (i == 1)
            MyUtils.showProgressDialog(this@MainActivity,"")

        val dialogsManager = DialogsManager()
        val userTags = StringifyArrayList<Int>()
        userTags.add(java.lang.Integer.valueOf(userQuickBlockId))
        QBUsers.getUsersByIDs(userTags, null)
            .performAsync(object : QBEntityCallback<java.util.ArrayList<QBUser>> {
                override fun onSuccess(usersByTags: java.util.ArrayList<QBUser>, params: Bundle) {

                    if (!usersByTags.isNullOrEmpty())
                        createDialog(usersByTags,"")
                    else MyUtils.showSnackbar(
                        this@MainActivity, "User not found for " +
                                "chat",mDrawerLayout
                    )
                }

                override fun onError(e: QBResponseException) {
                    MyUtils.dismissProgressDialog()
                    Log.d("Error while QB dialog", e.toString())
                }
            })
    }

    private fun createDialog(selectedUsers: ArrayList<QBUser>, chatName: String) {

        ChatHelper.createDialogWithSelectedUsers(selectedUsers,chatName,
            object : QBEntityCallback<QBChatDialog> {
                override fun onSuccess(dialog: QBChatDialog, args: Bundle) {
                    MyUtils.dismissProgressDialog()
                    selectedUsers.remove(ChatHelper.getCurrentUser())
                    ChatActivity.startForResult(this@MainActivity, REQUEST_DIALOG_ID_FOR_UPDATE, dialog, true)
                }

                override fun onError(e: QBResponseException) {
                    MyUtils.dismissProgressDialog()

                    Log.d("Error while QB dialog", e.toString())
                }
            }
        )
    }

    private fun createDialogGroup(selectedUsers: Int) {

        QBRestChatService.getChatDialogById("5e180a17a0eb470b95ed0d50")
            .performAsync(object :
                QBEntityCallback<QBChatDialog> {
                override fun onSuccess(p0: QBChatDialog?, p1: Bundle?) {

                    ChatActivity.startForResult(this@MainActivity, REQUEST_DIALOG_ID_FOR_UPDATE, p0!!, true)

                }

                override fun onError(p0: QBResponseException?) {

                }
            })
    }

    private fun logoutREST() {
        if (sessionManager != null)
            sessionManager?.clear_login_session()
        MyUtils.dismissProgressDialog()
        QBUsers.signOut().performAsync(null)
        val myIntent = Intent(this@MainActivity, SignInActivity::class.java)
        startActivity(myIntent)
        this@MainActivity.finishAffinity()
        (this@MainActivity).overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    private fun updatePushToken() {
        if(sessionManager?.isLoggedIn()!!)
        {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("TokenError", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }

                    // Get new Instance ID token
                    val token = task.result
                    var userdata = sessionManager!!.get_Authenticate_User()

                    val jsonArray = JSONArray()
                    val jsonObject = JSONObject()
                    try {

                        jsonObject.put("loginuserID", userdata.userID)
                        jsonObject.put("languageID", userdata.languageID)
                        jsonObject.put("userDeviceType", "Android")
                        jsonObject.put("userDeviceID", token)
                        jsonObject.put("apiType", RestClient.apiType)
                        jsonObject.put("apiVersion", RestClient.apiVersion)

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    jsonArray.put(jsonObject)
                    val loginModel = ViewModelProviders.of(this).get(SignupModel::class.java)
                    loginModel.userRegistration(this, false, jsonArray.toString(), "updateDeviceToken")
                        .observe(this,
                            Observer<List<SignupPojo>> { loginPojo ->
                                if (loginPojo != null && loginPojo.isNotEmpty()) {

                                    if (loginPojo[0].status.equals("true", true)) {

                                        StoreSessionManager(loginPojo[0].data.get(0))

                                    }
                                }
                            })
                })
        }
    }

    fun loginToChat(user: QBUser) {
        user.password = USER_DEFAULT_PASSWORD
        ChatHelper.loginToChat(user, object : QBEntityCallback<Void> {
            override fun onSuccess(void: Void?, bundle: Bundle?) {
                sessionManager?.saveQbUser(user)
                SharedPrefsHelper.saveQbUser(user)
                DialogsActivity.start(this@MainActivity)
                // finish()
            }

            override fun onError(e: QBResponseException) {
                showSnackBar(getString(R.string.login_chat_login_erro))
            }
        })
    }

    override fun notifyData(
        feedDatum: CreatePostData?,
        isDelete: Boolean,
        isDeleteComment: Boolean,
        postComment: String?,
        commentId:String?
    ) {
        val frag5: HomeMainFragment? =
            supportFragmentManager.findFragmentByTag(HomeMainFragment::class.java.getName()) as HomeMainFragment?
        if (frag5 != null)
            frag5.notifyData(feedDatum, isDelete, isDeleteComment, postComment,commentId)
      }

}