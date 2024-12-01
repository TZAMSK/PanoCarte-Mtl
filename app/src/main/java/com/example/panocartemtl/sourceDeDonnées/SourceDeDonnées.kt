package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Stationnement
import kotlin.jvm.Throws

class SourceDeDonnéesException( message: String) : Exception( message ) {}

interface SourceDeDonnées {
    @Throws(SourceDeDonnéesException::class)
    suspend fun obtenir_tous_stationnements( url: String ) : Array<Stationnement>

    @Throws(SourceDeDonnéesException::class)
    suspend fun obtenir_stationnement_par_id( url: String, id: Int ) : Stationnement

    @Throws(SourceDeDonnéesException::class)
    suspend fun obtenir_stationnement_par_heures_disponibles( url: String, heureDébut: String, heurePrévu: String ) : Array<Stationnement>

    @Throws(SourceDeDonnéesException::class)
    suspend fun obtenir_stationnement_par_adresse( url: String, numero_municipal: String, rue: String, code_postal: String ) : Stationnement

    @Throws(SourceDeDonnéesException::class)
    suspend fun obtenir_stationnement_image( url: String, image_url: String ) : Stationnement

    @Throws(SourceDeDonnéesException::class)
    suspend fun obtenir_numeros_municipaux_uniques( url: String ) : Array<String>

    @Throws(SourceDeDonnéesException::class)
    suspend fun obtenir_rues_uniques( url: String, numero_municipal: String ) : Array<String>

    @Throws(SourceDeDonnéesException::class)
    suspend fun obtenir_codes_postals_uniques( url: String, numero_municipal: String, rue: String ) : Array<String>

    @Throws(SourceDeDonnéesException::class)
    suspend fun obtenir_stationnements_rayon( url: String, longitude: Double, latitude: Double ) : Array<String>
}