package com.nxplayr.fsl.data.model

data class HomeFilterItemPojo(
    var title:String,var isVisiable:Boolean=true,val subFilterItem:ArrayList<FilterSubItem>
)