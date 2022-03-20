package com.nxplayr.fsl.ui.fragments.userpitchposition.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.data.model.PlayerPosData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_current_club.*
import kotlinx.android.synthetic.main.fragment_pitch_position.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class PitchPositionFragment : Fragment() {

    private var v: View? = null
    var mActivity: Activity? = null
    var playerPosList: ArrayList<PlayerPosData>? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var plyrposiID = ""
    var isCheckPlyID: Boolean = false
    var plyposList: ArrayList<String>? = ArrayList()
    var plyPosName = ""
    var plyPosTags = ""

    var fromProfile=""
    var userId=""
    var otherUserData: SignupData?=null
    private lateinit var  loginModel: SignupModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_pitch_position, container, false)
        }
        return v
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
        if (arguments != null)
        {
            fromProfile = arguments!!.getString("fromProfile")!!
            userId=arguments!!.getString("userId","")
            if(!userId.equals(userData?.userID,false))
            {
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            }
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.setText(getString(R.string.your_position_on_the_pitch))

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()

        }
        updatePlayerPosition()
        if(!userId.equals(userData?.userID,false))
        {
            btn_changePitchPosition.visibility=View.GONE
            plyrposiID = otherUserData!!.plyrposiID
            plyPosName = otherUserData!!.plyrposiName
            val plyPosNameList = plyPosName.replace(",", ",").split(",")

            plyPosTags = otherUserData!!.plyrposiTagss
            if (!plyPosNameList.isNullOrEmpty()) {
                tv_playerUpdatedPos.text = plyPosNameList?.last()
            } else {
                tv_playerUpdatedPos.text = playerPosList.toString()
            }
        }
        else
        {
            btn_changePitchPosition.visibility=View.VISIBLE
            plyrposiID = userData!!.plyrposiID
            plyPosName = userData!!.plyrposiName
            val plyPosNameList = plyPosName.replace(",", ",").split(",")

            plyPosTags = userData!!.plyrposiTagss
            if (!plyPosNameList.isNullOrEmpty()) {
                tv_playerUpdatedPos.text = plyPosNameList?.last()
            } else {
                tv_playerUpdatedPos.text = playerPosList.toString()
            }
        }

        if(userId.equals(userData?.userID,false)) {
            setPlayerPosition()
        }

        if (!userData!!.plyrposiID.isNullOrEmpty()) {
            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        } else {
            btn_changePitchPosition.strokeColor = (resources.getColor(R.color.grayborder))
            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_changePitchPosition.textColor = resources.getColor(R.color.colorPrimary)
        }

        btn_changePitchPosition.setOnClickListener {
            changePlayerPos()
        }

    }

    private fun setupViewModel() {
        loginModel = ViewModelProvider(this@PitchPositionFragment).get(SignupModel::class.java)

    }

    private fun setPlayerPosition() {
        tv_RS.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "9"
            if (!isCheckPlyID) {
                tv_RS.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_RS.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Right Safety"
            } else {
                tv_RS.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_RS.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_RS.text = "RS"
            plyPosTags = "RS"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)

        }
        tv_CF.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "8"
            if (!isCheckPlyID) {
                tv_CF.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CF.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Central Forward"
            } else {
                tv_CF.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CF.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_CF.text = "CF"
            plyPosTags = "CF"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)

        }
        tv_LS.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "10"
            if (!isCheckPlyID) {
                tv_LS.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_LS.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Left Safety"
            } else {
                tv_LS.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_LS.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }

            tv_LS.text = "LS"
            plyPosTags = "LS"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        }
        tv_LM.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "6"
            if (!isCheckPlyID) {
                tv_LM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_LM.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Left Midfielder"
            } else {
                tv_LM.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_LM.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_LM.text = "LM"
            plyPosTags = "LM"
            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)

        }
        tv_RM.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "7"
            if (!isCheckPlyID) {
                tv_RM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_RM.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Right Midfielder"
            } else {
                tv_RM.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_RM.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_RM.text = "RM"
            plyPosTags = "RM"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        }
        tv_CM.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "5"
            if (!isCheckPlyID) {
                tv_CM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CM.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Centre Midfielders"
            } else {
                tv_CM.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CM.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_CM.text = "CM"
            plyPosTags = "CM"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        }
        tv_LB.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "3"
            if (!isCheckPlyID) {
                tv_LB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_LB.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Line Backers"
            } else {
                tv_LB.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_LB.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_LB.text = "LB"
            plyPosTags = "LB"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        }
        tv_RB.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "4"
            if (!isCheckPlyID) {
                tv_RB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_RB.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Running Back"
            } else {
                tv_RB.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_RB.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_RB.text = "RB"
            plyPosTags = "RB"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        }
        tv_CB.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "11"
            if (!isCheckPlyID) {
                tv_CB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CB.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Corner Back Left"
            } else {
                tv_CB.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CB.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_CB.text = "CBL"
            plyPosTags = "CBL"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        }
        tv_CBR.setOnClickListener{
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "2"
            if (!isCheckPlyID) {
                tv_CBR.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CBR.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Corner Back Right"
            } else {
                tv_CBR.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CBR.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_CBR.text = "CBR"
            plyPosTags = "CBR"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        }
        tv_GK.setOnClickListener {
            isCheckPlyID = !isCheckPlyID
            plyrposiID = "1"
            if (!isCheckPlyID) {
                tv_GK.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_GK.setTextColor(resources.getColor(R.color.black))
                plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = "Goalkeeper"
            } else {
                tv_GK.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_GK.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_GK.text = "GK"
            plyPosTags = "GK"

            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        }
    }

    private fun updatePlayerPosition() {

        var str = if(!userId.equals(userData?.userID,false))
        {
            otherUserData!!.plyrposiTagss

        } else{
            userData!!.plyrposiTagss

        }
        val positionList = str.replace(",", ",").split(",")

        for (i in 0 until positionList.size) {
            isCheckPlyID = !isCheckPlyID
            if (positionList[i].equals("GK")) {
                isCheckPlyID = true
                plyrposiID = "1"
                plyposList?.add(plyrposiID)
                tv_GK.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_GK.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "1"
                plyposList?.remove(plyrposiID)
                tv_GK.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_GK.setTextColor(resources.getColor(R.color.colorPrimary))
            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("RS")) {
                isCheckPlyID = true
                plyrposiID = "9"
                plyposList?.add(plyrposiID)
                tv_RS.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_RS.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "9"
                plyposList?.remove(plyrposiID)
                tv_RS.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_RS.setTextColor(resources.getColor(R.color.colorPrimary))
            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("CF")) {
                isCheckPlyID = true
                plyrposiID = "8"
                plyposList?.add(plyrposiID)
                tv_CF.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CF.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "8"
                plyposList?.remove(plyrposiID)
                tv_CF.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CF.setTextColor(resources.getColor(R.color.colorPrimary))

            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("LS")) {
                isCheckPlyID = true
                plyrposiID = "10"
                plyposList?.add(plyrposiID)
                tv_LS.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_LS.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "10"
                plyposList?.remove(plyrposiID)
                tv_LS.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_LS.setTextColor(resources.getColor(R.color.colorPrimary))

            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("LM")) {
                isCheckPlyID = true
                plyrposiID = "6"
                plyposList?.add(plyrposiID)
                tv_LM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_LM.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "6"
                plyposList?.remove(plyrposiID)
                tv_LM.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_LM.setTextColor(resources.getColor(R.color.colorPrimary))

            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("RM")) {
                isCheckPlyID = true
                plyrposiID = "7"
                plyposList?.add(plyrposiID)
                tv_RM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_RM.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "7"
                plyposList?.remove(plyrposiID)
                tv_RM.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_RM.setTextColor(resources.getColor(R.color.colorPrimary))

            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("CM")) {
                isCheckPlyID = true
                plyrposiID = "5"
                plyposList?.add(plyrposiID)
                tv_CM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CM.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "5"
                plyposList?.remove(plyrposiID)
                tv_CM.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CM.setTextColor(resources.getColor(R.color.colorPrimary))

            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("LB")) {
                isCheckPlyID = true
                plyrposiID = "3"
                plyposList?.add(plyrposiID)
                tv_LB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_LB.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "3"
                plyposList?.remove(plyrposiID)
                tv_LB.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_LB.setTextColor(resources.getColor(R.color.colorPrimary))

            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("RB")) {
                isCheckPlyID = true
                plyrposiID = "4"
                plyposList?.add(plyrposiID)
                tv_RB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_RB.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "4"
                plyposList?.remove(plyrposiID)
                tv_RB.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_RB.setTextColor(resources.getColor(R.color.colorPrimary))

            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("CBL")) {
                isCheckPlyID = true
                plyrposiID = "11"
                plyposList?.add(plyrposiID)
                tv_CB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CB.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "11"
                plyposList?.remove(plyrposiID)
                tv_CB.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CB.setTextColor(resources.getColor(R.color.colorPrimary))

            }
        }

        for (i in 0 until positionList.size) {
            if (positionList[i].equals("CBR")) {
                isCheckPlyID = true
                plyrposiID = "2"
                plyposList?.add(plyrposiID)
                tv_CBR.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CBR.setTextColor(resources.getColor(R.color.black))
                break
            } else {
                isCheckPlyID = false
                plyrposiID = "2"
                plyposList?.remove(plyrposiID)
                tv_CBR.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CBR.setTextColor(resources.getColor(R.color.colorPrimary))

            }
        }

    }

    private fun changePlayerPos() {

        btn_changePitchPosition.startAnimation()

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        if (!plyposList.isNullOrEmpty()) {
            plyrposiID = plyposList!!.joinToString { it }
        }

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("plyrposiID", plyrposiID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        loginModel.userRegistration(mActivity!!, false, jsonArray.toString(), "change_playerPosition")
                .observe(viewLifecycleOwner,
                    { loginPojo ->

                        btn_changePitchPosition.endAnimation()
                        if (loginPojo != null) {
                            if (loginPojo.get(0).status.equals("true", true)) {
                                try {

                                    MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message!!, ll_pitchPosition)
                                    StoreSessionManager(loginPojo[0].data[0])
                                    Handler().postDelayed({
                                        (activity as MainActivity).onBackPressed()
                                    }, 1000)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            } else {

                                MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message!!, ll_pitchPosition)
                            }

                        } else {

                            btn_changePitchPosition.endAnimation()
                            Toast.makeText(context,R.string.error_common_network,Toast.LENGTH_SHORT).show()
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
