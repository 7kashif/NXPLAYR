package com.nxplayr.fsl.ui.fragments.userprofile.view

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.nxplayr.fsl.ui.activity.addmedia.view.AddMediaActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.DegreeListData
import com.nxplayr.fsl.data.model.EducationData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UniversityListData
import com.nxplayr.fsl.ui.fragments.bottomsheet.BottomSheetListFragment
import com.nxplayr.fsl.viewmodel.*
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MonthYearPickerDialog
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.fragment_add_education.*
import kotlinx.android.synthetic.main.fragment_add_education.editMedia_imageName
import kotlinx.android.synthetic.main.fragment_add_employement.*
import kotlinx.android.synthetic.main.fragment_add_languages.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class AddEducationFragment : Fragment(), View.OnClickListener,
    BottomSheetListFragment.SelectLanguage {

    private var v: View? = null
    private var mActivity: AppCompatActivity? = null
    private var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var addEducationList: ArrayList<EducationData>? = ArrayList()
    var degreeListData: ArrayList<DegreeListData>? = ArrayList()
    var universityListData: ArrayList<UniversityListData>? = ArrayList()
    var degreeID = ""
    var universityID = ""
    private var educationUpdateListener: EducationUpdateListener? = null
    var mediaTypeEducation = ""
    var mediaEduFileName = ""
    var serverfileSizeeducation = "0"
    var mediaEduImageTitle = ""
    var mediaEduDescription = ""
    var mediaEduLink = ""
    var from = ""
    var mediaList: ArrayList<String>? = ArrayList()
    var userEduData: EducationData? = null
    var position: Int = 0
    var fromMedia = ""
    var addEducationModel = EducationModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_add_education, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            educationUpdateListener = context as EducationUpdateListener
        } catch (e: Exception) {

        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        ll_mediaLinkEdu.visibility = View.GONE
        ll_mediaImageEdu.visibility = View.GONE

        if (arguments != null) {
            from = arguments!!.getString("from").toString()
            position = arguments!!.getInt("pos")
            userEduData = arguments!!.getSerializable("usereduData") as EducationData
        }

        addEducationModel =
            ViewModelProvider(this@AddEducationFragment).get(EducationModel::class.java)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        if (from.equals("edit")) {
            tvToolbarTitle.text = getString(R.string.edit_education)
            btn_save_education_detail.progressText = resources.getString(R.string.update)
            if (sessionManager != null && sessionManager?.LanguageLabel != null) {
                if (!sessionManager?.LanguageLabel?.lngUpdateEducation.isNullOrEmpty())
                    tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngUpdateEducation
            }
        } else {
            tvToolbarTitle.text = getString(R.string.add_education)
            btn_save_education_detail.progressText = resources.getString(R.string.save)
            if (sessionManager != null && sessionManager?.LanguageLabel != null) {
                if (!sessionManager?.LanguageLabel?.lngAddEducation.isNullOrEmpty())
                    tvToolbarTitle.text = sessionManager?.LanguageLabel?.lngAddEducation
            }
        }

        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_save_education_detail.progressText = sessionManager?.LanguageLabel?.lngSave
            if (!sessionManager?.LanguageLabel?.lngSchoolCollege.isNullOrEmpty())
                tv_university_edit_text.hint = sessionManager?.LanguageLabel?.lngSchoolCollege
            if (!sessionManager?.LanguageLabel?.lngDegree.isNullOrEmpty())
                tv_degree_edit_text.hint = sessionManager?.LanguageLabel?.lngDegree
            if (!sessionManager?.LanguageLabel?.lngGrade.isNullOrEmpty())
                tv_grade_edit_text.hint = sessionManager?.LanguageLabel?.lngGrade
            if (!sessionManager?.LanguageLabel?.lngIAmCurrentlyStudyHere.isNullOrEmpty())
                checkbox.text = sessionManager?.LanguageLabel?.lngIAmCurrentlyStudyHere
            if (!sessionManager?.LanguageLabel?.lngFrom.isNullOrEmpty())
                study_from.text = sessionManager?.LanguageLabel?.lngFrom
            if (!sessionManager?.LanguageLabel?.lngTo.isNullOrEmpty())
                study_to.text = sessionManager?.LanguageLabel?.lngTo
            if (!sessionManager?.LanguageLabel?.lngDescription.isNullOrEmpty())
                tv_txt_des_education.hint = sessionManager?.LanguageLabel?.lngDescription
            if (!sessionManager?.LanguageLabel?.lngMedia.isNullOrEmpty())
                study_media.text = sessionManager?.LanguageLabel?.lngMedia
            if (!sessionManager?.LanguageLabel?.lngAddOrLinkToExternaldoc.isNullOrEmpty())
                study_add_link.text = sessionManager?.LanguageLabel?.lngAddOrLinkToExternaldoc
            if (!sessionManager?.LanguageLabel?.lngUpload.isNullOrEmpty())
                btn_uploadImageEducation.text = sessionManager?.LanguageLabel?.lngUpload
            if (!sessionManager?.LanguageLabel?.lngLink.isNullOrEmpty())
                btn_setEduLink.text = sessionManager?.LanguageLabel?.lngLink
            if (!sessionManager?.LanguageLabel?.lngSave.isNullOrEmpty())
                btn_save_education_detail.progressText = sessionManager?.LanguageLabel?.lngSave
        }

        setOnClickListener()

        if (userData != null) {
            setEducationData()
        }

        checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isChecked) {
                ll_to_date.visibility = View.GONE
            } else {
                ll_to_date.visibility = View.VISIBLE
            }
            nextButtonEnable()
        }

        textWatcher()

    }

    fun setOnClickListener() {
        university_edit_text.setOnClickListener(this)
        degree_edit_text.setOnClickListener(this)
        btn_save_education_detail.setOnClickListener(this)
        btn_uploadImageEducation.setOnClickListener(this)
        btn_setEduLink.setOnClickListener(this)
        edit_Link_imageName.setOnClickListener(this)
        editMedia_imageName.setOnClickListener(this)
        close_Link_imageName.setOnClickListener(this)
        close_imagename.setOnClickListener(this)
        fromdate_edittext.setOnClickListener(this)
        todate_edittext.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.university_edit_text -> {
                getUniversityListApi()
            }
            R.id.degree_edit_text -> {
                getDegreeListApi()
            }
            R.id.btn_save_education_detail -> {
                checkValidation()
            }
            R.id.btn_uploadImageEducation -> {
                Intent(mActivity!!, AddMediaActivity()::class.java).apply {
                    putExtra("type", "Education")
                    putExtra("mediaType", "Image")
                    putExtra("from", "Add")
                    startActivityForResult(this, 1008)
                }
            }
            R.id.btn_setEduLink -> {
                Intent(mActivity!!, AddMediaActivity()::class.java).apply {
                    putExtra("type", "Education")
                    putExtra("from", "Add")
                    putExtra("mediaType", "Link")
                    startActivityForResult(this, 1007)
                }
            }
            R.id.edit_Link_imageName -> {
                if (!mediaEduLink.isNullOrEmpty() && mediaTypeEducation.equals("Link")) {
                    Intent(mActivity!!, AddMediaActivity()::class.java).apply {
                        putExtra("from", "edit")
                        putExtra("type", "Education")
                        putExtra("mediaType", "Link")
                        putExtra("editlinkMedia", mediaEduLink)
                        startActivityForResult(this, 1007)
                    }

                }
            }
            R.id.editMedia_imageName -> {
                if (!mediaEduFileName.isNullOrEmpty() && mediaTypeEducation.equals("Image")) {
                    Intent(mActivity!!, AddMediaActivity()::class.java).apply {
                        putExtra("from", "edit")
                        putExtra("type", "Education")
                        putExtra("mediaType", "Image")
                        putExtra("editMediaImage", mediaEduFileName)
                        putExtra("editMediaTitle", mediaEduImageTitle)
                        putExtra("editMediaDes", mediaEduDescription)
                        startActivityForResult(this, 1008)
                    }
                }
            }
            R.id.close_Link_imageName -> {
                if (!mediaEduLink.isNullOrEmpty()) {
                    for (i in 0 until mediaList!!.size) {
                        if (mediaList!![i].equals("Link")) {
                            mediaList?.removeAt(i)
                            Log.e("mediali", mediaList.toString())
                            break
                        }
                    }
                    ll_mediaLinkEdu.visibility = View.GONE
                }
            }
            R.id.close_imagename -> {
                if (!mediaEduFileName.isNullOrEmpty()) {
                    for (i in 0 until mediaList!!.size) {
                        if (mediaList!![i].equals("Image")) {
                            mediaList?.removeAt(i)
                            Log.e("mediali", mediaList.toString())
                            break
                        }
                    }
                    ll_mediaImageEdu.visibility = View.GONE
                }
            }
            R.id.fromdate_edittext -> {
                setMonthPickerDialog("from")
            }
            R.id.todate_edittext -> {
                if (fromdate_edittext.text.isNullOrEmpty()) {
                    MyUtils.showSnackbar(
                        mActivity!!,
                        "Please select from date",
                        ll_main_saveEducation
                    )
                } else {
                    setMonthPickerDialog("to")
                }

            }
        }
    }

    fun setEducationData() {
        if (from.equals("edit")) {
            if (userEduData != null) {
                university_edit_text.setText(userEduData!!.universityName)
                degree_edit_text.setText(userEduData!!.degreeName)
                grade_edit_text.setText(userEduData!!.usereducationGrade)

                try {

                    var fromDate = MyUtils.formatDate(
                        userEduData!!.usereducationPeriodOfTimeFrom,
                        "dd-MM-yyyy hh:mm:ss",
                        "MM/yyyy"
                    )
                    fromdate_edittext.setText(fromDate)
                    if (!userEduData!!.usereducationPeriodOfTimeTo.isNullOrEmpty()) {
                        var toDate = MyUtils.formatDate(
                            userEduData!!.usereducationPeriodOfTimeTo,
                            "dd-MM-yyyy hh:mm:ss",
                            "MM/yyyy"
                        )
                        ll_to_date.visibility = View.VISIBLE
                        todate_edittext.setText(toDate)
                        checkbox.isChecked = false
                    } else {
                        ll_to_date.visibility = View.GONE
                        checkbox.isChecked = true
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                txt_des_education.setText(userEduData!!.usereducationDescription)

                if (!userEduData!!.media.isNullOrEmpty()) {

                    for (i in 0 until userEduData!!.media.size) {

                        if (!userEduData!!.media[i].useredumediaType.isNullOrEmpty()) {
                            mediaList!!.add(userEduData!!.media[i].useredumediaType)
                        }

                        if (!userEduData!!.media[i].useredumediaFile.isNullOrEmpty()) {
                            if (userEduData!!.media[i].useredumediaType.equals("Image")) {

                                ll_mediaImageEdu.visibility = View.VISIBLE

                                mediaTypeEducation = userEduData!!.media[i].useredumediaType
                                mediaEduFileName = userEduData!!.media[i].useredumediaFile
                                mediaEduImageTitle = userEduData!!.media[i].useredumediaTitle
                                img_add_mediaEducation.setImageURI(RestClient.image_base_url_mediaEdu + userEduData!!.media[i].useredumediaFile)
                                mediaEduDescription = userEduData!!.media[i].useredumediaDescription
                                tv_add_media.text = userEduData!!.media[i].useredumediaTitle

                            }
                            if (userEduData!!.media[i].useredumediaType.equals("Link")) {
                                mediaTypeEducation = userEduData!!.media[i].useredumediaType
                                mediaEduLink = userEduData!!.media[i].useredumediaFile
                                media_edu_linkName.text = userEduData!!.media[i].useredumediaFile
                                ll_mediaLinkEdu.visibility = View.VISIBLE

                            }
                        } else {
                            ll_mediaLinkEdu.visibility = View.GONE
                            ll_mediaImageEdu.visibility = View.GONE
                        }
                    }
                }

                btn_save_education_detail.strokeColor = (resources.getColor(R.color.colorPrimary))
                btn_save_education_detail.backgroundTint =
                    (resources.getColor(R.color.colorPrimary))
                btn_save_education_detail.textColor = resources.getColor(R.color.black)
            }
        }

    }

    fun setMonthPickerDialog(from: String) {
        val pickerDialog = MonthYearPickerDialog()
        val c = Calendar.getInstance()

        pickerDialog.setListener(DatePickerDialog.OnDateSetListener { datePicker, year, month, i2 ->

            val months = (if (month < 10) "0$month" else month)
            val selected = "$year,$months"
            val selectedMonth = "$months"
            val selectedYear = "$year"
            var monthOfYear = month
            c.set(Calendar.MONTH, monthOfYear)
            c.set(Calendar.YEAR, year)
            c.set(Calendar.DAY_OF_MONTH, month)
            val currentDate = MyUtils.calenderToString(Calendar.getInstance())

            var fromdate = MyUtils.formatDate(selected, "yyyy,MM", "MM/yyyy")
            var toDate = MyUtils.formatDate(selected, "yyyy,MM", "MM/yyyy")

            try {
                if (from.equals("from", false)) {
                    if ((selected.compareTo(currentDate) > 0)) {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            "Please select valid from date",
                            ll_main_saveEducation
                        )
                    } else {
                        fromdate_edittext.setText(fromdate)
                    }
                } else if (from.equals("to")) {
                    var date =
                        MyUtils.formatDate(fromdate_edittext.text.toString(), "MM/yyyy", "yyyy,MM")
                    var fromYear = MyUtils.formatDate(date, "yyyy,MM", "yyyy")
                    var fromMonth = MyUtils.formatDate(date, "yyyy,MM", "MM")

                    if ((selected.compareTo(currentDate) > 0)) {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            "Please select valid to date",
                            ll_main_saveEducation
                        )
                    } else if ((selectedMonth.compareTo(fromMonth) <= 0) && (selectedYear.compareTo(
                            fromYear
                        ) <= 0)
                    ) {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            "Please select valid to date",
                            ll_main_saveEducation
                        )
                    } else if ((selectedMonth.compareTo(fromMonth) >= 0) && (selectedYear.compareTo(
                            fromYear
                        ) >= 0)
                    ) {
                        todate_edittext.setText(toDate)
                    } else {
                        todate_edittext.setText(toDate)

                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
            nextButtonEnable()
        })
        pickerDialog.show(childFragmentManager, "MonthYearPickerDialog")

    }


    private fun getDegreeListApi() {
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

        var degreeListModel =
            ViewModelProviders.of(this@AddEducationFragment).get(DegreeListModel::class.java)
        degreeListModel.getDegreeList(mActivity!!, false, jsonArray.toString())
            .observe(this@AddEducationFragment!!,
                androidx.lifecycle.Observer { degreeListPojo ->
                    if (degreeListPojo != null && degreeListPojo.isNotEmpty()) {

                        if (degreeListPojo[0].status.equals("true", false)) {
                            MyUtils.dismissProgressDialog()

                            degreeListData!!.clear()
                            degreeListData!!.addAll(degreeListPojo[0].data)
                            openDegreeListBottomSheet(degreeListData!!)
                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                mActivity!!,
                                degreeListPojo[0].message,
                                ll_main_saveEducation
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        Toast.makeText(
                            mActivity!!,
                            R.string.error_common_network,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

    }

    private fun getUniversityListApi() {
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

        var universityListModel =
            ViewModelProviders.of(this@AddEducationFragment).get(UniversityListModel::class.java)
        universityListModel.getUniversityList(mActivity!!, false, jsonArray.toString())
            .observe(this@AddEducationFragment!!,
                androidx.lifecycle.Observer { universityListPojo ->
                    if (universityListPojo != null && universityListPojo.isNotEmpty()) {

                        if (universityListPojo[0].status.equals("true", false)) {
                            MyUtils.dismissProgressDialog()

                            universityListData!!.clear()
                            universityListData!!.addAll(universityListPojo[0].data)
                            openUnsityListBottomSheet(universityListData!!)
                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                mActivity!!,
                                universityListPojo[0].message,
                                ll_main_saveEducation
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        Toast.makeText(
                            mActivity!!,
                            R.string.error_common_network,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

    }

    private fun openDegreeListBottomSheet(data: ArrayList<DegreeListData>) {

        var bottomSheetData = ArrayList<String>()
        bottomSheetData.clear()
        for (i in 0 until data.size) {
            bottomSheetData.add(data[i]!!.degreeName!!)
        }

        val bottomSheet = BottomSheetListFragment()
        val bundle = Bundle()
        bundle.putString("from", "DegreeName")
        bundle.putSerializable("data", bottomSheetData)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")
    }

    private fun openUnsityListBottomSheet(data: ArrayList<UniversityListData>) {

        var bottomSheetData = ArrayList<String>()
        bottomSheetData.clear()
        for (i in 0 until data.size) {
            bottomSheetData.add(data[i]!!.universityName!!)
        }

        val bottomSheet = BottomSheetListFragment()
        val bundle = Bundle()
        bundle.putString("from", "UniversityName")
        bundle.putSerializable("data", bottomSheetData)
        bottomSheet.arguments = bundle
        bottomSheet.show(childFragmentManager!!, "List")
    }

    fun textWatcher() {
        university_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })
        degree_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })
        grade_edit_text.addTextChangedListener(object : TextWatcher {
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
        if (validateAddEducation()) {
            btn_save_education_detail.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_save_education_detail.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_save_education_detail.textColor = resources.getColor(R.color.black)
        } else {
            btn_save_education_detail.strokeColor = (resources.getColor(R.color.grayborder))
            btn_save_education_detail.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_save_education_detail.textColor = resources.getColor(R.color.colorPrimary)
        }
    }

    fun validateAddEducation(): Boolean {
        var valid: Boolean = false
        when {
            university_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            degree_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            grade_edit_text.text.toString().trim().isEmpty() -> {
                return valid
            }
            fromdate_edittext.text.toString().trim().isEmpty() -> {
                return valid
            }
            (todate_edittext.text.toString().trim().isEmpty() && (!checkbox.isChecked)) -> {
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
        if (TextUtils.isEmpty(university_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter School/College", ll_main_saveEducation)
            university_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(degree_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter degree", ll_main_saveEducation)
            degree_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(grade_edit_text.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter grade", ll_main_saveEducation)
            grade_edit_text.requestFocus()
        } else if (TextUtils.isEmpty(fromdate_edittext.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Select from date", ll_main_saveEducation)
            fromdate_edittext.requestFocus()
        } else if (!checkbox.isChecked && TextUtils.isEmpty(
                todate_edittext.text.toString().trim()
            )
        ) {
            MyUtils.showSnackbar(mActivity!!, "Please select To date", ll_main_saveEducation)
            todate_edittext.requestFocus()
        } /*else if (TextUtils.isEmpty(txt_des_education.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter Description", ll_main_saveEducation)
            txt_des_education.requestFocus()
        } */ else {
            if (userEduData != null) {
                getAddEducation("Edit", userEduData!!.usereducationID)
            } else {
                getAddEducation("Add", "")
            }

        }

    }


    fun getAddEducation(from: String, usereducationID: String) {
        btn_save_education_detail.startAnimation()
        val jsonObject = JSONObject()
        val jsonArrayEducation = JSONArray()
        val jsonArray = JSONArray()
        try {
            jsonObject.put("languageID", "1")
            jsonObject.put("degreeName", degree_edit_text.text.toString().trim())
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("universityName", university_edit_text.text.toString())
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("universityID", universityID)
            jsonObject.put("degreeID", degreeID)
            jsonObject.put("usereducationGrade", grade_edit_text.text.toString())
            jsonObject.put(
                "usereducationPeriodOfTimeFrom",
                MyUtils.formatDate(
                    fromdate_edittext.text.toString(),
                    "MM/yyyy",
                    "dd-MM-yyyy hh:mm:ss"
                )
            )
            if (checkbox.isChecked) {
                jsonObject.put("usereducationPeriodOfTimeTo", "")
            } else {
                jsonObject.put(
                    "usereducationPeriodOfTimeTo",
                    MyUtils.formatDate(
                        todate_edittext.text.toString(),
                        "MM/yyyy",
                        "dd-MM-yyyy hh:mm:ss"
                    )
                )
            }
            jsonObject.put("usereducationDescription", txt_des_education.text.toString())
            jsonObject.put("usereducationPrivarcyType", "Public")
            jsonObject.put("usereducationPrivarcyData", "")
            if (userEduData != null) {
                jsonObject.put("usereducationID", usereducationID)
            }

            if (!mediaList.isNullOrEmpty()) {
                for (i in 0 until mediaList!!.size) {
                    val educationmediaPojo = JSONObject()
                    if (mediaList!![i].equals("Image")) {
                        educationmediaPojo.put("useredumediaType", "Image")
                        educationmediaPojo.put("useredumediaFile", mediaEduFileName)
                        educationmediaPojo.put(
                            "useredumediaTitle",
                            tv_add_media.text.toString().trim()
                        )
                        educationmediaPojo.put("useredumediaDescription", mediaEduDescription)
                        educationmediaPojo.put("useredumediaFileSize", serverfileSizeeducation)
                    }
                    if (mediaList!![i].equals("Link")) {
                        educationmediaPojo.put("useredumediaType", "Link")
                        educationmediaPojo.put(
                            "useredumediaFile",
                            media_edu_linkName.text.toString().trim()
                        )
                        educationmediaPojo.put("useredumediaTitle", "")
                        educationmediaPojo.put("useredumediaDescription", "")
                        educationmediaPojo.put("useredumediaFileSize", serverfileSizeeducation)

                    }
                    jsonArrayEducation.put(educationmediaPojo)

                }
            }
            jsonObject.put("media", jsonArrayEducation)


        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)


        addEducationModel.educationApi(jsonArray.toString(), from)
        addEducationModel.successEducation
            .observe(
                viewLifecycleOwner
            ) { loginPojo ->
                if (loginPojo != null) {
                    btn_save_education_detail.endAnimation()
                    if (loginPojo.get(0).status.equals("true", false)) {
                        try {

                            if (from.equals("Add")) {
                                userData?.education?.addAll(loginPojo.get(0).data)
                                StoreSessionManager(userData)
                            } else if (from.equals("Edit")) {
                                for (i in 0 until userData?.education!!.size) {
                                    if (usereducationID.equals(userData!!.education!![i]!!.usereducationID))
                                        userData!!.education!![i] = loginPojo!![0].data!![0]
                                    break
                                }
                                StoreSessionManager(userData)
                                if (educationUpdateListener != null)
                                    educationUpdateListener!!.onEducationUpdate()

                            }
                            Handler().postDelayed({
                                (activity as MainActivity).onBackPressed()
                            }, 2000)

                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message,
                                ll_main_saveEducation
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        MyUtils.showSnackbar(
                            mActivity!!,
                            loginPojo.get(0).message,
                            ll_main_saveEducation
                        )
                    }

                } else {
                    btn_save_education_detail.endAnimation()
                    ErrorUtil.errorMethod(ll_main_saveEducation)
                }
            }
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

    override fun onLanguageSelect(value: String, from: String) {

        when (from) {
            "DegreeName" -> {
                degree_edit_text.setText(value)
                for (i in 0 until degreeListData!!.size) {
                    if (value.equals(degreeListData!![i]!!.degreeName, false)) {
                        if (!degreeID.equals(degreeListData!![i]!!.degreeID))
                            degreeID = degreeListData!![i]!!.degreeID
                    }
                }
            }
            "UniversityName" -> {
                university_edit_text.setText(value)
                for (i in 0 until universityListData!!.size) {
                    if (value.equals(universityListData!![i]!!.universityName, false)) {
                        if (!universityID.equals(universityListData!![i]!!.universityID))
                            universityID = universityListData!![i]!!.universityID
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            1008 -> {
                if (data != null) {
                    mediaEduImageTitle = data.getStringExtra("title")!!
                    mediaEduDescription = data.getStringExtra("description")!!
                    mediaEduFileName = data.getStringExtra("imageMedia")!!
                    mediaTypeEducation = data.getStringExtra("media_type")!!
                    fromMedia = data.getStringExtra("fromMedia")!!
                    serverfileSizeeducation = data.getStringExtra("serverfileSizeeducation")!!

                    if (fromMedia.equals("AddMedia")) {

                        if (!mediaEduFileName.isNullOrEmpty() || !mediaEduImageTitle.isNullOrEmpty()) {
                            if (!mediaEduFileName.equals("null") || !mediaEduImageTitle.equals("null")) {
                                ll_mediaImageEdu.visibility = View.VISIBLE
                                img_add_mediaEducation.setImageURI(RestClient.image_base_url_mediaEdu + mediaEduFileName)
                                tv_add_media.text = mediaEduImageTitle

                                if (!mediaEduFileName.isNullOrEmpty()) {
                                    mediaList?.add(mediaTypeEducation)
                                    Log.e("listmedia", mediaList.toString())
                                }
                            }
                        } else {
                            ll_mediaImageEdu.visibility = View.GONE
                        }
                    }

                    if (fromMedia.equals("EditMedia")) {

                        if (!mediaEduFileName.isNullOrEmpty() || !mediaEduImageTitle.isNullOrEmpty()) {
                            if (!mediaEduFileName.equals("null") || !mediaEduImageTitle.equals("null")) {
                                ll_mediaImageEdu.visibility = View.VISIBLE
                                img_add_mediaEducation.setImageURI(RestClient.image_base_url_mediaEdu + mediaEduFileName)
                                tv_add_media.text = mediaEduImageTitle

                                for (i in 0 until mediaList!!.size) {
                                    if (mediaList!![i].equals("Image")) {
                                        mediaList?.removeAt(i)
                                        break
                                    }
                                }
                                if (!mediaEduFileName.isNullOrEmpty()) {
                                    mediaList?.add(mediaTypeEducation)
                                    Log.e("listmedia", mediaList.toString())
                                }
                            }
                        } else {
                            ll_mediaImageEdu.visibility = View.GONE
                        }
                    }
                }
            }
            1007 -> {
                if (data != null) {
                    mediaEduLink = data.getStringExtra("link_media")!!
                    mediaTypeEducation = data.getStringExtra("media_type")!!
                    fromMedia = data.getStringExtra("fromMedia")!!

                    if (fromMedia.equals("AddLink")) {
                        if (!mediaEduLink.isNullOrEmpty()) {
                            if (!mediaEduLink.equals("null")) {
                                ll_mediaLinkEdu.visibility = View.VISIBLE
                                media_edu_linkName.text = mediaEduLink

                                if (!mediaEduLink.isNullOrEmpty()) {
                                    mediaList?.add(mediaTypeEducation)
                                    Log.e("listmedia", mediaList.toString())
                                }
                            }
                        } else {
                            ll_mediaLinkEdu.visibility = View.GONE

                        }
                    }
                    if (fromMedia.equals("EditLink")) {
                        if (!mediaEduLink.isNullOrEmpty()) {
                            if (!mediaEduLink.equals("null")) {
                                ll_mediaLinkEdu.visibility = View.VISIBLE
                                media_edu_linkName.text = mediaEduLink

                                for (i in 0 until mediaList!!.size) {
                                    if (mediaList!![i].equals("Link")) {
                                        mediaList?.removeAt(i)
                                        break
                                    }
                                }

                                if (!mediaEduLink.isNullOrEmpty()) {
                                    mediaList?.add(mediaTypeEducation)
                                    Log.e("listmedia", mediaList.toString())
                                }
                            }
                        } else {
                            ll_mediaLinkEdu.visibility = View.GONE

                        }
                    }
                }
            }
        }

    }


    interface EducationUpdateListener {
        fun onEducationUpdate()
    }


}
