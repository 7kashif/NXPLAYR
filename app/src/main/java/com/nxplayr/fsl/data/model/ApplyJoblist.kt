package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName



data class ApplyJoblist(
    @SerializedName("data")
    val `data`: List<ApplyJobData>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
)

data class  ApplyJobData(
    @SerializedName("jobID")
    val jobID: String?,
    @SerializedName("jobapplyDate")
    val jobapplyDate: String?,
    @SerializedName("jobapplyID")
    val jobapplyID: String?,
    @SerializedName("jobapplyResume")
    val jobapplyResume: String?,
    @SerializedName("postdata")
    val postdata: List<JobPostdata>?,
    @SerializedName("userID")
    val userID: String?
)

data class JobPostdata(
    @SerializedName("applicantCount")
    val applicantCount: String?,
    @SerializedName("cityID")
    val cityID: String?,
    @SerializedName("cityName")
    val cityName: String?,
    @SerializedName("companyID")
    val companyID: String?,
    @SerializedName("connections")
    val connections: String?,
    @SerializedName("countryID")
    val countryID: String?,
    @SerializedName("countryName")
    val countryName: String?,
    @SerializedName("IsYouApplied")
    val isYouApplied: String?,
    @SerializedName("IsYouSaved")
    val isYouSaved: String?,
    @SerializedName("jobCreatedDate")
    val jobCreatedDate: String?,
    @SerializedName("jobDescription")
    val jobDescription: String?,
    @SerializedName("jobDuties")
    val jobDuties: Any?,
    @SerializedName("jobID")
    val jobID: String?,
    @SerializedName("jobImage")
    val jobImage: String?,
    @SerializedName("jobLocation")
    val jobLocation: String?,
    @SerializedName("jobPurpose")
    val jobPurpose: String?,
    @SerializedName("jobQualifications")
    val jobQualifications: Any?,
    @SerializedName("jobRefrenceNo")
    val jobRefrenceNo: String?,
    @SerializedName("jobShared")
    val jobShared: String?,
    @SerializedName("jobStatus")
    val jobStatus: String?,
    @SerializedName("jobTagline")
    val jobTagline: String?,
    @SerializedName("jobTitle")
    val jobTitle: String?,
    @SerializedName("jobfunctions")
    val jobfunctions: List<Jobfunction>?,
    @SerializedName("jobindustry")
    val jobindustry: List<Jobindustry>?,
    @SerializedName("jobtypes")
    val jobtypes: List<Jobtype>?,
    @SerializedName("stateID")
    val stateID: String?,
    @SerializedName("stateName")
    val stateName: String?,
    @SerializedName("userCompany")
    val userCompany: List<UserCompany>?,
    @SerializedName("userCountryCode")
    val userCountryCode: String?,
    @SerializedName("userEmail")
    val userEmail: String?,
    @SerializedName("userFirstName")
    val userFirstName: String?,
    @SerializedName("userID")
    val userID: String?,
    @SerializedName("userLastName")
    val userLastName: String?,
    @SerializedName("userMobile")
    val userMobile: String?,
    @SerializedName("userProfilePicture")
    val userProfilePicture: String?
)
