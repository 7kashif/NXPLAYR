package com.nxplayr.fsl.data.model

import android.net.Uri
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LanguageListPojo(
        var languageName: String? = "",
        var status: Boolean? = false,
        var checked: Boolean = false
) : Serializable

data class ListPojo(
        var languageName: String? = "",
        val isImage: Int,
        var checked: Boolean = false
) : Serializable



