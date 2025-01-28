package com.dadm.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.dadm.maps.ui.theme.MapsTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : ComponentActivity() {
    @SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }

        // Permitir operaciones de red en el hilo principal (no recomendado en producción)
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        setContent {
            MapsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    var userLocation by remember { mutableStateOf<LatLng?>(null) }
                    var places by remember { mutableStateOf(listOf<Place>()) }

                    val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
                    LaunchedEffect(Unit) {
                        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                userLocation = LatLng(location.latitude, location.longitude)
                                places = fetchNearbyPlaces(
                                    location.latitude,
                                    location.longitude,
                                    5000, // Radio en metros
                                    "hospital|tourist_attraction"
                                )
                            }
                        }
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        properties = MapProperties(isMyLocationEnabled = true),
                        uiSettings = MapUiSettings(zoomControlsEnabled = true)
                    ) {
                        userLocation?.let {
                            Marker(
                                state = MarkerState(position = it),
                                title = "You are here"
                            )
                        }

                        places.forEach { place ->
                            Marker(
                                state = MarkerState(position = LatLng(place.lat, place.lng)),
                                title = place.name
                            )
                        }
                    }
                }
            }
        }
    }

    private fun fetchNearbyPlaces(lat: Double, lng: Double, radius: Int, types: String): List<Place> {
        val apiKey = "AIzaSyA-RDEYgxI7kK21tuNitGp7yiviTlyvk0Q"
        val url =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$lat,$lng&radius=$radius&type=$types&key=$apiKey"
        val places = mutableListOf<Place>()

        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"

            val response = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)

            val results = jsonResponse.getJSONArray("results")
            for (i in 0 until results.length()) {
                val result = results.getJSONObject(i)
                val name = result.getString("name")
                val location = result.getJSONObject("geometry").getJSONObject("location")
                val placeLat = location.getDouble("lat")
                val placeLng = location.getDouble("lng")

                places.add(Place(name, placeLat, placeLng))
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return places
    }
}

data class Place(val name: String, val lat: Double, val lng: Double)
