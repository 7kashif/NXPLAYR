package com.nxplayr.fsl.ui.fragments.invite.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.SectionIndexer
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.api.RestClient
import com.nxplayr.fsl.data.model.ContactListData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.ui.fragments.feed.viewholder.LoaderViewHolder
import kotlinx.android.synthetic.main.item_invite_adapter.view.*


class InviteTabAdapter(
    val context: Activity,
    val listData: ArrayList<ContactListData?>?,
    val onItemClick: OnItemClick,
    val tabPosition: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), SectionIndexer, Filterable {
    private var contactListFiltered: List<ContactListData?>?

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == MyUtils.Loder_TYPE) run {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.loader, parent, false)
            return LoaderViewHolder(view)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_invite_adapter, parent, false)
            return ConnectionHolder(v, context)
        }
    }

    override fun getItemCount(): Int {
        return contactListFiltered!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LoaderViewHolder) {

        } else if (holder is ConnectionHolder) {
            holder.bind(contactListFiltered!![position], holder.adapterPosition, onItemClick, tabPosition)
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (contactListFiltered!![position] == null) MyUtils.Loder_TYPE else MyUtils.TEXT_TYPE
    }

    class ConnectionHolder(itemView: View, context: Activity) : RecyclerView.ViewHolder(itemView) {

        fun bind(inviteList: ContactListData?, position: Int, onitemClick: OnItemClick, tabPosition: Int) =
                with(itemView) {

                    if (tabPosition == 1) {
                        if (!inviteList?.usercontactPhone.isNullOrEmpty()) {
                            ll_mainSMS.visibility = View.VISIBLE
                            tv_contactUserName.text = inviteList!!.usercontactFirstName /*+" "+ inviteList.usercontactLastName*/
                            tv_userContactNum.text = inviteList!!.usercontactPhone
                            inviteUser_Imageview.setImageURI(RestClient.image_base_url_users + inviteList.userProfilePicture)
                        } else {
                            ll_mainSMS.visibility = View.GONE

                        }
                    } else if (tabPosition == 2) {
                        if ((!inviteList?.usercontactEmail.isNullOrEmpty())) {
                            ll_mainSMS.visibility = View.VISIBLE
                            tv_contactUserName.text = inviteList!!.usercontactFirstName /*+" "+ inviteList.usercontactLastName*/
                            tv_userContactNum.text = inviteList!!.usercontactEmail
                            inviteUser_Imageview.setImageURI(RestClient.image_base_url_users + inviteList.userProfilePicture)
                        } else {
                            ll_mainSMS.visibility = View.GONE

                        }
                    }
                    if (inviteList?.checked!!) {
                        icon_invite_check.setImageDrawable(resources.getDrawable(R.drawable.checkbox_selected))
                    } else {
                        icon_invite_check.setImageDrawable(resources.getDrawable(R.drawable.checkbox_unselected))
                    }

                    icon_invite_check.setOnClickListener {
                        onitemClick.onClicklisneter(position, "invite_contacts", inviteList)
                    }

                }
    }


    fun selectAllItem(isSelectedAll: Boolean) {
        try {
            if (listData!! != null) {
                for (index in 0 until listData!!.size) {
                    listData!!.get(index)!!.checked = isSelectedAll
                }
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getSections(): Array<Any>? {
        return null
    }

    override fun getSectionForPosition(position: Int): Int {
        return 0
    }

    override fun getPositionForSection(sectionIndex: Int): Int {
        if (sectionIndex == 35) {
            return 0
        }
        for (i in 0 until contactListFiltered!!.size) {
            val l = contactListFiltered!![i]
            val firstChar = l.toString().get(0)
//            val firstChar = l.toUpperCase().get(0)
            if (firstChar.toInt() == sectionIndex) {
                return i
            }
        }
        return -1
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                contactListFiltered = if (charString.isEmpty()) {
                    listData!!
                } else {
                    val filteredList: MutableList<ContactListData?>? = ArrayList<ContactListData?>()
                    for (row in listData!!) {

                        if (row!!.usercontactFirstName.toLowerCase().contains(charString.toLowerCase()) || row!!.usercontactPhone.contains(charSequence)) {
                            filteredList?.add(row)
                        }
                    }
                    filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                contactListFiltered = filterResults.values as ArrayList<ContactListData?>?
                notifyDataSetChanged()
            }
        }
    }

    interface OnItemClick {
        fun onClicklisneter(pos: Int, from: String, inviteList: ContactListData?)
    }

    init {
        contactListFiltered = listData
    }
}