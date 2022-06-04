package com.nxplayr.fsl.data.model.Interest

import com.google.gson.annotations.SerializedName

data class InterestResponse(

	@field:SerializedName("data")
	val data: List<DataItem> = listOf(),

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItem(

	@field:SerializedName("compsizeID")
	val compsizeID: String? = null,

	@field:SerializedName("usercompanyID")
	val usercompanyID: String? = null,

	@field:SerializedName("stateID")
	val stateID: String? = null,

	@field:SerializedName("comptypeName")
	val comptypeName: String? = null,

	@field:SerializedName("userID")
	val userID: String? = null,

	@field:SerializedName("userCompanyName")
	val userCompanyName: String? = null,

	@field:SerializedName("cityName")
	val cityName: String? = null,

	@field:SerializedName("stateName")
	val stateName: String? = null,

	@field:SerializedName("userCompanyYearEstablished")
	val userCompanyYearEstablished: String? = null,

	@field:SerializedName("userCompanyPhone")
	val userCompanyPhone: String? = null,

	@field:SerializedName("comptypeID")
	val comptypeID: String? = null,

	@field:SerializedName("userCompanyCreatedDate")
	val userCompanyCreatedDate: String? = null,

	@field:SerializedName("userCompanyTagline")
	val userCompanyTagline: String? = null,

	@field:SerializedName("userCompanyEmail")
	val userCompanyEmail: String? = null,

	@field:SerializedName("userCompanyCoverPhoto")
	val userCompanyCoverPhoto: String? = null,

	@field:SerializedName("compindName")
	val compindName: String? = null,

	@field:SerializedName("cityID")
	val cityID: String? = null,

	@field:SerializedName("IsYouFollowing")
	val isYouFollowing: String? = null,

	@field:SerializedName("countryID")
	val countryID: String? = null,

	@field:SerializedName("userCompanyUrl")
	val userCompanyUrl: String? = null,

	@field:SerializedName("userCompanyWebsite")
	val userCompanyWebsite: String? = null,

	@field:SerializedName("compsizeName")
	val compsizeName: String? = null,

	@field:SerializedName("compindID")
	val compindID: String? = null,

	@field:SerializedName("userCompanyLogo")
	val userCompanyLogo: String? = null,

	@field:SerializedName("userCompanyLocation")
	val userCompanyLocation: String? = null,

	@field:SerializedName("userCompanyOverview")
	val userCompanyOverview: String? = null,

	@field:SerializedName("countryName")
	val countryName: String? = null,

	@field:SerializedName("totalFollowers")
	val totalFollowers: String? = null
)
