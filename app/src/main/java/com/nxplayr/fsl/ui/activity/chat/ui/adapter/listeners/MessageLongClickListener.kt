package com.nxplayr.fsl.ui.activity.chat.ui.adapter.listeners

import android.view.View
import com.quickblox.chat.model.QBChatMessage


interface MessageLongClickListener {
    fun onMessageLongClicked(itemViewType: Int?, view: View, qbChatMessage: QBChatMessage?)
}