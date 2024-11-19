package com.example.panocartemtl.favoris

data class Stationnement(val adresse: String)

class Modèle {
    fun getStationnementSimulés(): MutableList<Stationnement> {
        return mutableListOf(
            Stationnement("1234 Rue Imaginaire, Montréal, QC H1A 1A1"),
            Stationnement("5678 Avenue Fictive, Montréal, QC H2B 2B2"),
            Stationnement("91011 Boulevard Faux, Montréal, QC H3C 3C3"),
            Stationnement("1213 Chemin Illusoire, Montréal, QC H4D 4D4"),
            Stationnement("1415 Route Mensongère, Montréal, QC H5E 5E5"),
        )
    }

    fun supprimerStationnement(index: Int, stationnement: MutableList<Stationnement>) {
        stationnement.removeAt(index)
    }
}