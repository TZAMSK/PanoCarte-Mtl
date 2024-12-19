package com.example.panocartemtl.carte

import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.carte.PrésentateurGestionnaire.GestionAPI
import com.example.panocartemtl.carte.PrésentateurGestionnaire.GestionInitialisation
import com.example.panocartemtl.carte.PrésentateurGestionnaire.GestionMapbox
import com.example.panocartemtl.carte.PrésentateurGestionnaire.GestionMontre
import com.example.panocartemtl.carte.PrésentateurGestionnaire.GestionNavigation
import com.example.panocartemtl.carte.PrésentateurGestionnaire.GestionSpinner
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class PrésentateurCarte( var vue: VueCarte, val iocontext: CoroutineContext = Dispatchers.IO ): IPrésentateurCarte {
    private var destinationChoisie: Point? = null
    val markerMap: MutableMap<PointAnnotation, Int> = mutableMapOf()

    var modèle = Modèle.instance

    private val gestionInstallation = GestionInitialisation( vue, markerMap )
    private val gestionSpinner = GestionSpinner( vue, iocontext )
    private val gestionAPI = GestionAPI( vue, iocontext, markerMap, destinationChoisie )
    private val gestionNavigation = GestionNavigation( vue, iocontext, markerMap )
    private val gestionMapbox = GestionMapbox( vue, iocontext, markerMap, destinationChoisie )
    private val gestionMontre = GestionMontre( vue )

    //--- Initialisation ---//
    override fun détruireTousMarqueurs() {
        gestionInstallation.détruireTousMarqueurs()
    }

    override fun caméraPremièreInstance() {
        gestionInstallation.caméraPremièreInstance()
    }

    //--- API ---//
    override fun recupérerTousStationnements() {
        gestionAPI.recupérerTousStationnements()
    }

    override fun afficherStationnementParId() {
        gestionAPI.afficherStationnementParId()
    }

    override fun afficherStationnementsRayon( position: Point, rayon: String ) {
        gestionAPI.afficherStationnementsRayon( position, rayon )
    }

    override fun afficherStationnementsParHeure() {
        gestionAPI.afficherStationnementsParHeure()
    }

    override fun afficherStationnementParAdresse( numéro_municipal: String, rue: String, code_postal: String ) {
        gestionAPI.afficherStationnementParAdresse( numéro_municipal, rue, code_postal )
    }

    //--- Mapbox ---//
    override fun navigationEntrePostion( à_partir: Point ) {
        gestionMapbox.navigationEntrePostion( à_partir )
    }

    override fun dessinerCercle( position: Point ) {
        gestionMapbox.dessinerCercle( position )
    }

    override fun getPositionActuelle() {
        gestionMapbox.getPositionActuelle()
    }

    override fun dessinerNavigationEntrePostion() {
        gestionMapbox.dessinerNavigationEntrePostion()
    }

    override fun dessinerCercleDepuisPositionActuelle() {
        gestionMapbox.dessinerCercleDepuisPositionActuelle()
    }

    override fun afficherPostionActuelle() {
        gestionMapbox.afficherPostionActuelle()
    }

    //--- Montre ---//
    override fun montrerMontreDébut() {
        gestionMontre.montrerMontreDébut()
    }

    override fun montrerMontrePrévu() {
        gestionMontre.montrerMontrePrévu()
    }

    //--- Navigation ---//
    override fun changerÉcranCliqueMenu( itemId: Int ): Boolean {
        return gestionNavigation.changerÉcranCliqueMenu( itemId )
    }

    override fun changerContenuPopupRechercheHeure( cliqué: Boolean ) {
        gestionNavigation.changerContenuPopupRechercheHeure( cliqué )
    }

    override fun changerContenuPopupRechercheAdresse( cliqué: Boolean ) {
        gestionNavigation.changerContenuPopupRechercheAdresse( cliqué )
    }

    override fun vérifierContenuEtAfficherStationnementParHeure() {
        gestionNavigation.vérifierContenuEtAfficherStationnementParHeure()
    }

    //--- Spinner ---//
    override suspend fun récuperListeNumérosMunicipaux( rue: String ): List<String> {
        return gestionSpinner.récuperListeNumérosMunicipaux( rue )
    }

    override suspend fun récuperListeRues(): List<String> {
        return gestionSpinner.récuperListeRues()
    }

    override suspend fun récuperListeCodesPostal( numéro_municipal: String, rue: String ): List<String> {
        return gestionSpinner.récuperListeCodesPostal( numéro_municipal, rue )
    }

    override fun afficherContenuePourSpinnerNuméroMunicipal() {
        gestionSpinner.afficherContenuePourSpinnerNuméroMunicipal()
    }

    override fun afficherContenuePourSpinnerCodePostal() {
        gestionSpinner.afficherContenuePourSpinnerCodePostal()
    }
}

