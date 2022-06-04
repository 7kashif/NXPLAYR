package com.nxplayr.fsl.ui.fragments.userprofile.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userprofile.adapter.WorkExperienceAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.viewmodel.EmployementModel
import com.nxplayr.fsl.data.model.EmploymentData
import com.nxplayr.fsl.data.model.Employmentpojo
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.fragment.AddEmployementFragment
import com.nxplayr.fsl.util.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_hashtags.*
import kotlinx.android.synthetic.main.fragment_work_experience.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class WorkExperienceFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: Activity? = null
    var employementList: ArrayList<EmploymentData?>? = ArrayList()
    var workExperienceAdapter: WorkExperienceAdapter? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    var pageNo = 0
    var list: java.util.ArrayList<String>? = null
    var userId: String = ""
    private lateinit var getEmployementModel: EmployementModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_work_experience, container, false)
        }
        return v
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)

        mActivity = context as AppCompatActivity

    }

    @SuppressLint("ResourceType")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            userId = arguments?.getString("userId", "")!!
        }
        setupViewModel()
        setupUI()
    }

    private fun setupViewModel() {
        getEmployementModel =
            ViewModelProvider(this@WorkExperienceFragment).get(EmployementModel::class.java)
    }

    private fun setupUI() {
        tvToolbarTitle1.setText(R.string.work_experience)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        if (userId.equals(userData?.userID)) {
            add_icon_connection.visibility = View.VISIBLE
        } else {
            add_icon_connection.visibility = View.GONE

        }

        add_icon_connection.setOnClickListener(this)
        btnRetry.setOnClickListener(this)

        workExperienceAdapter = WorkExperienceAdapter(
            mActivity!!,
            employementList!!,
            object : WorkExperienceAdapter.OnItemClick {
                override fun onClicled(
                    position: Int,
                    from: String,
                    v: View,
                    empData: EmploymentData?
                ) {
                    when (from) {
                        "delete" -> {
                            list = java.util.ArrayList()
                            list!!.add("Edit")
                            list!!.add("Delete")

                            PopupMenu(mActivity!!, v!!, list!!).showPopUp(object :
                                PopupMenu.OnMenuSelectItemClickListener {
                                override fun onItemClick(item: String, pos: Int) {
                                    when (pos) {
                                        0 -> {
                                            var bundle = Bundle()
                                            bundle.putString("from", "edit")
                                            bundle.putInt("pos", position)
                                            bundle.putSerializable(
                                                "userEmpData",
                                                employementList!![position]!!
                                            )
                                            (activity as MainActivity).navigateTo(
                                                AddEmployementFragment(),
                                                bundle,
                                                AddEmployementFragment::class.java.name,
                                                true
                                            )
                                        }
                                        1 -> {
                                            MyUtils.showMessageYesNo(mActivity!!,
                                                activity!!.resources.getString(R.string.experience_remove_dialog),
                                                "",
                                                DialogInterface.OnClickListener { dialogInterface, i ->
                                                    deleteWorkExp(
                                                        employementList!![position]!!.useremployementID,
                                                        position
                                                    )

                                                })
                                        }
                                    }
                                }


                            })
                        }
                    }
                }

            },
            "emp_list",
            userId
        )
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = workExperienceAdapter
        workExperienceAdapter?.notifyDataSetChanged()
        getEmploymentList()

    }


    private fun getEmploymentList() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            if (userId.equals(userData?.userID)) {
                jsonObject.put("loginuserID", userData?.userID)
            } else {
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
        getEmployementModel.employeeApi(jsonArray.toString(), "List")
        getEmployementModel.successEmployee
            .observe(viewLifecycleOwner) { employementPojo ->
                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                if (employementPojo != null && employementPojo.isNotEmpty()) {
                    if (employementPojo[0].status.equals("true", true)) {
                        employementList?.clear()
                        employementList?.addAll(employementPojo!![0]!!.data!!)
//                                    StoreSessionManager(userData!!)
                        workExperienceAdapter?.notifyDataSetChanged()


                    } else {

                        if (employementList!!.size == 0) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE

                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }
                    }
                } else {
                    relativeprogressBar.visibility = View.GONE
                    ErrorUtil.errorView(requireActivity(), nointernetMainRelativelayout)
                }
            }

    }

    private fun deleteWorkExp(useremployementID: String, position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("useremployementID", useremployementID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        Log.e("data", jsonArray.toString())
        getEmployementModel.employeeApi(jsonArray.toString(), "Delete")
        getEmployementModel.successEmployee
            .observe(viewLifecycleOwner) { employementpojo ->
                MyUtils.dismissProgressDialog()
                if (employementpojo != null && employementpojo.isNotEmpty()) {

                    if (employementpojo[0].status.equals("true", false)) {

                        val userData = sessionManager!!.userData
                        if (userData!!.employement!!.size > 0) {
                            for (i in 0 until userData!!.employement!!.size) {
                                if (employementList!![position]!!.useremployementID.equals(
                                        userData!!.employement!![i]!!.useremployementID
                                    )
                                ) {
                                    userData.employement!!.removeAt(i)
                                    sessionManager!!.userData = userData
                                    employementList!!.removeAt(position)
                                    break
                                }
                            }
                            workExperienceAdapter!!.notifyDataSetChanged()
                        }

                    } else {
                        if (activity != null && activity is MainActivity)
                            MyUtils.showSnackbar(
                                mActivity!!,
                                employementpojo[0].message,
                                ll_mainWorkExpe
                            )
                    }

                } else {
                    ErrorUtil.errorMethod(ll_mainHashtagsList)
                }
            }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRetry -> {
                getEmploymentList()

            }
            R.id.add_icon_connection -> {
                (activity as MainActivity).navigateTo(
                    AddEmployementFragment(),
                    AddEmployementFragment::class.java.name,
                    true
                )
            }
        }
    }


}
