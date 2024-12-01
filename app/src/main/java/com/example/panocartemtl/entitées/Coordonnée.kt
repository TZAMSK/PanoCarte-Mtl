package com.example.panocartemtl.entitées

class Coordonnée(
    var longitude: Double = 0.0,
    var latitude: Double = 0.0
){
    // Source: exemple code chargeur
    override fun equals( other: Any? ): Boolean {
        if ( this === other ) return true
        if ( other !is Coordonnée ) return false

        return this.longitude ==  other.longitude &&
                this.latitude == other.latitude
    }

    override fun toString(): String {
        return "Coordonnée(longitude=$longitude, latitude=$latitude)"
    }
}