package com.nxplayr.fsl.ui.activity.onboarding.adapter

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.viewpager.widget.PagerAdapter

class CommonPagerAdapter : PagerAdapter() {
    private val pageIds: ArrayList<Int>? = ArrayList()
    fun insertViewId(@IdRes pageId: Int) {
        pageIds?.add(pageId)
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        return container.findViewById(pageIds!!.get(position))


    }

    override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int {
        return pageIds!!.size
    }

    override fun isViewFromObject(@NonNull view: View, @NonNull `object`: Any): Boolean {
        return view === `object`
    }
}