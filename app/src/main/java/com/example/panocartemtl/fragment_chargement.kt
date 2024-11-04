package com.example.panocartemtl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class fragment_chargement : Fragment() {
    lateinit var prgText: TextView
    lateinit var prgChargement: ProgressBar
    lateinit var navController: NavController

    private var progrès = 0

    override fun onCreate( savedInstanceState: Bundle? ) {
        super.onCreate( savedInstanceState )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate( R.layout.fragment_chargement, container, false )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prgText = view.findViewById<TextView>( R.id.prgText )
        prgChargement = view.findViewById<ProgressBar>( R.id.prgChargement )

        viewLifecycleOwner.lifecycleScope.launch {
            commencerChargement( prgChargement, prgText )
        }
    }

    private suspend fun commencerChargement( prgChargement: ProgressBar, prgTextView: TextView ) {
        while ( progrès < 100 ) {
            progrès += 9
            if ( progrès > 100 ) {
                progrès = 100
            }
            prgChargement.progress = progrès
            prgTextView.text = "${progrès} %"

            delay(100)
        }

        delay( 200 )

        if ( isAdded ) {
            val navController = Navigation.findNavController(requireView())
            navController.navigate ( R.id.action_fragment_chargement_vers_fragment_carte )
        }

    }
}