package com.nxplayr.fsl.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupData

class ShareProfile(var context: Activity) {

    fun SharingMsgProfile(userID: String?,imageUri: Uri?=null) {
        var sessionManager=SessionManager(context)
        var userData: SignupData?=null
        if(sessionManager?.isLoggedIn()){
            userData=sessionManager.get_Authenticate_User()
        }

        var shareMsg = "Hey check out my profile on FSl."
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(RestClient.sharingUrl + "?userID=" + userID))
                .setDomainUriPrefix("https://nxplayr.page.link")

                // Open links with this app on Android
                .setAndroidParameters(
                        DynamicLink.AndroidParameters.Builder()

                                .build()
                )
                .setSocialMetaTagParameters(
                        DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle(context.getString(R.string.app_name))
                                .setDescription(shareMsg)
                                .setImageUrl(Uri.parse(RestClient.image_base_url_users + "appicon.png"))
                                .build()
                )
                 .setIosParameters(
                      DynamicLink.IosParameters.Builder("com.nxplayr.FSL")
                          .setFallbackUrl(Uri.parse("http://13.235.206.122/fsl/backend/web/"))
                          .setIpadFallbackUrl(Uri.parse("http://13.235.206.122/fsl/backend/web/"))

                          .build()
                  )
                .buildShortDynamicLink()


                .addOnSuccessListener { result ->
                    // Short link created
                    val shortLink = result.shortLink
                    //     val flowchartLink = result.previewLink


                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        type = "*/*"
                        putExtra(Intent.EXTRA_TEXT, "${shareMsg}\n${shortLink}")
                        if(imageUri!=null)
                            putExtra(Intent.EXTRA_STREAM,imageUri)
                    }
                    context.startActivityForResult(Intent.createChooser(sendIntent, context.getString(R.string.app_name)),1502)
                }.addOnFailureListener {
                    Log.d("shortUrl", it.toString())
                }

    }

}