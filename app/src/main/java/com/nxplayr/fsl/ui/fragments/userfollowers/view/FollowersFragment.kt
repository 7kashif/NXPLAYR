package com.nxplayr.fsl.ui.fragments.userfollowers.view


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.data.model.FollowingCount
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userconnection.view.AddConnectionsFragment
import com.nxplayr.fsl.ui.fragments.userfollowers.adapter.FollowersViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_followers.*
import kotlinx.android.synthetic.main.fragment_given.viewPager
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar2.*


@Suppress("DEPRECATION")
class FollowersFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var adapter: FollowersViewPagerAdapter? = null
    var mActivity: Activity? = null
    var tabposition = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var userId = ""
    var from = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(com.nxplayr.fsl.R.layout.fragment_followers, container, false)
        }
        return v
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            tabposition = arguments!!.getInt("tabposition", 0)
            userId = arguments!!.getString("userID").toString()
            from = arguments!!.getString("fromData").toString()

        }
        setupUI()


    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        tvToolbarTitle1.text = getString(R.string.followers_following)

        if (from.equals("profile")) {
            add_icon_connection.visibility = View.VISIBLE

        } else {
            add_icon_connection.visibility = View.GONE
        }
        setupViewPager(viewPager)
        tab_layout.setupWithViewPager(viewPager)
        add_icon_connection.setOnClickListener(this)
    }


    private fun setupViewPager(viewPager: ViewPager) {
        adapter = FollowersViewPagerAdapter(childFragmentManager, userId, from)
        adapter?.addFragment(FollowersListFragment(), "Followers")
        adapter?.addFragment(FollowersListFragment(), "Following")
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = adapter
        viewPager.currentItem = tabposition
        adapter?.notifyDataSetChanged()
    }

    fun setFollowingCount(count: List<FollowingCount>) {
        tab_layout.getTabAt(0)?.text = "Followers " + count[0].followerCount
        tab_layout.getTabAt(1)?.text = "Following " + count[0].followingCount
    }



    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.add_icon_connection->{
                (activity as MainActivity).navigateTo(AddConnectionsFragment(), AddConnectionsFragment::class.java.name, true)
            }

        }
    }


}

