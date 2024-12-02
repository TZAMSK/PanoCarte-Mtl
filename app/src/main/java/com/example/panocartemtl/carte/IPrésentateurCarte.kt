package com.example.panocartemtl.carte

import com.mapbox.maps.plugin.annotation.generated.PointAnnotation

interface IPrésentateurCarte {
    fun caméraPremièreInstance();
    fun recupérerTousStationnements();
    fun afficherStationnementParId();
}