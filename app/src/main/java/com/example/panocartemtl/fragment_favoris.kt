package com.example.panocartemtl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class fragment_favoris : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favoris, container, false)

        // Initialisation de la ListView
        val listView: ListView = view.findViewById(R.id.listViewFavoris)

        // Adresses fictives
        val adresses = listOf(
            "1234 Rue Imaginaire, Montréal, QC H1A 1A1",
            "5678 Avenue Fictive, Montréal, QC H2B 2B2",
            "91011 Boulevard Faux, Montréal, QC H3C 3C3",
            "1213 Chemin Illusoire, Montréal, QC H4D 4D4",
            "1415 Route Mensongère, Montréal, QC H5E 5E5"
        )

        // Création de l'adaptateur
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_favoris, R.id.txtAdresse, adresses)

        // Assignation de l'adaptateur à la ListView
        listView.adapter = adapter

        return view
    }
}
