package com.nxplayr.fsl.ui.fragments.cms.view


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_contact_us.*
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.toolbar.*


class ContactUsFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    private val CALL_PERMISSIONS_REQUEST = 11
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_contact_us, container, false)
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
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.contact_us)

        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngContactUs.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngContactUs
            if (!sessionManager?.LanguageLabel?.lngContactUsTitle1.isNullOrEmpty())
                title1.text = sessionManager?.LanguageLabel?.lngContactUsTitle1
            if (!sessionManager?.LanguageLabel?.lngContactUsTitle2.isNullOrEmpty())
                title2.text = sessionManager?.LanguageLabel?.lngContactUsTitle2
            if (!sessionManager?.LanguageLabel?.lngContactUsTitle3.isNullOrEmpty())
                title3.text = sessionManager?.LanguageLabel?.lngContactUsTitle3
            if (!sessionManager?.LanguageLabel?.lngContactUsTitle4.isNullOrEmpty())
                title4.text = sessionManager?.LanguageLabel?.lngContactUsTitle4
            if (!sessionManager?.LanguageLabel?.lngCommonQuestions.isNullOrEmpty())
                title5.text = sessionManager?.LanguageLabel?.lngCommonQuestions
            if (!sessionManager?.LanguageLabel?.lngCommonQuestionsSubTitle.isNullOrEmpty())
                title6.text = sessionManager?.LanguageLabel?.lngCommonQuestionsSubTitle
            if (!sessionManager?.LanguageLabel?.lngFAQ.isNullOrEmpty())
                tv_Faq.text = sessionManager?.LanguageLabel?.lngFAQ
            if (!sessionManager?.LanguageLabel?.lngNeedMoreHelp.isNullOrEmpty())
                title7.text = sessionManager?.LanguageLabel?.lngNeedMoreHelp
            if (!sessionManager?.LanguageLabel?.lngNeedMoreHelpSubTitle.isNullOrEmpty())
                title8.text = sessionManager?.LanguageLabel?.lngNeedMoreHelpSubTitle
            if (!sessionManager?.LanguageLabel?.lngCUGeneralQueries.isNullOrEmpty())
                title9.text = sessionManager?.LanguageLabel?.lngCUGeneralQueries
            if (!sessionManager?.LanguageLabel?.lngCUSuggestFeature.isNullOrEmpty())
                title11.text = sessionManager?.LanguageLabel?.lngCUSuggestFeature
            if (!sessionManager?.LanguageLabel?.lngCUPartnerShip.isNullOrEmpty())
                title13.text = sessionManager?.LanguageLabel?.lngCUPartnerShip
            if (!sessionManager?.LanguageLabel?.lngCUPress.isNullOrEmpty())
                title15.text = sessionManager?.LanguageLabel?.lngCUPress
        }

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        setData()
        tv_Faq.setOnClickListener(this)
    }

    fun setData() {

    }

    private fun checkCallPermission() {
        if (!MyUtils.addPermission(mActivity!!, Manifest.permission.CALL_PHONE)) {
            requestPermissions(
                arrayOf(Manifest.permission.CALL_PHONE),
                CALL_PERMISSIONS_REQUEST
            )
        } else
            startCallActivity()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALL_PERMISSIONS_REQUEST ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCallActivity()
                }
        }
    }

    private fun startCallActivity() {

        val intent = Intent(Intent.ACTION_CALL)

        intent.data = Uri.parse("tel:" + userData!!.settings[0].settingsSupportPhone)
        context!!.startActivity(intent)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in childFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.tv_emailContactUs -> {
//                if (!userData!!.settings[0].settingsSupportEmail.isNullOrEmpty()) {
//                    var emailIntent = Intent(android.content.Intent.ACTION_SEND)
//                    emailIntent.type = "text/plain"
//                    emailIntent.putExtra(
//                        Intent.EXTRA_EMAIL,
//                        userData!!.settings[0].settingsSupportEmail
//                    )
//                    emailIntent.type = "message/rfc822"
//                    try {
//                        startActivity(
//                            Intent.createChooser(
//                                emailIntent,
//                                "Send email using..."
//                            )
//                        )
//                    } catch (ex: android.content.ActivityNotFoundException) {
//                        Toast.makeText(
//                            activity,
//                            "No email clients installed.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//
//                }
//            }
//            R.id.tv_callContactUs ->{
//                if (!userData!!.settings[0].settingsSupportPhone.isNullOrEmpty()) {
//                    checkCallPermission()
//                }
//            }
            R.id.tv_Faq -> {
                (activity as MainActivity).navigateTo(
                    FAQTopicsFragment(),
                    FAQTopicsFragment::class.java.name,
                    true
                )
            }
        }
    }

}
