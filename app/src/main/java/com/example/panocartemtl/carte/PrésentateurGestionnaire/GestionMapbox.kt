package com.example.panocartemtl.carte.PrésentateurGestionnaire

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.panocartemtl.R
import com.example.panocartemtl.carte.InterfaceCarte.MapboxInterface
import com.example.panocartemtl.carte.VueCarte
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.utils.ColorUtils
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import kotlinx.coroutines.Dispatchers
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class GestionMapbox( var vue: VueCarte, val iocontext: CoroutineContext = Dispatchers.IO,
                    val markerMap: MutableMap<PointAnnotation, Int>, var destinationChoisie: Point? = null ): MapboxInterface {

    private val gestionAPI = GestionAPI( vue, iocontext, markerMap )

    // Position actuelle

    // Écrit grâce à la documentation officiel de Mapbox - «User Location»
    // Source: https://docs.mapbox.com/android/maps/guides/user-location/location-on-map/

    // Aussissous «Request permissions»
    // Source: https://developer.android.com/training/permissions/requesting
    val requestPermissionLauncher =
        vue.registerForActivityResult( ActivityResultContracts.RequestPermission() ) { permis: Boolean ->
            if ( permis == true ) {
                getPositionActuelle()
            } else {
                Toast.makeText( vue.requireContext(),
                    R.string.autorisation_de_la_position_est_requise, Toast.LENGTH_SHORT ).show()
            }
        }

    override fun navigationEntrePostion( à_partir: Point ) {
        // Source: https://docs.mapbox.com/help/tutorials/getting-started-directions-api/
        // Sous: Parameters
        // Clé
        val accessToken = "sk.eyJ1IjoidHphbXNrIiwiYSI6ImNtM2tzbGJtczBraHAyaXB2NmlpejlzMnMifQ.JAE5ZyxpPo4Y-n4FlaIuUg"
        // Url de l'api qui nous permet de dessiner une ligne
        // Source pour les ${profile}: https://docs.mapbox.com/help/glossary/routing-profile/
        // Url contient «walking» pour le ${profile} de navigation il y a driving, cycling et driving-traffic
        val url = "https://api.mapbox.com/directions/v5/mapbox/walking/${à_partir.longitude()},${à_partir.latitude()};${destinationChoisie?.longitude()},${destinationChoisie?.latitude()}?geometries=geojson&access_token=$accessToken"


        // Source: Copier coller de la documentation OkHttp
        // https://square.github.io/okhttp/recipes/ sous «Asynchronous Get (.kt, .java)»
        val request = Request.Builder().url( url ).build()

        val client = OkHttpClient()

        client.newCall( request ).enqueue( object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse( call: Call, response: Response ) {
                if ( response.isSuccessful ) {
                    val responseBody = response.body?.string()
                    if ( responseBody != null ) {
                        parseRouteRéponse( responseBody )
                    }
                }
            }
        } )
    }

    // Écrit grâce à l'example du Mapbox - «Cluster points within a layer»
    // Source: https://docs.mapbox.com/android/maps/examples/android-view/location-component-animation/
    override fun dessinerCercle( position: Point  ) {
        val mapboxMap = vue.mapView.getMapboxMap()

        // Si rayon pas encore procuré ou l'utilisateur veut pas
        var rayon = try {
            vue.txtRayon.text.toString().toInt()
        } catch ( e: NumberFormatException ) {
            Toast.makeText( vue.requireContext(), R.string.rayon_indéterminé, Toast.LENGTH_SHORT ).show()
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

    override fun getPositionActuelle() {
        if ( ActivityCompat.checkSelfPermission( vue.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED ) {
            vue.positionClient.lastLocation.addOnSuccessListener { position: Location? ->
                if ( position != null ) {
                    val positionActuelle = Point.fromLngLat( position.longitude, position.latitude )

                    vue.mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center( positionActuelle )
                            .zoom( 14.97 )
                            .build()
                    )
                }
            }
        } else {
            Toast.makeText( vue.requireContext(),
                R.string.autorisation_de_la_position_actuelle_n_a_pas_été_accordée, Toast.LENGTH_SHORT ).show()
            requestPermissionLauncher.launch( Manifest.permission.ACCESS_FINE_LOCATION )
        }
    }

    override fun dessinerNavigationEntrePostion() {
        if ( ActivityCompat.checkSelfPermission(
                vue.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ) {
            vue.positionClient.lastLocation.addOnSuccessListener { position: Location? ->
                if ( position != null ) {
                    val positionActuelle = Point.fromLngLat( position.longitude, position.latitude )
                    navigationEntrePostion( positionActuelle )
                }
            }
        } else {
            requestPermissionLauncher.launch( Manifest.permission.ACCESS_FINE_LOCATION )
        }
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
                    vue.présentateur.dessinerCercle( Point.fromLngLat( positionActuelle.longitude(), positionActuelle.latitude() )  )
                }
            }
        } else {
            requestPermissionLauncher.launch( Manifest.permission.ACCESS_FINE_LOCATION )
        }
    }

    override fun afficherPostionActuelle() {
        if ( ActivityCompat.checkSelfPermission(
                vue.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getPositionActuelle()
        } else {
            requestPermissionLauncher.launch( Manifest.permission.ACCESS_FINE_LOCATION )
        }
    }

    private fun parseRouteRéponse( responseBody: String ) {
        // On trouve le json dans: https://docs.mapbox.com/help/tutorials/getting-started-directions-api/
        // Sous: Review the response

        val json = JSONObject( responseBody )
        // Route commence à index zéro
        val route = json.getJSONArray( "routes" ).getJSONObject( 0 )
        val geometry = route.getJSONObject( "geometry" )
        val coordinates = geometry.getJSONArray( "coordinates" )

        // Les points qui permet de dessiner la ligne (ils se relient en genre de vecteurs?)
        val routePoints = ArrayList<Point>()
        for ( i in 0 until coordinates.length() ) {
            val coord = coordinates.getJSONArray( i )
            val point = Point.fromLngLat( coord.getDouble( 0 ), coord.getDouble( 1 ) )
            routePoints.add( point )
        }

        dessinerRoute( routePoints )
    }

    // Ressemble exactement à dessinerCercleAutourPostion
    private fun dessinerRoute( routePoints: List<Point> ) {
        vue.mapView.getMapboxMap().getStyle { style ->

            val geoJsonSource = geoJsonSource( "route-source" ) {
                geometry( LineString.fromLngLats( routePoints ) )
            }

            // Comme dans dessinerCercleAutourPostion
            // Erreur code: la lign existe déja. Alors j'ajouté si la ligne existe, on l'efface avant de permettre de recliqué le bouton destination
            if (style.getLayer( "route-layer" ) != null ) {
                style.removeStyleLayer( "route-layer" )
            }
            if (style.getSource( "route-source" ) != null) {
                style.removeStyleSource( "route-source" )
            }

            style.addSource( geoJsonSource )

            style.addLayer(
                lineLayer( "route-layer", "route-source" ) {
                    lineColor( ColorUtils.colorToRgbaString( Color.RED ) )
                    lineWidth( 5.0 )
                }
            )
        }
    }
}