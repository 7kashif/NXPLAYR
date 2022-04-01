package com.nxplayr.fsl.util


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.Window
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.gms.common.util.IOUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.custome_alert_dialog.*
import java.io.*
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Chandresh on 15/03/2017.
 */

@Suppress("DEPRECATION")
class MyUtils {

    enum class SharedPreferencesenum {
        languageId, Modeofbusiness
    }

    companion object {
        val GSTN_CODEPOINT_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val TEXT_TYPE = 0
        val Loder_TYPE = 1
        val TYPE_FULL = 0
        val TYPE_HALF = 1
        val TYPE_QUARTER = 2
        val TYPE_THIRD = 3
        val TYPE_HALF_H = 4
        val VIEWTYPE_ITEM = 1
        val VIEWTYPE_LOADER = 2
        val REQUEST_CAMERA_Profile = 101
        val REQUEST_GALLERY = 102
        var currentLongtiude = 0.00
        var currentLocation = ""
        var currentLattitude = 0.00
        var currentLongitude = 0.00
        var locationCityName: String = ""

        var currentLattitudeFix: Double = 0.0
        var currentLongitudeFix: Double = 0.0
        var locationCityNameFix: String = ""

        val Per_REQUEST_WRITE_EXTERNAL_STORAGE_1 = 201
        val Per_REQUEST_READ_EXTERNAL_STORAGE_1 = 202
        val Per_REQUEST_CAMERA_1 = 203
        var isMuteing = false
        var fbID = ""
        var linkedID = ""
        var instaID = ""
        var isSocial = false
        var user_Name = ""
        var user_Email = ""
        internal lateinit var userDialog: Dialog
        var progressDialog: ProgressHUD? = null

        var GlobarViewData = ""
        var GlobarViewSubData = "ShowData"
        var CollecationData = ""

        var isLoginForQuickBlockChat = false
        var isLoginForQuickBlock = false

        fun showDialogMessage(c: Context, title: String, body: String) {

            val builder = AlertDialog.Builder(c)
            builder.setTitle(title).setMessage(body).setNeutralButton("OK") { _dialog, which ->
                try {
                    userDialog.dismiss()

                } catch (e: Exception) {

                }
            }
            userDialog = builder.create()
            userDialog.show()
        }

        fun getStringSizeLengthFile(size: Long): String? {
            val df = DecimalFormat("0.00")
            val sizeKb = 1024.0f
            val sizeMb = sizeKb * sizeKb
            val sizeGb = sizeMb * sizeKb
            val sizeTerra = sizeGb * sizeKb
            if (size < sizeMb)
                return df.format((size / sizeKb).toDouble())
            else if (size < sizeGb)
                return df.format((size / sizeMb).toDouble())
            else if (size < sizeTerra)
                return df.format((size / sizeGb).toDouble())
            return ""
        }

        fun setViewAndChildrenEnabled(v: View, enabled: Boolean) {
            v.isEnabled = enabled
            if (v is ViewGroup) {

                for (i in 0 until v.childCount) {
                    var child = v.getChildAt(i)
                    setViewAndChildrenEnabled(child, enabled)
                }
            }
        }

        fun dismissDialog(context: Context, dialog: Dialog) {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    val cx = dialog.window!!.decorView.width / 2
                    val cy = dialog.window!!.decorView.height / 2


                    val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()


                    val anim = ViewAnimationUtils.createCircularReveal(
                        dialog.window!!.decorView,
                        cx,
                        cy,
                        initialRadius,
                        0f
                    )


                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {

                            dialog.dismiss()

                        }
                    })
                    anim.start()
                } else
                    dialog.dismiss()
            } catch (e: Exception) {
            }

        }


        /*    fun setSelectedModeTypeImageColor(context: Context, vimage: ArrayList<View>? = null, colorId: Int) {


                for (i in 0 until vimage!!.size) {
    //                vimage!![i].backgroundTintList = ColorStateList(context, colorId)

                }

            }*/

        fun setSelectedModeTypeViewColor(
            context: Context,
            vtext: ArrayList<View>? = null,
            colorId: Int
        ) {

            for (i in 0 until vtext!!.size) {
                when {

                    (vtext[i] is TextInputEditText) -> {
                        (vtext[i] as TextInputEditText).backgroundTintList =
                            ContextCompat.getColorStateList(context, colorId)
                        (vtext[i] as TextInputEditText).highlightColor =
                            ContextCompat.getColor(context, colorId)
                    }
                    (vtext[i] is EditText) -> DrawableCompat.setTint(
                        (vtext[i] as EditText).getBackground(),
                        ContextCompat.getColor(context, colorId)
                    )

                    vtext[i] is TextInputLayout -> {
                        (vtext[i] as TextInputLayout).defaultHintTextColor =
                            ContextCompat.getColorStateList(context, colorId)
                        (vtext[i] as TextInputLayout).backgroundTintList =
                            ContextCompat.getColorStateList(context, colorId)

                    }

                    vtext[i] is TextView -> (vtext[i] as TextView).setTextColor(
                        ContextCompat.getColor(
                            context,
                            colorId
                        )
                    )

                    vtext[i] is ImageView -> (vtext[i] as ImageView).setImageTintList(
                        ColorStateList.valueOf(
                            ContextCompat.getColor(context, colorId)
                        )
                    )


                    (vtext[i] is MaterialButton) -> {
                        (vtext[i] as MaterialButton).setBackgroundTintList(
                            ContextCompat.getColorStateList(
                                context,
                                colorId
                            )
                        )
                    }


                    vtext[i] is SimpleDraweeView -> (vtext[i] as SimpleDraweeView).setImageTintList(
                        ColorStateList.valueOf(ContextCompat.getColor(context, colorId))
                    )


                }

            }
        }


        /**
         * Function to convert milliseconds time to
         * Timer Format
         * Hours:Minutes:Seconds
         */
        fun milliSecondsToTimer(milliseconds: Long): String {
            var finalTimerString = ""
            var secondsString = ""

            // Convert total duration into time
            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
            // Add hours if there
            if (hours > 0) {
                finalTimerString = hours.toString() + ":"
            }

            // Prepending 0 to seconds if it is one digit
            if (seconds < 10) {
                secondsString = "0$seconds"
            } else {
                secondsString = "" + seconds
            }

            finalTimerString = "$finalTimerString$minutes:$secondsString"

            // return timer string
            return finalTimerString
        }

        /**
         * Function to get Progress percentage
         *
         * @param currentDuration
         * @param totalDuration
         */
        fun getProgressPercentage(currentDuration: Long, totalDuration: Long): Int {
            var percentage: Double? = 0.toDouble()

            val currentSeconds = (currentDuration / 1000).toInt().toLong()
            val totalSeconds = (totalDuration / 1000).toInt().toLong()

            // calculating percentage
            percentage = currentSeconds.toDouble() / totalSeconds * 100

            // return percentage
            return percentage.toInt()
        }

        /**
         * Function to change progress to timer
         *
         * @param progress      -
         * @param totalDuration returns current duration in milliseconds
         */
        fun progressToTimer(progress: Int, totalDuration: Int): Int {
            var totalDuration = totalDuration
            var currentDuration = 0
            totalDuration = totalDuration / 1000
            currentDuration = (progress.toDouble() / 100 * totalDuration).toInt()

            // return current duration in milliseconds
            return currentDuration * 1000
        }


//        companion object {
//            val Per_REQUEST_WRITE_EXTERNAL_STORAGE_1 = 1053
//            val Per_REQUEST_READ_EXTERNAL_STORAGE_1 = 1054
//            val Per_REQUEST_CAMERA_1 = 1057
//            val GSTINFORMAT_REGEX = "[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}"
//            val GSTN_CODEPOINT_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
//            val TEXT_TYPE = 0
//            val Loder_TYPE = 1
//            var menu_selection = 0
//            var LOG = true
//            var imageName = "snapzo.png"
//            var isIntentServiceRunning = false
//            var dialog: Dialog? = null
//        var progressDialog: ProgressHUD? = null

        fun expand(v: View) {
            v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val targtetHeight = v.measuredHeight

            v.layoutParams.height = 0
            v.visibility = View.VISIBLE
            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.height = if (interpolatedTime == 1f)
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    else
                        (targtetHeight * interpolatedTime).toInt()
                    v.requestLayout()

                    Log.d("System out", "Interpolated time : $interpolatedTime")

                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            a.duration =
                (targtetHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        }


        fun hideKeyboardFrom(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }


        fun showKeyboardWithFocus(v: View, a: Activity) {
            try {
                v.requestFocus()
                Handler().postDelayed({
                    val imm = a.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(
                        InputMethodManager.SHOW_FORCED,
                        InputMethodManager.HIDE_NOT_ALWAYS
                    )
                }, 50)
                // a.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun getPath1(uri: Uri, activity: Activity): String? {

            return getFilePathFromURI(activity, uri, createFileName(Date(), ""))

        }

        fun getFilePathFromURI(context: Context, contentUri: Uri, fileName: String?): String? {
            //copy file and send new file path

            if (!TextUtils.isEmpty(fileName)) {

                var copyFile = File(context.cacheDir, fileName!!)

                var copyFile1 = copy(context, contentUri, copyFile)
                return copyFile1?.absolutePath
            }
            return null
        }


        fun getFileName(uri: Uri?): String? {
            if (uri == null) return null
            var fileName: String? = null
            val path = uri.path
            val cut = path!!.lastIndexOf('/')
            if (cut != -1) {
                fileName = path.substring(cut + 1)
            }
            return fileName
        }

        fun copy(context: Context, srcUri: Uri, dstFile: File): File? {
            try {
                val inputStream = context.contentResolver.openInputStream(srcUri) ?: return null
                val outputStream = FileOutputStream(dstFile)
                IOUtils.copyStream(inputStream, outputStream)
                inputStream.close()
                outputStream.close()
                return dstFile
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return null

        }


        fun createFileName(today: Date, type: String = ""): String? {
            var filename: String? = null
            try {
                val dateFormatter = SimpleDateFormat("ddMMyyyyhhmmss")
                dateFormatter.isLenient = false
                //Date today = new Date();
                val s = dateFormatter.format(today)
                val min = 1
                val max = 1000

                val r = Random()
                val i1 = r.nextInt(max - min + 1) + min

                if (type.equals("Video", ignoreCase = true)) {
                    //   filename = "VIDEO_" + s + i1.toString() + ".mp4"
                    filename = s + i1.toString() + "post" + ".mp4"

                } else if (type.equals("Audio", ignoreCase = true)) {
                    filename = "Audio" + s + i1.toString() + ".mp3"
                } else {
                    filename = "IMG_" + s + i1.toString() + ".png"
                }

                //filename = "IMG_"+s+String.valueOf(i1)+".JPEG";

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return filename
        }

        fun createFileName(
            today: Date,
            type: String = "",
            imageHeight: Int,
            imageWidth: Int
        ): String? {
            var filename: String? = null
            try {
                val dateFormatter = SimpleDateFormat("ddMMyyyyhhmmss")
                dateFormatter.isLenient = false
                //Date today = new Date();
                val s = dateFormatter.format(today)
                val min = 1
                val max = 1000

                val r = Random()
                val i1 = r.nextInt(max - min + 1) + min

                if (type.equals("Video", ignoreCase = true)) {
                    filename =
                        s + i1.toString() + "post" + "_" + imageHeight.toString() + "X" + imageWidth.toString() + ".mp4"
                } else if (type.equals("Audio", ignoreCase = true)) {
                    filename = "Audio" + s + i1.toString() + ".mp3"
                } else {
                    filename =
                        s + i1.toString() + "post" + "_" + imageHeight.toString() + "X" + imageWidth.toString() + ".jpg"
                }

                //filename = "IMG_"+s+String.valueOf(i1)+".JPEG";

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return filename
        }

        fun createFileNameUser(
            today: Date,
            type: String = "",
            imageHeight: Int,
            imageWidth: Int
        ): String? {
            var filename: String? = null
            try {
                val dateFormatter = SimpleDateFormat("ddMMyyyyhhmmss")
                dateFormatter.isLenient = false
                //Date today = new Date();
                val s = dateFormatter.format(today)
                val min = 1
                val max = 1000

                val r = Random()
                val i1 = r.nextInt(max - min + 1) + min

                if (type.equals("Video", ignoreCase = true)) {
                    filename =
                        s + i1.toString() + "post" + "_" + imageHeight.toString() + "X" + imageWidth.toString() + ".mp4"
                } else if (type.equals("Audio", ignoreCase = true)) {
                    filename = "Audio" + s + i1.toString() + ".mp3"
                } else {
                    filename =
                        s + i1.toString() + "user" + "_" + imageHeight.toString() + "X" + imageWidth.toString() + ".jpg"
                }

                //filename = "IMG_"+s+String.valueOf(i1)+".JPEG";

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return filename
        }

        fun collapse(v: View) {
            val initialHeight = v.measuredHeight

            val a = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height =
                            initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

            a.duration =
                (initialHeight / v.context.resources.displayMetrics.density).toInt().toLong()
            v.startAnimation(a)
        }

        /**
         * Checks if the input parameter is a valid email.
         * **
         */


        fun isValidEmail(email: String): Boolean {
            val emailPattern =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            val matcher: Matcher?
            val pattern = Pattern.compile(emailPattern)

            matcher = pattern.matcher(email)

            return matcher?.matches() ?: false
        }

        fun isValidURL(webUrl: String): Boolean {
            val WebUrl =
                "^((ftp|http|https):\\/\\/)?(www.)?(?!.*(ftp|http|https|www.))[a-zA-Z0-9_-]+(\\.[a-zA-Z]+)+((\\/)[\\w#]+)*(\\/\\w+\\?[a-zA-Z0-9_]+=\\w+(&[a-zA-Z0-9_]+=\\w+)*)?$"
            val matcher: Matcher?
            val pattern = Pattern.compile(WebUrl)

            matcher = pattern.matcher(webUrl)

            return matcher?.matches() ?: false
        }


        /**
         * Checks if the input parameter is a valid Name
         * **
         */
        fun isValidName(name: String): Boolean {
            val pattern = Pattern.compile("^[a-zA-Z\\s]*$")
            val matcher = pattern.matcher(name)
            when {
                name.isEmpty() -> false
                matcher.matches() -> //if pattern matches
                    true
                else -> //if pattern does not matches
                    false
            }
            return false

        }


        // password validation check one lowercase,one uppercase,special symbol,numeric

        fun isValidPassword(pwd: String): Boolean {
//            val pattern = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{6,15}\$")

//            val pattern = Pattern.compile("^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{6,15}\$")
            val pattern =
                Pattern.compile("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[@${'$'}!%*#?&])[A-Za-z\d@${'$'}!%*#?&]{6,15}${'$'}""")

//            val pattern = Pattern.compile(  "^(?=.*[A-Za-z])(?=.*)(?=.*[@$!%*#?&])[A-Za-z@$!%*#?&]{6,15}$")
            val matcher = pattern.matcher(pwd)
            when {
                pwd.isEmpty() -> return false
                matcher.matches() -> //if pattern matches
                    return true
                else -> //if pattern does not matches
                    return false
            }
            return false
        }

        /**
         * hide soft keyboard
         * **
         */
        fun hideSoftKeyBoardOnTabClicked(context: Context?, v: View?) {
            if (v != null && context != null) {
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(
                    v.applicationWindowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }

        /**
         * get progressbar dialog
         * **
         */

        fun get_dialog(context: Context, layoutid: Int): Dialog {
            val dialog = Dialog(context)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(layoutid)
            dialog.window!!.setLayout(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT
            )
            dialog.window!!.setBackgroundDrawable(ColorDrawable(context.resources.getColor(android.R.color.transparent)))
            return dialog
        }

        /**
         * Get the correctly appended name from the given name parameters
         *
         * @param firstName First name
         * @param lastName  Last name
         * @return Returns correctly formatted full name. Returns null if both the values are null.
         * **
         */

        fun create_logcat(context: Context) {
            val fullName = context.getString(R.string.app_name) + "log.txt"
            val file = File(Environment.getExternalStorageDirectory(), fullName)

            // //clears a file
            if (file.exists()) {
                file.delete()
                clear_log()

            }
            //
            //
            // //write log to file
            try {
                val command = String.format("logcat -d")
                val process = Runtime.getRuntime().exec(command)

                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val result = StringBuilder()

                //
                while ((reader.readLine() != null)) {
                    //

                    result.append(reader.readLine())
                    result.append("\n")
                    //
                }
                //
                val out = FileWriter(file)
                out.write(result.toString())
                out.close()
                //
                //
            } catch (e: IOException) {
                // // Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            }
            //
        }

        fun clear_log() {
            //clear the log
            try {
                Runtime.getRuntime().exec("logcat -c")
            } catch (e: IOException) {
            }
        }

        fun show_message(msg: String, tv_msg: TextView) {

            tv_msg.text = msg
            tv_msg.clearAnimation()

            val fadeIn = AlphaAnimation(0f, 1f)
            fadeIn.interpolator = DecelerateInterpolator() //add this
            fadeIn.duration = 3000

            val fadeOut = AlphaAnimation(1f, 0f)
            fadeOut.interpolator = AccelerateInterpolator() //and this
            fadeOut.startOffset = 3000
            fadeOut.duration = 3000

            val animation = AnimationSet(false) //change to false
            animation.addAnimation(fadeIn)
            animation.addAnimation(fadeOut)

            animation.setAnimationListener(object : Animation.AnimationListener {

                override fun onAnimationEnd(animation: Animation) {
                    // TODO Auto-generated method stub
                    tv_msg.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {
                    // TODO Auto-generated method stub

                }

                override fun onAnimationStart(animation: Animation) {
                    // TODO Auto-generated method stub
                    tv_msg.visibility = View.VISIBLE

                }

            })
            tv_msg.animation = animation
        }

        /**
         * Checks if the Internet connection is available.
         *
         * @return Returns true if the Internet connection is available. False otherwise.
         * *
         */
        @SuppressLint("MissingPermission")
        fun isInternetAvailable(ctx: Context): Boolean {
            val check = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (check != null) {
                val info = check.allNetworkInfo
                if (info != null)
                    for (i in info.indices)
                        if (info[i].state == NetworkInfo.State.CONNECTED) {
                            return true
                            //Toast.makeText(context, "Internet is connected", Toast.LENGTH_SHORT).show();
                        }
            }
            return false
        }

        fun startActivity(
            oneActivity: Context,
            secondActivity: Class<*>,
            finishCurrentActivity: Boolean
        ) {
            val i = Intent(oneActivity, secondActivity)
            oneActivity.startActivity(i)
            if (finishCurrentActivity) (oneActivity as Activity).finish()
            (oneActivity as Activity).overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        fun startActivity(
            mode: String,
            title: String,
            oneActivity: Context,
            secondActivity: Class<*>,
            finishCurrentActivity: Boolean
        ) {
            val i = Intent(oneActivity, secondActivity)
            i.putExtra("mode", mode)
            i.putExtra("title", title)
            oneActivity.startActivity(i)
            if (finishCurrentActivity) (oneActivity as Activity).finish()
            (oneActivity as Activity).overridePendingTransition(
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        }

        fun getResourseId(
            context: Context,
            pVariableName: String,
            pResourseName: String,
            pPackageName: String
        ): Int {

            try {
                return context.resources.getIdentifier(pVariableName, pResourseName, pPackageName)

            } catch (e: Exception) {
                throw RuntimeException("Error getting ResoureId.", e)
            }

        }

        fun finishActivity(activity: Context, lefttorightanimation: Boolean) {
            (activity as Activity).finish()
            if (lefttorightanimation)
                activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            else
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)


        }

        fun showProgressDialog(activity: Context, message: String) {
            try {
                dismissProgressDialog()

                progressDialog =
                    ProgressHUD(activity, R.style.CustomBottomSheetDialogTheme, message)

                progressDialog?.setMessage(message)




                progressDialog?.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun showMessageYesNoListener(
            context: Context,
            message: String,
            title: String = "",
            okListener: DialogInterface.OnClickListener,
            NoListener: DialogInterface.OnClickListener
        ): Dialog {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setMessage(message)
            builder.setTitle(title)
            builder.setCancelable(false)

            builder.setPositiveButton("Yes", okListener)

            builder.setNegativeButton("No", NoListener)
            val alert = builder.create()
            alert.setOnShowListener {
                alert.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(context.resources.getColor(R.color.colorSecondary))
                alert.getButton(AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(context.resources.getColor(R.color.colorSecondary))
            }
            alert.show()

            return alert
        }


        fun versionUpdateDialog(
            context: Context,
            message: String,
            title: String = "",
            okListener: DialogInterface.OnClickListener,
            cancleListener: DialogInterface.OnClickListener

        ): MaterialAlertDialogBuilder {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setMessage(message)
            builder.setTitle(title)
            builder.setCancelable(false)
            builder.setPositiveButton("Ok", okListener)
            builder.setNegativeButton("Cancel", cancleListener)
            builder.show()
            return builder
        }


        /*   fun dismissProgressDialog() {
               try {
                   if (progressDialog != null && progressDialog!!.isShowing)
                       dismissDialog(progressDialog as Dialog)
               } catch (e: Exception) {
                   e.printStackTrace()
               }
           }*/
        fun dismissProgressDialog() {
            try {
                if (progressDialog != null)
                    progressDialog?.dismiss()
            } catch (e: Exception) {
            }
        }

        fun dismissDialog(dialog: Dialog) {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    val cx = dialog.window!!.decorView.width / 2
                    val cy = dialog.window!!.decorView.height / 2


                    val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()


                    val anim =
                        ViewAnimationUtils.createCircularReveal(
                            dialog.window!!.decorView,
                            cx,
                            cy,
                            initialRadius,
                            0f
                        )


                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {

                            dialog.dismiss()

                        }
                    })
                    anim.start()
                } else
                    dialog.dismiss()
            } catch (e: Exception) {
            }

        }


        fun startActivity(
            oneActivity: Context,
            secondActivity: Class<*>,
            finishCurrentActivity: Boolean,
            lefttorightanimation: Boolean
        ) {
            val i = Intent(oneActivity, secondActivity)
            oneActivity.startActivity(i)
            if (finishCurrentActivity)
                (oneActivity as Activity).finish()

            if (lefttorightanimation)
                (oneActivity as Activity).overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            else
                (oneActivity as Activity).overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )

        }


        fun isValidMobile(phone: String): Boolean {
            var check = false
            check = if (!Pattern.matches("[a-zA-Z]+", phone)) {
                !(phone.length < 9 || phone.length > 13)
            } else {
                false
            }
            return check
        }

        /*public static String convert_object_string(Object obj) {
Gson gson = new Gson();
String json = gson.toJson(obj); // myObject - instance of MyObject
return json;
}*/

        fun isVaildPassword(password: String): Boolean? {
            var check = false

            if (password.length >= 6) {
                check = true
            }
            return check
        }


        @Throws(ParseException::class)
        fun formatDate(date: String, initDateFormat: String, endDateFormat: String): String {
            val initDate = SimpleDateFormat(initDateFormat).parse(date.trim { it <= ' ' })
            val formatter = SimpleDateFormat(endDateFormat)
            return formatter.format(initDate)
        }

        fun calenderToString(calendar: Calendar): String {
            val date = Date(calendar.timeInMillis)
            val dateFormat = SimpleDateFormat("yyyy,MM")
            val strDate = dateFormat.format(date)
            return strDate
        }

        fun calenderToString1(calendar: Calendar): String {
            val date = Date(calendar.timeInMillis)
            val dateFormat = SimpleDateFormat("dd MMM yyyy")
            val strDate = dateFormat.format(date)
            return strDate
        }


        fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
            val width = bm.width
            val height = bm.height
            val scaleWidth = newWidth.toFloat() / width
            val scaleHeight = newHeight.toFloat() / height
            // CREATE A MATRIX FOR THE MANIPULATION
            val matrix = Matrix()
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight)

            // "RECREATE" THE NEW BITMAP
            val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false
            )
            bm.recycle()
            return resizedBitmap
        }


        fun dismissView(context: Context, view: View) {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    val cx = view.width / 2
                    val cy = view.height / 2


                    val initialRadius = Math.hypot(cx.toDouble(), cy.toDouble()).toFloat()


                    val anim =
                        ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0f)


                    anim.addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {

                            view.visibility = View.GONE

                        }
                    })
                    anim.start()
                } else
                    view.visibility = View.GONE

            } catch (e: Exception) {
            }

        }

        fun addPermission(context: Context, permission: String): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context!!.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
            return true
        }


        /*  public static String getDisplayableTime(long minute) throws ParseException {

  boolean isRemaining=false;
  if(minute<0)
      isRemaining=true;

  minute=Math.abs(minute);


  final long seconds = minute * 60;
  final long minutes = seconds / 60;
  final long hours = minutes / 60;
  final long days = hours / 24;
  final long months = days / 31;
  final long years = days / 365;


      if (seconds < 0)
      {
          return "not yet";
      } else if (seconds == 0) {
          return "Just now";
      } else if (seconds == 1) {
          return "Just now";
      } else if (seconds < 60 && seconds > 2)
      {
          if(!isRemaining)
          return seconds + " seconds ago";
          else
              return seconds + " seconds ";
      }
      *//*else if (seconds < 60 && seconds > 1) {
                return seconds == 2 ? " second ago" : seconds + " seconds ago";
            }*//*
            else if (seconds < 120) {
                if(!isRemaining)
                return "a minute ago";
                else
                    return "a minute remaining";
            } else if (seconds < 2700) // 45 * 60
            {
                if(!isRemaining)
                return minutes + " minutes ago";
                else
                    return minutes + " minutes ";
            } else if (seconds < 5400) // 90 * 60
            {
                if(!isRemaining)
                return "an hour ago";
                else
                    return "an hour ";
            } else if (seconds < 86400) // 24  60  60
            {
                if(!isRemaining)
                return hours + " hours ago";
                else
                    return hours + " hours ";
            }
           *//* else if (seconds < 172800) // 48  60  60
                if(!isRemaining)
                return "yesterday";
                else
                    return "tomorrow";
*//*
            else if (days >= 1 && days<366)
            {
                if(!isRemaining) {
                    if (days == 1)
                        return "1 day ";
                    else
                        return days + " days ";
                }
                else
                {
                    if (days == 1)
                        return "1 day ";
                    else
                        return days + " days ";
                }
            }
            else if (months >=1 && months<=12) {
                if(!isRemaining)
                    return months + " months ";
                else
                    return months + " months ";
            }
            else if (years >=1) {
                if(!isRemaining)
                return years + " years ";
                else
                    return years + " years ";

            }


       *//* else
        {
          if (seconds == 0) {
                return "Just now";
            } else if (seconds == 1) {
                return "Just now";
            } else if (seconds < 60 && seconds > 2) {
                return seconds + " seconds ";
            }
            *//**//*else if (seconds < 60 && seconds > 1) {
                return seconds == 2 ? " second ago" : seconds + " seconds ago";
            }*//**//*
            else if (seconds < 120) {
                return "a minute ";
            } else if (seconds < 2700) // 45 * 60
            {
                return minutes + " minutes ";
            } else if (seconds < 5400) // 90 * 60
            {
                return "an hour ago";
            } else if (seconds < 86400) // 24  60  60
            {
                return hours + " hours ";
            } else if (seconds < 172800) // 48  60  60
                return "yesterday";
            else if (days >= 1) {
                if (days == 1)
                    return "1 day ";
                else
                    return days + " days";
            }
          else if (months >=1) {

              return months + " months ";
          }
          else if (years >=1) {

              return years + " years ";
          }
        }
*//*
        return minute + " ago";
    }
*/


        fun diffDaysBetweenDates(startDate: String, endDate: String): Int {

            val sdf = SimpleDateFormat("EEE,dd MMM")

            val start: Date = sdf.parse(startDate)
            val end: Date = sdf.parse(endDate)


            return ((end.time - start.time) / (1000 * 60 * 60 * 24)).toInt()
        }

        fun diffOfDays(startDate: String, endDate: String): Int {
            try {
                val sdf = SimpleDateFormat("EEE,dd MMM HH:mm")
                return TimeUnit.DAYS.convert(
                    sdf.parse(endDate).time - sdf.parse(startDate).time,
                    TimeUnit.MILLISECONDS
                ).toInt()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return 0
            }

        }

        fun daysBetween(startDate: String, endDate: String): Int {
            val sdf = SimpleDateFormat("EEE,dd MMM hh:mm a")
            var sDate = sdf.parse(startDate)
            var eDate = sdf.parse(endDate)

            var daysBetween: Int = 0
            while (sDate.before(eDate)) {
                var cal = Calendar.getInstance()
                cal.time = sDate
                cal.add(Calendar.DAY_OF_MONTH, 1)
                sDate = cal.time
                daysBetween++
            }
            return daysBetween
        }


        fun checktimings(time: String, endtime: String): Boolean {

            var pattern = "HH:mm"
            var sdf = SimpleDateFormat(pattern)

            try {
                var d1 = sdf.parse(time)
                var d2 = sdf.parse(endtime)
                if (d1.compareTo(d2) > 0) {

                    // When Date d1 > Date d2
                    return false
                } else if (d1.compareTo(d2) < 0) {

                    // When Date d1 < Date d2
                    return true
                } else if (d1.compareTo(d2) == 0) {

                    // When Date d1 = Date d2
                    return false
                }


            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return false
        }

        @Throws(ParseException::class)
        fun getDisplayableTime(minute: Long): String {
            var minute = minute

            var isRemaining = false
            if (minute < 0)
                isRemaining = true

            minute = Math.abs(minute)


            val seconds = minute * 60
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 31
            val years = days / 365
            val week = days / 7


            if (seconds < 0) {
                return " - " + "not yet"
            } else if (seconds == 0L) {
                return " - " + "Just now"
            } else if (seconds == 1L) {
                return " - " + "Just now"
            } else if (seconds < 60 && seconds > 2) {
                return if (!isRemaining)
                    " - " + seconds.toString() + " sec"
                else
                    " - " + seconds.toString() + " sec"
            } else if (seconds < 120) {
                return if (!isRemaining)
                    " - " + "a min ago"
                else
                    " - " + "a min remaining"
            } else if (seconds < 2700)
            // 45 * 60
            {
                return if (!isRemaining)
                    " - " + minutes.toString() + " min"
                else
                    " - " + minutes.toString() + " min"
            } else if (seconds < 5400)
            // 90 * 60
            {
                return if (!isRemaining)
                    " - " + "h"
                else
                    " - " + "h"
            } else if (seconds < 86400)
            // 24  60  60
            {
                return if (!isRemaining)
                    " - " + hours.toString() + "h"
                else
                    " - " + hours.toString() + "h"
            } else if (days >= 1 && days <= 6) {
                return if (!isRemaining) {
                    if (days == 1L)
                        " - " + "1d "
                    else
                        " - " + days.toString() + "d"
                } else {
                    if (days == 1L)
                        " - " + "1d "
                    else
                        " - " + days.toString() + "d"
                }
            } else if (week >= 1) {
                return if (week == 1L)
                    " - " + week.toString() + "w"
                else
                    " - " + week.toString() + "w"
            }/* else if (seconds < 172800) // 48  60  60
                if(!isRemaining)
                return "yesterday";
                else
                    return "tomorrow";
*//*else if (seconds < 60 && seconds > 1) {
                return seconds == 2 ? " second ago" : seconds + " seconds ago";
            }*/
            /* else if (years >=1) {
        if(!isRemaining)
        return years + " years ";
        else
            return years + " years ";

    }
*/

            /* else
{
  if (seconds == 0) {
        return "Just now";
    } else if (seconds == 1) {
        return "Just now";
    } else if (seconds < 60 && seconds > 2) {
        return seconds + " seconds ";
    }
    *//*else if (seconds < 60 && seconds > 1) {
                return seconds == 2 ? " second ago" : seconds + " seconds ago";
            }//
            else if (seconds < 120) {
                return "a minute ";
            } else if (seconds < 2700) // 45 * 60
            {
                return minutes + " minutes ";
            } else if (seconds < 5400) // 90 * 60
            {
                return "an hour ago";
            } else if (seconds < 86400) // 24  60  60
            {
                return hours + " hours ";
            } else if (seconds < 172800) // 48  60  60
                return "yesterday";
            else if (days >= 1) {
                if (days == 1)
                    return "1 day ";
                else
                    return days + " days";
            }
          else if (months >=1) {

              return months + " months ";
          }
          else if (years >=1) {

              return years + " years ";
          }
        }
*/
            return minute.toString() + " ago"
        }

        fun getDisplayableTimeComment(minute: Long): String {
            var minute = minute

            var isRemaining = false
            if (minute < 0)
                isRemaining = true

            minute = Math.abs(minute)


            val seconds = minute * 60
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 31
            val years = days / 365
            val week = days / 7


            if (seconds < 0) {
                return "not yet"
            } else if (seconds == 0L) {
                return "Just now"
            } else if (seconds == 1L) {
                return "Just now"
            } else if (seconds < 60 && seconds > 2) {
                return if (!isRemaining)
                    seconds.toString() + " sec"
                else
                    seconds.toString() + " sec"
            } else if (seconds < 120) {
                return if (!isRemaining)
                    "a min ago"
                else
                    "a min remaining"
            } else if (seconds < 2700)
            // 45 * 60
            {
                return if (!isRemaining)
                    minutes.toString() + " min"
                else
                    minutes.toString() + " min"
            } else if (seconds < 5400)
            // 90 * 60
            {
                return if (!isRemaining)
                    "h"
                else
                    "h"
            } else if (seconds < 86400)
            // 24  60  60
            {
                return if (!isRemaining)
                    hours.toString() + "h"
                else
                    hours.toString() + "h"
            } else if (days >= 1 && days <= 6) {
                return if (!isRemaining) {
                    if (days == 1L)
                        "1d "
                    else
                        days.toString() + "d"
                } else {
                    if (days == 1L)
                        "1d "
                    else
                        days.toString() + "d"
                }
            } else if (week >= 1) {
                return if (week == 1L)
                    week.toString() + "w"
                else
                    week.toString() + "w"
            }/* else if (seconds < 172800) // 48  60  60
                if(!isRemaining)
                return "yesterday";
                else
                    return "tomorrow";
*//*else if (seconds < 60 && seconds > 1) {
                return seconds == 2 ? " second ago" : seconds + " seconds ago";
            }*/
            /* else if (years >=1) {
        if(!isRemaining)
        return years + " years ";
        else
            return years + " years ";

    }
*/

            /* else
{
  if (seconds == 0) {
        return "Just now";
    } else if (seconds == 1) {
        return "Just now";
    } else if (seconds < 60 && seconds > 2) {
        return seconds + " seconds ";
    }
    *//*else if (seconds < 60 && seconds > 1) {
                return seconds == 2 ? " second ago" : seconds + " seconds ago";
            }//
            else if (seconds < 120) {
                return "a minute ";
            } else if (seconds < 2700) // 45 * 60
            {
                return minutes + " minutes ";
            } else if (seconds < 5400) // 90 * 60
            {
                return "an hour ago";
            } else if (seconds < 86400) // 24  60  60
            {
                return hours + " hours ";
            } else if (seconds < 172800) // 48  60  60
                return "yesterday";
            else if (days >= 1) {
                if (days == 1)
                    return "1 day ";
                else
                    return days + " days";
            }
          else if (months >=1) {

              return months + " months ";
          }
          else if (years >=1) {

              return years + " years ";
          }
        }
*/
            return minute.toString() + " ago"
        }


        fun showMessageOKCancel(
            context: Context,
            message: String,
            title: String = "",
            okListener: DialogInterface.OnClickListener
        ): MaterialAlertDialogBuilder {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setMessage(message)
            builder.setTitle(title)
            builder.setCancelable(false)
            builder.setPositiveButton("Ok", okListener)
            builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
            builder.show()

            return builder
        }

        fun showMessageYesNoRound(
            context: Context,
            message: String,
            messagePositive: String,
            messageNagative: String,
            onYesClickListener: View.OnClickListener,
            isOKOnly: Boolean = false, title: String = ""
        ): Dialog {

            val builder = Dialog(context, R.style.CustomRoundAlertDailogStyle)
            builder.setContentView(R.layout.custome_alert_dialog)
            var window: Window = builder.window!!
            var width: Int = (context.resources.displayMetrics.widthPixels * 0.80).toInt()
            (context.resources.displayMetrics.heightPixels * 0.80)
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
            builder.setCancelable(false)
            builder.message.text = message
            builder.tv_yes.text = messagePositive

            if (!title.isNullOrEmpty()) {
                builder.title.text = title
                builder.title.visibility = View.VISIBLE
            } else {
                builder.title.visibility = View.GONE
            }
            builder.tv_no.text = messageNagative
            if (isOKOnly) {
                builder.tv_no.visibility = View.GONE
                builder.lineBR.visibility = View.GONE

            }
            builder.tv_yes.setOnClickListener {
                builder.dismiss()
                onYesClickListener.onClick(it)
            }
            builder.tv_no.setOnClickListener {
                builder.dismiss()
            }
            builder.show()
            return builder
        }


        fun showMessageYesNo(
            context: Context,
            message: String,
            title: String = "",
            okListener: DialogInterface.OnClickListener
        ): Dialog {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setMessage(message)
            builder.setTitle(title)
            builder.setCancelable(false)
            builder.setPositiveButton("Yes", okListener)
            builder.setNegativeButton("No") { dialog, which -> dialog.dismiss() }
            val alert = builder.create()
//            alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alert.show()

            return alert
        }

        fun showMessageOK(
            context: Context,
            message: String,
            okListener: DialogInterface.OnClickListener
        ): Dialog {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setMessage(message)
            builder.setCancelable(false)
            builder.setPositiveButton("Ok", okListener)


            val alert = builder.create()
//            alert.requestWindowFeature(Window.FEATURE_NO_TITLE)
            alert.show()

            return alert
        }


        fun ValidPANNumber(Pan: String): Boolean {
            val pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}")
            val matcher = pattern.matcher(Pan)
            return matcher.matches()

        }

        fun validateEmail(email: String): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            val EMAIL_PATTERN =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
            pattern = Pattern.compile(EMAIL_PATTERN)
            matcher = pattern.matcher(email)
            return matcher.matches()
        }

        fun hideKeyboard1(ctx: Context) {
            val inputManager = ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            // check if no view has focus:
            val v = (ctx as Activity).currentFocus ?: return

            inputManager.hideSoftInputFromWindow(v.windowToken, 0)
        }

        fun commaToArray(text: String): List<String> {

            return Arrays.asList(*text.split("\\s*,\\s*".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray())
        }

        /**
         * Method to check if a GSTIN is valid. Checks the GSTIN format and the
         * check digit is valid for the passed input GSTIN
         *
         * @param gstin
         * @return boolean - valid or not
         * @throws Exception
         */


        fun checkPattern(inputval: String, regxpatrn: String): Boolean {
            var result = false
            if (inputval.trim { it <= ' ' }.matches(regxpatrn.toRegex())) {
                result = true
            }
            return result
        }

        fun timeDifference(currentTime: Long, updateTime: Long): Long {
            var differenceTime = updateTime - currentTime
            var differenceTimemins = (differenceTime / (1000 * 60)) % 60

            return differenceTimemins
        }

        /**
         * Method to get the check digit for the gstin (without checkdigit)
         *
         * @param gstinWOCheckDigit
         * @return : GSTIN with check digit
         * @throws Exception
         */
        @Throws(Exception::class)
        fun getGSTINWithCheckDigit(gstinWOCheckDigit: String?): String {
            var factor = 2
            var sum = 0
            var checkCodePoint = 0
            var cpChars: CharArray?
            var inputChars: CharArray?

            try {
                if (gstinWOCheckDigit == null) {
                    throw Exception("GSTIN supplied for checkdigit calculation is null")
                }
                cpChars = GSTN_CODEPOINT_CHARS.toCharArray()
                inputChars = gstinWOCheckDigit.trim { it <= ' ' }.toUpperCase().toCharArray()

                val mod = cpChars.size
                for (i in inputChars.indices.reversed()) {
                    var codePoint = -1
                    for (j in cpChars.indices) {
                        if (cpChars[j] == inputChars[i]) {
                            codePoint = j
                        }
                    }
                    var digit = factor * codePoint
                    factor = if (factor == 2) 1 else 2
                    digit = digit / mod + digit % mod
                    sum += digit
                }
                checkCodePoint = (mod - sum % mod) % mod
                return gstinWOCheckDigit + cpChars[checkCodePoint]
            } finally {
                inputChars = null
                cpChars = null
            }
        }

        fun formatingPriceDisplay(price: String): String? {

            Log.d("System out", "price : " + price)

            val priceFormat = DecimalFormat("#,##,##0.00").format(price.toDouble())

            try {

                return priceFormat
            } catch (e: Exception) {

            }

            return ""
        }

        fun priceFormat(price: Double?): String {

            val priceFormat = price.toString()
            //        priceFormat = new DecimalFormat("#,##,##0.00").format(price);
            try {
                return String.format("%,.2f", price)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return priceFormat
        }

        fun discountFormat(price: Double?): Double {

            val priceFormat = price.toString()
            //        priceFormat = new DecimalFormat("#,##,##0.00").format(price);
            try {
                return "%.2f".format(price).toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return priceFormat.toDouble()
        }

        fun coordinatesFormat(price: Double?): String {

            val priceFormat = price.toString()
            //        priceFormat = new DecimalFormat("#,##,##0.00").format(price);
            try {
                return String.format("%,.4f", price)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return priceFormat
        }

        fun priceFormat(price: String): String {

            if (price.isNullOrEmpty())
                return "0.00"
            var priceFormat1 = price
            val priceFormat = java.lang.Double.parseDouble(price)

            try {
                priceFormat1 = DecimalFormat("#,##,###.00").format(priceFormat)
                return String.format("%,.2f", priceFormat)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return priceFormat1

        }


        @JvmStatic
        val EMAIL_REGEX =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"

        fun isEmailValid(email: String): Boolean {
            return EMAIL_REGEX.toRegex().matches(email)
        }


        fun showSnackbar(c: Context, msg: String, v: View) {
            val snackbar = Snackbar.make(v, msg, Snackbar.LENGTH_LONG)
            val sbView = snackbar.view

            sbView.setBackgroundColor(ContextCompat.getColor(c, R.color.red))
            snackbar.show()
        }


        fun CreateFileName(today: Date, type: String): String {
            var filename: String = ""
            try {
                val dateFormatter = SimpleDateFormat("dd_MM_yyyy_hh_mm_ss")
                dateFormatter.isLenient = false
                //Date today = new Date();
                val s = dateFormatter.format(today)
                val min = 1
                val max = 1000

                val r = Random()
                val i1 = r.nextInt(max - min + 1) + min

                if (type.equals("Video", ignoreCase = true)) {
                    filename = "VIDEO_$s$i1.mp4"
                } else if (type.equals("Audio", ignoreCase = true)) {
                    filename = "Audio$s$i1.mp3"
                } else {
                    filename = "IMG_$s$i1.jpg"
                }

                //filename = "IMG_"+s+String.valueOf(i1)+".JPEG";

            } catch (e: Exception) {
                e.printStackTrace()
            }

            return filename
        }

        @JvmStatic
        fun covertTimeToText(dataDate: String): String {
            var convTime: String = ""
            val prefix = ""
            val suffix = "Ago"
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val pasTime = dateFormat.parse(dataDate)
                val nowTime = Date()
                val dateDiff = nowTime.time - pasTime.time
                val second: Long = TimeUnit.MILLISECONDS.toSeconds(dateDiff)
                val minute: Long = TimeUnit.MILLISECONDS.toMinutes(dateDiff)
                val hour: Long = TimeUnit.MILLISECONDS.toHours(dateDiff)
                val day: Long = TimeUnit.MILLISECONDS.toDays(dateDiff)
                if (second < 60) {
                    convTime = second.toString() + "Sec"
                } else if (minute < 60) {
                    convTime = minute.toString() + "Min"
                } else if (hour < 24) {
                    convTime = hour.toString() + "h"
                } else if (day >= 7) {
                    var time: Int = 0
                    convTime = if (day > 360) {
                        time = (day / 30).toInt()
                        if (time.toString().equals("0", false)) {
                            "A Years " + suffix

                        } else {
                            time.toString() + " Years "

                        }
                    } else if (day > 30) {
                        time = (day / 360).toInt()
                        if (time.toString().equals("0", false)) {
                            "1mo"

                        } else {
                            time.toString() + "mo"

                        }
                    } else {
                        time = (day / 7).toInt()
                        if (time.toString().equals("0", false)) {
                            "1w" + suffix

                        } else {
                            time.toString() + "w"

                        }

                    }
                } else if (day < 7) {
                    convTime = "$day" + "d"
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return convTime
        }


        fun findTimeDifference(
            startDateTime: String,
            endDate: String,
            initDateFormat: String
        ): String {

//        val currentTime = SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(Date())
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
            val date1 = format.parse(
                MyUtils.formatDateNew(
                    startDateTime,
                    initDateFormat,
                    "dd/MM/yyyy HH:mm:ss a"
                )
            )
            val date2 = format.parse(
                MyUtils.formatDateNew(
                    endDate,
                    "HH:mm:ss dd/MM/yyyy",
                    "dd/MM/yyyy HH:mm:ss a"
                )
            )

            val different = date2.getTime() - date1.getTime();

            val secondsInMilli = (different / 1000).toInt()
            val minutesInMilli = (secondsInMilli / 60).toInt()
            val hoursInMilli = (different / (1000 * 60 * 60)).toInt()
            val daysInMilli = (hoursInMilli / 24).toInt()

            if (daysInMilli == 1) {
                return "$daysInMilli day ago"
//            return "$daysInMilli day ago"
            } else if (daysInMilli > 1) {
                return formatDate(startDateTime, "HH:mm:ss dd/MM/yyyy", "dd/MM/yyyy")
            } else if (hoursInMilli == 1) {
                return "$hoursInMilli hr ago"
            } else if (hoursInMilli > 1) {
                return "$hoursInMilli hrs ago"
            } else if (minutesInMilli == 1) {
                return "$minutesInMilli min ago"
            } else if (minutesInMilli > 1) {
                return "$minutesInMilli mins ago"
            } else if (minutesInMilli < 1) {
                return "Just now"
            } else return ""
        }


        fun formatDateNew(date: String, initDateFormat: String, endDateFormat: String): String {
            val initDate = SimpleDateFormat(initDateFormat).parse(date.trim { it <= ' ' })
            val formatter = SimpleDateFormat(endDateFormat)

            var str = formatter.format(initDate)

            if (str.contains("p.m.")) {
                str = str.replace("p.m.", "pm");
            } else if (str.contains("a.m.")) {
                str = str.replace("a.m.", "am");
            } else if (str.contains("P.M.")) {
                str = str.replace("P.M.", "PM");
            } else if (str.contains("A.M.")) {
                str = str.replace("A.M.", "AM");
            }

            return str
        }

        fun decodePushText(text1: String, context: Context): SpannableStringBuilder {
            var text = text1
// create the pattern matcher
            val m1 = Pattern.compile("###(.+?)###").matcher(text1)

            while (m1.find()) {
// get the match
// clear the string buffer
                text = text1.replace(m1.group(), "###" + m1.group(1).trim() + "###")
            }
            text = decodeShareText(text1, context)!!
            var ssb = SpannableStringBuilder(text1)
            val m = Pattern.compile("###(.+?)###").matcher(text1)


            var matchesSoFar = 0
// iterate through all matches
            while (m.find()) {
// get the match
// clear the string buffer
                var start = m.start() - (matchesSoFar * 6)
                var end = m.end() - (matchesSoFar * 6)
                ssb.setSpan(
                    ForegroundColorSpan(context.resources.getColor(R.color.colorPrimary)),
                    start + 3,
                    end - 3,
                    SPAN_EXCLUSIVE_EXCLUSIVE
                )
                ssb.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start + 3,
                    end - 3,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )


                ssb.delete(start, start + 3)
                ssb.delete(end - 6, end - 3)
                matchesSoFar++
            }
            return ssb
        }


        fun decodeShareText(
            text: String,
            context: Context
        ): String? {
            var text = text
            Log.d("hashText", text)
            if (text.contains("<Shase>")) {
                text = text.replace("<Shase>".toRegex(), "#")
                text = text.replace("<Chase>".toRegex(), "")
            }
            val ssb = SpannableStringBuilder(text)
            val MY_PATTERN = Pattern.compile("#(\\w+)")
            val mat = MY_PATTERN.matcher(text)
            while (mat.find()) {
                Log.d("match", mat.group())
                if (mat.group(1) != null && mat.group(1).length >= 1) {
                    ssb.setSpan(
                        null,
                        mat.start(),
                        mat.start() + mat.group().length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    ssb.setSpan(
                        ForegroundColorSpan(context.resources.getColor(R.color.colorPrimary)),
                        mat.start(),
                        mat.start() + mat.group().length,
                        0
                    )
                    ssb.setSpan(
                        StyleSpan(Typeface.BOLD),
                        mat.start(),
                        mat.start() + mat.group().length,
                        0
                    )
                }
            }
            // create the pattern matcher
            val m =
                Pattern.compile("<SatRate>(.+?)<CatRate>").matcher(text)
            var matchesSoFar = 0
            // iterate through all matches
            while (m.find()) { // get the match
                if (m.group().contains(",")) {
                    val word = m.group()
                    // remove the first and last characters of the match
                    val friendTagName = word.substring(9, word.indexOf(","))
                    var id: String? = null
                    var friendId = ""
                    if (word.contains(",")) {
                        friendId = word.substring(word.indexOf(",") + 2, word.length - 9)
                    }
                    if (word.contains(",")) {
                        id = word.substring(word.indexOf(","), word.indexOf("<CatRate>"))
                    }
                    // clear the string buffer
                    val start = m.start() - matchesSoFar
                    val end = m.end() - matchesSoFar
                    ssb.delete(start, start + 9)
                    val start1 = start + 9
                    val end1 = start1 + friendTagName.length - 9
                    ssb.delete(end1, end - 9)
                    matchesSoFar = matchesSoFar + 18 + id!!.length
                }
            }
            return ssb.toString()
        }
    }


}





