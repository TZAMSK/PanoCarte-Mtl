package com.example.panocartemtl.carte

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.R
import com.example.panocartemtl.VueFavoris
import com.example.panocartemtl.favoris.Présentateur
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class VueCarte : Fragment() {
    private lateinit var popupLayout: View
    lateinit var popupRecherche: View
    private lateinit var btnPostion: Button
    lateinit var txtRayon: EditText
    lateinit var btnRayon: ImageView
    private lateinit var popupBouton: Button
    private lateinit var btnFermerPopupRechercheHeure: Button
    lateinit var btnOkPopupRechercheHeure: Button
    private lateinit var btnFermerPopupRechercheAdresse: Button
    lateinit var btnOkPopupRechercheAdresse: Button
    private lateinit var btnFermerPopupRecherchePrèsDeMoi: Button
    lateinit var btnOkPopupRecherchePrèsDeMoi: Button
    lateinit var btnChoisirHeureDébut: Button
    lateinit var btnChoisirHeurePrévu: Button
    lateinit var navController: NavController
    lateinit var mapView: MapView
    private lateinit var annotationPlugin: AnnotationPlugin
    lateinit var pointAnnotationManager: PointAnnotationManager
    lateinit var positionClient: FusedLocationProviderClient
    private lateinit var btnDestination: ImageView
    lateinit var choisirHeure: ToggleButton
    lateinit var choisirAdresse: ToggleButton
    lateinit var choisirPrèsDeMoi: ToggleButton
    lateinit var insértionTexteHeure: LinearLayout
    lateinit var insértionTexteAdresse: LinearLayout
    lateinit var insértionTextePrèsDeMoi: LinearLayout
    private lateinit var btnTousStationnements: Button
    lateinit var imageStationnement: ImageView

    lateinit var sélectionNuméroMunicipal: Spinner
    lateinit var sélectionRue: Spinner
    lateinit var sélectionCodePostal: Spinner

    lateinit var sélectionRuePrèsDeMoi: Spinner

    lateinit var rechercheTxtRayon: EditText

    val modèle = Modèle.instance
    val présentateur = PrésentateurCarte( this )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate( R.layout.fragment_carte, container, false )
    }

    override fun onViewCreated( view: View, savedInstanceState: Bundle? ) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById( R.id.map )
        mapView.getMapboxMap().loadStyleUri( Style.MAPBOX_STREETS ) { style ->
            annotationPlugin = mapView.annotations
            pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
            setupMap( style )
        }

        positionClient = LocationServices.getFusedLocationProviderClient( requireContext() )

        navController = findNavController()

        // Instancier les composantes
        popupLayout = view.findViewById( R.id.popupLayout )
        popupRecherche = view.findViewById( R.id.popupRecherche )
        popupBouton = view.findViewById( R.id.popupBouton )
        btnPostion = view.findViewById( R.id.btnPositionActuelle )
        txtRayon = view.findViewById( R.id.txtRayon )
        btnRayon = view.findViewById( R.id.btnRayon )
        btnDestination = view.findViewById( R.id.btnDestination )
        btnFermerPopupRechercheHeure = view.findViewById( R.id.btnFermerPopupRechercheHeure )
        btnOkPopupRechercheHeure = view.findViewById( R.id.btnOkPopupRechercheHeure )
        btnChoisirHeureDébut = view.findViewById(R.id.btnChoisirHeureDébut )
        btnChoisirHeurePrévu = view.findViewById( R.id.btnChoisirHeurePrévu )
        btnFermerPopupRechercheAdresse = view.findViewById( R.id.btnFermerPopupRechercheAdresse )
        btnOkPopupRechercheAdresse = view.findViewById( R.id.btnOkPopupRechercheAdresse )
        btnOkPopupRecherchePrèsDeMoi = view.findViewById( R.id.btnOkPopupRecherchePrèsDeMoi )
        btnFermerPopupRecherchePrèsDeMoi = view.findViewById( R.id.btnFermerPopupRecherchePrèsDeMoi )
        btnTousStationnements = view.findViewById( R.id.btnTousStationnements )
        choisirHeure = view.findViewById( R.id.choisirHeure )
        choisirAdresse = view.findViewById( R.id.choisirAdresse )
        choisirPrèsDeMoi = view.findViewById( R.id.choisirPrèsDeMoi )
        insértionTexteHeure = view.findViewById( R.id.insértionTexteHeure )
        insértionTexteAdresse = view.findViewById( R.id.insértionTexteAdresse )
        insértionTextePrèsDeMoi = view.findViewById( R.id.insértionTextePrèsDeMoi )
        imageStationnement = view.findViewById( R.id.imageStationnement )

        sélectionNuméroMunicipal = view.findViewById( R.id.sélectionNuméroMunicipal )
        sélectionRue = view.findViewById( R.id.sélectionRue )
        sélectionCodePostal = view.findViewById( R.id.sélectionCodePostal )

        sélectionRuePrèsDeMoi = view.findViewById( R.id.sélectionRuePrèsDeMoi )

        rechercheTxtRayon = view.findViewById( R.id.rechercheTxtRayon )

        // Configuration bouton et TextView pour navigation
        val buttonFav = view.findViewById<Button>( R.id.buttonfav )
        val textViewAdresse = view.findViewById<TextView>( R.id.txtAdresse )

        buttonFav.setOnClickListener {
            val adresse = textViewAdresse.text.toString()
            val présentateurFavoris = Présentateur( VueFavoris() )
            présentateurFavoris.ajouterNouvelleAdresse( adresse )
            Toast.makeText( requireContext(), "Adresse ajoutée aux favoris : $adresse", Toast.LENGTH_SHORT ).show()
        }


        // Changer contenur du popupRecherche
        //  « CompoundedButton » pas définie ( pas utilisé dans notre cas )
        choisirHeure.setOnCheckedChangeListener { _, cliqué ->
            présentateur.changerContenuPopupRechercheHeure( cliqué )
        }

        choisirAdresse.setOnCheckedChangeListener { _, cliqué ->
            présentateur.changerContenuPopupRechercheAdresse( cliqué )
        }

        choisirPrèsDeMoi.setOnCheckedChangeListener { _, cliqué ->
            présentateur.changerContenuPopupRecherchePrèsDeMoi( cliqué )
        }

        val menuView = requireActivity().findViewById<BottomNavigationView>( R.id.menu_navigation )

        // Afficher tous
        btnTousStationnements.setOnClickListener {
            présentateur.détruireTousMarqueurs()
            présentateur.recupérerTousStationnements()
        }

        // Cacher popup description
        popupBouton.setOnClickListener {
            popupLayout.visibility = View.GONE
        }

        // Cacher popup recherche
        btnFermerPopupRechercheHeure.setOnClickListener {
            popupRecherche.visibility = View.GONE
        }

        btnFermerPopupRechercheAdresse.setOnClickListener {
            popupRecherche.visibility = View.GONE
        }

        btnFermerPopupRecherchePrèsDeMoi.setOnClickListener {
            popupRecherche.visibility = View.GONE
        }

        // Navigation menu
        menuView.setOnNavigationItemSelectedListener { item ->
            // Demande un boolean
            // Source: https://developer.android.com/reference/com/google/android/material/navigation/NavigationBarView.OnItemSelectedListener
            présentateur.changerÉcranCliqueMenu( item.itemId )
        }

        // Position actuelle
        btnPostion.setOnClickListener {
            présentateur.afficherPostionActuelle()
        }

        // Rayon cercle
        btnRayon.setOnClickListener {
            présentateur.détruireTousMarqueurs()
            présentateur.dessinerCercleDepuisPositionActuelle()
        }

        btnDestination.setOnClickListener {
            présentateur.dessinerNavigationEntrePostion()
        }

        btnChoisirHeureDébut.setOnClickListener {
            présentateur.montrerMontreDébut()
        }

        btnChoisirHeurePrévu.setOnClickListener {
            présentateur.montrerMontrePrévu()
        }

        btnOkPopupRechercheHeure.setOnClickListener {
            présentateur.vérifierContenuEtAfficherStationnementParHeure()
            popupRecherche.visibility = View.GONE
        }

        btnOkPopupRechercheAdresse.setOnClickListener {
            présentateur.détruireTousMarqueurs()
            présentateur.afficherStationnementParAdresse( sélectionNuméroMunicipal.selectedItem.toString(), sélectionRue.selectedItem.toString(), sélectionCodePostal.selectedItem.toString() )
            popupRecherche.visibility = View.GONE
        }

        btnOkPopupRecherchePrèsDeMoi.setOnClickListener {
            présentateur.détruireTousMarqueurs()
            présentateur.afficherStationnementsParRue( sélectionRuePrèsDeMoi.selectedItem.toString() )
            popupRecherche.visibility = View.GONE
        }

        présentateur.afficherContenuePourSpinnerNuméroMunicipal()
        présentateur.afficherContenuePourSpinnerCodePostal()
    }

    // Écrit grâce à la documentation officiel de Mapbox - «Markers and annotations»
    // Source: https://docs.mapbox.com/android/maps/guides/annotations/annotations/
    private fun setupMap( style: Style ) {
        style.addImage( "marqueur_rouge", BitmapFactory.decodeResource( resources,
            R.drawable.marqueur_rouge
        ) )

        présentateur.recupérerTousStationnements()

        // Première vue
        présentateur.caméraPremièreInstance()

        // Cliquer sur les marqueurs
        // Écrit grâce à l'exemple de Mapbox - «Add Point Annotations»
        // Source: https://docs.mapbox.com/android/maps/examples/add-point-annotations/
        présentateur.afficherStationnementParId()
    }

    fun montrerPopup( description: String ) {
        popupLayout.findViewById<TextView>( R.id.txtAdresse ).text = description
        popupLayout.visibility = View.VISIBLE
    }
}
