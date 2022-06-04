package com.nxplayr.fsl.ui.fragments.cms.view

import android.content.Context
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
import com.nxplayr.fsl.ui.fragments.cms.adapter.FaqAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.cms.viewmodel.FaqListModel
import com.nxplayr.fsl.data.model.FaqData
import com.nxplayr.fsl.ui.fragments.main.view.HomeMainFragment
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject


class FaqFragment : Fragment(), View.OnClickListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var mActivity: AppCompatActivity? = null
    var faqAdapter: FaqAdapter? = null
    var list_faq: ArrayList<FaqData>? = ArrayList()
    var faqcategoryID = ""
    private lateinit var faqModel: FaqListModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_faq, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sessionManager = SessionManager(requireContext())
        if (arguments != null) {
            faqcategoryID = arguments!!.getString("faqcategoryID").toString()
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
            jsonObject.put("languageID", "1")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("faqcategoryID", faqcategoryID)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        faqModel.getFaqList(mActivity!!, false, jsonArray.toString())
            .observe(
                viewLifecycleOwner
            ) { faqListPojo ->

                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                if (faqListPojo != null && faqListPojo.isNotEmpty()) {
                    if (faqListPojo[0].status.equals("true", true)) {
                        list_faq?.clear()
                        list_faq?.addAll(faqListPojo[0].data)
                        faqAdapter?.notifyDataSetChanged()
                    } else {
                        if (list_faq!!.size == 0) {
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
            }
    }

    private fun setupUI() {
        tvToolbarTitle.text = getString(com.nxplayr.fsl.R.string.faq)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        faqAdapter = FaqAdapter(mActivity!!, list_faq!!, object : FaqAdapter.OnItemClick {
            override fun onClicled(position: Int, from: String) {
                var bundle = Bundle()
                bundle.putString("title", "FAQ")
                if (sessionManager?.getSelectedLanguage() != null && sessionManager?.getSelectedLanguage()?.languageID.equals(
                        "1"
                    )
                ) {
                    bundle.putString("html", list_faq!![position].faqAnswer)
                } else {
                    bundle.putString("html", list_faq!![position].faqAnswerFrench)
                }
                (activity as MainActivity).navigateTo(
                    WebViewFragment(),
                    bundle,
                    WebViewFragment::class.java.name,
                    true
                )
            }
        }, sessionManager!!)
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = faqAdapter
        faqAdapter?.notifyDataSetChanged()
        setupObserver()

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
        faqModel = ViewModelProvider(this@FaqFragment).get(FaqListModel::class.java)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnRetry -> {
                setupObserver()
            }
        }
    }

}
