package com.nxplayr.fsl.ui.fragments.friendrequest.view


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.nxplayr.fsl.data.model.FriendCount
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.friendrequest.adapter.FriendRequestViewPagerAdapter
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_accept_request.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.ArrayList

class AcceptRequestFragment : Fragment() {

    private var v: View? = null
    var adapter: FriendRequestViewPagerAdapter? = null
    var tabposition: Int = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var mActivity: AppCompatActivity? = null
    var rec_req = "Received Requests"
    var send_req = "Sent Requests"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //  if (v == null)
        v = inflater.inflate(com.nxplayr.fsl.R.layout.fragment_accept_request, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(activity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngReceivedRequests.isNullOrEmpty())
                rec_req = sessionManager?.LanguageLabel?.lngReceivedRequests.toString()
            if (!sessionManager?.LanguageLabel?.lngSentRequests.isNullOrEmpty())
                send_req = sessionManager?.LanguageLabel?.lngSentRequests.toString()
        }
        setupUI()
    }
//
//    @SuppressLint("ResourceType")
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        sessionManager = SessionManager(activity!!)
//        if (sessionManager?.get_Authenticate_User() != null) {
//            userData = sessionManager?.get_Authenticate_User()
//        }
//        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
//            if (!sessionManager?.LanguageLabel?.lngReceivedRequests.isNullOrEmpty())
//                rec_req = sessionManager?.LanguageLabel?.lngReceivedRequests.toString()
//            if (!sessionManager?.LanguageLabel?.lngSentRequests.isNullOrEmpty())
//                send_req = sessionManager?.LanguageLabel?.lngSentRequests.toString()
//        }
//        setupUI()
//    }


    private fun setupUI() {
        setupViewPager(viewpager_request)
        tab_layout_request.setupWithViewPager(viewpager_request)
    }

    private fun setupViewPager(viewPager: ViewPager) {
        adapter = FriendRequestViewPagerAdapter(childFragmentManager)
        adapter?.addFragment(ReceiveRequestFragment(), rec_req)
        adapter?.addFragment(ReceiveRequestFragment(), send_req)
        viewPager.offscreenPageLimit = 2
        viewPager.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    fun setTabtitle(count: List<FriendCount>) {
        tab_layout_request.getTabAt(0)?.text =
            "$rec_req " + "(" + count[0].pendingCount + ")"
        tab_layout_request.getTabAt(1)?.text = "$send_req " + "(" + count[0].sentCount + ")"
    }
}