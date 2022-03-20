package com.nxplayr.fsl.ui.fragments.usergeographical.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.GeomobilitysData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_signup_select_football_type_adapter.view.*
import java.util.*


class GeographicalAdapter(
    val context: Activity,
    val listData: ArrayList<GeomobilitysData?>?, val onItemClick: OnItemClick, val from: String
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var sessionManager: SessionManager = SessionManager(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_signup_select_football_type_adapter, parent, false)
        return LanguageViewHolder(context, v)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LanguageViewHolder) {
            var holder1 = holder as LanguageViewHolder
            holder1.bind(context, listData!![position]!!, holder1.adapterPosition, onItemClick)
        }
    }

    class LanguageViewHolder(context: Activity, itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sessionManager: SessionManager? = null
        var userData: SignupData? = null
        var select: Boolean = false
        var colorId:Int=-1

        init {
            sessionManager = SessionManager(context)
            userData = sessionManager!!.get_Authenticate_User()
        }
        fun bind(context: Activity, countryList: GeomobilitysData, position: Int, onItemClick: OnItemClick) = with(itemView) {

            tv_football_type.text = countryList.geomobilityName
            if (countryList.checked!!) {

                when (position) {
                    0 -> {
                        colorId = R.color.colorPrimary
                        img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.international_black))
                    }
                    1 -> {
                        colorId = R.color.colorPrimary
                        img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.national_black))
                    }
                    2 -> {
                        colorId = R.color.colorPrimary
                        img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.regional_black))
                    }
                }
                MyUtils.setSelectedModeTypeViewColor(context, arrayListOf(tv_football_type), colorId!!)

            } else {

                when (position) {
                    0 -> {
                        colorId = R.color.colorPrimary
                        img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.international_cyan))

                    }
                    1 -> {
                        colorId = R.color.colorPrimary

                        img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.national_cyan))
                    }
                    2 -> {
                        colorId = R.color.colorPrimary
                        img_selectMode.setImageDrawable(resources.getDrawable(R.drawable.regional_cyan))
                    }
                }
                tv_football_type.setTextColor(context.resources.getColor(colorId))
            }

            ll_user_type.setOnClickListener {
                onItemClick.onClicled(position, "selectModeType")
            }



        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }
}
