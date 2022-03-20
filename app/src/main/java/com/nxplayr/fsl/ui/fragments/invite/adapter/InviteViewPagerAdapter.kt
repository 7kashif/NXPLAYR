package com.nxplayr.fsl.ui.fragments.invite.adapter

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.nxplayr.fsl.R

class InviteViewPagerAdapter(var activity: AppCompatActivity, manager: FragmentManager) : FragmentPagerAdapter(manager) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt("position", position)

        if (position == 0)
            bundle.putString("type", activity.resources.getString(R.string.share))
        else if (position == 1)
            bundle.putString("type", activity.resources.getString(R.string.sms))
        else if (position == 2)
            bundle.putString("type", activity.resources.getString(R.string.mail))

        val fragment = mFragmentList[position]
        fragment.arguments = bundle
        return fragment
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position].capitalize()
    }

    override fun saveState(): Parcelable? {
        return null
    }
}