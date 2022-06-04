package com.nxplayr.fsl.ui.fragments.userinterest.view


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.Interest.DataItem
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.userinterest.adapter.AddInterestsAdapter
import com.nxplayr.fsl.ui.fragments.userinterest.adapter.ViewProfessionalsAdapter
import com.nxplayr.fsl.ui.fragments.userinterest.viewmodel.InterestModel
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_employement.*
import kotlinx.android.synthetic.main.fragment_add_interests.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class AddInterestsFragment : Fragment() {

    private var v: View? = null
    var listData: ArrayList<String>? = ArrayList()
    var data: ArrayList<DataItem>? = ArrayList()
    var addInterestsAdapter: AddInterestsAdapter? = null
    var viewProfessionalsAdapter: ViewProfessionalsAdapter? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    val list = arrayListOf(R.drawable.image, R.drawable.appbg, R.drawable.image, R.drawable.appbg)
    var item_count: Int = 0
    var userData: SignupData? = null
    var sessionManager: SessionManager? = null
    private lateinit var interestModel: InterestModel

    companion object {
        @JvmStatic
        fun newInstance(count: Int) = AddInterestsFragment().apply {
            arguments = Bundle().apply {
                putInt("item_count", count)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getInt("item_count")?.let {
            item_count = it
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (v == null)
            v = inflater.inflate(R.layout.fragment_add_interests, container, false)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = activity?.let { SessionManager(it) }
        tvToolbarTitle.text = getString(R.string.add_interests)

        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        toolbar.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        setupViewModel()
        setupUI()
        setupObserver()

        addInterestsAdapter = AddInterestsAdapter(
            activity as MainActivity,
            listData,
            object : AddInterestsAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String) {

                    when (from) {
                        "Image" -> {

                        }
                    }
                }

            })
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerview.layoutManager = linearLayoutManager
        recyclerview.adapter = addInterestsAdapter
        addInterestsAdapter?.notifyDataSetChanged()


        //view professionals
        viewProfessionalsAdapter = ViewProfessionalsAdapter(
            activity as MainActivity,
            data,
            object : ViewProfessionalsAdapter.OnItemClick {
                override fun onClicked(data: DataItem, from: String) {
                    if (data.isYouFollowing.equals("Yes")) {
                        userInterest("unfollow", data)
                    } else {
                        userInterest("follow", data)
                    }
                }
            }, sessionManager!!)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerview1.layoutManager = linearLayoutManager
        recyclerview1.adapter = viewProfessionalsAdapter

        /* val divider = DividerItemDecoration(recyclerview_professionals.getContext(),
                 DividerItemDecoration.VERTICAL)
         divider.setDrawable(
                 context?.let { ContextCompat.getDrawable(it, R.drawable.line_layout) }!!
         )
         recyclerview_professionals.addItemDecoration(divider)*/

        viewProfessionalsAdapter?.notifyDataSetChanged()

        view_all_companies.setOnClickListener {
            (activity as MainActivity).navigateTo(
                AddInterestsFragment.newInstance(0),
                AddInterestsFragment::class.java.name,
                true
            )
        }
    }

    private fun setupViewModel() {
        interestModel =
            ViewModelProviders.of(this@AddInterestsFragment).get(InterestModel::class.java)
    }

    private fun setupObserver() {
        MyUtils.showProgressDialog(requireActivity(), "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        interestModel.getUserInterest(requireActivity(), false, jsonArray.toString())
            .observe(viewLifecycleOwner,
                androidx.lifecycle.Observer { companyListpojo ->
                    if (companyListpojo != null && companyListpojo.isNotEmpty()) {
                        if (companyListpojo[0].status.equals("true", false)) {
                            MyUtils.dismissProgressDialog()

                            data!!.clear()
                            if (item_count == 2 && companyListpojo[0].data.size > 2) {
                                data!!.add(companyListpojo[0].data[0])
                                data!!.add(companyListpojo[0].data[1])
                                view_all_companies.visibility = View.VISIBLE
                            } else {
                                data!!.addAll(companyListpojo[0].data)
                                view_all_companies.visibility = View.GONE
                            }
                            viewProfessionalsAdapter?.notifyDataSetChanged()

                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                requireActivity(),
                                companyListpojo[0].message!!,
                                ll_main_addEmployement
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        Toast.makeText(
                            requireActivity(),
                            R.string.error_common_network,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
    }

    private fun userInterest(action: String, data: DataItem) {

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("action", action)
            jsonObject.put("userfollowerFollowingID", data.usercompanyID)
            jsonObject.put("userfollowerFollowerID", userData?.userID)
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        interestModel.userInterest(requireActivity(), jsonArray.toString())
            .observe(
                viewLifecycleOwner
            ) { companyListpojo ->
                if (companyListpojo != null && companyListpojo.isNotEmpty()) {
                    setupObserver()
                } else {
                    MyUtils.dismissProgressDialog()
                    Toast.makeText(
                        requireActivity(),
                        R.string.error_common_network,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun setupUI() {

    }
}

