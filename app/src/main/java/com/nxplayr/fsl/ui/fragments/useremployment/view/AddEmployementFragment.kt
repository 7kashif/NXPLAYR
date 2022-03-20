package com.nxplayr.fsl.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.nxplayr.fsl.ui.activity.addmedia.view.AddMediaActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.viewmodel.CompanyListModel
import com.nxplayr.fsl.viewmodel.EmployementModel
import com.nxplayr.fsl.viewmodel.JobFunctionListModel
import com.nxplayr.fsl.data.model.CompanyListData
import com.nxplayr.fsl.data.model.EmploymentData
import com.nxplayr.fsl.data.model.JobFunctionList
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.bottomsheet.BottomSheetListFragment
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MonthYearPickerDialog
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_add_employement.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class AddEmployementFragment : Fragment(), View.OnClickListener, BottomSheetListFragment.SelectLanguage {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var mfUser: File? = null
    var currentapiVersion: Int = Build.VERSION.SDK_INT
    private var mImageCaptureUri: Uri? = null
    var imagePath: String = ""
    var mfAddMedia_Image: File? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var jobFunctionList: ArrayList<JobFunctionList>? = ArrayList()
    var companyList: ArrayList<CompanyListData>? = ArrayList()
    var jobfuncID = ""
    var companyID = ""
    private var employementUpdateListener: AddEmployementFragment.EmployementUpdateListener? = null
    var mediaType = ""
    var mediaFileName = ""
    var mediaImageTitle = ""
    var mediaDescription = ""
    var mediaLink = ""
    var from = ""
    var userEmplyData: EmploymentData? = null
    var position: Int = 0
    var mediaList: ArrayList<String>? = ArrayList()
    var fromMedia = ""
    var imageMediaSize = "0"
    var countryID="0"
    var cityID="0"
    var stateID=""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_add_employement, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            employementUpdateListener = context as EmployementUpdateListener
        } catch (e: Exception) {

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        ll_mainLink.visibility = View.GONE
        ll_mainMediaEmp.visibility = View.GONE

        if (arguments != null) {
            from = arguments!!.getString("from").toString()
            position = arguments!!.getInt("pos")
            userEmplyData = arguments!!.getSerializable("userEmpData") as EmploymentData
        }

        if (from.equals("edit")) {
            tvToolbarTitle.text = getString(R.string.edit_experience)
            btn_saveEmployement.progressText = resources.getString(R.string.update)
        } else {
            tvToolbarTitle.text = getString(R.string.add_experience)
            btn_saveEmployement.progressText = resources.getString(R.string.save)
        }

        setOnClickListener()

        if (userData != null) {
            setEmployementData()
        }

//        from_date_edittext.setOnClickListener {
////            val dialog = datePickerDialog("from")
////            /*  datePickerDialog(from).getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis())*/
////            dialog.show()
////
////            val day = dialog.findViewById<View>(Resources.getSystem().getIdentifier("android:id/day", null, null))
////            if (day != null) {
////                day.visibility = View.GONE
////            }
//        }

        checkbox_employement.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isChecked) {
                ll_toDate.visibility = View.GONE
            } else {
                ll_toDate.visibility = View.VISIBLE
            }
            nextButtonEnable()
        }

        textWatchers()
    }


    fun setOnClickListener() {
        jobTTitle_edit_text.setOnClickListener(this)
        companyname_edit_text.setOnClickListener(this)
        btn_saveEmployement.setOnClickListener(this)
        from_date_edittext.setOnClickListener(this)
        to_date_edittext.setOnClickListener(this)
        btn_setLink.setOnClickListener(this)
        btn_uploadImage.setOnClickListener(this)
        editMedia_imageName.setOnClickListener(this)
        editLink_imageName.setOnClickListener(this)
        closeLink_imageName.setOnClickListener(this)
        close_iconImage.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.jobTTitle_edit_text -> {
                getJobFunctionList()
            }
            R.id.companyname_edit_text -> {
                getCompanyList()
            }
            R.id.btn_saveEmployement -> {
                checkValidation()
            }
            R.id.from_date_edittext -> {
                setMonthPickerDialog("from")
            }
            R.id.to_date_edittext -> {
                if (from_date_edittext.text.isNullOrEmpty()) {
                    MyUtils.showSnackbar(mActivity!!, "Please select from date", ll_main_addEmployement)
                } else {
                    setMonthPickerDialog("to")
                }
            }
            R.id.btn_setLink -> {
                Intent(mActivity!!, AddMediaActivity()::class.java).apply {
                    putExtra("type", "Employement")
                    putExtra("mediaType", "Link")
                    putExtra("from", "Add")
                    startActivityForResult(this, 1005)
                }
            }
            R.id.btn_uploadImage -> {
                Intent(mActivity!!, AddMediaActivity()::class.java).apply {
                    putExtra("type", "Employement")
                    putExtra("mediaType", "Image")
                    putExtra("from", "Add")
                    startActivityForResult(this, 1006)
                }
            }
            R.id.editMedia_imageName -> {
                if (!mediaFileName.isNullOrEmpty()/* && mediaType.equals("Image")*/) {
                    Intent(mActivity!!, AddMediaActivity()::class.java).apply {
                        putExtra("from", "edit")
                        putExtra("type", "Employement")
                        putExtra("mediaType", "Image")
                        putExtra("editMediaImage", mediaFileName)
                        putExtra("editMediaTitle", mediaImageTitle)
                        putExtra("editMediaDes", mediaDescription)
                        startActivityForResult(this, 1006)
                    }
                }
            }
            R.id.editLink_imageName -> {
                if (!mediaLink.isNullOrEmpty()/* && mediaType.equals("Link")*/) {
                    Intent(mActivity!!, AddMediaActivity()::class.java).apply {
                        putExtra("from", "edit")
                        putExtra("type", "Employement")
                        putExtra("mediaType", "Link")
                        putExtra("editlinkMedia", mediaLink)
                        startActivityForResult(this, 1005)
                    }
                }
            }
            R.id.closeLink_imageName -> {
                if (!mediaLink.isNullOrEmpty()) {
                    for (i in 0 until mediaList!!.size) {
                        if (mediaList!![i].equals("Link")) {
                            mediaList?.removeAt(i)
                            Log.e("mediali", mediaList.toString())
                            break
                        }
                    }
                    ll_mainLink.visibility = View.GONE
                }
            }
            R.id.close_iconImage -> {
                if (!mediaFileName.isNullOrEmpty()) {
                    for (i in 0 until mediaList!!.size) {
                        if (mediaList!![i].equals("Image")) {
                            mediaList?.removeAt(i)
                            Log.e("mediali", mediaList.toString())
                            break
                        }
                    }
                    ll_mainMediaEmp.visibility = View.GONE
                }
            }
        }
    }

    fun setEmployementData() {
        if (from.equals("edit") && userEmplyData!=null) {
            companyID = userEmplyData?.companyID!!
            jobfuncID=userEmplyData?.jobfuncID!!

            stateID=userEmplyData?.stateID!!
            cityID=userEmplyData?.cityID!!
            countryID=userEmplyData?.countryID!!

            jobTTitle_edit_text.setText(userEmplyData!!.jobfuncName)
            companyname_edit_text.setText(userEmplyData!!.companyName)
            location_edit_text.setText(userEmplyData!!.cityName)
            try {
                var fromDate = MyUtils.formatDate(userEmplyData!!.useremployementPeriodOfTimeFrom, "dd-MM-yyyy hh:mm:ss", "MM/yyyy")
                from_date_edittext.setText(fromDate)
                if (!userEmplyData!!.useremployementPeriodOfTimeTo.isNullOrEmpty()) {
                    var toDate = MyUtils.formatDate(userEmplyData!!.useremployementPeriodOfTimeTo, "dd-MM-yyyy hh:mm:ss", "MM/yyyy")
                    ll_toDate.visibility = View.VISIBLE
                    to_date_edittext.setText(toDate)
                    checkbox_employement.isChecked = false
                } else {
                    ll_toDate.visibility = View.GONE
                    checkbox_employement.isChecked = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            txt_description.setText(userEmplyData!!.useremployementDescription)
            if (!userEmplyData!!.media.isNullOrEmpty()) {


                for (i in 0 until userEmplyData!!.media.size) {

                    if (!userEmplyData!!.media[i].userempmediaType.isNullOrEmpty()) {
                        mediaList!!.add(userEmplyData!!.media[i].userempmediaType)
                        Log.e("list_media", mediaList.toString())
                    }

                    if (!userEmplyData!!.media[i].userempmediaFile.isNullOrEmpty()) {
                        if (userEmplyData!!.media[i].userempmediaType.equals("Image")) {
                            ll_mainMediaEmp.visibility = View.VISIBLE
                            mediaType = userEmplyData!!.media[i].userempmediaType
                            mediaFileName = userEmplyData!!.media[i].userempmediaFile
                            mediaImageTitle = userEmplyData!!.media[i].userempmediaTitle
                            add_media_image.setImageURI(RestClient.image_base_url_mediaEmp + userEmplyData!!.media[i].userempmediaFile)
                            media_imageName.text = userEmplyData!!.media[i].userempmediaTitle
                            mediaDescription = userEmplyData!!.media[i].userempmediaDescription
                        } else {
//                                ll_mainMediaEmp.visibility = View.GONE
                        }
                        if (userEmplyData!!.media[i].userempmediaType.equals("Link")) {
                            ll_mainLink.visibility = View.VISIBLE
                            mediaType = userEmplyData!!.media[i].userempmediaType
                            mediaLink = userEmplyData!!.media[i].userempmediaFile
                            media_linkName.text = userEmplyData!!.media[i].userempmediaFile
                        } else {
//                                ll_mainLink.visibility = View.GONE
                        }

                    } else {
                        ll_mainLink.visibility = View.GONE
                        ll_mainMediaEmp.visibility = View.GONE
                    }
                }
            }
        }else{
                btn_saveEmployement.strokeColor = (resources.getColor(R.color.colorPrimary))
                btn_saveEmployement.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_saveEmployement.textColor = resources.getColor(R.color.black)
            }


    }

    fun setMonthPickerDialog(from: String) {
        val pickerDialog = MonthYearPickerDialog()

        val c = Calendar.getInstance()


        pickerDialog.setListener(DatePickerDialog.OnDateSetListener { datePicker, year, month, i2 ->

            val months = (if (month < 10) "0$month" else month)
            val selected = "$year,$months"
            val selected_year = "$year"
            val selected_month = "$months"
            var monthOfYear = month
            c.set(Calendar.MONTH, monthOfYear)
            c.set(Calendar.YEAR, year)
            c.set(Calendar.DAY_OF_MONTH, month)
            val currentDate = MyUtils.calenderToString(Calendar.getInstance())
            Log.e("curent", currentDate)

            var toDate = MyUtils.formatDate(selected, "yyyy,MM", "MM/yyyy")
            var fromdate = MyUtils.formatDate(selected, "yyyy,MM", "MM/yyyy")

            try {
                if (from.equals("from")) {
                    if ((selected.compareTo(currentDate) > 0)) {
                        MyUtils.showSnackbar(mActivity!!, "Please select valid from date", ll_main_addEmployement)
                    } else {
                        from_date_edittext.setText(fromdate)
                    }
                } else if (from.equals("to")) {
                    var date = MyUtils.formatDate(from_date_edittext.text.toString(), "MM/yyyy", "yyyy,MM")
                    var fromYear = MyUtils.formatDate(date, "yyyy,MM", "yyyy")
                    var fromMonth = MyUtils.formatDate(date, "yyyy,MM", "MM")

                    if ((selected.compareTo(currentDate) > 0)) {
                        MyUtils.showSnackbar(mActivity!!, "Please select valid to date", ll_main_addEmployement)
                    } else if ((selected_month.compareTo(fromMonth) <= 0) && (selected_year.compareTo(fromYear) <= 0)) {
                        MyUtils.showSnackbar(mActivity!!, "Please select valid to date", ll_main_addEmployement)
                    } else if ((selected_month.compareTo(fromMonth) >= 0) && (selected_year.compareTo(fromYear) >= 0)) {
                        to_date_edittext.setText(toDate)
                    } else {
                        to_date_edittext.setText(toDate)
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            nextButtonEnable()
        })
        pickerDialog.show(childFragmentManager, "MonthYearPickerDialog")
    }

    fun textWatchers() {
        jobTTitle_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })

        companyname_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
        location_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }

        })
    }

    fun nextButtonEnable() {
        if (validateAddWork()) {
            btn_saveEmployement.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_saveEmployement.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_saveEmployement.textColor = resources.getColor(R.color.black)
        } else {
            btn_saveEmployement.strokeColor = (resources.getColor(R.color.grayborder))
            btn_saveEmployement.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_saveEmployement.textColor = resources.getColor(R.color.colorPrimary)
        }
    }

    fun validateAddWork(): Boolean {
        var valid: Boolean = false
        when {
            jobTTitle_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            companyname_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            location_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            from_date_edittext.text.toString().trim().isEmpty() -> {
                return valid
            }
            (to_date_edittext.text.toString().trim().isEmpty() && (!checkbox_employement.isChecked)) -> {
                return valid
            }
            else -> {
                valid = true
            }
        }
        return valid
    }

    private fun checkValidation() {
        MyUtils.hideKeyboard1(mActivity!!)
        if (TextUtils.isEmpty(jobTTitle_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter Job Title", ll_main_addEmployement)
            jobTTitle_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(companyname_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter Company Name", ll_main_addEmployement)
            companyname_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(location_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter Location", ll_main_addEmployement)
            location_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(from_date_edittext.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Select from date", ll_main_addEmployement)
            from_date_edittext.requestFocus()

        } else if (!checkbox_employement.isChecked && TextUtils.isEmpty(to_date_edittext.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please select To date", ll_main_addEmployement)
            to_date_edittext.requestFocus()
        } /*else if (TextUtils.isEmpty(txt_description.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter Description", ll_main_addEmployement)
            txt_description.requestFocus()
        }*/ else {
            if (userEmplyData != null) {
                getEmployement("Edit", userEmplyData!!.useremployementID)
            } else {
                getEmployement("Add", "")
            }
        }
    }


    private fun getJobFunctionList() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", "")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        var jobFunctionListModel =
                ViewModelProviders.of(this@AddEmployementFragment).get(JobFunctionListModel::class.java)
        jobFunctionListModel.getJobFunctionList(mActivity!!, false, jsonArray.toString())
                .observe(this@AddEmployementFragment!!,
                        androidx.lifecycle.Observer { jobFunctionListpojo ->
                            if (jobFunctionListpojo != null && jobFunctionListpojo.isNotEmpty()) {

                                if (jobFunctionListpojo[0].status.equals("true", false)) {
                                    MyUtils.dismissProgressDialog()

                                    jobFunctionList!!.clear()
                                    jobFunctionList!!.addAll(jobFunctionListpojo[0].data)
                                    openJonFunctionListBottomSheet(jobFunctionList!!)
                                } else {
                                    MyUtils.dismissProgressDialog()
                                    MyUtils.showSnackbar(mActivity!!, jobFunctionListpojo[0].message, ll_main_addEmployement)
                                }

                            } else {
                                MyUtils.dismissProgressDialog()
                                Toast.makeText(mActivity!!,R.string.error_common_network,Toast.LENGTH_SHORT).show()
                            }
                        })


    }


    private fun getCompanyList() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", "")

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)

        var companyListModel =
                ViewModelProviders.of(this@AddEmployementFragment).get(CompanyListModel::class.java)
        companyListModel.getCompanyList(mActivity!!, false, jsonArray.toString())
                .observe(this@AddEmployementFragment!!,
                        androidx.lifecycle.Observer { companyListpojo ->
                            if (companyListpojo != null && companyListpojo.isNotEmpty()) {

                                if (companyListpojo[0].status.equals("true", false)) {
                                    MyUtils.dismissProgressDialog()

                                    companyList!!.clear()
                                    companyList!!.addAll(companyListpojo[0].data)
                                    openCompanyListBottomSheet(companyList!!)
                                } else {
                                    MyUtils.dismissProgressDialog()
                                    MyUtils.showSnackbar(mActivity!!, companyListpojo[0].message, ll_main_addEmployement)
                                }

                            } else {
                                MyUtils.dismissProgressDialog()
                                Toast.makeText(mActivity!!,R.string.error_common_network,Toast.LENGTH_SHORT).show()
                            }
                        })


    }


    private fun openJonFunctionListBottomSheet(data: ArrayList<JobFunctionList>) {

        var bottomSheetData = ArrayList<String>()
        bottomSheetData.clear()
        for (i in 0 until data.size) {
            bottomSheetData.add(data[i]!!.jobfuncName!!)
        }

        val bottomSheet = BottomSheetListFragment()
        val bundle = Bundle()
        bundle.putString("from", "JobFunName")
        bundle.putSerializable("data", bottomSheetData)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")
    }

    private fun openCompanyListBottomSheet(data: ArrayList<CompanyListData>) {

        var bottomSheetData = ArrayList<String>()
        bottomSheetData.clear()
        for (i in 0 until data.size) {
            bottomSheetData.add(data[i]!!.companyName!!)
        }

        val bottomSheet = BottomSheetListFragment()
        val bundle = Bundle()
        bundle.putString("from", "CompanyName")
        bundle.putSerializable("data", bottomSheetData)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")
    }


    fun getEmployement(from: String, useremployementID: String) {
        btn_saveEmployement.startAnimation()
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()
        val jsonArrayMedia = JSONArray()

        try {
            jsonObject.put("languageID", "1")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("companyName", companyname_edit_text.text.toString().trim())
            jsonObject.put("countryName", userData?.userHomeCountryName)
            jsonObject.put("useremployementPeriodOfTimeFrom", MyUtils.formatDate(from_date_edittext.text.toString(), "MM/yyyy", "dd-MM-yyyy hh:mm:ss"))
            if (checkbox_employement.isChecked) {
                jsonObject.put("useremployementPeriodOfTimeTo", "")
                jsonObject.put("useremployementIsCurrent", "Yes")
            } else {
                jsonObject.put("useremployementPeriodOfTimeTo", MyUtils.formatDate(to_date_edittext.text.toString(), "MM/yyyy", "dd-MM-yyyy hh:mm:ss"))
                jsonObject.put("useremployementIsCurrent", "No")
            }
            jsonObject.put("cityName", location_edit_text.text.toString().trim())
            jsonObject.put("stateName", "")
            jsonObject.put("jobfuncName", jobTTitle_edit_text.text.toString().trim())
            jsonObject.put("jobfuncID", jobfuncID)
            jsonObject.put("companyD", companyID)
            jsonObject.put("stateID", stateID)
            jsonObject.put("cityID", cityID)
            jsonObject.put("countryID", countryID)
            jsonObject.put("useremployementDescription", txt_description.text.toString().trim())
            jsonObject.put("useremployementPrivacyType", "Public")
            jsonObject.put("useremployementPrivacyData", "")
            jsonObject.put("useremployementLatitude", "")
            jsonObject.put("useremployementLongitude", "")
            if (userEmplyData != null) {
                jsonObject.put("useremployementID", useremployementID)
            }
//                    || !userData!!.employement[0].media.isNullOrEmpty()
            if (!mediaList.isNullOrEmpty() ) {
                for (i in 0 until mediaList!!.size) {
                    val mediaPojo = JSONObject()
                    if (mediaList!![i].equals("Image")) {

                        mediaPojo.put("userempmediaType", "Image")
                        mediaPojo.put("userempmediaFile", mediaFileName)
                        mediaPojo.put("userempmediaTitle", media_imageName.text.toString().trim())
                        mediaPojo.put("userempmediaDescription", mediaDescription)
                        mediaPojo.put("userempmediaFileSize", imageMediaSize)

                    }
                    if (mediaList!![i].equals("Link")) {
//                        val mediaPojo = JSONObject()
                        mediaPojo.put("userempmediaType", "Link")
                        mediaPojo.put("userempmediaFile", media_linkName.text.toString().trim())
                        mediaPojo.put("userempmediaTitle", "")
                        mediaPojo.put("userempmediaDescription", "")
                        mediaPojo.put("userempmediaFileSize", imageMediaSize)

                    }
                    jsonArrayMedia.put(mediaPojo)

                }

            }
            jsonObject.put("media", jsonArrayMedia)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

        var addEmployementModel =
                ViewModelProviders.of(this@AddEmployementFragment).get(EmployementModel::class.java)
        addEmployementModel.getEmployement(mActivity!!, false, jsonArray.toString(), from)
                .observe(this@AddEmployementFragment!!,
                        androidx.lifecycle.Observer { loginPojo ->
                            if (loginPojo != null) {
                                btn_saveEmployement.endAnimation()
                                if (loginPojo.get(0).status.equals("true", false)) {
                                    try {
//                                        userData?.employement?.clear()
                                        if (from.equals("Add")) {
                                            userData?.employement?.addAll(loginPojo.get(0).data)
                                            StoreSessionManager(userData)
                                        } else if (from.equals("Edit")) {
                                            for (i in 0 until userData?.employement!!.size) {
                                                if (useremployementID.equals(userData!!.employement!![i]!!.useremployementID))
                                                    userData!!.employement!![i] = loginPojo!![0].data!![0]
                                                break
                                            }
                                            StoreSessionManager(userData)
                                            if (employementUpdateListener != null)
                                                employementUpdateListener!!.onEmployementUpdate()

                                        }
                                        Handler().postDelayed({
                                            (activity as MainActivity).onBackPressed()
                                        }, 2000)

                                        MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message, ll_main_addEmployement)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }

                                } else {
                                    MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message, ll_main_addEmployement)
                                }

                            } else {
                                btn_saveEmployement.endAnimation()
                                ErrorUtil.errorMethod(ll_main_addEmployement)
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

    override fun onLanguageSelect(value: String, from: String) {
        when (from) {
            "JobFunName" -> {
                jobTTitle_edit_text.setText(value)
                for (i in 0 until jobFunctionList!!.size) {
                    if (value.equals(jobFunctionList!![i]!!.jobfuncName, false)) {
                        if (!jobfuncID.equals(jobFunctionList!![i]!!.jobfuncID))
                            jobfuncID = jobFunctionList!![i]!!.jobfuncID
                    }
                }
            }
            "CompanyName" -> {
                companyname_edit_text.setText(value)
                for (i in 0 until companyList!!.size) {
                    if (value.equals(companyList!![i]!!.companyName, false)) {
                        if (!companyID.equals(companyList!![i]!!.companyID))
                            companyID = companyList!![i]!!.companyID
                    }
                }
            }
        }

    }


    private fun errorMethod() {
        relativeprogressBar.visibility = View.GONE
        try {
            nointernetMainRelativelayout.visibility = View.VISIBLE
            if (MyUtils.isInternetAvailable(mActivity!!)) {
                nointernetImageview.setImageDrawable(mActivity!!.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(mActivity!!.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1006 -> {
                if (data != null) {
                    mediaImageTitle = data.getStringExtra("title")!!
                    mediaDescription = data.getStringExtra("description")!!
                    mediaFileName = data.getStringExtra("imageMedia")!!
                    mediaType = data.getStringExtra("media_type")!!
                    fromMedia = data.getStringExtra("fromMedia")!!
                    imageMediaSize = data.getStringExtra("imageMediaSize")!!

                    if (fromMedia.equals("AddMedia")) {
                        if (!mediaFileName.isNullOrEmpty() || !mediaImageTitle.isNullOrEmpty()) {
                            if (!mediaFileName.equals("null") || !mediaImageTitle.equals("null")) {
                                ll_mainMediaEmp.visibility = View.VISIBLE
                                add_media_image.setImageURI(RestClient.image_base_url_mediaEmp + mediaFileName)
                                media_imageName.text = mediaImageTitle

                                if (!mediaFileName.isNullOrEmpty()) {
                                    mediaList?.add(mediaType)
                                    Log.e("listmedia", mediaList.toString())
                                }
                            }
                        } else {
                            ll_mainMediaEmp.visibility = View.GONE
                        }
                    }
                    if (fromMedia.equals("EditMedia")) {
                        if (!mediaFileName.isNullOrEmpty() || !mediaImageTitle.isNullOrEmpty()) {
                            if (!mediaFileName.equals("null") || !mediaImageTitle.equals("null")) {
                                ll_mainMediaEmp.visibility = View.VISIBLE
                                add_media_image.setImageURI(RestClient.image_base_url_mediaEmp + mediaFileName)
                                media_imageName.text = mediaImageTitle

                                for (i in 0 until mediaList!!.size) {
                                    if (mediaList!![i].equals("Image")) {
                                        mediaList?.removeAt(i)
                                        break
                                    }
                                }

                                if (!mediaFileName.isNullOrEmpty()) {
                                    mediaList?.add(mediaType)
                                    Log.e("listmedia", mediaList.toString())
                                }
                            }
                        } else {
                            ll_mainMediaEmp.visibility = View.GONE
                        }
                    }

                }
            }
            1005 -> {
                if (data != null) {
                    mediaLink = data.getStringExtra("link_media")!!
                    mediaType = data.getStringExtra("media_type")!!
                    fromMedia = data.getStringExtra("fromMedia")!!

                    if (fromMedia.equals("AddLink")) {
                        if (!mediaLink.isNullOrEmpty()) {
                            if (!mediaLink.equals("null")) {
                                ll_mainLink.visibility = View.VISIBLE
                                media_linkName.text = mediaLink

                                if (!mediaLink.isNullOrEmpty()) {
                                    mediaList?.add(mediaType)
                                    Log.e("listmedia", mediaList.toString())
                                }
                            }
                        } else {
                            ll_mainLink.visibility = View.GONE
                        }
                    }
                    if (fromMedia.equals("EditLink")) {
                        if (!mediaLink.isNullOrEmpty()) {
                            if (!mediaLink.equals("null")) {
                                ll_mainLink.visibility = View.VISIBLE
                                media_linkName.text = mediaLink

                                for (i in 0 until mediaList!!.size) {
                                    if (mediaList!![i].equals("Link")) {
                                        mediaList?.removeAt(i)
                                        break
                                    }
                                }
                                if (!mediaLink.isNullOrEmpty()) {
                                    mediaList?.add(mediaType)
                                    Log.e("listmedia", mediaList.toString())
                                }
                            }
                        } else {
                            ll_mainLink.visibility = View.GONE
                        }
                    }
                }
            }
        }

    }

    interface EmployementUpdateListener {
        fun onEmployementUpdate()
    }


}




