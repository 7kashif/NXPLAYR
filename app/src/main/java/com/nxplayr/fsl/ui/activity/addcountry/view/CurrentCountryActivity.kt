package com.nxplayr.fsl.ui.activity.addcountry.view

import android.content.Intent
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.compat.AutocompleteFilter
import com.google.android.libraries.places.compat.AutocompletePrediction
import com.google.android.libraries.places.compat.GeoDataClient
import com.google.android.libraries.places.compat.Places
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.addcountry.adapter.PlaceAutoCompleteAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.addcountry.viewmmodel.AddLocationModel
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_current_location.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class CurrentCountryActivity : AppCompatActivity(),View.OnClickListener {

    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    private var placeAutoCompleteAdapter: PlaceAutoCompleteAdapter? = null
    private var typeFilter: AutocompleteFilter? = null
    private var mGeoDataClient: GeoDataClient? = null
    private var BOUNDS_INDIA: LatLngBounds? = null
    private val STYLE_BOLD = StyleSpan(Typeface.BOLD)
    var LATITUDE: Double? = null
    var LONGITUDE: Double? = null
    private lateinit var  addLocationModel: AddLocationModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_current_location)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)

        sessionManager = SessionManager(this)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupViewModel()
        setupUI()
    }

    private fun setupUI() {
        tvToolbarTitle.setText(R.string.current_country)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        ll_gpsLayout.setOnClickListener(this)

        val bundle = intent.extras
        LATITUDE = bundle!!.getDouble("latitude")
        LONGITUDE = bundle.getDouble("longitude")
        BOUNDS_INDIA = LatLngBounds(LatLng(LATITUDE!! - 0.5, LONGITUDE!! - 0.5), LatLng(LATITUDE!! + 0.5, LONGITUDE!! + 0.5))

        Log.e("bounds", BOUNDS_INDIA.toString())

        typeFilter = AutocompleteFilter.Builder()
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
            .setCountry("IN")
            .build()

        mGeoDataClient = Places.getGeoDataClient(this)

        initView()
        initRV()
    }

    private fun setupViewModel() {
        addLocationModel = ViewModelProvider(this@CurrentCountryActivity).get(AddLocationModel::class.java)
    }


    private fun initView() {
        search_location.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val str = search_location.text.toString()
                if (str.isNotEmpty()) {

                    if (placeAutoCompleteAdapter != null) {
                        ll_gpsLayout.visibility = View.GONE
                        RVViewSearchPlace.visibility = View.VISIBLE
                        RVViewSearchPlace.adapter = placeAutoCompleteAdapter
                        placeAutoCompleteAdapter?.notifyDataSetChanged()
                    } else {
                        RVViewSearchPlace.visibility = View.GONE
                        ll_gpsLayout.visibility = View.VISIBLE
                    }
                } else if (str.length == 0) {
                    placeAutoCompleteAdapter?.notifyDataSetChanged()
                    ll_gpsLayout.visibility = View.VISIBLE
                    RVViewSearchPlace.visibility = View.GONE
                }

                if (s.toString() != "") {
                    placeAutoCompleteAdapter!!.filter.filter(s.toString())
                }

            }
        })
    }

    private fun initRV() {

        val layoutManager = LinearLayoutManager(this)
        RVViewSearchPlace.layoutManager = layoutManager
        placeAutoCompleteAdapter = PlaceAutoCompleteAdapter(this, mGeoDataClient!!, BOUNDS_INDIA!!, typeFilter!!,
                object : PlaceAutoCompleteAdapter.PlaceAutoCompleteInterface {
                    override fun onPlaceClick(mResultList: ArrayList<AutocompletePrediction>, position: Int) {

                        addLocationApi(mResultList[position].getFullText(STYLE_BOLD))

                    }
                })
        RVViewSearchPlace.adapter = placeAutoCompleteAdapter
    }

    private fun addLocationApi(fullText: CharSequence) {

        MyUtils.showProgressDialog(this@CurrentCountryActivity, "Please wait...")

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("countryName", "")
            jsonObject.put("stateName", "")
            jsonObject.put("languageID", "1")
            jsonObject.put("countryID", "")
            jsonObject.put("stateID", "0")
            jsonObject.put("cityID", "")
            jsonObject.put("cityName",fullText)
            jsonObject.put("userlocationPincode", "")
            jsonObject.put("userlocationType", "Current")

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        addLocationModel.getLocation(this@CurrentCountryActivity, false, jsonArray.toString())
                .observe(this@CurrentCountryActivity,
                        androidx.lifecycle.Observer { locationListPojo ->
                            if (locationListPojo != null) {
                                MyUtils.dismissProgressDialog()
                                if (locationListPojo.get(0).status.equals("true", false)) {
                                    try {
//
                                        userData?.location?.addAll(locationListPojo.get(0).data)
                                         Intent().apply {
                                             setResult(503)
                                        }
                                        StoreSessionManager(userData)
                                        onBackPressed()
                                        MyUtils.showSnackbar(this, locationListPojo.get(0).message, ll_mainCurrentLocation)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                } else {
                                    MyUtils.showSnackbar(this, locationListPojo.get(0).message, ll_mainCurrentLocation)
                                }

                            } else {
                                ErrorUtil.errorMethod(ll_mainCurrentLocation)
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

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.ll_gpsLayout->{
                 if(LONGITUDE!=null && LATITUDE!=null)
                 {
                     CurrentCityName(LATITUDE!!,LONGITUDE!!)
                 }
            }
        }

    }




    private fun CurrentCityName(lattitude: Double, longitude: Double) {
        var geocoder: Geocoder?=null
        var addresses: List<Address>? = null
        geocoder = Geocoder(this@CurrentCountryActivity, Locale.getDefault())

        try {

            addresses = geocoder.getFromLocation(
                    lattitude,
                    longitude,
                    1
            )

            if (addresses != null) {

               var location=addresses[0].locality+", "+addresses[0].adminArea+", "+addresses[0].countryName
                addLocationApi(location)

            }

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@CurrentCountryActivity,true)
    }
}
