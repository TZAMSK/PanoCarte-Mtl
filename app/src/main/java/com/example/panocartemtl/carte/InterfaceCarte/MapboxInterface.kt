package com.example.panocartemtl.carte.InterfaceCarte

import com.mapbox.geojson.Point

interface MapboxInterface {

    fun dessinerCercle( position: Point );
    fun dessinerCercleDepuisPositionActuelle();
    fun afficherPostionActuelle();
}