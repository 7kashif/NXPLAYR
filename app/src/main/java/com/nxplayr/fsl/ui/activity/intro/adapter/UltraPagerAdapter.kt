package com.nxplayr.fsl.ui.activity.intro.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.IntroStaticDataPojo
import kotlinx.android.synthetic.main.intro_inflate_layout.view.*

class UltraPagerAdapter (private val isMultiScr: Boolean, val list: List<IntroStaticDataPojo>) :
        PagerAdapter() {

    override fun getCount(): Int {
        return list.size

    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val linearLayout =
                LayoutInflater.from(container.context).inflate(R.layout.item_home_pager, null) as LinearLayout

        val introTitleTextView = linearLayout.introTitleTextView
        val introSubTitleTextView = linearLayout.introSubTitleTextView
        val introImagesIv = linearLayout.introImagesIv

        introTitleTextView.text = list[position].title
        introSubTitleTextView.text = list[position].subTitle
        introImagesIv.setImageDrawable(list[position].Images)
        container.addView(linearLayout)
        return linearLayout

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as LinearLayout
        container.removeView(view)
    }
}