package com.example.panocartemtl.carte.InterfaceCarte

import com.mapbox.geojson.Point

interface MapboxInterface {

    fun navigationEntrePostion( à_partir: Point );
    fun dessinerCercle( position: Point );
    fun getPositionActuelle();
    fun dessinerNavigationEntrePostion();
    fun dessinerCercleDepuisPositionActuelle();
    fun afficherPostionActuelle();
}