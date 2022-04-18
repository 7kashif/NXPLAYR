package com.nxplayr.fsl.util


import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.ListPopupWindow
import com.nxplayr.fsl.R
import java.util.*

class PopupMenu(internal var mContext: Context, internal var view: View, menuList: List<String>) {
    internal var menuList: List<String> = ArrayList()
    internal var onMenuItemClickListener: OnMenuSelectItemClickListener? = null
    internal lateinit var popup: ListPopupWindow

    init {
        this.menuList = menuList

    }

    fun showPopUp(onMenuItemClickListener: OnMenuSelectItemClickListener?) {

        val wrapper = ContextThemeWrapper(mContext, R.style.popupmenu)

        popup = ListPopupWindow(wrapper)
        popup.setAdapter(ArrayAdapter(mContext, R.layout.item_menu_textview, menuList))
        popup.anchorView = view
        popup.width = 350
//        popup.width = ListPopupWindow.WRAP_CONTENT

        if (menuList.size > 5)
            popup.height = 400
        else
            popup.height = ListPopupWindow.WRAP_CONTENT

        popup.animationStyle = R.style.popup_window_animation
        popup.setOnItemClickListener { adapterView, view, i, l ->
            onMenuItemClickListener?.onItemClick(menuList[i].toString(), i)
            popup.dismiss()
        }

        popup.show()


    }

//    fun showPopUp(width: Int, onMenuItemClickListener: OnMenuSelectItemClickListener?) {
//
//        val wrapper = ContextThemeWrapper(mContext, R.style.popupmenu)
//
//        popup = ListPopupWindow(wrapper)
//        popup.setAdapter(ArrayAdapter(mContext, R.layout.item_menu_textview, menuList))
//        popup.anchorView = view
//        popup.width = width
//
//        if (menuList.size > 5)
//            popup.height = 400
//        else
//            popup.height = ListPopupWindow.WRAP_CONTENT
//
//        popup.animationStyle = R.style.popup_window_animation
//        popup.setOnItemClickListener { adapterView, view, i, l ->
//            onMenuItemClickListener?.onItemClick(menuList[i].toString(),i)
//            popup.dismiss()
//        }
//
//
//        popup.show()
//
//
//    }

    interface OnMenuSelectItemClickListener {
        fun onItemClick(item: String, pos: Int)
    }


}
