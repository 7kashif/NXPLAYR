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
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.PlayerPosData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.filterfeed.viewmodel.PlayerPositionModel
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_pitch_position.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class PitchPositionFragment : Fragment() {

    private var positionsData: List<PlayerPosData>? = null
    private var v: View? = null
    var mActivity: Activity? = null
    var playerPosList: ArrayList<PlayerPosData>? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var plyrposiID = ""
    var plyposList: ArrayList<String>? = ArrayList()
    var plyPosName = ""
    var plyPosTags = ""
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null

    var b_RS: Boolean = false
    var b_CF: Boolean = false
    var b_LS: Boolean = false
    var b_LM: Boolean = false
    var b_RM: Boolean = false
    var b_CM: Boolean = false
    var b_LB: Boolean = false
    var b_RB: Boolean = false
    var b_CB: Boolean = false
    var b_CBR: Boolean = false
    var b_GK: Boolean = false


    private lateinit var loginModel: SignupModelV2
    private lateinit var playerPositionModel: PlayerPositionModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_pitch_position, container, false)
        }
        return v
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngYourPositionPitch.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngYourPositionPitch
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_changePitchPosition.progressText = sessionManager?.LanguageLabel?.lngSave
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
        if (arguments != null) {
            fromProfile = arguments!!.getString("fromProfile")!!
            userId = arguments!!.getString("userId", "")
            if (!userId.equals(userData?.userID, false)) {
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
        if (!userId.equals(userData?.userID, false)) {
            btn_changePitchPosition.visibility = View.GONE
            plyrposiID = otherUserData!!.plyrposiID
            plyPosName = otherUserData!!.plyrposiName
            val plyPosNameList = plyPosName.replace(",", ",").split(",")

            plyPosTags = otherUserData!!.plyrposiTagss
            if (!plyPosNameList.isNullOrEmpty()) {
                tv_playerUpdatedPos.text = plyPosNameList?.last()
            } else {
                tv_playerUpdatedPos.text = playerPosList.toString()
            }
        } else {
            btn_changePitchPosition.visibility = View.VISIBLE
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

        if (userId.equals(userData?.userID, false)) {
            setPlayerPosition()
        }

        btn_changePitchPosition.setOnClickListener {
            changePlayerPos()
        }

    }

    private fun updateBtn() {
        if (!plyposList.isNullOrEmpty()) {
            btn_changePitchPosition.isEnabled = true
            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_changePitchPosition.textColor = resources.getColor(R.color.black)
            btn_changePitchPosition.strokeColor = resources.getColor(R.color.colorPrimary)
        } else {
            btn_changePitchPosition.isEnabled = false
            btn_changePitchPosition.strokeColor = (resources.getColor(R.color.grayborder))
            btn_changePitchPosition.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_changePitchPosition.textColor = resources.getColor(R.color.colorPrimary)
        }
    }

    private fun setupViewModel() {
        loginModel = ViewModelProvider(this@PitchPositionFragment).get(SignupModelV2::class.java)
        playerPositionModel =
            ViewModelProvider(this@PitchPositionFragment).get(PlayerPositionModel::class.java)
        getPitchPosition()
    }

    private fun getPitchPosition() {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")

            if (!sessionManager!!.isLoggedIn()) {
                jsonObject.put(
                    "languageID",
                    if (sessionManager?.getSelectedLanguage() == null) "1" else sessionManager?.getSelectedLanguage()?.languageID
                )
            } else {
                jsonObject.put("languageID", sessionManager!!.get_Authenticate_User().languageID)
            }
            jsonObject.put("page", 0)
            jsonObject.put("pagesize", "100")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        playerPositionModel.getPlayerPosList(mActivity!!, false, jsonArray.toString()).observe(
            viewLifecycleOwner
        ) { specialityPojo ->
            if (specialityPojo != null && specialityPojo.isNotEmpty()) {
                if (specialityPojo[0].status.equals("true", false)) {
                    positionsData = specialityPojo[0].data
                    setPositions(positionsData!!)
                }
            }
        }
    }

    private fun setPositions(positionsData: List<PlayerPosData>) {
        if (positionsData != null) {
            tv_RS.text = positionsData[8].plyrposiTagss
            tv_CF.text = positionsData[7].plyrposiTagss
            tv_LS.text = positionsData[9].plyrposiTagss
            tv_LM.text = positionsData[5].plyrposiTagss
            tv_RM.text = positionsData[6].plyrposiTagss
            tv_CM.text = positionsData[4].plyrposiTagss
            tv_LB.text = positionsData[2].plyrposiTagss
            tv_RB.text = positionsData[3].plyrposiTagss
            tv_CB.text = positionsData[10].plyrposiTagss
            tv_CBR.text = positionsData[1].plyrposiTagss
            tv_GK.text = positionsData[0].plyrposiTagss

            updatePlayerPosition(positionsData)
        }
    }

    private fun setPlayerPosition() {
        tv_RS.setOnClickListener {
            plyrposiID = positionsData?.get(8)?.plyrposiID.toString()
            if (!b_RS) {
                b_RS = true
                tv_RS.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_RS.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(8)?.plyrposiName.toString()
            } else {
                b_RS = false
                tv_RS.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_RS.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_RS.text = positionsData?.get(8)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(8)?.plyrposiTagss.toString()

            updateBtn()

        }
        tv_CF.setOnClickListener {
            plyrposiID = positionsData?.get(7)?.plyrposiID.toString()
            if (!b_CF) {
                b_CF = true
                tv_CF.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CF.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(7)?.plyrposiName.toString()
            } else {
                b_CF = false
                tv_CF.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CF.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_CF.text = positionsData?.get(7)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(7)?.plyrposiTagss.toString()

            updateBtn()

        }
        tv_LS.setOnClickListener {
            plyrposiID = positionsData?.get(9)?.plyrposiID.toString()
            if (!b_LS) {
                b_LS = true
                tv_LS.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_LS.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(9)?.plyrposiName.toString()
            } else {
                b_LS = false
                tv_LS.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_LS.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }

            tv_LS.text = positionsData?.get(9)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(9)?.plyrposiTagss.toString()

            updateBtn()
        }
        tv_LM.setOnClickListener {
            plyrposiID = positionsData?.get(5)?.plyrposiID.toString()
            if (!b_LM) {
                b_LM = true
                tv_LM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_LM.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(5)?.plyrposiName.toString()
            } else {
                b_LM = false
                tv_LM.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_LM.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_LM.text = positionsData?.get(5)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(5)?.plyrposiTagss.toString()
            updateBtn()

        }
        tv_RM.setOnClickListener {
            plyrposiID = positionsData?.get(6)?.plyrposiID.toString()
            if (!b_RM) {
                b_RM = true
                tv_RM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_RM.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(6)?.plyrposiName.toString()
            } else {
                b_RM = false
                tv_RM.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_RM.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_RM.text = positionsData?.get(6)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(6)?.plyrposiTagss.toString()

            updateBtn()
        }
        tv_CM.setOnClickListener {
            plyrposiID = positionsData?.get(4)?.plyrposiID.toString()
            if (!b_CM) {
                b_CM = true
                tv_CM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CM.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(4)?.plyrposiName.toString()
            } else {
                b_CM = false
                tv_CM.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CM.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_CM.text = positionsData?.get(4)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(4)?.plyrposiTagss.toString()

            updateBtn()
        }
        tv_LB.setOnClickListener {
            plyrposiID = positionsData?.get(2)?.plyrposiID.toString()
            if (!b_LB) {
                b_LB = true
                tv_LB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_LB.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(2)?.plyrposiName.toString()
            } else {
                b_LB = false
                tv_LB.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_LB.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_LB.text = positionsData?.get(2)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(2)?.plyrposiTagss.toString()

            updateBtn()
        }
        tv_RB.setOnClickListener {
            plyrposiID = positionsData?.get(3)?.plyrposiID.toString()
            if (!b_RB) {
                b_RB = true
                tv_RB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_RB.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(3)?.plyrposiName.toString()
            } else {
                b_RB = false
                tv_RB.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_RB.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_RB.text = positionsData?.get(3)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(3)?.plyrposiTagss.toString()

            updateBtn()
        }
        tv_CB.setOnClickListener {
            plyrposiID = positionsData?.get(10)?.plyrposiID.toString()
            if (!b_CB) {
                b_CB = true
                tv_CB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CB.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(10)?.plyrposiName.toString()
            } else {
                b_CB = false
                tv_CB.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CB.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_CB.text = positionsData?.get(10)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(10)?.plyrposiTagss.toString()

            updateBtn()
        }
        tv_CBR.setOnClickListener {
            plyrposiID = positionsData?.get(1)?.plyrposiID.toString()
            if (!b_CBR) {
                b_CBR = true
                tv_CBR.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_CBR.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(1)?.plyrposiName.toString()
            } else {
                b_CBR = false
                tv_CBR.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_CBR.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_CBR.text = positionsData?.get(1)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(1)?.plyrposiTagss.toString()

            updateBtn()
        }
        tv_GK.setOnClickListener {
            plyrposiID = positionsData?.get(0)?.plyrposiID.toString()
            if (!b_GK) {
                b_GK = true
                tv_GK.background = resources.getDrawable(R.drawable.circle_fill_blue)
                tv_GK.setTextColor(resources.getColor(R.color.black))
                if (!plyposList!!.contains(plyrposiID))
                    plyposList?.add(plyrposiID)
                tv_playerUpdatedPos.text = positionsData?.get(0)?.plyrposiName.toString()
            } else {
                b_GK = false
                tv_GK.background = resources.getDrawable(R.drawable.circle_pitch_black)
                tv_GK.setTextColor(resources.getColor(R.color.colorPrimary))
                plyposList?.remove(plyrposiID)
            }
            tv_GK.text = positionsData?.get(0)?.plyrposiTagss.toString()
            plyPosTags = positionsData?.get(0)?.plyrposiTagss.toString()

            updateBtn()
        }
    }

    private fun updatePlayerPosition(positionsData: List<PlayerPosData>) {

        var str = if (!userId.equals(userData?.userID, false)) {
            otherUserData!!.plyrposiTagss

        } else {
            userData!!.plyrposiTagss

        }
        val positionList = str.replace(",", ",").split(",")

        for (element in positionList) {
            for (element2 in positionsData) {
                if (element == element2.plyrposiTagss) {
                    when (element2.plyrposiID) {
                        "1" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_GK = true
                            tv_GK.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_GK.setTextColor(resources.getColor(R.color.black))
                        }
                        "2" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_CBR = true
                            tv_CBR.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_CBR.setTextColor(resources.getColor(R.color.black))
                        }
                        "3" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_LB = true
                            tv_LB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_LB.setTextColor(resources.getColor(R.color.black))
                        }
                        "4" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_RB = true
                            tv_RB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_RB.setTextColor(resources.getColor(R.color.black))
                        }
                        "5" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_CM = true
                            tv_CM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_CM.setTextColor(resources.getColor(R.color.black))
                        }
                        "6" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_LM = true
                            tv_LM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_LM.setTextColor(resources.getColor(R.color.black))
                        }
                        "7" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_RM = true
                            tv_RM.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_RM.setTextColor(resources.getColor(R.color.black))
                        }
                        "8" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_CF = true
                            tv_CF.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_CF.setTextColor(resources.getColor(R.color.black))
                        }
                        "9" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_RS = true
                            tv_RS.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_RS.setTextColor(resources.getColor(R.color.black))
                        }
                        "10" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_LS = true
                            tv_LS.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_LS.setTextColor(resources.getColor(R.color.black))
                        }
                        "11" -> {
                            if (!plyposList!!.contains(element2.plyrposiID))
                                plyposList?.add(element2.plyrposiID)
                            b_CB = true
                            tv_CB.background = resources.getDrawable(R.drawable.circle_fill_blue)
                            tv_CB.setTextColor(resources.getColor(R.color.black))
                        }
                    }
                }
            }
        }
        updateBtn()
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
        loginModel.changePlayerPosition(jsonArray.toString())
        loginModel.changePlayerPosition
            .observe(
                viewLifecycleOwner
            ) { loginPojo ->

                btn_changePitchPosition.endAnimation()
                if (loginPojo != null) {
                    if (loginPojo.get(0).status.equals("true", true)) {
                        try {

                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message!!,
                                ll_pitchPosition
                            )
                            StoreSessionManager(loginPojo[0].data[0])
                            Handler().postDelayed({
                                (activity as MainActivity).onBackPressed()
                            }, 1000)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {

                        MyUtils.showSnackbar(
                            mActivity!!,
                            loginPojo.get(0).message!!,
                            ll_pitchPosition
                        )
                    }

                } else {
                    btn_changePitchPosition.endAnimation()
                    Toast.makeText(context, R.string.error_common_network, Toast.LENGTH_SHORT)
                        .show()
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
