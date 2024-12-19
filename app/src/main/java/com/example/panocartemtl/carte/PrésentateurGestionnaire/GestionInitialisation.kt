package com.example.panocartemtl.carte.PrésentateurGestionnaire

import com.example.panocartemtl.carte.InterfaceCarte.InitialisationInterface
import com.example.panocartemtl.carte.VueCarte
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation

class GestionInitialisation(val vue: VueCarte, val markerMap: MutableMap<PointAnnotation, Int> ): InitialisationInterface {

    override fun détruireTousMarqueurs() {
        for ( marker in markerMap.keys ) {
            vue.pointAnnotationManager.delete( marker )
        }
        markerMap.clear()
    }

    override fun caméraPremièreInstance() {
        vue.mapView.getMapboxMap().setCamera(
            CameraOptions.Builder().center( Point.fromLngLat( -73.554640, 45.561120 ) ).zoom( 13.0 ).build()
        )
    }
}