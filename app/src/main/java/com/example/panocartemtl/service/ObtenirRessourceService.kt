package com.example.panocartemtl.service

import com.example.panocartemtl.entitées.Stationnement
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnées
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesHTTP

class ObtenirRessourceService( var source: SourceDeDonnées = SourceDeDonnéesHTTP() ) {

    suspend fun obtenirTousStationnements( url: String ): List<Stationnement> {
        return source.obtenirTousStationnements( url )
    }

    suspend fun obtenirStationnementParId( url: String, id: Int ): Stationnement {
        return source.obtenirStationnementParId( url, id )
    }

    suspend fun obtenirStationnementParHeuresDisponibles(
        url: String,
        heureDébut: String,
        heurePrévu: String
    ): List<Stationnement> {
        return source.obtenirStationnementParHeuresDisponibles( url, heureDébut, heurePrévu )
    }

    suspend fun obtenirStationnementParAdresse(
        url: String,
        numero_municipal: String,
        rue: String,
        code_postal: String
    ): Stationnement {
        return source.obtenirStationnementParAdresse( url, numero_municipal, rue, code_postal )
    }

    suspend fun obtenirStationnementImage(
        url: String,
        image_url: String
    ): Stationnement {
        return source.obtenirStationnementImage( url, image_url )
    }

    suspend fun obtenirNumerosMunicipauxUniques( url: String ): List<String> {
        return source.obtenirNumerosMunicipauxUniques( url )
    }

    suspend fun obtenirRuesUniques( url: String, numero_municipal: String ): List<String> {
        return source.obtenirRuesUniques( url, numero_municipal )
    }

    suspend fun obtenirCodesPostalsUniques(
        url: String,
        numero_municipal: String,
        rue: String
    ): List<String> {
        return source.obtenirCodesPostalsUniques( url, numero_municipal, rue )
    }

    suspend fun obtenirStationnementsRayon(
        url: String,
        longitude: Double,
        latitude: Double,
        rayon: String
    ): List<Stationnement> {
        return source.obtenirStationnementsRayon( url, longitude, latitude, rayon )
    }
}