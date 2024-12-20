package com.example.panocartemtl.entitées

import java.time.LocalDate

data class Stationnement (
    var id: Int = 0,
    var adresse: Adresse=Adresse("", "", ""),
    var coordonnée: Coordonnée,
    var panneau: String = "",
    var heures_début: String = "",
    var heures_fin: String = "",
    var dateSelectionnee: LocalDate? = null
){}