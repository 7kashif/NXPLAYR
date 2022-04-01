package com.nxplayr.fsl.ui.activity.intro.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.intro.adapter.UltraPagerAdapter
import com.nxplayr.fsl.data.model.IntroStaticDataPojo
import com.nxplayr.fsl.ui.activity.onboarding.view.SignInActivity
import com.nxplayr.fsl.ui.activity.onboarding.view.SignupActivity
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_intro_screen.*


class IntroScreenActivity : AppCompatActivity(), View.OnClickListener {

    var sessionManager: SessionManager? = null
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
        initData()
        setupUI()
    }

    private fun initData() {
        sessionManager = SessionManager(this@IntroScreenActivity)
    }

    private fun setupUI() {
        introLoginButton.setOnClickListener(this)
        introSignupButton.setOnClickListener(this)
        setUpPager()
    }


    private fun setUpPager() {

        list.clear()
        if (sessionManager?.LanguageLabel != null) {
            // static data from preference
            val langLabel = sessionManager?.LanguageLabel

            if (!langLabel?.lngIntroTitle1.isNullOrEmpty() && !langLabel?.lngIntroDetail1.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle1.toString(),
                        langLabel?.lngIntroDetail1.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img1)
                    )
                )
            }

            if (!langLabel?.lngIntroTitle2.isNullOrEmpty() && !langLabel?.lngIntroDetail2.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle2.toString(),
                        langLabel?.lngIntroDetail2.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img2)
                    )
                )
            }

            if (!langLabel?.lngIntroTitle3.isNullOrEmpty() && !langLabel?.lngIntroDetail3.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle3.toString(),
                        langLabel?.lngIntroDetail3.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img3)
                    )
                )
            }

            if (!langLabel?.lngIntroTitle4.isNullOrEmpty() && !langLabel?.lngIntroDetail4.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle4.toString(),
                        langLabel?.lngIntroDetail4.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img4)
                    )
                )
            }

            if (!langLabel?.lngIntroTitle5.isNullOrEmpty() && !langLabel?.lngIntroDetail5.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle5.toString(),
                        langLabel?.lngIntroDetail5.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img5)
                    )
                )
            }

            if (!langLabel?.lngIntroTitle6.isNullOrEmpty() && !langLabel?.lngIntroDetail6.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle6.toString(),
                        langLabel?.lngIntroDetail6.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img6)
                    )
                )
            }

            if (!langLabel?.lngIntroTitle7.isNullOrEmpty() && !langLabel?.lngIntroDetail7.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle7.toString(),
                        langLabel?.lngIntroDetail7.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img7)
                    )
                )
            }

            if (!langLabel?.lngIntroTitle8.isNullOrEmpty() && !langLabel?.lngIntroDetail8.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle8.toString(),
                        langLabel?.lngIntroDetail8.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img8)
                    )
                )
            }

            if (!langLabel?.lngIntroTitle9.isNullOrEmpty() && !langLabel?.lngIntroDetail9.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle9.toString(),
                        langLabel?.lngIntroDetail9.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img9)
                    )
                )
            }

            if (!langLabel?.lngIntroTitle10.isNullOrEmpty() && !langLabel?.lngIntroDetail10.isNullOrEmpty()) {
                list.add(
                    IntroStaticDataPojo(
                        langLabel?.lngIntroTitle10.toString(),
                        langLabel?.lngIntroDetail10.toString(),
                        ContextCompat.getDrawable(this, R.drawable.intro_slider_img10)
                    )
                )
            }

            if (!langLabel?.lngSignin.isNullOrEmpty()) {
                introLoginButton.progressText = langLabel?.lngSignin.toString()
            }

            if (!langLabel?.lngSignup.isNullOrEmpty()) {
                introSignupButton.progressText = langLabel?.lngSignup.toString()
            }

        } else {
            // static data from string files
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.less_business_more_social),
                    resources.getString(R.string.connect_yourself_to_catch_nopportunities_amp_achieve_your_goals),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img1)
                )
            )
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.dream_big_show_talent),
                    resources.getString(R.string.intro_screen2_subtitle),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img2)
                )
            )
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.intro_title_3),
                    resources.getString(R.string.intro_screen3_subtitle),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img3)
                )
            )
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.intro_title_4),
                    resources.getString(R.string.intro_screen4_subtitle),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img4)
                )
            )
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.intro_title_5),
                    resources.getString(R.string.intro_screen5_subtitle),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img5)
                )
            )
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.intro_title_6),
                    resources.getString(R.string.intro_screen6_subtitle),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img6)
                )
            )
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.intro_title_7),
                    resources.getString(R.string.intro_screen7_subtitle),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img7)
                )
            )
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.intro_title_8),
                    resources.getString(R.string.intro_screen8_subtitle),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img8)
                )
            )
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.intro_title_9),
                    resources.getString(R.string.intro_screen9_subtitle),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img9)
                )
            )
            list.add(
                IntroStaticDataPojo(
                    resources.getString(R.string.intro_title_10),
                    resources.getString(R.string.intro_screen10_subtitle),
                    ContextCompat.getDrawable(this, R.drawable.intro_slider_img10)
                )
            )
        }

        intro_viewpager.startAutoScroll()
        intro_viewpager.interval = 5000
        intro_viewpager.isCycle = true
        intro_viewpager.isStopScrollWhenTouch = true

        val adapter = UltraPagerAdapter(true, list)
        intro_viewpager.adapter = adapter
        tab_layout.setupWithViewPager(intro_viewpager)
    }
}
