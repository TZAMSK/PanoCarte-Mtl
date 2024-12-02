package com.example.panocartemtl.carte

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

    override fun navigationEntrePostion(à_partir: Point) {
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
        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    if (responseBody != null) {
                        parseRouteResponse(responseBody)
                    }
                }
            }
        })
    }

    private fun parseRouteResponse(responseBody: String) {
        // On trouve le json dans: https://docs.mapbox.com/help/tutorials/getting-started-directions-api/
        // Sous: Review the response

        val json = JSONObject(responseBody)
        // Route commence à index zéro
        val route = json.getJSONArray("routes").getJSONObject(0)
        val geometry = route.getJSONObject("geometry")
        val coordinates = geometry.getJSONArray("coordinates")

        // Les points qui permet de dessiner la ligne (ils se relient en genre de vecteurs?)
        val routePoints = ArrayList<Point>()
        for (i in 0 until coordinates.length()) {
            val coord = coordinates.getJSONArray(i)
            val point = Point.fromLngLat(coord.getDouble(0), coord.getDouble(1))
            routePoints.add(point)
        }

        dessinerRoute(routePoints)
    }

    // Ressemble exactement à dessinerCercleAutourPostion
    private fun dessinerRoute(routePoints: List<Point>) {
        vue.mapView.getMapboxMap().getStyle { style ->

            val geoJsonSource = geoJsonSource("route-source") {
                geometry(LineString.fromLngLats(routePoints))
            }

            // Comme dans dessinerCercleAutourPostion
            // Erreur code: la lign existe déja. Alors j'ajouté si la ligne existe, on l'efface avant de permettre de recliqué le bouton destination
            if (style.getLayer("route-layer") != null) {
                style.removeStyleLayer("route-layer")
            }
            if (style.getSource("route-source") != null) {
                style.removeStyleSource("route-source")
            }

            style.addSource(geoJsonSource)

            style.addLayer(
                lineLayer("route-layer", "route-source") {
                    lineColor(ColorUtils.colorToRgbaString(Color.RED))
                    lineWidth(5.0)
                }
            )
        }
    }
}

