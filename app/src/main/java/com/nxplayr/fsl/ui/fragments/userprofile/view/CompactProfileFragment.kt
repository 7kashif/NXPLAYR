package com.nxplayr.fsl.ui.fragments.userprofile.view


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ClubListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.addcountry.view.CurrentCountryActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModelV2
import com.nxplayr.fsl.ui.fragments.PreferreOutfittersFragment
import com.nxplayr.fsl.ui.fragments.usercontractsituation.view.ContractSitiuationFragment
import com.nxplayr.fsl.ui.fragments.usercurrentclub.view.CurrentClubFragment
import com.nxplayr.fsl.ui.fragments.usercurrentclub.view.PreviousFragment
import com.nxplayr.fsl.ui.fragments.userfootballleague.view.FootballLeagueFragment
import com.nxplayr.fsl.ui.fragments.usergeographical.view.GeographicalFragment
import com.nxplayr.fsl.ui.fragments.usergeographical.viewmodel.UpdateResumeCallsViewModel
import com.nxplayr.fsl.ui.fragments.userjerusynumber.view.JerusyNumberFragment
import com.nxplayr.fsl.ui.fragments.usernationteam.view.NationalTeamFragment
import com.nxplayr.fsl.ui.fragments.userpasspassportnationality.view.PassportNationalityFragment
import com.nxplayr.fsl.ui.fragments.userpitchposition.view.PitchPositionFragment
import com.nxplayr.fsl.ui.fragments.usersetofskills.view.SetOfSkillsFragment
import com.nxplayr.fsl.ui.fragments.usertrophyhonors.view.TrophyHonorsFragment
import com.nxplayr.fsl.ui.fragments.userwebsite.view.StaticWebsiteFragment
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.LocationProvider
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.activity_profile.ll_basic
import kotlinx.android.synthetic.main.activity_profile.ll_best_foot
import kotlinx.android.synthetic.main.activity_profile.ll_current_club
import kotlinx.android.synthetic.main.activity_profile.ll_foorball_level
import kotlinx.android.synthetic.main.activity_profile.ll_football_age_group
import kotlinx.android.synthetic.main.activity_profile.ll_height_weight
import kotlinx.android.synthetic.main.activity_profile.ll_nationality
import kotlinx.android.synthetic.main.fragment_compact_profile.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException


class CompactProfileFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var clubId = ""
    var clubName = ""
    var countryName = ""
    private val REQUEST_CODE_LOCATION_PERMISSIONS = 6
    var LATITUDE: Double = 0.00
    var LONGITUDE: Double = 0.00
    private var locationProvider: LocationProvider? = null
    var isLocationGot: Boolean = false
    var infaltorScheduleMode: LayoutInflater? = null
    var fromProfile = ""
    var userId = ""
    var otherUserData: SignupData? = null
    private lateinit var loginModel: SignupModelV2
    private lateinit var passportNationalityModel: UpdateResumeCallsViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // if (v == null) {
        v = inflater.inflate(R.layout.fragment_compact_profile, container, false)
        // }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        if (arguments != null) {
            fromProfile = arguments!!.getString("fromProfile").toString()
            userId = arguments!!.getString("userID").toString()
        }
        setupViewModel()
        setupUI()
//        setupObserver()
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//
//        sessionManager = SessionManager(mActivity!!)
//        if (sessionManager?.get_Authenticate_User() != null) {
//            userData = sessionManager?.get_Authenticate_User()
//        }
//        if (arguments != null) {
//            fromProfile = arguments!!.getString("fromProfile").toString()
//            userId = arguments!!.getString("userID").toString()
//        }
//        setupViewModel()
//        setupUI()
////        setupObserver()
//    }

    override fun onResume() {
        super.onResume()
        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngBasicDetail.isNullOrEmpty())
                tv_basic_details.text = sessionManager?.LanguageLabel?.lngBasicDetail
            if (!sessionManager?.LanguageLabel?.lngPassportNationality.isNullOrEmpty())
                tv_passport_nationality.text = sessionManager?.LanguageLabel?.lngPassportNationality
            if (!sessionManager?.LanguageLabel?.lngCurrentCountry.isNullOrEmpty())
                tv_current_country.text = sessionManager?.LanguageLabel?.lngCurrentCountry
            if (!sessionManager?.LanguageLabel?.lngFootballAgeCategory.isNullOrEmpty())
                tv_football_age_category.text =
                    sessionManager?.LanguageLabel?.lngFootballAgeCategory
            if (!sessionManager?.LanguageLabel?.lngHeight.isNullOrEmpty() &&
                !sessionManager?.LanguageLabel?.lngCm.isNullOrEmpty() &&
                !sessionManager?.LanguageLabel?.lngKg.isNullOrEmpty() &&
                !sessionManager?.LanguageLabel?.lngAnd.isNullOrEmpty() &&
                !sessionManager?.LanguageLabel?.lngWeight.isNullOrEmpty()
            )
                tv_height_weight.text =
                    sessionManager?.LanguageLabel?.lngHeight + "(" + sessionManager?.LanguageLabel?.lngCm + ") " +
                            sessionManager?.LanguageLabel?.lngAnd + " " + sessionManager?.LanguageLabel?.lngWeight + "(" + sessionManager?.LanguageLabel?.lngKg + ")"
            if (!sessionManager?.LanguageLabel?.lngFootballLevel.isNullOrEmpty())
                tv_football_level.text = sessionManager?.LanguageLabel?.lngFootballLevel
            if (!sessionManager?.LanguageLabel?.lngFootballLeague.isNullOrEmpty())
                tv_football_league.text = sessionManager?.LanguageLabel?.lngFootballLeague
            if (!sessionManager?.LanguageLabel?.lngCurrentClub.isNullOrEmpty())
                tv_current_club.text = sessionManager?.LanguageLabel?.lngCurrentClub
            if (!sessionManager?.LanguageLabel?.lngcontract.isNullOrEmpty())
                tv_contract_situation.text = sessionManager?.LanguageLabel?.lngcontract
            if (!sessionManager?.LanguageLabel?.lngPreviousClub.isNullOrEmpty())
                tv_prev_club.text = sessionManager?.LanguageLabel?.lngPreviousClub
            if (!sessionManager?.LanguageLabel?.lngPitchPosition.isNullOrEmpty())
                tv_pitch_position.text = sessionManager?.LanguageLabel?.lngPitchPosition
            if (!sessionManager?.LanguageLabel?.lngBestFootFeet.isNullOrEmpty())
                tv_best_foot.text = sessionManager?.LanguageLabel?.lngBestFootFeet
            if (!sessionManager?.LanguageLabel?.lngNationalTeam.isNullOrEmpty())
                tv_nationalTeam.text = sessionManager?.LanguageLabel?.lngNationalTeam
            if (!sessionManager?.LanguageLabel?.lngJersey.isNullOrEmpty())
                tv_jerseyNumber.text = sessionManager?.LanguageLabel?.lngJersey
            if (!sessionManager?.LanguageLabel?.lngPreferredOutfitters.isNullOrEmpty())
                tv_preferred_outfit.text = sessionManager?.LanguageLabel?.lngPreferredOutfitters
            if (!sessionManager?.LanguageLabel?.lngCurrentSkill.isNullOrEmpty())
                tv_set_of_skills.text = sessionManager?.LanguageLabel?.lngCurrentSkill
            if (!sessionManager?.LanguageLabel?.lngLinkWebsite.isNullOrEmpty())
                tv_website.text = sessionManager?.LanguageLabel?.lngLinkWebsite
            if (!sessionManager?.LanguageLabel?.lngTrophiesNHonors.isNullOrEmpty())
                tv_trophy_honor.text = sessionManager?.LanguageLabel?.lngTrophiesNHonors
            if (!sessionManager?.LanguageLabel?.lngGeographical.isNullOrEmpty())
                tv_geographical_mob.text = sessionManager?.LanguageLabel?.lngGeographical
            if (!sessionManager?.LanguageLabel?.lngAgent.isNullOrEmpty())
                tv_current_location.text = sessionManager?.LanguageLabel?.lngAgent
        }
    }

    private fun setupObserver() {
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("otherUserID", userId)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonArray.put(jsonObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        loginModel.otherUserProfile(jsonArray.toString())
    }

    private fun setupUI() {
        if (fromProfile.equals("OtherUserProfile")) {
            setupObserver()
            ll_nationality.setOnClickListener(this)
            ll_football_age_group.setOnClickListener(this)
            ll_foorball_level.setOnClickListener(this)
            ll_foorball_language.setOnClickListener(this)
            ll_current_club.setOnClickListener(this)
            ll_pitch_position.setOnClickListener(this)
            ll_best_foot.setOnClickListener(this)
            ll_static_website.setOnClickListener(this)
            ll_National_team.setOnClickListener(this)
            ll_geographical.setOnClickListener(this)
            ll_trophyhonors.setOnClickListener(this)
            ll_setskill.setOnClickListener(this)
            ll_preferred_outfitters.setOnClickListener(this)

        } else {
            setupObserver()
            setProfileData(userData!!)
            setClubData(userData!!.clubs)
            setOnClick()
        }
    }

    private fun setupViewModel() {
        loginModel = ViewModelProvider(this@CompactProfileFragment).get(SignupModelV2::class.java)
        passportNationalityModel = ViewModelProvider(this@CompactProfileFragment).get(
            UpdateResumeCallsViewModel::class.java
        )

        loginModel.otherUserProfile
            .observe(
                viewLifecycleOwner
            ) { loginPojo ->

                if (loginPojo != null) {

                    if (loginPojo[0].status.equals("true", true)) {

                        if (loginPojo[0].data.isNotEmpty()) {
                            otherUserData = loginPojo[0].data[0]
                            if (otherUserData != null) {
                                setProfileData(otherUserData!!)
                            }
                        }
                    }
                }

            }
    }

    fun setOnClick() {
        ll_current_country.setOnClickListener(this)
        ll_basic.setOnClickListener(this)
        ll_nationality.setOnClickListener(this)
        ll_football_age_group.setOnClickListener(this)
        ll_height_weight.setOnClickListener(this)
        ll_foorball_level.setOnClickListener(this)
        ll_foorball_language.setOnClickListener(this)
        ll_current_club.setOnClickListener(this)
        ll_previous_club.setOnClickListener(this)
        ll_pitch_position.setOnClickListener(this)
        ll_agent.setOnClickListener(this)
        ll_best_foot.setOnClickListener(this)
        ll_Contract.setOnClickListener(this)
        ll_static_website.setOnClickListener(this)
        ll_National_team.setOnClickListener(this)
        ll_geographical.setOnClickListener(this)
        ll_trophyhonors.setOnClickListener(this)
        ll_setskill.setOnClickListener(this)
        ll_preferred_outfitters.setOnClickListener(this)
        ll_jersury_number.setOnClickListener(this)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.ll_current_country -> {
                ll_current_country.isEnabled = false
                val currentapiVersion = Build.VERSION.SDK_INT
                if (currentapiVersion >= Build.VERSION_CODES.M) {
                    permissionLocation()
                } else {
                    connectLocation()
                }
                ll_current_country.isEnabled = true
            }
            R.id.ll_basic -> {
                var bundle = Bundle()
                bundle.putString("type", "BasicDetails")
                (activity as MainActivity).navigateTo(
                    UpdateUserProfileFragment(),
                    bundle,
                    UpdateUserProfileFragment::class.java.name,
                    true
                )
            }
            R.id.ll_nationality -> {
                var bundle = Bundle()
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    PassportNationalityFragment(),
                    bundle,
                    PassportNationalityFragment::class.java.name,
                    true
                )
            }
            R.id.ll_football_age_group -> {
                var bundle = Bundle()
                bundle.putString("type", "footballAgeCat")
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    UpdateUserProfileFragment(),
                    bundle,
                    UpdateUserProfileFragment::class.java.name,
                    true
                )
            }
            R.id.ll_height_weight -> {
                var bundle = Bundle()
                bundle.putString("type", "heightWeight")
                (activity as MainActivity).navigateTo(
                    UpdateUserProfileFragment(),
                    bundle,
                    UpdateUserProfileFragment::class.java.name,
                    true
                )
            }
            R.id.ll_foorball_level -> {
                var bundle = Bundle()
                bundle.putString("type", "footballLevel")
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    UpdateUserProfileFragment(),
                    bundle,
                    UpdateUserProfileFragment::class.java.name,
                    true
                )
            }
            R.id.ll_foorball_language -> {
                var bundle = Bundle()

                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    FootballLeagueFragment(), bundle,
                    FootballLeagueFragment::class.java.name, true
                )
            }
            R.id.ll_current_club -> {
                var bundle = Bundle()
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    CurrentClubFragment(),
                    bundle,
                    CurrentClubFragment::class.java.name,
                    true
                )
            }
            R.id.ll_previous_club -> {
                var bundle = Bundle()
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    PreviousFragment(),
                    bundle,
                    PreviousFragment::class.java.name,
                    true
                )
            }
            R.id.ll_pitch_position -> {
                var bundle = Bundle()

                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    PitchPositionFragment(),
                    bundle,
                    PitchPositionFragment()::class.java.name,
                    true
                )
            }
            R.id.ll_agent -> {
                showAgent()
            }
            R.id.ll_best_foot -> {
                var bundle = Bundle()
                bundle.putString("type", "FootballbestFeet")
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    UpdateUserProfileFragment(),
                    bundle,
                    UpdateUserProfileFragment::class.java.name,
                    true
                )
            }
            R.id.ll_static_website -> {
                var bundle = Bundle()
                bundle.putString("type", "FootballbestFeet")
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    StaticWebsiteFragment(), bundle,
                    StaticWebsiteFragment::class.java.name, true
                )
            }
            R.id.ll_Contract -> {
                var bundle = Bundle()
                bundle.putString("type", "Contract")
                (activity as MainActivity).navigateTo(
                    ContractSitiuationFragment(),
                    bundle,
                    ContractSitiuationFragment::class.java.name,
                    true
                )
            }
            R.id.ll_National_team -> {
                var bundle = Bundle()
                bundle.putString("type", "National_team")
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    NationalTeamFragment(),
                    bundle,
                    NationalTeamFragment::class.java.name,
                    true
                )
            }
            R.id.ll_jersury_number -> {
                var bundle = Bundle()
                bundle.putString("type", "jersury_number")
                (activity as MainActivity).navigateTo(
                    JerusyNumberFragment(),
                    bundle,
                    JerusyNumberFragment::class.java.name,
                    true
                )
            }
            R.id.ll_preferred_outfitters -> {
                var bundle = Bundle()
                bundle.putString("type", "preferred_outfitters")
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    PreferreOutfittersFragment(),
                    bundle,
                    PreferreOutfittersFragment::class.java.name,
                    true
                )
            }
            R.id.ll_setskill -> {
                var bundle = Bundle()
                bundle.putString("type", "setskill")
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    SetOfSkillsFragment(),
                    bundle,
                    SetOfSkillsFragment::class.java.name,
                    true
                )
            }
            R.id.ll_trophyhonors -> {
                var bundle = Bundle()
                bundle.putString("type", "trophyhonors")
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    TrophyHonorsFragment(),
                    bundle,
                    TrophyHonorsFragment::class.java.name,
                    true
                )
            }
            R.id.ll_geographical -> {
                var bundle = Bundle()
                bundle.putString("type", "geographical")
                bundle.putString("fromProfile", fromProfile)
                bundle.putString("userId", userId)
                bundle.putSerializable("otherUserData", otherUserData)
                (activity as MainActivity).navigateTo(
                    GeographicalFragment(),
                    bundle,
                    GeographicalFragment::class.java.name,
                    true
                )
            }
        }
    }

    private fun setProfileData(userData: SignupData) {

        img_userprofile.setImageURI(RestClient.image_base_url_users + userData?.userProfilePicture)

        if (userData != null) {
            tv_userName.visibility = View.VISIBLE
            tv_userName.text = userData!!.userFirstName + " " + userData!!.userLastName
            if (!userData?.passport.isNullOrEmpty()) {
                tv_NationalityName.visibility = View.VISIBLE
                var countrName = userData!!.passport.joinToString {
                    it.countryName
                }
                tv_NationalityName.text = countrName
            }
            if (!userData!!.userHeight.isNullOrEmpty() && !userData!!.userWeight.isNullOrEmpty()) {
                tv_heightWeight.visibility = View.VISIBLE
                tv_heightWeight.text = userData!!.userHeight + ", " + userData!!.userWeight
            } else {
                tv_heightWeight.text = " "
            }
            if (!userData!!.userBestFoot.isNullOrEmpty()) {
                tv_bestFootFeet.visibility = View.VISIBLE
                tv_bestFootFeet.text = userData!!.userBestFoot
            }
            if (!userData!!.footbllevelName.isNullOrEmpty()) {
                tv_footballLevel.visibility = View.VISIBLE
                tv_footballLevel.text = userData!!.footbllevelName
            }
            if (!userData!!.leagueName.isNullOrEmpty()) {
                tv_footballleague.visibility = View.VISIBLE
                tv_footballleague.text = userData!!.leagueName
            }
            if (!userData!!.footballagecatName.isNullOrEmpty()) {
                tv_footballAgeCatName.text = userData!!.footballagecatName
                tv_footballAgeCatName.visibility = View.VISIBLE
            }
            if (!userData?.location.isNullOrEmpty()) {
                tv_currentCountry.visibility = View.VISIBLE
                if (!userData!!.location[0].countryName.isNullOrEmpty()) {
                    tv_currentCountry.text = userData!!.location[0].countryName
                } else if (!userData!!.location[0].cityName.isNullOrEmpty()) {
                    tv_currentCountry.text = userData!!.location[0].cityName
                }
            }
            if (!userData!!.clubs.isNullOrEmpty()) {
                clubId = userData!!.clubs[0].clubID
                clubName = userData!!.clubs[0].clubName
                setClubData(userData!!.clubs)
            }
            if (!userData!!.plyrposiName.isNullOrEmpty()) {
                tv_pitchPosition.visibility = View.VISIBLE
                tv_pitchPosition.text = userData.plyrposiName.trim().replace(",", ", ")

            }
            if (!userData?.contractsituationName.isNullOrEmpty()) {
                tv_Contract.visibility = View.VISIBLE
                try {
                    tv_Contract.text = userData?.contractsituationName + "(${
                        MyUtils.formatDate(
                            userData?.userContractExpiryDate!!,
                            "yyyy-MM-dd",
                            "dd/MM/yyyy"
                        )
                    })"
                } catch (e: Exception) {
                    e.printStackTrace()
                    tv_Contract.text = userData?.contractsituationName
                }
            } else {

                tv_Contract.text = ""
                tv_Contract.visibility = View.GONE
            }
            if (!userData?.previousclubName.isNullOrEmpty()) {
                tv_clubName_compatProfile.visibility = View.VISIBLE
                try {
                    tv_clubName_compatProfile.text =
                        userData?.previousclubName.trim().replace(",", ", ")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                tv_clubName_compatProfile.text = ""
                tv_clubName_compatProfile.visibility = View.GONE
            }

            if (!userData?.teamcountryName.isNullOrEmpty()) {
                tv_national_team.visibility = View.VISIBLE
                try {
                    tv_national_team.text = userData?.teamcountryName
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                tv_national_team.text = ""
                tv_national_team.visibility = View.GONE
            }
            if (!userData?.userJersyNumber.isNullOrEmpty()) {
                try {
                    if (userData!!.userJersyNumber.equals("0")) {
                        tv_jersey_number.text = ""
                        tv_jersey_number.visibility = View.GONE
                    } else {
                        tv_jersey_number.text = userData?.userJersyNumber
                        tv_jersey_number.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                tv_jersey_number.text = ""
                tv_jersey_number.visibility = View.GONE
            }
            if (!userData?.outfitterNames.isNullOrEmpty()) {
                tv_preferred_outfitters.visibility = View.VISIBLE
                try {
                    tv_preferred_outfitters.text =
                        userData?.outfitterNames.trim().replace(",", ", ")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                tv_preferred_outfitters.text = ""
                tv_preferred_outfitters.visibility = View.GONE
            }
            if (!userData?.usertrophies.isNullOrEmpty()) {
                tv_Trophy.visibility = View.VISIBLE
                try {
                    tv_Trophy.text = userData?.usertrophies
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                tv_Trophy.text = ""
                tv_Trophy.visibility = View.GONE
            }

            if (!userData?.geomobilityName.isNullOrEmpty()) {
                tv_geographical.visibility = View.VISIBLE
                try {
                    tv_geographical.text = userData?.geomobilityName
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                tv_geographical.text = ""
                tv_geographical.visibility = View.GONE
            }
            if (!userData?.skills.isNullOrEmpty()) {
                tv_Setofskills.visibility = View.VISIBLE
                try {
                    tv_Setofskills.text = userData?.skills?.joinToString { it.skillName }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {

                tv_Setofskills.text = ""
                tv_Setofskills.visibility = View.GONE
            }

            if (!userData?.userAgentName.isNullOrEmpty()) {
                tv_agent.visibility = View.VISIBLE
                try {
                    tv_agent.text = userData?.userAgentName
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                tv_agent.text = ""
                tv_agent.visibility = View.GONE
            }

            if (!sessionManager?.getWebLinks().isNullOrEmpty()) {
                tv_site.visibility = View.VISIBLE
                try {
                    tv_site.text = sessionManager?.getWebLinks()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                tv_site.text = ""
                tv_site.visibility = View.GONE
            }

            /*if (!userData?.userHeight.isNullOrEmpty()) {
                tv_heightWeight.visibility = View.VISIBLE
                try {

                    tv_heightWeight.text = userData?.userHeight+ " (cm)" + " " + userData?.userWeight+ " (kg)"

                    Log.d("0000", userData?.userWeight + userData?.userHeight)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                tv_heightWeight.text = ""
                tv_heightWeight.visibility = View.GONE
            }*/
        }

    }

    private fun setClubData(clubs: ArrayList<ClubListData>) {

        ll_mainClubList.removeAllViews()
        for (i in 0 until clubs.size) {
            infaltorScheduleMode =
                activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            var convertView = infaltorScheduleMode?.inflate(
                com.nxplayr.fsl.R.layout.list_current_club_layout,
                null
            )

            var tv_clubName = convertView?.findViewById(R.id.tv_clubName) as TextView
            if (i == clubs.size - 1) {
                tv_clubName.text = clubs[i].clubName
            } else {
                tv_clubName.text = clubs[i].clubName + ","
            }

            ll_mainClubList.addView(convertView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun permissionLocation(): Boolean {

        if (!addPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            val message = getString(R.string.grant_access_location)

            MyUtils.showMessageOKCancel(mActivity!!, message, "Use Location Service?",
                DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_CODE_LOCATION_PERMISSIONS
                    )

                })

        } else {
            connectLocation()

        }
        return true
    }

    private fun addPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    mActivity!!,
                    permission
                ) !== android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {

            REQUEST_CODE_LOCATION_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    connectLocation()

                } else {

                    Toast.makeText(
                        mActivity,
                        resources.getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun selectPlace() {
        val intent = Intent(mActivity, CurrentCountryActivity::class.java)
        intent.putExtra("latitude", LATITUDE)
        intent.putExtra("longitude", LONGITUDE)
        startActivityForResult(intent, 503)


    }

    private fun connectLocation() {
        MyUtils.showProgressDialog(mActivity!!, "Please wait..")
        locationProvider = LocationProvider(mActivity!!, LocationProvider.HIGH_ACCURACY,
            object : LocationProvider.CurrentLocationCallback {
                override fun handleNewLocation(location: Location) {
                    MyUtils.dismissProgressDialog()
                    locationProvider?.disconnect()
                    if (location != null) {
                        LATITUDE = location.latitude
                        LONGITUDE = location.longitude
                        isLocationGot = true
                        selectPlace()
                    }

                }
            })
        locationProvider!!.connect()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            503 -> {
                setupObserver()
                setProfileData(userData!!)
                setClubData(userData!!.clubs)
                setOnClick()
            }
            LocationProvider.CONNECTION_FAILURE_RESOLUTION_REQUEST -> {
                connectLocation()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }

    }

    private fun showAgent() {

        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = (context as AppCompatActivity).getLayoutInflater()

        @SuppressLint("InflateParams")
        val rv = inflater.inflate(R.layout.custom_dialog_add_agent, null)
        dialogBuilder.setView(rv)
        var b = dialogBuilder.create()
        val txt_cancel = rv.findViewById(R.id.txt_cancel) as TextView
        val txt_album = rv.findViewById(R.id.txt_album) as AppCompatTextView
        val txt_okay = rv.findViewById(R.id.txt_okay) as TextView
        val edtAlbumName = rv.findViewById(R.id.edtAlbumName) as EditText

        txt_album.setText("Add Agent")
        txt_okay.setText("Add")

        if (sessionManager != null && sessionManager?.LanguageLabel != null) {
            if (!sessionManager?.LanguageLabel?.lngAddAgent.isNullOrEmpty())
                txt_album.text = sessionManager?.LanguageLabel?.lngAddAgent
            if (!sessionManager?.LanguageLabel?.lngAdd.isNullOrEmpty())
                txt_okay.text = sessionManager?.LanguageLabel?.lngAdd
            if (!sessionManager?.LanguageLabel?.lngCancel.isNullOrEmpty())
                txt_cancel.text = sessionManager?.LanguageLabel?.lngCancel
            if (!sessionManager?.LanguageLabel?.lngAgentName.isNullOrEmpty())
                edtAlbumName.hint = sessionManager?.LanguageLabel?.lngAgentName
        }
        edtAlbumName.requestFocus()

//        if (!userData?.userAgentName.isNullOrEmpty()) {
//            edtAlbumName.setText(userData?.userAgentName)
//        }
        txt_okay.setOnClickListener {

            val albumName = edtAlbumName!!.text.toString().trim()

            if (albumName.equals("")) {

                Toast.makeText(mActivity!!, "Please Enter Album Name", Toast.LENGTH_SHORT).show()
            } else {
                b!!.dismiss()
                getUpadteResume(albumName)
            }

        }

        txt_cancel.setOnClickListener {
            MyUtils.hideKeyboard1(context as AppCompatActivity)
            b!!.dismiss()
        }

        dialogBuilder.setTitle("")
        dialogBuilder.setMessage("")
        b!!.setCanceledOnTouchOutside(false)
        b.setCancelable(false)
        b.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        b.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        b.show()

    }

    private fun getUpadteResume(s: String) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("leagueID", userData?.leagueID)

            jsonObject.put("contractsituationID", userData?.contractsituationID)
            try {
                jsonObject.put(
                    "userContractExpiryDate",
                    MyUtils.formatDate(
                        userData?.userContractExpiryDate!!,
                        "dd/MM/yyyy",
                        "yyyy-MM-dd"
                    )
                )
            } catch (e: Exception) {
            }


            jsonObject.put("userPreviousClubID", userData?.userPreviousClubID)
            jsonObject.put("userJersyNumber", userData?.userJersyNumber)
            jsonObject.put("usertrophies", userData?.usertrophies)
            jsonObject.put("geomobilityID", userData?.geomobilityID)
            jsonObject.put("userNationalCountryID", userData?.userNationalCountryID)
            jsonObject.put("userNationalCap", userData?.userNationalCap)
            jsonObject.put("useNationalGoals", userData?.useNationalGoals)
            jsonObject.put("userAgentName", s)
            jsonObject.put("outfitterIDs", userData?.outfitterIDs)
            jsonObject.put("userPreviousClubID", userData?.userPreviousClubID)
            jsonObject.put("previousclubName", userData?.previousclubName)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        passportNationalityModel.getUpdateResume(mActivity!!, "Add", jsonArray?.toString())
            .observe(this@CompactProfileFragment!!,
                androidx.lifecycle.Observer { countryListPojo ->
                    if (countryListPojo != null) {
                        MyUtils.dismissProgressDialog()
                        if (countryListPojo.get(0).status.equals("true", false)) {
                            try {
                                StoreSessionManager(countryListPojo.get(0).data[0])
                                tv_agent.visibility = View.VISIBLE
                                tv_agent.setText(countryListPojo.get(0).data[0]?.userAgentName)
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    countryListPojo.get(0).message,
                                    ll_agent
                                )
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        } else {
                            MyUtils.showSnackbar(
                                mActivity!!,
                                countryListPojo.get(0).message,
                                ll_agent
                            )
                        }

                    } else {
                        MyUtils.dismissProgressDialog()
                        ErrorUtil.errorMethod(ll_agent)
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
}






