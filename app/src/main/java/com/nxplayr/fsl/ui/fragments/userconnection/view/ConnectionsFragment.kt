package com.nxplayr.fsl.ui.fragments.userconnection.view


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.All
import com.nxplayr.fsl.data.model.FriendCount
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.ConnectionViewPagerAdapter
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.fragment_connections.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar2.*


class ConnectionsFragment : Fragment() {

    private var v: View? = null
    var adapter: ConnectionViewPagerAdapter? = null
    var connection_list: ArrayList<FriendCount>? = ArrayList()
    var mActivity: AppCompatActivity? = null
    var from = ""
    var userId = ""
    var viewcount: ArrayList<String?>? = null

    private val tabIcons = intArrayOf(0, com.nxplayr.fsl.R.drawable.friend_icon_big_connection, com.nxplayr.fsl.R.drawable.acquaintance_icon_big_connection, com.nxplayr.fsl.R.drawable.professional_icon_white)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(com.nxplayr.fsl.R.layout.fragment_connections, container, false)
        }
        return v

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (arguments != null) {
            from = arguments!!.getString("fromData").toString()
            userId = arguments!!.getString("userID").toString()
        }

        setupUI()
    }
    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        tvToolbarTitle1.setText(com.nxplayr.fsl.R.string.connections)


        if(from.equals("profile",false))
        {
            add_icon_connection.visibility=View.VISIBLE
        }else{
            add_icon_connection.visibility=View.GONE
        }


        add_icon_connection.setOnClickListener {
            (activity as MainActivity).navigateTo(AddConnectionsFragment(), AddConnectionsFragment::class.java.name, true)
        }

        viewcount = ArrayList()
        viewcount?.clear()
        viewcount?.add("0")
        viewcount?.add("0")
        viewcount?.add("0")
        viewcount?.add("0")
        viewcount?.add("0")
        setupViewPager(viewPager_connection)
        tab_layout_connection.tabMode = TabLayout.MODE_FIXED
    }

    fun setupTabIcons(countArray: FriendCount) {
        tab_layout_connection.setupWithViewPager(viewPager_connection)
        viewcount = ArrayList()
        viewcount?.clear()
        viewcount?.add(countArray?.all.toString())
        viewcount?.add(countArray.friends.toString())
        viewcount?.add(countArray.acquaintances.toString())
        viewcount?.add(countArray?.professionals.toString())

        for (i in 0 until tab_layout_connection.tabCount) {
            val yourlinearlayout = LayoutInflater.from(activity).inflate(
                    R.layout.customtablayout,
                    null
            ) as LinearLayoutCompat
            yourlinearlayout.orientation = LinearLayoutCompat.HORIZONTAL
            val tab_text = yourlinearlayout.findViewById<View>(R.id.tabContent) as AppCompatTextView
            val ivtabIcon =
                    yourlinearlayout.findViewById<View>(R.id.ivTabIcon) as AppCompatImageView
            val tabContentAll =
                    yourlinearlayout.findViewById<View>(R.id.tabContentAll) as AppCompatTextView

            tabContentAll.text = "All"

            if (tabIcons[i] == 0) {
                tabContentAll.visibility = View.VISIBLE
                ivtabIcon.visibility = View.GONE
            } else {
                tabContentAll.visibility = View.GONE
                ivtabIcon.visibility = View.VISIBLE
                ivtabIcon.setImageResource(tabIcons[i])

            }
            tab_text.visibility = View.VISIBLE
            tab_text.text = "(" + viewcount?.get(i) + ")"
            tab_layout_connection.getTabAt(i)?.customView = yourlinearlayout
        }
    }

    fun setupTabIcons(countArray: All) {
        tab_layout_connection.setupWithViewPager(viewPager_connection)
        viewcount = ArrayList()
        viewcount?.clear()
        viewcount?.add(countArray?.all.toString())
        viewcount?.add(countArray.friends.toString())
        viewcount?.add(countArray.acquaintances.toString())
        viewcount?.add(countArray?.professionals.toString())

        for (i in 0 until tab_layout_connection.tabCount) {
            val yourlinearlayout = LayoutInflater.from(activity).inflate(
                    R.layout.customtablayout,
                    null
            ) as LinearLayoutCompat
            yourlinearlayout.orientation = LinearLayoutCompat.HORIZONTAL
            val tab_text = yourlinearlayout.findViewById<View>(R.id.tabContent) as AppCompatTextView
            val ivtabIcon =
                    yourlinearlayout.findViewById<View>(R.id.ivTabIcon) as AppCompatImageView
            val tabContentAll =
                    yourlinearlayout.findViewById<View>(R.id.tabContentAll) as AppCompatTextView

            tabContentAll.text = "All"

            if (tabIcons[i] == 0) {
                tabContentAll.visibility = View.VISIBLE
                ivtabIcon.visibility = View.GONE
            } else {
                tabContentAll.visibility = View.GONE
                ivtabIcon.visibility = View.VISIBLE
                ivtabIcon.setImageResource(tabIcons[i])

            }
            tab_text.visibility = View.VISIBLE
            tab_text.text = "(" + viewcount?.get(i) + ")"
            tab_layout_connection.getTabAt(i)?.customView = yourlinearlayout
        }
    }

    fun setTabtitle(count: List<All>) {
        tab_layout_connection.getTabAt(0)?.text = "All " + "(" + count[0].all + ")"
        tab_layout_connection.getTabAt(1)?.text = count[0].friends.toString()
        tab_layout_connection.getTabAt(2)?.text = count[0].acquaintances.toString()
        tab_layout_connection.getTabAt(3)?.text = count[0].professionals.toString()
    }

    private fun setupViewPager(viewPager: ViewPager) {

        adapter = ConnectionViewPagerAdapter(childFragmentManager, from, userId,viewcount,tabIcons)
        viewPager.offscreenPageLimit = 4
        viewPager.adapter = adapter
    }

    fun updateViewPager() {
        viewPager_connection.adapter!!.notifyDataSetChanged()
    }
}
