package com.nxplayr.fsl.ui.fragments.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.core.content.ContextCompat
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.password_info_dialog.*

class MessageDialog(
    selectModeType1: Int,
    context: Activity,
    var msg : String,
) :
    Dialog(context) {

    var selectModeType: Int = selectModeType1
    var sessionManager: SessionManager? = null
    private var activity: Activity = context

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        window?.setBackgroundDrawableResource(android.R.color.transparent);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_info_dialog)
        initData()
    }

    private fun initData() {

        if (selectModeType == 0) {
            password_info_card.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            password_info.setTextColor(ContextCompat.getColor(activity, R.color.black))
        } else if (selectModeType == 1) {
            password_info_card.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.yellow))
            password_info.setTextColor(ContextCompat.getColor(activity, R.color.black))
        } else if (selectModeType == 2) {
            password_info_card.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.colorAccent))
            password_info.setTextColor(ContextCompat.getColor(activity, R.color.black))
        }

//        sessionManager = SessionManager(activity)

//        var str = context.getString(R.string.password_must)
//        if (sessionManager?.LanguageLabel != null) {
//            val langLabel = sessionManager?.LanguageLabel
//            if (!langLabel?.lngValidPasswordInfo.isNullOrEmpty()) {
//                str = langLabel?.lngValidPasswordInfo.toString()
//            }
//        }
        password_info.text = msg
    }
}