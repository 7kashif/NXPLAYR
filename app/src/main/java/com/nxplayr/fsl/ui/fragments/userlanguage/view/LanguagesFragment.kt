package com.nxplayr.fsl.ui.fragments.userlanguage.view


import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userlanguage.adapter.LanguagesAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ProfileLanguageData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.fragments.userlanguage.viewmodel.ProfileLanguageModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.PopupMenu
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_hashtags.*
import kotlinx.android.synthetic.main.fragment_languages.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.android.synthetic.main.toolbar2.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class LanguagesFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var language_list: ArrayList<ProfileLanguageData?>? = ArrayList()
    var languagesAdapter: LanguagesAdapter? = null
    var mActivity: Activity? = null
    var list: java.util.ArrayList<String>? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var pageNo = 0
    private lateinit var  languageModel: ProfileLanguageModel

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_languages, container, false)
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
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        languageModel.getLanguageList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(
                viewLifecycleOwner,
                { languagesPojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (languagesPojo != null && languagesPojo.isNotEmpty()) {
                        if (languagesPojo[0].status.equals("true", true)) {
                            language_list?.clear()
                            language_list?.addAll(languagesPojo[0].data)
                            languagesAdapter?.notifyDataSetChanged()


                        } else {

                            if (language_list!!.size == 0) {
                                ll_no_data_found.visibility = View.VISIBLE
                                recyclerview.visibility = View.GONE

                            } else {
                                ll_no_data_found.visibility = View.GONE
                                recyclerview.visibility = View.VISIBLE

                            }


                        }
                    } else {
                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })
    }

    private fun setupUI() {
        tvToolbarTitle1.text = getString(R.string.languages)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
        add_icon_connection.setOnClickListener(this)

        btnRetry!!.setOnClickListener(this)


        languagesAdapter = LanguagesAdapter(activity as MainActivity, language_list, object : LanguagesAdapter.OnItemClick {

            override fun onClicled(position: Int, from: String, v: View) {
                when (from) {
                    "deleteLan" -> {
                        list = java.util.ArrayList()
                        list!!.add("Edit")
                        list!!.add("Delete")

                        PopupMenu(mActivity!!, v, list!!).showPopUp(object : PopupMenu.OnMenuSelectItemClickListener {
                            override fun onItemClick(item: String, pos: Int) {
                                when (pos) {
                                    0 -> {
                                        var bundle = Bundle()
                                        bundle.putString("from", "edit")
                                        bundle.putSerializable("userlanlID", language_list!![position]!!)
                                        bundle.putInt("pos", position)

                                        (activity as MainActivity).navigateTo(AddLanguageFragment(), bundle, AddLanguageFragment::class.java.name, true)
                                    }
                                    1 -> {
                                        MyUtils.showMessageYesNo(mActivity!!, activity!!.resources.getString(R.string.language_remove_dialog),
                                            "", DialogInterface.OnClickListener { dialogInterface, i ->
                                                deleteLanguage(language_list!![position]!!.userlanguageID, position)
                                            })
                                    }
                                }
                            }

                        })
                    }
                }
            }
        }, "Language")
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = languagesAdapter
        setupObserver()

    }

    private fun setupViewModel() {
         languageModel = ViewModelProvider(this@LanguagesFragment).get(ProfileLanguageModel::class.java)

    }

    private fun deleteLanguage(userlanguageID: String, position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("userlanguageID", userlanguageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        languageModel.getLanguageList(mActivity!!, false, jsonArray.toString(), "Delete")
                .observe(this@LanguagesFragment,
                    { languagepojo ->
                        MyUtils.dismissProgressDialog()
                        if (languagepojo != null && languagepojo.isNotEmpty()) {

                            if (languagepojo[0].status.equals("true", false)) {

                                val userData = sessionManager!!.userData
                                if (userData!!.languages.size > 0) {
                                    for (i in 0 until userData.languages.size) {
                                        if (language_list!![position]!!.userlanguageID.equals(
                                                userData.languages[i].userlanguageID)) {
                                            userData.languages.removeAt(i)
                                            sessionManager!!.userData = userData
                                            language_list!!.removeAt(position)
                                            break
                                        }
                                    }
                                    languagesAdapter!!.notifyDataSetChanged()
                                }

                            } else {
                                if (activity != null && activity is MainActivity)
                                    MyUtils.showSnackbar(mActivity!!, languagepojo[0].message, ll_mainLaguages)
                            }

                        } else {
                            ErrorUtil.errorMethod(ll_mainHashtagsList)


                        }
                    })


    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRetry->{
                pageNo = 0
                setupObserver()
            }
            R.id.add_icon_connection->{
                (activity as MainActivity).navigateTo(AddLanguageFragment(), AddLanguageFragment::class.java.name, true)
            }
        }
    }

}
