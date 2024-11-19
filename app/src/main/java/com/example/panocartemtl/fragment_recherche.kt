package com.example.panocartemtl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController


class fragment_recherche : Fragment() {
    private lateinit var btnAnnulé: Button
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recherche, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        btnAnnulé = view.findViewById(R.id.btn_annule)
        btnAnnulé.setOnClickListener {
            navController.navigate(R.id.action_fragment_recherche_vers_fragment_carte)
        }
    }
}