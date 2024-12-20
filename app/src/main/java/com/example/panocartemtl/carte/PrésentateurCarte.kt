package com.example.panocartemtl.carte

import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.carte.PrésentateurGestionnaire.GestionIPA
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
    val markerMap: MutableMap<PointAnnotation, Int> = mutableMapOf()

    var modèle = Modèle.instance

    private val gestionInitialisation = GestionInitialisation( vue, markerMap )
    private val gestionSpinner = GestionSpinner( vue, iocontext )
    private val gestionIPA = GestionIPA( vue, iocontext, markerMap )
    private val gestionNavigation = GestionNavigation( vue, iocontext, markerMap )
    private val gestionMapbox = GestionMapbox( vue, iocontext, markerMap )
    private val gestionMontre = GestionMontre( vue )

    //--- Initialisation ---//
    override fun détruireTousMarqueurs() {
        gestionInitialisation.détruireTousMarqueurs()
    }

    override fun caméraPremièreInstance() {
        gestionInitialisation.caméraPremièreInstance()
    }

    //--- API ---//
    override fun recupérerTousStationnements() {
        gestionIPA.recupérerTousStationnements()
    }

    override fun afficherStationnementParId() {
        gestionIPA.afficherStationnementParId()
    }

    override fun afficherStationnementsRayon( position: Point, rayon: String ) {
        gestionIPA.afficherStationnementsRayon( position, rayon )
    }

    override fun afficherStationnementsParHeure() {
        gestionIPA.afficherStationnementsParHeure()
    }

    override fun afficherStationnementParAdresse( numéro_municipal: String, rue: String, code_postal: String ) {
        gestionIPA.afficherStationnementParAdresse( numéro_municipal, rue, code_postal )
    }

    override fun afficherStationnementsParRue( rue: String ) {
        gestionIPA.afficherStationnementsParRue( rue )
    }

    override fun getPositionActuelle() {
        gestionIPA.getPositionActuelle()
    }

    override fun dessinerNavigationEntrePostion() {
        gestionIPA.dessinerNavigationEntrePostion()
    }

    override fun navigationEntrePostion( à_partir: Point ) {
        gestionIPA.navigationEntrePostion( à_partir )
    }


    //--- Mapbox ---//
    override fun dessinerCercle( position: Point ) {
        gestionMapbox.dessinerCercle( position )
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

    override fun changerContenuPopupRecherchePrèsDeMoi( cliqué: Boolean ) {
        gestionNavigation.changerContenuPopupRecherchePrèsDeMoi( cliqué )
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

    override suspend fun récuperListeRuesRayon(
        longitude: Double,
        latitude: Double,
        rayon: String
    ): List<String> {
        return gestionSpinner.récuperListeRuesRayon( longitude, latitude, rayon )
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

