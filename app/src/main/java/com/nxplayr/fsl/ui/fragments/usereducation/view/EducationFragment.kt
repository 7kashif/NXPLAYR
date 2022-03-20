package com.nxplayr.fsl.ui.fragments.usereducation.view


import android.app.Activity
import android.content.Context
import android.os.Bundle
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
import com.nxplayr.fsl.ui.fragments.usereducation.adapter.EducationAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.viewmodel.EducationModel
import com.nxplayr.fsl.data.model.EducationData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.userprofile.view.AddEducationFragment
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.PopupMenu
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_education.*
import kotlinx.android.synthetic.main.fragment_hashtags.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class EducationFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var education_list: ArrayList<EducationData?>? = ArrayList()
    var educationAdapter: EducationAdapter? = null
    var mActivity: Activity? = null

    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var pageNo = 0
    var list: java.util.ArrayList<String>? = null
    private lateinit var  getEducationModel: EducationModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_education, container, false)

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
        setupViewModel()
        setupUI()
        setupObserver()
    }

    private fun setupObserver() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

        getEducationModel.getEducation(mActivity!!, false, jsonArray.toString(), "List")
            .observe(viewLifecycleOwner,
                Observer { educationPojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (educationPojo != null && educationPojo.isNotEmpty()) {
                        if (educationPojo[0].status.equals("true", true)) {
                            education_list?.clear()
                            education_list?.addAll(educationPojo!![0]!!.data!!)
                            educationAdapter?.notifyDataSetChanged()


                        } else {

                            if (education_list!!.size == 0) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE

                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })
    }

    private fun setupUI() {
        tvToolbarTitle1.setText(R.string.education)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        add_icon_connection.setOnClickListener(this)
        btnRetry!!.setOnClickListener (this)

        educationAdapter = EducationAdapter(mActivity!!, education_list, object : EducationAdapter.OnItemClick {

            override fun onClicled(position: Int, from: String, v: View) {
                when (from) {
                    "delete" -> {
                        list = java.util.ArrayList()
                        list!!.add("Edit")
                        list!!.add("Delete")

                        PopupMenu(mActivity!!, v!!, list!!).showPopUp(object : PopupMenu.OnMenuSelectItemClickListener {
                            override fun onItemClick(item: String, pos: Int) {
                                when (pos) {
                                    0 -> {
                                        var bundle = Bundle()
                                        bundle.putString("from", "edit")
                                        bundle.putSerializable("usereduData", education_list!![position]!!)
                                        bundle.putInt("pos", position)
                                        (activity as MainActivity).navigateTo(AddEducationFragment(), bundle, AddEducationFragment::class.java.name, true)
                                    }
                                    1 -> {
                                        MyUtils.showMessageYesNo(mActivity!!, activity!!.resources.getString(R.string.education_remove_dialog),
                                            ""
                                        ) { dialogInterface, i ->
                                            deleteEducation(
                                                education_list!![position]!!.usereducationID,
                                                position
                                            )

                                        }
                                    }
                                }
                            }

                        })
                    }
                }
            }
        }, "education_list")
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.adapter = educationAdapter
        recyclerview.setHasFixedSize(true)
        educationAdapter?.notifyDataSetChanged()
        setupObserver()
    }

    private fun setupViewModel() {
        getEducationModel = ViewModelProvider(this@EducationFragment).get(EducationModel::class.java)

    }

    private fun deleteEducation(usereducationID: String, position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("usereducationID", usereducationID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        getEducationModel.getEducation(mActivity!!, false, jsonArray.toString(), "Delete")
                .observe(this@EducationFragment,
                        androidx.lifecycle.Observer { educationtpojo ->
                            MyUtils.dismissProgressDialog()
                            if (educationtpojo != null && educationtpojo.isNotEmpty()) {

                                if (educationtpojo[0].status.equals("true", false)) {

                                    val userData = sessionManager!!.userData
                                    if (userData!!.education!!.size > 0) {
                                        for (i in 0 until userData.education!!.size) {
                                            if (education_list!![position]!!.usereducationID.equals(userData.education!![i]!!.usereducationID)) {
                                                userData.education!!.removeAt(i)
                                                sessionManager!!.userData = userData
                                                education_list!!.removeAt(position)
                                                break
                                            }
                                        }
                                        educationAdapter!!.notifyDataSetChanged()
                                    }

                                } else {
                                    if (activity != null && activity is MainActivity)
                                        MyUtils.showSnackbar(mActivity!!, educationtpojo[0].message, ll_mainEducation)
                                }

                            } else {
                                ErrorUtil.errorMethod(ll_mainHashtagsList)


                            }
                        })


    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
           R.id.add_icon_connection->{
               (activity as MainActivity).navigateTo(AddEducationFragment(), AddEducationFragment::class.java.name, true)
           }
            R.id.btnRetry->{
                pageNo = 0
                setupObserver()
            }

        }
    }


}
