package com.nxplayr.fsl.util

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.nxplayr.fsl.R
import com.google.android.material.button.MaterialButton
import java.util.*

/**
 * Created by ankititjunkies on 20/03/18.
 */
class MonthYearPickerDialog : DialogFragment() {
    private var listener: DatePickerDialog.OnDateSetListener? = null
    fun setListener(listener: DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        // Get the layout inflater
        val inflater = activity!!.layoutInflater
        val cal = Calendar.getInstance()
        val dialog: View = inflater.inflate(R.layout.month_year_picker_dialog, null)
        val monthPicker = dialog.findViewById<NumberPicker>(R.id.picker_month)
        val yearPicker = dialog.findViewById<NumberPicker>(R.id.picker_year)
        val okButton: MaterialButton = dialog.findViewById(R.id.calenderOkButton)
        val cancelButton: MaterialButton = dialog.findViewById(R.id.calenderCancelButton)
        val month = cal[Calendar.MONTH]
         var yearVal = -1
        var monthVal = -1
        monthPicker!!.displayedValues = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sep", "Oct", "Nov", "Dec")

        if (monthVal != -1)
            monthPicker!!.value = monthVal
        else
            monthPicker!!.value = cal.get(Calendar.MONTH) + 1

        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal[Calendar.MONTH] + 1

        val year = cal[Calendar.YEAR]

        yearPicker.minValue = 1990
        yearPicker.maxValue = year
        yearPicker.value = year

        okButton.setOnClickListener {
            listener!!.onDateSet(null, yearPicker.value, monthPicker.value, 0)
            dismiss()
        }
        cancelButton.setOnClickListener { dismiss() }
        builder.setView(dialog)
        // Add action buttons
//                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        MonthYearPickerDialog.this.getDialog().cancel();
//                    }
//                });
        return builder.create()
    }

    companion object {
        private const val MAX_YEAR = 2099
    }
}