package com.example.panocartemtl

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.panocartemtl.favoris.Présentateur
import com.example.panocartemtl.favoris.Stationnement
import java.util.*

class VueFavoris : Fragment() {

    private var adresses: MutableList<String> = mutableListOf()
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var navController: NavController
    private lateinit var présentateur: Présentateur

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favoris, container, false)

        présentateur = Présentateur(this)

        // Gestion du bouton pour afficher le calendrier
        val btnAfficherCalendrier: Button = view.findViewById(R.id.btnAfficherCalendrier)
        btnAfficherCalendrier.setOnClickListener {
            ouvrirCalendrier()
        }

        // Initialisation de la ListView
        val listView: ListView = view.findViewById(R.id.listViewFavoris)

        // Création de l'adaptateur
        adapter = object : ArrayAdapter<String>(requireContext(), R.layout.list_item_favoris, R.id.txtAdresse, adresses) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)

                // Récupération du bouton de suppression
                val btnSupprimer: Button = view.findViewById(R.id.btnSupprimer)
                btnSupprimer.setOnClickListener {
                    présentateur.supprimerStationnement(position)
                }

                // Récupération du bouton de sélection de date
                val btnDate: Button = view.findViewById(R.id.btnDate)
                btnDate.setOnClickListener {
                    afficherDatePicker(position)
                }

                return view
            }
        }

        // Assignation de l'adaptateur à la ListView
        listView.adapter = adapter

        // Gestion du bouton retour
        val btnRetour: Button = view.findViewById(R.id.btnRetour)
        btnRetour.setOnClickListener {
            présentateur.retourVersCarte()
        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        présentateur.chargerListeStationnement()
    }

    private fun afficherDatePicker(position: Int) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay, 9, 0) // Heure par défaut : 9h
            }

            // Vérification de la permission avant d'ajouter l'événement au calendrier
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_CALENDAR), 1)
            } else {
                // Si la permission est déjà accordée, on peut ajouter l'événement
                val stationnement = présentateur.récupérerListeStationnement()[position]
                ajouterEvenementDansCalendrier(
                    titre = "Stationnement réservé",
                    description = "Réservation pour le stationnement à ${stationnement.adresse}",
                    location = stationnement.adresse,
                    date = selectedDate.timeInMillis
                )
            }
        }, year, month, day).show()
    }

    // Fonction pour ajouter un événement au calendrier
    private fun ajouterEvenementDansCalendrier(titre: String, description: String, location: String, date: Long) {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, titre)
            putExtra(CalendarContract.Events.DESCRIPTION, description)
            putExtra(CalendarContract.Events.EVENT_LOCATION, location)
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, date)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, date + 60 * 60 * 1000) // 1 heure par défaut
        }
        val packageManager = requireContext().packageManager
        val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        if (activities.isNotEmpty()) {
            startActivity(intent)
        } else {
            // Si aucune application de calendrier n'est trouvée
            Toast.makeText(requireContext(), "Aucune application de calendrier disponible", Toast.LENGTH_SHORT).show()
        }
    }

    fun listeStationnement(stationnements: List<Stationnement>) {
        adresses.clear()
        stationnements.forEach { stationnement ->
            adresses.add(stationnement.adresse)
        }
        adapter.notifyDataSetChanged()
    }

    fun notifierSuppression() {
        Toast.makeText(requireContext(), "Stationnement supprimé", Toast.LENGTH_SHORT).show()
    }

    // Notifie la vue qu'une date a été sélectionnée
    fun notifierDateSelectionnee(date: String) {
        Toast.makeText(requireContext(), "Date sélectionnée: $date", Toast.LENGTH_SHORT).show()
    }

    // Affiche une erreur dans la vue
    fun afficherErreur(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Navigue vers la carte
    fun naviguerVersCarte() {
        navController.navigate(R.id.action_fragment_favoris_vers_fragment_carte)
    }
    private fun ouvrirCalendrier() {
        try {
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_CALENDAR)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Impossible d'ouvrir l'application calendrier", Toast.LENGTH_SHORT).show()
        }
    }

}
