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
import com.example.panocartemtl.favoris.Présentateur
import com.example.panocartemtl.favoris.Stationnement

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

        // Initialisation de la ListView
        val listView: ListView = view.findViewById(R.id.listViewFavoris)

        // Création de l'adaptateur
        adapter = object : ArrayAdapter<String>(requireContext(), R.layout.list_item_favoris, R.id.txtAdresse, adresses) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)

                // Récupération du bouton de suppression
                val btnSupprimer: Button = view.findViewById(R.id.btnSupprimer)
                btnSupprimer.setOnClickListener {
                    // Suppression de l'adresse de la liste
                    présentateur.supprimerStationnement(position)
                }

                return view
            }
        }

        // Assignation de l'adaptateur à la ListView
        listView.adapter = adapter

        // Retour
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

    fun listeStationnement(stationnements: List<Stationnement>) {
        adresses.clear()
        adresses.addAll(stationnements.map { it.adresse })
        adapter.notifyDataSetChanged()
    }

    fun notifierSuppression() {
        Toast.makeText(requireContext(), R.string.adresse_supprimée, Toast.LENGTH_SHORT).show()
    }

    fun naviguerVersCarte() {
        navController.navigate(R.id.action_fragment_favoris_vers_fragment_carte)
    }
}
