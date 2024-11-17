package com.example.panocartemtl

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.utils.ColorUtils
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlin.random.Random

class fragment_carte : Fragment() {
    private lateinit var popupLayout: View
    private lateinit var btnPostion: Button
    private lateinit var txtRayon: EditText
    private lateinit var btnRayon: ImageView
    private lateinit var popupBouton: Button
    private lateinit var navController: NavController
    private lateinit var mapView: MapView
    private lateinit var annotationPlugin: AnnotationPlugin
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var positionClient: FusedLocationProviderClient

    private val markerMap: MutableMap<PointAnnotation, String> = mutableMapOf()

    // Position actuelle

    // Écrit grâce à la documentation officiel de Mapbox - «User Location»
    // Source: https://docs.mapbox.com/android/maps/guides/user-location/location-on-map/

    // Aussissous «Request permissions»
    // Source: https://developer.android.com/training/permissions/requesting
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permis: Boolean ->
            if (permis == true) {
                getPositionActuelle()
            } else {
                Toast.makeText(requireContext(), R.string.autorisation_de_la_position_est_requise, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.map)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            annotationPlugin = mapView.annotations
            pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
            setupMap(style)
        }

        positionClient = LocationServices.getFusedLocationProviderClient(requireContext())

        navController = findNavController()

        // Instancier les composantes
        popupLayout = view.findViewById(R.id.popupLayout)
        popupBouton = view.findViewById(R.id.popupBouton)
        btnPostion = view.findViewById(R.id.btnPositionActuelle)
        txtRayon = view.findViewById(R.id.txtRayon)
        btnRayon = view.findViewById(R.id.btnRayon)

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

        // Position actuelle
        btnPostion.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getPositionActuelle()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        // Rayon cercle
        btnRayon.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Ressemble à btnPostion.setOnClickListener mais avec position actuelle
                positionClient.lastLocation.addOnSuccessListener { position: Location? ->
                    if (position != null) {
                        val positionActuelle = Point.fromLngLat(position.longitude, position.latitude)
                        dessinerCercleAutourPostion(positionActuelle)
                    }
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

    }

    private fun getPositionActuelle() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            positionClient.lastLocation.addOnSuccessListener { position: Location? ->
                if (position != null) {
                    val positionActuelle = Point.fromLngLat(position.longitude, position.latitude)

                    mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(positionActuelle)
                            .zoom(18.0)
                            .build()
                    )
                }
            }
        } else {
            Toast.makeText(requireContext(), R.string.autorisation_de_la_position_actuelle_n_a_pas_été_accordée, Toast.LENGTH_SHORT).show()
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }


    // Écrit grâce à l'example du Mapbox - «Cluster points within a layer»
    // Source: https://docs.mapbox.com/android/maps/examples/android-view/location-component-animation/
    private fun dessinerCercleAutourPostion(position: Point) {
        val mapboxMap = mapView.getMapboxMap()

        // Si rayon pas encore procuré ou l'utilisateur veut pas
        var rayon = try {
            txtRayon.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), R.string.rayon_indéterminé, Toast.LENGTH_SHORT).show()
            0.0
        }

        val geoJsonSource = geoJsonSource("circle-source") {
            geometry(position)
        }

        mapboxMap.getStyle { style ->
            // Erreur code: le cerle existe déja. Alors j'ajouté si le cercle existe, on l'efface avant de permettre de recliqué le bouton rayon
            if (style.getLayer("circle-layer") != null) {
                style.removeStyleLayer("circle-layer")
            }
            if (style.getSource("circle-source") != null) {
                style.removeStyleSource("circle-source")
            }

            // Pour qu'il n'y a pas de nouveau 5 marqueurs à chaque fois qu'on clique bouton rayon
            pointAnnotationManager.deleteAll()

            style.addSource(geoJsonSource)
            style.addLayer(
                circleLayer("circle-layer", "circle-source") {
                    circleColor(ColorUtils.colorToRgbaString(Color.BLUE))
                    circleRadius(rayon)
                    circleOpacity(0.2)
                }
            )
        }

        for(i in 0 until 5) {
            val longHazard = Random.nextDouble(-0.001,0.002)
            val latHazard = Random.nextDouble(-0.001,0.002)

            val coordRandom = Point.fromLngLat(position.longitude() + longHazard, position.latitude() + latHazard)

            val insectarium = PointAnnotationOptions()
                .withPoint(coordRandom)
                .withIconImage("marqueur_rouge")
                .withIconAnchor(IconAnchor.BOTTOM)
                .withIconSize(0.3)

            pointAnnotationManager.create(insectarium)
        }
    }

    // Écrit grâce à la documentation officiel de Mapbox - «Markers and annotations»
    // Source: https://docs.mapbox.com/android/maps/guides/annotations/annotations/
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
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().center(coordInsectarium).zoom(13.0).build()
        )

        // Cliquer sur les marqueurs
        // Écrit grâce à l'exemple de Mapbox - «Add Point Annotations»
        // Source: https://docs.mapbox.com/android/maps/examples/add-point-annotations/
        pointAnnotationManager.addClickListener(object : OnPointAnnotationClickListener {
            override fun onAnnotationClick(pointAnnotation: PointAnnotation): Boolean {
                val marqueurId = markerMap[pointAnnotation]
                when (marqueurId) {
                    "rosemont" -> {
                        Toast.makeText(requireContext(),"${getString(R.string.marqueur_cliqué)}: Position Rosemont",Toast.LENGTH_SHORT).show()
                        montrerPopup("Position de Rosemont", "6400 16e Avenue, Montréal, QC H1X 2S9")
                    }
                    "insectarium" -> {
                        Toast.makeText(requireContext(), "${getString(R.string.marqueur_cliqué)}: Position Insectarium", Toast.LENGTH_SHORT).show()
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
