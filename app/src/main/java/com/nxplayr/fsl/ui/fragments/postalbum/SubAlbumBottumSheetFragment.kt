package com.nxplayr.fsl.ui.fragments.postalbum

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.collection.adapter.CreateAlbumAdapater
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.DeleteSubAlbumModel
import com.nxplayr.fsl.ui.fragments.collection.viewmodel.EditSubAlbumModel
import com.nxplayr.fsl.data.model.AlbumDatum
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.ErrorUtil
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.JsonParseException
import kotlinx.android.synthetic.main.explore_album_option_sheet.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class SubAlbumBottumSheetFragment: BottomSheetDialogFragment(), View.OnClickListener {

    private var mBottomSheetListener: BottomSheetListenerAlbum? = null
    var sessionManager: SessionManager? = null
    var userData: SignupData? = null

    var popup: RelativeLayout? = null
    var mActivity: AppCompatActivity? = null
    var b: AlertDialog? = null

    var create_album_list: ArrayList<AlbumDatum?>? = ArrayList()
    var create_album_adapter: CreateAlbumAdapater? = null
    var SubExalbumID = ""
    var AlbumID = ""
    var SubAlbumName = ""
    var AlbumName : String? = null
    var postId : String? = null
    var globalView : String? = null
    var albumPosition: Int? = null
    var onClick: BottomSheetListenerAlbum?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme1);
    }

    fun setOnclickLisner(mListener: BottomSheetListenerAlbum) {
        this.onClick = mListener
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.explore_album_option_sheet, container, false)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        sessionManager = SessionManager(context!!)
        if (sessionManager?.get_Authenticate_User() != null) {
            userData = sessionManager?.get_Authenticate_User()
        }

        if (arguments != null) {

            SubExalbumID = arguments?.getString("subExalbumID", "")!!
            AlbumID = arguments?.getString("AlbumID", "")!!
            SubAlbumName = arguments?.getString("SubAlbumName", "")!!

            AlbumName = arguments?.getString("AlbumName", "")!!
            postId = arguments?.getString("postId", "")!!
            globalView = arguments?.getString("globalView", "")!!
            albumPosition = arguments?.getInt("albumPosition", 0)!!
        }

        lyl_album_edit.setOnClickListener(this)
        lyl_album_delete.setOnClickListener(this)
    }

    interface BottomSheetListenerAlbum {
        fun onOptionClick(text: String)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.lyl_album_edit -> {

                showEditAlbums("Edit",lyl_album_edit)
            }

            R.id.lyl_album_delete -> {
                MyUtils.showMessageOKCancel(context!!,
                        "Are you sure want to delete sub album ?",
                        "Album Delete ",
                        DialogInterface.OnClickListener { dialogInterface, i ->

                            getDeleteSubAlbum(userData?.userID, AlbumID, SubExalbumID, RestClient.apiVersion, RestClient.apiType,"Delete",lyl_album_delete)
                        })
            }
        }
    }

    private fun getDeleteSubAlbum(userID: String?, albumID: String, subExalbumID: String, apiVersion: String, apiType: String, s: String, lyl_album_delete: LinearLayout) {
        MyUtils.showProgressDialog(context!! as Activity,"Please wait..")
        val jsonArray = JSONArray()
        val jsonObject = JSONObject()

        try {

            jsonObject.put("loginuserID", userID)
            jsonObject.put("exalbumID", albumID)
            jsonObject.put("exsubalbumID", subExalbumID)
            jsonObject.put("apiVersion", apiVersion)
            jsonObject.put("apiType", apiType)

        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("SUB_ALBUM_DELETE_LIST", jsonObject.toString())
        jsonArray.put(jsonObject)
        var getDeleteSubAlbumModel =
                ViewModelProviders.of(this@SubAlbumBottumSheetFragment).get(DeleteSubAlbumModel::class.java)
        getDeleteSubAlbumModel.apiDeleteSubAlbum((context as Activity?)!!, false, jsonArray.toString())
                .observe(this@SubAlbumBottumSheetFragment,
                        Observer { albumDeletepojo ->

                            if (albumDeletepojo != null && albumDeletepojo.isNotEmpty()) {
                                MyUtils.dismissProgressDialog()
                                if (albumDeletepojo[0].status.equals("true", true))
                                {

                                        Toast.makeText(context, albumDeletepojo[0].message, Toast.LENGTH_SHORT).show()
                                        onClick?.onOptionClick(s)
//                                        putString("albumId", AlbumID!!)
                                        dismiss()


                                }
                                else
                                {

                                    Toast.makeText(context, albumDeletepojo[0].message, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                MyUtils.dismissProgressDialog()
                                ErrorUtil.errorMethod(lyl_album_delete)
                            }

                        })
    }

    private fun showEditAlbums(s: String, lyl_album_edit: LinearLayout) {
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

        edtAlbumName.setText(SubAlbumName)

        txt_okay.setOnClickListener {

            SubAlbumName = edtAlbumName!!.text.toString().trim()

            if (SubAlbumName.equals("")) {

                Toast.makeText(context, "Please Enter SubAlbum Name", Toast.LENGTH_SHORT).show()
            } else {

                editAlbumName(userData?.userID, SubAlbumName!!, AlbumID, SubExalbumID, RestClient.apiType, RestClient.apiVersion,s,lyl_album_edit)
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

    private fun editAlbumName(userID: String?, subAlbumName: String, albumID: String, subExalbumID: String, apiType: String, apiVersion: String, s: String, lyl_album_edit: LinearLayout) {
        MyUtils.showProgressDialog(context!! as Activity,"Please wait..")
        val jsonObject = JSONObject()
        val jsonArray = JSONArray()

        try {

            jsonObject.put("loginuserID", userID)
            jsonObject.put("exsubalbumName", subAlbumName)
            jsonObject.put("exalbumID", albumID)
            jsonObject.put("exsubalbumID", subExalbumID)
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
                ViewModelProviders.of(this@SubAlbumBottumSheetFragment).get(EditSubAlbumModel::class.java)
        geteditSubAlbumModel.apiSubEditAlbumDelete((context as Activity?)!!, false, jsonArray.toString())
                .observe(this@SubAlbumBottumSheetFragment,
                        Observer { albumEditpojo ->

                            if (albumEditpojo != null && albumEditpojo.isNotEmpty()) {
                                MyUtils.dismissProgressDialog()
                                if (albumEditpojo[0].status.equals("true", true)) {

                                 //   Toast.makeText(context, albumEditpojo[0].message, Toast.LENGTH_SHORT).show()
                                    onClick?.onOptionClick(s)
//                                        putString("albumId", AlbumID!!)
                                    b!!.dismiss()

                                } else {

                                    Toast.makeText(context, albumEditpojo[0].message, Toast.LENGTH_SHORT).show()
                                    b!!.dismiss()
                                }

                            } else {
                                MyUtils.dismissProgressDialog()
                                ErrorUtil.errorMethod(lyl_album_edit)

                            }

                        })
    }

}