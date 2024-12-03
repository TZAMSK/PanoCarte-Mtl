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
    suspend fun récuperListeNumérosMunicipaux(): List<String>;
    suspend fun récuperListeRues( numéro_municipal: String ): List<String>;
    suspend fun récuperListeCodesPostal( numéro_municipal: String, rue: String ): List<String>;
}