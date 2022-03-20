package com.nxplayr.fsl.ui.fragments.userlanguage.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.ProfileLanguageData
import kotlinx.android.synthetic.main.item_languages_adapter.view.*

class LanguagesAdapter(
    val context: Activity,
    val listData: ArrayList<ProfileLanguageData?>?
    , val onItemClick: OnItemClick, val from: String
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_languages_adapter, parent, false)
        return LanguageViewHolder(v)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is LanguageViewHolder) {
            holder.bind(listData!![position]!!, holder.adapterPosition, onItemClick, from)

        }


    }

    class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(languagelistData: ProfileLanguageData,
                 adapterPosition: Int,
                 onItemClick: OnItemClick,
                 from: String) = with(itemView) {

            if (from.equals("profile")) {
                if (adapterPosition == 0 || adapterPosition == 1) {
                    tv_language.text = languagelistData.languageName
                    tv_proficiency.text = languagelistData.profiencyName
                    delete_language.visibility = View.GONE
                } else {
                    ll_languages.visibility = View.GONE
                }
            } else if (from.equals("Language")) {
                tv_language.text = languagelistData.languageName
                tv_proficiency.text = languagelistData.profiencyName
                delete_language.visibility = View.VISIBLE

            }

            delete_language.setOnClickListener {

                onItemClick.onClicled(adapterPosition, "deleteLan", delete_language)

            }

        }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String, view: View)
    }

}
