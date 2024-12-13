package com.example.panocartemtl.carte

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VueCarte : Fragment() {
    private lateinit var popupLayout: View
    private lateinit var popupRecherche: View
    private lateinit var btnPostion: Button
    lateinit var txtRayon: EditText
    private lateinit var txtRecherche: AutoCompleteTextView
    lateinit var btnRayon: ImageView
    private lateinit var popupBouton: Button
    private lateinit var btnFermerPopupRechercheHeure: Button
    lateinit var btnOkPopupRechercheHeure: Button
    private lateinit var btnFermerPopupRechercheAdresse: Button
    lateinit var btnOkPopupRechercheAdresse: Button
    lateinit var btnChoisirHeureDébut: Button
    lateinit var btnChoisirHeurePrévu: Button
    private lateinit var navController: NavController
    lateinit var mapView: MapView
    private lateinit var annotationPlugin: AnnotationPlugin
    lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var positionClient: FusedLocationProviderClient
    private lateinit var btnDestination: ImageView
    private lateinit var choisirHeure: ToggleButton
    private lateinit var choisirAdresse: ToggleButton
    private lateinit var heureInsértionTexteHeure: LinearLayout
    private lateinit var heureInsértionTexteAdresse: LinearLayout
    private lateinit var btnTousStationnements: Button
    lateinit var imageStationnement: ImageView
    lateinit var sélectionNuméroMunicipal: Spinner
    lateinit var sélectionRue: Spinner
    lateinit var sélectionCodePostal: Spinner
    lateinit var checkBoxRayon: CheckBox
    lateinit var checkBoxTxtRayon: EditText

    val modèle = Modèle.instance
    val présentateur = PrésentateurCarte(this )

    // Position actuelle

    // Écrit grâce à la documentation officiel de Mapbox - «User Location»
    // Source: https://docs.mapbox.com/android/maps/guides/user-location/location-on-map/

    // Aussissous «Request permissions»
    // Source: https://developer.android.com/training/permissions/requesting
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permis: Boolean ->
            if (permis == true) {
                getPositionActuelle()
            } else {
                Toast.makeText(requireContext(),
                    R.string.autorisation_de_la_position_est_requise, Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_carte, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = view.findViewById(R.id.map)
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) { style ->
            annotationPlugin = mapView.annotations
            pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
            setupMap(style)
        }

        positionClient = LocationServices.getFusedLocationProviderClient(requireContext())

        navController = findNavController()

        // Instancier les composantes
        popupLayout = view.findViewById(R.id.popupLayout)
        popupRecherche = view.findViewById(R.id.popupRecherche)
        popupBouton = view.findViewById(R.id.popupBouton)
        btnPostion = view.findViewById(R.id.btnPositionActuelle)
        txtRayon = view.findViewById(R.id.txtRayon)
        btnRayon = view.findViewById(R.id.btnRayon)
        btnDestination = view.findViewById(R.id.btnDestination)
        btnFermerPopupRechercheHeure = view.findViewById(R.id.btnFermerPopupRechercheHeure)
        btnOkPopupRechercheHeure = view.findViewById(R.id.btnOkPopupRechercheHeure)
        btnChoisirHeureDébut = view.findViewById(R.id.btnChoisirHeureDébut)
        btnChoisirHeurePrévu = view.findViewById(R.id.btnChoisirHeurePrévu)
        btnFermerPopupRechercheAdresse = view.findViewById(R.id.btnFermerPopupRechercheAdresse)
        btnOkPopupRechercheAdresse = view.findViewById(R.id.btnOkPopupRechercheAdresse)
        btnTousStationnements = view.findViewById(R.id.btnTousStationnements)
        choisirHeure = view.findViewById(R.id.choisirHeure)
        choisirAdresse = view.findViewById(R.id.choisirAdresse)
        heureInsértionTexteHeure = view.findViewById(R.id.heureInsértionTexteHeure)
        heureInsértionTexteAdresse = view.findViewById(R.id.heureInsértionTexteAdresse)
        imageStationnement = view.findViewById(R.id.imageStationnement)
        sélectionNuméroMunicipal = view.findViewById(R.id.sélectionNuméroMunicipal)
        sélectionRue = view.findViewById(R.id.sélectionRue)
        sélectionCodePostal = view.findViewById(R.id.sélectionCodePostal)
        checkBoxRayon = view.findViewById(R.id.checkBoxRayon)
        checkBoxTxtRayon = view.findViewById(R.id.checkBoxTxtRayon)



        // Changer contenur du popupRecherche
        choisirHeure.setOnCheckedChangeListener { _, cliqué ->
            if (cliqué) {
                heureInsértionTexteHeure.visibility = View.VISIBLE
                heureInsértionTexteAdresse.visibility = View.GONE
                choisirAdresse.isChecked = false
            }
        }

        choisirAdresse.setOnCheckedChangeListener { _, cliqué ->
            if (cliqué) {
                heureInsértionTexteAdresse.visibility = View.VISIBLE
                heureInsértionTexteHeure.visibility = View.GONE
                choisirHeure.isChecked = false
            }
        }

        val menuView = requireActivity().findViewById<BottomNavigationView>(R.id.menu_navigation)

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

        // Navigation menu
        menuView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_carte -> true
                R.id.navigation_recherche -> {
                    popupRecherche.visibility = View.VISIBLE
                    true
                }
                R.id.navigation_favoris -> {
                    navController.navigate(R.id.action_fragment_carte_vers_fragment_favoris)
                    true
                }
                else -> false
            }
        }

        // Position actuelle
        btnPostion.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                getPositionActuelle()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        // Rayon cercle
        btnRayon.setOnClickListener {
            présentateur.détruireTousMarqueurs()

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Ressemble à btnPostion.setOnClickListener mais avec position actuelle
                positionClient.lastLocation.addOnSuccessListener { position: Location? ->
                    if (position != null) {
                        val positionActuelle = Point.fromLngLat(position.longitude, position.latitude)
                        présentateur.dessinerCercle( Point.fromLngLat( positionActuelle.longitude(), positionActuelle.latitude() )  )
                    }
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        btnDestination.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED) {
                positionClient.lastLocation.addOnSuccessListener { position: Location? ->
                    if (position != null) {
                        val positionActuelle = Point.fromLngLat(position.longitude, position.latitude)
                        présentateur.navigationEntrePostion(positionActuelle)
                    }
                }
            } else {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        btnChoisirHeureDébut.setOnClickListener {
            présentateur.montrerMontreDébut()
        }

        btnChoisirHeurePrévu.setOnClickListener {
            présentateur.montrerMontrePrévu()
        }

        btnOkPopupRechercheHeure.setOnClickListener {
            if (présentateur.vérifierBoutonsHeureRempli() == true) {
                présentateur.détruireTousMarqueurs()
                présentateur.afficherStationnementsParHeure()
            }

            popupRecherche.visibility = View.GONE
        }

        btnOkPopupRechercheAdresse.setOnClickListener {
            présentateur.détruireTousMarqueurs()
            présentateur.afficherStationnementParAdresse( sélectionNuméroMunicipal.selectedItem.toString(), sélectionRue.selectedItem.toString(), sélectionCodePostal.selectedItem.toString() )
            popupRecherche.visibility = View.GONE
        }

        // Source: https://www.geeksforgeeks.org/spinner-in-kotlin/
        sélectionRue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected( parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long ) {
                val rue = sélectionRue.selectedItem.toString()

                CoroutineScope( Dispatchers.Main ).launch {
                    présentateur.mettreÀJourSpinnerNuméroMunicipal( rue )
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        sélectionNuméroMunicipal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected( parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long ) {
                val numéro_municipal = sélectionNuméroMunicipal.selectedItem.toString()
                val rue = sélectionRue.selectedItem.toString()

                CoroutineScope( Dispatchers.Main ).launch {
                    présentateur.mettreÀJourSpinnerCodePostal( numéro_municipal, rue)
                }
            }

            override fun onNothingSelected( parentView: AdapterView<*> ) {}
        }

    }

    private fun getPositionActuelle() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            positionClient.lastLocation.addOnSuccessListener { position: Location? ->
                if (position != null) {
                    val positionActuelle = Point.fromLngLat(position.longitude, position.latitude)

                    mapView.getMapboxMap().setCamera(
                        CameraOptions.Builder()
                            .center(positionActuelle)
                            .zoom(14.97)
                            .build()
                    )
                }
            }
        } else {
            Toast.makeText(requireContext(),
                R.string.autorisation_de_la_position_actuelle_n_a_pas_été_accordée, Toast.LENGTH_SHORT).show()
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Écrit grâce à la documentation officiel de Mapbox - «Markers and annotations»
    // Source: https://docs.mapbox.com/android/maps/guides/annotations/annotations/
    private fun setupMap(style: Style) {
        style.addImage("marqueur_rouge", BitmapFactory.decodeResource(resources,
            R.drawable.marqueur_rouge
        ))

        présentateur.recupérerTousStationnements()

        // Première vue
        présentateur.caméraPremièreInstance()

        // Cliquer sur les marqueurs
        // Écrit grâce à l'exemple de Mapbox - «Add Point Annotations»
        // Source: https://docs.mapbox.com/android/maps/examples/add-point-annotations/
        présentateur.afficherStationnementParId()
    }

    fun montrerPopup(description: String) {
        popupLayout.findViewById<TextView>(R.id.txtAdresse).text = description
        popupLayout.visibility = View.VISIBLE
    }
}
