package com.example.panocartemtl.carte

import com.mapbox.geojson.Point

interface IPrésentateurCarte {
    fun détruireTousMarqueurs();
    fun caméraPremièreInstance();
    fun recupérerTousStationnements();
    fun afficherStationnementParId();
    fun navigationEntrePostion( à_partir: Point );
    fun afficherStationnementsParHeure();
    fun afficherStationnementsRayon( position: Point, rayon: String );
    fun dessinerCercle( position: Point );
    fun afficherStationnementParAdresse( numéro_municipal: String, rue: String, code_postal: String );
    suspend fun récuperListeNumérosMunicipaux( rue: String ): List<String>;
    suspend fun récuperListeRues(): List<String>;
    suspend fun récuperListeCodesPostal( numéro_municipal: String, rue: String ): List<String>;

    fun afficherContenuePourSpinnerNuméroMunicipal();
    fun afficherContenuePourSpinnerCodePostal();
    fun vérifierContenuEtAfficherStationnementParHeure();
    fun changerÉcranCliqueMenu( itemId: Int ): Boolean;

    fun changerContenuPopupRechercheHeure( cliqué: Boolean );
    fun changerContenuPopupRechercheAdresse( cliqué: Boolean );

    fun getPositionActuelle();
    fun dessinerNavigationEntrePostion();
    fun dessinerCercleDepuisPartirPositionActuelle();
    fun afficherPostionActuelle();
}