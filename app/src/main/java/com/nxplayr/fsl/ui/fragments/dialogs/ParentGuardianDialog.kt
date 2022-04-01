package com.nxplayr.fsl.ui.fragments.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.util.interfaces.DialogListener
import kotlinx.android.synthetic.main.parent_dialog.*

class ParentGuardianDialog(
    context: Activity,
    listener1: DialogListener
) :
    Dialog(context) {

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