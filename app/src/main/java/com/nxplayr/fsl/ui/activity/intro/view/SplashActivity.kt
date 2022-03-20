package com.nxplayr.fsl.ui.activity.intro.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.nxplayr.fsl.BuildConfig
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.view.OtpVerificationActivity
import com.nxplayr.fsl.ui.activity.onboarding.view.SignInActivity
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.ui.fragments.setting.viewmodel.LanguageLabelModel
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import kotlinx.android.synthetic.main.activity_splash.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class SplashActivity : AppCompatActivity() {

    var sessionManager: SessionManager? = null
    private lateinit var  languageLabelModel: LanguageLabelModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_splash)
        sessionManager = SessionManager(this@SplashActivity)
        setupViewModel()
        setupUI()


    }

    private fun setupUI() {
        getLanguageLabelList()
    }

    private fun setupViewModel() {
         languageLabelModel = ViewModelProvider(this@SplashActivity).get(LanguageLabelModel::class.java)

    }

    private fun runSplash() {
      //  Handler().postDelayed({
            if (sessionManager?.isLoggedIn()!!) {
                if (sessionManager?.get_Authenticate_User()?.userOVerified.equals("Yes", false))
                {
                    startHomeActivity()
                }
                else
                {
                    var i = Intent(this@SplashActivity, OtpVerificationActivity::class.java)
                    i.putExtra("from", "LoginByOtpVerification")
                    startActivity(i)
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                    finish()
                }


            } else {
                val sp = getSharedPreferences("Tutorial", Context.MODE_PRIVATE)
                if (!sp.getBoolean("first", false)) {
                    val editor = sp.edit()
                    editor.putBoolean("first", true)
                    editor.commit()
                    val intent = Intent(this@SplashActivity, IntroScreenActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    MyUtils.startActivity(this@SplashActivity, SignInActivity::class.java, true)
                }

            }
       // }, 300)
    }

    private fun startHomeActivity() {
        MyUtils.startActivity(this@SplashActivity, MainActivity::class.java, true)
    }

    private fun getLanguageLabelList() {
        progressBar.visibility = View.VISIBLE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("languageID",  if (sessionManager?.getsetSelectedLanguage()
                    .isNullOrEmpty()
            ) "1" else sessionManager?.getsetSelectedLanguage())
            jsonObject.put("langLabelStatus", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        languageLabelModel.getLanguageList(this@SplashActivity!!, false, jsonArray.toString())
            .observe(this@SplashActivity,
                {languageLabelPojo ->
                    progressBar.visibility = View.GONE
                    if (languageLabelPojo != null && languageLabelPojo.isNotEmpty()) {
                        if (languageLabelPojo[0].status == true && !languageLabelPojo[0].data.isNullOrEmpty()) {

                            sessionManager?.LanguageLabel = languageLabelPojo[0].data?.get(0)
                            runSplash()


                        } else {

                            showSnackBar(languageLabelPojo[0]!!.info!!)
                        }

                    } else {
                        errorMethod()

                    }
                })
    }
    fun errorMethod() {
        try {

            progressBar.visibility = View.GONE

            ll_no_data.visibility = View.VISIBLE
            ll_no_data.post {
                val animate = TranslateAnimation(0f, 0f, ll_no_data.getHeight().toFloat(), 0f)
                animate.duration = 500


                ll_no_data.startAnimation(animate)
            }

            tNodataTitle.text =
                if (!MyUtils.isInternetAvailable(this@SplashActivity)) getString(R.string.error_common_network)
                else
                    getString(R.string.error_crash_error_message)
            btnRetry.visibility = View.VISIBLE
            btnRetry.setOnClickListener {

                    getLanguageLabelList()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun showSnackBar(message: String) {
        if ((ll_main_splash != null) and !isFinishing)
            Snackbar.make(this.ll_main_splash!!, message, Snackbar.LENGTH_LONG).show()

    }

    fun printHasyKeyFb(){
        try {
            val info = packageManager.getPackageInfo( BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.d("System out", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("System out", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("System out", "printHashKey()", e)
        }

    }
}
