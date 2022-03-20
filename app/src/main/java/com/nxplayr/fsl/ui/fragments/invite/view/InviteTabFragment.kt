package com.nxplayr.fsl.ui.fragments.invite.view

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userconnection.adapter.AlphaBetAdapter
import com.nxplayr.fsl.ui.fragments.invite.adapter.InviteTabAdapter
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.*
import com.nxplayr.fsl.ui.fragments.invite.viewmodel.ContactListModel
import com.nxplayr.fsl.ui.fragments.invite.viewmodel.UserContactSyncModel
import com.nxplayr.fsl.util.*
import kotlinx.android.synthetic.main.activity_contact_list.*
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_tab_invite.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject


class InviteTabFragment : Fragment(),View.OnClickListener {

    var v: View? = null
    var mActivity: AppCompatActivity? = null
    var tabPosition: Int = 0
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    val PERMISSIONS_REQUEST_READ_CONTACTS = 2
    var contactSyncList: ArrayList<Contact?>? = ArrayList()
    var contactList: ArrayList<ContactListData?>? = null
    var alphabetListData: ArrayList<String>? = ArrayList()
    var alphaBetAdapter: AlphaBetAdapter? = null
    var inviteListData: ArrayList<ContactListData?>? = ArrayList()
    var inviteTabAdapter: InviteTabAdapter? = null
    var pageNo = 0
    var pageSize = 20
    var visibleItemCount: Int = 0
    var totalItemCount: Int = 0
    var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var y: Int = 0
    private var l: CharArray? = charArrayOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z')
    var idx = 0
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var linearLayoutManageralphabet: LinearLayoutManager
    var synceusers: JSONArray? = null
    var count = 0
    var totalcontacts = 0
    var incrementedCount = 50
    var uploadedCount = 0
    var totalServerCount = 0
    private lateinit var sendContactsModel: UserContactSyncModel
    private lateinit var  contactListModel: ContactListModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        if (v == null)
            v = inflater.inflate(R.layout.fragment_tab_invite, container, false)
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
        tabPosition = arguments!!.getInt("position") as Int

        setupViewModel()
        setupUI()
    }

     fun setupObserver() {
        emptyLayout(false, true)
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {
            emptyLayout(false, true)
            contactList!!.clear()
            if (inviteTabAdapter != null)
                inviteTabAdapter?.notifyDataSetChanged()
        } else {
            emptyLayout(false, false)
            recyclerview?.setVisibility(View.VISIBLE)
            contactList!!.add(null)
            inviteTabAdapter?.notifyItemInserted(contactList!!.size - 1)
        }
        val jsonObject = JSONObject()
        try {

            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsonArray = JSONArray()
        jsonArray.put(jsonObject)
        contactListModel.getContactList(mActivity!!, false, "" + jsonArray, "contact_list")
            ?.observe(viewLifecycleOwner,
                { contactsDisplayPojos ->
                    if (contactsDisplayPojos != null && contactsDisplayPojos.isNotEmpty()) {
                        isLoading = false
                        if (pageNo > 0) {
                            contactList!!.removeAt(contactList!!.size - 1)
                            inviteTabAdapter?.notifyItemRemoved(contactList!!.size)
                        }
                        if (contactsDisplayPojos[0].status.equals("true", true)) {
                            emptyLayout(false, false)

                            recyclerview?.visibility = View.VISIBLE

                            if (pageNo == 0) {
                                contactList!!.clear()
                            }
                            tv_select_connections.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.connection_icon_share, 0, 0, 0)
                            tv_select_connections.text = resources.getString(R.string.connections_selected)
                            contactList!!.addAll(contactsDisplayPojos[0].data!!)

                            if (contactList!!.size < 10) {
                                isLastpage = true
                            }

                            if (userData != null) {
                                if (!userData?.userID.isNullOrEmpty()) {
                                    for (i in 0 until contactList!!.size) {
                                        if (!contactList!![i]?.userID.isNullOrEmpty()) {
                                            if (userData?.userID.equals(contactList!![i]?.userID)) {
                                                contactList!!.removeAt(i)
                                                break
                                            }
                                        }
                                    }
                                }
                            }

                            inviteTabAdapter?.notifyDataSetChanged()
                            pageNo = pageNo + 1

                        } else {
                            if (contactList!!.size == 0) {
                                emptyLayout(true, false)

                            } else {
                                recyclerview?.visibility = View.VISIBLE
                            }

                        }

                    } else {
                        if (mActivity != null) {
                            relativeprogressBar.visibility = View.GONE
                            ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                        }

                    }
                })
    }

    private fun setupUI() {
        val arrayis: Array<String> = resources.getStringArray(R.array.alphabet)
        alphabetListData!!.clear()
        alphabetListData!!.addAll(arrayis)
        idx = l!!.size - 1

        emptyLayout(true, false)
        val permissionCheck = ContextCompat.checkSelfPermission(mActivity!!, Manifest.permission.READ_CONTACTS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getContacts()
        } else {
            showDialogMessage()
        }

        linearLayoutManager = LinearLayoutManager(mActivity)
        linearLayoutManageralphabet = LinearLayoutManager(mActivity!!)

        if (contactList == null) {
            contactList = ArrayList()

            inviteTabAdapter = InviteTabAdapter(activity as MainActivity, contactList, object : InviteTabAdapter.OnItemClick {
                override fun onClicklisneter(pos: Int, from: String, contactData: ContactListData?) {

                    when (from) {
                        "invite_contacts" -> {
                            for (i in 0 until contactList!!.size) {
                                if (i == pos) {
                                    contactData!!.checked = !(contactData!!.checked)!!

                                    if (contactData.checked!!) {
                                        inviteListData!!.add(contactData!!)
                                        (parentFragment as InviteMainFragment).inviteTabList(inviteListData!!)
                                    } else if (!(contactData!!.checked!!)) {
                                        btn_SelectAll.setImageDrawable(resources.getDrawable(R.drawable.checkbox_unselected))
                                        inviteListData!!.remove(contactData)
                                        (parentFragment as InviteMainFragment).inviteTabList(inviteListData!!)


                                    }
                                }
                            }
                            if (inviteListData!!.size > 0) {
                                var count = inviteListData?.size
                                tv_select_connections.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.connection_icon_share, 0, 0, 0)
                                tv_select_connections.text = count.toString() + " " + resources.getString(R.string.connections_selected)
                            } else {
                                tv_select_connections.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.connection_icon_share, 0, 0, 0)
                                tv_select_connections.text = resources.getString(R.string.connections_selected)
                            }
                            inviteTabAdapter?.notifyDataSetChanged()
                        }
                    }

                }
            }, tabPosition)
            recyclerview.setHasFixedSize(true)
            recyclerview.layoutManager = linearLayoutManager
            recyclerview.adapter = inviteTabAdapter
            emptyLayout(false, false)
            setupObserver()

        }
        recyclerview.addItemDecoration(DividerItemDecoration(activity as MainActivity, DividerItemDecoration.VERTICAL), Color.RED)

        val sectionItemDecoration = RecyclerSectionItemDecoration(resources.getDimensionPixelSize(R.dimen._30sdp), true,
            getSectionCallback(contactList!!))
        recyclerview.addItemDecoration(sectionItemDecoration)
        alphaBetAdapter = AlphaBetAdapter(activity as MainActivity, object : AlphaBetAdapter.OnItemClick {
            override fun onClicklisneter(pos: Int, name: String) {

                val position = inviteTabAdapter!!.getPositionForSection(l!![pos].toInt())
                recyclerview!!.layoutManager!!.scrollToPosition(position)

            }
        }, alphabetListData)

        recyclerview_alphabet!!.layoutManager = linearLayoutManageralphabet
        recyclerview_alphabet!!.adapter = alphaBetAdapter


        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager.childCount
                totalItemCount = linearLayoutManager.itemCount
                firstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition()
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                        && firstVisibleItemPosition >= 0
                        && totalItemCount >= 20
                    ) {

                        isLoading = true
                        setupObserver()

                    }
                }
            }
        })
        btnRetry.setOnClickListener(this)
        search_sms.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                inviteTabAdapter!!.filter?.filter(p0.toString())
                recyclerview.stopScroll()
            }

        })
        btn_SelectAll.setOnClickListener (this)

    }

    private fun setupViewModel() {
        contactListModel = ViewModelProvider(this@InviteTabFragment).get(ContactListModel::class.java)
        try {
            sendContactsModel= ViewModelProvider(this@InviteTabFragment).get(UserContactSyncModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getSectionCallback(people: ArrayList<ContactListData?>?): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            override fun isSection(position: Int): Boolean {
                return position == 0 || people?.get(position)?.usercontactFirstName?.get(0) != people?.get(position - 1)?.usercontactFirstName?.get(0)
            }

            override fun getSectionHeader(position: Int): CharSequence? {
                return people?.get(position)?.usercontactFirstName?.subSequence(0, 1)

            }
        }

    }

    private fun showDialogMessage() {
        MyUtils.showMessageOK(
                mActivity!!,
                resources.getString(R.string.give_permission_ro_read_contacts),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        loadContacts()
                    }
                })
    }

    fun loadContacts() {
        val permissionCheck = ContextCompat.checkSelfPermission(mActivity!!, Manifest.permission.READ_CONTACTS)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            getContacts()
        } else {
            ActivityCompat.requestPermissions(
                    mActivity!!,
                    arrayOf(Manifest.permission.READ_CONTACTS),
                    PERMISSIONS_REQUEST_READ_CONTACTS
            )
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContacts()
            } else {

            }
        }
    }

    private fun getContacts() {

        emptyLayout(false, true)

        MyAsyncTask().execute()
    }

    private fun emptyLayout(yesrOrNo: Boolean, progress: Boolean) {

        if (progress) {
            ll_mainContactSync?.visibility = View.VISIBLE
            recyclerview?.visibility = View.GONE
            relativeprogressBar?.visibility = View.VISIBLE
        } else {
            if (!yesrOrNo) {
                relativeprogressBar?.visibility = View.GONE
                ll_mainContactSync?.visibility = View.VISIBLE
                recyclerview?.visibility = View.VISIBLE
            } else {
                ll_mainContactSync?.visibility = View.VISIBLE
                recyclerview?.visibility = View.GONE
                relativeprogressBar?.visibility = View.GONE
            }
        }
    }

    inner class MyAsyncTask : AsyncTask<Void?, Void?, Void?>() {

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (synceusers != null && synceusers?.length() != 0) {
                sendContactsToServer(synceusers!!)
            } else {
                pageNo = 0
                setupObserver()
            }

        }

        override fun onPreExecute() {
            super.onPreExecute()
            recyclerview?.visibility = View.GONE
            ll_mainContactSync?.visibility = View.VISIBLE
            uploadedCount = 0
        }

        override fun doInBackground(vararg arg0: Void?): Void? {
            try {
                createJsonArray()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }

    private fun sendContactsToServer(syncarray: JSONArray) {
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {
            jsonObject.put("loginuserID", userData!!.userID)
            jsonObject.put("synceusers", syncarray)
            jsonObject.put("apiType", RestClient.apiType)
            jsonObject.put("apiVersion", RestClient.apiVersion)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        jsonArray.put(jsonObject)
        Log.d("System out", "json req $jsonArray")
        sendContactsModel?.getContactSync(mActivity!!, false, jsonArray.toString(), "Contact_sync")?.observe(this@InviteTabFragment, object : Observer<List<UserContactSyncPojo?>?> {
            override fun onChanged(sendContactsToServerPojos: List<UserContactSyncPojo?>?) {
                emptyLayout(false, false)

                if (sendContactsToServerPojos != null && sendContactsToServerPojos.size > 0) {

                    if (sendContactsToServerPojos[0]!!.status.equals("true")) {
                        recyclerview.setVisibility(View.VISIBLE)
                        val result = totalcontacts / incrementedCount
                        totalServerCount++

                        if (uploadedCount <= totalcontacts) {
                            createJsonArray()
                        } else {
                        }
                        pageNo = 0
                        setupObserver()
                    } else {
                        MyUtils.showSnackbar(mActivity!!, sendContactsToServerPojos[0]!!.message, ll_mainContactSync)
                    }
                } else {
                    if (!MyUtils.isInternetAvailable(mActivity!!)) {
                        MyUtils.showSnackbar(mActivity!!, "" + mActivity!!.getResources().getString(R.string.error_common_network), ll_mainContactSync)
                    } else {
                        MyUtils.showSnackbar(mActivity!!, "" + mActivity!!.getResources().getString(R.string.error_crash_error_message), ll_mainContactSync)
                    }
                }
            }


        })
    }

    private fun createJsonArray() {


        if (contactSyncList.isNullOrEmpty()) {
            contactSyncList!!.clear()
            contactSyncList!!.addAll(ReadContacts.readContacts(mActivity!!))
        }

        totalcontacts = contactSyncList!!.size
        synceusers = JSONArray()

        try {
            if (contactList!!.size > 0) {
                for (i in contactSyncList!!.indices) {

                    for (j in contactList!!.indices) {
                        if (contactSyncList?.get(i)!!.phone.equals(contactList?.get(j)?.usercontactPhone)) {
                            contactSyncList!!.removeAt(i)
                        }
                    }
                }
            }
        } catch (e: IndexOutOfBoundsException) {
            e.printStackTrace()
        }
        if (totalcontacts <= incrementedCount) {


            var syncObj: JSONObject? = null

            for (i in contactSyncList!!.indices) {

                syncObj = JSONObject()

                try {
                    syncObj.put("usercontactLastName", contactSyncList?.get(i)?.contactLastname)
                    syncObj.put("usercontactPhone", contactSyncList?.get(i)?.phone!!.replace("'?'", ""))
                    syncObj.put("usercontactSyncedFrom", "Phone")
                    syncObj.put("usercontactEmail", contactSyncList?.get(i)?.emailaddress)
                    syncObj.put("usercontactFirstName", contactSyncList?.get(i)?.contactFirstname)
                    synceusers?.put(syncObj)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }


            sendContactsToServer(synceusers!!)

        } else {

            val iter = contactSyncList!!.iterator()
            while (iter.hasNext()) {
                if (uploadedCount <= totalcontacts) {

                    var syncObj: JSONObject? = null

                    for (k in uploadedCount until uploadedCount + incrementedCount) {

                        syncObj = JSONObject()

                        try {
                            syncObj.put("usercontactLastName", contactSyncList?.get(k)?.contactLastname)
                            syncObj.put("usercontactPhone", contactSyncList?.get(k)?.phone)
                            syncObj.put("usercontactSyncedFrom", "Phone")
                            syncObj.put("usercontactEmail", contactSyncList?.get(k)?.emailaddress)
                            syncObj.put("usercontactFirstName", contactSyncList?.get(k)?.contactFirstname)
                            synceusers?.put(syncObj)

                        } catch (e: IndexOutOfBoundsException) {
                            e.printStackTrace()
                        }
                        uploadedCount = uploadedCount + 1
                    }

                    sendContactsToServer(synceusers!!)
                } else {
                    break
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MyAsyncTask().cancel(true)
    }

    override fun onClick(v: View?) {
        when(v?.id)
        {
           R.id.btnRetry ->{
               pageNo = 0
               setupObserver()
           }
            R.id.btn_SelectAll ->{
                for (i in 0 until contactList!!.size) {

                    contactList!![i]!!.checked = !(contactList!![i]!!.checked)!!

                    if (contactList!![i]!!.checked!!) {
                        btn_SelectAll.setImageDrawable(resources.getDrawable(R.drawable.checkbox_selected))
                        inviteTabAdapter?.selectAllItem(true)
                        inviteListData!!.addAll(contactList!!)
                        (parentFragment as InviteMainFragment).inviteTabList(inviteListData!!)

                    } else {
                        btn_SelectAll.setImageDrawable(resources.getDrawable(R.drawable.checkbox_unselected))
                        inviteTabAdapter?.selectAllItem(false)
                        inviteListData!!.removeAll(contactList!!)
                        (parentFragment as InviteMainFragment).inviteTabList(inviteListData!!)
                    }
                    break
                }
                inviteTabAdapter?.notifyDataSetChanged()
           }

        }
    }
}

