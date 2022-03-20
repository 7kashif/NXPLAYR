package com.nxplayr.fsl.ui.fragments.userprofile.view

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.adapter.*
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.viewmodel.*
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.data.model.AgecategoryList
import com.nxplayr.fsl.data.model.FootballLevelData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.UserLanguageList
import com.nxplayr.fsl.ui.fragments.userprofile.adapter.FootballAgeSelectAdapter
import com.nxplayr.fsl.ui.fragments.userfootballleague.adapter.FootballLanguageListAdapter
import com.nxplayr.fsl.ui.fragments.userprofile.adapter.FootballLevelListAdapter
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.FootballLevelListModel
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import com.nxplayr.fsl.ui.fragments.userprofile.viewmodel.FootballAgeListModel
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_user_profile_details.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_basic_detail.*
import kotlinx.android.synthetic.main.fragment_best_foot_print.*
import kotlinx.android.synthetic.main.fragment_explore_main.*
import kotlinx.android.synthetic.main.fragment_football_age_catogary.*
import kotlinx.android.synthetic.main.fragment_football_language.*
import kotlinx.android.synthetic.main.fragment_football_level.*
import kotlinx.android.synthetic.main.fragment_height_weight.*
import kotlinx.android.synthetic.main.fragment_height_weight.ll_main_login
import kotlinx.android.synthetic.main.fragment_name_bio.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.sub_album_activity.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.tvToolbarTitle
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class UpdateUserProfileFragment : Fragment() {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var type = ""
    var fromDetails = ""
    var userId = ""
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var dob: String = ""
    var age1: Int = 0
    var date: String = ""
    var dateAge: String = ""
    var selectGender: String = ""
    var footballAgeSelectAdapter: FootballAgeSelectAdapter? = null
    var footballAgeList: ArrayList<AgecategoryList>? = ArrayList()
    private lateinit var gridLayoutManager: GridLayoutManager
    var football_level_list: ArrayList<FootballLevelData>? = ArrayList()
    var footballLevelListAdapter: FootballLevelListAdapter? = null

    var footballLanguageListAdapter: FootballLanguageListAdapter? = null
    var football_language_list: ArrayList<UserLanguageList?>? = ArrayList()

    var linearLayoutManager: LinearLayoutManager? = null
    var footballLevelId = ""
    var footballLanguageId = ""
    var footballLevelName = ""
    var footbllevelName = ""
    var footballagecatId = ""
    var footballagecatName = ""
    var isSelect: Boolean = false
    var selectfoot = ""
    val c = Calendar.getInstance()
    var otherUserData: SignupData? = null
    var fromProfile=""
    private lateinit var  footballLevelListModel: FootballLevelListModel
    private lateinit var  loginModel: SignupModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_update_user_profile, container, false)
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
        if (arguments != null) {
            type = arguments!!.getString("type")!!
            userId=arguments!!.getString("userId","")
            if(!userId.equals(userData?.userID,false))
            {
                otherUserData = arguments!!.getSerializable("otherUserData") as SignupData?
            }
        }

        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(R.string.basic_details)

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }


        if(userId.equals(userData?.userID,false))
        {
            if (userData != null)
            {
                setUserData(userData!!)
            }
        }
        else
        {
            if (otherUserData != null)
            {
                setUserData(otherUserData!!)
            }
        }


        if (type.equals("BasicDetails")) {
            icon_edit!!.visibility = View.VISIBLE
            tvToolbarTitle.text = getString(R.string.basic_details)
            layout_basicDetails.visibility = View.VISIBLE
            basicDetails()

        } else if (type.equals("footballAgeCat")) {
            tvToolbarTitle.text = getString(R.string.football_age_category)
            layout_footballAgeCategory.visibility = View.VISIBLE
            ageCategory()

        } else if (type.equals("heightWeight")) {
            tvToolbarTitle.text = getString(R.string.height_weight)
            layout_heigntWeight.visibility = View.VISIBLE
            setHeightWeight()

        } else if (type.equals("footballLevel")) {
            tvToolbarTitle.text = getString(R.string.football_level)
            layout_footballLevel.visibility = View.VISIBLE

            footballLevel()

        } else if (type.equals("FootballbestFeet")) {
            tvToolbarTitle.text = getString(R.string.best_foot_feet)
            layout_bestFootPrint.visibility = View.VISIBLE
            bestFootFeet()

        } else if (type.equals("NameAndBio")) {
            tvToolbarTitle.text = getString(R.string.name_bio)
            layout_nameandBio.visibility = View.VISIBLE
            NameAndBio()
        }
    }

    private fun setupViewModel() {
        footballLevelListModel = ViewModelProvider(this@UpdateUserProfileFragment).get(
            FootballLevelListModel::class.java)
        loginModel = ViewModelProvider(this@UpdateUserProfileFragment).get(SignupModel::class.java)

    }

    @JvmName("setUserData1")
     fun setUserData(userData: SignupData) {
        if (userData != null) {

            edit_first_name.isEnabled = false
            edit_last_name.isEnabled = false
//            edit_family_name.isEnabled = false
            edit_dateofbirth.isEnabled = false

            img_select_male.isEnabled = false
            img_select_female.isEnabled = false

            edit_first_name.setText(userData!!.userFirstName)
            edit_last_name.setText(userData!!.userLastName)
            edit_userFirstname.setText(userData!!.userFirstName)
            edit_userLastname.setText(userData!!.userLastName)

            if (!userData!!.userDOB.isNullOrEmpty()&&!userData!!.userDOB.equals("0000-00-00"))
            {
                var dateOfBirth = ((convertStringToDate(userData!!.userDOB)))
                var userage = (calculateAge(dateOfBirth))

                try {
                    edit_dateofbirth.setText(MyUtils.formatDate(userData!!.userDOB, "yyyy-MM-dd", "dd MMM yyyy") + ", " + userage + " " + "Years")
                } catch (e: Exception) {

                }
            }
//            }
            selectGender = userData!!.userGender
            if (!userData!!.userHeight.isNullOrEmpty() && !userData!!.userWeight.isNullOrEmpty() && !userData?.userHeight.equals("0")&& !userData?.userWeight.equals("0"))
            {
                edittext_height.setText(userData!!.userHeight)
                edittext_weight.setText(userData!!.userWeight)
            }
            if(!userData!!.footballagecatID.isNullOrEmpty())
                footballagecatId = userData!!.footballagecatID

            if(!userData!!.footballagecatName.isNullOrEmpty())
                   footballagecatName = userData!!.footballagecatName

              if(!userData!!.userBio.isNullOrEmpty())
             {
                 edit_write_about_yourself.setText(userData!!.userBio)

             }

            if(!userData!!.userBestFoot.isNullOrEmpty())
              selectfoot = userData!!.userBestFoot

            if(!userData!!.footbllevelID.isNullOrEmpty())
            footballLevelId = userData!!.footbllevelID

            if(!userData!!.footbllevelName.isNullOrEmpty())
            footballLevelName = userData!!.footbllevelName

        }

    }

    fun basicDetails() {

//        btnUpdateBasicDetail.visibility = View.VISIBLE
        selectGender=userData?.userGender!!
        edit_first_name.setText(userData?.userFirstName)
        edit_last_name.setText(userData?.userLastName)

        try {
          var  date = MyUtils.formatDate(userData?.userDOB!!, "yyyy-MM-dd", "dd MMM yyyy")

          var  dateAge = date + ", " + age1 + " Years"

            edit_dateofbirth.setText(dateAge)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        if(!userData?.userPlaceofBirth.isNullOrEmpty())
        {
            edit_place_of_birth.setText(userData?.userPlaceofBirth!!)

        }

        icon_edit.setOnClickListener {
            icon_edit.visibility = View.GONE
            btnUpdateBasicDetail.visibility = View.VISIBLE

            edit_first_name.isEnabled = true
            edit_last_name.isEnabled = true
//            edit_family_name.isEnabled = true
            edit_dateofbirth.isEnabled = true
            img_select_male.isEnabled = true
            img_select_female.isEnabled = true

        }

        btnUpdateBasicDetail.backgroundTint = (resources.getColor(R.color.colorPrimary))
        btnUpdateBasicDetail.textColor = resources.getColor(R.color.black)
        btnUpdateBasicDetail.strokeColor = resources.getColor(R.color.colorPrimary)

        btnUpdateBasicDetail.setOnClickListener {
            checkValidation()
        }


        edit_dateofbirth.setOnClickListener {
            DatePickerDialog()
        }


        rgGenderSelection.setOnCheckedChangeListener { group, checkedId ->


            when (checkedId) {
                R.id.img_select_male -> {
                    selectGender = img_select_male.text.toString().capitalize()
                    img_select_male.isChecked = true
                    img_select_male.setCompoundDrawablesWithIntrinsicBounds(R.drawable.male_icon_selected, 0, 0, 0)
                    img_select_female.setCompoundDrawablesWithIntrinsicBounds(R.drawable.female_icon_unselected, 0, 0, 0)
                }
                R.id.img_select_female -> {
                    selectGender = img_select_female.text.toString().capitalize()
                    img_select_female.isChecked = true
                    img_select_female.setCompoundDrawablesWithIntrinsicBounds(R.drawable.female_icon_selected, 0, 0, 0)
                    img_select_male.setCompoundDrawablesWithIntrinsicBounds(R.drawable.male_icon_unselected, 0, 0, 0)

                }

            }
        }
        if (selectGender.equals("male") || selectGender.equals("Male")) {
            img_select_male.isChecked = true
            img_select_male.setCompoundDrawablesWithIntrinsicBounds(R.drawable.male_icon_selected, 0, 0, 0)

        } else if (selectGender.equals("female") || selectGender.equals("Female")) {
            img_select_female.isChecked = true
            img_select_female.setCompoundDrawablesWithIntrinsicBounds(R.drawable.female_icon_selected, 0, 0, 0)

        }

    }

    private fun DatePickerDialog() {


        val c = Calendar.getInstance()
        val today = c.time
        //
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)


        val mincalendar = Calendar.getInstance()
        mincalendar.set(mYear, mMonth, mDay)

        val dpd = android.app.DatePickerDialog(
                mActivity!!,
                android.app.DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var monthOfYear = monthOfYear
                    Log.d("year", year.toString() + "")
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
//                    dateSpecified = c.time
                    monthOfYear = monthOfYear + 1
                    val minAdultAge = GregorianCalendar()
                    minAdultAge.add(Calendar.YEAR, 0)



                    age1 = (calculateAge(c.time))

                    dob = year.toString() + "-" + (if (monthOfYear < 10) "0$monthOfYear" else monthOfYear) + "-" + if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth
                    try {
                        date = MyUtils.formatDate(dob, "yyyy-MM-dd", "dd MMM yyyy")

                        dateAge = date + ", " + age1 + " Years"

                        edit_dateofbirth.setText(dateAge)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }, mYear, mMonth, mDay
        )

        dpd.datePicker.maxDate = mincalendar.timeInMillis
        dpd.show()
    }

    fun calculateAge(date: Date): Int {
        val dob = Calendar.getInstance()
        dob.time = date
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    fun convertStringToDate(date: String): Date {
        val dtStart = date
        val format = SimpleDateFormat("yyyy-MM-dd")
        try {
            val date = format.parse(dtStart)
            System.out.println(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return format.parse(date)
    }

    private fun checkValidation() {
        MyUtils.hideKeyboard1(mActivity!!)
        if (TextUtils.isEmpty(edit_first_name.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter First Name", ll_mainUpdateprofile)
            edit_first_name.requestFocus()
        } else if(edit_first_name.length() < 3){
            MyUtils.showSnackbar(mActivity!!, "Please Enter minimum 3 charecters", ll_mainUpdateprofile)
            edit_first_name.requestFocus()
        } else if(edit_first_name.length() > 100){
            MyUtils.showSnackbar(mActivity!!, "Please Enter maximum 100 charecters", ll_mainUpdateprofile)
            edit_first_name.requestFocus()
        } else if (TextUtils.isEmpty(edit_last_name.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter Last Name", ll_mainUpdateprofile)
            edit_last_name.requestFocus()
        } else if(edit_last_name.length() < 3){
            MyUtils.showSnackbar(mActivity!!, "Please Enter minimum 3 charecters", ll_mainUpdateprofile)
            edit_first_name.requestFocus()
        } else if(edit_last_name.length() > 100){
            MyUtils.showSnackbar(mActivity!!, "Please Enter maximum 100 charecters", ll_mainUpdateprofile)
            edit_first_name.requestFocus()
        } /*else if (TextUtils.isEmpty(edit_family_name.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Enter Family Name", ll_mainUpdateprofile)
            edit_family_name.requestFocus()
        }*/ else if (TextUtils.isEmpty(edit_dateofbirth.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please Select Date of birth", ll_mainUpdateprofile)
            edit_dateofbirth.requestFocus()
        } else if (TextUtils.isEmpty(edit_place_of_birth.text.toString().trim())) {
            MyUtils.showSnackbar(mActivity!!, "Please enter place of birth", ll_mainUpdateprofile)
            edit_place_of_birth.requestFocus()
        } else if (TextUtils.isEmpty(selectGender)) {
            MyUtils.showSnackbar(mActivity!!, "Please Select Gender", ll_mainUpdateprofile)
        } else {
            updateUserProfile()
        }
    }

    private fun ageCategory() {
        if(userId.equals(userData?.userID,false))
        {
            btn_save_age.visibility=View.VISIBLE
        }
        else
        {
            btn_save_age.visibility=View.GONE

        }
        gridLayoutManager = GridLayoutManager(mActivity, 2)
        footballAgeSelectAdapter = FootballAgeSelectAdapter(mActivity!!, footballAgeList, object : FootballAgeSelectAdapter.OnItemClick {
                    override fun onClicled(position: Int, from: String) {
                       if(userId.equals(userData?.userID)){
                           for (i in 0 until footballAgeList!!.size) {
                               footballAgeList!![i].isSelect = i == position
                           }
                           footballagecatId = footballAgeList!![position].footballagecatID
                           footballagecatName = footballAgeList!![position].footballagecatName

                           btn_save_age.backgroundTint = (resources.getColor(R.color.colorPrimary))
                           btn_save_age.textColor = resources.getColor(R.color.black)
                           btn_save_age.strokeColor = (resources.getColor(R.color.colorPrimary))

                           footballAgeSelectAdapter?.notifyDataSetChanged()
                       }


                    }

                }, footballagecatId,userId,userData?.userID!!)


        recyclerview.layoutManager = gridLayoutManager
        recyclerview.adapter = footballAgeSelectAdapter
        footballAgeCatList()


        btnRetry.setOnClickListener {
            footballAgeCatList()

            if (!userData!!.footballagecatID.isNullOrEmpty() && (!userData!!.footballagecatID.equals("0"))) {
                btn_save_age.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_save_age.textColor = resources.getColor(R.color.black)
                btn_save_age.strokeColor = (resources.getColor(R.color.colorPrimary))
            } else {
                btn_save_age.backgroundTint = (resources.getColor(R.color.transperent1))
                btn_save_age.textColor = resources.getColor(R.color.colorPrimary)
                btn_save_age.strokeColor = (resources.getColor(R.color.grayborder))
            }
        }

        if (!footballagecatId.isNullOrEmpty()) {
            btn_save_age.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_save_age.textColor = resources.getColor(R.color.black)
            btn_save_age.strokeColor = (resources.getColor(R.color.colorPrimary))
        } else {
            btn_save_age.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_save_age.textColor = resources.getColor(R.color.colorPrimary)
            btn_save_age.strokeColor = (resources.getColor(R.color.grayborder))
        }


        btn_save_age.setOnClickListener {
            if (!footballagecatId.isNullOrEmpty()) {
                updateUserProfile()
            } else {
                MyUtils.showSnackbar(mActivity!!, getString(R.string.please_select_ageCategory), ll_mainUpdateprofile)
            }
        }
    }

    private fun footballAgeCatList() {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE


        val jsonObject = JSONObject()
        var jsonArray = JSONArray()

        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("searchWord", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)

        var footballAgeListModel =
                ViewModelProviders.of(this@UpdateUserProfileFragment).get(FootballAgeListModel::class.java)
        footballAgeListModel.getFootballAgeList(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                        androidx.lifecycle.Observer { footballAgeListPojo ->

                            relativeprogressBar.visibility = View.GONE
                            recyclerview.visibility = View.VISIBLE

                            if (footballAgeListPojo != null) {

                                if (footballAgeListPojo.get(0).status.equals("true", false)) {
                                    footballAgeList?.clear()
                                    footballAgeList?.addAll(footballAgeListPojo.get(0).data)
                                    footballAgeSelectAdapter?.notifyDataSetChanged()
                                } else {

                                    if (footballAgeList!!.size == 0) {
                                        ll_no_data_found.visibility = View.VISIBLE
                                        recyclerview.visibility = View.GONE

                                    } else {
                                        ll_no_data_found.visibility = View.GONE
                                        recyclerview.visibility = View.VISIBLE

                                    }
                                }

                            } else {
                                btn_save_age.backgroundTint = (resources.getColor(R.color.transperent1))
                                btn_save_age.textColor = resources.getColor(R.color.colorPrimary)
                                btn_save_age.strokeColor = (resources.getColor(R.color.grayborder))
                                errorMethod()
                            }
                        })

    }

    fun setHeightWeight() {
        if (!userData?.userHeight.isNullOrEmpty() || !userData?.userWeight.isNullOrEmpty()){

            edittext_height.setText(userData?.userHeight)
            edittext_weight.setText(userData?.userWeight)
        } else{
            edittext_height.setText("")
            edittext_weight.setText("")
        }

        btn_save_heightWeight.setOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)

            if (TextUtils.isEmpty(edittext_height.text.toString().trim())) {
                MyUtils.showSnackbar(mActivity!!, "Please Enter User Height", ll_mainUpdateprofile)
                edittext_height.requestFocus()
            } else if (TextUtils.isEmpty(edittext_weight.text.toString().trim())) {
                MyUtils.showSnackbar(mActivity!!, "Please Enter User Weight", ll_mainUpdateprofile)
                edittext_weight.requestFocus()
            } else {
                updateUserProfile()
            }
        }

        edittext_height.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btn_save_heightWeight.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_save_heightWeight.textColor = resources.getColor(R.color.black)
                btn_save_heightWeight.strokeColor = resources.getColor(R.color.colorPrimary)
                if (p0!!.isNullOrEmpty()) {
                    btn_save_heightWeight.strokeColor = (resources.getColor(R.color.grayborder))
                    btn_save_heightWeight.backgroundTint = (resources.getColor(R.color.transperent1))
                    btn_save_heightWeight.textColor = resources.getColor(R.color.colorPrimary)
                }
            }
        })
        edittext_weight.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btn_save_heightWeight.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_save_heightWeight.textColor = resources.getColor(R.color.black)
                btn_save_heightWeight.strokeColor = resources.getColor(R.color.colorPrimary)
                if (p0!!.isEmpty()) {
                    btn_save_heightWeight.strokeColor = (resources.getColor(R.color.grayborder))
                    btn_save_heightWeight.backgroundTint = (resources.getColor(R.color.transperent1))
                    btn_save_heightWeight.textColor = resources.getColor(R.color.colorPrimary)
                }
            }
        })

        if ((!userData!!.userHeight.isNullOrEmpty() && !userData!!.userHeight.equals("0")) || (!userData!!.userWeight.isNullOrEmpty()&&!userData!!.userWeight.equals("0"))) {
            btn_save_heightWeight.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_save_heightWeight.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_save_heightWeight.textColor = resources.getColor(R.color.black)
        } else {
            btn_save_heightWeight.strokeColor = (resources.getColor(R.color.grayborder))
            btn_save_heightWeight.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_save_heightWeight.textColor = resources.getColor(R.color.colorPrimary)
        }
    }

    fun footballLevel() {
        if(!userId.equals(userData?.userID,false))
        {
            btn_save_footballLevel.visibility=View.GONE
        }
        else{
            btn_save_footballLevel.visibility=View.VISIBLE
        }
        linearLayoutManager = LinearLayoutManager(mActivity!!)
        footballLevelListAdapter =
                FootballLevelListAdapter(mActivity!!, football_level_list!!, object : FootballLevelListAdapter.OnItemClick {
                    override fun onClicled(position: Int, from: String) {

                        for (i in 0 until football_level_list!!.size) {

                            football_level_list!![i].isSelect = i == position
                        }
                        footballLevelId = football_level_list!![position].footbllevelID
                        footballLevelName = football_level_list!![position].footbllevelName

                        btn_save_footballLevel.backgroundTint = (resources.getColor(R.color.colorPrimary))
                        btn_save_footballLevel.textColor = resources.getColor(R.color.black)
                        btn_save_footballLevel.strokeColor = (resources.getColor(R.color.colorPrimary))

                        footballLevelListAdapter?.notifyDataSetChanged()

                    }

                }, footballLevelId,userId,userData?.userID)

        RV_footballLevel.layoutManager = LinearLayoutManager(mActivity!!)
        RV_footballLevel.adapter = footballLevelListAdapter
        RV_footballLevel.setHasFixedSize(true)
        footballLevelListApi()


        if (!(userData!!.footbllevelID.isNullOrEmpty()) && (!userData!!.footbllevelID.equals("0"))) {
            btn_save_footballLevel.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_save_footballLevel.textColor = resources.getColor(R.color.black)
            btn_save_footballLevel.strokeColor = (resources.getColor(R.color.colorPrimary))
        } else {
            btn_save_footballLevel.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_save_footballLevel.textColor = resources.getColor(R.color.colorPrimary)
            btn_save_footballLevel.strokeColor = (resources.getColor(R.color.grayborder))
        }

        btn_save_footballLevel.setOnClickListener {
            if (!footballLevelId.isNullOrEmpty()) {
                updateUserProfile()
            } else {
                MyUtils.showSnackbar(mActivity!!, getString(R.string.please_select_footballLevel), ll_mainUpdateprofile)
            }
        }

        btn_Retry.setOnClickListener {
            nointernet.visibility = View.VISIBLE
            footballLevelListApi()

            if (!(userData!!.footbllevelID.isNullOrEmpty()) && (!userData!!.footbllevelID.equals("0"))) {
                btn_save_footballLevel.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_save_footballLevel.textColor = resources.getColor(R.color.black)
                btn_save_footballLevel.strokeColor = (resources.getColor(R.color.colorPrimary))
            } else {
                btn_save_footballLevel.backgroundTint = (resources.getColor(R.color.transperent1))
                btn_save_footballLevel.textColor = resources.getColor(R.color.colorPrimary)
                btn_save_footballLevel.strokeColor = (resources.getColor(R.color.grayborder))
            }
        }

    }

    private fun footballLevelListApi() {
        progress.visibility = View.VISIBLE
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nodata.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE
        nointernet.visibility = View.GONE

        val jsonObject = JSONObject()
        var jsonArray = JSONArray()
        try {
            jsonObject.put("loginuserID", "0")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        footballLevelListModel.getFootballLevelList(mActivity!!, false, jsonArray.toString())
                .observe(mActivity!!,
                        androidx.lifecycle.Observer { footballLevelListPojo ->
                            progress.visibility = View.GONE
                            relativeprogressBar.visibility = View.GONE
                            RV_footballLevel.visibility = View.VISIBLE

                            if (footballLevelListPojo != null) {

                                if (footballLevelListPojo.get(0).status.equals("true", false)) {
                                    football_level_list?.clear()
                                    football_level_list?.addAll(footballLevelListPojo.get(0).data)
                                    footballLevelListAdapter?.notifyDataSetChanged()

                                } else {

                                    if (football_level_list!!.size == 0) {
                                        nodata.visibility = View.VISIBLE
                                        ll_no_data_found.visibility = View.VISIBLE
                                        RV_footballLevel.visibility = View.GONE

                                    } else {
                                        nodata.visibility = View.GONE
                                        ll_no_data_found.visibility = View.GONE
                                        RV_footballLevel.visibility = View.VISIBLE

                                    }
                                }

                            } else {
                                btn_save_footballLevel.backgroundTint = (resources.getColor(R.color.transperent1))
                                btn_save_footballLevel.textColor = resources.getColor(R.color.colorPrimary)
                                btn_save_footballLevel.strokeColor = (resources.getColor(R.color.grayborder))
                                errorMethod()
                            }
                        })

    }

    fun bestFootFeet() {
        if(!userId.equals(userData?.userID,false))
        {
            btn_save_footprint.visibility=View.GONE
        }
        else
        {
            btn_save_footprint.visibility=View.VISIBLE

        }
        tv_foot_print_left.setOnClickListener {
            if(userId.equals(userData?.userID,false)) {
                isSelect = true
                selectfoot = tv_leftFeet.text.toString()
                tv_foot_print_left.setBackgroundResource(R.drawable.feet_left_selected)
                tv_foot_print_right.setBackgroundResource(R.drawable.feet_right_unselected)
                tv_foot_print_rightleft.setBackgroundResource(R.drawable.feet_left_right_unselected)
                btn_save_footprint.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_save_footprint.textColor = resources.getColor(R.color.black)
                btn_save_footprint.strokeColor = resources.getColor(R.color.colorPrimary)
            }
        }

        tv_foot_print_right.setOnClickListener {
            if(userId.equals(userData?.userID,false)) {
                isSelect = true
                selectfoot = selectRightFeet.text.toString()
                tv_foot_print_right.setBackgroundResource(R.drawable.feet_right_selected)
                tv_foot_print_left.setBackgroundResource(R.drawable.feet_left_unselected)
                tv_foot_print_rightleft.setBackgroundResource(R.drawable.feet_left_right_unselected)
                btn_save_footprint.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_save_footprint.textColor = resources.getColor(R.color.black)
                btn_save_footprint.strokeColor = resources.getColor(R.color.colorPrimary)
            }
        }

        tv_foot_print_rightleft.setOnClickListener {
            if(userId.equals(userData?.userID,false)) {
                isSelect = true
                selectfoot = tv_rightLeft.text.toString()
                tv_foot_print_rightleft.setBackgroundResource(R.drawable.feet_left_right_selected)
                tv_foot_print_left.setBackgroundResource(R.drawable.feet_left_unselected)
                tv_foot_print_right.setBackgroundResource(R.drawable.feet_right_unselected)
                btn_save_footprint.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_save_footprint.textColor = resources.getColor(R.color.black)
                btn_save_footprint.strokeColor = resources.getColor(R.color.colorPrimary)
            }
        }

        if (selectfoot.equals("Left")) {
            tv_foot_print_left.setBackgroundResource(R.drawable.feet_left_selected)
        } else if (selectfoot.equals("Right")) {
            tv_foot_print_right.setBackgroundResource(R.drawable.feet_right_selected)
        } else if (selectfoot.equals("Right & Left")) {
            tv_foot_print_rightleft.setBackgroundResource(R.drawable.feet_left_right_selected)
        }

        if (!selectfoot.isEmpty()) {
            btn_save_footprint.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_save_footprint.textColor = resources.getColor(R.color.black)
            btn_save_footprint.strokeColor = resources.getColor(R.color.colorPrimary)
        } else {
            btn_save_footprint.strokeColor = (resources.getColor(R.color.grayborder))
            btn_save_footprint.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_save_footprint.textColor = resources.getColor(R.color.colorPrimary)
        }

        btn_save_footprint.setOnClickListener {
            if (selectGender.equals(" ")) {

                MyUtils.showSnackbar(mActivity!!, getString(R.string.please_select_foot), ll_mainUpdateprofile)
            } else {

                updateUserProfile()
            }
        }
    }

    private fun NameAndBio() {

        if (!userData!!.userFirstName.isNullOrEmpty() || !userData!!.userLastName.isNullOrEmpty()) {
            btn_updateNameBio.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_updateNameBio.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_updateNameBio.textColor = resources.getColor(R.color.black)
        }
        if (!userData!!.userBio.isNullOrEmpty()) {
            btn_updateNameBio.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_updateNameBio.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_updateNameBio.textColor = resources.getColor(R.color.black)
            tv_count_Bio.text = (150 - edit_write_about_yourself.text.toString().length).toString()
        }

        edit_userFirstname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })
        edit_userLastname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                nextButtonEnable()
            }
        })

        edit_write_about_yourself.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                tv_count_Bio.text = (150 - p0!!.length).toString() /*+ "/150"*/
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                tv_count_Bio.text = p0?.length.toString()

                nextButtonEnable()
            }
        })


        btn_updateNameBio.setOnClickListener {
            if (TextUtils.isEmpty(edit_userFirstname.text.toString().trim())) {
                MyUtils.showSnackbar(mActivity!!, "Please Enter First Name", ll_mainUpdateprofile)
                edit_userFirstname.requestFocus()
            } else if (TextUtils.isEmpty(edit_userLastname.text.toString().trim())) {
                MyUtils.showSnackbar(mActivity!!, "Please Enter Last Name", ll_mainUpdateprofile)
                edit_userLastname.requestFocus()
            } else if (TextUtils.isEmpty(edit_write_about_yourself.text.toString().trim())) {
                MyUtils.showSnackbar(mActivity!!, "Please Enter about YourSelf", ll_mainUpdateprofile)
                edit_write_about_yourself.requestFocus()
            } else if (edit_write_about_yourself.text.toString().length >= 150) {
                MyUtils.showSnackbar(mActivity!!, "Maximun lenght should be 150 in Bio", ll_mainUpdateprofile)
                edit_write_about_yourself.requestFocus()
            } else {
                updateUserProfile()
            }
        }
    }

    fun nextButtonEnable() {
        if (validateNameBioInput()) {
            btn_updateNameBio.strokeColor = (resources.getColor(R.color.colorPrimary))
            btn_updateNameBio.backgroundTint = (resources.getColor(R.color.colorPrimary))
            btn_updateNameBio.textColor = resources.getColor(R.color.black)
        } else {
            btn_updateNameBio.strokeColor = (resources.getColor(R.color.grayborder))
            btn_updateNameBio.backgroundTint = (resources.getColor(R.color.transperent1))
            btn_updateNameBio.textColor = resources.getColor(R.color.colorPrimary)
        }
    }

    fun validateNameBioInput(): Boolean {
        var valid: Boolean = false
        when {
            edit_userFirstname.text.toString().trim().isEmpty() -> {
                return valid
            }
            edit_userLastname.text.toString().trim().isEmpty() -> {
                return valid
            }
            edit_write_about_yourself.text.toString().trim().isEmpty() -> {
                return valid
            }
            else -> {
                valid = true
            }
        }
        return valid
    }

    private fun updateUserProfile() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                MyUtils.dismissProgressDialog()
                ErrorUtil.errorMethod(ll_main_login)
                return@OnCompleteListener
            }
            val token = task.result
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("userDeviceID",token)
            jsonObject.put("userDeviceType", RestClient.apiType)
            jsonObject.put("userEmail", userData?.userEmail)
            if (type.equals("NameAndBio")) {
                jsonObject.put(
                    "userFirstName",
                    edit_userFirstname.text.toString().trim().capitalize()
                )
                jsonObject.put(
                    "userLastName",
                    edit_userLastname.text.toString().trim().capitalize()
                )
            } else if (type.equals("BasicDetails")) {
                jsonObject.put("userFirstName", edit_first_name.text.toString().trim().capitalize())
                jsonObject.put("userLastName", edit_last_name.text.toString().trim().capitalize())
            } else {
                jsonObject.put("userFirstName", userData!!.userFirstName)
                jsonObject.put("userLastName", userData!!.userLastName)
            }
            jsonObject.put("userGender", selectGender)
            jsonObject.put("userMobile", userData!!.userMobile)
            jsonObject.put("userPassword", userData!!.userPassword)
            jsonObject.put("userCountryCode", userData!!.userCountryCode)
            jsonObject.put("userProfilePicture", userData!!.userProfilePicture)
            jsonObject.put("userReferKey", "")
            if (!userData?.apputypeID.isNullOrEmpty()) {
                jsonObject.put("apputypeID", userData?.apputypeID)
            } else {
                jsonObject.put("apputypeID", "0")

            }
            if (!userData?.footbltypeID.isNullOrEmpty()) {
                jsonObject.put("footbltypeID", userData?.footbltypeID)
            } else {
                jsonObject.put("footbltypeID", "0")

            }
            if (!userData?.agegroupID.isNullOrEmpty()) {
                jsonObject.put("agegroupID", userData?.agegroupID)
            } else {
                jsonObject.put("agegroupID", "0")

            }
            if (!userData?.appuroleID.isNullOrEmpty()) {
                jsonObject.put("appuroleID", userData?.appuroleID)
            } else {
                jsonObject.put("appuroleID", "0")

            }
            if (!userData?.appuroleID.isNullOrEmpty()) {
                jsonObject.put("appuroleID", userData?.appuroleID)
            } else {
                jsonObject.put("appuroleID", "0")

            }
            if (!userData?.specialityID.isNullOrEmpty()) {
                jsonObject.put("specialityID", userData?.specialityID)
            } else {
                jsonObject.put("specialityID", "0")

            }
            jsonObject.put("specialityID", "0")
            jsonObject.put("userBestFoot", selectfoot)
            if (edit_dateofbirth.text.toString().isNotEmpty()) {
                jsonObject.put(
                    "userDOB", MyUtils.formatDate(
                        edit_dateofbirth.text.toString().trim(),
                        "dd MMM yyyyy", "yyyy-MM-dd"
                    )
                )
            } else {
                jsonObject.put("userDOB", userData?.userDOB)
            }
            if (!footballagecatId.isNullOrEmpty()) {
                jsonObject.put("footballagecatID", footballagecatId)
            }
            jsonObject.put("userFullname", userData?.userFullname)
            jsonObject.put("userPlaceofBirth", edit_place_of_birth?.text.toString().trim())
            if (!edit_write_about_yourself.text.toString().trim().isNullOrEmpty()) {
                jsonObject.put("userBio", edit_write_about_yourself.text.toString().trim())
            } else {
                jsonObject.put("userBio", userData?.userBio)

            }
            if (!edittext_height.text.toString().trim().isNullOrEmpty()) {
                jsonObject.put("userHeight", edittext_height.text.toString().trim())
            } else {
                jsonObject.put("userHeight", userData?.userHeight)

            }
            if (!edittext_weight.text.toString().trim().isNullOrEmpty()) {
                jsonObject.put("userWeight", edittext_weight.text.toString().trim())
            } else {
                jsonObject.put("userWeight", userData?.userWeight)

            }
            jsonObject.put("userCoverPhoto", userData!!.userCoverPhoto)
            jsonObject.put("userAlternateMobile", userData!!.userAlternateMobile)
            jsonObject.put("userWebsite", userData?.userWebsite)
            jsonObject.put("userAlternateEmail", userData?.userAlternateEmail)
            jsonObject.put("userNickname", userData?.userNickname)
            jsonObject.put("clubID", userData?.clubID)
            if (!footballLevelId.isNullOrEmpty()) {
                jsonObject.put("footbllevelID", footballLevelId)
            }
            jsonObject.put("userHomeCountryID", userData?.userHomeCountryID)
            jsonObject.put("userHomeCountryName", userData?.userHomeCountryName)
            jsonObject.put("userHomeCityName", userData?.userHomeCityName)
            jsonObject.put("userHomeCityID", userData?.userHomeCityID)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        loginModel.userRegistration(mActivity!!, false, jsonArray.toString(), "update_userProfile")
            .observe(mActivity!!,
                androidx.lifecycle.Observer { loginPojo ->
                    if (loginPojo != null) {
                        if (loginPojo.get(0).status.equals("true", true)) {
                            MyUtils.dismissProgressDialog()
                            try {
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    loginPojo.get(0).message!!,
                                    ll_mainUpdateprofile
                                )
                                StoreSessionManager(loginPojo[0].data[0])
                                Handler().postDelayed({
                                    (activity as MainActivity).onBackPressed()
                                }, 1000)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        } else {
                            MyUtils.dismissProgressDialog()
                            MyUtils.showSnackbar(
                                mActivity!!,
                                loginPojo.get(0).message!!,
                                ll_mainUpdateprofile
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(ll_mainUpdateprofile)
                    }
                })

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


    fun errorMethod() {
        progress.visibility = View.GONE
        relativeprogressBar.visibility = View.GONE
        try {
            nointernetMainRelativelayout.visibility = View.VISIBLE
            nointernet.visibility = View.VISIBLE
            if (MyUtils.isInternetAvailable(mActivity!!)) {
                nointernetImageview.setImageDrawable(mActivity!!.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernet_Imageview.setImageDrawable(mActivity!!.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernettextview.text = (getString(R.string.error_crash_error_message))
                nointernet_textview.text = (this.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(mActivity!!.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernet_Imageview.setImageDrawable(mActivity!!.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_common_network))
                nointernet_textview.text = (this.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}
