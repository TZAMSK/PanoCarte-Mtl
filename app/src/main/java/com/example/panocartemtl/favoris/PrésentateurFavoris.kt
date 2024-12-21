package com.example.panocartemtl.favoris

import com.example.panocartemtl.VueFavoris
import android.content.Intent
import android.provider.CalendarContract
import android.content.ActivityNotFoundException
import android.widget.Toast
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.R
import com.example.panocartemtl.entitées.BaseDeDonnées
import com.example.panocartemtl.entitées.Stationnement
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import java.util.*

class PrésentateurFavoris( val vue: VueFavoris, val baseDeDonnées: BaseDeDonnées ) {
    private val modèle = Modèle.instance

    private var stationnementsFavorisAdresse: MutableList<String> = mutableListOf()

    // Charge la liste des stationnements et l'affiche dans la vue
    fun chargerListeStationnement() {
        val stationnements = baseDeDonnées.obtenirTousStationnementBD() // Utilisation de la méthode pour récupérer la liste
        listeStationnement(stationnements)
    }

    // Récupère la liste des stationnements
    fun récupérerListeStationnement(): MutableList<Stationnement> {
        return baseDeDonnées.obtenirTousStationnementBD()
    }


    // Supprime un stationnement et met à jour la vue
    fun supprimerStationnement( index: Int ) {
        val stationnementToDelete = récupérerListeStationnement()[index]
        baseDeDonnées.supprimerStationnement( stationnementToDelete.id )
        val ListeStationnementsMisÀJour = récupérerListeStationnement()
        listeStationnement( ListeStationnementsMisÀJour )
        vue.adapter.notifyDataSetChanged()
    }

    /*
    // Associe une date à un stationnement et met à jour la vue
    fun associerDateAuStationnement(index: Int, date: LocalDate) {
        val stationnements = modèle.getStationnementSimulés().toMutableList() // Créer une liste mutable pour modification
        modèle.associerDate(index, stationnements, date)
        modèle.mettreAJourStationnements(stationnements) // Mettre à jour la liste dans le modèle
        listeStationnement(stationnements) // Rafraîchir la vue
        notifierDateSelectionnee(date.toString()) // Notifier la vue avec la date sélectionnée
    }

     */

    // Navigation vers la carte
    fun retourVersCarte() {
        naviguerVersCarte()
    }

    fun ajouterNouvelleAdresse( stationnement: Stationnement ) {
        baseDeDonnées.insérerStationnement( stationnement )
    }


    fun afficherDatePicker( position: Int ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get( Calendar.YEAR )
        val month = calendar.get( Calendar.MONTH )
        val day = calendar.get( Calendar.DAY_OF_MONTH )

        DatePickerDialog(vue.requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set( selectedYear, selectedMonth, selectedDay, 9, 0 ) // 9:00 par défaut
            }
            val stationnement = récupérerListeStationnement()[position]
            val intent = préparerIntentCalendrier(
                titre = "Stationnement réservé",
                description = "Réservation pour le stationnement à ${stationnement.adresse}",
                location = "${stationnement.adresse.numero_municipal}, ${stationnement.adresse.rue}, ${stationnement.adresse.code_postal}",
                date = selectedDate.timeInMillis
            )
            ajouterEvenementDansCalendrier( intent )
        }, year, month, day).show()
    }

    fun ouvrirCalendrier() {
        try {
            val intent = Intent( Intent.ACTION_MAIN ).apply {
                addCategory( Intent.CATEGORY_APP_CALENDAR )
            }
            vue.startActivity( intent )
        } catch ( e: ActivityNotFoundException ) {
            afficherErreur( "L'application calendrier n'est pas disponible ou n'a pas été trouvé" )
        }
    }

    private fun préparerIntentCalendrier( titre: String, description: String, location: String, date: Long ): Intent {
        return Intent( Intent.ACTION_INSERT ).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra( CalendarContract.Events.TITLE, titre )
            putExtra( CalendarContract.Events.DESCRIPTION, description )
            putExtra( CalendarContract.Events.EVENT_LOCATION, location )
            putExtra( CalendarContract.EXTRA_EVENT_BEGIN_TIME, date )
            putExtra( CalendarContract.EXTRA_EVENT_END_TIME, date + 60 * 60 * 1000 ) // 1 heure par défaut
        }
    }

    // Fonction pour ajouter un événement au calendrier
    fun ajouterEvenementDansCalendrier( intent: Intent ) {
        val packageManager = vue.requireContext().packageManager
        val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        if ( activities.isNotEmpty() ) {
            vue.startActivity( intent )
        } else {
            Toast.makeText( vue.requireContext(), "Aucune application de calendrier disponible", Toast.LENGTH_SHORT ).show()
        }
    }

    // Notifie la vue qu'une date a été sélectionnée
    fun notifierDateSelectionnee( date: String ) {
        Toast.makeText( vue.requireContext(), "Date sélectionnée: $date", Toast.LENGTH_SHORT ).show()
    }

    // Affiche une erreur dans la vue
    fun afficherErreur( message: String ) {
        Toast.makeText( vue.requireContext(), message, Toast.LENGTH_SHORT ).show()
    }

    // Navigue vers la carte
    fun naviguerVersCarte() {
        vue.navController.navigate( R.id.action_fragment_favoris_vers_fragment_carte )
    }

    fun listeStationnement( stationnements: List<Stationnement> ) {
        stationnementsFavorisAdresse.clear()
        stationnements.forEach { stationnement ->
            stationnementsFavorisAdresse.add(" ${stationnement.adresse.numero_municipal} ${stationnement.adresse.rue} ${stationnement.adresse.code_postal} ")
        }
        vue.adapter.clear()
        vue.adapter.addAll( stationnementsFavorisAdresse )
        vue.adapter.notifyDataSetChanged()
    }
}
