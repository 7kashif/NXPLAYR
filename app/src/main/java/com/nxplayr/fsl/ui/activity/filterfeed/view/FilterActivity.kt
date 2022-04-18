package com.nxplayr.fsl.ui.activity.filterfeed.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.filterfeed.adapter.FilterAdapter
import com.nxplayr.fsl.ui.activity.filterfeed.adapter.FilterSubItemAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.activity.filterfeed.viewmodel.PlayerPositionModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.CountryListModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.FootballLevelListModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.FootballLevelModel
import com.nxplayr.fsl.ui.fragments.userprofile.viewmodel.FootballAgeListModel
import com.nxplayr.fsl.viewmodel.*
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_filter.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class FilterActivity : AppCompatActivity(), View.OnClickListener {

    var list: ArrayList<FilterItemPojo>? = null
    var listSubItem: ArrayList<FilterSubItem?>? = null
    var listSubItem1: ArrayList<FilterSubItem?>? = null

    var listGenderItem: ArrayList<FilterSubItem?>? = null
    var listPublicationItem: ArrayList<FilterSubItem?>? = null
    var listSortByItem: ArrayList<FilterSubItem?>? = null

    val filterList = ArrayList<FilterModel>()

    var footballTypeItemList = ArrayList<FootballLevelListData>()
    var footballAgeGroupList = ArrayList<AgecategoryList>()
    var footballLevelItemList = ArrayList<FootballLevelData>()
    var pitchPositionItemList = ArrayList<PlayerPosData>()
    var countryItemList = ArrayList<CountryListData>()
    val publicationList = ArrayList<FilterModel>()
    val sortByList = ArrayList<FilterModel>()

    private var linearLayoutManager: LinearLayoutManager? = null
    private var linearLayoutManager1: LinearLayoutManager? = null
    var filterAdapter: FilterAdapter? = null
    var filterSubItemAdapter: FilterSubItemAdapter? = null
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false

    var selectedPos: Int = 0
    var selectedfrom: String = ""
    var isSelected: Boolean = false
    var sessionManager: SessionManager? = null

    var pageNumberFootballType = 0
    var pageNumberFootballAgeType = 0
    var pageNumberFootballLevelType = 0
    var pageNumberPitchPos = 0
    var pageNumberCountry = 0

    var userData: SignupData? = null

    var footballTypedegreeIDs = ""
    var footballageIDs = ""
    var footballLevelIDs = ""
    var footballPitchPositionIDs = ""
    var countryIDs = ""
    var genders = ""
    var publicationTime = ""
    var sortBy = ""

    var listSortBy: List<String> = listOf("My posts only", "Connection's posts", "Most Viewed", "Most Starred", "Most Commented")
    var listGender: List<String> = listOf("Male", "Female")
    var listPublicationTime: List<String> = listOf("Anytime", "Today", "This Week", "This Month")
    private lateinit var  degreeListModel: CountryListModel
    private lateinit var  playerPositionModel: PlayerPositionModel
    private lateinit var  footballLevelListModel: FootballLevelListModel
    private lateinit var  footballAgeListModel: FootballAgeListModel
    private lateinit var  footballLevelModel: FootballLevelModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        sessionManager = SessionManager(this@FilterActivity)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        imgFilterBack.setOnClickListener(this)
        txtClearAll.setOnClickListener(this)
        searchEditText.visibility = View.GONE
        footballTypeItemList = ArrayList()
        footballAgeGroupList = ArrayList()
        pitchPositionItemList = ArrayList()
        countryItemList = ArrayList()
        footballLevelItemList = ArrayList()
        listGenderItem = ArrayList()
        listPublicationItem = ArrayList()
        listSortByItem = ArrayList()

        if (intent != null) {
            footballTypedegreeIDs = intent.getStringExtra("footballType")!!
            footballageIDs = intent.getStringExtra("footballagecatID")!!
            footballLevelIDs = intent.getStringExtra("footballLevel")!!
            footballPitchPositionIDs = intent.getStringExtra("pitchPosition")!!
            countryIDs = intent.getStringExtra("countryID")!!
            genders = intent.getStringExtra("gender")!!
            publicationTime = intent.getStringExtra("publicationTime")!!
            sortBy = intent.getStringExtra("sortby")!!

        }
        listSubItem = ArrayList()
        listSubItem1 = ArrayList()

        filterList.add(FilterModel("Gender"))
        filterList.add(FilterModel("Football Age Category"))
        filterList.add(FilterModel("Football Type"))
        filterList.add(FilterModel("Football Level"))
        filterList.add(FilterModel("Pitch Position"))
        filterList.add(FilterModel("Country"))
        filterList.add(FilterModel("Publication Time"))
        filterList.add(FilterModel("Sort By"))

        linearLayoutManager1 = LinearLayoutManager(this@FilterActivity)
        linearLayoutManager = LinearLayoutManager(this@FilterActivity)
        setAdapter()
        filterAdapter = FilterAdapter(this@FilterActivity, object : FilterAdapter.OnItemClick {
            override fun onClicklisneter(pos: Int, from: String) {
                selectedfrom = from
                selectedPos = pos
                getFilterData()
            }


        }, filterList!!, false)
        rcFilterList.layoutManager = linearLayoutManager

        rcFilterList.setHasFixedSize(true)
        rcFilterList.adapter = filterAdapter

        getGenderData()
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchFilter()
            }
        })

        lylUnSelectApply.setOnClickListener(this)
        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
        degreeListModel = ViewModelProvider(this@FilterActivity).get(CountryListModel::class.java)
        playerPositionModel = ViewModelProvider(this@FilterActivity).get(PlayerPositionModel::class.java)
        footballLevelListModel = ViewModelProvider(this@FilterActivity).get(FootballLevelListModel::class.java)
        footballAgeListModel = ViewModelProvider(this@FilterActivity).get(FootballAgeListModel::class.java)
        footballLevelModel = ViewModelProvider(this@FilterActivity).get(FootballLevelModel::class.java)

    }

    private fun applyFilter(isFinish: Boolean) {
        var intent = Intent().apply {
            putExtra("footballLevel", footballLevelIDs)
            putExtra("countryID", countryIDs)
            putExtra("sortby", sortBy)
            putExtra("pitchPosition", footballPitchPositionIDs)
            putExtra("gender", genders)
            putExtra("footballagecatID", footballageIDs)
            putExtra("footballType", footballTypedegreeIDs)
            putExtra("publicationTime", publicationTime)
        }
        setResult(820, intent)
        if (isFinish)
            onBackPressed()
    }


    private fun getFilterData() {
        when (selectedPos) {

            0 -> {
                searchEditText.visibility = View.GONE
                if (listGenderItem.isNullOrEmpty()) {
                    getGenderData()
                } else {
                    inflateGender(listGenderItem)
                }

            }
            1 -> {
                searchEditText.visibility = View.GONE
                if (footballAgeGroupList.isNullOrEmpty()) {
                    getageCategoryDataApi()
                } else {
                    getageCategoryData(footballAgeGroupList)
                }
            }
            2 -> {
                searchEditText.visibility = View.GONE
                if (footballTypeItemList.isNullOrEmpty()) {
                    getFootbalTypeApi()
                } else {
                    getFootbalType(footballTypeItemList)
                }

            }
            3 -> {
                searchEditText.visibility = View.GONE
                if (footballLevelItemList.isNullOrEmpty()) {
                    getFootballLevelApi()
                } else {
                    getFootballLevel(footballLevelItemList)
                }

            }
            4 -> {
                searchEditText.visibility = View.GONE
                if (pitchPositionItemList.isNullOrEmpty()) {
                    getPitchPositionApi()
                } else {
                    getPitchPosition(pitchPositionItemList)
                }

            }
            5 -> {
                searchEditText.visibility = View.VISIBLE
                if (countryItemList.isNullOrEmpty()) {
                    getCountryDataAPI()
                } else {
                    getCountryData(countryItemList)
                }
            }
            6 -> {
                searchEditText.visibility = View.GONE
                if (listPublicationItem.isNullOrEmpty()) {
                    getPublicationTime()
                } else {
                    inflatePublicationTime(listPublicationItem!!)
                }

            }
            7 -> {
                searchEditText.visibility = View.GONE
                if (listSortByItem.isNullOrEmpty()) {
                    getSortByData()
                } else {
                    inflateSortBy(listSortByItem!!)
                }

            }
        }
    }

    private fun searchFilter() {
        when (selectedPos) {
            5 -> {
                if (!searchEditText.text.toString().trim().isNullOrEmpty()) {
                    getCountryData(countryItemList!!)
                    var filterListData = listSubItem?.filter {
                        it?.title.toString().toLowerCase().trim()
                                .contains(searchEditText.text.toString().toLowerCase().trim())
                    }
                    listSubItem?.clear()
                    listSubItem?.addAll(filterListData!!)
                    filterSubItemAdapter?.notifyDataSetChanged()
                } else {
                    getCountryData(countryItemList!!)
                }

            }
        }
    }

    private fun getCountryDataAPI() {
        ll_no_data_found.visibility = View.GONE
        if (pageNumberCountry == 0) {
            MyUtils.showProgressDialog(this@FilterActivity, "Please wait...")
            relativeprogressBar.visibility = View.GONE
            listSubItem!!.clear()
            filterSubItemAdapter?.notifyDataSetChanged()
            recyclerview.visibility = (View.GONE)
        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = (View.VISIBLE)
            listSubItem!!.add(null)
            filterSubItemAdapter?.notifyItemInserted(listSubItem!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")

            jsonObject.put("languageID",  if (sessionManager?.getSelectedLanguage() == null) "1" else sessionManager?.getSelectedLanguage()?.languageID)
            jsonObject.put("page", pageNumberCountry)
            jsonObject.put("pagesize", "100")
            jsonObject.put("blankCountryCode","No")

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        degreeListModel.getCountryList(this@FilterActivity, false, jsonArray.toString()).observe(this@FilterActivity,
                Observer { specialityPojo ->
                    if (specialityPojo != null && specialityPojo.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()
                        isLoading = false
                        //   remove progress item
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE
                        recyclerview.visibility = (View.VISIBLE)

                        if (pageNumberCountry > 0) {
                            listSubItem!!.removeAt(listSubItem!!.size - 1)
                            filterSubItemAdapter?.notifyItemRemoved(listSubItem!!.size)
                        }
                        if (specialityPojo[0].status.equals("true", false)) {

                            if (pageNumberCountry == 0)
                                footballTypeItemList?.clear()

                            countryItemList.addAll(specialityPojo[0].data)

                            pageNumberCountry += 1
                            if (specialityPojo[0].data!!.size < 100) {
                                isLastpage = true
                            }
                            getCountryData(specialityPojo[0].data as ArrayList<CountryListData>)
                        }

                        relativeprogressBar.visibility = View.GONE

                        if (pitchPositionItemList?.size == 0) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE
                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }
                    } else {
                        errromethod1()
                    }
                })

    }

    private fun getPitchPositionApi() {
        ll_no_data_found.visibility = View.GONE
        if (pageNumberPitchPos == 0) {
            MyUtils.showProgressDialog(this@FilterActivity, "Please wait...")
            relativeprogressBar.visibility = View.GONE
            listSubItem!!.clear()
            filterSubItemAdapter?.notifyDataSetChanged()
            recyclerview.visibility = (View.GONE)
        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = (View.VISIBLE)
            listSubItem!!.add(null)
            filterSubItemAdapter?.notifyItemInserted(listSubItem!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")

            if (!sessionManager!!.isLoggedIn()) {
                jsonObject.put("languageID",  if (sessionManager?.getSelectedLanguage() == null) "1" else sessionManager?.getSelectedLanguage()?.languageID)
            } else {
                jsonObject.put("languageID", sessionManager!!.get_Authenticate_User().languageID)
            }
            jsonObject.put("page", pageNumberPitchPos)
            jsonObject.put("pagesize", "100")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        playerPositionModel.getPlayerPosList(this@FilterActivity, false, jsonArray.toString()).observe(this@FilterActivity,
            { specialityPojo ->
                if (specialityPojo != null && specialityPojo.isNotEmpty()) {
                    MyUtils.dismissProgressDialog()
                    isLoading = false
                    //   remove progress item
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = (View.VISIBLE)

                    if (pageNumberPitchPos > 0) {
                        listSubItem!!.removeAt(listSubItem!!.size - 1)
                        filterSubItemAdapter?.notifyItemRemoved(listSubItem!!.size)
                    }
                    if (specialityPojo[0].status.equals("true", false)) {

                        if (pageNumberPitchPos == 0)
                            footballTypeItemList?.clear()

                        pitchPositionItemList.addAll(specialityPojo[0].data)

                        pageNumberPitchPos += 1
                        if (specialityPojo[0].data!!.size < 100) {
                            isLastpage = true
                        }
                        getPitchPosition(specialityPojo[0].data as ArrayList<PlayerPosData>)
                    }

                    relativeprogressBar.visibility = View.GONE

                    if (pitchPositionItemList?.size == 0) {
                        ll_no_data_found.visibility = View.VISIBLE
                        recyclerview.visibility = View.GONE
                    } else {
                        ll_no_data_found.visibility = View.GONE
                        recyclerview.visibility = View.VISIBLE
                    }
                } else {
                    errromethod1()
                }
            })
    }

    private fun getFootballLevelApi() {
        ll_no_data_found.visibility = View.GONE
        if (pageNumberFootballLevelType == 0) {
            MyUtils.showProgressDialog(this@FilterActivity, "Please wait...")
            relativeprogressBar.visibility = View.GONE
            listSubItem!!.clear()
            filterSubItemAdapter?.notifyDataSetChanged()
            recyclerview.visibility = (View.GONE)
        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = (View.VISIBLE)
            listSubItem!!.add(null)
            filterSubItemAdapter?.notifyItemInserted(listSubItem!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")

            if (!sessionManager!!.isLoggedIn()) {
                jsonObject.put("languageID",  if (sessionManager?.getSelectedLanguage() == null) "1" else sessionManager?.getSelectedLanguage()?.languageID)
            } else {
                jsonObject.put("languageID", sessionManager!!.get_Authenticate_User().languageID)
            }
            jsonObject.put("page", pageNumberFootballLevelType)
            jsonObject.put("pagesize", "100")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        footballLevelListModel.getFootballLevelList(this@FilterActivity, false, jsonArray.toString()).observe(this@FilterActivity,
            { specialityPojo ->
                if (specialityPojo != null && specialityPojo.isNotEmpty()) {
                    MyUtils.dismissProgressDialog()
                    isLoading = false
                    //   remove progress item
                    ll_no_data_found.visibility = View.GONE
                    nointernetMainRelativelayout.visibility = View.GONE
                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = (View.VISIBLE)

                    if (pageNumberFootballLevelType > 0) {
                        listSubItem!!.removeAt(listSubItem!!.size - 1)
                        filterSubItemAdapter?.notifyItemRemoved(listSubItem!!.size)
                    }
                    if (specialityPojo[0].status.equals("true", false)) {

                        if (pageNumberFootballAgeType == 0)
                            footballTypeItemList?.clear()

                        footballLevelItemList.addAll(specialityPojo[0].data)

                        pageNumberFootballAgeType += 1
                        if (specialityPojo[0].data!!.size < 100) {
                            isLastpage = true
                        }
                        getFootballLevel(specialityPojo[0].data as ArrayList<FootballLevelData>)
                    }

                    relativeprogressBar.visibility = View.GONE

                    if (footballLevelItemList?.size == 0) {
                        ll_no_data_found.visibility = View.VISIBLE
                        recyclerview.visibility = View.GONE
                    } else {
                        ll_no_data_found.visibility = View.GONE
                        recyclerview.visibility = View.VISIBLE
                    }
                } else {
                    errromethod1()
                }
            })
    }

    private fun getageCategoryDataApi() {
        ll_no_data_found.visibility = View.GONE
        if (pageNumberFootballAgeType == 0) {
            MyUtils.showProgressDialog(this@FilterActivity, "Please wait...")
            relativeprogressBar.visibility = View.GONE
            listSubItem!!.clear()
            filterSubItemAdapter?.notifyDataSetChanged()
            recyclerview.visibility = (View.GONE)
        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = (View.VISIBLE)
            listSubItem!!.add(null)
            filterSubItemAdapter?.notifyItemInserted(listSubItem!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")

            if (!sessionManager!!.isLoggedIn()) {
                jsonObject.put("languageID",  if (sessionManager?.getSelectedLanguage() == null) "1" else sessionManager?.getSelectedLanguage()?.languageID)
            } else {
                jsonObject.put("languageID", sessionManager!!.get_Authenticate_User().languageID)
            }
            jsonObject.put("page", pageNumberFootballAgeType)
            jsonObject.put("pagesize", "100")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        footballAgeListModel.getFootballAgeList(this@FilterActivity, false, jsonArray.toString()).observe(this@FilterActivity,
                Observer { specialityPojo ->
                    if (specialityPojo != null && specialityPojo.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()
                        isLoading = false
                        //   remove progress item
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE
                        recyclerview.visibility = (View.VISIBLE)

                        if (pageNumberFootballAgeType > 0) {
                            listSubItem!!.removeAt(listSubItem!!.size - 1)
                            filterSubItemAdapter?.notifyItemRemoved(listSubItem!!.size)
                        }
                        if (specialityPojo[0].status.equals("true", false)) {

                            if (pageNumberFootballAgeType == 0)
                                footballTypeItemList?.clear()

                            footballAgeGroupList.addAll(specialityPojo[0].data)

                            pageNumberFootballAgeType += 1
                            if (specialityPojo[0].data!!.size < 100) {
                                isLastpage = true
                            }
                            getageCategoryData(specialityPojo[0].data)
                        }

                        relativeprogressBar.visibility = View.GONE

                        if (footballAgeGroupList?.size == 0) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE
                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }
                    } else {
                        errromethod1()
                    }
                })
    }

    private fun getFootbalTypeApi() {
        ll_no_data_found.visibility = View.GONE
        if (pageNumberFootballType == 0) {
            MyUtils.showProgressDialog(this@FilterActivity, "Please wait...")
            relativeprogressBar.visibility = View.GONE
            listSubItem!!.clear()
            filterSubItemAdapter?.notifyDataSetChanged()
            recyclerview.visibility = (View.GONE)
        } else {
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = (View.VISIBLE)
            listSubItem!!.add(null)
            filterSubItemAdapter?.notifyItemInserted(listSubItem!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")

            if (!sessionManager!!.isLoggedIn()) {
                jsonObject.put("languageID",  if (sessionManager?.getSelectedLanguage() == null) "1" else sessionManager?.getSelectedLanguage()?.languageID)
            } else {
                jsonObject.put("languageID", sessionManager!!.get_Authenticate_User().languageID)
            }
            jsonObject.put("page", pageNumberFootballType)
            jsonObject.put("pagesize", "100")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        footballLevelModel.getFootballLevelList(this@FilterActivity, false, jsonArray.toString()).observe(this@FilterActivity,
                Observer { specialityPojo ->
                    if (specialityPojo != null && specialityPojo.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()
                        isLoading = false
                        //   remove progress item
                        ll_no_data_found.visibility = View.GONE
                        nointernetMainRelativelayout.visibility = View.GONE
                        relativeprogressBar.visibility = View.GONE
                        recyclerview.visibility = (View.VISIBLE)

                        if (pageNumberFootballType > 0) {
                            listSubItem!!.removeAt(listSubItem!!.size - 1)
                            filterSubItemAdapter?.notifyItemRemoved(listSubItem!!.size)
                        }
                        if (specialityPojo[0].status.equals("true", false)) {

                            if (pageNumberFootballType == 0)
                                footballTypeItemList?.clear()

                            footballTypeItemList.addAll(specialityPojo[0].data)

                            pageNumberFootballType += 1
                            if (specialityPojo[0].data!!.size < 100) {
                                isLastpage = true
                            }
                            getFootbalType(specialityPojo[0].data as ArrayList<FootballLevelListData>)
                        }

                        relativeprogressBar.visibility = View.GONE

                        if (footballTypeItemList!!.size == 0) {
                            ll_no_data_found.visibility = View.VISIBLE
                            recyclerview.visibility = View.GONE
                        } else {
                            ll_no_data_found.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE
                        }

                    } else {
                        errromethod1()
                    }
                })
    }

    fun showSnackBar(message: String) {
        if ((ll_main_filter != null) and !isFinishing)
            Snackbar.make(this.ll_main_filter!!, message, Snackbar.LENGTH_LONG).show()

    }

    fun errorMethod() {
        try {
            if (!MyUtils.isInternetAvailable(this@FilterActivity)) {
                showSnackBar(resources.getString(R.string.error_common_network))
            } else {
                showSnackBar(resources.getString(R.string.error_crash_error_message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun errromethod1() {
        if (this != null) {
            MyUtils.dismissProgressDialog()
            relativeprogressBar.visibility = View.GONE
            recyclerview.visibility = (View.GONE)

            try {
                nointernetMainRelativelayout.visibility = View.VISIBLE
                if (MyUtils.isInternetAvailable(this)) {
                    nointernetImageview.setImageDrawable(this.getDrawable(R.drawable.something_went_wrong))
                    nointernettextview.text = resources.getString(R.string.error_crash_error_message)
                } else {
                    nointernetImageview.setImageDrawable(this.getDrawable(R.drawable.no_internet_connection))
                    nointernettextview.text = resources.getString(R.string.error_common_network)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun getGenderData() {
        listGenderItem!!.add(FilterSubItem("Male","0",false))
        listGenderItem!!.add(FilterSubItem("Female","1",false))
        //filterSubItemAdapter!!.notifyDataSetChanged()
        inflateGender(listGenderItem)

    }

    private fun inflateGender(listSubItem1: ArrayList<FilterSubItem?>?) {
        if (selectedPos != 0)
            return

        ll_no_data_found.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        listSubItem?.clear()
        var ids: List<String>? = null

        if (!genders.isNullOrEmpty()) {
            ids = genders.split(",")
        }
        listSubItem1?.forEachIndexed { indext, it ->


            var isSelected = false

            if (!ids.isNullOrEmpty()) {
                for (i in 0 until ids.size) {
                    if (ids[i].trim().equals(listGender?.get(indext).toString().trim(), true)) {
                        isSelected = true
                    }
                }
            }
            listSubItem?.add(FilterSubItem(it?.title.toString().capitalize(), indext.toString(), isSelected))
        }


        filterSubItemAdapter?.notifyDataSetChanged()

    }

    private fun getageCategoryData(footballAgeGroupList: List<AgecategoryList>) {
        if (selectedPos != 1)
            return

        ll_no_data_found.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        listSubItem?.clear()
        var ids: List<String>? = null
        if (!footballageIDs.isNullOrEmpty()) {
            ids = footballageIDs.split(",")
        }
        footballAgeGroupList.forEach {


            var isSelected = false

            if (!ids.isNullOrEmpty()) {
                for (i in 0 until ids.size) {
                    if (ids[i].trim().equals(it?.footballagecatID.toString().trim())) {
                        isSelected = true
                        break
                    }
                }
            }
            listSubItem?.add(FilterSubItem(it?.footballagecatName.toString().capitalize(), it?.footballagecatID.toString(), isSelected))
        }


        filterSubItemAdapter?.notifyDataSetChanged()

    }

    private fun getFootbalType(footballTypeItemList: ArrayList<FootballLevelListData>) {
        if (selectedPos != 2)
            return

        ll_no_data_found.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        listSubItem?.clear()
        var ids: List<String>? = null
        if (!footballTypedegreeIDs.isNullOrEmpty()) {
            ids = footballTypedegreeIDs.split(",")
        }
        footballTypeItemList.forEach {


            var isSelected = false

            if (!ids.isNullOrEmpty()) {
                for (i in 0 until ids.size) {
                    if (ids[i].trim().equals(it?.footbltypeID.toString().trim())) {
                        isSelected = true
                        break
                    }
                }
            }
            listSubItem?.add(FilterSubItem(it?.footbltypeName.toString().capitalize(), it?.footbltypeID.toString(), isSelected))
        }


        filterSubItemAdapter?.notifyDataSetChanged()
    }

    private fun getFootballLevel(footballLevelList: ArrayList<FootballLevelData>) {
        if (selectedPos != 3)
            return

        ll_no_data_found.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        listSubItem?.clear()
        var ids: List<String>? = null
        if (!footballLevelIDs.isNullOrEmpty()) {
            ids = footballLevelIDs.split(",")
        }
        footballLevelList.forEach {
            var isSelected = false
            if (!ids.isNullOrEmpty()) {
                for (i in 0 until ids.size) {
                    if (ids[i].trim().equals(it?.footbllevelID.toString().trim())) {
                        isSelected = true
                        break
                    }
                }
            }
            listSubItem?.add(FilterSubItem(it?.footbllevelName.toString().capitalize(), it?.footbllevelID.toString(), isSelected))
        }
        filterSubItemAdapter?.notifyDataSetChanged()
    }

    private fun getPitchPosition(pitchPositionItemList: ArrayList<PlayerPosData>) {
        if (selectedPos != 4)
            return

        ll_no_data_found.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        listSubItem?.clear()
        var ids: List<String>? = null
        if (!footballPitchPositionIDs.isNullOrEmpty()) {
            ids = footballPitchPositionIDs.split(",")
        }
        pitchPositionItemList.forEach {
            var isSelected = false
            if (!ids.isNullOrEmpty()) {
                for (i in 0 until ids.size) {
                    if (ids[i].trim().equals(it?.plyrposiID.toString().trim())) {
                        isSelected = true
                        break
                    }
                }
            }
            listSubItem?.add(FilterSubItem(it?.plyrposiName.toString().capitalize(), it?.plyrposiID.toString(), isSelected))
        }
        filterSubItemAdapter?.notifyDataSetChanged()
    }

    private fun getCountryData(arrayList: ArrayList<CountryListData>) {
        if (selectedPos != 5)
            return

        ll_no_data_found.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        listSubItem?.clear()
        var ids: List<String>? = null
        if (!countryIDs.isNullOrEmpty()) {
            ids = countryIDs.split(",")
        }
        arrayList.forEach {
            var isSelected = false
            if (!ids.isNullOrEmpty()) {
                for (i in 0 until ids.size) {
                    if (ids[i].trim().equals(it?.countryID.toString().trim())) {
                        isSelected = true
                        break
                    }
                }
            }
            listSubItem?.add(FilterSubItem(it?.countryName.toString().capitalize(), it?.countryID.toString(), isSelected))
        }
        filterSubItemAdapter?.notifyDataSetChanged()
    }

    private fun getPublicationTime() {
        listPublicationItem!!.clear()

        listPublicationItem!!.add(FilterSubItem("Anytime","0"))
        listPublicationItem!!.add(FilterSubItem("Today","1"))
        listPublicationItem!!.add(FilterSubItem("This Week","2"))
        listPublicationItem!!.add(FilterSubItem("This Month","3"))

        //filterSubItemAdapter!!.notifyDataSetChanged()
        inflatePublicationTime(listPublicationItem!!)
    }

    private fun inflatePublicationTime(listSubItem1: ArrayList<FilterSubItem?>) {
        if (selectedPos != 6)
            return

        ll_no_data_found.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        listSubItem?.clear()
        var ids: List<String>? = null

        if (!publicationTime.isNullOrEmpty()) {
            ids = publicationTime.split(",")
        }
        listSubItem1.forEachIndexed { indext, it ->
            var isSelected = false
            if (!ids.isNullOrEmpty()) {
                for (i in 0 until ids.size) {
                    if (ids[i].trim().equals(listPublicationTime?.get(indext).toString().trim(), true)) {
                        isSelected = true
                    }
                }
            }
            listSubItem?.add(FilterSubItem(it?.title.toString().capitalize(), indext.toString(), isSelected))
        }


        filterSubItemAdapter?.notifyDataSetChanged()
    }

    private fun getSortByData() {
        listSortByItem!!.clear()

        listSortByItem!!.add(FilterSubItem("My posts only","0"))
        listSortByItem!!.add(FilterSubItem("Connection's posts","1"))
        listSortByItem!!.add(FilterSubItem("Most Viewed","2"))
        listSortByItem!!.add(FilterSubItem("Most Starred","3"))
        listSortByItem!!.add(FilterSubItem("Most Commented","4"))

        //filterSubItemAdapter!!.notifyDataSetChanged()
        inflateSortBy(listSortByItem!!)
    }

    private fun inflateSortBy(listSubItem1: ArrayList<FilterSubItem?>) {
        if (selectedPos != 7)
            return

        ll_no_data_found.visibility = View.GONE
        recyclerview.visibility = View.VISIBLE

        listSubItem?.clear()
        var ids: List<String>? = null

        if (!sortBy.isNullOrEmpty()) {
            ids = sortBy.split(",")
        }
        listSubItem1.forEachIndexed { indext, it ->
            var isSelected = false
            if (!ids.isNullOrEmpty()) {
                for (i in 0 until ids.size) {
                    if (ids[i].trim().equals(listSortBy?.get(indext).toString().trim(), true)) {
                        isSelected = true
                    }
                }
            }
            listSubItem?.add(FilterSubItem(it?.title.toString().capitalize(), indext.toString(), isSelected))
        }


        filterSubItemAdapter?.notifyDataSetChanged()
    }

    private fun setAdapter() {
        linearLayoutManager1 = LinearLayoutManager(this@FilterActivity)

        filterSubItemAdapter =
                FilterSubItemAdapter(this@FilterActivity, object : FilterSubItemAdapter.OnItemClick {
                    override fun onClicklisneter(pos: Int, from: String) {

                        var selectedIds=""
                        selectedIds=listSubItem?.filter {
                            it?.isSelected!!
                        }!!.joinToString {
                            it!!.id.toString()
                        }
                        if (selectedPos == 0)
                        {
                            var selectedIdsGen = listSubItem?.filter {
                                it?.isSelected!!
                            }!!.joinToString {
                                if (it?.id.equals("0", false)) {
                                    "Male"
                                } else if (it?.id.equals("1", false)) {
                                    "Female"
                                } else {
                                    ""
                                }

                            }
                            var ids: List<String>? = null
                            if (!selectedIdsGen.isNullOrEmpty()) {
                                ids = selectedIdsGen.split(",")
                            }
                            var listGender1: ArrayList<String>?=ArrayList()
                            listGender?.forEachIndexed { indext, it ->

                                if (!ids.isNullOrEmpty()) {
                                    for (i in 0 until ids?.size!!) {
                                        if (ids!![i].trim().equals(listGender?.get(indext).toString().trim(), true)) {
                                            listGender1?.add(ids!![i])

                                        }
                                    }
                                }
                            }
                            genders=   listGender1?.joinToString {
                                it.toString()
                            }!!
                        }
                        else if (selectedPos == 1)
                        {
                            footballageIDs = selectedIds
                        }
                        else if (selectedPos == 2)
                        {
                            footballTypedegreeIDs = selectedIds
                        }
                        else if (selectedPos == 3)
                        {
                            footballLevelIDs = selectedIds
                        }
                        else if (selectedPos == 4)
                        {
                            footballPitchPositionIDs = selectedIds
                        }
                        else if (selectedPos == 5)
                        {
                            countryIDs = selectedIds
                        }
                        else if (selectedPos == 6)
                        {
                            var selectedIdsGen = listSubItem?.filter {
                                it?.isSelected!!
                            }!!.joinToString {
                                if (it?.id.equals("0", false)) {
                                    "Anytime"
                                } else if (it?.id.equals("1", false)) {
                                    "Today"
                                } else if (it?.id.equals("2", false)) {
                                    "This Week"
                                } else if (it?.id.equals("3", false)) {
                                    "This Month"
                                } else {
                                    "Anytime"
                                }

                            }

                            var ids: List<String>? = null
                            if (!selectedIdsGen.isNullOrEmpty()) {
                                ids = selectedIdsGen.split(",")
                            }
                            var listGender1: ArrayList<String>?=ArrayList()
                            listPublicationTime?.forEachIndexed { indext, it ->

                                if (!ids.isNullOrEmpty()) {
                                    for (i in 0 until ids?.size!!) {
                                        if (ids!![i].trim().equals(listPublicationTime?.get(indext).toString().trim(), true)) {
                                            listGender1?.add(ids!![i])

                                        }
                                    }
                                }
                            }
                            publicationTime=   listGender1?.joinToString {
                                it.toString()
                            }!!
                        }
                        else if (selectedPos == 7)
                        {
                            var selectedIdsGen = listSubItem?.filter {
                                it?.isSelected!!
                            }!!.joinToString {
                                if (it?.id.equals("0", false)) {
                                    "My posts only"
                                } else if (it?.id.equals("1", false)) {
                                    "Most Viewed"
                                } else if (it?.id.equals("2", false)) {
                                    "Most Starred"
                                } else if (it?.id.equals("3", false)) {
                                    "Most Commented"
                                } else {
                                    "My posts only"
                                }

                            }
                            var ids: List<String>? = null
                            if (!selectedIdsGen.isNullOrEmpty()) {
                                ids = selectedIdsGen.split(",")
                            }
                            var listGender1: ArrayList<String>?=ArrayList()
                            listSortBy?.forEachIndexed { indext, it ->

                                if (!ids.isNullOrEmpty()) {
                                    for (i in 0 until ids.size) {
                                        if (ids[i].trim().equals(listSortBy?.get(indext).toString().trim(), true)) {
                                            listGender1?.add(ids[i])

                                        }
                                    }
                                }
                            }
                            sortBy=   listGender1?.joinToString {
                                it.toString()
                            }!!
                        }
                    }

                }, listSubItem)

        recyclerview.layoutManager = linearLayoutManager1
        recyclerview.adapter = filterSubItemAdapter
        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager?.childCount!!
                totalItemCount = linearLayoutManager?.itemCount!!
                firstVisibleItemPosition = linearLayoutManager?.findFirstVisibleItemPosition()!!
                if (!isLoading) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 100
                    ) {

                        isLoading = true
                        getRetryApi()
                    }
                }
            }
        })

    }

    private fun getRetryApi() {
        if (selectedPos == 1) {
            getageCategoryData(footballAgeGroupList)
        } else if (selectedPos == 2) {
            getFootbalType(footballTypeItemList)
        } else if (selectedPos == 3) {
            getFootballLevel(footballLevelItemList)
        } else if (selectedPos == 4) {
            getPitchPosition(pitchPositionItemList)
        } else if (selectedPos == 5) {
            getCountryData(countryItemList)
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.imgFilterBack -> {
                onBackPressed()
            }

            R.id.txtClearAll -> {
                footballLevelIDs=""
                countryIDs=""
                sortBy=""
                footballPitchPositionIDs=""
                genders=""
                footballageIDs=""
                footballTypedegreeIDs=""
                publicationTime=""
                applyFilter(false)
                getFilterData()
            }
            R.id.lylUnSelectApply->{
                applyFilter(true)
            }
            R.id.btnRetry->{
                getRetryApi()
            }

        }
    }
    override fun onBackPressed() {
        MyUtils.finishActivity(this@FilterActivity, true)
    }
}