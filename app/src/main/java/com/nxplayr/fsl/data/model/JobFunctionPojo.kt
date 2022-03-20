package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName



data class JobFunctionPojo(
    @SerializedName("data")
    var `data`: List<JobFunctionList>,
    @SerializedName("message")
    var message: String,
    @SerializedName("status")
    var status: String
)

data class JobFunctionList(
    @SerializedName("jobfuncCreatedDate")
    var jobfuncCreatedDate: String,
    @SerializedName("jobfuncID")
    var jobfuncID: String,
    @SerializedName("jobfuncName")
    var jobfuncName: String,
    @SerializedName("jobfuncRemarks")
    var jobfuncRemarks: String,
    @SerializedName("jobfuncStatus")
    var jobfuncStatus: String
)