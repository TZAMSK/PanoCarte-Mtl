package com.example.panocartemtl.carte.PrésentateurGestionnaire

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.carte.InterfaceCarte.SpinnerInterface
import com.example.panocartemtl.carte.VueCarte
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GestionSpinner( var vue: VueCarte, val iocontext: CoroutineContext = Dispatchers.IO ): SpinnerInterface {

    var modèle = Modèle.instance

    override suspend fun récuperListeNumérosMunicipaux( rue: String ): List<String> {
        return withContext( iocontext ) {
            modèle.obtenirNumerosMunicipauxUniques( rue )
        }
    }

    override suspend fun récuperListeRues(): List<String> {
        return withContext( iocontext ) {
            modèle.obtenirRuesUniques()
        }
    }

    override suspend fun récuperListeCodesPostal(
        numéro_municipal: String,
        rue: String
    ): List<String> {
        return withContext( iocontext ) {
            modèle.obtenirCodesPostalsUniques( numéro_municipal, rue )
        }
    }

    override fun afficherContenuePourSpinnerNuméroMunicipal() {
        // Source: https://www.geeksforgeeks.org/spinner-in-kotlin/
        vue.sélectionRue.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected( parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long ) {
                val rue = vue.sélectionRue.selectedItem.toString()

                CoroutineScope( Dispatchers.Main ).launch {
                    mettreÀJourSpinnerNuméroMunicipal( rue )
                }
            }

            override fun onNothingSelected( parentView: AdapterView<*> ) {}
        }
    }

    override fun afficherContenuePourSpinnerCodePostal() {
        vue.sélectionNuméroMunicipal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected( parentView: AdapterView<*>, selectedItemView: View?, position: Int, id: Long ) {
                val numéro_municipal = vue.sélectionNuméroMunicipal.selectedItem.toString()
                val rue = vue.sélectionRue.selectedItem.toString()

                CoroutineScope( Dispatchers.Main ).launch {
                    mettreÀJourSpinnerCodePostal( numéro_municipal, rue)
                }
            }

            override fun onNothingSelected( parentView: AdapterView<*> ) {}
        }
    }

    suspend fun instancierSpinnerRue() {
        val liste_rues = récuperListeRues()

        withContext( Dispatchers.Main ) {
            val adaptateur = ArrayAdapter( vue.requireContext(), android.R.layout.simple_spinner_dropdown_item, liste_rues )
            vue.sélectionRue.adapter = adaptateur
        }
    }

    suspend fun mettreÀJourSpinnerNuméroMunicipal( rue: String ) {
        val liste_numéros_municipaux = récuperListeNumérosMunicipaux( rue )

        withContext( Dispatchers.Main ) {
            val adaptateur = ArrayAdapter( vue.requireContext(), android.R.layout.simple_spinner_dropdown_item, liste_numéros_municipaux )
            vue.sélectionNuméroMunicipal.adapter = adaptateur
        }
    }

    suspend fun mettreÀJourSpinnerCodePostal( numéro_municipal: String, rue: String ) {
        val liste_codes_postaux = récuperListeCodesPostal( numéro_municipal, rue )

        withContext( Dispatchers.Main ) {
            val adaptateur = ArrayAdapter( vue.requireContext(), android.R.layout.simple_spinner_dropdown_item, liste_codes_postaux )
            vue.sélectionCodePostal.adapter = adaptateur
        }
    }
}