package com.nxplayr.fsl.ui.fragments.otheruserprofile.view

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.MyUtils
import com.google.android.material.tabs.TabLayout
import com.nxplayr.fsl.ui.fragments.userprofile.view.ProfileFragment
import com.nxplayr.fsl.ui.fragments.otheruserprofile.adapter.OtherUserViewPagerAdapter
import com.nxplayr.fsl.ui.fragments.userprofile.view.CompactProfileFragment
import kotlinx.android.synthetic.main.fragment_other_user_profile_main.*
import kotlinx.android.synthetic.main.toolbar.*


class OtherUserProfileMainFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var adapter: OtherUserViewPagerAdapter? = null
    private val tabIcons = intArrayOf(R.drawable.profile_top_menu_icon1_unselected, com.nxplayr.fsl.R.drawable.profile_top_menu_icon2_unselected, com.nxplayr.fsl.R.drawable.profile_top_menu_icon3_unselected)
    var tab_position = 0
    var userId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_other_user_profile_main, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.profile)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        if (arguments != null) {
            userId = arguments!!.getString("userId").toString()
        }

        setupViewPager(viewPager_OtherProfile)
        tabLayout_otherProfile.setupWithViewPager(viewPager_OtherProfile)
        setupTabIcons()
        tabLayout_otherProfile.getTabAt(tab_position)?.icon?.setColorFilter(Color.parseColor("#00F0FF"), PorterDuff.Mode.SRC_IN)

        tabLayout_otherProfile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(Color.parseColor("#00F0FF"), PorterDuff.Mode.SRC_IN)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
    }

    private fun setupTabIcons() {
        tabLayout_otherProfile.getTabAt(0)?.setIcon(tabIcons[0])
        tabLayout_otherProfile.getTabAt(1)?.setIcon(tabIcons[1])
        tabLayout_otherProfile.getTabAt(2)?.setIcon(tabIcons[2])
    }

    private fun setupViewPager(viewPager: ViewPager) {
        if (adapter == null) {
            adapter = OtherUserViewPagerAdapter(childFragmentManager, userId, "OtherUserProfile", "other_user_profile")
            adapter?.addFragment(OtherUserProfileFragment(), "")
            adapter?.addFragment(CompactProfileFragment(), "")
            adapter?.addFragment(ProfileFragment(), "")
        }
        viewPager.offscreenPageLimit = 3
        viewPager.adapter = adapter
        viewPager.currentItem = tab_position

    }
}