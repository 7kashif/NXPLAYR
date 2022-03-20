package com.nxplayr.fsl.util

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.TextView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.application.MyApplication
import kotlinx.android.synthetic.main.nointernetconnection.view.*

class ErrorUtil {
    companion object {
        fun errorMethod(view: View) {
            try {
                if (!MyUtils.isInternetAvailable(MyApplication.instance)) {
                    MyUtils.showSnackbar(MyApplication.instance, MyApplication.instance.resources.getString(R.string.error_common_network), view)
                } else {
                    MyUtils.showSnackbar(MyApplication.instance, MyApplication.instance.resources.getString(R.string.error_crash_error_message), view)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun TextView.errorMessage(msg: String) {
            try {
                this.visibility = View.VISIBLE
                this.text = msg
                var height = this.height
                var tv = this
                this.post {
                    val animate = TranslateAnimation(0f, 0f, height.toFloat(), 0f)
                    animate.duration = 1500


                    this.startAnimation(animate)
                    animate.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationRepeat(p0: Animation?) {

                        }

                        override fun onAnimationStart(p0: Animation?) {

                        }

                        override fun onAnimationEnd(p0: Animation?) {
                            val animate = TranslateAnimation(0f, 0f, 0f, height.toFloat())
                            animate.duration = 1500
                            animate.startOffset = 1500
                            animate.fillAfter = true
                            tv.startAnimation(animate)
                            tv.visibility = View.GONE

                        }
                    })

                }
            } catch (e: Exception) {
            }

        }

        fun errorView(activity: Activity, nointernetMainRelativelayout: View) {

            try {
                nointernetMainRelativelayout.visibility = View.VISIBLE
                if (MyUtils.isInternetAvailable(activity)) {
                    nointernetMainRelativelayout.nointernetImageview.setImageDrawable(activity.getDrawable(R.drawable.ic_warning_black_24dp))
                    nointernetMainRelativelayout.nointernettextview.text = activity.getString(R.string.error_crash_error_message)
//                    nointernetMainRelativelayout.nointernettextview1.text = activity.getString(R.string.error_crash_error_message)
                } else {
                    nointernetMainRelativelayout.nointernetImageview.setImageDrawable(activity.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                    nointernetMainRelativelayout.nointernettextview.text = activity.getString(R.string.error_common_network)
//                    nointernetMainRelativelayout.nointernettextview1.text = activity.getString(R.string.internetmsg1)

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }
}