package com.nxplayr.fsl.ui.activity.chat.ui.adapter.listeners

import android.view.View
import com.quickblox.chat.model.QBAttachment


interface AttachClickListener {
    fun onAttachmentClicked(itemViewType: Int?, view: View, attachment: QBAttachment)
}