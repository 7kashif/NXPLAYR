package com.nxplayr.fsl.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by ADMIN on 06/02/2018.
 */
data class Contact(
        @SerializedName("contactID")
        var contactID: String? = "",
        @SerializedName("contactFirstname")
        var contactFirstname: String? = "",
        @SerializedName("contactLastname")
        var contactLastname: String? = "",
        @SerializedName("emailaddress")
        var emailaddress: String? = "",
        @SerializedName("phone")
        var phone: String? = "",

        var checked: Boolean? = false

) : Serializable