package com.nxplayr.fsl.ui.fragments.userprofile.adapter

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.ArrayList

class UserDetailViewPagerAdapter(manager: FragmentManager, var fromData: String) : FragmentStatePagerAdapter(manager) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {

        val bundle = Bundle()
        bundle.putInt("position", position)
        bundle.putString("from", fromData)

        if (position == 0)
            bundle.putString("type", "All")
        else if (position == 1)
            bundle.putString("type", "MyExplore")
        else if (position == 2)
            bundle.putString("type", "Photo")
        else if (position == 3)
            bundle.putString("type", "Video")
        else if (position == 4)
            bundle.putString("type", "Document")
        else if (position == 5)
            bundle.putString("type", "Link")


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

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        super.restoreState(state, loader)
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mFragmentTitleList[position]
    }

}