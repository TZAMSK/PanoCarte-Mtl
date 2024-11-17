package com.example.panocartemtl

import android.graphics.BitmapFactory
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
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.geojson.Point
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class fragment_carte : Fragment() {
    private lateinit var popupLayout: View
    private lateinit var btnPostion: Button
    private lateinit var btnFavoris: Button
    private lateinit var popupBouton: Button
    private lateinit var navController: NavController
    private lateinit var mapView: MapView
    private lateinit var annotationPlugin: AnnotationPlugin
    private lateinit var pointAnnotationManager: PointAnnotationManager

    private val markerMap: MutableMap<PointAnnotation, String> = mutableMapOf()

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
            pointAnnotationManager = annotationPlugin.createPointAnnotationManager() // Correctly initialize PointAnnotationManager
            setupMap(style)
        }

        navController = findNavController()

        // Instancier les composantes
        popupLayout = view.findViewById(R.id.popupLayout)
        popupBouton = view.findViewById(R.id.popupBouton)
        btnPostion = view.findViewById(R.id.btnPositionActuelle)
        btnFavoris = view.findViewById(R.id.btnFavoris)

        val menuView = requireActivity().findViewById<BottomNavigationView>(R.id.menu_navigation)

        // Cacher popup
        popupBouton.setOnClickListener {
            popupLayout.visibility = View.GONE
        }

        // Navigation menu
        menuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_carte -> true
                R.id.navigation_recherche -> true
                R.id.navigation_favoris -> {
                    navController.navigate(R.id.action_fragment_carte_vers_fragment_favoris)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupMap(style: Style) {

        style.addImage("marqueur_rouge", BitmapFactory.decodeResource(resources, R.drawable.marqueur_rouge))

        // Rosemont
        val coordRosemont = Point.fromLngLat(-73.5826029, 45.5571566)

        val rosemont = PointAnnotationOptions()
            .withPoint(coordRosemont)
            .withIconImage("marqueur_rouge")
            .withIconAnchor(IconAnchor.BOTTOM)
            .withIconSize(0.6)

        val rosemontAnnotation = pointAnnotationManager.create(rosemont)
        markerMap[rosemontAnnotation] = "rosemont"

        // Insectarium
        val coordInsectarium = Point.fromLngLat(-73.554640, 45.561120)

        val insectarium = PointAnnotationOptions()
            .withPoint(coordInsectarium)
            .withIconImage("marqueur_rouge")
            .withIconAnchor(IconAnchor.BOTTOM)
            .withIconSize(0.6)

        val insectariumAnnotation = pointAnnotationManager.create(insectarium)
        markerMap[insectariumAnnotation] = "insectarium"

        // Première vue
        mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(coordInsectarium).zoom(13.0).build())

        // Boutons dans la vue carte
        btnPostion.setOnClickListener {
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(coordRosemont).zoom(18.0).build())
        }

        btnFavoris.setOnClickListener {
            navController.navigate(R.id.action_fragment_carte_vers_fragment_favoris)
        }

        // Cliquer sur les marqueurs
        pointAnnotationManager.addClickListener(object : OnPointAnnotationClickListener {
            override fun onAnnotationClick(pointAnnotation: PointAnnotation): Boolean {
                val marqueurId = markerMap[pointAnnotation] // Get the marker ID from the map
                when (marqueurId) {
                    "rosemont" -> {
                        Toast.makeText(requireContext(), "Marqueur cliqué: Position de Rosemont", Toast.LENGTH_SHORT).show()
                        montrerPopup("Position de Rosemont", "6400 16e Avenue, Montréal, QC H1X 2S9")
                    }
                    "insectarium" -> {
                        Toast.makeText(requireContext(), "Marqueur cliqué: Position de l'Insectarium", Toast.LENGTH_SHORT).show()
                        montrerPopup("Position de l'Insectarium", "4581 Sherbrooke St E, Montreal, QC H1X 2B2")
                    }
                }
                return true
            }
        })
    }


    private fun montrerPopup(titre: String, description: String) {
        popupLayout.findViewById<TextView>(R.id.txtAdresse).text = description
        popupLayout.visibility = View.VISIBLE
    }
}
