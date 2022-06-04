package com.nxplayr.fsl.ui.fragments.cms.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.cms.adapter.FaqCategoryListAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.cms.viewmodel.FaqCategoryListModel
import com.nxplayr.fsl.data.model.FaqCategoryData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONObject


class FAQTopicsFragment : Fragment(),View.OnClickListener {

    private var v: View? = null
    var mActivity: AppCompatActivity? = null
    var faqCategoryListAdapter: FaqCategoryListAdapter? = null
    var faqCategoryList: ArrayList<FaqCategoryData>? = ArrayList()
    private lateinit var gridLayoutManager: GridLayoutManager
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null

    private lateinit var  faqCategoryModel: FaqCategoryListModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_f_a_q_topics, container, false)
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
            jsonObject.put("loginuserID", "0")
            jsonObject.put("searchWord", "")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        jsonArray.put(jsonObject)
        faqCategoryModel.getFaqCategoryList(mActivity!!, false, jsonArray.toString())
            .observe(viewLifecycleOwner
            ) { faqCategoryPojo ->

                relativeprogressBar.visibility = View.GONE
                recyclerview.visibility = View.VISIBLE

                if (faqCategoryPojo != null && faqCategoryPojo.isNotEmpty()) {
                    if (faqCategoryPojo[0].status.equals("true", true)) {
                        faqCategoryList?.clear()
                        faqCategoryList?.addAll(faqCategoryPojo[0].data)
                        faqCategoryListAdapter?.notifyDataSetChanged()

                    } else {

                        if (faqCategoryList!!.size == 0) {
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
        tvToolbarTitle.setText(R.string.faq)

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }


        gridLayoutManager = GridLayoutManager(mActivity, 2)
        faqCategoryListAdapter = FaqCategoryListAdapter(mActivity!!, faqCategoryList!!, object : FaqCategoryListAdapter.OnItemClick {
            override fun onClicled(position: Int, from: String) {
                val bundle = Bundle()
                bundle.putString("faqcategoryID", faqCategoryList!![position].faqcategoryID)
                (activity as MainActivity).navigateTo(FaqFragment(), bundle, FaqFragment::class.java.name, true)
            }
        }, sessionManager!!)
        recyclerview.layoutManager = gridLayoutManager
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = faqCategoryListAdapter
        faqCategoryListAdapter?.notifyDataSetChanged()
        faqCategoryList()

        btnRetry.setOnClickListener(this)
    }

    private fun setupViewModel() {
         faqCategoryModel = ViewModelProvider(this@FAQTopicsFragment).get(FaqCategoryListModel::class.java)

    }


    private fun faqCategoryList() {


    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
            R.id.btnRetry->{
                faqCategoryList()

            }
        }    }


}
