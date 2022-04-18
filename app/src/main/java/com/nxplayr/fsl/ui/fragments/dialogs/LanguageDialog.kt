package com.nxplayr.fsl.ui.fragments.dialogs

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.LanguageListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.setting.adapter.LanguageAdapterWithoutUser
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.util.interfaces.LanguageSelection
import kotlinx.android.synthetic.main.language_dialog.*

class LanguageDialog(
    context: Activity,
    languageListParam: ArrayList<LanguageListData>?,
    listener1: LanguageSelection
) :
    Dialog(context) {

    var listener: LanguageSelection = listener1
    private lateinit var linearLayoutManager: LinearLayoutManager
    var languageAdapter: LanguageAdapterWithoutUser? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var languageId = "1"
    private var activity: Activity = context
    private var languageList: ArrayList<LanguageListData>? = languageListParam

    init {
        setCancelable(true)
        window?.setBackgroundDrawableResource(android.R.color.transparent);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.language_dialog)
        initData()
    }

    private fun initData() {
        sessionManager = SessionManager(activity)
        if (sessionManager?.getSelectedLanguage() != null)
            languageId = sessionManager?.getSelectedLanguage()!!.languageID
        linearLayoutManager = LinearLayoutManager(activity)
        languageAdapter = LanguageAdapterWithoutUser(
            activity,
            languageList,
            object : LanguageAdapterWithoutUser.OnItemClick {
                override fun onClicled(position: Int, from: String) {
                    for (i in 0 until languageList!!.size) {
                        languageList!![i].status = i == position
                    }
                    languageId = languageList!![position].languageID
                    sessionManager?.setSelectedLanguage(languageList!![position])
                    languageAdapter?.notifyDataSetChanged()
                    listener.onLanguageSelect(languageList!![position])
                    dismiss()
                }
            },
            languageId
        )

        languages.layoutManager = linearLayoutManager
        languages.adapter = languageAdapter
        languages.setHasFixedSize(true)
    }
}