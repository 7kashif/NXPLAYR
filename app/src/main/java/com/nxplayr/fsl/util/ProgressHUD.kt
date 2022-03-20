package com.nxplayr.fsl.util

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.nxplayr.fsl.R


class ProgressHUD(context: Context, themeResId: Int,message: CharSequence?) : Dialog(context, themeResId) {
    lateinit var tvMsg: TextView
    internal var message: CharSequence =""


    init {
        init(message)
    }


    private fun init(message: CharSequence?) {
        setTitle("")

        setContentView(R.layout.custom_progressbar1)
        setCancelable(false)
        tvMsg = findViewById<View>(R.id.message) as TextView
        if (message == null || message.length == 0) {
            tvMsg.visibility = View.GONE
        } else {

            tvMsg.text = message
        }

        window!!.attributes.gravity = Gravity.CENTER
        val lp = window!!.attributes
        lp.dimAmount = 0.2f
        window!!.attributes = lp
    }



    /* public void onWindowFocusChanged(boolean hasFocus) {
        ProgressBar imageView = (ProgressBar) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }*/

    fun setMessage(message: CharSequence?) {
        if (message != null && message.isNotEmpty()) {

            tvMsg.text = message
            tvMsg.invalidate()
        }
    }




}
