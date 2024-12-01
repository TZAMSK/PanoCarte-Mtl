package com.example.panocartemtl.Modèle

import com.example.panocartemtl.entitées.Stationnement
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesException

interface IModèle {
    var url_stationnements : String
    var url_stationnement : String
    var url_image : String
    var url_numéros_municipal: String
    var url_rues: String
    var url_codes_postals: String
    var url_rayon: String

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenir_tous_stationnements(): List<Stationnement>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenir_stationnement_par_id(id : Int): Stationnement

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenir_stationnements_par_heures_disponibles( heure_début : String, heure_prévu : String ) : List<Stationnement>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenir_stationnement_par_adresse( numéro_municipal : String, rue : String, code_postal: String ) : Stationnement

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenir_stationnement_image( url_image : String ) : List<Stationnement>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenir_numeros_municipaux_uniques() : List<String>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenir_rues_uniques( numéro_municipal: String ) : List<String>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenir_codes_postals_uniques( numéro_municipal: String, rue: String ) : List<String>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenir_stationnements_rayon( longitude: String, latitude: String ) : List<Stationnement>
}