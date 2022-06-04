package com.nxplayr.fsl.ui.fragments.ownprofile.view

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.CreatePostPhotoPojo
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.friendrequest.view.AcceptRequestFragment
import com.nxplayr.fsl.ui.fragments.invite.view.InviteMainFragment
import com.nxplayr.fsl.ui.fragments.notification.view.NotificationFragment
import com.nxplayr.fsl.ui.fragments.ownprofile.adapter.OwnProfileViewPagerAdapter
import com.nxplayr.fsl.ui.fragments.setting.SettingFragment
import com.nxplayr.fsl.ui.fragments.userprofile.view.CompactProfileFragment
import com.nxplayr.fsl.ui.fragments.userprofile.view.ProfileFragment
import com.nxplayr.fsl.ui.fragments.userprofile.view.UserDetailFragment
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_profile_main.*
import kotlinx.android.synthetic.main.toolbar.*


@Suppress("DEPRECATION")
class ProfileMainFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var adapter: OwnProfileViewPagerAdapter? = null
    var mActivity: AppCompatActivity? = null
    private val tabIcons = intArrayOf(
        com.nxplayr.fsl.R.drawable.profile_top_menu_icon1_unselected,
        com.nxplayr.fsl.R.drawable.profile_top_menu_icon2_unselected,
        com.nxplayr.fsl.R.drawable.profile_top_menu_icon3_unselected,
        com.nxplayr.fsl.R.drawable.profile_top_menu_icon4_unselected,
        com.nxplayr.fsl.R.drawable.profile_top_menu_icon5_unselected,
        com.nxplayr.fsl.R.drawable.profile_top_menu_icon6_unselected
    )
    var tab_position = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(com.nxplayr.fsl.R.layout.fragment_profile_main, container, false)
        return v
    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngProfile.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngProfile
            if (!sessionManager?.LanguageLabel?.lngInvite.isNullOrEmpty())
                menuToolbarInvite.text = sessionManager?.LanguageLabel?.lngInvite
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            tab_position = arguments!!.getInt("tabPosition", 0)
        }
        setupUI()
    }

//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        sessionManager = SessionManager(mActivity!!)
//        if (sessionManager?.get_Authenticate_User() != null) {
//            userData = sessionManager?.get_Authenticate_User()
//        }
//        if (arguments != null) {
//            tab_position = arguments!!.getInt("tabPosition", 0)
//        }
//        setupUI()
//    }

    private fun setupUI() {
        toolbar.visibility = View.VISIBLE
        tab_layout_profile.visibility = View.VISIBLE
        tvToolbarTitle.text = getString(com.nxplayr.fsl.R.string.profile)
        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        menuToolbarInvite.visibility = View.VISIBLE
        toolbar.visibility = View.VISIBLE
        ll_mainProfileTab.visibility = View.VISIBLE
        viewPagerProfile.visibility = View.VISIBLE

        tv_notifications.setOnClickListener(this)
        menuToolbarInvite.setOnClickListener(this)

        setupViewPager(viewPagerProfile)
        tab_layout_profile.setupWithViewPager(viewPagerProfile)
        setupTabIcons()
        tab_layout_profile.getTabAt(tab_position)?.icon?.setColorFilter(
            Color.parseColor("#00F0FF"),
            PorterDuff.Mode.SRC_IN
        )
        tab_layout_profile.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(Color.parseColor("#00F0FF"), PorterDuff.Mode.SRC_IN)
                viewPagerProfile.setCurrentItem(tab!!.position, true)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.icon?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }

    private fun setupTabIcons() {
        tab_layout_profile.getTabAt(0)?.setIcon(tabIcons[0])
        tab_layout_profile.getTabAt(1)?.setIcon(tabIcons[1])
        tab_layout_profile.getTabAt(2)?.setIcon(tabIcons[2])
        tab_layout_profile.getTabAt(3)?.setIcon(tabIcons[3])
        tab_layout_profile.getTabAt(4)?.setIcon(tabIcons[4])
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    private fun setupViewPager(viewPager: ViewPager) {
        if (adapter == null) {
            adapter = OwnProfileViewPagerAdapter(childFragmentManager, userData?.userID!!)
            adapter?.addFragment(UserDetailFragment(), "")
            adapter?.addFragment(CompactProfileFragment(), "")
            adapter?.addFragment(ProfileFragment(), "")
            adapter?.addFragment(AcceptRequestFragment(), "")
            adapter?.addFragment(SettingFragment(), "")
        }
        viewPager.offscreenPageLimit = 5
        viewPager.adapter = adapter
        viewPager.currentItem = tab_position

    }

    fun isPogress(isVisiBle: Boolean) {
        for (frag1 in childFragmentManager.fragments) {
            if (frag1 is UserDetailFragment) {
                frag1.isPogress(isVisiBle)
            }

        }
    }

    fun createPost(
        from: String,
        datumList: ArrayList<CreatePostPhotoPojo>,
        stringExtraDes: String,
        stringExtraPrivcy: String,
        Location: String?,
        latitude: String?,
        longitude: String?,
        tag: String?,
        VideoThumb: ArrayList<CreatePostPhotoPojo>,
        radioText: String?,
        connectionTypeIDs: String?
    ) {

        for (frag1 in childFragmentManager.fragments) {
            if (frag1 is UserDetailFragment) {
                frag1.createPost(
                    from,
                    datumList,
                    stringExtraDes,
                    stringExtraPrivcy,
                    Location,
                    latitude,
                    longitude,
                    tag,
                    VideoThumb,
                    radioText,
                    connectionTypeIDs!!
                )
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((activity as MainActivity).getCurrentFragment() is ProfileMainFragment) {
            (activity as MainActivity).getCurrentFragment()!!
                .onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.menuToolbarInvite -> {
                (activity as MainActivity).navigateTo(
                    InviteMainFragment(),
                    InviteMainFragment::class.java.name,
                    true
                )
            }
            R.id.tv_notifications -> {
                toolbar.visibility = View.GONE
                ll_mainProfileTab.visibility = View.GONE
                viewPagerProfile.visibility = View.GONE
                (activity as MainActivity).navigateTo(
                    NotificationFragment(),
                    NotificationFragment::class.java.name,
                    true
                )
            }
        }
    }
}

