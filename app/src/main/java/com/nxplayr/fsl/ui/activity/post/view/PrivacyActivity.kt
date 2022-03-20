package com.nxplayr.fsl.ui.activity.post.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.nxplayr.fsl.R
import com.nxplayr.fsl.data.model.FilterSubItem
import com.nxplayr.fsl.util.MyUtils
import kotlinx.android.synthetic.main.activity_post_home.*
import kotlinx.android.synthetic.main.activity_privacy.*
import kotlinx.android.synthetic.main.toolbar.*

class PrivacyActivity : AppCompatActivity() {

    var postPrivacyType = ""
    var connectionTypeIDs = ""
    var listSortByItem: ArrayList<FilterSubItem?>? = null
    var newListSortByItem: ArrayList<FilterSubItem?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)

        if (intent != null) {
            if (intent.hasExtra("postPrivacyType")) {
                postPrivacyType = intent?.getStringExtra("postPrivacyType")!!
            }
        }
        setupUI()

    }

    private fun setupUI() {

        tvToolbarTitle?.text = getString(R.string.privacy)
        menuToolbarItem.visibility = View.VISIBLE
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        when (postPrivacyType) {
            "Public" -> {
                radioButton_public_activity.isChecked = true
            }
            "Connection" -> {
                radioButton_connection_activity.isChecked = true

            }
            "Groups" -> {
                radioButton_groups_public_activity.isChecked = true
            }

        }
        menuToolbarItem.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0)
        menuToolbarItem.setText("Save")
        menuToolbarItem.setOnClickListener {
            when (postPrivacyType) {
                "Public" -> {

                    connectionTypeIDs=""
                    var intent = Intent().apply {
                        putExtra("connectionTypeIDs", connectionTypeIDs)
                        putExtra("postPrivacyType", postPrivacyType)
                    }
                    setResult(800, intent)
                    onBackPressed()
                }
                "Connection" -> {
                    if (!newListSortByItem.isNullOrEmpty()) {
                        connectionTypeIDs = newListSortByItem?.joinToString { it?.id!! }!!
                        var intent = Intent().apply {
                            putExtra("connectionTypeIDs", connectionTypeIDs)
                            putExtra("postPrivacyType", postPrivacyType)
                        }
                        setResult(800, intent)
                        onBackPressed()
                    } else {
                        MyUtils.showSnackbar(this@PrivacyActivity, "Please select any connection type", ll_mainNotificationList)
                    }
                }
                "Groups" -> {
                    connectionTypeIDs=""
                    var intent = Intent().apply {
                        putExtra("connectionTypeIDs", connectionTypeIDs)
                        putExtra("postPrivacyType", postPrivacyType)
                    }
                    setResult(800, intent)
                    onBackPressed()
                }
            }
        }


        rgGroup_activity.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId) as RadioButton
            postPrivacyType = radioButton.text.toString().trim()
            when (radioButton.text.toString().trim()) {
                resources.getString(R.string.Public) -> {
                    ll_sub_privacy.visibility = View.GONE
                    connectionTypeIDs = ""
                }
                resources.getString(R.string.connections) -> {
                    listSortByItem = ArrayList()
                    listSortByItem!!.clear()
                    connectionTypeIDs = ""
                    listSortByItem!!.add(FilterSubItem(getString(R.string.all), "3,2,4"))
                    listSortByItem!!.add(FilterSubItem(getString(R.string.friends), "3"))
                    listSortByItem!!.add(FilterSubItem(getString(R.string.acquaintances), "2"))
                    listSortByItem!!.add(FilterSubItem(getString(R.string.professionals), "4"))
                    newListSortByItem = ArrayList()
                    getConnetionType(listSortByItem!!)
                }
                resources.getString(R.string.groups) -> {
                    connectionTypeIDs = ""
                    ll_sub_privacy.visibility = View.GONE
                }
            }
        }

    }

    private fun getConnetionType(listSortByItem: ArrayList<FilterSubItem?>) {
        ll_sub_privacy.visibility = View.VISIBLE
        ll_sub_privacy.removeAllViews()
        for (i in 0 until listSortByItem.size) {
            val view: View = this.layoutInflater.inflate(
                    R.layout.item_privacy_list, // Custom view/ layout
                    ll_sub_privacy, // Root layout to attach the view
                    false // Attach with root layout or not
            )

            val radioButton_professionals_activity = view.findViewById<CheckBox>(R.id.radioButton_professionals_activity)

            radioButton_professionals_activity.text = listSortByItem[i]?.title
            radioButton_professionals_activity.isChecked = listSortByItem!![i]?.isSelected!!

            radioButton_professionals_activity?.tag = i
            radioButton_professionals_activity.setOnCheckedChangeListener { compoundButton, b ->
                var selectPosition = radioButton_professionals_activity.tag as Int
                if (compoundButton.isPressed) {
                    when (selectPosition) {
                        0 -> {
                            for (i2 in 0 until listSortByItem?.size) {
                                listSortByItem!![i2]?.isSelected = b
                            }
                            if (listSortByItem!![0]?.isSelected!!) {
                                newListSortByItem= ArrayList()
                                newListSortByItem?.add(listSortByItem!![0])

                            } else {
                                newListSortByItem = ArrayList()


                            }
                            getConnetionType(listSortByItem)
                        }
                        else -> {
                            listSortByItem!![selectPosition]?.isSelected = b
                            if (listSortByItem!![selectPosition]?.isSelected!!) {
                                newListSortByItem?.add(listSortByItem!![selectPosition])
                            } else {
                                for (i3 in 0 until newListSortByItem?.size!!) {
                                    if (listSortByItem!![selectPosition]?.title.equals(newListSortByItem!![i3]?.title)) {
                                        newListSortByItem?.removeAt(i3)
                                        break
                                    }
                                }

                            }
                        }
                    }
                }

            }


            ll_sub_privacy.addView(view)
        }
    }

    override fun onBackPressed() {
        MyUtils.finishActivity(this@PrivacyActivity, true)
    }
}