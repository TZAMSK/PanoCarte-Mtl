package com.example.panocartemtl

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView

class fragment_carte : Fragment() {
    private lateinit var popupLayout: View
    private lateinit var btnPostion: Button
    private lateinit var btnFavoris: Button
    private lateinit var popupBouton: Button
    private lateinit var navController: NavController

    private val callback = OnMapReadyCallback { googleMap ->

        // Création des coordonées fautifs
        val coordRosemont = LatLng( 45.5571566, -73.5826029 )
        val markerRosemont = googleMap.addMarker( MarkerOptions().position( coordRosemont ).title( "Position de Rosemont").icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED ) ))

        val coordInsectarium = LatLng( 45.561120, -73.554640 )
        val markerInsectarium = googleMap.addMarker( MarkerOptions().position( coordInsectarium ).title( "Position de l'Insectarium" ) )


        googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( coordInsectarium, 13f ) )

        // Évenement marqueur cliqué
        // Affichage popup
        googleMap.setOnMarkerClickListener { clickedMarker ->
            if ( clickedMarker == markerRosemont ) {
                Toast.makeText( requireContext(), "Marqueur cliqué: ${clickedMarker.title}", Toast.LENGTH_SHORT ).show()
                montrerPopup( "Position de Rosemont", "6400 16e Avenue, Montréal, QC H1X 2S9" )
                true
            } else if ( clickedMarker == markerInsectarium ){
                Toast.makeText( requireContext(), "Marqueur cliqué: ${clickedMarker.title}", Toast.LENGTH_SHORT ).show()
                montrerPopup( "Position de l'Insectarium", "4581 Sherbrooke St E, Montreal, QC H1X 2B2" )
                true
            } else {
                false
            }
        }

        navController = findNavController()

        // Évenement boutons
        btnPostion.setOnClickListener {
            googleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( coordRosemont, 18f ), 700, null )
        }

        btnFavoris.setOnClickListener {
            navController.navigate( R.id.action_fragment_carte_vers_fragment_favoris )
        }

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isCompassEnabled = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate( R.layout.fragment_carte, container, false )
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle? ) {
        super.onViewCreated( view, savedInstanceState )

        val mapFragment = childFragmentManager.findFragmentById( R.id.map ) as SupportMapFragment?
        mapFragment?.getMapAsync( callback )

        // Instancier les composantes
        popupLayout = view.findViewById( R.id.popupLayout )
        popupBouton = view.findViewById( R.id.popupBouton )
        btnPostion = view.findViewById( R.id.btnPositionActuelle )
        btnFavoris = view.findViewById( R.id.btnFavoris )

        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>( R.id.menu_navigation )

        // Cacher popup
        popupBouton.setOnClickListener {
            popupLayout.visibility = View.GONE
        }

        // Navigation menu
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when ( item.itemId ) {
                R.id.navigation_carte -> {
                    true
                }
                R.id.navigation_recherche -> {
                    true
                }
                R.id.navigation_favoris -> {
                    navController.navigate( R.id.action_fragment_carte_vers_fragment_favoris )
                    true
                }
                else -> false
            }
        }
    }

    private fun montrerPopup( titre: String, description: String ) {
        popupLayout.findViewById<TextView>( R.id.txtAdresse ).text = description
        popupLayout.visibility = View.VISIBLE
    }
}