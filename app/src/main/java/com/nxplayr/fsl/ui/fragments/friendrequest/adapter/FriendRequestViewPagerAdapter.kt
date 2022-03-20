package com.nxplayr.fsl.ui.fragments.friendrequest.adapter

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.ArrayList

class FriendRequestViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {

        val bundle = Bundle()
        bundle.putInt("position", position)

        if (position == 0)
            bundle.putString("type", "Receive Requests")
        else if (position == 1)
            bundle.putString("type", "Sent Requests")

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

//        override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
//            super.restoreState(state, loader)
//        }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }

}