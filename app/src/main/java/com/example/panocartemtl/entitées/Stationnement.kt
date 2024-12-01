package com.example.panocartemtl.entitées

data class Stationnement (
    var id: Int = 0,
    var adresse: Adresse,
    var coordonnée: Coordonnée,
    var panneau: String = "",
    var heures_début: String = "",
    var heures_fin: String = ""
){}