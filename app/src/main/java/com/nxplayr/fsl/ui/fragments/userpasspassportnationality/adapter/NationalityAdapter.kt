package com.nxplayr.fsl.ui.fragments.userpasspassportnationality.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.CountryListData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.SessionManager
import kotlinx.android.synthetic.main.item_nationality_list.view.*


class NationalityAdapter(val context: Activity,
                         val countryList: ArrayList<CountryListData>?,
                         val onItemClick: OnItemClick
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var countryListFiltered: List<CountryListData>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(com.nxplayr.fsl.R.layout.item_nationality_list, parent, false)

        return NationalityHolder(v, context)
    }

    override fun getItemCount(): Int {
        return countryListFiltered!!.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is NationalityHolder) {
            var holder1 = holder as NationalityHolder
            holder1.bind(countryListFiltered!![position], holder1.adapterPosition, onItemClick)

            holder1.itemView.setOnClickListener {
                onItemClick.onClicled(holder1.adapterPosition, "")

//                Toast.makeText(context,countryList!!.get(position).countryName + " " + countryList!!.get(position).countryID , Toast.LENGTH_SHORT).show()
            }

        }
    }


    class NationalityHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        var sessionManager: SessionManager? = null
        var userData: SignupData? = null
        var select: Boolean = false
        var ll_expireDate=itemView.ll_expireDate
        init {
            sessionManager = SessionManager(context)
            userData = sessionManager!!.get_Authenticate_User()
            ll_expireDate.visibility=View.GONE
        }

        fun bind(countryList: CountryListData, position: Int, onItemClick: OnItemClick) = with(itemView) {

            tv_nationality.text = countryList.countryName

            if (countryList.checked) {
                btn_nationality.setImageDrawable(resources.getDrawable(R.drawable.checkbox_selected))
            } else {
                btn_nationality.setImageDrawable(resources.getDrawable(R.drawable.checkbox_unselected))
            }

        }

    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                countryListFiltered = if (charString.isEmpty()) {
                    countryList!!
                } else {
                    val filteredList: MutableList<CountryListData> = ArrayList<CountryListData>()
                    for (row in countryList!!) {

                        if (row.countryName.toLowerCase().contains(charString.toLowerCase()) || row.countryName.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    filteredList

                }
                val filterResults = FilterResults()
                filterResults.values = countryListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                countryListFiltered = filterResults.values as ArrayList<CountryListData>
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)


    }

    init {
        countryListFiltered = countryList!!
    }

}










