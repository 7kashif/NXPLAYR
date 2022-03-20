package com.nxplayr.fsl.ui.activity.post.view

import android.Manifest
import android.annotation.TargetApi
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.post.adapter.CreatePostOneMainRVAdapter
import com.nxplayr.fsl.data.model.CreatePostOneMainRVPojo
import com.nxplayr.fsl.util.LocationProvider
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.toolbar_main.*

@Suppress("DEPRECATED_IDENTITY_EQUALS")
class CreatePostActivity : AppCompatActivity(), View.OnClickListener {

    private var imageTextMainRVCreatePostOnePojo: ArrayList<CreatePostOneMainRVPojo> = ArrayList()
    private var adapterOneMain: CreatePostOneMainRVAdapter? = null
    private val REQUEST_CODE_LOCATION_PERMISSIONS = 6
    var LATITUDE: Double? = null
    var LONGITUDE: Double? = null
    private lateinit var gridLayoutManager: GridLayoutManager
    private var locationProvider: LocationProvider? = null
    var isLocationGot: Boolean = false
    private val currentApiVersion = Build.VERSION.SDK_INT


    private val mYourBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // now you can call all your fragments method here
            MyUtils.finishActivity(this@CreatePostActivity, true)
        }
    }
    private val permissionsRequestStorage = 99
    private val permissionsRequestStorage1 = 999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        setupUI()
    }

    private fun setupUI() {
        ivCloseCreatePostOne.setOnClickListener(this)
        LocalBroadcastManager.getInstance(this@CreatePostActivity)
            .registerReceiver(mYourBroadcastReceiver, IntentFilter("CreatePost"))

        add()
        createPostRvLinearLayout.setOnClickListener(this)
        createPostRvLinearLayout2.setOnClickListener(this)

        gridLayoutManager = GridLayoutManager(this@CreatePostActivity, 2)
        adapterOneMain = CreatePostOneMainRVAdapter(this, imageTextMainRVCreatePostOnePojo,
            object : CreatePostOneMainRVAdapter.OnItemClickListener {
                @TargetApi(Build.VERSION_CODES.M)
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onClicked(imageTextMainRVCreatePostOnePojo: ArrayList<CreatePostOneMainRVPojo>, position: Int) {
                    if (imageTextMainRVCreatePostOnePojo[position].itemName == "Place")
                    {
                        if (currentApiVersion >= Build.VERSION_CODES.M) {
                            permissionLocation()
                        } else {
                            connectLocation()
                        }

                    }
                    else if (imageTextMainRVCreatePostOnePojo[position].itemName == "Video" || imageTextMainRVCreatePostOnePojo[position].itemName == "Photo")
                    {
                        val intent = Intent(this@CreatePostActivity, TransperentActivity::class.java)
                        intent.putExtra("From", imageTextMainRVCreatePostOnePojo[position].itemName)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                    } else if (imageTextMainRVCreatePostOnePojo[position].itemName == "Link") {
                        checkPermissionLink("Link")
                    } else if (imageTextMainRVCreatePostOnePojo[position].itemName == "Document") {
                        checkPermission("Gallery")
                    }
                    else {
                        val intent = Intent(this@CreatePostActivity, CreatePostActivityTwo::class.java)
                        intent.putExtra("From", imageTextMainRVCreatePostOnePojo[position].itemName)
                        startActivity(intent)
                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)

                    }
                }
            })

        createPostRV!!.layoutManager = gridLayoutManager
        createPostRV!!.adapter = adapterOneMain
    }


    private fun add() {
        imageTextMainRVCreatePostOnePojo.add(CreatePostOneMainRVPojo("Photo", R.drawable.create_post_photo))
        imageTextMainRVCreatePostOnePojo.add(CreatePostOneMainRVPojo("Video", R.drawable.create_post_video))
        imageTextMainRVCreatePostOnePojo.add(CreatePostOneMainRVPojo("Document", R.drawable.create_post_document))
        imageTextMainRVCreatePostOnePojo.add(CreatePostOneMainRVPojo("Link", R.drawable.create_post_link))
    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@CreatePostActivity, false)
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionLocation(): Boolean {

        if (!addPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val message = getString(R.string.grant_access_location)

            MyUtils.showMessageOKCancel(this@CreatePostActivity, message, "Use Location Service?",
                    DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                        requestPermissions(
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                REQUEST_CODE_LOCATION_PERMISSIONS
                        )
                    })

        } else {
            connectLocation()
        }
        return true
    }

    private fun addPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) !== PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            REQUEST_CODE_LOCATION_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connectLocation()
                } else {

                    Toast.makeText(this, resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
                return
            }
            permissionsRequestStorage -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this@CreatePostActivity, CreatePostActivityTwo::class.java)
                    intent.putExtra("From", "Document")
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                } else {

                    Toast.makeText(this, resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
                return
            }
            permissionsRequestStorage1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val intent = Intent(this@CreatePostActivity, WebPostActivity::class.java)
                    intent.putExtra("From", "Link")
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
                } else {

                    Toast.makeText(this, resources.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        when (requestCode) {

            LocationProvider.CONNECTION_FAILURE_RESOLUTION_REQUEST -> {
                connectLocation()
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }


    private fun selectPlace() {
        val string = "Place"
        val intent = Intent(this@CreatePostActivity, CreatePostSelectPlaceActivity::class.java)
        intent.putExtra("From", string)
        intent.putExtra("latitude", LATITUDE)
        intent.putExtra("longitude", LONGITUDE)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
    }

    private fun connectLocation() {
        locationProvider = LocationProvider(this@CreatePostActivity, LocationProvider.HIGH_ACCURACY,
                object : LocationProvider.CurrentLocationCallback {
                    override fun handleNewLocation(location: Location) {
                         locationProvider?.disconnect()
                        if (location != null) {
                            Log.d("currentLocation", location.toString())
                            locationProvider?.disconnect()
                            LATITUDE = location.latitude
                            LONGITUDE = location.longitude
                            isLocationGot = true
                            selectPlace()
                        }
                    }
                })

        locationProvider!!.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@CreatePostActivity)
                .unregisterReceiver(mYourBroadcastReceiver)
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
        } else
        {

            val intent = Intent(this@CreatePostActivity, CreatePostActivityTwo::class.java)
            intent.putExtra("From", "Document")
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            return true
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun checkPermissionLink(s:String):Boolean {
        if (ContextCompat.checkSelfPermission(this@CreatePostActivity,Manifest.permission.READ_EXTERNAL_STORAGE) + ContextCompat
                        .checkSelfPermission(
                                this.applicationContext!!,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) !== PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
            ) {
                requestPermissions(
                        arrayOf(
                                Manifest.permission
                                        .READ_EXTERNAL_STORAGE,  Manifest.permission
                                .WRITE_EXTERNAL_STORAGE
                        ),
                        permissionsRequestStorage1
                )

            } else {
                requestPermissions(
                        arrayOf(
                                Manifest.permission
                                        .READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        permissionsRequestStorage1
                )
            }
            return false
        } else
        {
            val intent = Intent(this@CreatePostActivity, WebPostActivity::class.java)
            intent.putExtra("From", "Link")
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
            return true
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.ivCloseCreatePostOne->{
                onBackPressed()

            }
            R.id.createPostRvLinearLayout->{
                val intent2 = Intent("CreatePost")
                intent2.putExtra("from", "Connection")
                LocalBroadcastManager.getInstance(this@CreatePostActivity)
                    .sendBroadcast(intent2)
            }
            R.id.createPostRvLinearLayout2->{
                val intent2 = Intent("CreatePost")
                intent2.putExtra("from", "Invite")
                LocalBroadcastManager.getInstance(this@CreatePostActivity)
                    .sendBroadcast(intent2)
            }
        }
    }


}
