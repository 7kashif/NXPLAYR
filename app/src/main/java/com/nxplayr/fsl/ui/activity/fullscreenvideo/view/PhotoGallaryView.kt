package com.nxplayr.fsl.ui.activity.fullscreenvideo.view

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.adapter.PhotogalleryAdapterr
import com.nxplayr.fsl.data.model.Albummedia
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_photo_gallery.*
import java.util.*

class PhotoGallaryView: AppCompatActivity(),View.OnClickListener {


    var sessionManager : SessionManager? = null
    var userData : SignupData? = null
    var photogalleryAdapterr: PhotogalleryAdapterr?=null
    var albummedia=ArrayList<Albummedia>()
    var page_position = 0
    private var dots: Array<ImageView?>? = null
    private var dotsCount: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_photo_gallery)

        sessionManager = SessionManager(this@PhotoGallaryView!!)
        if (sessionManager != null && sessionManager!!.isLoggedIn()){
            userData = sessionManager?.get_Authenticate_User()
        }
        if (getIntent() != null) {
            albummedia= getIntent().getSerializableExtra("photoUri") as ArrayList<Albummedia>
        }
        setupUI()
    }

    private fun setupUI() {
        viewpager.adapter = PhotogalleryAdapterr(this@PhotoGallaryView!!, albummedia)
        if (! albummedia.isNullOrEmpty() &&  albummedia?.size!! > 1) {
            addBottomDots(0, layoutDotsTutorial)
            layoutDotsTutorial.visibility = View.VISIBLE
        } else {
            layoutDotsTutorial.visibility = View.GONE
        }
        viewpager?.setCurrentItem(0,true)
        viewpager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                page_position = position

            }

            override fun onPageSelected(position: Int) {
                page_position = position
                addBottomDots(position, layoutDotsTutorial!!)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        imgLoginCloseIcon.setOnClickListener(this)
    }


    override fun onBackPressed() {
        MyUtils.finishActivity(this@PhotoGallaryView, true)

    }

    fun addBottomDots(currentPage: Int, layoutDotsTutorial: LinearLayout) {
        // dotsCount = stichingServicData?.stitchingserviceImages!!.size
        //  dotsCount=trendingFeedDatum1?.postSerializedData?.get(0)?.albummedia!!.size
        dotsCount = if ( albummedia.isNullOrEmpty()) 0 else  albummedia?.size!!

        layoutDotsTutorial.removeAllViews()

        dots = arrayOfNulls(dotsCount)

        for (i in 0 until dotsCount) {

            dots!![i] = ImageView(this!!)
            dots!![i]?.setImageResource(R.drawable.default_dot)
            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(4, 0, 4, 0)
            dots!![i]?.setPadding(0, 0, 0, 0)
            dots!![i]?.layoutParams = params
            layoutDotsTutorial.addView(dots!![i]!!, params)
            layoutDotsTutorial.bringToFront()

        }
        if (dots!!.isNotEmpty() && dots!!.size > currentPage) {
            dots!![currentPage]?.setImageResource(R.drawable.selected_dot)
        }

    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.imgLoginCloseIcon->{
                onBackPressed()
            }
        }
    }

}