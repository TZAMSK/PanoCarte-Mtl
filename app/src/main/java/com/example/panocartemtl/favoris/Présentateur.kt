package com.example.panocartemtl.favoris

import com.example.panocartemtl.VueFavoris

class Présentateur(val vue: VueFavoris) {
    private val modèle = Modèle()
    private val stationnements: MutableList<Stationnement> = modèle.getStationnementSimulés()

    fun récupérerListeStationnement(): MutableList<Stationnement> {
        return modèle.getStationnementSimulés()
    }

    fun chargerListeStationnement() {
        vue.listeStationnement(récupérerListeStationnement())
    }

    fun supprimerStationnement(index: Int) {
        stationnements.removeAt(index)
        vue.listeStationnement(stationnements)
        vue.notifierSuppression()
    }

    fun retourVersCarte() {
        vue.naviguerVersCarte()
    }
}