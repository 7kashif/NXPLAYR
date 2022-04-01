package com.nxplayr.fsl.ui.fragments.setting.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.LanguageListData
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_language_select.view.*


class LanguageAdapterWithoutUser(
    val context: Activity, val list_language: ArrayList<LanguageListData>?,
    val onItemClick: OnItemClick,
    var languageId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(com.nxplayr.fsl.R.layout.item_language_select_dialog, parent, false)
        return LanguageHolder(v, context)
    }

    override fun getItemCount(): Int {
        return list_language!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LanguageHolder) {
            var holder1 = holder as LanguageHolder
            holder.bind(list_language!![position], position, onItemClick, languageId)

            holder1.languageMain.setOnClickListener {
                onItemClick.onClicled(position, "")
                languageId = list_language[position].languageID
                notifyDataSetChanged()
            }
        }
    }

    class LanguageHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {
        var sessionManager: SessionManager? = null
        var languageMain = itemView.findViewById(R.id.languageMain) as LinearLayout

        init {
            sessionManager = SessionManager(context)
        }

        fun bind(
            list_language: LanguageListData,
            adapterPosition: Int,
            onItemClick: OnItemClick,
            languageId: String
        ) = with(itemView) {

            tv_language.text = list_language.languageName
            language_listImageview.setImageURI(RestClient.image_base_url_flag + list_language.languageFlag)

            if ((list_language.status)) {
                radiobtn_language.setImageResource(R.drawable.radio_btn_selected)
            } else {
                radiobtn_language.setImageResource(R.drawable.radio_btn_unselected)
            }

            if (languageId.equals(list_language.languageID, false)) {
                radiobtn_language.setImageResource(R.drawable.radio_btn_selected)
            } else {
                radiobtn_language.setImageResource(R.drawable.radio_btn_unselected)
            }

        }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }
}




