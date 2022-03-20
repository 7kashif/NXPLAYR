package com.nxplayr.fsl.ui.activity.post.view

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.activity_transperent.*
import kotlinx.android.synthetic.main.layout_select_dialog_camera.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransperentActivity : Activity(), View.OnClickListener {

    private val currentApiVersion = Build.VERSION.SDK_INT
    private val permissionsRequestStorage = 99
    var listFromPastActivity=""

    var from=""
    var type=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transperent)

        listFromPastActivity = if (intent != null && intent.hasExtra("From"))
            intent?.extras!!.getString("From", "")
        else ""

        type = if (intent != null && intent.hasExtra("type"))
            intent?.extras!!.getString("type", "")
        else ""

        setupUI()
    }

    private fun setupUI() {

        btnCloseSelection.setOnClickListener (this)
        img_select_Camera.setOnClickListener(this)
        img_select_Gallery.setOnClickListener(this)
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun checkPermission(s:String):Boolean {
        if (ContextCompat.checkSelfPermission(this.applicationContext!!,Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat.checkSelfPermission(this.applicationContext!!, Manifest.permission.CAMERA) + ContextCompat
                        .checkSelfPermission(
                                this.applicationContext!!,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) !== PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.CAMERA
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
            ) {
                requestPermissions(
                        arrayOf(
                                Manifest.permission
                                        .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission
                                .WRITE_EXTERNAL_STORAGE
                        ),
                        permissionsRequestStorage
                )

            } else {
                requestPermissions(
                        arrayOf(
                                Manifest.permission
                                        .READ_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        permissionsRequestStorage
                )
            }
            return false
        } else {
            if (listFromPastActivity == "Photo") {
                if(s.equals("Gallery",false)){

                    val intent = Intent(this@TransperentActivity, CreatePostActivityTwo::class.java)
                    intent.putExtra("From", listFromPastActivity)
                    intent.putExtra("type","Gallery")
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    finish()

                    /*val intent = Intent(this@TransperentActivity, AlbumSelectActivity::class.java)
                    //set limit on number of images that can be selected, default is 10
                    intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5)
                    startActivityForResult(intent, 1211)*/

                }
                else if(s.equals("CameraPhoto",false)){
                    val intent = Intent(this@TransperentActivity, CreatePostActivityTwo::class.java)
                    intent.putExtra("From", listFromPastActivity)
                    intent.putExtra("type","CameraPhoto")

                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    finish()
                    /*try {
                        intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(intent, 1212)
                    } catch (e: Exception) {
                        Toast.makeText(
                                this@TransperentActivity, "Please install a File Manager.",
                                Toast.LENGTH_SHORT
                        ).show()
                        e.printStackTrace()
                    }*/
                }
             }
            else if (listFromPastActivity == "Video") {
                if(s.equals("Gallery",false)){

                    val intent = Intent(this@TransperentActivity, CreatePostActivityTwo::class.java)
                    intent.putExtra("From", listFromPastActivity)
                    intent.putExtra("type","Gallery")
                    intent.putExtra("type1",type)

                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    finish()

                    /*val intent = Intent(this@TransperentActivity, AlbumSelectActivity::class.java)
                    //set limit on number of images that can be selected, default is 10
                    intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5)
                    startActivityForResult(intent, 1211)*/

                }
            else if(s.equals("CameraPhoto",false)){
                    val intent = Intent(this@TransperentActivity, CreatePostActivityTwo::class.java)
                    intent.putExtra("From", listFromPastActivity)
                    intent.putExtra("type","CameraPhoto")
                    intent.putExtra("type1",type)
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    finish()
                }

            }

            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            permissionsRequestStorage -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (listFromPastActivity == "Photo") {
                        if(from.equals("Gallery",false)){

                            val intent = Intent(this@TransperentActivity, CreatePostActivityTwo::class.java)
                            intent.putExtra("From", listFromPastActivity)
                            intent.putExtra("type","Gallery")
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                            finish()

                            /*val intent = Intent(this@TransperentActivity, AlbumSelectActivity::class.java)
                            //set limit on number of images that can be selected, default is 10
                            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5)
                            startActivityForResult(intent, 1211)*/

                        }
                        else if(from.equals("CameraPhoto",false)){
                            val intent = Intent(this@TransperentActivity, CreatePostActivityTwo::class.java)
                            intent.putExtra("From", listFromPastActivity)
                            intent.putExtra("type","CameraPhoto")
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                            finish()
                            /*try {
                                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, 1212)
                            } catch (e: Exception) {
                                Toast.makeText(
                                        this@TransperentActivity, "Please install a File Manager.",
                                        Toast.LENGTH_SHORT
                                ).show()
                                e.printStackTrace()
                            }*/
                        }
                    }
                    else if (listFromPastActivity == "Video") {
                        if(from.equals("Gallery",false)){

                            val intent = Intent(this@TransperentActivity, CreatePostActivityTwo::class.java)
                            intent.putExtra("From", listFromPastActivity)
                            intent.putExtra("type","Gallery")
                            intent.putExtra("type1",type)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                            finish()

                            /*val intent = Intent(this@TransperentActivity, AlbumSelectActivity::class.java)
                            //set limit on number of images that can be selected, default is 10
                            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5)
                            startActivityForResult(intent, 1211)*/

                        }
                        else if(from.equals("CameraPhoto",false)){
                            val intent = Intent(this@TransperentActivity, CreatePostActivityTwo::class.java)
                            intent.putExtra("From", listFromPastActivity)
                            intent.putExtra("type","CameraPhoto")
                            intent.putExtra("type1",type)
                            startActivity(intent)
                            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                            finish()
                            /*try {
                                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                startActivityForResult(intent, 1212)
                            } catch (e: Exception) {
                                Toast.makeText(
                                        this@TransperentActivity, "Please install a File Manager.",
                                        Toast.LENGTH_SHORT
                                ).show()
                                e.printStackTrace()
                            }*/
                        }

                    }
                } else {

                    Toast.makeText(this, resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@TransperentActivity, false)
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }


    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnCloseSelection->{
                onBackPressed()

            }
            R.id.img_select_Camera->{
                img_select_Camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_selected_popup))
                img_select_Gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon))

                if (currentApiVersion >= Build.VERSION_CODES.M) {
                    from="CameraPhoto"
                    checkPermission("CameraPhoto")
                }

            }
            R.id.img_select_Gallery->{

                img_select_Gallery.setImageDrawable(resources.getDrawable(R.drawable.gallery_icon_selected_popup))
                img_select_Camera.setImageDrawable(resources.getDrawable(R.drawable.camera_icon_unselected_popup))

                if (currentApiVersion >= Build.VERSION_CODES.M) {
                    from="Gallery"
                    checkPermission("Gallery")
                }
            }
        }
    }

}