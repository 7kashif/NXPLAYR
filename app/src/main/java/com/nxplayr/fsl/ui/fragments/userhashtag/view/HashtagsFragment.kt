package com.nxplayr.fsl.ui.fragments.userhashtag.view


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userhashtag.adapter.HashtagAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.userhashtag.viewmodel.HashtagsModel
import com.nxplayr.fsl.data.model.Hashtags
import com.nxplayr.fsl.data.model.HashtagsPojo
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_languages.*
import kotlinx.android.synthetic.main.fragment_hashtags.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar2.*
import kotlinx.android.synthetic.main.toolbar2.toolbar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class HashtagsFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var hashtagtList: ArrayList<Hashtags?>? = ArrayList()
    var hashtagAdapter: HashtagAdapter? = null
    var mActivity: Activity? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var pageNo = 0
    var userId = ""
    private lateinit var hashtagsModel: HashtagsModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_hashtags, container, false)
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
            userId = arguments?.getString("userId", "")!!
        }
        setupViewModel()
        setupUI()
    }

    private fun setupObserver() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            if (userId.equals(userData?.userID)) {
                jsonObject.put("loginuserID", userData?.userID)
            } else {
                jsonObject.put("loginuserID", userId)

            }
            jsonObject.put("languageID", userData?.languageID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        hashtagsModel.getHashtagsList(mActivity!!, false, jsonArray.toString(), "List")
            .observe(
                viewLifecycleOwner,
                Observer { hashtagsPojo ->

                    relativeprogressBar.visibility = View.GONE
                    recyclerview.visibility = View.VISIBLE

                    if (hashtagsPojo != null && hashtagsPojo.isNotEmpty()) {
                        if (hashtagsPojo[0].status.equals("true", true)) {
                            hashtagtList?.clear()
                            hashtagtList?.addAll(hashtagsPojo[0].data)
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
                        ErrorUtil.errorView(activity!!, nointernetMainRelativelayout)
                    }
                })

    }

    private fun setupUI() {
        tvToolbarTitle1.text = getString(R.string.hashtags)
        if (userId.equals(userData?.userID)) {
            add_icon_connection.visibility = View.VISIBLE
        } else {
            add_icon_connection.visibility = View.GONE

        }

        toolbar.setNavigationOnClickListener {
            MyUtils.hideKeyboard1(mActivity!!)
            (activity as MainActivity).onBackPressed()
        }

        add_icon_connection.setOnClickListener(this)
        hashtagAdapter =
            HashtagAdapter(mActivity!!, hashtagtList, object : HashtagAdapter.OnItemClick {
                override fun onClicled(position: Int, from: String) {
                    deleteHashtags(hashtagtList!![position]!!.userhashtagID, position)

                }

            }, userId, userData?.userID)
        recyclerview.adapter = hashtagAdapter
        recyclerview.layoutManager = LinearLayoutManager(mActivity!!)
        recyclerview.setHasFixedSize(true)
        hashtagAdapter?.notifyDataSetChanged()

        setupObserver()

        btnRetry!!.setOnClickListener(this)

    }

    private fun setupViewModel() {
        hashtagsModel = ViewModelProvider(this@HashtagsFragment).get(HashtagsModel::class.java)

    }

    private fun deleteHashtags(userhashtagID: String, position: Int) {
        MyUtils.showProgressDialog(mActivity!!, "Please wait...")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()
        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("languageID", "1")
            jsonObject.put("userhashtagID", userhashtagID)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        Log.e("data", jsonArray.toString())
        hashtagsModel.getHashtagsList(mActivity!!, false, jsonArray.toString(), "Delete")
            .observe(this@HashtagsFragment,
                androidx.lifecycle.Observer<List<HashtagsPojo>> { hashtagpojo ->
                    MyUtils.dismissProgressDialog()
                    if (hashtagpojo != null && hashtagpojo.isNotEmpty()) {

                        if (hashtagpojo[0].status.equals("true", false)) {

                            val userData = sessionManager!!.userData
                            if (userData!!.hashtags.size > 0) {
                                for (i in 0 until userData.hashtags.size) {
                                    if (hashtagtList!![position]!!.userhashtagID.equals(
                                            userData.hashtags[i].userhashtagID
                                        )
                                    ) {
                                        userData.hashtags.removeAt(i)
                                        sessionManager!!.userData = userData
                                        hashtagtList!!.removeAt(position)
                                        break
                                    }
                                }
                                hashtagAdapter!!.notifyDataSetChanged()
                            }

                        } else {
                            if (activity != null && activity is MainActivity)
                                MyUtils.showSnackbar(
                                    mActivity!!,
                                    hashtagpojo.get(0).message, ll_mainHashtagsList
                                )
                        }

                    } else {
                        ErrorUtil.errorMethod(ll_mainHashtagsList)


                    }
                })


    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.add_icon_connection -> {
                (activity as MainActivity).navigateTo(
                    AddHashtagsFragment(),
                    AddHashtagsFragment::class.java.name,
                    true
                )

            }
            R.id.btnRetry -> {
                setupObserver()
            }

        }
    }


}
