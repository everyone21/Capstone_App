package com.example.capstone

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMapClickListener {
    private var selectedLocation: LatLng? = null
    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST_CODE = 123
    private val REQUEST_CHECK_SETTINGS = 987
    private val USER_INPUTS_REQUEST_CODE = 456
    private val SELECT_LOCATION_REQUEST_CODE = 789

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val btnSaveLocation: Button = findViewById(R.id.btnSaveLocation)
        btnSaveLocation.setOnClickListener {
            saveLocationAndNavigateToFoodnBevPost()
        }

        checkLocationSettings()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(this)

        // Set a default location (e.g., city center)
        val defaultLocation = LatLng(16.420323121190634, 120.55681381000272) // Replace with your desired default coordinates
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12f))

        // You can also add a marker to the default location
        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Irisan Barangay Hall"))

        checkAndRequestLocationPermission()
    }

    override fun onMapClick(point: LatLng) {
        // Clear previous markers
        mMap.clear()

        // Add a marker at the clicked location
        mMap.addMarker(MarkerOptions().position(point).title("Selected Location"))

        // Save the selected location
        selectedLocation = point
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnCompleteListener { task ->
            try {
                val response = task.result!!
                // All location settings are satisfied. The client can initialize the location
                // requests here.
                checkAndRequestLocationPermission()
            } catch (exception: Exception) {
                if (exception is ResolvableApiException) {
                    try {
                        exception.startResolutionForResult(
                            this,
                            REQUEST_CHECK_SETTINGS
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
        }
    }

    private fun checkAndRequestLocationPermission() {
        // Check if location permission is granted
//        if (checkLocationPermission()) {
//            enableMyLocation()
//        } else {
//            // Request location permission from the user
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        }
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun checkLocationPermission(): Boolean {
        // Check if location permission is granted
        return ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        // Request location permission from the user
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                enableMyLocation()
            } else {
                // Permission denied
                Toast.makeText(
                    this,
                    "Location permission denied. Map functionality will be limited.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun enableMyLocation() {
        // Check if location permission is granted
        if (checkLocationPermission()) {
            // Enable the My Location layer if the permission has been granted.
            mMap.isMyLocationEnabled = true
        } else {
            // Permission was denied. Display an error message.
            Toast.makeText(
                this,
                "Location permission denied. Map functionality will be limited.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == USER_INPUTS_REQUEST_CODE && resultCode == RESULT_OK) {
            // Handle the result and retrieve any data, including user inputs
            val resultData = data?.extras
            if (resultData != null) {
                // Restore user inputs from resultData and handle them
                // For example:
                val userInput = resultData.getString("userInputKey")
                if (userInput != null) {
                    // Do something with the user input
                }
            }
        }
    }

    private fun saveLocationAndNavigateToFoodnBevPost() {
        if (selectedLocation != null) {
            // Save the selected location (you can save it to SharedPreferences, a database, etc.)
            // For demonstration purposes, let's use SharedPreferences
            val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("savedLatitude", selectedLocation?.latitude.toString())
            editor.putString("savedLongitude", selectedLocation?.longitude.toString())
            editor.apply()

            // Pass the location to FoodnBevPost
            val intent = Intent()
            intent.putExtra("latitude", selectedLocation?.latitude)
            intent.putExtra("longitude", selectedLocation?.longitude)
            setResult(SELECT_LOCATION_REQUEST_CODE, intent)
            finish()

        } else {
            Toast.makeText(
                this,
                "Please select a location on the map first.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}