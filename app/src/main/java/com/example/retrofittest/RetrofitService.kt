package com.example.retrofittest

import android.content.ClipData
import com.example.retrofittest.data1.Weather
import com.example.retrofittest.data2.Pollution
import retrofit2.Call
import retrofit2.http.GET

interface RetrofitService {

    @GET("data/2.5/weather?lat=37.572025&lon=127.005028&appid=3bbea22f826e4eef49dc445bd1114a75")
    fun getWeather() : Call<Weather>

    @GET("B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=JCrJa4%2F4eF07FKbnkSi7BDDUvnJXCE1CTiyt%2FfnxJ%2B7jewHaXTp5hrKQzOKdWYctQB%2B3a%2FHLuUHkTPq4hqrxvA%3D%3D&returnType=json&numOfRows=10&pageNo=1&sidoName=%EC%84%9C%EC%9A%B8&ver=1.0")
    fun getAirPollution() : Call<Pollution>

}