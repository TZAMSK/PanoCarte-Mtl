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

class fragment_favoris : Fragment() {

    private lateinit var adresses: MutableList<String>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favoris, container, false)

        // Initialisation de la ListView
        val listView: ListView = view.findViewById(R.id.listViewFavoris)

        // Adresses fictives
        adresses = mutableListOf(
            "1234 Rue Imaginaire, Montréal, QC H1A 1A1",
            "5678 Avenue Fictive, Montréal, QC H2B 2B2",
            "91011 Boulevard Faux, Montréal, QC H3C 3C3",
            "1213 Chemin Illusoire, Montréal, QC H4D 4D4",
            "1415 Route Mensongère, Montréal, QC H5E 5E5"
        )

        // Création de l'adaptateur
        adapter = object : ArrayAdapter<String>(requireContext(), R.layout.list_item_favoris, R.id.txtAdresse, adresses) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)

                // Récupération du bouton de suppression
                val btnSupprimer: Button = view.findViewById(R.id.btnSupprimer)
                btnSupprimer.setOnClickListener {
                    // Suppression de l'adresse de la liste
                    adresses.removeAt(position)
                    // Mise à jour de l'adaptateur
                    notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Adresse supprimée", Toast.LENGTH_SHORT).show()
                }

                return view
            }
        }

        // Assignation de l'adaptateur à la ListView
        listView.adapter = adapter

        return view
    }
}
