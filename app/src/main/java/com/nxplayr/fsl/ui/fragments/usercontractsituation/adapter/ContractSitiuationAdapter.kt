package com.nxplayr.fsl.ui.fragments.usercontractsituation.adapter

import android.app.Activity
import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.ContractSitiuationData
import com.nxplayr.fsl.data.model.SignupData
import com.nxplayr.fsl.util.MyUtils
import com.nxplayr.fsl.util.SessionManager
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.item_nationality_list.view.*
import java.util.*


class ContractSitiuationAdapter(
    val context: Activity,
    val listData: ArrayList<ContractSitiuationData?>?,
    val onItemClick: OnItemClick,
    val from: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var mSelection = -1
    var sessionManager: SessionManager = SessionManager(context)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_nationality_list, parent, false)
        return LanguageViewHolder(context, v)
    }

    override fun getItemCount(): Int {

        return listData!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LanguageViewHolder) {
            var holder1 = holder as LanguageViewHolder
            holder1.bind(context, listData!![position]!!, holder1.adapterPosition, onItemClick)
            holder1.edit_expire_date.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (listData!![position]!!.contractsituationName.equals("Free", false)) {
                        listData!![position]!!.userContractExpiryDate = "Free"
                    } else {
                        listData!![position]!!.userContractExpiryDate = p0.toString();

                    }
                }

                override fun afterTextChanged(p0: Editable?) {
                }

            })
            holder1.itemView.setOnClickListener {
                onItemClick.onClicled(holder1.adapterPosition, "")
            }
        }
    }

    class LanguageViewHolder(context: Activity, itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var sessionManager: SessionManager? = null
        var userData: SignupData? = null
        var select: Boolean = false
        var edit_expire_date = itemView.edit_expire_date
        var ll_expireDate = itemView.ll_expireDate
        var dob: String = ""
        var age1: String = ""
        var date: String = ""
        var dateAge: String = ""
        var selectGender: String = ""

        init {
            sessionManager = SessionManager(context)
            userData = sessionManager!!.get_Authenticate_User()
        }

        fun bind(
            context: Activity,
            countryList: ContractSitiuationData,
            position: Int,
            onItemClick: OnItemClick
        ) = with(itemView) {

            tv_nationality.text = countryList.contractsituationName
            edit_expire_date.setText(countryList.userContractExpiryDate)
            if (countryList.checked) {
                btn_nationality.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.checkbox_selected
                    )
                )
                if (countryList.contractsituationName.equals("Free", false)) {
                    ll_expireDate.visibility = View.GONE
                } else {
                    ll_expireDate.visibility = View.VISIBLE
                }
            } else {
                btn_nationality.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.checkbox_unselected
                    )
                )
                ll_expireDate.visibility = View.GONE

            }
            edit_expire_date.setOnClickListener {
                DataPickerDialog(context, edit_expire_date)
            }

        }


        private fun DataPickerDialog(context: Activity, edit_expire_date: TextInputEditText) {
            val c = Calendar.getInstance()
            val today = c.time
            //
            val mYear = c.get(Calendar.YEAR)
            val mMonth = c.get(Calendar.MONTH)
            val mDay = c.get(Calendar.DAY_OF_MONTH)


            val mincalendar = Calendar.getInstance()
            mincalendar.set(mYear, mMonth, mDay)

            val dpd = DatePickerDialog(
                context!!,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    var monthOfYear = monthOfYear
                    Log.d("year", year.toString() + "")
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, monthOfYear)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    monthOfYear = monthOfYear + 1
                    val minAdultAge = GregorianCalendar()
                    minAdultAge.add(Calendar.YEAR, 0)

                    age1 = (Integer.toString(calculateAge(c.timeInMillis)))
                    dob =
                        year.toString() + "-" + (if (monthOfYear < 10) "0$monthOfYear" else monthOfYear) + "-" + if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth
                    try {
                        date = MyUtils.formatDate(dob, "yyyy-MM-dd", "dd/MM/yyyy")
                        edit_expire_date.setText(date)

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }, mYear, mMonth, mDay
            )
//            dpd.datePicker.maxDate = mincalendar.timeInMillis
            dpd.show()
        }


        fun calculateAge(date: Long): Int {
            val dob = Calendar.getInstance()
            dob.timeInMillis = date
            val today = Calendar.getInstance()
            var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
            if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
                age--
            }
            return age
        }

    }

    interface OnItemClick {
        fun onClicled(position: Int, from: String)
    }
}
