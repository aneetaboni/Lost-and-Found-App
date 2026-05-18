package com.example.lostandfoundapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var etRadius: EditText
    private lateinit var btnSearchRadius: Button

    private var userLatitude = 0.0
    private var userLongitude = 0.0

    private lateinit var btnShowAll: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        dbHelper = DatabaseHelper(this)
        etRadius = findViewById(R.id.etRadius)
        btnSearchRadius = findViewById(R.id.btnSearchRadius)
        btnShowAll = findViewById(R.id.btnShowAll)

        btnShowAll.setOnClickListener {
            loadAllMarkers()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)

        btnSearchRadius.setOnClickListener {
            searchWithinRadius()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        loadAllMarkers()
        getUserLocation()
    }

    private fun loadAllMarkers() {
        googleMap.clear()

        val adverts = dbHelper.getAllAdvertObjects()

        for (advert in adverts) {
            val position = LatLng(advert.latitude, advert.longitude)

            val markerColor = if (advert.postType == "Lost") {
                BitmapDescriptorFactory.HUE_RED
            } else {
                BitmapDescriptorFactory.HUE_GREEN
            }

            googleMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(advert.name)
                    .snippet("${advert.postType}\n${advert.location}")
                    .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
            )
        }

        if (adverts.isNotEmpty()) {
            val firstLocation = LatLng(adverts[0].latitude, adverts[0].longitude)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation, 12f))
        }
    }

    private fun getUserLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                200
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLatitude = location.latitude
                userLongitude = location.longitude
            }
        }
    }

    private fun searchWithinRadius() {
        val radiusText = etRadius.text.toString().trim()

        if (radiusText.isEmpty()) {
            Toast.makeText(this, "Enter radius in km", Toast.LENGTH_SHORT).show()
            return
        }

        if (userLatitude == 0.0 || userLongitude == 0.0) {
            Toast.makeText(this, "Current location not ready. Try again.", Toast.LENGTH_SHORT).show()
            getUserLocation()
            return
        }

        val radiusKm = radiusText.toDouble()
        val adverts = dbHelper.getAllAdvertObjects()

        googleMap.clear()

        var count = 0

        for (advert in adverts) {
            val results = FloatArray(1)

            Location.distanceBetween(
                userLatitude,
                userLongitude,
                advert.latitude,
                advert.longitude,
                results
            )

            val distanceKm = results[0] / 1000

            if (distanceKm <= radiusKm) {
                val position = LatLng(advert.latitude, advert.longitude)

                val markerColor = if (advert.postType == "Lost") {
                    BitmapDescriptorFactory.HUE_RED
                } else {
                    BitmapDescriptorFactory.HUE_GREEN
                }

                googleMap.addMarker(
                    MarkerOptions()
                        .position(position)
                        .title(advert.name)
                        .snippet("${advert.postType} - %.2f km away".format(distanceKm))
                        .icon(BitmapDescriptorFactory.defaultMarker(markerColor))
                )

                count++
            }
        }

        val userLocation = LatLng(userLatitude, userLongitude)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))

        Toast.makeText(this, "$count items found within $radiusKm km", Toast.LENGTH_SHORT).show()
    }
}