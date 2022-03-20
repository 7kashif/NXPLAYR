package com.nxplayr.fsl.ui.fragments.userlanguage.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.feed.view.SelectContentLanguage
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_content_language.*
import kotlinx.android.synthetic.main.toolbar.*

class ContentLanguage : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: Activity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_content_language, container, false)
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
            if(userData?.userContentLanguage.isNullOrEmpty())
            {
                tv_content_language.text="Select Language"
            }
            else
            {
                tv_content_language.text=userData?.userContentLanguage
            }
        }
        setupUI()

    }

    private fun setupUI() {
        tvToolbarTitle.setText(R.string.content_language)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        tv_content_language_change.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_content_language_change->{
                (activity as MainActivity).navigateTo(SelectContentLanguage(), SelectContentLanguage::class.java.name, true)
            }
        }
    }

}