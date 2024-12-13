package com.example.panocartemtl.favoris

import java.time.LocalDate

data class Stationnement(
    val adresse: String,
    var dateSelectionnee: LocalDate? = null // Utilisation d'un type Date plus approprié
)

class Modèle {

    private val stationnements = mutableListOf(
        Stationnement("1234 Rue Imaginaire, Montréal, QC H1A 1A1"),
        Stationnement("5678 Avenue Fictive, Montréal, QC H2B 2B2"),
        Stationnement("91011 Boulevard Faux, Montréal, QC H3C 3C3"),
        Stationnement("1213 Chemin Illusoire, Montréal, QC H4D 4D4"),
        Stationnement("1415 Route Mensongère, Montréal, QC H5E 5E5")
    )

    // Renvoie une liste simulée de stationnements (immutable pour éviter les modifications directes)
    fun getStationnementSimulés(): List<Stationnement> = stationnements.toList()

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
