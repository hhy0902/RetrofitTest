package com.example.retrofittest.data3


import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("coFlag")
    val coFlag: Any?,
    @SerializedName("coGrade")
    val coGrade: String?,
    @SerializedName("coValue")
    val coValue: String?,
    @SerializedName("dataTime")
    val dataTime: String?,
    @SerializedName("khaiGrade")
    val khaiGrade: String?,
    @SerializedName("khaiValue")
    val khaiValue: String?,
    @SerializedName("no2Flag")
    val no2Flag: Any?,
    @SerializedName("no2Grade")
    val no2Grade: String?,
    @SerializedName("no2Value")
    val no2Value: String?,
    @SerializedName("o3Flag")
    val o3Flag: Any?,
    @SerializedName("o3Grade")
    val o3Grade: String?,
    @SerializedName("o3Value")
    val o3Value: String?,
    @SerializedName("pm10Flag")
    val pm10Flag: Any?,
    @SerializedName("pm10Grade")
    val pm10Grade: String?,
    @SerializedName("pm10Value")
    val pm10Value: String?,
    @SerializedName("pm25Flag")
    val pm25Flag: Any?,
    @SerializedName("pm25Grade")
    val pm25Grade: String?,
    @SerializedName("pm25Value")
    val pm25Value: String?,
    @SerializedName("sidoName")
    val sidoName: String?,
    @SerializedName("so2Flag")
    val so2Flag: Any?,
    @SerializedName("so2Grade")
    val so2Grade: String?,
    @SerializedName("so2Value")
    val so2Value: String?,
    @SerializedName("stationName")
    val stationName: String?
)