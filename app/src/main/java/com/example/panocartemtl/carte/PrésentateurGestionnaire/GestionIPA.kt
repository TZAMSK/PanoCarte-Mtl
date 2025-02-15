package com.example.panocartemtl.carte.PrésentateurGestionnaire

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.R
import com.example.panocartemtl.VueFavoris
import com.example.panocartemtl.carte.InterfaceCarte.IPAInterface
import com.example.panocartemtl.carte.VueCarte
import com.example.panocartemtl.entitées.BaseDeDonnées
import com.example.panocartemtl.favoris.PrésentateurFavoris
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.extension.style.utils.ColorUtils
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class GestionIPA(var vue: VueCarte, val iocontext: CoroutineContext = Dispatchers.IO,
                 val markerMap: MutableMap<PointAnnotation, Int> ): IPAInterface {

    var modèle = Modèle.instance
    private var destinationChoisie: Point? = null
    private var stationnementIdChoisie: Int = 1
    val vueFavoris = VueFavoris()

    // Position actuelle

    // Écrit grâce à la documentation officiel de Mapbox - «User Location»
    // Source: https://docs.mapbox.com/android/maps/guides/user-location/location-on-map/

    // Aussissous «Request permissions»
    // Source: https://developer.android.com/training/permissions/requesting
    val requestPermissionLauncher =
        vue.registerForActivityResult( ActivityResultContracts.RequestPermission() ) { permis: Boolean ->
            if ( permis == true ) {
                getPositionActuelle()
            }
        }

    private val gestionSpinner = GestionSpinner( vue )

    override fun recupérerTousStationnements() {
        CoroutineScope(iocontext).launch {
            val listeStationnements = modèle.obtenirTousStationnements()
            gestionSpinner.instancierSpinnerRue()

            if (ActivityCompat.checkSelfPermission(
                    vue.requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                vue.positionClient.lastLocation.addOnSuccessListener { position: Location? ->
                    if (position != null) {
                        val positionActuelle = Point.fromLngLat(position.longitude, position.latitude)

                        // Source: https://stackoverflow.com/questions/40569436/kotlin-addtextchangelistener-lambda
                        vue.rechercheTxtRayon.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

                            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                                var rayon = vue.rechercheTxtRayon.text.toString()

                                if ( rayon.isNullOrEmpty() ) {
                                    rayon = "0"
                                }
                                CoroutineScope(iocontext).launch {
                                    gestionSpinner.instancierSpinnerRuePrèsDeMoi(
                                        positionActuelle.longitude(),
                                        positionActuelle.latitude(),
                                        rayon
                                    )
                                }
                            }

                            override fun afterTextChanged(editable: Editable?) {}
                        })

                        val initialRayon = vue.rechercheTxtRayon.text.toString()
                        CoroutineScope(iocontext).launch {
                            gestionSpinner.instancierSpinnerRuePrèsDeMoi(
                                positionActuelle.longitude(),
                                positionActuelle.latitude(),
                                initialRayon
                            )
                        }
                    }
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }

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
        vue.pointAnnotationManager.addClickListener( object : OnPointAnnotationClickListener {
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

                        stationnementIdChoisie = marqueurId

                        // Inspiré de:
                        // Source: https://www.youtube.com/watch?v=81gJ8MB25yw
                        // Source: https://github.com/square/picasso
                        Picasso.get().load( "http://10.0.0.136:3000${stationnement.panneau}" ).into( vue.imageStationnement  )

                        vue.montrerPopup(
                            "${stationnement.adresse.numero_municipal} ${stationnement.adresse.rue} ${stationnement.adresse.code_postal}"
                        )
                    }
                }
                return true
            }
        } )
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

    override fun afficherStationnementsParHeure() {
        if ( vérifierBoutonsHeureRempli() == true ) {
            CoroutineScope( iocontext ).launch {
                val début = vue.btnChoisirHeureDébut.text.toString()
                val prévu = vue.btnChoisirHeurePrévu.text.toString()
                val listeStationnementsHeure = modèle.obtenirStationnementsParHeuresDisponibles( début, prévu )

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

    override fun afficherStationnementParAdresse(
        numéro_municipal: String,
        rue: String,
        code_postal: String
    ) {
        CoroutineScope( iocontext ).launch {
            val stationnement = modèle.obtenirStationnementParAdresse( numéro_municipal, rue, code_postal )

            withContext ( Dispatchers.Main ) {
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

    override fun afficherStationnementsParRue(
        rue: String,
    ) {
        CoroutineScope( iocontext ).launch {
            val listeStationnementsRayon = modèle.obtenirStationnementsParRue( rue )

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

    override fun navigationEntrePostion( à_partir: Point ) {
        // Source: https://docs.mapbox.com/help/tutorials/getting-started-directions-api/
        // Sous: Parameters
        // Clé
        val accessToken = "sk.eyJ1IjoidHphbXNrIiwiYSI6ImNtM2tzbGJtczBraHAyaXB2NmlpejlzMnMifQ.JAE5ZyxpPo4Y-n4FlaIuUg"
        // Url de l'api qui nous permet de dessiner une ligne
        // Source pour les ${profile}: https://docs.mapbox.com/help/glossary/routing-profile/
        // Url contient «walking» pour le ${profile} de navigation il y a driving, cycling et driving-traffic
        val url = "https://api.mapbox.com/directions/v5/mapbox/driving/${à_partir.longitude()},${à_partir.latitude()};${destinationChoisie?.longitude()},${destinationChoisie?.latitude()}?geometries=geojson&access_token=$accessToken"


        // Source: Copier coller de la documentation OkHttp
        // https://square.github.io/okhttp/recipes/ sous «Asynchronous Get (.kt, .java)»
        val request = Request.Builder().url( url ).build()

        val client = OkHttpClient()

        client.newCall( request ).enqueue( object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                if ( response.isSuccessful ) {
                    val responseBody = response.body?.string()
                    if ( responseBody != null ) {
                        parseRouteRéponse( responseBody )
                    }
                }
            }
        } )
    }

    fun vérifierBoutonsHeureRempli(): Boolean {
        return vue.btnChoisirHeureDébut.text != vue.getString( R.string.début ) && vue.btnChoisirHeurePrévu.text != vue.getString( R.string.prévu )
    }

    fun parseRouteRéponse( responseBody: String ) {
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
    fun dessinerRoute( routePoints: List<Point> ) {
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

    override fun ajouterStationnementFavoris() {
        CoroutineScope( Dispatchers.Main ).launch {
            val context = vue.requireContext()
            val stationnement = withContext( iocontext ) {
                modèle.obtenirStationnementParId( stationnementIdChoisie )
            }

            // Create the presenter only if the fragment is still attached
            val présentateurFavoris = PrésentateurFavoris( vueFavoris, BaseDeDonnées(context) )
            présentateurFavoris.ajouterNouvelleAdresse( stationnement )

            // Make sure the fragment is still attached before showing Toast
            Toast.makeText( vue.requireContext(), "Adresse ajoutée aux favoris: ${stationnement.adresse.rue}", Toast.LENGTH_SHORT ).show()
        }
    }
}