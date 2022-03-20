package com.nxplayr.fsl.data.model
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class JobListPojo(
    @SerializedName("data")
    val `data`: List<JobListData>?,
    @SerializedName("message")
    val message: String?,
    @SerializedName("status")
    val status: String?
):Serializable

data class JobListData(
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
    var isYouSaved: String?,
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
):Serializable

data class Jobfunction(
    @SerializedName("jobID")
    val jobID: String?,
    @SerializedName("jobfuncID")
    val jobfuncID: String?,
    @SerializedName("jobfuncName")
    val jobfuncName: String?,
    @SerializedName("jobsfuncID")
    val jobsfuncID: String?
):Serializable

data class Jobindustry(
    @SerializedName("compindID")
    val compindID: String?,
    @SerializedName("compindName")
    val compindName: String?,
    @SerializedName("jobID")
    val jobID: String?,
    @SerializedName("jobindID")
    val jobindID: String?
):Serializable

data class Jobtype(
    @SerializedName("jobID")
    val jobID: String?,
    @SerializedName("jobstypeID")
    val jobstypeID: String?,
    @SerializedName("jobtypeID")
    val jobtypeID: String?,
    @SerializedName("jobtypeName")
    val jobtypeName: String?
):Serializable

data class UserCompany(
    @SerializedName("cityID")
    val cityID: String?,
    @SerializedName("cityName")
    val cityName: String?,
    @SerializedName("compindID")
    val compindID: String?,
    @SerializedName("compindName")
    val compindName: Any?,
    @SerializedName("compsizeID")
    val compsizeID: String?,
    @SerializedName("compsizeName")
    val compsizeName: Any?,
    @SerializedName("comptypeID")
    val comptypeID: String?,
    @SerializedName("comptypeName")
    val comptypeName: Any?,
    @SerializedName("countryID")
    val countryID: String?,
    @SerializedName("countryName")
    val countryName: String?,
    @SerializedName("stateID")
    val stateID: String?,
    @SerializedName("stateName")
    val stateName: String?,
    @SerializedName("userCompanyCoverPhoto")
    val userCompanyCoverPhoto: String?,
    @SerializedName("userCompanyCreatedDate")
    val userCompanyCreatedDate: String?,
    @SerializedName("userCompanyEmail")
    val userCompanyEmail: String?,
    @SerializedName("userCompanyLocation")
    val userCompanyLocation: String?,
    @SerializedName("userCompanyLogo")
    val userCompanyLogo: String?,
    @SerializedName("userCompanyName")
    val userCompanyName: String?,
    @SerializedName("userCompanyOverview")
    val userCompanyOverview: String?,
    @SerializedName("userCompanyPhone")
    val userCompanyPhone: String?,
    @SerializedName("userCompanyTagline")
    val userCompanyTagline: String?,
    @SerializedName("userCompanyUrl")
    val userCompanyUrl: String?,
    @SerializedName("userCompanyWebsite")
    val userCompanyWebsite: String?,
    @SerializedName("userCompanyYearEstablished")
    val userCompanyYearEstablished: String?,
    @SerializedName("userID")
    val userID: String?,
    @SerializedName("usercompanyID")
    val usercompanyID: String?
):Serializable