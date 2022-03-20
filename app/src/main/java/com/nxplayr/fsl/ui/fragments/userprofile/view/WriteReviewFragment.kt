package com.nxplayr.fsl.ui.fragments.userprofile.view

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.nxplayr.fsl.ui.activity.main.view.MainActivity

import com.nxplayr.fsl.R
import kotlinx.android.synthetic.main.toolbar.*


class WriteReviewFragment : Fragment() {

    private var v: View? = null
    var mActivity: Activity? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        if (v == null) {
            v = inflater.inflate(R.layout.fragment_write_review, container, false)
        }
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = context as AppCompatActivity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        tvToolbarTitle.setText(resources.getString(R.string.write_a_review))

        toolbar.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }

}
