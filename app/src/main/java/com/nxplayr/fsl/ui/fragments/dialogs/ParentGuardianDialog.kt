package com.nxplayr.fsl.ui.fragments.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.util.interfaces.DialogListener
import kotlinx.android.synthetic.main.fragment_select_age.*
import kotlinx.android.synthetic.main.parent_dialog.*

class ParentGuardianDialog(
    selectModeType1: Int,
    context: Activity,
    listener1: DialogListener
) :
    Dialog(context) {

    var selectModeType: Int = selectModeType1
    var listener: DialogListener = listener1
    var sessionManager: SessionManager? = null
    private var activity: Activity = context

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        window?.setBackgroundDrawableResource(android.R.color.transparent);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.parent_dialog)
        initData()
    }

    private fun initData() {

        if (selectModeType == 0) {
            title.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            okText.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
        } else if (selectModeType == 1) {
            title.setTextColor(ContextCompat.getColor(activity, R.color.yellow))
            okText.setTextColor(ContextCompat.getColor(activity, R.color.yellow))
        } else if (selectModeType == 2) {
            title.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
            okText.setTextColor(ContextCompat.getColor(activity, R.color.colorAccent))
        }

        sessionManager = SessionManager(activity)

        if (sessionManager?.LanguageLabel != null) {
            // static data from preference
            val langLabel = sessionManager?.LanguageLabel

            if (!langLabel?.lngParentAlertTitle.isNullOrEmpty()) {
                title.text = langLabel?.lngParentAlertTitle
            }
            if (!langLabel?.lngParentAlertSubTitle.isNullOrEmpty()) {
                desc.text = langLabel?.lngParentAlertSubTitle
            }
            if (!langLabel?.lngCancel.isNullOrEmpty()) {
                cancelText.text = langLabel?.lngCancel
            }
            if (!langLabel?.lngOK.isNullOrEmpty()) {
                okText.text = langLabel?.lngOK
            }
        }

        cancel.setOnClickListener { dismiss() }
        ok.setOnClickListener {
            listener.onOK()
            dismiss()
        }
    }
}