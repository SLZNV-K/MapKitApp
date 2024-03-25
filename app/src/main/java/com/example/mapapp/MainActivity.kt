package com.example.mapapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.mapapp.databinding.ActivityMainBinding
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.Map

private const val MAP_KIT_API_KEY = "fbc1f7ff-db17-4e29-a99d-cf7a33086129"
private lateinit var map: Map
private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(MAP_KIT_API_KEY)

        MapKitFactory.initialize(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        map = binding.mapView.mapWindow.map

        map.move(
            CameraPosition(
                Point(55.751225, 37.629540),
                17.0f,
                0.0f,
                0.0f
            )
        )
        requestLocationPermissions()

        val userLocationLayer =
            MapKitFactory.getInstance().createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer.isVisible = true
        val userLocation = userLocationLayer.cameraPosition()?.target

        with(binding) {

            zoomIn.setOnClickListener {
                zoomIn()
            }

            zoomOut.setOnClickListener {
                zoomOut()
            }

            location.setOnClickListener {
                searchLocation(userLocation)
            }
        }
    }

    private fun zoomIn() {
        val currentCameraPosition = map.cameraPosition
        map.move(
            CameraPosition(
                Point(
                    currentCameraPosition.target.latitude,
                    currentCameraPosition.target.longitude
                ),
                currentCameraPosition.zoom + 1.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.SMOOTH, 1.0f),
            null
        )
    }

    private fun zoomOut() {
        val currentCameraPosition = map.cameraPosition
        map.move(
            CameraPosition(
                Point(
                    currentCameraPosition.target.latitude,
                    currentCameraPosition.target.longitude
                ),
                currentCameraPosition.zoom - 1.0f,
                0.0f,
                0.0f
            ),
            Animation(Animation.Type.SMOOTH, 1.0f),
            null
        )
    }

    private fun searchLocation(location: Point?) {
        if (location != null) {
            map.move(
                CameraPosition(location, 17.0f, 0.0f, 0.0f),
                Animation(Animation.Type.SMOOTH, 3.0f),
                null
            )
        } else {
            Toast.makeText(this, "There is no access to the location", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ), 0
            )
            return
        }
    }

    override fun onStart() {
        binding.mapView.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()

    }
}