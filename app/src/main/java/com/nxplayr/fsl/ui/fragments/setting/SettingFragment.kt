package com.nxplayr.fsl.ui.fragments.setting


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.blockeduser.view.BlockedUsersFragment
import com.nxplayr.fsl.ui.fragments.setting.view.PrivacyFragment
import com.nxplayr.fsl.ui.fragments.setting.view.RequestVerificationFragment
import com.nxplayr.fsl.ui.fragments.feed.view.SavedPostsFragment
import com.nxplayr.fsl.ui.fragments.setting.view.SuggestedFeedbackFragment
import com.nxplayr.fsl.ui.fragments.setting.view.*
import com.nxplayr.fsl.ui.fragments.userlanguage.view.ContentLanguage
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        //if (v == null) {
            v = inflater.inflate(R.layout.fragment_setting, container, false)
       // }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        tv_myaccount.setOnClickListener (this)
        tv_privacy.setOnClickListener (this)
        tv_notification_setting.setOnClickListener(this)
        tv_saved_posts.setOnClickListener (this)
        tv_hidden_posts.setOnClickListener(this)
        tv_block_users.setOnClickListener (this)
        tv_language.setOnClickListener (this)
        tv_change_password.setOnClickListener (this)
        tv_delete_account.setOnClickListener (this)
        tv_suggested_feature.setOnClickListener (this)
        tv_req_verification.setOnClickListener (this)
        tv_content_language.setOnClickListener (this)
        tv_setting.setOnClickListener(this)
        tv_send_feedback.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
           R.id.tv_myaccount->{
                (activity as MainActivity).navigateTo(MyAccountFragment(), MyAccountFragment::class.java.name, true)

            }
            R.id. tv_privacy-> {
                (activity as MainActivity).navigateTo(PrivacyFragment(), PrivacyFragment::class.java.name, true)
            }

            R.id.tv_notification_setting->{
                (activity as MainActivity).navigateTo(NotificationSettingFragment(), NotificationSettingFragment::class.java.name, true)
            }
            R.id.tv_saved_posts-> {
                (activity as MainActivity).navigateTo(SavedPostsFragment(), SavedPostsFragment::class.java.name, true)
            }
             R.id.tv_hidden_posts-> {
                (activity as MainActivity).navigateTo(HidePostsFragment(), HidePostsFragment::class.java.name, true)
            }
            R.id.tv_block_users-> {
                (activity as MainActivity).navigateTo(BlockedUsersFragment(), BlockedUsersFragment::class.java.name, true)
            }
            R.id.tv_language-> {

                (activity as MainActivity).navigateTo(LanguageFragment(), LanguageFragment::class.java.name, true)

            }
            R.id.tv_change_password-> {
                (activity as MainActivity).navigateTo(ChangePasswordFragment(), ChangePasswordFragment::class.java.name, true)
            }
            R.id.tv_delete_account-> {
                (activity as MainActivity).navigateTo(DeleteAccountFragment(), DeleteAccountFragment::class.java.name, true)
            }
            R.id.tv_suggested_feature-> {
                (activity as MainActivity).navigateTo(SuggestedFeedbackFragment(), SuggestedFeedbackFragment::class.java.name, true)

            }
            R.id.tv_req_verification-> {
                (activity as MainActivity).navigateTo(RequestVerificationFragment(), RequestVerificationFragment::class.java.name, true)

            }
            R.id.tv_content_language-> {
                (activity as MainActivity).navigateTo(ContentLanguage(), ContentLanguage::class.java.name, true)

            }
            R.id.tv_send_feedback-> {
                (activity as MainActivity).navigateTo(SendFeedbackFragment(), SendFeedbackFragment::class.java.name, true)

            }
        }
    }

}
