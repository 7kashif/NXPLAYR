package com.nxplayr.fsl.ui.fragments.menu


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.onboarding.view.PartnerWithUSActivity
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.cms.view.CmsFragment
import com.nxplayr.fsl.ui.fragments.cms.view.ContactUsFragment
import com.nxplayr.fsl.ui.fragments.invite.view.InviteMainFragment
import com.nxplayr.fsl.ui.fragments.userconnection.view.AddConnectionsFragment
import com.nxplayr.fsl.ui.fragments.notification.view.NotificationFragment
import com.nxplayr.fsl.ui.fragments.main.view.HomeMainFragment
import com.nxplayr.fsl.ui.fragments.userprofile.view.ProfileFragment
import com.nxplayr.fsl.ui.fragments.ownprofile.view.ProfileMainFragment
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_menu.*


class MenuFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var mActivity: AppCompatActivity? = null
    var userData: SignupData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_menu, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)

        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
            if (userData != null) {
                setUserData()
            }
        }

        setupUI()
    }

    private fun setUserData() {
        image_profile.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)
        tv_name.text = userData?.userFirstName + " " + userData?.userLastName
        Log.d("USER_ID",userData!!.userID)
    }

    private fun setupUI() {
        tv_view_profile.setOnClickListener(this)
        close_icon.setOnClickListener(this)
        tv_get_VIP.setOnClickListener(this)
        tv_add_connections.setOnClickListener(this)
        tv_events.setOnClickListener(this)
        tv_groups.setOnClickListener(this)
        tv_ranking.setOnClickListener(this)
        tv_donate.setOnClickListener(this)
        tv_channels.setOnClickListener(this)
        layout_aboutUs.setOnClickListener(this)
        layout_termsConditions.setOnClickListener(this)
        layout_privacy.setOnClickListener(this)
        tv_more_info.setOnClickListener(this)
        tv_logout.setOnClickListener(this)
        layout_contactUs.setOnClickListener(this)
        ll_mainHome.setOnClickListener(this)
        ll_mainNotification.setOnClickListener(this)
        ll_mainSettings.setOnClickListener(this)
        ll_mainChat.setOnClickListener(this)
        ll_main_menu_profile.setOnClickListener(this)
        layout_invite_Contacts.setOnClickListener(this)
        layout_community_guidelines.setOnClickListener(this)
        layout_copyrightprivacy.setOnClickListener(this)
        layout_partner_invest.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.ll_mainHome -> {
                var bundle = Bundle()
                bundle.putInt("tab_position", 0)
                (activity as MainActivity).navigateTo(HomeMainFragment(), bundle, HomeMainFragment::class.java.name, true)
            }
            R.id.ll_mainChat -> {
                var bundle = Bundle()
                bundle.putInt("tab_position", 3)
                (activity as MainActivity).navigateTo(HomeMainFragment(), bundle, HomeMainFragment::class.java.name, true)
            }
            R.id.ll_mainNotification -> {
                (activity as MainActivity).navigateTo(NotificationFragment(), NotificationFragment::class.java.name, true)
            }
            R.id.ll_mainSettings -> {
                var bundle = Bundle()
                bundle.putInt("tabPosition", 4)
                (activity as MainActivity).navigateTo(ProfileMainFragment(), bundle, ProfileFragment::class.java.name, true)
            }
            R.id.tv_get_VIP -> {
//                (activity as MainActivity).navigateTo(OtherUserProfileMainFragment(), OtherUserProfileMainFragment::class.java.name, true)

//                MyUtils.startActivity(mActivity!!, AddEmploymenyActivity()::class.java, true)
            }
            R.id.tv_events -> {
               // (activity as MainActivity).navigateTo(EventMainFragment(), EventMainFragment::class.java.name, true)
            }
            R.id.tv_add_connections -> {
                (activity as MainActivity).navigateTo(AddConnectionsFragment(), AddConnectionsFragment::class.java.name, true)
//                (activity as MainActivity).navigateTo(WriteReviewFragment(), WriteReviewFragment::class.java.name, true)
//                MyUtils.startActivity(mActivity!!, AddEmploymenyActivity()::class.java, true)
            }
            R.id.tv_view_profile -> {

                (activity as MainActivity).navigateTo(ProfileMainFragment(), ProfileMainFragment::class.java.name, true)
            }
            R.id.ll_main_menu_profile -> {

                (activity as MainActivity).navigateTo(ProfileMainFragment(), ProfileMainFragment::class.java.name, true)
            }
            R.id.close_icon -> {
                (activity as MainActivity).navigateTo(HomeMainFragment(), HomeMainFragment::class.java.name, true)
            }
            R.id.tv_groups -> {
                //(activity as MainActivity).navigateTo(GroupFragment(), GroupFragment::class.java.name, true)
            }
            R.id.tv_more_info -> {

                if (menu_MoreInfoViewed.visibility != View.VISIBLE) {
                    tv_more_info.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.menu_icon11_more_info,
                            0,
                            R.drawable.menu_arrow_up,
                            0
                    )
                    MyUtils.expand(menu_MoreInfoViewed)
                } else {
                    tv_more_info.setCompoundDrawablesWithIntrinsicBounds(
                            R.drawable.menu_icon11_more_info,
                            0,
                            R.drawable.menu_arrow_down,
                            0
                    )

                    MyUtils.collapse(menu_MoreInfoViewed)
                }

            }
            R.id.layout_aboutUs -> {
                val bundle = Bundle()
                bundle.putString("title", getString(R.string.about_us))
                bundle.putString("cmsparameter", "About Us")
                (activity as MainActivity).navigateTo(CmsFragment(), bundle, CmsFragment::class.java.name, true)
            }
            R.id.layout_termsConditions -> {
                val bundle = Bundle()
                bundle.putString("title", getString(R.string.terms_condition))
                bundle.putString("cmsparameter", "Terms and Conditions")
                (activity as MainActivity).navigateTo(CmsFragment(), bundle, CmsFragment::class.java.name, true)

            }
            R.id.layout_privacy -> {
                val bundle = Bundle()
                bundle.putString("title", getString(R.string.privacy_policy))
                bundle.putString("cmsparameter", "Privacy Policy")
                (activity as MainActivity).navigateTo(CmsFragment(), bundle, CmsFragment::class.java.name, true)
            }
            R.id.layout_community_guidelines -> {
                val bundle = Bundle()
                bundle.putString("title", getString(R.string.community_guidelines))
                bundle.putString("cmsparameter", "Community Guidelines")
                (activity as MainActivity).navigateTo(CmsFragment(), bundle, CmsFragment::class.java.name, true)
            }
            R.id.layout_copyrightprivacy -> {
                val bundle = Bundle()
                bundle.putString("title", getString(R.string.copyright_policy))
                bundle.putString("cmsparameter", "Copyright Policy")
                (activity as MainActivity).navigateTo(CmsFragment(), bundle, CmsFragment::class.java.name, true)
            }
            R.id.layout_invite_Contacts -> {
                (activity as MainActivity).navigateTo(InviteMainFragment(), InviteMainFragment::class.java.name, true)
            }
            R.id.layout_contactUs -> {
                (activity as MainActivity).navigateTo(ContactUsFragment(), ContactUsFragment::class.java.name, true)
            }
            R.id.layout_partner_invest -> {

                Intent(mActivity, PartnerWithUSActivity::class.java).apply {
                    startActivity(this)
                }
            }
            R.id.tv_logout -> {
                MyUtils.showMessageOKCancel(mActivity!!,
                        "Are you sure want to logout?",
                        "Logout",
                        DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                            (activity as MainActivity).logOut()
                        })
            }


        }
    }
}
