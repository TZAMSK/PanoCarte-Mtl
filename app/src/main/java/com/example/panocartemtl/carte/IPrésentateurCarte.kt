package com.example.panocartemtl.carte

import com.mapbox.geojson.Point

interface IPrésentateurCarte {
    fun détruireTousMarqueurs();
    fun caméraPremièreInstance();
    fun recupérerTousStationnements();
    fun afficherStationnementParId();
    fun navigationEntrePostion( à_partir: Point );
    fun afficherStationnementParHeure();
}