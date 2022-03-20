package com.nxplayr.fsl.ui.fragments.collection.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.CreateAlbumDeleteModel
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.CreateAlbumModel
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.CteateAlbumListModel
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.EditAlbumModel
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.gson.JsonParseException
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.AlbumDatum
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.collection.adapter.CreateAlbumAdapater
import com.nxplayr.fsl.ui.fragments.postalbum.SubAlbumFragment
import kotlinx.android.synthetic.main.fragment_collection.*
import kotlinx.android.synthetic.main.fragment_explore_main.*
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class CollectionsFragment : androidx.fragment.app.Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null

    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    var pageNo = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false

    var popup: RelativeLayout? = null
    var mActivity: AppCompatActivity? = null
    var b: AlertDialog? = null
    var swipeCount = 0

    var create_album_list: ArrayList<AlbumDatum?>? = ArrayList()
    var create_album_adapter: CreateAlbumAdapater? = null
    var AlbumID: String? = null
    var AlbumName: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_collection, container, false)

        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(context!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        txt_add_albums.setOnClickListener {

            showAddAlbums()
        }

        getAblumList()

        btnRetry.setOnClickListener { getAblumList() }
        swipeAlbumlayout.setOnRefreshListener(this)

        rc_album_create.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                y = dy
                visibleItemCount = linearLayoutManager!!.childCount
                totalItemCount = linearLayoutManager!!.itemCount
                firstVisibleItemPosition = linearLayoutManager!!.findFirstVisibleItemPosition()
                if (!isLoading && !isLastpage) {
                    if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 10
                    ) {

                        isLoading = true

                        getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
                    }
                }
            }
        })
    }

    private fun getAblumList() {

        linearLayoutManager = LinearLayoutManager(mActivity)
        if (create_album_list.isNullOrEmpty() || pageNo == 0) {

            create_album_adapter = CreateAlbumAdapater(mActivity!!, create_album_list, object : CreateAlbumAdapater.OnItemClick {

                override fun onSubAlbum(posistion: Int) {

                    val subFragment = SubAlbumFragment()
                    Bundle().apply {
                        putString("albumName", create_album_list!!.get(posistion)?.exalbumName)
                        putInt("albumPosition", posistion)
                        putString("albumId", create_album_list!!.get(posistion)?.exalbumID)
                        putString("subAlbum", "SubAlbumHide")
                        subFragment.arguments = this
                    }
                    (activity as MainActivity).navigateTo(subFragment,subFragment::class.java.name,true)


                }

                override fun onAlbumOption(pos: Int) {
                    val bottomSheet = AlbumBottumSheetFragment()
                    bottomSheet.show(getFragmentManager()!!, "AlbumBottumSheetFragment")
                    Bundle().apply {
                        putString("exalbumID", create_album_list!!.get(pos)?.exalbumID)
                        putString("AlbumID", create_album_list!!.get(pos)?.exalbumID)
                        putString("AlbumName", create_album_list!!.get(pos)?.exalbumName)
                        bottomSheet.arguments = this
                    }
                    bottomSheet.setOnclickLisner(object :
                        AlbumBottumSheetFragment.BottomSheetListenerAlbum {
                        override fun onOptionClick(text: String) {
                            when(text){
                                "Edit"->{
                                    bottomSheet.dismiss()
                                    pageNo=0
                                    getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
                                }
                                "Delete"->{
                                    bottomSheet.dismiss()
                                    pageNo=0
                                    getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
                                }
                            }
                        }
                    })
                }

                override fun onEditAlbum(pos: Int) {

                    AlbumID = create_album_list!!.get(pos)?.exalbumID
                    AlbumName = create_album_list!!.get(pos)?.exalbumName

                    showEditAlbums()

                }

                override fun onClicklisneter(pos: Int) {

                    MyUtils.showMessageOKCancel(mActivity!!,
                            "Are you sure want to delete album ?",
                            "Album Delete ",
                            DialogInterface.OnClickListener { dialogInterface, i ->

                                var exalbumID = create_album_list!!.get(pos)?.exalbumID

                                create_album_list!!.removeAt(pos);
                                create_album_adapter!!.notifyDataSetChanged();
                                getCreateAblumDelete(userData?.userID, exalbumID, RestClient.apiVersion, RestClient.apiType)
                            })

                }

            }, false, MyUtils.GlobarViewData)
        }

        rc_album_create.layoutManager = linearLayoutManager
        rc_album_create.setHasFixedSize(true)
        rc_album_create.adapter = create_album_adapter

        getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
    }

    override fun onRefresh() {
        swipeCount += 1;
        if (swipeCount > 0) {

            create_album_adapter!!.notifyDataSetChanged()
            swipeAlbumlayout.isRefreshing = false
            pageNo = 0
            getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)

        }
    }

    //Popup Funcation
    private fun showAddAlbums() {

        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = (context as AppCompatActivity).getLayoutInflater()
        @SuppressLint("InflateParams")
        val rv = inflater.inflate(R.layout.custom_dialog_add_ablums_collection, null)
        dialogBuilder.setView(rv)
        b = dialogBuilder.create()
        val txt_cancel = rv.findViewById(R.id.txt_cancel) as TextView
        val txt_okay = rv.findViewById(R.id.txt_okay) as TextView
        popup = rv.findViewById(R.id.popup) as RelativeLayout
        val edtAlbumName = rv.findViewById(R.id.edtAlbumName) as EditText

        txt_okay.setOnClickListener {

            val albumName = edtAlbumName!!.text.toString().trim()

            if (albumName.equals("")) {

                Toast.makeText(context!!, "Please Enter Album Name", Toast.LENGTH_SHORT).show()
            } else {

                createAlbumName(userData?.userID, albumName, RestClient.apiType, RestClient.apiVersion)
            }

        }

        txt_cancel.setOnClickListener {
            b!!.dismiss()
        }

        dialogBuilder.setTitle("")
        dialogBuilder.setMessage("")
        b!!.setCanceledOnTouchOutside(false)
        b!!.setCancelable(false)
        b!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        b!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        b!!.show()

    }

    private fun showEditAlbums() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = (context as AppCompatActivity).getLayoutInflater()
        @SuppressLint("InflateParams")
        val rv = inflater.inflate(R.layout.custom_dialog_add_ablums_collection, null)
        dialogBuilder.setView(rv)
        b = dialogBuilder.create()
        val txt_cancel = rv.findViewById(R.id.txt_cancel) as TextView
        val txt_okay = rv.findViewById(R.id.txt_okay) as TextView
        popup = rv.findViewById(R.id.popup) as RelativeLayout
        val edtAlbumName = rv.findViewById(R.id.edtAlbumName) as EditText
        edtAlbumName.setText(AlbumName)

        txt_okay.setOnClickListener {

            AlbumName = edtAlbumName!!.text.toString().trim()

            if (AlbumName.equals("")) {

                Toast.makeText(context!!, "Please Enter SubAlbum Name", Toast.LENGTH_SHORT).show()
            } else {

                editAlbumName(userData?.userID, AlbumID, AlbumName, RestClient.apiType, RestClient.apiVersion)
            }

        }

        txt_cancel.setOnClickListener {
            b!!.dismiss()
        }

        dialogBuilder.setTitle("")
        dialogBuilder.setMessage("")
        b!!.setCanceledOnTouchOutside(false)
        b!!.setCancelable(false)
        b!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        b!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        b!!.show()
    }

    //Api Calling Funcation
    private fun createAlbumName(useriD: String?, albumName: String, apiType: String, apiVersion: String) {
        relativeprogressBar.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {
            jsonObject.put("loginuserID", useriD)
            jsonObject.put("exalbumName", albumName)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        Log.d("CreatAlbumObject", jsonObject.toString())
        var getEmployementModel =
                ViewModelProviders.of(this@CollectionsFragment).get(CreateAlbumModel::class.java)
        getEmployementModel.apiCreateAlbum(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                        Observer { albumCreatepojo ->

                            relativeprogressBar.visibility = View.GONE

                            if (albumCreatepojo != null && albumCreatepojo.isNotEmpty()) {

                                if (albumCreatepojo[0].status.equals("true", true)) {

                                    Toast.makeText(context, albumCreatepojo[0].message, Toast.LENGTH_SHORT).show()
                                    b!!.dismiss()
                                    pageNo = 0
                                    getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
                                } else {

                                    Toast.makeText(context, albumCreatepojo[0].message, Toast.LENGTH_SHORT).show()
                                }

                            } else {

                                relativeprogressBar.visibility = View.GONE

                            }

                        })
    }

    private fun getCreatAlbumList(userID: String?, pageno: Int?, pagesize: String?, apiType: String?, apiVersion: String?, searchKeyword: String="") {

        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        if (pageNo == 0) {

            relativeprogressBar.visibility = View.VISIBLE
            create_album_list?.clear()
            create_album_adapter?.notifyDataSetChanged()

        } else {

            relativeprogressBar.visibility = View.GONE
            rc_album_create.visibility = View.VISIBLE
            create_album_list!!.add(null)
            create_album_adapter?.notifyItemInserted(create_album_list!!.size - 1)
        }
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userID)
            jsonObject.put("page", pageno)
            jsonObject.put("searchKeyword", searchKeyword)

            jsonObject.put("pagesize", pagesize)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.d("album_jsonObject", jsonObject.toString())
        jsonArray.put(jsonObject)
        val albumlistModel = ViewModelProvider(this@CollectionsFragment).get(CteateAlbumListModel::class.java)
        albumlistModel.getAlbumDataList(mActivity!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                        Observer { albumpojo ->
                            if (albumpojo != null && albumpojo.isNotEmpty()) {

                                isLoading = false
                                ll_no_data_found.visibility = View.GONE
                                nointernetMainRelativelayout.visibility = View.GONE
                                relativeprogressBar.visibility = View.GONE

                                if (pageNo > 0) {

                                    create_album_list?.removeAt(create_album_list!!.size - 1)
                                    create_album_adapter?.notifyItemRemoved(create_album_list!!.size)
                                }

                                if (albumpojo[0].status.equals("true", true)) {

                                    rc_album_create.visibility = View.VISIBLE
                                    if (pageNo == 0) {
                                        create_album_list?.clear()
                                    }
                                    create_album_list!!.addAll(albumpojo[0].data)
                                    create_album_adapter?.notifyDataSetChanged()
                                    pageNo += 1
                                    if (albumpojo[0].data.size < 10) {
                                        isLastpage = true
                                    }
                                    if (!albumpojo[0].data!!.isNullOrEmpty()) {
                                        if (albumpojo[0].data!!.isNullOrEmpty()) {
                                            ll_no_data_found.visibility = View.VISIBLE
                                            rc_album_create.visibility = View.GONE
                                        } else {
                                            ll_no_data_found.visibility = View.GONE
                                            rc_album_create.visibility = View.VISIBLE
                                        }


                                    } else {
                                        ll_no_data_found.visibility = View.VISIBLE
                                        rc_album_create.visibility = View.GONE
                                    }

                                } else {
                                    relativeprogressBar.visibility = View.GONE

                                    if (create_album_list!!.isNullOrEmpty()) {
                                        ll_no_data_found.visibility = View.VISIBLE
                                        rc_album_create.visibility = View.GONE

                                    } else {
                                        ll_no_data_found.visibility = View.GONE
                                        rc_album_create.visibility = View.VISIBLE

                                    }
                                }

                            } else {

                                relativeprogressBar.visibility = View.GONE
                                if (activity != null) {
                                    ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)

                                }
                            }
                        })
    }

    private fun getCreateAblumDelete(userID: String?, exalbumID: String?, apiVersion: String, apiType: String) {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userID)
            jsonObject.put("exalbumID", exalbumID)
            jsonObject.put("apiVersion", apiVersion)
            jsonObject.put("apiType", apiType)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("ALBUM_DELETE_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)
        var getCreateAlbumDeleteModel = ViewModelProvider(this@CollectionsFragment).get(CreateAlbumDeleteModel::class.java)
        getCreateAlbumDeleteModel.apiCreateAlbumDelete((context as Activity), false, jsonArray.toString())
                .observe(this@CollectionsFragment!!,
                        Observer { albumDeletepojo ->

                            relativeprogressBar.visibility = View.GONE

                            if (albumDeletepojo != null && albumDeletepojo.isNotEmpty()) {

                                if (albumDeletepojo[0].status.equals("true", true)) {

                                    if (create_album_list!!.size == 0) {

                                        ll_no_data_found.visibility = View.VISIBLE
                                        Toast.makeText(context, albumDeletepojo[0].message, Toast.LENGTH_SHORT).show()
                                        create_album_adapter!!.notifyDataSetChanged();

                                    } else {

                                        ll_no_data_found.visibility = View.GONE
                                    }


                                } else {


                                    Toast.makeText(context, albumDeletepojo[0].message, Toast.LENGTH_SHORT).show()
                                }

                            } else {

                                relativeprogressBar.visibility = View.GONE
                                ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                            }

                        })
    }

    private fun editAlbumName(useriD: String?, albumID: String?, albumName: String?, apiType: String, apiVersion: String) {

        relativeprogressBar.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {
            jsonObject.put("loginuserID", useriD)
            jsonObject.put("exalbumID", albumID)
            jsonObject.put("exalbumName", albumName)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        Log.d("EditAlbumObject", jsonObject.toString())
        var geteditModel = ViewModelProvider(this@CollectionsFragment).get(EditAlbumModel::class.java)
        geteditModel.apiEditAlbumDelete(mActivity!!, false, jsonArray.toString())
                .observe(this@CollectionsFragment,
                        Observer { albumEditpojo ->

                            relativeprogressBar.visibility = View.GONE
                            if (albumEditpojo != null && albumEditpojo.isNotEmpty()) {
                                MyUtils.dismissProgressDialog()
                                if (albumEditpojo[0].status.equals("true", true)) {

                                    Toast.makeText(context, albumEditpojo[0].message, Toast.LENGTH_SHORT).show()
                                    b!!.dismiss()
                                    pageNo = 0
                                    getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion)
                                } else {

                                    Toast.makeText(context, albumEditpojo[0].message, Toast.LENGTH_SHORT).show()
                                }

                            } else {

                                relativeprogressBar.visibility = View.GONE

                            }

                        })
    }

    fun applySearch(searchKeyword: String) {
        pageNo = 0
        isLastpage = false
        isLoading = false
        getCreatAlbumList(userData?.userID, pageNo, "10", RestClient.apiType, RestClient.apiVersion,searchKeyword)

    }
}