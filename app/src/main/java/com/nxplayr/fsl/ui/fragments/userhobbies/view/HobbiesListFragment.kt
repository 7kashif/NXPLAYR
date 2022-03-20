package com.nxplayr.fsl.ui.fragments.userhobbies.view


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userhobbies.adapter.HobbiesAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.Hobby
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.userhobbies.viewmodel.HobbiesModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_hobbies.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class HobbiesListFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: Activity? = null
    var hobbies_list: ArrayList<Hobby?>? = ArrayList()
    var hobbiesAdapter: HobbiesAdapter? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var pageNo = 0
    var userId=""
    private lateinit var  hobbiesModel: HobbiesModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_add_hobbies, container, false)
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
        if(arguments!=null)
        {
            userId=arguments?.getString("userId","")!!
        }
        setupViewModel()
        setupUI()
    }

    private fun setupObserver() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            if(userId.equals(userData?.userID)) {
                jsonObject.put("loginuserID", userData?.userID)
            }else{
                jsonObject.put("loginuserID", userId)

            }
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        hobbiesModel.getHobbiesList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(viewLifecycleOwner,
                Observer { hobbiesListPojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (hobbiesListPojo != null && hobbiesListPojo.isNotEmpty()) {
                        if (hobbiesListPojo[0].status.equals("true", true)) {
                            hobbies_list?.clear()
                            hobbies_list?.addAll(hobbiesListPojo!![0]!!.data!!)
                            if (hobbiesListPojo[0].data.size > 0) {
                                tv_hobbies_count.text = "Hobbies Added " + "(" + hobbiesListPojo[0].data.size + ")"
                            }
                            hobbiesAdapter?.notifyDataSetChanged()


                        } else {

                            if (hobbies_list!!.size == 0) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE

                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE

                            }


                        }
                    } else {
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })
    }

    private fun setupUI() {
        tvToolbarTitle1.setText(getString(R.string.hobbies))

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        if(userId.equals(userData?.userID))
        {
            add_icon_connection.visibility=View.VISIBLE
        }else{
            add_icon_connection.visibility=View.GONE

        }

        search_hobbies.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hobbiesAdapter!!.filter?.filter(p0.toString())
                hobbiesAdapter?.notifyDataSetChanged()
            }

        })

        add_icon_connection.setOnClickListener(this)
        hobbiesAdapter = HobbiesAdapter(activity as MainActivity, hobbies_list, object : HobbiesAdapter.OnItemClick {
            override fun onClicled(position: Int, from: String, hobbiesData: Hobby) {
                when (from) {
                    "delete_hobbies" -> {
                        search_hobbies.text.clear()
                        deleteHobbies(hobbiesData!!.userhobbiesID, position, hobbiesData)
                    }
                }
            }
        },userId,userData?.userID!!)
        recyclerview.adapter = hobbiesAdapter
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.setHasFixedSize(true)
        hobbiesAdapter?.notifyDataSetChanged()
        setupObserver()
        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
         hobbiesModel = ViewModelProvider(this@HobbiesListFragment).get(HobbiesModel::class.java)

    }
    fun updateHobbies() {
        hobbies_list?.clear()
        hobbies_list?.addAll(userData!!.hobbies)
        hobbiesAdapter?.notifyDataSetChanged()
    }

    private fun deleteHobbies(userhobbiesID: String, position: Int, hobbiesData: Hobby) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("userhobbiesID", userhobbiesID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        hobbiesModel.getHobbiesList(mActivity!!, false, jsonArray.toString(), "Delete")
                .observe(this@HobbiesListFragment,
                    { hobbiespojo ->
                        MyUtils.dismissProgressDialog()
                        if (hobbiespojo != null && hobbiespojo.isNotEmpty()) {

                            if (hobbiespojo[0].status.equals("true", false)) {

                                val userData = sessionManager!!.userData
                                if (userData!!.hobbies!!.size > 0) {
                                    for (i in 0 until userData!!.hobbies!!.size) {
                                        if (hobbiesData!!.userhobbiesID.equals(userData!!.hobbies!![i]!!.userhobbiesID)) {
                                            userData.hobbies!!.remove(hobbiesData)
                                            tv_hobbies_count.text = "Hobbies Added " + "(" + userData!!.hobbies!!.size + ")"
                                            sessionManager!!.userData = userData
                                            hobbies_list!!.remove(hobbiesData)
                                            break
                                        }
                                    }
                                    hobbiesAdapter!!.notifyDataSetChanged()
                                }

                            } else {
                                if (activity != null && activity is MainActivity)
                                    MyUtils.showSnackbar(mActivity!!, hobbiespojo[0].message, ll_mainHobbies)
                            }

                        } else {
                            ErrorUtil.errorMethod(ll_mainHobbies)


                        }
                    })
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.add_icon_connection->{
                (activity as MainActivity).navigateTo(AddHobbiesFragment(), AddHobbiesFragment::class.java.name, true)

            }
            R.id.btnRetry->{
                setupObserver()

            }
        }
    }

}


