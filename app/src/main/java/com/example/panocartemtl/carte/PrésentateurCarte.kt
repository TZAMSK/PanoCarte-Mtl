package com.example.panocartemtl.carte

import android.util.Log
import android.widget.Toast
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener

class PrésentateurCarte(var vue: VueCarte, val iocontext: CoroutineContext = Dispatchers.IO ): IPrésentateurCarte {
    private var destinationChoisie: Point? = null
    private val markerMap: MutableMap<PointAnnotation, Int> = mutableMapOf()

    val modèle = Modèle.instance

    fun initializeMap(mapView: MapView) {
        vue.mapView = mapView
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            setupMap(style)
        }
    }

    private fun setupMap(style: Style) {
        val annotationPlugin = vue.mapView.annotations
        vue.pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
        caméraPremièreInstance()
        setupMarkerClickListener()
    }

    override fun caméraPremièreInstance() {
        vue.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().center(Point.fromLngLat(-73.554640, 45.561120)).zoom(13.0).build()
        )
    }

    private fun setupMarkerClickListener() {
        vue.pointAnnotationManager.addClickListener { pointAnnotation ->
            afficherStationnementParId()
            true
        }
    }

    override fun recupérerTousStationnements() {
        CoroutineScope(iocontext).launch {
            val listeStationnements = modèle.obtenirTousStationnements()
            withContext(Dispatchers.Main) {
                for (stationnement in listeStationnements) {
                    val nouveauPoint = PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(stationnement.coordonnée.longitude, stationnement.coordonnée.latitude))
                        .withIconImage("marqueur_rouge")
                        .withIconAnchor(IconAnchor.BOTTOM)
                        .withIconSize(0.6)

                    val point = vue.pointAnnotationManager.create(nouveauPoint)
                    markerMap[point] = stationnement.id
                }
            }
        }
    }

    override fun afficherStationnementParId() {
        vue.pointAnnotationManager.addClickListener(object : OnPointAnnotationClickListener {
            override fun onAnnotationClick(pointAnnotation: PointAnnotation): Boolean {
                val marqueurId = markerMap[pointAnnotation]?.toInt()

                // Check if the ID is valid
                if (marqueurId != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val stationnement = withContext(Dispatchers.IO) {
                            modèle.obtenirStationnementParId(marqueurId)
                        }

                        // Set destination and display details
                        destinationChoisie = Point.fromLngLat(
                            stationnement.coordonnée.longitude,
                            stationnement.coordonnée.latitude
                        )

                        Toast.makeText(
                            vue.requireContext(),
                            vue.getString(R.string.marqueur_cliqué),
                            Toast.LENGTH_SHORT
                        ).show()

                        vue.montrerPopup(
                            "${stationnement.adresse.numero_municipal} ${stationnement.adresse.rue} ${stationnement.adresse.code_postal}"
                        )
                    }
                }
                return true
            }
        })
    }
}

