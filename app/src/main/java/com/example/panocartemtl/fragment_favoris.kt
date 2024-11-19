package com.example.panocartemtl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

class fragment_favoris : Fragment() {

    private lateinit var adresses: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var btnRetour: Button
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favoris, container, false)

        // Initialisation de la ListView
        val listView: ListView = view.findViewById(R.id.listViewFavoris)

        // Liste d'adresses fictives
        adresses = mutableListOf(
            "1234 Rue Imaginaire, Montréal, QC H1A 1A1",
            "5678 Avenue Fictive, Montréal, QC H2B 2B2",
            "91011 Boulevard Faux, Montréal, QC H3C 3C3",
            "1213 Chemin Illusoire, Montréal, QC H4D 4D4",
            "1415 Route Mensongère, Montréal, QC H5E 5E5"
        )

        // Création de l'adaptateur
        adapter = ArrayAdapter(requireContext(), R.layout.list_item_favoris, R.id.txtAdresse, adresses)
        listView.adapter = adapter

        // Récupérer l'adresse depuis les arguments
        arguments?.getString("adresse")?.let { nouvelleAdresse ->
            adresses.add(nouvelleAdresse)
            adapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "Adresse ajoutée", Toast.LENGTH_SHORT).show()
        }

        // Retour
        btnRetour = view.findViewById(R.id.btnRetour)
        navController = findNavController()
        btnRetour.setOnClickListener {
            navController.navigate(R.id.action_fragment_favoris_vers_fragment_carte)
        }

        return view
    }
}
