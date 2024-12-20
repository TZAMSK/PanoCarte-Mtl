package com.example.panocartemtl.carte.PrésentateurGestionnaire

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.core.app.ActivityCompat
import com.example.panocartemtl.carte.InterfaceCarte.MapboxInterface
import com.example.panocartemtl.carte.VueCarte
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.utils.ColorUtils
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class GestionMapbox( var vue: VueCarte, val iocontext: CoroutineContext = Dispatchers.IO,
                    val markerMap: MutableMap<PointAnnotation, Int> ): MapboxInterface {

    private val gestionAPI = GestionIPA( vue, iocontext, markerMap )

    // Écrit grâce à l'example du Mapbox - «Cluster points within a layer»
    // Source: https://docs.mapbox.com/android/maps/examples/android-view/location-component-animation/
    override fun dessinerCercle( position: Point  ) {
        val mapboxMap = vue.mapView.getMapboxMap()

        // Si rayon pas encore procuré ou l'utilisateur veut pas
        var rayon = try {
            vue.txtRayon.text.toString().toInt()
        } catch ( e: NumberFormatException ) {
            0
        }

        val geoJsonSource = geoJsonSource("circle-source") {
            geometry( position )
        }

        if ( rayon > 0) {
            mapboxMap.getStyle { style ->
                // Erreur code: le cerle existe déja. Alors j'ajouté si le cercle existe, on l'efface avant de permettre de recliqué le bouton rayon
                if (style.getLayer( "circle-layer" ) != null) {
                    style.removeStyleLayer( "circle-layer" )
                }
                if (style.getSource( "circle-source" ) != null) {
                    style.removeStyleSource( "circle-source" )
                }

                style.addSource( geoJsonSource )
                style.addLayer(
                    circleLayer( "circle-layer", "circle-source" ) {
                        circleColor( ColorUtils.colorToRgbaString( Color.BLUE ) )
                        circleRadius( rayon.toDouble() )
                        circleOpacity( 0.2 )
                    }
                )
            }
        }

        gestionAPI.afficherStationnementsRayon( position, rayon.toString() )
    }

    override fun dessinerCercleDepuisPositionActuelle() {
        if ( ActivityCompat.checkSelfPermission(
                vue.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Ressemble à btnPostion.setOnClickListener mais avec position actuelle
            vue.positionClient.lastLocation.addOnSuccessListener { position: Location? ->
                if ( position != null ) {
                    val positionActuelle = Point.fromLngLat( position.longitude, position.latitude )
                    dessinerCercle( Point.fromLngLat( positionActuelle.longitude(), positionActuelle.latitude() )  )
                }
            }
        } else {
            gestionAPI.requestPermissionLauncher.launch( Manifest.permission.ACCESS_FINE_LOCATION )
        }
    }

    override fun afficherPostionActuelle() {
        if ( ActivityCompat.checkSelfPermission(
                vue.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            gestionAPI.getPositionActuelle()
        } else {
            gestionAPI.requestPermissionLauncher.launch( Manifest.permission.ACCESS_FINE_LOCATION )
        }
    }
}