package com.nxplayr.fsl.ui.fragments.setting.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.LanguageListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_language_select.view.*


class LanguageAdapter(val context: Activity, val list_language: ArrayList<LanguageListData>?,
                      val onItemClick: OnItemClick,
                      var languageId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(com.nxplayr.fsl.R.layout.item_language_select, parent, false)

        return LanguageHolder(v, context)
    }

    override fun getItemCount(): Int {

        return list_language!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LanguageHolder) {

//            holder.bind(list_language!![position], holder.adapterPosition, onItemClick)

            var holder1 = holder as LanguageHolder
            holder.bind(list_language!![position], holder.adapterPosition, onItemClick, languageId)

            holder1.radiobtn_language.setOnClickListener {
                onItemClick.onClicled(holder1.adapterPosition, "")
                languageId = list_language[position].languageID
                notifyDataSetChanged()
            }

        }

    }


    class LanguageHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        var sessionManager: SessionManager? = null
        var userData: SignupData? = null
        var radiobtn_language = itemView.findViewById(R.id.radiobtn_language) as ImageView

        init {
            sessionManager = SessionManager(context)
            userData = sessionManager?.get_Authenticate_User()
        }

        fun bind(list_language: LanguageListData, adapterPosition: Int, onItemClick: OnItemClick, languageId: String) = with(itemView) {


            tv_language.text = list_language.languageName
            language_listImageview.setImageURI(RestClient.image_base_url_flag + list_language.languageFlag)

            if ((list_language.status)) {
                radiobtn_language.setImageDrawable(resources.getDrawable(R.drawable.radio_btn_selected))
            } else {
                radiobtn_language.setImageDrawable(resources.getDrawable(R.drawable.radio_btn_unselected))
            }

            if ((userData?.languageID.equals(list_language.languageID, false))) {

                if (languageId.equals(list_language.languageID, false)) {

                    radiobtn_language.setImageDrawable(resources.getDrawable(R.drawable.radio_btn_selected))
                } else {
                    radiobtn_language.setImageDrawable(resources.getDrawable(R.drawable.radio_btn_unselected))
                }
            }


            radiobtn_language.setOnClickListener {
                onItemClick.onClicled(adapterPosition, "")
            }

        }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }
}




