package com.nxplayr.fsl.ui.fragments.setting.view


import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.SignupPojo
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.ui.fragments.setting.adapter.NotificationSettingViewPagerAdapter
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_notification_setting.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject


class NotificationSettingFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity:Activity?=null
    var adapter: NotificationSettingViewPagerAdapter? = null
    var sessionManager:SessionManager?=null
    var upnsTag = "Anyone"
    var upnsChat="On"
    var upnsFriendRequest= "On"
    var upnsFollowRequest= "On"
    var upnsComment="Anyone"
    var upnsLike="Anyone"
    var upnsNotification= "Yes"
    var userData: SignupData?=null
    private lateinit var  loginModel: SignupModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_notification_setting, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity=context as Activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager= SessionManager(mActivity!!)
        if(sessionManager?.get_Authenticate_User()!=null)
        {
            userData=sessionManager?.get_Authenticate_User()!!
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        menuToolbarItem.visibility=View.VISIBLE
        menuToolbarItem.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        menuToolbarItem.text = "Save"

        tvToolbarTitle.setText(R.string.notification)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        setData()

        ll_from_anyone.setOnClickListener(this)
        menuToolbarItem.setOnClickListener(this)
        ll_from_connection.setOnClickListener(this)
        ll_off.setOnClickListener(this)
        ll_comment.setOnClickListener(this)
        ll_comment_from_connection.setOnClickListener(this)
        ll_comment_off.setOnClickListener(this)
        ll_mention_fromanyone.setOnClickListener(this)
        ll_mention_fromconnecction.setOnClickListener(this)
        ll_mention_off.setOnClickListener(this)
        ll_chat_off.setOnClickListener(this)
        ll_chat_on.setOnClickListener(this)
        ll_req_off.setOnClickListener(this)
        ll_req_on.setOnClickListener(this)
        ll_follow_off.setOnClickListener(this)
        ll_follow_on.setOnClickListener(this)

    }

    private fun setData() {
        if(!userData?.pushNotification.isNullOrEmpty())
        {
            upnsTag =userData?.pushNotification!![0].upnsTag!!
            upnsChat=userData?.pushNotification!![0].upnsChat!!
            upnsFriendRequest= userData?.pushNotification!![0].upnsFriendRequest!!
            upnsFollowRequest=userData?.pushNotification!![0].upnsFollowRequest!!
            upnsComment=userData?.pushNotification!![0].upnsComment!!
            upnsLike=userData?.pushNotification!![0].upnsLike!!
            upnsNotification= userData?.pushNotification!![0].upnsNotification!!

            switch_follow_unfollow.isChecked = !userData?.pushNotification!![0].upnsNotification.isNullOrEmpty() && userData?.pushNotification!![0].upnsNotification!!.toString().contains("Yes")
        }

        when(upnsChat){
            "Off"->{
                img_chat_on.visibility=View.GONE
                img_chat_off.visibility=View.VISIBLE
            }
            "On"->{
                img_chat_on.visibility=View.VISIBLE
                img_chat_off.visibility=View.GONE
            }
        }
        when(upnsNotification){
            "Yes"->{
                switch_follow_unfollow.isChecked=true
            }
            "No"->{
                switch_follow_unfollow.isChecked=false
            }
        }
        when(upnsFriendRequest){
            "Off"->{
                img_req_off.visibility=View.VISIBLE
                img_req_on.visibility=View.GONE
            }
            "On"->{
                img_req_off.visibility=View.VISIBLE
                img_req_on.visibility=View.GONE
            }
        }
        when(upnsFollowRequest){
            "Off"->{
                img_follow_off.visibility=View.VISIBLE
                img_follow_on.visibility=View.GONE
            }
            "On"->{
                img_follow_off.visibility=View.GONE
                img_follow_on.visibility=View.VISIBLE

            }
        }
        when(upnsTag){
            "Anyone"->{
                img_mention_fromanyone.visibility=View.VISIBLE
                img_mention_fromconnecction.visibility=View.GONE
                img_mention_off.visibility=View.GONE
            }
            "Connection"->{
                img_mention_fromanyone.visibility=View.GONE
                img_mention_fromconnecction.visibility=View.VISIBLE
                img_mention_off.visibility=View.GONE

            }
            "Off"->{
                img_mention_fromanyone.visibility=View.GONE
                img_mention_fromconnecction.visibility=View.GONE
                img_mention_off.visibility=View.VISIBLE
            }
        }
        when(upnsComment){
            "Anyone"->{
                img_comment_from_anyone.visibility=View.VISIBLE
                img_comment_from_connection.visibility=View.GONE
                img_comment_off.visibility=View.GONE
            }
            "Connection"->{
                img_comment_from_anyone.visibility=View.GONE
                img_comment_from_connection.visibility=View.VISIBLE
                img_comment_off.visibility=View.GONE
            }
            "Off"->{
                img_comment_from_anyone.visibility=View.GONE
                img_comment_from_connection.visibility=View.GONE
                img_comment_off.visibility=View.VISIBLE

            }
        }
        when(upnsLike){
            "Anyone"->{
                img_from_anyone.visibility=View.VISIBLE
                img_from_connection.visibility=View.GONE
                img_off.visibility=View.GONE
            }
            "Connection"->{
                img_from_anyone.visibility=View.GONE
                img_from_connection.visibility=View.VISIBLE
                img_off.visibility=View.GONE

            }
            "Off"->{
                img_from_anyone.visibility=View.GONE
                img_from_connection.visibility=View.GONE
                img_off.visibility=View.VISIBLE
            }
        }


    }

    private fun setupViewModel() {
        loginModel = ViewModelProvider(this@NotificationSettingFragment).get(SignupModel::class.java)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_from_anyone -> {
                upnsLike="Anyone"
                img_from_anyone.visibility=View.VISIBLE
                img_from_connection.visibility=View.GONE
                img_off.visibility=View.GONE


            }
            R.id.ll_from_connection -> {
                upnsLike="Connection"
                img_from_anyone.visibility=View.GONE
                img_from_connection.visibility=View.VISIBLE
                img_off.visibility=View.GONE

            }
            R.id.ll_off -> {
                upnsLike="Off"
                img_from_anyone.visibility=View.GONE
                img_from_connection.visibility=View.GONE
                img_off.visibility=View.VISIBLE
            }

            R.id.ll_comment -> {
                upnsComment="Anyone"
                img_comment_from_anyone.visibility=View.VISIBLE
                img_comment_from_connection.visibility=View.GONE
                img_comment_off.visibility=View.GONE

            }

            R.id.ll_comment_from_connection -> {
                upnsComment="Connection"
                img_comment_from_anyone.visibility=View.GONE
                img_comment_from_connection.visibility=View.VISIBLE
                img_comment_off.visibility=View.GONE
            }

            R.id.ll_comment_off -> {
                upnsComment="Off"
                img_comment_from_anyone.visibility=View.GONE
                img_comment_from_connection.visibility=View.GONE
                img_comment_off.visibility=View.VISIBLE
            }

            R.id.ll_mention_fromanyone -> {
                upnsTag="Anyone"
                img_mention_fromanyone.visibility=View.VISIBLE
                img_mention_fromconnecction.visibility=View.GONE
                img_mention_off.visibility=View.GONE
            }

            R.id.ll_mention_fromconnecction -> {
                upnsTag="Connection"
                img_mention_fromanyone.visibility=View.GONE
                img_mention_fromconnecction.visibility=View.VISIBLE
                img_mention_off.visibility=View.GONE

            }

            R.id.ll_mention_off -> {
                upnsTag="Off"
                img_mention_fromanyone.visibility=View.GONE
                img_mention_fromconnecction.visibility=View.GONE
                img_mention_off.visibility=View.VISIBLE
            }

            R.id.ll_chat_off -> {
                upnsChat="Off"
                img_chat_on.visibility=View.GONE
                img_chat_off.visibility=View.VISIBLE
            }

            R.id.ll_chat_on -> {
                upnsChat="On"
                img_chat_on.visibility=View.VISIBLE
                img_chat_off.visibility=View.GONE
            }

            R.id.ll_req_off -> {
                upnsFollowRequest="Off"
                img_req_off.visibility=View.VISIBLE
                img_req_on.visibility=View.GONE
            }

            R.id.ll_req_on -> {
                upnsFollowRequest="On"
                img_req_off.visibility=View.VISIBLE
                img_req_on.visibility=View.GONE

            }

            R.id.ll_follow_off -> {
                upnsFollowRequest="Off"
                img_follow_off.visibility=View.VISIBLE
                img_follow_on.visibility=View.GONE
            }

            R.id.ll_follow_on -> {
                upnsFollowRequest="On"
                img_follow_off.visibility=View.GONE
                img_follow_on.visibility=View.VISIBLE
            }

            R.id.menuToolbarItem -> {
                updatePushNotification()
            }

        }
    }



    fun updatePushNotification() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("upnsLike", upnsLike)
            jsonObject.put("upnsTag", upnsTag)

            if (switch_follow_unfollow.isChecked)
                jsonObject.put("upnsNotification", "Yes")
            else
                jsonObject.put("upnsNotification", "No")

            jsonObject.put("upnsFollowRequest", upnsFollowRequest)
            jsonObject.put("upnsFriendRequest", upnsFriendRequest)
            jsonObject.put("upnsComment", upnsComment)
            jsonObject.put("upnsChat", upnsChat)

            jsonObject.put("languageID", "1")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        loginModel.userRegistration(mActivity!!, false, jsonArray.toString(), "update_PushNotification")
            .observe(
                this@NotificationSettingFragment,
                { loginPojo ->
                    if (loginPojo != null && loginPojo.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()

                        if (loginPojo[0].status.equals("true", true)) {

                            StoreSessionManager(loginPojo[0].data[0])
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo[0].message,
                                ll_mainNotification
                            )
                        } else {
                            if (activity != null && activity is MainActivity)
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    loginPojo[0].message,
                                    ll_mainNotification
                                )
                        }

                    } else {
                        if (activity != null && activity is MainActivity) {
                            (activity as MainActivity).errorMethod()
                            MyUtils.dismissProgressDialog()

                        }

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

}
