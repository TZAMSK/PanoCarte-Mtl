package com.example.panocartemtl.favoris

import java.time.LocalDate

data class Stationnement(
    val adresse: String,
    var dateSelectionnee: LocalDate? = null // Utilisation d'un type Date plus approprié
)

class Modèle {

    private val stationnements = mutableListOf<Stationnement>()

    // Supprime un stationnement de la liste
    fun supprimerStationnement(index: Int, stationnements: MutableList<Stationnement>) {
        if (index in stationnements.indices) {
            stationnements.removeAt(index)
        } else {
            throw IndexOutOfBoundsException("Index de stationnement invalide : $index")
        }
    }

    // Associe une date à un stationnement (utilisation de LocalDate pour une meilleure gestion des dates)
    fun associerDate(index: Int, stationnements: MutableList<Stationnement>, date: LocalDate) {
        if (index in stationnements.indices) {
            stationnements[index].dateSelectionnee = date
        } else {
            throw IndexOutOfBoundsException("Index de stationnement invalide : $index")
        }
    }

    // Met à jour la liste des stationnements
    fun mettreAJourStationnements(stationnements: List<Stationnement>) {
        this.stationnements.clear()
        this.stationnements.addAll(stationnements)
    }
    fun ajouterStationnement(adresse: String) {
        if (adresse.isNotBlank()) {
            stationnements.add(Stationnement(adresse))
        } else {
            throw IllegalArgumentException("L'adresse ne peut pas être vide.")
        }
    }

}
