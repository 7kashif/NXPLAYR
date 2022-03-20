package com.nxplayr.fsl.ui.activity.post.view

import android.content.*
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.compat.AutocompleteFilter
import com.google.android.libraries.places.compat.AutocompletePrediction
import com.google.android.libraries.places.compat.GeoDataClient
import com.google.android.libraries.places.compat.Places
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.activity.addcountry.adapter.PlaceAutoCompleteAdapter
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.activity_create_post_select_place.*
import kotlinx.android.synthetic.main.toolbar.*


open class CreatePostSelectPlaceActivity: AppCompatActivity()  {

    private var placeAutoCompleteAdapter: PlaceAutoCompleteAdapter? = null
    private var typeFilter: AutocompleteFilter? = null
    private var mGeoDataClient: GeoDataClient? = null
    private var BOUNDS_INDIA: LatLngBounds? = null
    private val STYLE_BOLD = StyleSpan(Typeface.BOLD)
    var LATITUDE: Double? = null
    var LONGITUDE: Double? = null
    var from="Place"

    private val mYourBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // now you can call all your fragments method here
            MyUtils.finishActivity(this@CreatePostSelectPlaceActivity, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post_select_place)
        LocalBroadcastManager.getInstance(this@CreatePostSelectPlaceActivity)
                .registerReceiver(mYourBroadcastReceiver, IntentFilter("CreatePost"))

        setupUI()

    }

    private fun setupUI() {
        tvToolbarTitle.text = resources.getString(R.string.select_place)
        toolbar.setNavigationIcon(R.drawable.back_arrow_signup)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        recyclerViewSearchPlace.visibility = View.GONE
        imageViewLocationShowHide.visibility = View.VISIBLE
        textViewLocationShowHide.visibility = View.VISIBLE

        from =if(intent.extras != null && intent.hasExtra("From")){
            intent.extras!!.getString("From", "") }
        else{""}
        val bundle = intent.extras
        LATITUDE= bundle!!.getDouble("latitude")
        LONGITUDE = bundle.getDouble("longitude")
        BOUNDS_INDIA= LatLngBounds(LatLng(LATITUDE!! - 0.5, LONGITUDE!! - 0.5), LatLng(LATITUDE!! + 0.5, LONGITUDE!!  + 0.5))
        Log.e("bounds", BOUNDS_INDIA.toString())

        typeFilter = AutocompleteFilter.Builder()
            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE)
            .setCountry("IN")
            .build()
        mGeoDataClient = Places.getGeoDataClient(this)
        initView()
        initRV()
    }

    private fun initView() {

        searchPlaceEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val str= searchPlaceEditText.text.toString()
                if (str.isNotEmpty()) {

                    if (placeAutoCompleteAdapter != null) {
                        imageViewLocationShowHide.visibility = View.GONE
                        textViewLocationShowHide.visibility = View.GONE
                        recyclerViewSearchPlace.visibility = View.VISIBLE
                        recyclerViewSearchPlace.adapter = placeAutoCompleteAdapter
                        placeAutoCompleteAdapter?.notifyDataSetChanged()
                    } else {
                        recyclerViewSearchPlace.visibility = View.GONE
                        imageViewLocationShowHide.visibility = View.VISIBLE
                        textViewLocationShowHide.visibility = View.VISIBLE
                    }
                } else if(str.length == 0) {
                    placeAutoCompleteAdapter?.notifyDataSetChanged()
                    imageViewLocationShowHide.visibility = View.VISIBLE
                    textViewLocationShowHide.visibility = View.VISIBLE
                    recyclerViewSearchPlace.visibility= View.GONE
                }

                if (s.toString() != "") {
                    placeAutoCompleteAdapter!!.filter.filter(s.toString())
                }

            }
        })
    }

    private fun initRV() {

        val layoutManager = LinearLayoutManager(this)
        recyclerViewSearchPlace.layoutManager = layoutManager
        placeAutoCompleteAdapter = PlaceAutoCompleteAdapter(this, mGeoDataClient!!, BOUNDS_INDIA!!, typeFilter!!,
                object : PlaceAutoCompleteAdapter.PlaceAutoCompleteInterface {

            override fun onPlaceClick(mResultList: ArrayList<AutocompletePrediction>, position: Int) {
                placeAutoCompleteAdapter?.notifyDataSetChanged()
                val intent=  Intent()
                intent.putExtra("Place","Place")
                intent.putExtra("From",from)
                intent.putExtra("latitude", LATITUDE)
                intent.putExtra("longitude", LONGITUDE)
                intent.putExtra("Location", placeAutoCompleteAdapter!!.getItem(position).getPrimaryText(STYLE_BOLD).toString())
                intent.putExtra("LocationAddress", placeAutoCompleteAdapter!!.getItem(position).getFullText(STYLE_BOLD).toString())
                setResult(1002,intent)
                finish()
            }
        })
        recyclerViewSearchPlace.adapter = placeAutoCompleteAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this@CreatePostSelectPlaceActivity)
                .unregisterReceiver(mYourBroadcastReceiver)
    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@CreatePostSelectPlaceActivity, true)

    }
}
