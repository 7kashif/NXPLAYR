package com.nxplayr.fsl.ui.fragments.setting.view

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.main.view.HomeMainFragment
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.android.synthetic.main.toolbar.*


class MyAccountFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        v = inflater.inflate(R.layout.fragment_my_account, container, false)

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngMyAccount.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngMyAccount
            if (!sessionManager?.LanguageLabel?.lngFullName.isNullOrEmpty())
                txt_edit_full_name.hint = sessionManager?.LanguageLabel?.lngFullName
            if (!sessionManager?.LanguageLabel?.lngEmail.isNullOrEmpty())
                txt_edit_emailId.hint = sessionManager?.LanguageLabel?.lngEmail
            if (!sessionManager?.LanguageLabel?.lngMobileNo.isNullOrEmpty())
                txt_edit_mobileNumber.hint = sessionManager?.LanguageLabel?.lngMobileNo
            if (!sessionManager?.LanguageLabel?.lngLogout.isNullOrEmpty())
                btn_logout.progressText = sessionManager?.LanguageLabel?.lngLogout
            if (!sessionManager?.LanguageLabel?.lngDeleteAccount.isNullOrEmpty())
                btn_deleteAccount.progressText = sessionManager?.LanguageLabel?.lngDeleteAccount
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }


        tvToolbarTitle.text = getString(R.string.my_account)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }




        setUesrAccountData()

        btn_logout.setOnClickListener {
            MyUtils.showMessageOKCancel(mActivity!!,
                "Are you sure want to logout?",
                "Logout",
                DialogInterface.OnClickListener { dialog, which ->
                    dialog.dismiss()
                    (activity as MainActivity).logOut()
                })
        }
        btn_deleteAccount.setOnClickListener {
            (activity as MainActivity).navigateTo(
                DeleteAccountFragment(),
                DeleteAccountFragment::class.java.name,
                true
            )
        }

    }

    private fun setUesrAccountData() {
        if (userData != null) {
            edit_full_name.setText(userData?.userFirstName + " " + userData?.userLastName)
            edit_emailId.setText(userData?.userEmail)
            edit_mobileNumber.setText(userData?.userCountryCode + " " + userData?.userMobile)
        }
    }

}