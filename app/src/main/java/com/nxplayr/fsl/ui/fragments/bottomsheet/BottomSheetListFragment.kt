package com.nxplayr.fsl.ui.fragments.bottomsheet

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.fragment_bottom_sheet.*
import kotlinx.android.synthetic.main.list_item_layout.view.*


class BottomSheetListFragment : BottomSheetDialogFragment() {

    var mListener: SelectLanguage? = null

    var bottomSheetListAdapter: BottomSheetListAdapter? = null
    var mContext: Activity? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var list = ArrayList<String>()
    var from: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
    }

    fun setOnclickLisner(mListener: SelectLanguage) {
        this.mListener = mListener
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments != null) {
            list = arguments!!.get("data") as ArrayList<String>
            from = arguments!!.getString("from")!!
        }

        linearLayoutManager = LinearLayoutManager(mContext)
        bottomSheetListAdapter = BottomSheetListAdapter(
            mContext!!,
            from,
            list,
            object : BottomSheetListAdapter.OnItemClick {
                override fun onClicled(value: String, from: String) {
                    mListener!!.onLanguageSelect(value, from)
                    dismiss()
                }

            })
        rv_bottomSheet.layoutManager = linearLayoutManager
        rv_bottomSheet.adapter = bottomSheetListAdapter
        rv_bottomSheet.addItemDecoration(
            DividerItemDecoration(
                mContext,
                DividerItemDecoration.VERTICAL
            )
        )

        bottomSheetListAdapter?.notifyDataSetChanged()

        if (!from.isNullOrEmpty()) {
            if (from === "CompanyName") {
                search_job_title.hint = mContext!!.getString(R.string.company_name)
            } else if (from === "JobFunName") {
                search_job_title.hint = mContext!!.getString(R.string.job_title)
            } else if (from === "UniversityName") {
                search_job_title.hint = mContext!!.getString(R.string.schoolcollege)
            } else if (from === "DegreeName") {
                search_job_title.hint = mContext!!.getString(R.string.degree)
            } else {
                header.visibility = View.GONE
            }
        } else {
            header.visibility = View.GONE
        }

        back.setOnClickListener { dismiss() }
        search_job_title.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                bottomSheetListAdapter!!.filter.filter(s.toString())
                bottomSheetListAdapter?.notifyDataSetChanged()
            }
        })
    }

    interface SelectLanguage {
        fun onLanguageSelect(value: String, from: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as Activity
        val parent = parentFragment
        mListener = if (parent != null) {
            parent as SelectLanguage
        } else {
            mContext as SelectLanguage
        }
    }


    class BottomSheetListAdapter(
        internal var mContext: Activity,
        from: String,
        data: ArrayList<String>,
        val onItemClick: OnItemClick
    ) :
        RecyclerView.Adapter<BottomSheetListAdapter.ViewHolder>(), Filterable {
        var listDataFiltered: ArrayList<String> = java.util.ArrayList()
        var data: ArrayList<String> = java.util.ArrayList()
        var from: String = ""

        init {
            this.data = data
            this.listDataFiltered = data
            this.from = from
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(charSequence: CharSequence): FilterResults {
                    val charString = charSequence.toString()
                    listDataFiltered = if (charString.isEmpty()) {
                        data
                    } else {
                        val filteredList: ArrayList<String> = ArrayList<String>()
                        for (row in data) {
                            if (row.toLowerCase()
                                    .contains(charString.toLowerCase()) || row.contains(
                                    charSequence
                                )
                            ) {
                                filteredList.add(row)
                            }
                        }
                        filteredList
                    }
                    if (listDataFiltered.size == 0) {
                        listDataFiltered.add(charString)
                    }
                    val filterResults = FilterResults()
                    filterResults.values = listDataFiltered
                    return filterResults
                }

                override fun publishResults(
                    charSequence: CharSequence,
                    filterResults: FilterResults
                ) {
                    listDataFiltered = filterResults.values as ArrayList<String>
                    notifyDataSetChanged()
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(listDataFiltered[position]!!)

            holder.itemView.setOnClickListener {
                if (onItemClick != null)
                    onItemClick.onClicled(listDataFiltered[position]!!, from)
            }

        }

        override fun getItemCount(): Int {
            return listDataFiltered.size
        }

        interface OnItemClick {
            fun onClicled(value: String, from: String)
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(name: String) = with(itemView) {
                itemNameTv.text = name.toString().capitalize()
            }
        }
    }

}
