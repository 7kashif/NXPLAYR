package com.nxplayr.fsl.util

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import android.webkit.WebView
import android.view.View
import android.view.Window
import android.webkit.WebViewClient
import com.nxplayr.fsl.data.model.LinkedInUserProfile
import com.nxplayr.fsl.data.model.LinkedInEmailPojo
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestApi
import com.nxplayr.fsl.data.api.RestClient
import kotlinx.android.synthetic.main.linkedin_activity.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import com.google.gson.GsonBuilder



class LinkedinActivity(context1: Context, var mListener: LinkedinData) {
    private val API_KEY = "77oyexmcv4a991"
    //This is the private api key of our application
    private val SECRET_KEY = "CJekBJF0IXxua4qO"
    //This is any string we want to use. This will be used for avoiding CSRF attacks. You can generate one here: http://strongpasswordgenerator.com/
    private val STATE = "123456789"
    //We use a made up url that we will intercept when redirecting. Avoid Uppercases.
    private val REDIRECT_URI = "http://betaapplication.com/fsl/backend/web/index.php/"
    /** */
    //These are constants used for build the urls
    private val AUTHORIZATION_URL = "https://www.linkedin.com/oauth/v2/authorization"
    private val ACCESS_TOKEN_URL = "https://www.linkedin.com/uas/oauth2/accessToken"
    private val SECRET_KEY_PARAM = "client_secret"
    private val RESPONSE_TYPE_PARAM = "response_type"
    private val GRANT_TYPE_PARAM = "grant_type"
    private val GRANT_TYPE = "authorization_code"
    private val RESPONSE_TYPE_VALUE = "code"
    private val CLIENT_ID_PARAM = "client_id"
    private val STATE_PARAM = "state"
    private val REDIRECT_URI_PARAM = "redirect_uri"
    /*---------------------------------------*/
    private val QUESTION_MARK = "?"
    private val AMPERSAND = "&"
    private val EQUALS = "="
    private var pd: ProgressDialog? = null

    //private OauthInterface oauthInterface;
    var accessToken: String? = null
    var context: Context? = null
    var dialog: Dialog? = null

    init {
        this.context = context1
    }

    fun showLinkedin() {

        dialog = Dialog(context!!, R.style.AppTheme)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)

        dialog?.setContentView(R.layout.linkedin_activity)

        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        pd = ProgressDialog.show(context, "", "Loading...", true)
        dialog?.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                mListener.linkedCancel()
                dialog.dismiss()
            }
            true
        })
        dialog?.close_icon!!.setOnClickListener {
            mListener.linkedCancel()
            dialog?.dismiss()
        }


        //Request focus for the webview
        dialog?.activity_web_view!!.requestFocus(View.FOCUS_DOWN)
        dialog?.activity_web_view!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (pd != null && pd!!.isShowing) {
                    pd!!.dismiss()
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, authorizationUrl: String): Boolean {

                if (authorizationUrl.startsWith(REDIRECT_URI))
                {
                    Log.i("Authorize", "")
                    val uri = Uri.parse(authorizationUrl)
                    val stateToken = uri.getQueryParameter(STATE_PARAM)
                    if (stateToken == null || stateToken != STATE) {
                        Log.e("Authorize", "State token doesn't match")
                        return true
                    }
                    //If the user doesn't allow authorization to our application, the authorizationToken Will be null.
                    val authorizationToken = uri.getQueryParameter(RESPONSE_TYPE_VALUE)
                    if (authorizationToken == null) {
                        Log.i("Authorize", "The user doesn't allow authorization.")
                        return true
                    }
                    Log.i("Authorize", "Auth token received: " + authorizationToken)
                    //Generate URL for requesting Access Token
                    val accessTokenUrl = getAccessTokenUrl(authorizationToken)
                    getLinkedinData(accessTokenUrl)

                } else {
                    Log.i("Authorize", "Redirecting to: $authorizationUrl")
                    dialog?.activity_web_view!!.loadUrl(authorizationUrl)
                }
                return true
            }
        }

        val authUrl = getAuthorizationUrl()
        Log.i("Authorize", "Loading Auth Url: $authUrl")
        dialog?.activity_web_view!!.loadUrl(authUrl)
        dialog?.show()

    }

    private fun getAccessTokenUrl(authorizationToken: String): String {
        return (ACCESS_TOKEN_URL
                + QUESTION_MARK
                + GRANT_TYPE_PARAM + EQUALS + GRANT_TYPE
                + AMPERSAND
                + RESPONSE_TYPE_VALUE + EQUALS + authorizationToken
                + AMPERSAND
                + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND
                + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND
                + SECRET_KEY_PARAM + EQUALS + SECRET_KEY)
    }

    /*private fun getAuthorizationUrl(): String {
        return (AUTHORIZATION_URL
                + QUESTION_MARK + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE_VALUE
                + AMPERSAND + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND + STATE_PARAM + EQUALS + STATE
                + AMPERSAND + "scope=r_liteprofile%20r_emailaddress%20w_member_social")
    }*/
    private fun getAuthorizationUrl(): String {
        return (AUTHORIZATION_URL
                + QUESTION_MARK + RESPONSE_TYPE_PARAM + EQUALS + RESPONSE_TYPE_VALUE
                + AMPERSAND + CLIENT_ID_PARAM + EQUALS + API_KEY
                + AMPERSAND + REDIRECT_URI_PARAM + EQUALS + REDIRECT_URI
                + AMPERSAND + STATE_PARAM + EQUALS + STATE
                + AMPERSAND + "scope=r_liteprofile%20r_emailaddress")
    }


    private fun getLinkedinData(mUrlString: String) {

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(mUrlString)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()


            }
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {

                if (!response.isSuccessful) {
                    throw IOException("Error : $response")
                } else {
                    Log.d("Linkedin", "Request Successful.")
                }

                // Read data in the worker thread
                val data = response.body!!.string()
                Log.e("data is", data.toString())

                val resultJson = JSONObject(data)
                val expiresIn = if (resultJson.has("expires_in")) resultJson.getInt("expires_in") else 0


                accessToken =
                    if (resultJson.has("access_token")) resultJson.getString("access_token") else null
                Log.e("Token", "" + accessToken)
                if (expiresIn > 0 && accessToken != null) {

                    Log.i(
                        "Authorize",
                        "This is the access Token: $accessToken. It will expires in $expiresIn secs"
                    )
                    getLinkedInEmailData(accessToken!!)

                }
            }
        })
    }

    private fun getLinkedInEmailData(token: String) {

        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.linkedin.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(
                RestClient.setOkHttpClientBuilder()
                    .build()
            )
            .build()
        val client = retrofit.create<RestApi>(RestApi::class.java)
        val call = client.getUserEmail("Bearer $token")
        call.enqueue(object : retrofit2.Callback<LinkedInEmailPojo> {
            override fun onFailure(call: retrofit2.Call<LinkedInEmailPojo>, t: Throwable) {
                Log.e("error", t.toString())
            }

            override fun onResponse(

                call: retrofit2.Call<LinkedInEmailPojo>,
                response: retrofit2.Response<LinkedInEmailPojo>
            ) {

                if (response.code() == 200) {
                    val data = response.body()
                    getLinkedInProfileData(token, data?.elements?.get(0)?.handle?.emailAddress)

                }
            }

        })
    }

    private fun getLinkedInProfileData(token: String, email: String?) {

        val gson = GsonBuilder()
            .setLenient()
            .create()
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.linkedin.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(
                RestClient.setOkHttpClientBuilder()
                    .build()
            )
            .build()
        val client = retrofit.create<RestApi>(RestApi::class.java)
        val call = client.getUser("Bearer $token")
        call.enqueue(object : retrofit2.Callback<LinkedInUserProfile> {
            override fun onFailure(call: retrofit2.Call<LinkedInUserProfile>, t: Throwable) {

                Log.e("error", t.toString())
            }

            override fun onResponse(
                call: retrofit2.Call<LinkedInUserProfile>,
                response: retrofit2.Response<LinkedInUserProfile>
            ) {

                if (response.code() == 200) {
                    val data = response.body()
                    dialog?.dismiss()
                    mListener.LinkedinSuccess(
                        email,
                        data?.firstName?.localized?.enUS,
                        data?.lastName?.localized?.enUS,
                        data?.id
                    )
                }
            }

        })
    }

    interface LinkedinData {

        fun linkedCancel()

        fun LinkedinSuccess(email: String?, first_name: String?, last_name: String?, id: String?)
    }

}