package com.nxplayr.fsl.ui.fragments.ownprofile.adapter

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class OwnProfileViewPagerAdapter (manager: FragmentManager,val userID:String) : FragmentPagerAdapter(manager) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {

        val bundle = Bundle()
        bundle.putInt("position", position)
        bundle.putString("fromProfile","MyProfile")
        bundle.putString("userID",userID)
        if (position == 0)
            bundle.putString("type", "FollowersDetailFragment")
        else if (position == 1)
            bundle.putString("type", "CompactProfileFragment")
        else if (position == 2)
            bundle.putString("type", "ProfileFragment")
        else if (position == 3)
            bundle.putString("type", "AcceptRequestFragment")
        else if (position == 4)
            bundle.putString("type", "SettingFragment")
        else if (position == 5)
            bundle.putString("type", "NotificationFragment")

        val fragment = mFragmentList[position]
        fragment.arguments = bundle

        return fragment
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun saveState(): Parcelable? {
        return null
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }

}