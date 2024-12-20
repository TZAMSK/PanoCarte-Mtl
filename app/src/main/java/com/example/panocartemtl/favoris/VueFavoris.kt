package com.example.panocartemtl

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.panocartemtl.favoris.PrésentateurFavoris
import com.example.panocartemtl.favoris.Stationnement

class VueFavoris : Fragment() {

    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var navController: NavController
    private lateinit var présentateur: PrésentateurFavoris

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favoris, container, false)

        présentateur = PrésentateurFavoris(this)

        // Gestion du bouton pour afficher le calendrier
        val btnAfficherCalendrier: Button = view.findViewById(R.id.btnAfficherCalendrier)
        btnAfficherCalendrier.setOnClickListener {
            présentateur.ouvrirCalendrier()
        }

        // Initialisation de la ListView
        val listView: ListView = view.findViewById(R.id.listViewFavoris)

        // Création de l'adaptateur
        adapter = object : ArrayAdapter<String>(requireContext(), R.layout.list_item_favoris, R.id.txtAdresse, mutableListOf()) {
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
                    présentateur.afficherDatePicker(position)
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

        // Vérifie si une adresse a été transmise
        val adresse = activity?.intent?.getStringExtra("ADRESSE")
        if (!adresse.isNullOrEmpty()) {
            présentateur.ajouterNouvelleAdresse(adresse)
        }
    }

    // Fonction pour ajouter un événement au calendrier
    fun ajouterEvenementDansCalendrier(intent: Intent) {
        val packageManager = requireContext().packageManager
        val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

        if (activities.isNotEmpty()) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Aucune application de calendrier disponible", Toast.LENGTH_SHORT).show()
        }
    }

    fun listeStationnement(stationnements: List<Stationnement>) {
        // Source: https://www.geeksforgeeks.org/how-to-check-if-a-lateinit-variable-has-been-initialized-or-not-in-kotlin/
        if(::adapter.isInitialized) {
            val adresses = stationnements.map { it.adresse }
            adapter.clear()
            adapter.addAll(adresses)
            adapter.notifyDataSetChanged()
        }
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


}
