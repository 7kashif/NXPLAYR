package com.nxplayr.fsl.data.model


import com.google.gson.annotations.SerializedName


data class LinkedInEmailPojo (
    @SerializedName("elements")
    var elements: List<Element?>? = listOf()
)

data class Element(
    @SerializedName("handle")
    var handleEmail: String? = "",
    @SerializedName("handle~")
    var handle: Handle? = Handle()
)

data class Handle(
    @SerializedName("emailAddress")
    var emailAddress: String? = ""
)