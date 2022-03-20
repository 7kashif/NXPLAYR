package com.nxplayr.fsl.ui.fragments.explorepost.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.Banner
import com.squareup.picasso.Picasso


class BannerAdapter(var mActivity: Activity,
                    var onItemClick: OnItemClick,
                    bannerData: ArrayList<Banner>?) : PagerAdapter() {

    val bannerData = bannerData

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun getCount(): Int {
        return bannerData?.size!!
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val linearLayout =
                LayoutInflater.from(container.context).inflate(R.layout.item_banner_list, null) as LinearLayout

        var sdBannerImage1 = linearLayout.findViewById(R.id.sdBannerImage1) as ImageView
        Picasso.get().load(RestClient.image_base_url_banners +bannerData!![position]!!.bannerImage).into(sdBannerImage1)

        var tvbannerName = linearLayout.findViewById(R.id.tvbannerName) as TextView
        tvbannerName.setText(bannerData!![position]!!.bannerURL)

        container.addView(linearLayout)
        return linearLayout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as LinearLayout
        container.removeView(view)
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int)

    }
    
}