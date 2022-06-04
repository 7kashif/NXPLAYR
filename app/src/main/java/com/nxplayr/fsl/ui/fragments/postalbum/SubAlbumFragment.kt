package com.nxplayr.fsl.ui.fragments.postalbum

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonParseException
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.data.model.SubAlbum
import com.nxplayr.fsl.ui.activity.main.managers.NotifyAlbumInterface
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.ui.fragments.collection.adapter.CreateSubAlbumAdapter
import com.nxplayr.fsl.ui.fragments.collection.view.AssignPostAlbumFragment
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.*
import com.nxplayr.fsl.ui.fragments.explorepost.adapter.ExploreVideoListAdapater
import com.nxplayr.fsl.ui.fragments.explorepost.adapter.OldExploreVideoAdapater
import com.nxplayr.fsl.ui.fragments.explorepost.view.ExploreVideoDetailFragment
import com.nxplayr.fsl.ui.fragments.explorepost.viewmodel.ExploreVideoModelV2
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.nodafound.*
import kotlinx.android.synthetic.main.nointernetconnection.*
import kotlinx.android.synthetic.main.progressbar.*
import kotlinx.android.synthetic.main.sub_album_activity.*
import org.json.JSONArray
import org.json.JSONObject


class SubAlbumFragment : androidx.fragment.app.Fragment(), View.OnClickListener {

    private var v: View? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null

    var popup: RelativeLayout? = null
    var mActivity: AppCompatActivity? = null
    var b: AlertDialog? = null
    var swipeCount = 0
    var create_album_list: ArrayList<SubAlbum>? = null
    var create_album_adapter: CreateSubAlbumAdapter? = null
    var AlbumID: String? = null
    var AlbumName: String? = null
    var SubAlbumID: String? = null
    var SubAlbumName: String? = null
    var postId: String? = null
    var SubAlbumId: String? = null
    var SubAlbumPostion: Int? = null
    var globalView: String = ""
    var pageNo = 0
    var clicked = false
    var sharedPreferences: SharedPreferences? = null
    var explore_video_list: ArrayList<CreatePostData?>? = ArrayList()
    var oldExploreVideo_adapter: OldExploreVideoAdapater? = null
    var exploreVideoListadapter: ExploreVideoListAdapater? = null
    var Add = ""
    var explore_video_list1: ArrayList<CreatePostData?>? = ArrayList()

    private var linearLayoutManager: LinearLayoutManager? = null
    private var y: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var firstVisibleItemPosition: Int = 0
    private var isLoading = false
    private var isLastpage = false
    var postType = ""
    var notifyInterface: NotifyAlbumInterface? = null
    var OldSubAlbumID = ""
    var fromCome = ""
    var OldAlbumID = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (v == null) {
            v = inflater.inflate(R.layout.sub_album_activity, container, false)

        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
        try {
            notifyInterface = activity as NotifyAlbumInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(
                activity.toString()
                        + " must implement TextClicked"
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(mActivity!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        if (arguments != null) {

            AlbumID = arguments?.getString("albumId", "")!!
            AlbumName = arguments?.getString("albumName", "")!!
            postId = arguments?.getString("postId", "")!!
            SubAlbumPostion = arguments?.getInt("albumPosition", 0)!!
            globalView = arguments?.getString("subAlbum", "")!!
            Add = arguments?.getString("Add", "")!!
            when (Add) {
                "addToCollection" -> {
                    explore_video_list1 =
                        arguments?.getSerializable("explore_video_list") as ArrayList<CreatePostData?>?
                    postType = arguments?.getString("postType")!!
                }
            }

            if (!arguments?.getString("fromCome").isNullOrEmpty()) {
                fromCome = arguments?.getString("fromCome")!!
                if (fromCome.equals("MoveToOther")) {
                    OldSubAlbumID = arguments?.getString("SubAlbumID")!!
                    OldAlbumID = arguments?.getString("AlbumID")!!
                }
            }

        }
        txtSubAlbumName.text = AlbumName

        if (globalView.equals("SubAlbumShow")) {

            lylSelectedSubAlbum.visibility = View.VISIBLE
            lylSelectSubAlbum.visibility = View.GONE
            lylSubAlbumListShow.visibility = View.GONE
            rc_subalbum_list.visibility = View.GONE

        } else {

            lylSelectedSubAlbum.visibility = View.GONE
            lylSelectSubAlbum.visibility = View.GONE
            lylSubAlbumListShow.visibility = View.VISIBLE
            rc_subalbum_list.visibility = View.VISIBLE
        }

        getSubAblumList()

        img_back.setOnClickListener(this)
        txt_add_subAlbums.setOnClickListener(this)
        lylSelectSubAlbum.setOnClickListener(this)
        lylSelectedSubAlbum.setOnClickListener(this)
        btnRetry.setOnClickListener(this)

        when (fromCome) {
            "MoveToOther" -> {
                txtSelectName.text = "Move to " + AlbumName
            }
            else -> {
                txtSelectName.text = "Add to " + AlbumName
            }
        }

        getGridAssignPostAlbum()

        img_sublistView.setOnClickListener(this)
        img_sublistView2.setOnClickListener(this)
        img_subExpGridView.setOnClickListener(this)
        img_subExpGridView2.setOnClickListener(this)
    }

    //View Type
    private fun getSubAblumList() {
        linearLayoutManager = LinearLayoutManager(mActivity!!)

        if (create_album_list == null) {
            create_album_list = ArrayList()
            create_album_adapter =
                CreateSubAlbumAdapter(mActivity!!, object : CreateSubAlbumAdapter.OnItemClick {

                    override fun onDeleteClickListner(pos: Int) {

                        MyUtils.showMessageOKCancel(mActivity!!,
                            "Are you sure want to delete album ?",
                            "Album Delete ",
                            DialogInterface.OnClickListener { dialogInterface, i ->

                                SubAlbumId = create_album_list!![pos].exsubalbumID

                                create_album_list!!.removeAt(pos)
                                create_album_adapter!!.notifyDataSetChanged()

                                getDeleteSubAlbum(
                                    userData?.userID,
                                    AlbumID,
                                    SubAlbumId,
                                    RestClient.apiVersion,
                                    RestClient.apiType
                                )
                            })

                    }

                    override fun onSubAlbumClick(pos: Int) {
                        val bottomSheet = SubAlbumBottumSheetFragment()
                        bottomSheet.show(requireFragmentManager(), "SubAlbumBottumSheetFragment")
                        Bundle().apply {
                            putString("subExalbumID", create_album_list!!.get(pos)?.exsubalbumID)
                            putString("AlbumID", AlbumID)
                            putString("SubAlbumName", create_album_list!!.get(pos)?.exsubalbumName)
                            putString("AlbumName", AlbumName)
                            putString("postId", postId)
                            putString("globalView", globalView)
                            putInt("albumPosition", SubAlbumPostion!!)
                            bottomSheet.arguments = this
                        }
                        bottomSheet.setOnclickLisner(object :
                            SubAlbumBottumSheetFragment.BottomSheetListenerAlbum {
                            override fun onOptionClick(text: String) {
                                when (text) {
                                    "Edit" -> {
                                        bottomSheet.dismiss()
                                        pageNo = 0
                                        getCreatSubAlbumList()
                                    }
                                    "Delete" -> {
                                        bottomSheet.dismiss()
                                        pageNo = 0
                                        getCreatSubAlbumList()
                                    }
                                }
                            }

                        })
                    }

                    override fun onEditClickListner(pos: Int) {

                        SubAlbumName = create_album_list!!.get(pos).exsubalbumName
                        showEditAlbums()
                    }

                    override fun onClicklisneter(pos: Int) {

                        if (globalView.equals("SubAlbumShow")) {
                            txtSelectName.text = "Add to " + create_album_list!![pos].exsubalbumName
                            SubAlbumId = create_album_list!![pos].exsubalbumID
                            clicked = true
                        } else {

                            val assignPostAlbumFragment = AssignPostAlbumFragment()
                            Bundle().apply {
                                putString("albumId", AlbumID)
                                putString("albumName", AlbumName)
                                putString("subAlbumId", create_album_list!![pos].exsubalbumID)
                                putString("subAlbumName", create_album_list!![pos].exsubalbumName)
                                putString("postId", "0")
                                assignPostAlbumFragment.arguments = this
                            }
                            (activity as MainActivity).navigateTo(
                                assignPostAlbumFragment,
                                assignPostAlbumFragment::class.java.name,
                                true
                            )

                        }

                    }

                }, create_album_list!!, false, globalView)
            rc_subalbum_create.setHasFixedSize(true)
            rc_subalbum_create.layoutManager = linearLayoutManager
            rc_subalbum_create.adapter = create_album_adapter
            pageNo = 0
            getCreatSubAlbumList()
        }

    }

    private fun getGridAssignPostAlbum() {
        rc_subalbum_list.layoutManager = GridLayoutManager(mActivity!!, 2)
        if (explore_video_list.isNullOrEmpty()) {

            oldExploreVideo_adapter =
                OldExploreVideoAdapater(mActivity!!, object : OldExploreVideoAdapater.OnItemClick {
                    override fun onClicklisneter(pos: Int) {

                        val exploreDetailFragment = ExploreVideoDetailFragment()
                        Bundle().apply {
                            putString("user_ID", explore_video_list!!.get(pos)?.userID)
                            putInt("pos", pos)
                            putString("username", explore_video_list!!.get(pos)?.userFirstName)
                            putString("like", explore_video_list!!.get(pos)?.postLike)
                            putString("view", explore_video_list!!.get(pos)?.postViews)
                            putString(
                                "userProfile",
                                explore_video_list!!.get(pos)?.userProfilePicture
                            )
                            putString("postID", explore_video_list!!.get(pos)?.postID)
                            putString("postLike", explore_video_list!!.get(pos)?.youpostLiked)
                            putString("posthashtag", explore_video_list!!.get(pos)?.posthashtag)
                            putString(
                                "exploreVideo",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile
                            )
                            putString(
                                "exploreThumbnail",
                                explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail
                            )
                            putString(
                                "albumID",
                                explore_video_list!!.get(pos)?.postAlbum!![0].exalbumID
                            )
                            putString(
                                "subAlbumAId",
                                explore_video_list!!.get(pos)?.postAlbum!![0].exsubalbumID
                            )
                            putString("collection", "Remove")
                            putSerializable("explore_video_list", explore_video_list)
                            putString("postType", explore_video_list?.get(pos)?.postType)
                            exploreDetailFragment.arguments = this
                        }
                        (activity as MainActivity).navigateTo(
                            exploreDetailFragment,
                            exploreDetailFragment::class.java.name,
                            true
                        )

                    }

                }, explore_video_list!!, false)
        }
        rc_subalbum_list.setHasFixedSize(true)
        rc_subalbum_list.adapter = oldExploreVideo_adapter

        getAssignPostAlbumData(
            userData?.userID!!, pageNo, "Album", "", "", "", "", RestClient.apiVersion, "10",
            "", "", "", "", "0", "", "",
            "Video", "0", AlbumID!!, "0", RestClient.apiType
        )
    }

    private fun getViewListExploreData() {
        rc_subalbum_list.layoutManager =
            LinearLayoutManager(mActivity!!, LinearLayoutManager.VERTICAL, false)

        exploreVideoListadapter =
            ExploreVideoListAdapater(mActivity!!, object : ExploreVideoListAdapater.OnItemClick {
                override fun onClicklisneter(pos: Int) {
                    val exploreDetailFragment = ExploreVideoDetailFragment()
                    Bundle().apply {
                        putString("user_ID", explore_video_list!!.get(pos)?.userID)
                        putInt("pos", pos)
                        putString("username", explore_video_list!!.get(pos)?.userFirstName)
                        putString("like", explore_video_list!!.get(pos)?.postLike)
                        putString("view", explore_video_list!!.get(pos)?.postViews)
                        putString("postDescription", explore_video_list!!.get(pos)?.postDescription)
                        putString("userProfile", explore_video_list!!.get(pos)?.userProfilePicture)
                        putString("postID", explore_video_list!!.get(pos)?.postID)
                        putString("postLike", explore_video_list!!.get(pos)?.youpostLiked)
                        putString("posthashtag", explore_video_list!!.get(pos)?.posthashtag)
                        putString(
                            "exploreVideo",
                            explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaFile
                        )
                        putString(
                            "exploreThumbnail",
                            explore_video_list!!.get(pos)?.postSerializedData!![0].albummedia!![0].albummediaThumbnail
                        )
                        putString(
                            "albumID",
                            explore_video_list!!.get(pos)?.postAlbum!![0].exalbumID
                        )
                        putString(
                            "subAlbumAId",
                            explore_video_list!!.get(pos)?.postAlbum!![0].exsubalbumID
                        )
                        putString("collection", "Remove")
                        putSerializable("explore_video_list", explore_video_list)
                        putSerializable("postType", explore_video_list!![pos]!!.postType)
                        exploreDetailFragment.arguments = this
                    }
                    (activity as MainActivity).navigateTo(
                        exploreDetailFragment,
                        exploreDetailFragment::class.java.name,
                        true
                    )

                }

            }, explore_video_list!!, false)

        rc_subalbum_list.setHasFixedSize(true)
        rc_subalbum_list.adapter = exploreVideoListadapter
    }

    //Click Handle
    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.img_back -> {
                (activity as MainActivity).onBackPressed()
            }

            R.id.txt_add_subAlbums -> {

                getAlbumCreate()
            }

            R.id.lylSelectSubAlbum -> {

                Toast.makeText(mActivity!!, "Please Select Any Sub-Album", Toast.LENGTH_SHORT)
                    .show()

            }

            R.id.lylSelectedSubAlbum -> {

                if (clicked == true) {

                    getAssignPostAlbum(
                        userData?.userID,
                        SubAlbumId!!,
                        AlbumID,
                        postId,
                        RestClient.apiVersion,
                        RestClient.apiType
                    )
                } else {

                    getAssignPostAlbum(
                        userData?.userID,
                        "0",
                        AlbumID,
                        postId,
                        RestClient.apiVersion,
                        RestClient.apiType
                    )
                }

            }

            R.id.btnRetry -> {

                getSubAblumList()
            }

            R.id.img_sublistView -> {

                img_sublistView2.visibility = View.VISIBLE
                img_subExpGridView2.visibility = View.VISIBLE
                img_subExpGridView.visibility = View.GONE
                img_sublistView.visibility = View.GONE

                if (explore_video_list.isNullOrEmpty()) {

                    getAssignPostAlbumData(
                        userData?.userID!!,
                        pageNo,
                        "Album",
                        "",
                        "",
                        "",
                        "",
                        RestClient.apiVersion,
                        "10",
                        "",
                        "",
                        "",
                        "",
                        "0",
                        "",
                        "",
                        "Video",
                        "0",
                        AlbumID!!,
                        "0",
                        RestClient.apiType
                    )

                } else {

                    getViewListExploreData()
                }
            }
            R.id.img_subExpGridView2 -> {

                img_sublistView2.visibility = View.GONE
                img_subExpGridView2.visibility = View.GONE
                img_subExpGridView.visibility = View.VISIBLE
                img_sublistView.visibility = View.VISIBLE

                if (explore_video_list.isNullOrEmpty()) {

                    getAssignPostAlbumData(
                        userData?.userID!!,
                        pageNo,
                        "Album",
                        "",
                        "",
                        "",
                        "",
                        RestClient.apiVersion,
                        "10",
                        "",
                        "",
                        "",
                        "",
                        "0",
                        "",
                        "",
                        "Video",
                        "0",
                        AlbumID!!,
                        "0",
                        RestClient.apiType
                    )

                } else {

                    getGridAssignPostAlbum()
                }
            }
        }

    }

    //Popup
    private fun getAlbumCreate() {
        val dialogBuilder = AlertDialog.Builder(context)
        val inflater = (context as AppCompatActivity).layoutInflater

        @SuppressLint("InflateParams")
        val rv = inflater.inflate(R.layout.custom_dialog_add_ablums_collection, null)
        dialogBuilder.setView(rv)
        b = dialogBuilder.create()
        val txt_cancel = rv.findViewById(R.id.txt_cancel) as TextView
        val txt_album = rv.findViewById(R.id.txt_album) as TextView
        val txt_okay = rv.findViewById(R.id.txt_okay) as TextView
        popup = rv.findViewById(R.id.popup) as RelativeLayout
        val edtAlbumName = rv.findViewById(R.id.edtAlbumName) as EditText
        txt_album.text = "Enter SubAlbum Name "

        txt_okay.setOnClickListener {

            val subAlbumName = edtAlbumName.text.toString().trim()

            if (subAlbumName.equals("")) {

                Toast.makeText(requireContext(), "Please Enter SubAlbum Name", Toast.LENGTH_SHORT).show()

            } else {

                createSubAlbumName(
                    userData?.userID,
                    subAlbumName,
                    AlbumID,
                    RestClient.apiType,
                    RestClient.apiVersion
                )
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
        val dialogBuilder = AlertDialog.Builder(requireContext())
        val inflater = (context as AppCompatActivity).layoutInflater

        @SuppressLint("InflateParams")
        val rv = inflater.inflate(R.layout.custom_dialog_add_ablums_collection, null)
        dialogBuilder.setView(rv)
        b = dialogBuilder.create()
        val txt_cancel = rv.findViewById(R.id.txt_cancel) as TextView
        val txt_okay = rv.findViewById(R.id.txt_okay) as TextView
        popup = rv.findViewById(R.id.popup) as RelativeLayout
        val edtAlbumName = rv.findViewById(R.id.edtAlbumName) as EditText

        edtAlbumName.setText(SubAlbumName)

        txt_okay.setOnClickListener {

            SubAlbumName = edtAlbumName.text.toString().trim()

            if (SubAlbumName.equals("")) {

                Toast.makeText(context, "Please Enter SubAlbum Name", Toast.LENGTH_SHORT).show()
            } else {

                editAlbumName(
                    userData?.userID,
                    SubAlbumName!!,
                    AlbumID,
                    SubAlbumId,
                    RestClient.apiType,
                    RestClient.apiVersion
                )
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

    //API Funcation
    private fun getCreatSubAlbumList() {
        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userData?.userID)
            jsonObject.put("page", pageNo)
            jsonObject.put("pagesize", "10")
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("apiType", RestClient.apiType)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("SUBALBUM_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)
        var getEmployementModel =
            ViewModelProvider(this@SubAlbumFragment).get(CollectionViewModel::class.java)
        getEmployementModel.createAlbumList(jsonArray.toString())
        getEmployementModel.createAlbumList
            .observe(viewLifecycleOwner,
                Observer { albumListpojo ->

                    relativeprogressBar.visibility = View.GONE

                    if (albumListpojo != null && albumListpojo.isNotEmpty()) {

                        if (albumListpojo[0].status.equals("true", true)) {

                            create_album_list?.clear()
                            create_album_list?.addAll(albumListpojo[0].data.get(SubAlbumPostion!!).subAlbums)
                            create_album_adapter?.notifyDataSetChanged()

                        } else {

                            if (create_album_list!!.size == 0) {

                                ll_no_data_found.visibility = View.VISIBLE

                            } else {

                                ll_no_data_found.visibility = View.GONE
                            }

                        }

                    } else {

                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }

                })
    }

    private fun createSubAlbumName(
        useriD: String?,
        subalbumName: String,
        albumID: String?,
        apiType: String,
        apiVersion: String
    ) {
        relativeprogressBar.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {
            jsonObject.put("loginuserID", useriD)
            jsonObject.put("exsubalbumName", subalbumName)
            jsonObject.put("exalbumID", albumID)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        Log.d("CreatSubAlbumObject", jsonObject.toString())
        var getSubAlbumModel =
            ViewModelProviders.of(this@SubAlbumFragment).get(CreateSubAlbumModel::class.java)
        getSubAlbumModel.apiSubCreateAlbum(mActivity!!, false, jsonArray.toString())
            .observe(mActivity!!,
                Observer { subAlbumCreatepojo ->

                    relativeprogressBar.visibility = View.GONE
                    if (subAlbumCreatepojo != null && subAlbumCreatepojo.isNotEmpty()) {

                        if (subAlbumCreatepojo[0].status.equals("true", true)) {

                            Toast.makeText(
                                context,
                                subAlbumCreatepojo[0].message,
                                Toast.LENGTH_SHORT
                            ).show()
                            b!!.dismiss()
                            pageNo = 0
                            getCreatSubAlbumList()
                        } else {

                            Toast.makeText(
                                context,
                                subAlbumCreatepojo[0].message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {
                        relativeprogressBar.visibility = View.GONE
                    }
                })
    }

    private fun editAlbumName(
        userID: String?,
        albumName: String,
        albumID: String?,
        subAlbumID: String?,
        apiType: String,
        apiVersion: String
    ) {

        relativeprogressBar.visibility = View.VISIBLE
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {

            jsonObject.put("loginuserID", userID)
            jsonObject.put("exsubalbumName", albumName)
            jsonObject.put("exalbumID", albumID)
            jsonObject.put("exsubalbumID", subAlbumID)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        Log.d("EditSubAlbumObject", jsonObject.toString())
        var geteditSubAlbumModel =
            ViewModelProviders.of(this@SubAlbumFragment).get(EditSubAlbumModel::class.java)
        geteditSubAlbumModel.apiSubEditAlbumDelete(mActivity!!, false, jsonArray.toString())
            .observe(mActivity!!,
                Observer { albumEditpojo ->

                    relativeprogressBar.visibility = View.GONE
                    if (albumEditpojo != null && albumEditpojo.isNotEmpty()) {
                        MyUtils.dismissProgressDialog()
                        if (albumEditpojo[0].status.equals("true", true)) {

                            Toast.makeText(requireContext(), albumEditpojo[0].message, Toast.LENGTH_SHORT)
                                .show()
                            b!!.dismiss()
                            pageNo = 0
                            getCreatSubAlbumList()
                        } else {

                            Toast.makeText(requireContext(), albumEditpojo[0].message, Toast.LENGTH_SHORT)
                                .show()
                        }

                    } else {

                        relativeprogressBar.visibility = View.GONE

                    }

                })
    }

    private fun getDeleteSubAlbum(
        userID: String?,
        albumID: String?,
        exsubalbumID: String?,
        apiVersion: String,
        apiType: String
    ) {

        relativeprogressBar.visibility = View.VISIBLE
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userID)
            jsonObject.put("exalbumID", albumID)
            jsonObject.put("exsubalbumID", exsubalbumID)
            jsonObject.put("apiVersion", apiVersion)
            jsonObject.put("apiType", apiType)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("SUB_ALBUM_DELETE_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)
        var getDeleteSubAlbumModel =
            ViewModelProviders.of(this@SubAlbumFragment).get(DeleteSubAlbumModel::class.java)
        getDeleteSubAlbumModel.apiDeleteSubAlbum(mActivity!!, false, jsonArray.toString())
            .observe(mActivity!!,
                Observer { albumDeletepojo ->

                    relativeprogressBar.visibility = View.GONE

                    if (albumDeletepojo != null && albumDeletepojo.isNotEmpty()) {

                        if (albumDeletepojo[0].status.equals("true", true)) {

                            if (create_album_list!!.size == 0) {

                                ll_no_data_found.visibility = View.VISIBLE
                                Toast.makeText(
                                    requireContext(),
                                    albumDeletepojo[0].message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                create_album_adapter!!.notifyDataSetChanged()

                            } else {

                                ll_no_data_found.visibility = View.GONE
                            }

                        } else {

                            Toast.makeText(
                                requireContext(),
                                albumDeletepojo[0].message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {

                        relativeprogressBar.visibility = View.GONE
                        ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }

                })
    }

    private fun getAssignPostAlbum(
        userID: String?,
        exsubalbumID: String,
        albumID: String?,
        postId: String?,
        apiVersion: String,
        apiType: String
    ) {

        relativeprogressBar.visibility = View.VISIBLE
        ll_no_data_found.visibility = View.GONE
        nointernetMainRelativelayout.visibility = View.GONE

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {
            jsonObject.put("loginuserID", userID)
            if (OldAlbumID.isNullOrEmpty()) {
                jsonObject.put("exalbumID", albumID)
            } else {
                jsonObject.put("exalbumID", OldAlbumID)
                jsonObject.put("newexalbumID", albumID)
            }
            if (OldSubAlbumID.isNullOrEmpty()) {
                jsonObject.put("exsubalbumID", exsubalbumID)
            } else {
                jsonObject.put("exsubalbumID", OldSubAlbumID)
                jsonObject.put("newexsubalbumID", exsubalbumID)
            }

            jsonObject.put("postID", postId)
            jsonObject.put("apiVersion", RestClient.apiVersion)
            jsonObject.put("apiType", RestClient.apiType)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("ASSING_POST", jsonObject.toString())
        jsonArray.put(jsonObject)
        var getAssignAlbumModel =
            ViewModelProviders.of(this@SubAlbumFragment).get(CollectionViewModel::class.java)
        getAssignAlbumModel.assignPostAlbum(jsonArray.toString())
        getAssignAlbumModel.assignPostAlbumCreated
            .observe(viewLifecycleOwner,
                Observer { assignalbumpojo ->

                    relativeprogressBar.visibility = View.GONE
                    if (assignalbumpojo != null && assignalbumpojo.isNotEmpty()) {

                        if (assignalbumpojo[0].status.equals("true", true)) {
                            if (notifyInterface != null) {

                                notifyInterface?.AlbumNotifyData(
                                    postId!!,
                                    userID!!,
                                    "AddSubAlbum",
                                    explore_video_list1,
                                    postType
                                )
                            }

                            Toast.makeText(
                                requireContext(),
                                assignalbumpojo[0].message,
                                Toast.LENGTH_SHORT
                            ).show()
                            sharedPreferences = mActivity!!.getSharedPreferences(
                                MyUtils.GlobarViewSubData,
                                Context.MODE_PRIVATE
                            )
                            val editor = sharedPreferences!!.edit()
                            editor.putString(MyUtils.CollecationData, "ShowData")
                            editor.apply()
                            editor.commit()
//                                    finish()

                        } else {

                            Toast.makeText(
                                requireContext(),
                                assignalbumpojo[0].message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    } else {

                        relativeprogressBar.visibility = View.GONE

                    }

                })
    }

    private fun getAssignPostAlbumData(
        userid: String,
        page: Int,
        tabname: String,
        sortby: String,
        publicationTime: String,
        tag: String,
        gender: String,
        apiVersion: String,
        pagesize: String,
        footballType: String,
        footballLevel: String,
        pitchPosition: String,
        footballagecatID: String,
        exsubalbumID: String,
        countryID: String,
        postType: String,
        postMediaType: String,
        loginuserID: String?,
        exalbumID:
        String,
        postID: String,
        apiType: String
    ) {

        relativeprogressBar.visibility = View.VISIBLE

        if (pageNo == 0) {

            relativeprogressBar.visibility = View.VISIBLE
            explore_video_list?.clear()
            oldExploreVideo_adapter?.notifyDataSetChanged()

        } else {

            relativeprogressBar.visibility = View.GONE
            rc_subalbum_list.visibility = View.VISIBLE
            explore_video_list!!.add(null)
            oldExploreVideo_adapter?.notifyItemInserted(explore_video_list!!.size - 1)
        }

        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("userID", userid)
            jsonObject.put("page", page)
            jsonObject.put("tabname", tabname)
            jsonObject.put("sortby", sortby)
            jsonObject.put("publicationTime", publicationTime)
            jsonObject.put("tag", tag)
            jsonObject.put("gender", gender)
            jsonObject.put("apiVersion", apiVersion)
            jsonObject.put("pagesize", pagesize)
            jsonObject.put("footballType", footballType)
            jsonObject.put("footballLevel", footballLevel)
            jsonObject.put("pitchPosition", pitchPosition)
            jsonObject.put("footballagecatID", footballagecatID)
            jsonObject.put("exsubalbumID", exsubalbumID)
            jsonObject.put("countryID", countryID)
            jsonObject.put("postType", postType)
            jsonObject.put("postMediaType", postMediaType)
            jsonObject.put("loginuserID", loginuserID)
            jsonObject.put("exalbumID", exalbumID)
            jsonObject.put("postID", postID)
            jsonObject.put("apiType", apiType)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("SubAssignPost_List", jsonObject.toString())
        jsonArray.put(jsonObject)
        var getAssignPostAlbumModel =
            ViewModelProviders.of(this@SubAlbumFragment).get(ExploreVideoModelV2::class.java)
        getAssignPostAlbumModel.getVideos(jsonArray.toString())
        getAssignPostAlbumModel.exploreSuccessLiveData
            .observe(mActivity!!,
                Observer { exploreVidelistpojo ->

                    if (exploreVidelistpojo != null && exploreVidelistpojo.isNotEmpty()) {

                        isLoading = false
                        relativeprogressBar.visibility = View.GONE

                        if (pageNo > 0) {

                            explore_video_list?.removeAt(explore_video_list!!.size - 1)
                            oldExploreVideo_adapter?.notifyItemRemoved(explore_video_list!!.size)
                        }

                        if (exploreVidelistpojo[0].status.equals("true", true)) {

                            rc_subalbum_list.visibility = View.VISIBLE
                            if (pageNo == 0) {
                                explore_video_list?.clear()
                            }

                            explore_video_list?.addAll(exploreVidelistpojo[0].data!!)
                            oldExploreVideo_adapter?.notifyDataSetChanged()
                            exploreVideoListadapter?.notifyDataSetChanged()
                            pageNo += 1
                            if (exploreVidelistpojo[0].data!!.size < 10) {
                                isLastpage = true
                            }
                            if (explore_video_list.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.GONE
                                rc_subalbum_list.visibility = View.GONE
                                lylSubAlbumListShow.visibility = View.GONE
                            } else {

                                rc_subalbum_list.visibility = View.VISIBLE
                                lylSubAlbumListShow.visibility = View.VISIBLE
                            }

                        } else {

                            if (explore_video_list.isNullOrEmpty()) {
                                ll_no_data_found.visibility = View.GONE
                                rc_subalbum_list.visibility = View.GONE
                                lylSubAlbumListShow.visibility = View.GONE
                            } else {
                                rc_subalbum_list.visibility = View.VISIBLE
                                lylSubAlbumListShow.visibility = View.VISIBLE
                            }

                        }

                    } else {

                        relativeprogressBar.visibility = View.GONE
//                                ErrorUtil.errorView(mActivity!!, nointernetMainRelativelayout)
                    }
                })
    }

    override fun onDestroyView() {
        if (v != null) {
            val viewGroup = v?.parent as ViewGroup?
            viewGroup?.removeView(v);
        }
        super.onDestroyView()
    }

}