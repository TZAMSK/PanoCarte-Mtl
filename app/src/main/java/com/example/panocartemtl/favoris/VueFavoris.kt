package com.example.panocartemtl

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
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
import com.example.panocartemtl.entitées.BaseDeDonnées
import com.example.panocartemtl.favoris.Présentateur
import com.example.panocartemtl.entitées.Stationnement
import java.util.*

class VueFavoris : Fragment() {

    lateinit var adapter: ArrayAdapter<String>
    lateinit var navController: NavController
    private lateinit var présentateur: Présentateur
    private lateinit var baseDeDonnées: BaseDeDonnées

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favoris, container, false)

        baseDeDonnées = BaseDeDonnées( requireContext() )

        présentateur = Présentateur(this, baseDeDonnées)

        // Gestion du bouton pour afficher le calendrier
        val btnAfficherCalendrier: Button = view.findViewById(R.id.btnAfficherCalendrier)
        btnAfficherCalendrier.setOnClickListener {
            présentateur.ouvrirCalendrier()
        }

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

        // Initialisation de la ListView
        val listView: ListView = view.findViewById(R.id.listViewFavoris)

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
}
