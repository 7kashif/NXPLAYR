package com.nxplayr.fsl.ui.fragments.setting.view


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.ui.fragments.setting.adapter.NotificationSettingViewPagerAdapter
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_notification_setting.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject


class NotificationSettingFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: Activity? = null
    var adapter: NotificationSettingViewPagerAdapter? = null
    var sessionManager: SessionManager? = null
    var upnsTag = "Anyone"
    var upnsChat = "On"
    var upnsFriendRequest = "On"
    var upnsFollowRequest = "On"
    var upnsComment = "Anyone"
    var upnsLike = "Anyone"
    var upnsNotification = "Yes"
    var userData: SignupData? = null
    private lateinit var loginModel: SignupModelV2

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
        mActivity = context as Activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()!!
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        menuToolbarItem.visibility = View.VISIBLE
        menuToolbarItem.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        menuToolbarItem.text = "Save"
        menuToolbarItem.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))

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

        switch_follow_unfollow.setOnCheckedChangeListener { _, isChecked ->
            pushNotification(isChecked)
        }
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngNotification.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngNotification
            if (!sessionManager?.LanguageLabel?.lngAllowPushNotifications.isNullOrEmpty())
                tv_allow_notify.text = sessionManager?.LanguageLabel?.lngAllowPushNotifications
            if (!sessionManager?.LanguageLabel?.lngPostLikes.isNullOrEmpty())
                tv_likes.text = sessionManager?.LanguageLabel?.lngPostLikes
            if (!sessionManager?.LanguageLabel?.lngFromAnyone.isNullOrEmpty()) {
                tv_from_anyone.text = sessionManager?.LanguageLabel?.lngFromAnyone
                tv_comment_from_anyone.text = sessionManager?.LanguageLabel?.lngFromAnyone
            }
            if (!sessionManager?.LanguageLabel?.lngFromConnections.isNullOrEmpty()) {
                tv_from_connection.text = sessionManager?.LanguageLabel?.lngFromConnections
                tv_comment_from_connection.text = sessionManager?.LanguageLabel?.lngFromConnections
            }
            if (!sessionManager?.LanguageLabel?.lngFromOff.isNullOrEmpty()) {
                tv_off.text = sessionManager?.LanguageLabel?.lngFromOff
                tv_comment_off.text = sessionManager?.LanguageLabel?.lngFromOff
                tv_chat_off.text = sessionManager?.LanguageLabel?.lngFromOff
                tv_req_off.text = sessionManager?.LanguageLabel?.lngFromOff
                tv_follow_off.text = sessionManager?.LanguageLabel?.lngFromOff
            }
            if (!sessionManager?.LanguageLabel?.lngFromOn.isNullOrEmpty()) {
                tv_chat_on.text = sessionManager?.LanguageLabel?.lngFromOn
                tv_req_on.text = sessionManager?.LanguageLabel?.lngFromOn
                tv_follow_on.text = sessionManager?.LanguageLabel?.lngFromOn
            }
            if (!sessionManager?.LanguageLabel?.lngPostComments.isNullOrEmpty())
                tv_comments.text = sessionManager?.LanguageLabel?.lngPostComments
            if (!sessionManager?.LanguageLabel?.lngConnectionRequests.isNullOrEmpty())
                connect_req.text = sessionManager?.LanguageLabel?.lngConnectionRequests
            if (!sessionManager?.LanguageLabel?.lngFollowers.isNullOrEmpty())
                tv_follow.text = sessionManager?.LanguageLabel?.lngFollowers
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                menuToolbarItem.text = sessionManager?.LanguageLabel?.lngSave
            if (!sessionManager?.LanguageLabel?.lngChatMessages.isNullOrEmpty())
                chat_msg.text = sessionManager?.LanguageLabel?.lngChatMessages
        }
    }

    private fun pushNotification(flag: Boolean) {
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (flag) {
                switch_follow_unfollow.isChecked = true
                main_push.visibility = View.VISIBLE
                if (!sessionManager?.LanguageLabel?.lngAllowPushNotificationsOnMessage.isNullOrEmpty())
                    tv_allow_notify_desc.text =
                        sessionManager?.LanguageLabel?.lngAllowPushNotificationsOnMessage
            } else {
                switch_follow_unfollow.isChecked = false
                main_push.visibility = View.GONE
                if (!sessionManager?.LanguageLabel?.lngAllowPushNotificationsOffMessage.isNullOrEmpty())
                    tv_allow_notify_desc.text =
                        sessionManager?.LanguageLabel?.lngAllowPushNotificationsOffMessage
            }
        }
    }

    private fun setData() {
        if (!userData?.pushNotification.isNullOrEmpty()) {
            upnsTag = userData?.pushNotification!![0].upnsTag!!
            upnsChat = userData?.pushNotification!![0].upnsChat!!
            upnsFriendRequest = userData?.pushNotification!![0].upnsFriendRequest!!
            upnsFollowRequest = userData?.pushNotification!![0].upnsFollowRequest!!
            upnsComment = userData?.pushNotification!![0].upnsComment!!
            upnsLike = userData?.pushNotification!![0].upnsLike!!
            upnsNotification = userData?.pushNotification!![0].upnsNotification!!

            switch_follow_unfollow.isChecked =
                !userData?.pushNotification!![0].upnsNotification.isNullOrEmpty() && userData?.pushNotification!![0].upnsNotification!!.toString()
                    .contains("Yes")
        }


        when (upnsNotification) {
            "Yes" -> {
                pushNotification(true)
            }
            "No" -> {
                pushNotification(false)
            }
        }

        followRequest(upnsFollowRequest)
        connectRequest(upnsFriendRequest)
        chats(upnsChat)
        commentsAnyone(upnsComment)
        likeAnyone(upnsLike)
    }

    private fun followRequest(upnsFollowRequest: String) {
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            when (upnsFollowRequest) {
                "Off" -> {
                    img_follow_off.visibility = View.VISIBLE
                    img_follow_on.visibility = View.GONE
                    if (!sessionManager?.LanguageLabel?.lngFollowersFromOffMessage.isNullOrEmpty())
                        tv_follow_desc.text = sessionManager?.LanguageLabel?.lngFollowersFromOffMessage
                }
                "On" -> {
                    img_follow_off.visibility = View.GONE
                    img_follow_on.visibility = View.VISIBLE
                    if (!sessionManager?.LanguageLabel?.lngFollowersFromOnMessage.isNullOrEmpty())
                        tv_follow_desc.text = sessionManager?.LanguageLabel?.lngFollowersFromOnMessage
                }
            }
        }
    }

    private fun connectRequest(upnsFriendRequest: String) {
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            when (upnsFriendRequest) {
                "Off" -> {
                    img_req_off.visibility = View.VISIBLE
                    img_req_on.visibility = View.GONE
                    if (!sessionManager?.LanguageLabel?.lngConnectionRequestFromOffMessage.isNullOrEmpty())
                        tv_req_desc.text = sessionManager?.LanguageLabel?.lngConnectionRequestFromOffMessage
                }
                "On" -> {
                    img_req_off.visibility = View.GONE
                    img_req_on.visibility = View.VISIBLE
                    if (!sessionManager?.LanguageLabel?.lngConnectionRequestFromOnMessage.isNullOrEmpty())
                        tv_req_desc.text = sessionManager?.LanguageLabel?.lngConnectionRequestFromOnMessage
                }
            }
        }
    }

    private fun chats(upnsChat: String) {
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            when (upnsChat) {
                "Off" -> {
                    img_chat_on.visibility = View.GONE
                    img_chat_off.visibility = View.VISIBLE
                    if (!sessionManager?.LanguageLabel?.lngChatFromOffMessage.isNullOrEmpty())
                        tv_chats_desc.text = sessionManager?.LanguageLabel?.lngChatFromOffMessage
                }
                "On" -> {
                    img_chat_on.visibility = View.VISIBLE
                    img_chat_off.visibility = View.GONE
                    if (!sessionManager?.LanguageLabel?.lngChatFromOnMessage.isNullOrEmpty())
                        tv_chats_desc.text = sessionManager?.LanguageLabel?.lngChatFromOnMessage
                }
            }
        }
    }

    private fun commentsAnyone(upnsComment: String) {
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            when (upnsComment) {
                "Anyone" -> {
                    img_comment_from_anyone.visibility = View.VISIBLE
                    img_comment_from_connection.visibility = View.GONE
                    img_comment_off.visibility = View.GONE
                    if (!sessionManager?.LanguageLabel?.lngCommentsFromAnyoneMessage.isNullOrEmpty())
                        tv_comments_desc.text = sessionManager?.LanguageLabel?.lngCommentsFromAnyoneMessage
                }
                "Connection" -> {
                    img_comment_from_anyone.visibility = View.GONE
                    img_comment_from_connection.visibility = View.VISIBLE
                    img_comment_off.visibility = View.GONE
                    if (!sessionManager?.LanguageLabel?.lngCommentsFromConnectionMessage.isNullOrEmpty())
                        tv_comments_desc.text = sessionManager?.LanguageLabel?.lngCommentsFromConnectionMessage
                }
                "Off" -> {
                    img_comment_from_anyone.visibility = View.GONE
                    img_comment_from_connection.visibility = View.GONE
                    img_comment_off.visibility = View.VISIBLE
                    if (!sessionManager?.LanguageLabel?.lngCommentsFromOffMessage.isNullOrEmpty())
                        tv_comments_desc.text = sessionManager?.LanguageLabel?.lngCommentsFromOffMessage
                }
            }
        }
    }

    private fun likeAnyone(upnsLike: String) {
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            when (upnsLike) {
                "Anyone" -> {
                    img_from_anyone.visibility = View.VISIBLE
                    img_from_connection.visibility = View.GONE
                    img_off.visibility = View.GONE
                    if (!sessionManager?.LanguageLabel?.lngLikeFromAnyoneMessage.isNullOrEmpty())
                        tv_likes_desc.text = sessionManager?.LanguageLabel?.lngLikeFromAnyoneMessage
                }
                "Connection" -> {
                    img_from_anyone.visibility = View.GONE
                    img_from_connection.visibility = View.VISIBLE
                    img_off.visibility = View.GONE
                    if (!sessionManager?.LanguageLabel?.lngLikeFromConnectionMessage.isNullOrEmpty())
                        tv_likes_desc.text =
                            sessionManager?.LanguageLabel?.lngLikeFromConnectionMessage
                }
                "Off" -> {
                    img_from_anyone.visibility = View.GONE
                    img_from_connection.visibility = View.GONE
                    img_off.visibility = View.VISIBLE
                    if (!sessionManager?.LanguageLabel?.lngLikeFromOffMessage.isNullOrEmpty())
                        tv_likes_desc.text = sessionManager?.LanguageLabel?.lngLikeFromOffMessage
                }
            }
        }
    }

    private fun setupViewModel() {
        loginModel =
            ViewModelProvider(this@NotificationSettingFragment).get(SignupModelV2::class.java)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_from_anyone -> {
                upnsLike = "Anyone"
                likeAnyone(upnsLike)
            }
            R.id.ll_from_connection -> {
                upnsLike = "Connection"
                likeAnyone(upnsLike)
            }
            R.id.ll_off -> {
                upnsLike = "Off"
                likeAnyone(upnsLike)
            }

            R.id.ll_comment -> {
                upnsComment = "Anyone"
                commentsAnyone(upnsComment)
            }

            R.id.ll_comment_from_connection -> {
                upnsComment = "Connection"
                commentsAnyone(upnsComment)
            }

            R.id.ll_comment_off -> {
                upnsComment = "Off"
                commentsAnyone(upnsComment)
            }
            R.id.ll_chat_off -> {
                upnsChat = "Off"
                chats(upnsChat)
            }

            R.id.ll_chat_on -> {
                upnsChat = "On"
                chats(upnsChat)
            }

            R.id.ll_req_off -> {
                upnsFriendRequest = "Off"
                connectRequest(upnsFriendRequest)
            }

            R.id.ll_req_on -> {
                upnsFriendRequest = "On"
                connectRequest(upnsFriendRequest)
            }

            R.id.ll_follow_off -> {
                upnsFollowRequest = "Off"
                followRequest(upnsFollowRequest)
            }

            R.id.ll_follow_on -> {
                upnsFollowRequest = "On"
                followRequest(upnsFollowRequest)
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

        loginModel.updatePushNotification(jsonArray.toString())
        loginModel.updatePushNotification
            .observe(
                this@NotificationSettingFragment
            ) { loginPojo ->
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
            }

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
