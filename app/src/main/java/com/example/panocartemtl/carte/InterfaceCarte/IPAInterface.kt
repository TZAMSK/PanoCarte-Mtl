package com.example.panocartemtl.carte.InterfaceCarte

import com.mapbox.geojson.Point

interface IPAInterface {

    fun recupérerTousStationnements();
    fun afficherStationnementParId();
    fun afficherStationnementsRayon( position: Point, rayon: String );
    fun afficherStationnementsParHeure();
    fun afficherStationnementParAdresse( numéro_municipal: String, rue: String, code_postal: String );
}