package com.nxplayr.fsl.ui.fragments.userpreferreoutfitter.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.OutfittersPojoData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_outfitters_list.view.*
import java.util.*


class PreferreOutfittersAdapter(
    val context: Activity,
    val listData: ArrayList<OutfittersPojoData?>?, val onItemClick: OnItemClick, val from: String
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var sessionManager: SessionManager = SessionManager(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_outfitters_list, parent, false)
        return LanguageViewHolder(context, v)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LanguageViewHolder) {
            var holder1 = holder as LanguageViewHolder
            holder1.bind(context, listData!![position]!!, holder1.adapterPosition, onItemClick)
            holder1.itemView.setOnClickListener {
                onItemClick.onClicled(holder1.adapterPosition, "")
            }
        }
    }

    class LanguageViewHolder(context: Activity, itemView: View) : RecyclerView.ViewHolder(itemView) {
        var sessionManager: SessionManager? = null
        var userData: SignupData? = null
        var select: Boolean = false

        init {
            sessionManager = SessionManager(context)
            userData = sessionManager!!.get_Authenticate_User()
        }
        fun bind(context: Activity, countryList: OutfittersPojoData, position: Int, onItemClick: OnItemClick) = with(itemView) {

            tv_nationality.text = countryList.outfitterName
            if (countryList.checked) {
                btn_nationality.setImageDrawable(resources.getDrawable(R.drawable.checkbox_experience_selected))
            } else {
                btn_nationality.setImageDrawable(resources.getDrawable(R.drawable.checkbox_experience_unselected))
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }


}
