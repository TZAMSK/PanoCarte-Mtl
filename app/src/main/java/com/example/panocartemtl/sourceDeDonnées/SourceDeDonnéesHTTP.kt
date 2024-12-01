package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Stationnement
import okhttp3.OkHttpClient
import kotlin.jvm.Throws
import okhttp3.Request
import java.io.IOException

class SourceDeDonnéesHTTP() : SourceDeDonnées {
    @Throws(SourceDeDonnéesException::class)
    override suspend fun obtenir_tous_stationnements( url: String ): List<Stationnement> {
        try {
            val client = OkHttpClient()
            val requête = Request.Builder().url( url ).build()
            val réponse = client.newCall( requête ).execute();

            if ( réponse.code != 200 ) {
                throw SourceDeDonnéesException( "Erreur: " + réponse.code )
            }

            val données = réponse.body

            if ( données == null ) {
                throw SourceDeDonnéesException( "Pas de données reçues " )
            }

            return DécodeurJson.décoderJsonVersStationnementsListe( données.string() )
        }
        catch ( e: IOException ) {
            throw SourceDeDonnéesException( e.message ?: "Erreur inconnue" )
        }
    }

    @Throws(SourceDeDonnéesException::class)
    override suspend fun obtenir_stationnement_par_id( url: String, id: Int ): Stationnement {
        try {
            val urlComplet = "${url}/${id}"
            val client = OkHttpClient()
            val requête = Request.Builder().url( urlComplet ).build()
            val réponse = client.newCall( requête ).execute();

            if ( réponse.code != 200 ) {
                throw SourceDeDonnéesException( "Erreur: " + réponse.code )
            }

            val données = réponse.body

            if ( données == null ) {
                throw SourceDeDonnéesException( "Pas de données reçues " )
            }

            return DécodeurJson.décoderJsonVersStationnement( données.string() )
        }
        catch ( e: IOException ) {
            throw SourceDeDonnéesException( e.message ?: "Erreur inconnue" )
        }
    }

    @Throws(SourceDeDonnéesException::class)
    override suspend fun obtenir_stationnement_par_heures_disponibles(
        url: String,
        heureDébut: String,
        heurePrévu: String
    ): List<Stationnement> {
        try {
            val urlComplet = "${url}/${heureDébut}/${heurePrévu}"
            val client = OkHttpClient()
            val requête = Request.Builder().url( urlComplet ).build()
            val réponse = client.newCall( requête ).execute();

            if ( réponse.code != 200 ) {
                throw SourceDeDonnéesException( "Erreur: " + réponse.code )
            }

            val données = réponse.body

            if ( données == null ) {
                throw SourceDeDonnéesException( "Pas de données reçues " )
            }

            return DécodeurJson.décoderJsonVersStationnementsListe( données.string() )
        }
        catch ( e: IOException ) {
            throw SourceDeDonnéesException( e.message ?: "Erreur inconnue" )
        }
    }

    @Throws(SourceDeDonnéesException::class)
    override suspend fun obtenir_stationnement_par_adresse(
        url: String,
        numero_municipal: String,
        rue: String,
        code_postal: String
    ): Stationnement {
        TODO("Not yet implemented")
    }

    @Throws(SourceDeDonnéesException::class)
    override suspend fun obtenir_stationnement_image(
        url: String,
        image_url: String
    ): Stationnement {
        TODO("Not yet implemented")
    }

    @Throws(SourceDeDonnéesException::class)
    override suspend fun obtenir_numeros_municipaux_uniques( url: String ): Array<String> {
        TODO("Not yet implemented")
    }

    @Throws(SourceDeDonnéesException::class)
    override suspend fun obtenir_rues_uniques( url: String, numero_municipal: String ): Array<String> {
        TODO("Not yet implemented")
    }

    @Throws(SourceDeDonnéesException::class)
    override suspend fun obtenir_codes_postals_uniques(
        url: String,
        numero_municipal: String,
        rue: String
    ): Array<String> {
        TODO("Not yet implemented")
    }

    @Throws(SourceDeDonnéesException::class)
    override suspend fun obtenir_stationnements_rayon(
        url: String,
        longitude: Double,
        latitude: Double
    ): List<Stationnement> {
        TODO("Not yet implemented")
    }
}