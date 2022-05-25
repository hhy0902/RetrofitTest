package com.example.retrofittest

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.retrofittest.data1.Weather
import com.example.retrofittest.data2.Pollution
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private var cancellationTokenSource : CancellationTokenSource? = null

    private lateinit var geocoder: Geocoder

    private var lon : String = ""
    private var lat : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        requestPermission()

        val url = "https://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty?serviceKey=JCrJa4%2F4eF07FKbnkSi7BDDUvnJXCE1CTiyt%2FfnxJ%2B7jewHaXTp5hrKQzOKdWYctQB%2B3a%2FHLuUHkTPq4hqrxvA%3D%3D&returnType=json&numOfRows=10&pageNo=1&sidoName=%EC%84%9C%EC%9A%B8&ver=1.0"

        val url3 = "https://api.openweathermap.org/data/2.5/weather?lat=37.572025&lon=127.005028&appid=3bbea22f826e4eef49dc445bd1114a75"

        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val retrofitService = retrofit.create(RetrofitService::class.java)

        retrofitService.getAirPollution().enqueue(object : Callback<Pollution> {
            override fun onResponse(call: Call<Pollution>, response: Response<Pollution>) {
                if (response.isSuccessful) {
                    val pollution = response.body()

                    Log.d("testt items", "${pollution?.response?.body?.items}")
                    Log.d("testt pm10Value", "${pollution?.response?.body?.items?.firstOrNull()?.pm10Value}")
                    Log.d("testt pm10Value2", "${pollution?.response?.body?.items?.get(1)?.pm10Value}")

                }
            }

            override fun onFailure(call: Call<Pollution>, t: Throwable) {
                Log.d("testt","${t.message}")
            }
        })

//        retrofitService.getWeather().enqueue(object : Callback<Weather> {
//            override fun onResponse(call: Call<Weather>, response: Response<Weather>) {
//                if (response.isSuccessful) {
//                    val weather = response.body()
//
//                    Toast.makeText(this@MainActivity, "${weather?.clouds?.all}", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this@MainActivity, "abcd", Toast.LENGTH_SHORT).show()
//                    Log.d("testt cloud","${weather?.clouds?.all}")
//                }
//            }
//
//            override fun onFailure(call: Call<Weather>, t: Throwable) {
//                Log.d("testt","${t.message}")
//            }
//        })


//        retrofitService.getItemList3().enqueue(object : Callback<Item3> {
//            override fun onResponse(call: Call<Item3>, response: Response<Item3>) {
//
//                if(response.isSuccessful) {
//                    val data = response.body()
//                    Log.d("asdf2 ", "${response.body()?.wind}")
//
//                    data?.let {
//                        Log.d("asdf" ,"${it.wind}")
//                    }
//
//                }
//            }
//
//            override fun onFailure(call: Call<Item3>, t: Throwable) {
//                Log.d("testt","${t.message}")
//            }
//        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d("testt", "승낙")

                fetchAirPollution()

            } else {
                Log.d("testt", "거부")
                finish()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchAirPollution() {
        cancellationTokenSource = CancellationTokenSource()
        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token
        ).addOnSuccessListener { location ->
            try {
                lat = location.latitude.toString()
                lon = location.longitude.toString()
                Log.d("testt", "$lat / $lon")

                geocoder = Geocoder(this, Locale.getDefault())

                val address = geocoder.getFromLocation(lat.toDouble(), lon.toDouble(), 1)

                Log.d("testt getAddressLine","${address[0].getAddressLine(0)}")
                Log.d("testt adminArea","${address[0].adminArea}") // 서울특별시
                Log.d("testt subLocality","${address[0].subLocality}") // 송파구
                Log.d("testt subThoroughfare","${address[0].subThoroughfare}") // 201
                Log.d("testt thoroughfare","${address[0].thoroughfare}") // 문정동

                val lat2 = location.latitude
                val lon2 = location.longitude

                val tmp = convertGRID_GPS(TO_GRID, lat2, lon2)

                Log.d("testt xy convert", "x = " + tmp.x + ", y = " + tmp.y)


            } catch (e : Exception) {
                e.printStackTrace()
                Toast.makeText(this,"error 발생 다시 시도", Toast.LENGTH_SHORT).show()
            } finally {
                Log.d("testt finish","finish")
            }
        }

    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION_PERMISSIONS
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
    }

    private fun convertGRID_GPS(mode: Int, lat_X: Double, lng_Y: Double): LatXLngY {
        val RE = 6371.00877 // 지구 반경(km)
        val GRID = 5.0 // 격자 간격(km)
        val SLAT1 = 30.0 // 투영 위도1(degree)
        val SLAT2 = 60.0 // 투영 위도2(degree)
        val OLON = 126.0 // 기준점 경도(degree)
        val OLAT = 38.0 // 기준점 위도(degree)
        val XO = 43.0 // 기준점 X좌표(GRID)
        val YO = 136.0 // 기1준점 Y좌표(GRID)

        //
        // LCC DFS 좌표변환 ( code : "TO_GRID"(위경도->좌표, lat_X:위도,  lng_Y:경도), "TO_GPS"(좌표->위경도,  lat_X:x, lng_Y:y) )
        //
        val DEGRAD = Math.PI / 180.0
        val RADDEG = 180.0 / Math.PI
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD
        var sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn)
        var sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn
        var ro = Math.tan(Math.PI * 0.25 + olat * 0.5)
        ro = re * sf / Math.pow(ro, sn)
        val rs: LatXLngY = LatXLngY()
        if (mode == TO_GRID) {
            rs.lat = lat_X
            rs.lng = lng_Y
            var ra = Math.tan(Math.PI * 0.25 + lat_X * DEGRAD * 0.5)
            ra = re * sf / Math.pow(ra, sn)
            var theta = lng_Y * DEGRAD - olon
            if (theta > Math.PI) theta -= 2.0 * Math.PI
            if (theta < -Math.PI) theta += 2.0 * Math.PI
            theta *= sn
            rs.x = Math.floor(ra * Math.sin(theta) + XO + 0.5)
            rs.y = Math.floor(ro - ra * Math.cos(theta) + YO + 0.5)
        } else {
            rs.x = lat_X
            rs.y = lng_Y
            val xn = lat_X - XO
            val yn = ro - lng_Y + YO
            var ra = Math.sqrt(xn * xn + yn * yn)
            if (sn < 0.0) {
                ra = -ra
            }
            var alat = Math.pow(re * sf / ra, 1.0 / sn)
            alat = 2.0 * Math.atan(alat) - Math.PI * 0.5
            var theta = 0.0
            if (Math.abs(xn) <= 0.0) {
                theta = 0.0
            } else {
                if (Math.abs(yn) <= 0.0) {
                    theta = Math.PI * 0.5
                    if (xn < 0.0) {
                        theta = -theta
                    }
                } else theta = Math.atan2(xn, yn)
            }
            val alon = theta / sn + olon
            rs.lat = alat * RADDEG
            rs.lng = alon * RADDEG
        }
        return rs
    }

    internal inner class LatXLngY {
        var lat = 0.0
        var lng = 0.0
        var x = 0.0
        var y = 0.0
    }

    companion object {
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 1000
        var TO_GRID = 0
        var TO_GPS = 1
    }
}






































