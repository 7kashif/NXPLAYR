package com.nxplayr.fsl.ui.activity.onboarding.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_otp_verification.*
import kotlinx.android.synthetic.main.activity_successfully_registered.*
import kotlinx.android.synthetic.main.fragment_select_gender.*

@Suppress("DEPRECATION")
class SuccessfullyRegisteredActivity : AppCompatActivity(), View.OnClickListener {

    var selectModeType1: Int = 0
    var colorId: Int? = null
    var sessionManager: SessionManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_successfully_registered)
        sessionManager = SessionManager(this@SuccessfullyRegisteredActivity)

        if (intent != null)
            selectModeType1 = intent.getIntExtra("selectModeType", 0)

        setupUI()
    }

    private fun setupUI() {
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngProfileRegisterTitle.isNullOrEmpty())
                successRegisteredTv.text = sessionManager?.LanguageLabel?.lngProfileRegisterTitle
            if (!sessionManager?.LanguageLabel?.lngProfileRegisterDetail.isNullOrEmpty())
                successRegisteredTvDetail.text =
                    sessionManager?.LanguageLabel?.lngProfileRegisterDetail
            if (!sessionManager?.LanguageLabel?.lngNext.isNullOrEmpty())
                btnSuccesfullyRegister.progressText = sessionManager?.LanguageLabel?.lngNext
        }

        if (selectModeType1 == 0) {
            colorId = R.color.colorPrimary
            successRegisteredimagview.setImageDrawable(resources.getDrawable(R.drawable.tick_mark_registered))
        } else if (selectModeType1 == 1) {
            colorId = R.color.yellow
        } else if (selectModeType1 == 2) {
            colorId = R.color.colorAccent
            successRegisteredimagview.setImageDrawable(resources.getDrawable(R.drawable.sucucess_icon))
        }

        btnSuccesfullyRegister.backgroundTint = (resources.getColor(colorId!!))
        btnSuccesfullyRegister.textColor = resources.getColor(R.color.black)
        MyUtils.setSelectedModeTypeViewColor(this, arrayListOf(successRegisteredTv), colorId!!)

        btnSuccesfullyRegister.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnSuccesfullyRegister -> {
                when (selectModeType1) {
                    0 -> {
                        /*intent = Intent(this@SuccessfullyRegisteredActivity, ContactListActivity::class.java)
                         intent.putExtra("selectModeType", selectModeType1)
                         Log.e("selectMode", selectModeType1.toString())
                         startActivity(intent)*/

                        var intent =
                            Intent(this@SuccessfullyRegisteredActivity, MainActivity::class.java)
                        intent.putExtra("selectModeType", selectModeType1)
                        startActivity(intent)
                        finishAffinity()
                    }
                    1 -> {
                        var intent =
                            Intent(this@SuccessfullyRegisteredActivity, MainActivity::class.java)
                        intent.putExtra("selectModeType", selectModeType1)
                        startActivity(intent)
                        finishAffinity()
                    }
                    2 -> {

//                    intent = Intent(this@SuccessfullyRegisteredActivity, MainActivity::class.java)
//                    intent.putExtra("selectModeType", selectModeType1)
//                    Log.e("selectMode", selectModeType1.toString())
//                    startActivity(intent)
                    }
                }
            }
        }
    }
}
