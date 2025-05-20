//package com.example.to_do_app.ui.map
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.app.ActivityCompat
//import androidx.fragment.app.Fragment
//import androidx.navigation.fragment.findNavController
//import com.example.to_do_app.R
//import com.example.to_do_app.databinding.FragmentMapBinding
//import com.google.android.g ms.maps.CameraUpdateFactory
//import com.google.android.g ms.maps.MapView
//import com.google.android.g ms.maps.model.LatLng
//import com.google.android.g ms.maps.model.MarkerOptions
//
//class MapFragment : Fragment() {
//
//    private var _binding: FragmentMapBinding? = null
//    private val binding get() = _binding!!
//    private lateinit var mapView: MapView
//    private var selectedLocation: LatLng? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentMapBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        mapView = binding.mapView
//        mapView.onCreate(savedInstanceState)
//        mapView.onResume()
//
//        mapView.getMapAsync { googleMap ->
//            // Kiểm tra quyền truy cập vị trí
//            if (ActivityCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                    1
//                )
//                return@getMapAsync
//            }
//
//            googleMap.isMyLocationEnabled = true
//            val defaultLocation = LatLng(10.7769, 106.7009) // TP.HCM, Việt Nam
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15f))
//
//            googleMap.setOnMapClickListener { latLng ->
//                googleMap.clear()
//                googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
//                selectedLocation = latLng
//            }
//        }
//
//        binding.btnSelectLocation.setOnClickListener {
//            if (selectedLocation != null) {
//                val bundle = Bundle().apply {
//                    putDouble("latitude", selectedLocation!!.latitude)
//                    putDouble("longitude", selectedLocation!!.longitude)
//                }
//                findNavController().navigate(R.id.action_mapFragment_to_addTaskFragment, bundle)
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView.onDestroy()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}