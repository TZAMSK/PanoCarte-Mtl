package com.example.panocartemtl.favoris

import com.example.panocartemtl.VueFavoris
import java.time.LocalDate
import android.app.DatePickerDialog
import android.content.Intent
import android.provider.CalendarContract
import android.content.ActivityNotFoundException
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.entitées.Stationnement
import java.util.*

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

        val stationnements = modèle.getStationnementSimulés().toMutableList() // Créer une liste mutable pour modification
        modèle.supprimerStationnement(index, stationnements)
        modèle.mettreAJourStationnements(stationnements) // Mettre à jour la liste dans le modèle
        vue.listeStationnement(stationnements) // Rafraîchir la vue
        vue.notifierSuppression()
    }

    // Associe une date à un stationnement et met à jour la vue
    fun associerDateAuStationnement(index: Int, date: LocalDate) {
        val stationnements = modèle.getStationnementSimulés().toMutableList() // Créer une liste mutable pour modification
        modèle.associerDate(index, stationnements, date)
        modèle.mettreAJourStationnements(stationnements) // Mettre à jour la liste dans le modèle
        vue.listeStationnement(stationnements) // Rafraîchir la vue
        vue.notifierDateSelectionnee(date.toString()) // Notifier la vue avec la date sélectionnée
    }

    // Navigation vers la carte
    fun retourVersCarte() {
        vue.naviguerVersCarte()
    }
    fun ajouterNouvelleAdresse(adresse: String) {
        try {
            if (adresse.isBlank()) {
                throw IllegalArgumentException("L'adresse ne peut pas être vide.")
            }
            val stationnements = modèle.getStationnementSimulés().toMutableList()
            stationnements.add(Stationnement(adresse)) // Ajout de la nouvelle adresse
            modèle.mettreAJourStationnements(stationnements)
            vue.listeStationnement(stationnements) // Mise à jour de la vue
        } catch (e: IllegalArgumentException) {
            vue.afficherErreur("Erreur : ${e.message}")
        }
    }

    fun afficherDatePicker(position: Int) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(vue.requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay, 9, 0) // 9:00 par défaut
            }
            val stationnement = récupérerListeStationnement()[position]
            val intent = préparerIntentCalendrier(
                titre = "Stationnement réservé",
                description = "Réservation pour le stationnement à ${stationnement.adresse}",
                location = stationnement.adresse,
                date = selectedDate.timeInMillis
            )
            vue.ajouterEvenementDansCalendrier(intent)
        }, year, month, day).show()
    }

    fun ouvrirCalendrier() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
            }
            vue.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            vue.afficherErreur("L'application calendrier n'est pas disponible ou n'a pas été trouvé")
        }
    }

    private fun préparerIntentCalendrier(titre: String, description: String, location: String, date: Long): Intent {
        return Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, titre)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date + 60 * 60 * 1000) // 1 heure par défaut
        }
    }

}
