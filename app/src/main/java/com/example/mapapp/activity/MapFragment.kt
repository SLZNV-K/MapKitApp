package com.example.mapapp.activity

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mapapp.R
import com.example.mapapp.databinding.FragmentMapBinding
import com.example.mapapp.viewmodel.PointViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.MapObjectTapListener
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.runtime.image.ImageProvider


class MapFragment : Fragment() {
    private lateinit var map: Map
    private lateinit var binding: FragmentMapBinding
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var placeMarkMapObject: PlacemarkMapObject

    private val viewModel: PointViewModel by activityViewModels()

    private val mapObjectTapListener = MapObjectTapListener { _, selectedPoint ->
        val editText = EditText(requireActivity())
        AlertDialog.Builder(requireActivity()).apply {
            setTitle(getString(R.string.saving_a_point))
            setMessage(getString(R.string.do_you_want_to_save_this_location))
            setView(editText)
            setPositiveButton(getString(R.string.add)) { _, _ ->
                val userInput = editText.text.toString()
                viewModel.changeName(userInput)
                viewModel.changeLocation(selectedPoint)
                viewModel.save()
                Toast.makeText(
                    requireContext(),
                    getString(R.string.the_point_has_been_successfully_added_to_favorites),
                    Toast.LENGTH_LONG
                ).show()
            }
            setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
                removeMarker()
            }
            setCancelable(true)
        }.create().show()
        true
    }

    private val geoObjectTapListener =
        GeoObjectTapListener { geoObjectTapEvent ->
            val selectionMetadata: GeoObjectSelectionMetadata = geoObjectTapEvent
                .geoObject
                .metadataContainer
                .getItem(GeoObjectSelectionMetadata::class.java)
            binding.mapView.mapWindow.map.selectGeoObject(
                selectionMetadata,
            )
            val point = geoObjectTapEvent.geoObject.geometry[0].point
            setMarkerInLocation(point)
            false
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMapBinding.inflate(layoutInflater)
        map = binding.mapView.mapWindow.map
//          TODO(): Отображение выбранной метки на карте
//        val latitude = arguments?.getString(ListPointsFragment.LATITUDE)?.toDouble()
//        val longitude = arguments?.getString(ListPointsFragment.LONGITUDE)?.toDouble()
//        if (latitude != null && longitude != null) {
//            val userSelectedFavPoint = Point(latitude, longitude)
//            startLocation(userSelectedFavPoint)
//        }


        val userLocationLayer =
            MapKitFactory.getInstance().createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer.isVisible = true
        val userLocation = userLocationLayer.cameraPosition()?.target
        println("USER LOCATION: $userLocation")
        startLocation(userLocation)

        map.addTapListener(geoObjectTapListener)

        with(binding) {
            zoomIn.setOnClickListener {
                zoomInAndOut(Zoom.IN)
            }

            zoomOut.setOnClickListener {
                zoomInAndOut(Zoom.OUT)
            }

            location.setOnClickListener {
                searchLocation(userLocation)
            }

            pointList.setOnClickListener {
                findNavController().navigate(R.id.action_mapFragment_to_pointFragment)
            }
        }

        return binding.root
    }

    private fun searchLocation(location: Point?) {
        if (location != null) {
            map.move(
                CameraPosition(location, START_ZOOM, START_AZIMUTH, START_TILT),
                Animation(Animation.Type.SMOOTH, 3.0f),
                null
            )
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.there_is_no_access_to_the_location), Toast.LENGTH_LONG
            ).show()
        }
        setMarkerInLocation(location)
    }

    private fun startLocation(location: Point?) {
        val startLocation = location ?: Point(55.7520233, 37.6174994)
        map.move(
            CameraPosition(
                startLocation,
                START_ZOOM,
                START_AZIMUTH,
                START_TILT
            )
        )
        setMarkerInLocation(startLocation)
    }

    private fun setMarkerInLocation(location: Point?) {
        val marker = createBitmapFromVector(R.drawable.ic_location_on)
        if (location != null) {
            mapObjectCollection =
                binding.mapView.mapWindow.map.mapObjects
            placeMarkMapObject = mapObjectCollection.addPlacemark(
                location,
                ImageProvider.fromBitmap(marker)
            )
            placeMarkMapObject.opacity = 0.5f
            placeMarkMapObject.addTapListener(mapObjectTapListener)
        } else Toast.makeText(
            requireContext(),
            getString(R.string.location_could_not_be_determined),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun removeMarker() {
        if (::placeMarkMapObject.isInitialized) {
            mapObjectCollection.remove(placeMarkMapObject)
        }
    }

    private fun createBitmapFromVector(art: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(requireContext(), art) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun zoomInAndOut(zooming: Zoom) {
        val zoom = when (zooming) {
            Zoom.IN -> 1.0f
            Zoom.OUT -> -1.0f
        }
        val currentCameraPosition = map.cameraPosition
        map.move(
            CameraPosition(
                Point(
                    currentCameraPosition.target.latitude,
                    currentCameraPosition.target.longitude
                ),
                currentCameraPosition.zoom + zoom,
                START_AZIMUTH,
                START_TILT
            ),
            Animation(Animation.Type.SMOOTH, 1.0f),
            null
        )
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        MapKitFactory.getInstance().onStop()
        binding.mapView.onStop()
    }

    companion object {

        const val START_AZIMUTH = 0.0f
        const val START_TILT = 0.0f
        const val START_ZOOM = 17.0f

        enum class Zoom {
            IN, OUT
        }
    }
}