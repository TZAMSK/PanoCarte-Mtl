package com.example.panocartemtl.favoris

import com.example.panocartemtl.VueFavoris
import java.time.LocalDate

class Présentateur(val vue: VueFavoris) {
    private val modèle = Modèle()

    // Récupère la liste des stationnements
    fun récupérerListeStationnement(): List<Stationnement> {
        return modèle.getStationnementSimulés() // Retourne la liste immuable
    }

    // Charge la liste des stationnements et l'affiche dans la vue
    fun chargerListeStationnement() {
        val stationnements = récupérerListeStationnement() // Utilisation de la méthode pour récupérer la liste
        vue.listeStationnement(stationnements)
    }

    // Supprime un stationnement et met à jour la vue
    fun supprimerStationnement(index: Int) {
        try {
            val stationnements = modèle.getStationnementSimulés().toMutableList() // Créer une liste mutable pour modification
            modèle.supprimerStationnement(index, stationnements)
            modèle.mettreAJourStationnements(stationnements) // Mettre à jour la liste dans le modèle
            vue.listeStationnement(stationnements) // Rafraîchir la vue
            vue.notifierSuppression()
        } catch (e: IndexOutOfBoundsException) {
            vue.afficherErreur("Erreur : ${e.message}")
        }
    }

    // Associe une date à un stationnement et met à jour la vue
    fun associerDateAuStationnement(index: Int, date: LocalDate) {
        try {
            val stationnements = modèle.getStationnementSimulés().toMutableList() // Créer une liste mutable pour modification
            modèle.associerDate(index, stationnements, date)
            modèle.mettreAJourStationnements(stationnements) // Mettre à jour la liste dans le modèle
            vue.listeStationnement(stationnements) // Rafraîchir la vue
            vue.notifierDateSelectionnee(date.toString()) // Notifier la vue avec la date sélectionnée
        } catch (e: IndexOutOfBoundsException) {
            vue.afficherErreur("Erreur : ${e.message}")
        }
    }

    // Navigation vers la carte
    fun retourVersCarte() {
        vue.naviguerVersCarte()
    }
    fun ajouterNouvelleAdresse(adresse: String) {
        try {
            val stationnements = modèle.getStationnementSimulés().toMutableList()
            stationnements.add(Stationnement(adresse)) // Ajout de la nouvelle adresse
            modèle.mettreAJourStationnements(stationnements)
            vue.listeStationnement(stationnements) // Mise à jour de la vue
        } catch (e: Exception) {
            vue.afficherErreur("Erreur : ${e.message}")
        }
    }

}
