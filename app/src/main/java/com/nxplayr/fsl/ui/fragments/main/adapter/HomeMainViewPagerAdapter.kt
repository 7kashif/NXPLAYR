package com.nxplayr.fsl.ui.fragments.main.adapter

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeMainViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager,1) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {

        val bundle = Bundle()
        bundle.putInt("position", position)

        if (position == 0)
            bundle.putString("type", "")
        else if (position == 1)
            bundle.putString("type", "")
        else if (position == 2)
            bundle.putString("type", "")
        else if (position == 3)
            bundle.putString("type", "")
        else if (position == 4)
            bundle.putString("type", "")
        else if (position == 5)
            bundle.putString("type", "")


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