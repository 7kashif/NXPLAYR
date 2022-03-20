package com.nxplayr.fsl.ui.activity.chat.utils

import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import com.nxplayr.fsl.application.MyApplication

@IntDef(Toast.LENGTH_LONG, Toast.LENGTH_SHORT)
private annotation class ToastLength

fun shortToast(@StringRes text: Int) {
    shortToast(MyApplication.instance.getString(text))
}

fun shortToast(text: String?) {
    show(text, Toast.LENGTH_SHORT)
}

fun longToast(@StringRes text: Int) {
    longToast(MyApplication.instance.getString(text))
}

fun longToast(text: String) {
    show(text, Toast.LENGTH_LONG)
}

private fun show(text: String?, @ToastLength length: Int) {
    text?.let {
        makeToast(it, length).show()
    }
}

private fun makeToast(text: String, @ToastLength length: Int): Toast {
    return Toast.makeText(MyApplication.instance, text, length)
}