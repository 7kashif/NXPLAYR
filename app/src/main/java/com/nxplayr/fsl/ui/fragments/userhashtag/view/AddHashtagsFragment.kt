package com.nxplayr.fsl.ui.fragments.userhashtag.view


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userhashtag.adapter.AddHashtagAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userhashtag.viewmodel.HashtagsModel
import com.nxplayr.fsl.data.model.Hashtags
import com.nxplayr.fsl.data.model.HashtagsList
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.Gson
import com.nxplayr.fsl.ui.fragments.bottomsheet.BottomSheetListFragment
import com.nxplayr.fsl.ui.activity.onboarding.viewmodel.SignupModel
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_hashtag.*
import kotlinx.android.synthetic.main.fragment_add_languages.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class AddHashtagsFragment : Fragment(), BottomSheetListFragment.SelectLanguage,View.OnClickListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var hashtagsList: ArrayList<HashtagsList> = ArrayList()
    var hashtagsID: String = ""
    var mActivity: AppCompatActivity? = null
    private var hashtagsUpdateListener: HashtagsUpdateListener? = null
    var hashtagtList: ArrayList<Hashtags?>? = ArrayList()
    var hashtagAdapter: AddHashtagAdapter? = null
    private lateinit var  loginModel: SignupModel
    private lateinit var  addHashtagsModel: HashtagsModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (v == null)
            v = inflater.inflate(R.layout.fragment_add_hashtag, container, false)

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            hashtagsUpdateListener = context as HashtagsUpdateListener
        } catch (e: Exception) {

        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvToolbarTitle.setText(getString(R.string.add_hashtags))

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
        addHashtagsModel.getHashtagsList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(mActivity!!,
                { hashtagsPojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (hashtagsPojo != null && hashtagsPojo.isNotEmpty()) {
                        if (hashtagsPojo[0].status.equals("true", true)) {
                            hashtagtList?.clear()
                            hashtagtList?.addAll(hashtagsPojo!![0]!!.data!!)
                            hashtagAdapter?.notifyDataSetChanged()

                        } else {

                            if (hashtagtList!!.size == 0) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE

                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE
                            }

                        }
                    } else {
                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }
                })
    }

    private fun setupUI() {
        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }
        ll_mainAddHashtags.visibility = View.VISIBLE

        add_hashtags_edit_text.setOnClickListener(this)
        img_backHashtag.setOnClickListener (this)
        add_hashtags_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                btn_savehashtag.strokeColor = (resources.getColor(R.color.colorPrimary))
                btn_savehashtag.backgroundTint = (resources.getColor(R.color.colorPrimary))
                btn_savehashtag.textColor = resources.getColor(R.color.black)
                if (p0!!.isEmpty()) {
                    btn_savehashtag.strokeColor = (resources.getColor(R.color.grayborder))
                    btn_savehashtag.backgroundTint = (resources.getColor(R.color.transperent1))
                    btn_savehashtag.textColor = resources.getColor(R.color.colorPrimary)
                }
            }

        })
        var isSelfChanging = false
        edittext_addHashtags.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!isSelfChanging) {
                    isSelfChanging = true

                    val formattedText = ("#" + p0.toString())
                        .replaceFirst("##", "#")
                        .replace(" #", " ")
                        .replace(Regex(" (\\S)"), " #$1")
                        .toLowerCase(Locale.getDefault())
                    if (p0.toString() != formattedText) {
                        p0?.replace(0, p0.length, formattedText, 0, formattedText.length)
                    }
                    isSelfChanging = false
                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {


                if (!hashtagtList.isNullOrEmpty()) {
                    for (i in 0 until hashtagtList!!.size) {
                        if (p0.toString().equals(hashtagtList!![i]!!.hashtagName)) {
                            tv_added_hashtags.visibility = View.GONE
                            baseline_hashtag.visibility = View.GONE
                        } else {
                            tv_added_hashtags.visibility = View.VISIBLE
                            baseline_hashtag.visibility = View.VISIBLE
                            tv_added_hashtags.text = edittext_addHashtags.text.toString()
                        }
                    }
                } else {
                    tv_added_hashtags.visibility = View.VISIBLE
                    baseline_hashtag.visibility = View.VISIBLE
                    tv_added_hashtags.text = edittext_addHashtags.text.toString()
                }
                hashtagAdapter!!.filter?.filter(p0.toString())
                hashtagAdapter?.notifyDataSetChanged()

            }
        })

        tv_added_hashtags.setOnClickListener(this)

        btn_savehashtag.setOnClickListener(this)
    }

    private fun setupViewModel() {
         addHashtagsModel = ViewModelProvider(this@AddHashtagsFragment).get(HashtagsModel::class.java)
         loginModel = ViewModelProvider(this@AddHashtagsFragment).get(SignupModel::class.java)
    }

    override fun onLanguageSelect(value: String, from: String) {

        add_hashtags_edit_text.setText(value)
        for (i in 0 until hashtagsList!!.size) {
            if (value.equals(hashtagsList!![i]!!.hashtagName, false)) {
                if (!hashtagsID.equals(hashtagsList!![i]!!.hashtagID))
                    hashtagsID = hashtagsList!![i]!!.hashtagID
            }
        }

    }

    private fun addHashtags() {

        btn_savehashtag.startAnimation()

        val jsonObject = JSONObject()
        val jsonArrayhashtags = JSONArray()

        try {

            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("languageID", "1")
            jsonObject.put("loginuserID", userData?.userID)

            if (add_hashtags_edit_text != null) {
                val hashtagDetailPojo = JSONObject()
                hashtagDetailPojo.put("hashtagName", add_hashtags_edit_text.text.toString().trim())
                hashtagDetailPojo.put("hashtagID", hashtagsID)
                jsonArrayhashtags.put(hashtagDetailPojo)
            }
            jsonObject.put("hashtagdetails", jsonArrayhashtags)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonArray = JSONArray()
        jsonArray.put(jsonObject)
        addHashtagsModel.getHashtagsList(mActivity!!, false, jsonArray.toString(), "Add")
                .observe(mActivity!!,
                    { loginPojo ->
                        if (loginPojo != null) {
                            btn_savehashtag.endAnimation()
                            if (loginPojo.get(0).status.equals("true", false)) {
                                try {
                                    userData!!.hashtags.clear()
                                    userData?.hashtags?.addAll(loginPojo.get(0).data)
                                    if (hashtagsUpdateListener != null)
                                        hashtagsUpdateListener!!.onHashtagsUpdate()
                                    Handler().postDelayed({
                                        (activity as MainActivity).onBackPressed()
                                    }, 2000)
                                    StoreSessionManager(userData)
                                    MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message, main_add_hashtag)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            } else {
                                MyUtils.showSnackbar(mActivity!!, loginPojo.get(0).message, main_add_hashtag)
                            }

                        } else {
                            btn_savehashtag.endAnimation()
                            ErrorUtil.errorMethod(main_add_hashtag)
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


    interface HashtagsUpdateListener {
        fun onHashtagsUpdate()
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.add_hashtags_edit_text->{
                ll_mainhashtags.visibility = View.VISIBLE
                ll_mainAddHashtags.visibility = View.GONE
                edittext_addHashtags.setSelection(edittext_addHashtags.text!!.length);
                edittext_addHashtags.requestFocus()
                MyUtils.showKeyboardWithFocus(edittext_addHashtags, mActivity!!)

                hashtagAdapter = AddHashtagAdapter(mActivity!!, hashtagtList, object : AddHashtagAdapter.OnItemClick {
                    override fun onClicled(position: Int, from: String, hashtagsList: Hashtags) {
                        MyUtils.hideKeyboard1(mActivity!!)
                        ll_mainhashtags.visibility = View.GONE
                        ll_mainAddHashtags.visibility = View.VISIBLE
                        add_hashtags_edit_text.setText(hashtagsList.hashtagName)
                    }
                })
                recyclerview.adapter = hashtagAdapter
                recyclerview.layoutManager = LinearLayoutManager(activity)
                recyclerview.setHasFixedSize(true)
                hashtagAdapter?.notifyDataSetChanged()
                setupObserver()

            }
            R.id.btnRetry->{

                setupObserver()

            }
            R.id.img_backHashtag->{
                ll_mainhashtags.visibility = View.GONE
                ll_mainAddHashtags.visibility = View.VISIBLE
                MyUtils.hideKeyboard1(mActivity!!)
            }
            R.id.tv_added_hashtags->{
                MyUtils.hideKeyboard1(mActivity!!)
                ll_mainhashtags.visibility = View.GONE
                ll_mainAddHashtags.visibility = View.VISIBLE
                add_hashtags_edit_text.setText(tv_added_hashtags.text.toString())
            }
            R.id.btn_savehashtag->{
                MyUtils.hideKeyboard1(mActivity!!)
                if (TextUtils.isEmpty(add_hashtags_edit_text.text.toString().trim())) {
                    MyUtils.showSnackbar(mActivity!!, "Enter Hashtag", main_add_hashtag)
                    add_hashtags_edit_text.requestFocus()
                } else {
                    addHashtags()
                }
            }
        }
    }


}
