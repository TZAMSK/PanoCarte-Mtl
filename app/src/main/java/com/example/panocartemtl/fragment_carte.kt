package com.example.panocartemtl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
//import com.mapbox.maps.plugin.annotation.generated.Marker
//import com.mapbox.maps.plugin.annotation.generated.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.maps.plugin.annotation.annotations

class fragment_carte : Fragment() {
    private lateinit var popupLayout: View
    private lateinit var btnPostion: Button
    private lateinit var btnFavoris: Button
    private lateinit var popupBouton: Button
    private lateinit var navController: NavController
    private lateinit var mapView: MapView
    private lateinit var annotationPlugin: AnnotationPlugin

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // MapView initialization
        mapView = view.findViewById(R.id.map)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            annotationPlugin = mapView.annotations
            setupMap(style)
        }

        // Instancier les composantes
        popupLayout = view.findViewById(R.id.popupLayout)
        popupBouton = view.findViewById(R.id.popupBouton)
        btnPostion = view.findViewById(R.id.btnPositionActuelle)
        btnFavoris = view.findViewById(R.id.btnFavoris)

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.menu_navigation)

        // Cacher popup
        popupBouton.setOnClickListener {
            popupLayout.visibility = View.GONE
        }

        // Navigation menu
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_carte -> {
                    true
                }
                R.id.navigation_recherche -> {
                    true
                }
                R.id.navigation_favoris -> {
                    navController.navigate(R.id.action_fragment_carte_vers_fragment_favoris)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupMap(style: Style) {
        /*
        // Define coordinates
        val coordRosemont = com.mapbox.geojson.Point.fromLngLat(-73.5826029, 45.5571566)
        val coordInsectarium = com.mapbox.geojson.Point.fromLngLat(-73.554640, 45.561120)

        // Add markers to the map
        //val markerRosemont = annotationPlugin.createMarker(MarkerOptions().withLatLng(coordRosemont).withTitle("Position de Rosemont"))
        //val markerInsectarium = annotationPlugin.createMarker(MarkerOptions().withLatLng(coordInsectarium).withTitle("Position de l'Insectarium"))

        // Set initial camera position
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(coordInsectarium).zoom(13.0).build())

        // Marker click event
        annotationPlugin.addClickListener { marker ->
            if (marker == markerRosemont) {
                Toast.makeText(requireContext(), "Marqueur cliqué: ${marker.title}", Toast.LENGTH_SHORT).show()
                montrerPopup("Position de Rosemont", "6400 16e Avenue, Montréal, QC H1X 2S9")
            } else if (marker == markerInsectarium) {
                Toast.makeText(requireContext(), "Marqueur cliqué: ${marker.title}", Toast.LENGTH_SHORT).show()
                montrerPopup("Position de l'Insectarium", "4581 Sherbrooke St E, Montreal, QC H1X 2B2")
            }
            true
        }

        // Event for position button
        btnPostion.setOnClickListener {
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(coordRosemont).zoom(18.0).build())
        }

        // Event for favoris button
        btnFavoris.setOnClickListener {
            navController.navigate(R.id.action_fragment_carte_vers_fragment_favoris)
        }

         */
    }

    private fun montrerPopup(titre: String, description: String) {
        popupLayout.findViewById<TextView>(R.id.txtAdresse).text = description
        popupLayout.visibility = View.VISIBLE
    }
}
