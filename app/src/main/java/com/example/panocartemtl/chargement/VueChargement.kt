package com.example.panocartemtl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.panocartemtl.chargement.PrésentateurChargement

class VueChargement : Fragment() {
    lateinit var prgChargement: ProgressBar
    lateinit var présentateur: PrésentateurChargement
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate( R.layout.fragment_chargement, container, false )
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle? ) {
        super.onViewCreated( view, savedInstanceState )

        prgChargement = view.findViewById( R.id.prgChargement )

        présentateur = PrésentateurChargement(this)

        présentateur.commencerChargement()

        navController = findNavController()
    }

    fun mettreÀjourCercleProgression(progrès: Int) {
        prgChargement.progress = progrès
    }

    fun naviguerVersCarte() {
        navController.navigate( R.id.action_fragment_chargement_vers_fragment_carte )
    }
}