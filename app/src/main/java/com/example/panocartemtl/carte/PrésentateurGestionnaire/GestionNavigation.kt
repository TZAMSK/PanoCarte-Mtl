package com.example.panocartemtl.carte.PrésentateurGestionnaire

import android.view.View
import com.example.panocartemtl.R
import com.example.panocartemtl.carte.InterfaceCarte.NavigationInterface
import com.example.panocartemtl.carte.VueCarte
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class GestionNavigation( val vue: VueCarte, val iocontext: CoroutineContext = Dispatchers.IO,
                        val markerMap: MutableMap<PointAnnotation, Int> ): NavigationInterface {

    private val gestionInstallation = GestionInitialisation( vue, markerMap )
    private val gestionAPI = GestionIPA( vue, iocontext, markerMap )

    override fun changerÉcranCliqueMenu( itemId: Int ): Boolean {
        return when ( itemId ) {
            R.id.navigation_carte -> true
            R.id.navigation_recherche -> {
                vue.popupRecherche.visibility = View.VISIBLE
                true
            }
            R.id.navigation_favoris -> {
                vue.navController.navigate( R.id.action_fragment_carte_vers_fragment_favoris )
                true
            }
            else -> false
        }
    }

    override fun changerContenuPopupRechercheHeure( cliqué: Boolean ) {
        if ( cliqué ) {
            vue.insértionTexteHeure.visibility = View.VISIBLE
            vue.insértionTexteAdresse.visibility = View.GONE
            vue.insértionTextePrèsDeMoi.visibility = View.GONE
            vue.choisirAdresse.isChecked = false
        }
    }

    override fun changerContenuPopupRechercheAdresse( cliqué: Boolean ) {
        if ( cliqué ) {
            vue.insértionTexteAdresse.visibility = View.VISIBLE
            vue.insértionTexteHeure.visibility = View.GONE
            vue.insértionTextePrèsDeMoi.visibility = View.GONE
            vue.choisirHeure.isChecked = false
        }
    }


    override fun changerContenuPopupRecherchePrèsDeMoi( cliqué: Boolean ) {
        if (cliqué) {
            vue.insértionTextePrèsDeMoi.visibility = View.VISIBLE
            vue.insértionTexteAdresse.visibility = View.GONE
            vue.insértionTexteHeure.visibility = View.GONE
            vue.choisirHeure.isChecked = false
        }
    }

    override fun vérifierContenuEtAfficherStationnementParHeure() {
        if ( gestionAPI.vérifierBoutonsHeureRempli() == true ) {
            gestionInstallation.détruireTousMarqueurs()
            gestionAPI.afficherStationnementsParHeure()
        }
    }
}