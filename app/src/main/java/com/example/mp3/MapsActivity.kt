package com.example.mp3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps3)

        // Initialize the map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Fetch locations from Firestore
        fetchLocationsFromFirestore()
    }

    private fun fetchLocationsFromFirestore() {
        firestore.collection("locations")
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {  // Check if documents are not empty
                    var firstLatLng: LatLng? = null  // Variable to store the first LatLng

                    for (document in documents) {
                        val geoPoint = document.getGeoPoint("location") // Access the GeoPoint field
                        if (geoPoint != null) {
                            val latitude = geoPoint.latitude
                            val longitude = geoPoint.longitude
                            val locationName = document.getString("name") ?: "Unknown Location"

                            // Add marker for each location
                            val location = LatLng(latitude, longitude)
                            map.addMarker(
                                MarkerOptions()
                                    .position(location)
                                    .title(locationName)
                            )

                            // Set the first location to move the camera to it
                            if (firstLatLng == null) {
                                firstLatLng = location
                            }
                        }
                    }

                    // Move the camera to the first location (if available)
                    if (firstLatLng != null) {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLatLng, 10f))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore Error", "Error fetching locations: ", exception)
            }
    }

}