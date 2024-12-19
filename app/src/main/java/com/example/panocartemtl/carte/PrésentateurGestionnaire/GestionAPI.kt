package com.example.panocartemtl.carte.PrésentateurGestionnaire

import android.widget.Toast
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.R
import com.example.panocartemtl.carte.InterfaceCarte.IPAInterface
import com.example.panocartemtl.carte.VueCarte
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GestionAPI( var vue: VueCarte, val iocontext: CoroutineContext = Dispatchers.IO,
                  val markerMap: MutableMap<PointAnnotation, Int>, var destinationChoisie: Point? = null ): IPAInterface {

    var modèle = Modèle.instance
    private val gestionSpinner = GestionSpinner( vue )

    override fun recupérerTousStationnements() {
        CoroutineScope( iocontext ).launch {
            val listeStationnements = modèle.obtenirTousStationnements()
            gestionSpinner.instancierSpinnerRue()

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

                        Toast.makeText(
                            vue.requireContext(),
                            vue.getString( R.string.marqueur_cliqué ),
                            Toast.LENGTH_SHORT
                        ).show()

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

    fun vérifierBoutonsHeureRempli(): Boolean {
        return vue.btnChoisirHeureDébut.text != vue.getString( R.string.début ) && vue.btnChoisirHeurePrévu.text != vue.getString( R.string.prévu )
    }
}