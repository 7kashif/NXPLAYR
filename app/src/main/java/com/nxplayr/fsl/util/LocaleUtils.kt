package com.nxplayr.fsl.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.webkit.WebView
import java.util.*

class LocaleUtils {

     companion object {
         fun updateConfig( context: Context, language: String): Context {


             val locale = Locale(language,"MA")
             Locale.setDefault(locale)

             val res = context.resources
             val config = Configuration(res.configuration)
             try {
                 WebView(context).destroy()
             } catch (e: Exception) {
             }

             if (Build.VERSION.SDK_INT >= 17) {
                 config.setLocale(locale)
                 return context.createConfigurationContext(config)
             } else {
                 config.locale = locale
                 res.updateConfiguration(config, res.displayMetrics)
                 return context
             }
         }
     }
}