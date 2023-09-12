package com.example.ust_laundry

import android.content.Intent
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.ust_laundry.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import mumayank.com.airlocationlibrary.AirLocation

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener {

    private lateinit var b: ActivityMapsBinding

    val MAPBOX_TOKEN = "pk.eyJ1IjoibmFzeWFyeCIsImEiOiJjbDQ4MmtnZWIwbzU0M2lwaTc2anVwM2xmIn0.Mz7mkrr00kTYrroq-qRg0g"
    var URL = ""

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnGo ->{
                var paket : Bundle? = intent.extras
                var alm = paket?.getString("alm")
                URL = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + "$alm.json?proximity=$lng,$lat&access_token=$MAPBOX_TOKEN&limit=1"
                getDestinationLocation(URL)
            }

            R.id.fab ->{
                val ll = LatLng(lat,lng)
                gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,16.0f))
                b.txMyLocation.setText("My Position : LAT=$lat, LNG=$lng")
            }
        }
    }

    var lat : Double = 0.0; var lng : Double = 0.0;
    var airLoc : AirLocation? = null
    var gMap : GoogleMap? = null
    lateinit var mapFragment : SupportMapFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(b.root)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mapFragment = supportFragmentManager.findFragmentById(R.id.mapsFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        b.btnGo.setOnClickListener(this)
        b.fab.setOnClickListener(this)
    }

    fun getDestinationLocation(url : String){
        val request = JsonObjectRequest(Request.Method.GET,url,null,
            Response.Listener {
                val features = it.getJSONArray("features").getJSONObject(0)
                val place_name = features.getString("place_name")
                val center = features.getJSONArray("center")
                val lat = center.get(0).toString()
                val lng = center.get(1).toString()
                getDestinationRoutes(lng,lat,place_name)
            }, Response.ErrorListener {
                Toast.makeText(this,"can't get destination location",Toast.LENGTH_SHORT).show()
            })
        val q = Volley.newRequestQueue(this)
        q.add(request)
    }

    fun getDestinationRoutes(destLat : String, destLng : String, place_name : String){
        URL = "https://api.mapbox.com/directions/v5/mapbox/driving/" + "$lng,$lat;$destLng,$destLat?access_token=$MAPBOX_TOKEN&geometries=geojson"
        val request = JsonObjectRequest(Request.Method.GET,URL,null,
            Response.Listener {
                val routes = it.getJSONArray("routes").getJSONObject(0)
                val legs = routes.getJSONArray("legs").getJSONObject(0)
                val distance = legs.getInt("distance")/1000.0
                val duration = legs.getInt("duration")/60
                b.txMyLocation.setText("Lokasi saya :\nLat : $lat   Lng : $lng\n" +
                        "Destinasi : $place_name\nLat : $destLat   Lng : $destLng\n" +
                        "Jarak : $distance km   Duration : $duration minute")
                val geometry = routes.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                var arraySteps = ArrayList<LatLng>()
                for (i in 0..coordinates.length()-1){
                    val lngLat = coordinates.getJSONArray(i)
                    val fLng = lngLat.getDouble(0)
                    val fLat = lngLat.getDouble(1)
                    arraySteps.add(LatLng(fLat,fLng))
                }
                drawRoutes(arraySteps,place_name)
            },
            Response.ErrorListener {
                Toast.makeText(this, "something is wrong! ${it.message.toString()}",
                    Toast.LENGTH_SHORT).show()
            })
        val q = Volley.newRequestQueue(this)
        q.add(request)
    }

    fun drawRoutes(array : ArrayList<LatLng>,place_name: String){
        gMap?.clear()
        val polyline = PolylineOptions().color(Color.BLUE).width(10.0f)
            .clickable(true).addAll(array)
        gMap?.addPolyline(polyline)
        val ll = LatLng(lat,lng)
        gMap?.addMarker(MarkerOptions().position(ll).title("Hei! I'am here"))
        gMap?.addMarker(MarkerOptions().position(array.get(array.size-1)).title(place_name))
        gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,10.0f))
    }

    override fun onMapReady(p0: GoogleMap) {
        gMap = p0
        if (gMap != null){
            airLoc = AirLocation(this,true,true,
                object : AirLocation.Callbacks{
                    override fun onFailed(locationFailedEnum: AirLocation.LocationFailedEnum) {
                        Toast.makeText(this@MapsActivity, "Failed to get current location",
                            Toast.LENGTH_SHORT).show()
                        b.txMyLocation.setText("Failed to get current location")
                    }

                    override fun onSuccess(location: Location) {
                        lat = location.latitude; lng = location.longitude
                        val ll = LatLng(location.latitude, location.longitude)
                        gMap!!.addMarker(MarkerOptions().position(ll).title("Hei! I'm here"))
                        gMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,16.0f))
                        b.txMyLocation.setText("Lokasi saya : LAT=${location.latitude}," +
                                "LNG=${location.longitude}")
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        airLoc?.onActivityResult(requestCode,resultCode,data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        airLoc?.onRequestPermissionsResult(requestCode,
            permissions,grantResults)
        super.onRequestPermissionsResult(requestCode,
            permissions, grantResults)
    }
}