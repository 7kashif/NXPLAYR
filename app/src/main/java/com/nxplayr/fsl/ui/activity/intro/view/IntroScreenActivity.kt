package com.nxplayr.fsl.ui.activity.intro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.intro.adapter.UltraPagerAdapter
import com.nxplayr.fsl.data.model.IntroStaticDataPojo
import com.nxplayr.fsl.ui.activity.onboarding.view.SignInActivity
import com.nxplayr.fsl.ui.activity.onboarding.view.SignupActivity
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.activity_intro_screen.*


class IntroScreenActivity : AppCompatActivity(), View.OnClickListener {


    val list: MutableList<IntroStaticDataPojo> = ArrayList()

    override fun onClick(v: View?) {
        when (v?.getId()) {
            R.id.introLoginButton -> {
                MyUtils.startActivity(this, SignInActivity::class.java, true, true)
            }
            R.id.introSignupButton -> {
                MyUtils.startActivity(this, SignupActivity::class.java, true, true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro_screen)
        setupUI()


    }

    private fun setupUI() {
        introLoginButton.setOnClickListener(this)
        introSignupButton.setOnClickListener(this)
        setUpPager()
    }


    private fun setUpPager() {

        list.clear()
        list.add(IntroStaticDataPojo(resources.getString(R.string.less_business_more_social), resources.getString(R.string.connect_yourself_to_catch_nopportunities_amp_achieve_your_goals), resources.getDrawable(R.drawable.intro_slider_img1)))
        list.add(IntroStaticDataPojo(resources.getString(R.string.dream_big_show_talent), resources.getString(R.string.intro_screen2_subtitle), resources.getDrawable(R.drawable.intro_slider_img2)))
        list.add(IntroStaticDataPojo(resources.getString(R.string.intro_title_3), resources.getString(R.string.intro_screen3_subtitle), resources.getDrawable(R.drawable.intro_slider_img3)))
        list.add(IntroStaticDataPojo(resources.getString(R.string.intro_title_4), resources.getString(R.string.intro_screen4_subtitle), resources.getDrawable(R.drawable.intro_slider_img4)))
        list.add(IntroStaticDataPojo(resources.getString(R.string.intro_title_5), resources.getString(R.string.intro_screen5_subtitle), resources.getDrawable(R.drawable.intro_slider_img5)))
        list.add(IntroStaticDataPojo(resources.getString(R.string.intro_title_6), resources.getString(R.string.intro_screen6_subtitle), resources.getDrawable(R.drawable.intro_slider_img6)))
        list.add(IntroStaticDataPojo(resources.getString(R.string.intro_title_7), resources.getString(R.string.intro_screen7_subtitle), resources.getDrawable(R.drawable.intro_slider_img7)))
        list.add(IntroStaticDataPojo(resources.getString(R.string.intro_title_8), resources.getString(R.string.intro_screen8_subtitle), resources.getDrawable(R.drawable.intro_slider_img8)))
        list.add(IntroStaticDataPojo(resources.getString(R.string.intro_title_9), resources.getString(R.string.intro_screen9_subtitle), resources.getDrawable(R.drawable.intro_slider_img9)))
        list.add(IntroStaticDataPojo(resources.getString(R.string.intro_title_10), resources.getString(R.string.intro_screen10_subtitle), resources.getDrawable(R.drawable.intro_slider_img10)))


        intro_viewpager.startAutoScroll()
        intro_viewpager.interval = 5000
        intro_viewpager.isCycle = true
        intro_viewpager.isStopScrollWhenTouch = true

        val adapter = UltraPagerAdapter(true, list)
        intro_viewpager.adapter = adapter
        tab_layout.setupWithViewPager(intro_viewpager)

    }

}
