package com.nxplayr.fsl.ui.fragments.bottomsheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nxplayr.fsl.R
import com.nxplayr.fsl.util.MyUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.choose_image_bottomsheet.*


class SelectPhotoBottomSheetFragment : BottomSheetDialogFragment(),View.OnClickListener {


       var mActivity: Activity?=null
       var Typeof: String? = ""
       private var onItemSelectedListener: OnItemSelectedListener? = null
       var v: View?=null



    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        mActivity = activity as Activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            Typeof = arguments!!.getString("Typeof")
        }
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        v = inflater.inflate(R.layout.choose_image_bottomsheet, container)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        txtGallery.setOnClickListener(this)
        llGallery.setOnClickListener(this)
        ll_cancel.setOnClickListener(this)
        txtGlobal.setOnClickListener(this)
        llCameraPhoto.setOnClickListener(this)
        close_btn.setOnClickListener(this)
    }


    fun setListener(listener: OnItemSelectedListener) {
        this.onItemSelectedListener = listener
    }

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.ll_cancel ->{
                dismiss()
                MyUtils.finishActivity(this.mActivity!!,true)
            }
            R.id.close_btn ->{
                dismiss()
                MyUtils.finishActivity(this.mActivity!!,true)
            }
            R.id.txtGallery -> {
                txtGallery.setTextColor(mActivity!!.resources.getColor(R.color.colorAccent))
                if (onItemSelectedListener != null) {

                    onItemSelectedListener!!.onselectoption("Gallery")
                    dismiss()
                }
            }
            R.id.llGallery -> {
                txtGallery.setTextColor(mActivity!!.resources.getColor(R.color.colorAccent))
                if (onItemSelectedListener != null) {

                    onItemSelectedListener!!.onselectoption("Gallery")
                    dismiss()
                }
            }
            R.id.txtGlobal -> {
                txtGlobal.setTextColor(mActivity!!.resources.getColor(R.color.colorAccent))

                if (onItemSelectedListener != null) {

                    onItemSelectedListener!!.onselectoption("CameraPhoto")
                    dismiss()
                }
            }
            R.id.llCameraPhoto -> {
                txtGlobal.setTextColor(mActivity!!.resources.getColor(R.color.colorAccent))

                if (onItemSelectedListener != null) {

                    onItemSelectedListener!!.onselectoption("CameraPhoto")
                    dismiss()
                }
            }

        }
    }


    interface OnItemSelectedListener {
        fun onselectoption(SelectName: String)

    }

}
