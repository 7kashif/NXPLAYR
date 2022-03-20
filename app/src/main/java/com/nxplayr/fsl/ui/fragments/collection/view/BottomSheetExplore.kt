package com.nxplayr.fsl.ui.fragments.collection.view

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nxplayr.fsl.ui.activity.main.view.MainActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.DeletePostCollecationModel
import com.nxplayr.fsl.ui.fragments.explorepost.viewmodel.RemoveCollectionModel
import com.nxplayr.fsl.data.model.CreatePostData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonParseException
import kotlinx.android.synthetic.main.explore_video_option_sheet.*
import kotlinx.android.synthetic.main.explore_video_option_sheet.view.*
import kotlinx.android.synthetic.main.progressbar.*
import org.json.JSONArray
import org.json.JSONObject


class BottomSheetExplore : BottomSheetDialogFragment(), View.OnClickListener {

    var sessionManager: SessionManager? = null
    var userData: SignupData? = null
    var postId = ""
    var Collection = ""
    var AlbumID = ""
    var SubAlbumID = ""
    var user_ID = ""
    var postType = ""
    var ReomveCollections = ""
    var sharedPreferences: SharedPreferences? = null
    var onClick: BottomSheetListener?=null
    var v:View?=null
    var explore_video_list:ArrayList<CreatePostData?>? = java.util.ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme1);
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if(v==null)
        {
             v = inflater.inflate(R.layout.explore_video_option_sheet, container, false)
        }

        return v
    }

    interface BottomSheetListener {
        fun onOptionClick(text: String)
    }

    fun setOnclickLisner(mListener: BottomSheetListener) {
        this.onClick = mListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sharedPreferences = context!!.getSharedPreferences(MyUtils.GlobarViewSubData, Context.MODE_PRIVATE)
        sessionManager = SessionManager(context!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()

        }
        if (arguments != null)
        {
            postId = arguments?.getString("postId", "")!!
            user_ID = arguments?.getString("user_ID", "")!!
            Collection = arguments?.getString("collection", "")!!
            AlbumID = arguments?.getString("albumID", "")!!
            SubAlbumID = arguments?.getString("subAlbumAId", "")!!
            ReomveCollections = arguments?.getString("ReomveCollection", "")!!
            postType = arguments?.getString("postType", "")!!
            explore_video_list = (arguments?.getSerializable("explore_video_list") as ArrayList<CreatePostData?>?)!!
        }

        lyl_collection.setOnClickListener(this)
        lyl_delete.setOnClickListener(this)
        lyl_Move_to_Videos.setOnClickListener(this)
        lyl_edit.setOnClickListener(this)
        lyl_Move_to_collection.setOnClickListener(this)
        lyl_remove.setOnClickListener{
            getRemoveCollecation(userData?.userID, SubAlbumID, AlbumID, postId,lyl_remove)

        }
        if (userData!!.userID.equals(user_ID,false))
        {
                if(!MyUtils.CollecationData.equals("Add",false))
                {
                    if (Collection.equals("Add")) {
                        lyl_collection.visibility = View.VISIBLE
                        lyl_delete.visibility = View.VISIBLE
                        lyl_remove.visibility = View.GONE
                        lyl_Move_to_collection.visibility = View.GONE
                        lyl_Move_to_Videos.visibility=View.VISIBLE
                        lyl_edit.visibility=View.VISIBLE
                    } else if (Collection.equals("Remove")) {
                        lyl_collection.visibility = View.GONE
                        lyl_delete.visibility = View.VISIBLE
                        lyl_remove.visibility = View.VISIBLE
                        lyl_Move_to_collection.visibility = View.VISIBLE
                        lyl_Move_to_Videos.visibility=View.VISIBLE
                        lyl_edit.visibility=View.VISIBLE

                    }

                }else{
                    lyl_edit.visibility=View.VISIBLE
                    lyl_collection.visibility = View.GONE
                    lyl_delete.visibility = View.VISIBLE
                    lyl_remove.visibility = View.VISIBLE
                    lyl_Move_to_Videos.visibility=View.VISIBLE
                    lyl_Move_to_collection.visibility = View.GONE

                }

            when(postType)
            {
                "ExploreVideos"->{
                    tvMove_to.text="Move to Tricks"
                }
                "ExploreTricks"->{
                    tvMove_to.text="Move to Videos"

                }
            }
        }
        else
        {
            if(!MyUtils.CollecationData.equals("Add",false)) {
                if (Collection.equals("Add")) {
                    lyl_collection.visibility = View.VISIBLE
                    lyl_delete.visibility = View.GONE
                    lyl_remove.visibility = View.GONE
                    lyl_edit.visibility=View.GONE
                    lyl_Move_to_collection.visibility = View.GONE
                } else if (Collection.equals("Remove")) {
                    lyl_collection.visibility = View.GONE
                    lyl_delete.visibility = View.GONE
                    lyl_remove.visibility = View.VISIBLE
                    lyl_edit.visibility=View.GONE
                    lyl_Move_to_collection.visibility = View.VISIBLE
                }
                lyl_Move_to_Videos.visibility=View.GONE

            }else{
                lyl_edit.visibility=View.GONE
                lyl_collection.visibility = View.GONE
                lyl_delete.visibility = View.GONE
                lyl_remove.visibility = View.VISIBLE
                lyl_Move_to_Videos.visibility=View.GONE
                lyl_Move_to_collection.visibility = View.GONE


            }
        }
    }

    //handle clicks
    override fun onClick(v: View?) {
        when (v?.id!!) {
            R.id.lyl_collection -> {

                val i_add_collection = AddToCollectionFragment()
                Bundle().apply {
                    this.putString("Add", "addToCollection")
                    this.putString("postId", postId)
                    this.putString("bootmsheet", "HideData")
                    this.putString("postType", postType)
                    this.putSerializable("explore_video_list", explore_video_list)

                    i_add_collection.arguments=this
                }
                (activity as MainActivity).navigateTo(i_add_collection,i_add_collection::class.java.name,true)
                dismiss()
            }
            R.id.lyl_Move_to_collection -> {

                val i_add_collection = AddToCollectionFragment()
                Bundle().apply {
                    this.putString("Add", "addToCollection")
                    this.putString("postId", postId)
                    this.putString("fromCome", "MoveToOther")
                    this.putString("bootmsheet", "HideData")
                    this.putString("postType", postType)
                    this.putSerializable("explore_video_list", explore_video_list)
                    this.putString("SubAlbumID", SubAlbumID)
                    this.putString("AlbumID", AlbumID)
                    i_add_collection.arguments=this
                }
                (activity as MainActivity).navigateTo(i_add_collection,i_add_collection::class.java.name,true)
                dismiss()
            }
            R.id.lyl_delete -> {
                getDeletePost(userData!!.userID, postId, RestClient.apiType, RestClient.apiVersion)
            }
            R.id.lyl_Move_to_Videos -> {
                onClick?.onOptionClick("MoveTo")

            }
            R.id.lyl_edit -> {
                onClick?.onOptionClick("EditPost")

            }
        }
    }

    private fun getDeletePost(userID: String, postId: String, apiType: String, apiVersion: String) {

        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {
            jsonObject.put("loginuserID", userID)
            jsonObject.put("postID", postId)
            jsonObject.put("apiType", apiType)
            jsonObject.put("apiVersion", apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        Log.d("DeleteObject", jsonObject.toString())
        var getDeleteCollectionModel =
                ViewModelProviders.of(this@BottomSheetExplore).get(DeletePostCollecationModel::class.java)
        getDeleteCollectionModel.getDeleteCollecation(context!!, false, jsonArray.toString())
                .observe(this@BottomSheetExplore,
                        Observer { deletePostpojo ->

                            if (deletePostpojo != null && deletePostpojo.isNotEmpty()) {

                                if (deletePostpojo[0].status.equals("true", true)) {

                                    Toast.makeText(context!!, deletePostpojo[0].message, Toast.LENGTH_SHORT).show()
                                    dismiss()
                                } else {

                                    Toast.makeText(context!!, deletePostpojo[0].message, Toast.LENGTH_SHORT).show()
                                    dismiss()
                                }

                            } else {

                            }

                        })
    }

    private fun getRemoveCollecation(userID: String?, subAlbumID: String, albumID: String, postId: String, lyl_remove: LinearLayout) {
        MyUtils.showProgressDialog(context!! as Activity,"Please Wait..")
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {
            jsonObject.put("loginuserID", userID)
            jsonObject.put("exsubalbumID", subAlbumID)
            jsonObject.put("exalbumID", albumID)
            jsonObject.put("postID", postId)
            jsonObject.put("apiType",  RestClient.apiType)
            jsonObject.put("apiVersion",  RestClient.apiVersion)
            jsonArray.put(jsonObject)

        } catch (e: Exception) {
            e.printStackTrace()
        } catch (e: JsonParseException) {
            e.printStackTrace()
        }
        Log.d("RemoveObject", jsonObject.toString())
        var getRemoveCollectionModel =
                ViewModelProviders.of(this@BottomSheetExplore).get(RemoveCollectionModel::class.java)
        getRemoveCollectionModel.getRemoveCollectionData(context!!, false, jsonArray.toString())
                .observe(viewLifecycleOwner,
                        Observer { subAlbumCreatepojo ->

                            if (subAlbumCreatepojo != null && subAlbumCreatepojo.isNotEmpty()) {
                                MyUtils.dismissProgressDialog()

                                if (subAlbumCreatepojo[0].status.equals("true", true)) {

                                    Toast.makeText(context!!, subAlbumCreatepojo[0].message, Toast.LENGTH_SHORT).show()
                                    sharedPreferences = context!!.getSharedPreferences(MyUtils.GlobarViewSubData, Context.MODE_PRIVATE)
                                    sharedPreferences!!.edit().remove(MyUtils.CollecationData).commit();
                                    if (userData!!.userID.equals(user_ID,false)) {
                                        lyl_collection.visibility = View.VISIBLE
                                        lyl_delete.visibility = View.VISIBLE
                                        lyl_remove.visibility = View.GONE
                                    }else{
                                        lyl_collection.visibility = View.VISIBLE
                                        lyl_delete.visibility = View.GONE
                                        lyl_remove.visibility = View.GONE
                                    }
                                   onClick?.onOptionClick("RemoveAPi")
                                   dismiss()
                                } else {

                                    Toast.makeText(context!!, subAlbumCreatepojo[0].message, Toast.LENGTH_SHORT).show()
                                    dismiss()
                                }

                            }

                            else {
                                MyUtils.dismissProgressDialog()
                                ErrorUtil.errorMethod(lyl_remove)

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