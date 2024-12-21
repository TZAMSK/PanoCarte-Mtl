package com.example.panocartemtl.favoris

import com.example.panocartemtl.VueFavoris
import java.time.LocalDate
import android.app.DatePickerDialog
import android.content.Intent
import android.provider.CalendarContract
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.NavController
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.R
import com.example.panocartemtl.entitées.Stationnement
import java.util.*

class Présentateur(val vue: VueFavoris) {
    private val modèle = Modèle.instance
    private lateinit var adapter: ArrayAdapter<String>

    // Récupère la liste des stationnements
    fun récupérerListeStationnement(): List<Stationnement> {
        return modèle.getStationnementSimulés() // Retourne la liste immuable
    }

    // Charge la liste des stationnements et l'affiche dans la vue
    fun chargerListeStationnement() {
        val stationnements = récupérerListeStationnement() // Utilisation de la méthode pour récupérer la liste
        listeStationnement(stationnements)
    }

    // Supprime un stationnement et met à jour la vue
    fun supprimerStationnement(index: Int) {

        val stationnements = modèle.getStationnementSimulés().toMutableList() // Créer une liste mutable pour modification
        modèle.supprimerStationnement(index, stationnements)
        modèle.mettreAJourStationnements(stationnements) // Mettre à jour la liste dans le modèle
        listeStationnement(stationnements) // Rafraîchir la vue
        notifierSuppression()
    }

    // Associe une date à un stationnement et met à jour la vue
    fun associerDateAuStationnement(index: Int, date: LocalDate) {
        val stationnements = modèle.getStationnementSimulés().toMutableList() // Créer une liste mutable pour modification
        modèle.associerDate(index, stationnements, date)
        modèle.mettreAJourStationnements(stationnements) // Mettre à jour la liste dans le modèle
        listeStationnement(stationnements) // Rafraîchir la vue
        notifierDateSelectionnee(date.toString()) // Notifier la vue avec la date sélectionnée
    }

    // Navigation vers la carte
    fun retourVersCarte() {
        naviguerVersCarte()
    }

    fun ajouterNouvelleAdresse( stationnement: Stationnement ) {
        val stationnements = modèle.getStationnementSimulés().toMutableList()
        stationnements.add(stationnement) // Ajout de la nouvelle adresse
        modèle.mettreAJourStationnements(stationnements)
        listeStationnement(stationnements) // Mise à jour de la vue
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
                location = "${stationnement.adresse.numero_municipal}, ${stationnement.adresse.rue}, ${stationnement.adresse.code_postal}",
                date = selectedDate.timeInMillis
            )
            ajouterEvenementDansCalendrier(intent)
        }, year, month, day).show()
    }

    fun ouvrirCalendrier() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
            }
            vue.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            afficherErreur("L'application calendrier n'est pas disponible ou n'a pas été trouvé")
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

    // Fonction pour ajouter un événement au calendrier
    fun ajouterEvenementDansCalendrier(intent: Intent) {
        val packageManager = vue.requireContext().packageManager
        val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        if (activities.isNotEmpty()) {
            vue.startActivity(intent)
        } else {
            Toast.makeText(vue.requireContext(), "Aucune application de calendrier disponible", Toast.LENGTH_SHORT).show()
        }
    }

    fun notifierSuppression() {
        Toast.makeText(vue.requireContext(), "Stationnement supprimé", Toast.LENGTH_SHORT).show()
    }

    // Notifie la vue qu'une date a été sélectionnée
    fun notifierDateSelectionnee(date: String) {
        Toast.makeText(vue.requireContext(), "Date sélectionnée: $date", Toast.LENGTH_SHORT).show()
    }

    // Affiche une erreur dans la vue
    fun afficherErreur(message: String) {
        Toast.makeText(vue.requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Navigue vers la carte
    fun naviguerVersCarte() {
        vue.navController.navigate(R.id.action_fragment_favoris_vers_fragment_carte)
    }

    fun listeStationnement(stationnements: List<Stationnement>) {
        // Source: https://www.geeksforgeeks.org/how-to-check-if-a-lateinit-variable-has-been-initialized-or-not-in-kotlin/
        if(::adapter.isInitialized) {
            val adresses = stationnements.map { it.adresse }
            adapter.clear()
            //.addAll(adresses)
            adapter.notifyDataSetChanged()
        }
    }
}
