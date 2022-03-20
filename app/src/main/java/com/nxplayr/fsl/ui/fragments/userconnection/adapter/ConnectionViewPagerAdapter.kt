package com.nxplayr.fsl.ui.fragments.userconnection.adapter

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.nxplayr.fsl.ui.fragments.userconnection.view.ConnectionListFragment

class ConnectionViewPagerAdapter(
    manager: FragmentManager,
    var from: String,
    var userId: String,
    var viewcount: ArrayList<String?>?,
    var tabIcons: IntArray
) : FragmentStatePagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {

        val bundle = Bundle()
        var fragment: Fragment? = null
        bundle.putInt("position", position)
        bundle.putString("userID", userId)
        bundle.putString("fromData", from)

        when (position) {
            0 -> {
                bundle.putString("type", "All 250")
                fragment = ConnectionListFragment()
            }
            1 -> {
                bundle.putString("type", "150")
                fragment = ConnectionListFragment()
            }
            2 -> {
                bundle.putString("type", "100")
                fragment = ConnectionListFragment()
            }
            3 -> {
                bundle.putString("type", "50")
                fragment = ConnectionListFragment()
            }
        }
        fragment!!.arguments = bundle
        return fragment!!
    }

    override fun getPageTitle(position: Int): CharSequence {
        return viewcount?.get(position)!!
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun getCount(): Int {
        return tabIcons.size
    }
}