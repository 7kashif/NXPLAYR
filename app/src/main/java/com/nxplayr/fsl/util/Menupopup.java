package com.nxplayr.fsl.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.nxplayr.fsl.R;

import java.util.ArrayList;

/**
 * Created by ADMIN on 26/02/2018.
 */

public class Menupopup {
    OnItemSelectedListener mListener;


    public EasyDialog setMenuoption(Context context, final ArrayList<String> data, View v) {
        EasyDialog easyDialog;
        View view = ((Activity) context).getLayoutInflater().inflate(R.layout.popup_view_selection_layout, null);

        ListView listView = (ListView) view.findViewById(R.id.list_item);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                R.layout.otheruser_item_filter, data);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mListener != null) {
                    mListener.onMenuitemclick(position, data.get(position));
                }
            }
        });
        easyDialog = new EasyDialog(context)
                .setLayout(view)

                .setLocationByAttachedView(v)
                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                // .setAnimationTranslationShow(EasyDialog.DIRECTION_X, 1000, -600, 100, -50, 50, 0)

                .setAnimationAlphaShow(500, 0.3f, 1.0f)
                //  .setAnimationTranslationDismiss(EasyDialog.DIRECTION_X, 500, -50, 800)
                .setAnimationAlphaDismiss(500, 1.0f, 0.0f)
                .setTouchOutsideDismiss(true)
                .setMatchParent(false)
                // .setMarginLeftAndRight(10, 10)
                .setCornerRadius(10)
                .setOutsideColor(context.getResources().getColor(R.color.outside_color_trans))
                .show();
        return easyDialog;

    }

    public void setListener(OnItemSelectedListener listener) {
        this.mListener = listener;
    }

    public interface OnItemSelectedListener {
        public void onMenuitemclick(int pos, String menuname);
    }

}
