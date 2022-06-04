package com.nxplayr.fsl.ui.fragments.userhobbies.view


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userhobbies.adapter.AddHobbiesListAdapter
import com.nxplayr.fsl.ui.fragments.userhobbies.adapter.HobbiesAdapter
import com.nxplayr.fsl.ui.fragments.userhobbies.adapter.HobbiesListAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userhobbies.viewmodel.HobbiesListModel
import com.nxplayr.fsl.ui.fragments.userhobbies.viewmodel.HobbiesModel
import com.nxplayr.fsl.data.model.HobbiesList
import com.nxplayr.fsl.data.model.Hobby
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_add_hobbies_list.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_hobbies.*
import kotlinx.android.synthetic.main.fragment_add_skills_endorsement.*
import kotlinx.android.synthetic.main.fragment_hashtags.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class AddHobbiesFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var hobbies_list: ArrayList<HobbiesList?>? = ArrayList()
    var addhobbies_list: ArrayList<HobbiesList?>? = ArrayList()
    var userHobbiesList: ArrayList<Hobby?>? = ArrayList()
    var hobbiesAdapter: HobbiesListAdapter? = null
    var addHobbiesAdapter: AddHobbiesListAdapter? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var infaltorScheduleMode: LayoutInflater? = null
    var addedhobbiesAdapter: HobbiesAdapter? = null
    private var hobbiesUpdateListener: HobbiesUpdateListener? = null
    var addHobbies = "Hobbies Added"

    private lateinit var  addHobbiesModel: HobbiesModel
    private lateinit var  hobbiesListModel: HobbiesListModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (v == null)
            v = inflater.inflate(R.layout.activity_add_hobbies_list, container, false)
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            hobbiesUpdateListener = context as HobbiesUpdateListener
        } catch (e: Exception) {

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)

        tvToolbarTitle.text = getString(R.string.add_hobbies)

        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngAddHobbies.isNullOrEmpty())
                tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngAddHobbies
            if (!sessionManager?.LanguageLabel?.lngSearch.isNullOrEmpty())
                edit_searchHobbies.hint = sessionManager?.LanguageLabel?.lngSearch
            if (!sessionManager?.LanguageLabel?.lngHobbiesAdded.isNullOrEmpty()) {
                tv_hobbies_added.text = sessionManager?.LanguageLabel?.lngHobbiesAdded
                addHobbies = sessionManager?.LanguageLabel?.lngHobbiesAdded.toString()
            }
            if (!sessionManager?.LanguageLabel?.lngSuggestedHobbiesBased.isNullOrEmpty())
                tv_suggested_hobbies_added.text = sessionManager?.LanguageLabel?.lngSuggestedHobbiesBased
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_add_hobbies.progressText = sessionManager?.LanguageLabel?.lngSave
        }
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
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", "")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

       hobbiesListModel.getHobbiesList(mActivity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner
            ) { hobbiesListpojo ->

                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                if (hobbiesListpojo != null && hobbiesListpojo.isNotEmpty()) {

                    if (hobbiesListpojo[0].status.equals("true", false)) {


                        hobbies_list?.clear()
                        if (!userData!!.hobbies.isNullOrEmpty()) {
                            val firstListIds = userData!!.hobbies.map { it.hobbyID }
                            val new = hobbiesListpojo[0].data.filter { it.hobbyID !in firstListIds }
                            hobbies_list?.addAll((new))
                            hobbiesAdapter?.notifyDataSetChanged()
                        } else {
                            hobbies_list?.addAll(hobbiesListpojo[0].data)
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
                    ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                }
            }

    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        btn_add_hobbies.setOnClickListener(this)

        edit_searchHobbies.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                hobbiesAdapter!!.filter.filter(p0.toString())
                hobbiesAdapter?.notifyDataSetChanged()
            }

        })
        if (!userData!!.hobbies.isNullOrEmpty()) {
            addedHobiesList()
        }
        hobbiesAdapter = HobbiesListAdapter(mActivity!!, hobbies_list!!, object : HobbiesListAdapter.OnItemClick {

            override fun onClicled(hobbiesData: HobbiesList, from: String) {
                when (from) {
                    "add_hobbies" -> {
                        addToHobbiesList(hobbiesData)
                        hobbies_list?.remove(hobbiesData)
                        edit_searchHobbies.text.clear()
                        if (addhobbies_list!!.size > 0) {
                            btn_add_hobbies.backgroundTint = (resources.getColor(R.color.colorPrimary))
                            btn_add_hobbies.textColor = resources.getColor(R.color.black)
                            btn_add_hobbies.strokeColor = (resources.getColor(R.color.colorPrimary))

                            if (addhobbies_list!!.size > 0 || userData!!.hobbies.size > 0) {
                                if (!userData?.hobbies.isNullOrEmpty()) {
                                    var count = userData!!.hobbies.size + addhobbies_list!!.size
                                    tv_hobbies_added.text = "$addHobbies " + "(" + count + ")"
                                } else {
                                    tv_hobbies_added.text = "$addHobbies " + "(" + addhobbies_list?.size + ")"
                                }
                            } else if (addhobbies_list!!.size <= 0 || userData!!.hobbies.size <= 0) {
                                tv_hobbies_added.text = "$addHobbies "
                            }
                        }
                        hobbiesAdapter?.notifyDataSetChanged()
                    }
                }
            }
        })
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = hobbiesAdapter
        setupObserver()


        if (userData!!.hobbies.size > 0) {
            btn_add_hobbies.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_add_hobbies.textColor = resources.getColor(R.color.black)
            btn_add_hobbies.strokeColor = (resources.getColor(R.color.colorPrimary))
        } else {
            btn_add_hobbies.strokeColor = (resources.getColor(R.color.grayborder))
            btn_add_hobbies.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_add_hobbies.textColor = resources.getColor(R.color.colorPrimary)
        }

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
         addHobbiesModel = ViewModelProvider(this@AddHobbiesFragment).get(HobbiesModel::class.java)
         hobbiesListModel = ViewModelProvider(this@AddHobbiesFragment).get(HobbiesListModel::class.java)

    }

    private fun addToHobbiesList(hobbies: HobbiesList) {
        addHobbiesAdapter = AddHobbiesListAdapter(mActivity!!, addhobbies_list!!, object : AddHobbiesListAdapter.OnItemClick {
            override fun onClicled(position: Int, from: String) {

                when (from) {
                    "remove_hobbies" -> {
                        if (!addhobbies_list.isNullOrEmpty()) {
                            hobbies_list?.add(addhobbies_list!![position])
                            hobbiesAdapter?.notifyDataSetChanged()
                            addhobbies_list?.removeAt(position)
                            addHobbiesAdapter?.notifyDataSetChanged()

                            if (addhobbies_list!!.size > 0 || userData!!.hobbies.size >= 0) {
                                if (!userData?.hobbies.isNullOrEmpty()) {
                                    var count = userData!!.hobbies.size + addhobbies_list!!.size
                                    tv_hobbies_added.text = "$addHobbies " + "(" + count + ")"
                                } else {
                                    tv_hobbies_added.text = "$addHobbies " + "(" + addhobbies_list!!.size + ")"
                                }
                            } else {
                                tv_hobbies_added.text = "$addHobbies "
                            }
//                            }
                        }

                    }
                }
            }
        })

        addhobbies_list?.add(hobbies)
        RV_hobbiesList.layoutManager = LinearLayoutManager(mActivity!!)
        RV_hobbiesList.setHasFixedSize(true)
        RV_hobbiesList.adapter = addHobbiesAdapter
        addHobbiesAdapter?.notifyDataSetChanged()

    }

    private fun addHobbies() {

        btn_add_hobbies.startAnimation()

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        val jsonArrayhobbies = JSONArray()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)

            if (!addhobbies_list.isNullOrEmpty()) {
                for (i in 0 until addhobbies_list!!.size) {
                    val hobbiesDetailPojo = JSONObject()
                    hobbiesDetailPojo.put("hobbyName", addhobbies_list!![i]?.hobbyName)
                    hobbiesDetailPojo.put("hobbyID", addhobbies_list!![i]?.hobbyID)
                    jsonArrayhobbies.put(hobbiesDetailPojo)
                }
            }

            jsonObject.put("hobbydetails", jsonArrayhobbies)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

         addHobbiesModel.getHobbiesList(mActivity!!, false, jsonArray.toString(), "Add")
                .observe(viewLifecycleOwner,
                    { hobbiesPojo ->
                        if (hobbiesPojo != null) {
                            btn_add_hobbies.endAnimation()
                            if (hobbiesPojo.get(0).status.equals("true", false)) {
                                try {
                                    userData?.hobbies?.clear()
                                    userData?.hobbies?.addAll(hobbiesPojo.get(0).data)
                                    if (hobbiesUpdateListener != null)
                                        hobbiesUpdateListener!!.onHobbiesUpdate()
                                    Handler().postDelayed({
                                        (activity as MainActivity).onBackPressed()
                                    }, 2000)
                                    StoreSessionManager(userData)

                                    MyUtils.showSnackbar(mActivity!!, hobbiesPojo.get(0).message, ll_mainHobbiesList)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                MyUtils.showSnackbar(mActivity!!, hobbiesPojo.get(0).message, ll_mainHobbiesList)
                            }

                        } else {
                            btn_add_hobbies.endAnimation()
                            ErrorUtil.errorMethod(ll_mainHobbiesList)
                        }
                    })
    }

    private fun addedHobiesList() {
        addedhobbiesAdapter = HobbiesAdapter(activity as MainActivity, userHobbiesList!!, object : HobbiesAdapter.OnItemClick {
            override fun onClicled(position: Int, from: String, hobbiesData: Hobby) {
                when (from) {
                    "delete_hobbies" -> {
                        deleteHobbies(hobbiesData.userhobbiesID, position, hobbiesData)
                    }
                }
            }
        },userData?.userID!!,userData?.userID!!)
        RV_addedHobbiesList.adapter = addedhobbiesAdapter
        RV_addedHobbiesList.layoutManager = LinearLayoutManager(activity)
        RV_addedHobbiesList.setHasFixedSize(true)
        addedhobbiesAdapter?.notifyDataSetChanged()
        AddedHobbiesListApi()
    }

    private fun AddedHobbiesListApi() {

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

        addHobbiesModel.getHobbiesList(mActivity!!, false, jsonArray.toString(), "List")
                .observe(viewLifecycleOwner,
                    { hobbiesListPojo ->

                        RV_addedHobbiesList.visibility = View.VISIBLE

                        if (hobbiesListPojo != null && hobbiesListPojo.isNotEmpty()) {
                            if (hobbiesListPojo[0].status.equals("true", true)) {
                                userHobbiesList?.clear()
                                userHobbiesList?.addAll(hobbiesListPojo[0].data)
                                if (hobbiesListPojo[0].data.size > 0) {

                                    if (addhobbies_list!!.size >= 0 || userData!!.hobbies.size >= 0) {
                                        if (!userData?.hobbies.isNullOrEmpty()) {
                                            var count = hobbiesListPojo[0].data.size + addhobbies_list!!.size
                                            tv_hobbies_added.text = "$addHobbies " + "(" + count + ")"
                                            addHobbiesAdapter?.notifyDataSetChanged()
                                            addedhobbiesAdapter?.notifyDataSetChanged()
                                        } else {
                                            tv_hobbies_added.text = "$addHobbies " + "(" + addhobbies_list!!.size + ")"
                                        }
                                    } else if (addhobbies_list!!.size <= 0) {
                                        tv_hobbies_added.text = "$addHobbies "
                                    }
                                }
                                addedhobbiesAdapter?.notifyDataSetChanged()

                            } else {

                                if (userHobbiesList!!.size == 0) {
                                    RV_addedHobbiesList.visibility = View.GONE
                                } else {
                                    RV_addedHobbiesList.visibility = View.VISIBLE
                                }
                            }
                        } else {
                            ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                        }
                    })

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

        addHobbiesModel.getHobbiesList(mActivity!!, false, jsonArray.toString(), "Delete")
                .observe(this@AddHobbiesFragment,
                    { hobbiespojo ->
                        MyUtils.dismissProgressDialog()
                        if (hobbiespojo != null && hobbiespojo.isNotEmpty()) {

                            if (hobbiespojo[0].status.equals("true", false)) {

                                if (userData!!.hobbies.size > 0) {
                                    for (i in 0 until userData!!.hobbies.size) {
                                        if (hobbiesData.userhobbiesID.equals(userData!!.hobbies[i].userhobbiesID)) {
                                            userData!!.hobbies.remove(hobbiesData)
                                            sessionManager!!.userData = userData
                                            userHobbiesList!!.remove(hobbiesData)
                                            break
                                        }
                                    }

                                    addedhobbiesAdapter!!.notifyDataSetChanged()

                                    setupObserver()
                                    hobbiesAdapter?.notifyDataSetChanged()

                                    if (addhobbies_list!!.size >= 0 || userData!!.hobbies.size >= 0) {

                                        var count = hobbiespojo[0].data.size + addhobbies_list!!.size
                                        tv_hobbies_added.text = "$addHobbies " + "(" + count + ")"

                                    } else if (addhobbies_list!!.size < 0 && hobbiespojo[0].data.size < 0) {
                                        tv_hobbies_added.text = "$addHobbies "
                                    }
                                }


                            } else {
                                if (activity != null && activity is MainActivity)
                                    MyUtils.showSnackbar(mActivity!!, hobbiespojo[0].message, ll_mainHobbiesList)
                            }

                        } else {
                            ErrorUtil.errorMethod(ll_mainHobbiesList)

                        }
                    })
    }

    private fun StoreSessionManager(uesedata: SignupData?) {

        val gson = Gson()

        val json = gson.toJson(uesedata)
        sessionManager?.create_login_session(
                json,
                uesedata!!.userMobile,
                "",
                true,
                sessionManager!!.isEmailLogin()
        )

    }

    interface HobbiesUpdateListener {
        fun onHobbiesUpdate()
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btn_add_hobbies->{
                MyUtils.hideKeyboard1(mActivity!!)
                if (!addhobbies_list!!.isEmpty() || (!userData!!.hobbies.isEmpty())) {
                    addHobbies()
                } else {
                    MyUtils.showSnackbar(mActivity!!, "Please add Hobbies", ll_mainHobbiesList)
                }
            }
            R.id.btnRetry->{
                setupObserver()
                AddedHobbiesListApi()
            }
        }
    }


}
