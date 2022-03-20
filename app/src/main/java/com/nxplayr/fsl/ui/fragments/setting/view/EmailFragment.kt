package com.nxplayr.fsl.ui.fragments.setting.view


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.SignupPojo
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_education.*
import kotlinx.android.synthetic.main.fragment_email.*
import kotlinx.android.synthetic.main.fragment_privacy.*
import org.json.JSONArray
import org.json.JSONObject


class EmailFragment : Fragment(),CompoundButton.OnCheckedChangeListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var tabposition = 0
    private lateinit var  loginModel: SignupModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_email, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null) {
            tabposition = arguments!!.getInt("position", 0)
        }
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupViewModel()
        setupUI()

    }
    private fun setupViewModel() {
        loginModel = ViewModelProvider(this@EmailFragment).get(SignupModel::class.java)

    }

    private fun setupUI() {
        if ((!userData?.emailNotification.isNullOrEmpty()) || (!userData?.pushNotification.isNullOrEmpty()) || (!userData?.inAppSettings.isNullOrEmpty())) {
            setnotificationSettings()
        }

        switch_follow_unfollow.setOnCheckedChangeListener(this)
        switch_connectDisconnect.setOnCheckedChangeListener(this)
        switch_like.setOnCheckedChangeListener(this)
        switch_vote.setOnCheckedChangeListener(this)
        switch_comment.setOnCheckedChangeListener(this)

    }

    fun setnotificationSettings() {
        when (tabposition) {
            0 -> {
                if (!userData?.emailNotification.isNullOrEmpty()) {
                    switch_follow_unfollow.isChecked = !userData!!.emailNotification[0].uensFollowRequest.isNullOrEmpty() && userData!!.emailNotification[0].uensFollowRequest.toString().contains("Yes")
                    switch_connectDisconnect.isChecked = !userData!!.emailNotification[0].uensFriendRequest.isNullOrEmpty() && userData!!.emailNotification[0].uensFriendRequest.toString().contains("Yes")
                    switch_like.isChecked = !userData!!.emailNotification[0].uensLike.isNullOrEmpty() && userData!!.emailNotification[0].uensLike.toString().contains("Yes")
                    switch_vote.isChecked = !userData!!.emailNotification[0].uensVote.isNullOrEmpty() && userData!!.emailNotification[0].uensVote.toString().contains("Yes")
                    switch_comment.isChecked = !userData!!.emailNotification[0].uensComment.isNullOrEmpty() && userData!!.emailNotification[0].uensComment.toString().contains("Yes")
                }
            }
            1 -> {
                if (!userData?.pushNotification.isNullOrEmpty()) {
                    switch_follow_unfollow.isChecked = !userData!!.pushNotification[0].upnsFollowRequest.isNullOrEmpty() && userData!!.pushNotification[0].upnsFollowRequest.toString().contains("Yes")
                    switch_connectDisconnect.isChecked = !userData!!.pushNotification[0].upnsFriendRequest.isNullOrEmpty() && userData!!.pushNotification[0].upnsFriendRequest.toString().contains("Yes")
                    switch_like.isChecked = !userData!!.pushNotification[0].upnsLike.isNullOrEmpty() && userData!!.pushNotification[0].upnsLike.toString().contains("Yes")
                    switch_comment.isChecked = !userData!!.pushNotification[0].upnsComment.isNullOrEmpty() && userData!!.pushNotification[0].upnsComment.toString().contains("Yes")
                }
            }
            2 -> {
                if (!userData?.inAppSettings.isNullOrEmpty()) {
                    switch_follow_unfollow.isChecked = !userData!!.inAppSettings[0].usnsFollowRequest.isNullOrEmpty() && userData!!.inAppSettings[0].usnsFollowRequest.toString().contains("Yes")
                    switch_connectDisconnect.isChecked = !userData!!.inAppSettings[0].usnsFriendRequest.isNullOrEmpty() && userData!!.inAppSettings[0].usnsFriendRequest.toString().contains("Yes")
                    switch_like.isChecked = !userData!!.inAppSettings[0].usnsLike.isNullOrEmpty() && userData!!.inAppSettings[0].usnsLike.toString().contains("Yes")
                    switch_vote.isChecked = !userData!!.inAppSettings[0].usnsVote.isNullOrEmpty() && userData!!.inAppSettings[0].usnsVote.toString().contains("Yes")
                    switch_comment.isChecked = !userData!!.inAppSettings[0].usnsComment.isNullOrEmpty() && userData!!.inAppSettings[0].usnsComment.toString().contains("Yes")
                }
            }
        }
    }


    fun updateEmailNotification() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)

            if (switch_like.isChecked)
                jsonObject.put("uensLike", "Yes")
            else
                jsonObject.put("uensLike", "No")

            if (switch_vote.isChecked)
                jsonObject.put("uensVote", "Yes")
            else
                jsonObject.put("uensVote", "No")

            if (switch_follow_unfollow.isChecked)
                jsonObject.put("uensFollowRequest", "Yes")
            else
                jsonObject.put("uensFollowRequest", "No")

            if (switch_connectDisconnect.isChecked)
                jsonObject.put("uensFriendRequest", "Yes")
            else
                jsonObject.put("uensFriendRequest", "No")

            if (switch_comment.isChecked)
                jsonObject.put("uensComment", "Yes")
            else
                jsonObject.put("uensComment", "No")

            jsonObject.put("languageID", "0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        loginModel.userRegistration(mActivity!!, false, jsonArray.toString(), "update_EmailNotification")
                .observe(this@EmailFragment!!,
                        Observer<List<SignupPojo>> { loginPojo ->
                            if (loginPojo != null && loginPojo.isNotEmpty()) {
                                MyUtils.dismissProgressDialog()

                                if (loginPojo[0].status.equals("true", true)) {

                                    StoreSessionManager(loginPojo[0].data[0])

                                } else {
                                    if (activity != null && activity is MainActivity)
                                        MyUtils.showSnackbar(mActivity!!, loginPojo!![0]!!.message!!, ll_mainNotification)
                                }

                            } else {
                                if (activity != null && activity is MainActivity) {
                                    (activity as MainActivity).errorMethod()
                                    MyUtils.dismissProgressDialog()

                                }

                            }
                        })

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

            if (switch_like.isChecked)
                jsonObject.put("upnsLike", "Yes")
            else
                jsonObject.put("upnsLike", "No")

            if (switch_vote.isChecked)
                jsonObject.put("upnsVote", "Yes")
            else
                jsonObject.put("upnsVote", "No")

            if (switch_follow_unfollow.isChecked)
                jsonObject.put("upnsFollowRequest", "Yes")
            else
                jsonObject.put("upnsFollowRequest", "No")

            if (switch_connectDisconnect.isChecked)
                jsonObject.put("upnsFriendRequest", "Yes")
            else
                jsonObject.put("upnsFriendRequest", "No")

            if (switch_comment.isChecked)
                jsonObject.put("upnsComment", "Yes")
            else
                jsonObject.put("upnsComment", "No")

            jsonObject.put("languageID", "0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        loginModel.userRegistration(mActivity!!, false, jsonArray.toString(), "update_PushNotification")
                .observe(this@EmailFragment!!,
                        Observer<List<SignupPojo>> { loginPojo ->
                            if (loginPojo != null && loginPojo.isNotEmpty()) {
                                MyUtils.dismissProgressDialog()

                                if (loginPojo[0].status.equals("true", true)) {

                                    StoreSessionManager(loginPojo[0].data[0])

                                } else {
                                    if (activity != null && activity is MainActivity)
                                        MyUtils.showSnackbar(mActivity!!, loginPojo!![0]!!.message!!, ll_mainNotification)
                                }

                            } else {
                                if (activity != null && activity is MainActivity) {
                                    (activity as MainActivity).errorMethod()
                                    MyUtils.dismissProgressDialog()

                                }

                            }
                        })

    }

    fun updateSmsNotification() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)

            if (switch_like.isChecked)
                jsonObject.put("usnsLike", "Yes")
            else
                jsonObject.put("usnsLike", "No")

            if (switch_vote.isChecked)
                jsonObject.put("usnsVote", "Yes")
            else
                jsonObject.put("usnsVote", "No")

            if (switch_follow_unfollow.isChecked)
                jsonObject.put("usnsFollowRequest", "Yes")
            else
                jsonObject.put("usnsFollowRequest", "No")

            if (switch_connectDisconnect.isChecked)
                jsonObject.put("usnsFriendRequest", "Yes")
            else
                jsonObject.put("usnsFriendRequest", "No")

            if (switch_comment.isChecked)
                jsonObject.put("usnsComment", "Yes")
            else
                jsonObject.put("usnsComment", "No")

            jsonObject.put("languageID", "0")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        val loginModel = ViewModelProviders.of(this@EmailFragment).get(SignupModel::class.java)
        loginModel.userRegistration(mActivity!!, false, jsonArray.toString(), "update_SmsNotification")
                .observe(this@EmailFragment!!,
                        Observer<List<SignupPojo>> { loginPojo ->
                            if (loginPojo != null && loginPojo.isNotEmpty()) {
                                MyUtils.dismissProgressDialog()

                                if (loginPojo[0].status.equals("true", true)) {

                                    StoreSessionManager(loginPojo[0].data[0])

                                } else {
                                    if (activity != null && activity is MainActivity)
                                        MyUtils.showSnackbar(mActivity!!, loginPojo!![0]!!.message!!, ll_mainNotification)
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

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when(buttonView?.id)
        {
            R.id.switch_follow_unfollow->{
                when (tabposition) {
                    0 -> {
                        updateEmailNotification()
                    }
                    1 -> {
                        updatePushNotification()
                    }
                    2 -> {
                        updateSmsNotification()
                    }
                }
            }
            R.id.switch_connectDisconnect->{
                when (tabposition) {
                    0 -> {
                        updateEmailNotification()
                    }
                    1 -> {
                        updatePushNotification()
                    }
                    2 -> {
                        updateSmsNotification()
                    }
                }
            }
            R.id.switch_like->{
                when (tabposition) {
                    0 -> {
                        updateEmailNotification()
                    }
                    1 -> {
                        updatePushNotification()
                    }
                    2 -> {
                        updateSmsNotification()
                    }
                }
            }
            R.id.switch_vote->{
                when (tabposition) {
                    0 -> {
                        updateEmailNotification()
                    }
                    1 -> {
                        updatePushNotification()
                    }
                    2 -> {
                        updateSmsNotification()
                    }
                }
            }
            R.id.switch_comment->{
                when (tabposition) {
                    0 -> {
                        updateEmailNotification()
                    }
                    1 -> {
                        updatePushNotification()
                    }
                    2 -> {
                        updateSmsNotification()
                    }
                }
            }

        }
    }

}
