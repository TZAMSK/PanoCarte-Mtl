package com.example.panocartemtl.service

import com.example.panocartemtl.entitées.Stationnement
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnées
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesHTTP

class ObtenirRessourceService(var source: SourceDeDonnées = SourceDeDonnéesHTTP()) {

    suspend fun obtenir_tous_stationnements( url: String ): List<Stationnement> {
        return source.obtenir_tous_stationnements( url )
    }

    suspend fun obtenir_stationnement_par_id( url: String, id: Int ): Stationnement {
        return source.obtenir_stationnement_par_id( url, id )
    }

    suspend fun obtenir_stationnement_par_heures_disponibles(
        url: String,
        heureDébut: String,
        heurePrévu: String
    ): List<Stationnement> {
        return source.obtenir_stationnement_par_heures_disponibles( url, heureDébut, heurePrévu )
    }

    suspend fun obtenir_stationnement_par_adresse(
        url: String,
        numero_municipal: String,
        rue: String,
        code_postal: String
    ): Stationnement {
        return source.obtenir_stationnement_par_adresse( url, numero_municipal, rue, code_postal )
    }

    suspend fun obtenir_stationnement_image(
        url: String,
        image_url: String
    ): Stationnement {
        return source.obtenir_stationnement_image( url, image_url )
    }

    suspend fun obtenir_numeros_municipaux_uniques( url: String ): Array<String> {
        return source.obtenir_numeros_municipaux_uniques( url )
    }

    suspend fun obtenir_rues_uniques( url: String, numero_municipal: String ): Array<String> {
        return source.obtenir_rues_uniques( url, numero_municipal )
    }

    suspend fun obtenir_codes_postals_uniques(
        url: String,
        numero_municipal: String,
        rue: String
    ): Array<String> {
        return source.obtenir_codes_postals_uniques( url, numero_municipal, rue )
    }

    suspend fun obtenir_stationnements_rayon(
        url: String,
        longitude: Double,
        latitude: Double
    ): List<Stationnement> {
        return source.obtenir_stationnements_rayon( url, longitude, latitude )
    }
}