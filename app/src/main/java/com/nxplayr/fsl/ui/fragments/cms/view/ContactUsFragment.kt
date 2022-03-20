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

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        setData()

        tv_emailContactUs.setOnClickListener(this)
        tv_callContactUs.setOnClickListener(this)
        tv_Faq.setOnClickListener(this)
    }

    fun setData() {
        if (!userData!!.settings[0].settingsSupportEmail.isNullOrEmpty()) {
            tv_emailContactUs.text = userData!!.settings[0].settingsSupportEmail
        }
        if (!userData!!.settings[0].settingsSupportPhone.isNullOrEmpty()) {
            tv_callContactUs.text = userData!!.settings[0].settingsSupportPhone
        }
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
            R.id.tv_emailContactUs -> {
                if (!userData!!.settings[0].settingsSupportEmail.isNullOrEmpty()) {
                    var emailIntent = Intent(android.content.Intent.ACTION_SEND)
                    emailIntent.type = "text/plain"
                    emailIntent.putExtra(
                        Intent.EXTRA_EMAIL,
                        userData!!.settings[0].settingsSupportEmail
                    )
                    emailIntent.type = "message/rfc822"
                    try {
                        startActivity(
                            Intent.createChooser(
                                emailIntent,
                                "Send email using..."
                            )
                        )
                    } catch (ex: android.content.ActivityNotFoundException) {
                        Toast.makeText(
                            activity,
                            "No email clients installed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
            R.id.tv_callContactUs ->{
                if (!userData!!.settings[0].settingsSupportPhone.isNullOrEmpty()) {
                    checkCallPermission()
                }
            }
            R.id.tv_Faq->{
                (activity as MainActivity).navigateTo(
                    FAQTopicsFragment(),
                    FAQTopicsFragment::class.java.name,
                    true
                )

            }
        }
    }

}
