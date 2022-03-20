package com.nxplayr.fsl.ui.fragments.explorepost.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.google.android.material.tabs.TabLayout
import com.nxplayr.fsl.ui.fragments.collection.view.CollectionsFragment
import kotlinx.android.synthetic.main.explore_main_activity.*
import java.util.*

class ExploreAllFragment: Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    var adapter: ViewPagerExploreAdapter?=null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       if(v==null){
           v = inflater.inflate(R.layout.explore_main_activity, container, false)

       }
        return v

    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        img_back_explore_all.setOnClickListener {

            (mActivity as MainActivity).onBackPressed()
        }

        tabLayout = v!!.findViewById(R.id.tabLayout)
        viewPager = v!!.findViewById(R.id.viewPager)

        tabLayout.addTab(tabLayout.newTab().setText("Explore"))
        tabLayout.addTab(tabLayout.newTab().setText("Collections"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        setupViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }
    private fun setupViewPager(viewPager: ViewPager) {
            adapter = ViewPagerExploreAdapter(childFragmentManager)
            adapter?.addFragment(ExploreInbuildFragment(), "Explore")
            adapter?.addFragment(CollectionsFragment(), "Collections")
            viewPager.adapter = adapter
    }

    inner class ViewPagerExploreAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {

            val bundle = Bundle()
            bundle.putInt("position", position)
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


}