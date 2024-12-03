package com.example.panocartemtl.carte

import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.R
import com.mapbox.geojson.LineString
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
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.utils.ColorUtils
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.logging.SimpleFormatter
import kotlin.random.Random

class PrésentateurCarte( var vue: VueCarte, val iocontext: CoroutineContext = Dispatchers.IO ): IPrésentateurCarte {
    private var destinationChoisie: Point? = null
    private val markerMap: MutableMap<PointAnnotation, Int> = mutableMapOf()

    val modèle = Modèle.instance


    override fun caméraPremièreInstance() {
        vue.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().center( Point.fromLngLat( -73.554640, 45.561120 ) ).zoom( 13.0 ).build()
        )
    }

    override fun détruireTousMarqueurs() {
        for ( marker in markerMap.keys ) {
            vue.pointAnnotationManager.delete( marker )
        }
        markerMap.clear()
    }

    override fun recupérerTousStationnements() {
        CoroutineScope( iocontext ).launch {
            val listeStationnements = modèle.obtenirTousStationnements()
            withContext( Dispatchers.Main ) {
                for ( stationnement in listeStationnements ) {
                    val nouveauPoint = PointAnnotationOptions()
                        .withPoint( Point.fromLngLat( stationnement.coordonnée.longitude, stationnement.coordonnée.latitude ) )
                        .withIconImage( "marqueur_rouge" )
                        .withIconAnchor( IconAnchor.BOTTOM )
                        .withIconSize( 0.6 )

                    val point = vue.pointAnnotationManager.create( nouveauPoint )
                    markerMap[point] = stationnement.id
                }
            }
        }
    }

    override fun afficherStationnementParId() {
        vue.pointAnnotationManager.addClickListener(object : OnPointAnnotationClickListener {
            override fun onAnnotationClick( pointAnnotation: PointAnnotation ): Boolean {
                val marqueurId = markerMap[pointAnnotation]?.toInt()

                if ( marqueurId != null ) {
                    CoroutineScope( Dispatchers.Main ).launch {
                        val stationnement = withContext( iocontext ) {
                            modèle.obtenirStationnementParId( marqueurId )
                        }

                        destinationChoisie = Point.fromLngLat(
                            stationnement.coordonnée.longitude,
                            stationnement.coordonnée.latitude
                        )

                        Toast.makeText(
                            vue.requireContext(),
                            vue.getString( R.string.marqueur_cliqué ),
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

        client.newCall( request ).enqueue(object : Callback {
            override fun onFailure( call: Call, e: IOException ) {
                e.printStackTrace()
            }
            override fun onResponse( call: Call, response: Response ) {
                if ( response.isSuccessful ) {
                    val responseBody = response.body?.string()
                    if ( responseBody != null ) {
                        parseRouteResponse( responseBody )
                    }
                }
            }
        })
    }

    private fun parseRouteResponse( responseBody: String ) {
        // On trouve le json dans: https://docs.mapbox.com/help/tutorials/getting-started-directions-api/
        // Sous: Review the response

        val json = JSONObject( responseBody )
        // Route commence à index zéro
        val route = json.getJSONArray("routes").getJSONObject(0)
        val geometry = route.getJSONObject("geometry")
        val coordinates = geometry.getJSONArray("coordinates")

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

            val geoJsonSource = geoJsonSource("route-source") {
                geometry( LineString.fromLngLats( routePoints ) )
            }

            // Comme dans dessinerCercleAutourPostion
            // Erreur code: la lign existe déja. Alors j'ajouté si la ligne existe, on l'efface avant de permettre de recliqué le bouton destination
            if (style.getLayer( "route-layer" ) != null ) {
                style.removeStyleLayer( "route-layer" )
            }
            if (style.getSource("route-source") != null) {
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

    fun vérifierBoutonsHeureRempli(): Boolean {
        return vue.btnChoisirHeureDébut.text != vue.getString( R.string.début ) && vue.btnChoisirHeurePrévu.text != vue.getString( R.string.prévu )
    }

    // Pour la montre, code copier coller de ce tutoriel
    // Source: https://www.youtube.com/watch?v=BLmFrR13-bs
    fun montrerMontreDébut() {
        val calendrier = Calendar.getInstance()
        val heureListener = TimePickerDialog.OnTimeSetListener{ heureChoix, heure, minute ->
            calendrier.set( Calendar.HOUR_OF_DAY, heure )
            calendrier.set( Calendar.MINUTE, minute )
            vue.btnChoisirHeureDébut.text = SimpleDateFormat( "HH:mm" ).format( calendrier.time )
        }

        TimePickerDialog( vue.requireContext(), heureListener, calendrier.get( Calendar.HOUR_OF_DAY ), calendrier.get( Calendar.MINUTE ), true).show()
    }

    fun montrerMontrePrévu() {
        val calendrier = Calendar.getInstance()
        val heureListener = TimePickerDialog.OnTimeSetListener{ heureChoix, heure, minute ->
            calendrier.set( Calendar.HOUR_OF_DAY, heure )
            calendrier.set( Calendar.MINUTE, minute )
            vue.btnChoisirHeurePrévu.text = SimpleDateFormat( "HH:mm" ).format( calendrier.time )
        }

        TimePickerDialog( vue.requireContext(), heureListener, calendrier.get( Calendar.HOUR_OF_DAY ), calendrier.get( Calendar.MINUTE ), true).show()
    }

    override fun afficherStationnementsParHeure() {
        if ( vérifierBoutonsHeureRempli() == true ) {
            CoroutineScope( iocontext ).launch {
                val début = vue.btnChoisirHeureDébut.text.toString()
                val prévu = vue.btnChoisirHeurePrévu.text.toString()
                val listeStationnementsHeure = modèle.obtenirStationnementsParHeuresDisponibles(début, prévu)

                withContext ( Dispatchers.Main ) {
                    for ( stationnement in listeStationnementsHeure ) {
                        val nouveauPoint = PointAnnotationOptions()
                            .withPoint( Point.fromLngLat( stationnement.coordonnée.longitude, stationnement.coordonnée.latitude ) )
                            .withIconImage( "marqueur_rouge" )
                            .withIconAnchor( IconAnchor.BOTTOM )
                            .withIconSize( 0.6 )

                        val point = vue.pointAnnotationManager.create( nouveauPoint )
                        markerMap[point] = stationnement.id
                    }
                }
            }
        }
    }

    override fun afficherStationnementsRayon( position: Point, rayon: String ) {
        CoroutineScope( iocontext ).launch {
            val listeStationnementsRayon = modèle.obtenirStationnementsRayon( position.longitude(), position.latitude(), rayon )

            withContext ( Dispatchers.Main ) {
                for ( stationnement in listeStationnementsRayon ) {
                    val nouveauPoint = PointAnnotationOptions()
                        .withPoint( Point.fromLngLat( stationnement.coordonnée.longitude, stationnement.coordonnée.latitude ) )
                        .withIconImage( "marqueur_rouge" )
                        .withIconAnchor( IconAnchor.BOTTOM )
                        .withIconSize( 0.6 )

                    val point = vue.pointAnnotationManager.create( nouveauPoint )
                    markerMap[point] = stationnement.id
                }
            }
        }
    }

    // Écrit grâce à l'example du Mapbox - «Cluster points within a layer»
    // Source: https://docs.mapbox.com/android/maps/examples/android-view/location-component-animation/
    override fun dessinerCercle( position: Point ) {
        val mapboxMap = vue.mapView.getMapboxMap()

        // Si rayon pas encore procuré ou l'utilisateur veut pas
        var rayon = try {
            vue.txtRayon.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText( vue.requireContext(), R.string.rayon_indéterminé, Toast.LENGTH_SHORT ).show()
            0
        }

        val geoJsonSource = geoJsonSource("circle-source") {
            geometry(position)
        }

        if ( rayon > 0) {
            mapboxMap.getStyle { style ->
                // Erreur code: le cerle existe déja. Alors j'ajouté si le cercle existe, on l'efface avant de permettre de recliqué le bouton rayon
                if (style.getLayer("circle-layer") != null) {
                    style.removeStyleLayer("circle-layer")
                }
                if (style.getSource("circle-source") != null) {
                    style.removeStyleSource("circle-source")
                }

                style.addSource(geoJsonSource)
                style.addLayer(
                    circleLayer("circle-layer", "circle-source") {
                        circleColor(ColorUtils.colorToRgbaString(Color.BLUE))
                        circleRadius(rayon.toDouble())
                        circleOpacity(0.2)
                    }
                )
            }
        }

        afficherStationnementsRayon( position, rayon.toString() )

    }
}

