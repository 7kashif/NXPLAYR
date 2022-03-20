package com.nxplayr.fsl.ui.activity.addhashtag.view


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.fragments.userhashtag.adapter.AddHashtagAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.activity.addhashtag.viewmodel.HashtagsListModel
import com.nxplayr.fsl.data.model.Hashtags
import com.nxplayr.fsl.data.model.HashtagsList
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.nxplayr.fsl.R
import com.nxplayr.fsl.base.BaseActivity
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_hashtag.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.toolbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class AddHashtagsActivity : BaseActivity(),View.OnClickListener {

    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var hashtagsList: ArrayList<HashtagsList> = ArrayList()
    var hashtagsID: String = ""
    var hashtagtList: ArrayList<Hashtags?>? = ArrayList()
    var hashtagAdapter: AddHashtagAdapter? = null
    var from = ""
    private lateinit var  hashtagListModel: HashtagsListModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_hastag)

        from = intent!!.getStringExtra("from")!!

        sessionManager = SessionManager(this@AddHashtagsActivity)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }
        setupViewModel()
        setupUI()

    }

    private fun setupUI() {
        img_backHashtag.setOnClickListener(this)
        tv_added_hashtags.setOnClickListener (this)

        edittext_addHashtags.setSelection(edittext_addHashtags.text!!.length);
        edittext_addHashtags.requestFocus()
        MyUtils.showKeyboardWithFocus(edittext_addHashtags, this!!)

        hashtagAdapter = AddHashtagAdapter(this@AddHashtagsActivity!!, hashtagtList, object : AddHashtagAdapter.OnItemClick {
            override fun onClicled(position: Int, from: String, hashtagsList: Hashtags) {
                MyUtils.hideKeyboard1(this@AddHashtagsActivity!!)
//                ll_mainhashtags.visibility = View.GONE
                ll_mainAddHashtags.visibility = View.VISIBLE
//                add_hashtags_edit_text.setText(hashtagsList.hashtagName)
            }
        })
        recyclerview.adapter = hashtagAdapter
        recyclerview.layoutManager = LinearLayoutManager(this@AddHashtagsActivity)
        recyclerview.setHasFixedSize(true)
        hashtagAdapter?.notifyDataSetChanged()
        hashTagsListApi()

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
    }

    private fun setupViewModel() {
         hashtagListModel = ViewModelProvider(this@AddHashtagsActivity).get(HashtagsListModel::class.java)

    }


    private fun hashTagsListApi() {
        MyUtils.showProgressDialog(this@AddHashtagsActivity!!, "Please wait...")
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
        hashtagListModel.getHashtagList(this@AddHashtagsActivity!!, false, jsonArray.toString())
                .observe(this@AddHashtagsActivity!!,
                        androidx.lifecycle.Observer { hashtagsListpojo ->
                            if (hashtagsListpojo != null && hashtagsListpojo.isNotEmpty()) {

                                if (hashtagsListpojo[0].status.equals("true", false)) {
                                    MyUtils.dismissProgressDialog()

                                    hashtagsList!!.clear()
                                    hashtagsList!!.addAll(hashtagsListpojo[0].data!!)
                                } else {
                                    MyUtils.dismissProgressDialog()
                                    MyUtils.showSnackbar(this@AddHashtagsActivity!!, hashtagsListpojo[0].message, ll_mainhashtags)
                                }

                            } else {
                                MyUtils.dismissProgressDialog()
                                errorMethod()
                            }
                        })


    }


    private fun errorMethod() {
        relativeprogressBar.visibility = View.GONE
        try {
            nointernetMainRelativelayout.visibility = View.VISIBLE
            if (MyUtils.isInternetAvailable(this@AddHashtagsActivity!!)) {
                nointernetImageview.setImageDrawable(this@AddHashtagsActivity!!.getDrawable(R.drawable.ic_warning_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_crash_error_message))
            } else {
                nointernetImageview.setImageDrawable(this@AddHashtagsActivity!!.getDrawable(R.drawable.ic_signal_wifi_off_black_24dp))
                nointernettextview.text = (this.getString(R.string.error_common_network))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun setHashtagData() {
        if (from.equals("Add")) {
            var intent = Intent()
            intent.putExtra("add_hashtag", tv_added_hashtags.text.toString())
            setResult(115, intent)
            finish()
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@AddHashtagsActivity,true)
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
           R.id.img_backHashtag->{
               onBackPressed()
           }
           R.id.tv_added_hashtags->{
               setHashtagData()
           }

        }
    }
}
