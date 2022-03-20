package com.nxplayr.fsl.ui.fragments.userinterest.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import com.nxplayr.fsl.ui.fragments.userinterest.adapter.AddInterestsAdapter
import com.nxplayr.fsl.ui.fragments.userinterest.adapter.ViewProfessionalsAdapter
import kotlinx.android.synthetic.main.common_recyclerview.*
import kotlinx.android.synthetic.main.fragment_add_interests.*
import kotlinx.android.synthetic.main.toolbar.*


class AddInterestsFragment : Fragment() {

    private var v: View? = null
    var listData: ArrayList<String>? = ArrayList()
    var data: ArrayList<String>? = ArrayList()
    var addInterestsAdapter: AddInterestsAdapter? = null
    var viewProfessionalsAdapter: ViewProfessionalsAdapter? = null
    lateinit var linearLayoutManager: LinearLayoutManager
    val list = arrayListOf(R.drawable.image, R.drawable.appbg, R.drawable.image, R.drawable.appbg)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        if (v == null)
            v = inflater.inflate(R.layout.fragment_add_interests, container, false)

        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvToolbarTitle.setText(getString(R.string.add_interests))


        toolbar.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        addInterestsAdapter = AddInterestsAdapter(activity as MainActivity, listData, object : AddInterestsAdapter.OnItemClick {
            override fun onClicled(position: Int, from: String) {

                when (from) {
                    "Image" -> {

                    }
                }
            }

        })
        listData!!.add("Arif Sheliya")
        listData!!.add("Aran Smith")
        listData!!.add("Arif Sheliya")
        listData!!.add("Aran Smith")


        linearLayoutManager = LinearLayoutManager(activity)

        recyclerview.layoutManager = linearLayoutManager

        recyclerview.adapter = addInterestsAdapter

        /* val dividers = DividerItemDecoration(recyclerview.getContext(),
            DividerItemDecoration.VERTICAL)
    dividers.setDrawable(
            context?.let { ContextCompat.getDrawable(it, R.drawable.line_layout) }!!
    )
    recyclerview.addItemDecoration(dividers)
*/

        addInterestsAdapter?.notifyDataSetChanged()


        //view professionals

        viewProfessionalsAdapter = ViewProfessionalsAdapter(activity as MainActivity, data)
        data!!.add("Amazone")
        data!!.add("Harvard Business")
        data!!.add("Amazone")
        data!!.add("Harvard Business")

        linearLayoutManager = LinearLayoutManager(activity)
        recyclerview1.layoutManager = linearLayoutManager
        recyclerview1.adapter = viewProfessionalsAdapter

        /* val divider = DividerItemDecoration(recyclerview_professionals.getContext(),
                 DividerItemDecoration.VERTICAL)
         divider.setDrawable(
                 context?.let { ContextCompat.getDrawable(it, R.drawable.line_layout) }!!
         )
         recyclerview_professionals.addItemDecoration(divider)*/

        viewProfessionalsAdapter?.notifyDataSetChanged()
    }
}

