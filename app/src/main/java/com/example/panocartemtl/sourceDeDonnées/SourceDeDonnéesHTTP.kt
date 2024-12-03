package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Stationnement
import okhttp3.OkHttpClient
import kotlin.jvm.Throws
import okhttp3.Request
import java.io.IOException

class SourceDeDonnéesHTTP() : SourceDeDonnées {
    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirTousStationnements( url: String ): List<Stationnement> {
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

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementParId( url: String, id: Int ): Stationnement {
        try {
            val url_complet = "${url}/${id}"
            val client = OkHttpClient()
            val requête = Request.Builder().url( url_complet ).build()
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

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementParHeuresDisponibles(
        url: String,
        heure_début: String,
        heure_prévu: String
    ): List<Stationnement> {
        try {
            val url_complet = "${url}/${heure_début}/${heure_prévu}"
            val client = OkHttpClient()
            val requête = Request.Builder().url( url_complet ).build()
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

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementParAdresse(
        url: String,
        numero_municipal: String,
        rue: String,
        code_postal: String
    ): Stationnement {
        try {
            val url_complet = "${url}/${numero_municipal}/${rue}/${code_postal}"
            val client = OkHttpClient()
            val requête = Request.Builder().url(url_complet).build()
            val réponse = client.newCall(requête).execute()

            if ( réponse.code != 200 ) {
                throw SourceDeDonnéesException( "Erreur: ${réponse.code}" )
            }

            val données = réponse.body

            if ( données == null ) {
                throw SourceDeDonnéesException( "Pas de données reçues" )
            }

            return DécodeurJson.décoderJsonVersStationnement( données.string() )
        } catch (e: IOException) {
            throw SourceDeDonnéesException( e.message ?: "Erreur inconnue" )
        }
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementImage(
        url: String,
        image_url: String
    ): Stationnement {
        try {
            val url_complet = "${url}${image_url}"

            val client = OkHttpClient()
            val requête = Request.Builder().url( url_complet ).build()

            val réponse = client.newCall( requête ).execute()

            if (réponse.code != 200) {
                throw SourceDeDonnéesException( "Erreur: ${réponse.code}" )
            }

            val données = réponse.body
            if ( données == null ) {
                throw SourceDeDonnéesException( "Pas de données reçues" )
            }
            return DécodeurJson.décoderJsonVersStationnement( données.string() )
        } catch ( e: IOException ) {
            // En cas d'erreur, lance une exception avec un message approprié
            throw SourceDeDonnéesException( e.message ?: "Erreur inconnue" )
        }
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirNumerosMunicipauxUniques( url: String ): List<String> {
        try {
            val client = OkHttpClient()
            val requête = Request.Builder().url( url ).build()
            val réponse = client.newCall( requête ).execute()

            if ( réponse.code != 200 ) {
                throw SourceDeDonnéesException( "Erreur: " + réponse.code )
            }

            val données = réponse.body

            if (données == null) {
                throw SourceDeDonnéesException( "Pas de données reçues" )
            }

            return DécodeurJson.décoderListe( données.string() )
        } catch ( e: IOException ) {
            throw SourceDeDonnéesException( e.message ?: "Erreur inconnue" )
        }
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirRuesUniques( url: String, numero_municipal: String ): List<String> {
        try {
            val url_complet = "${url}/${numero_municipal}"
            val client = OkHttpClient()
            val requête = Request.Builder().url( url_complet ).build()
            val réponse = client.newCall( requête ).execute();

            if ( réponse.code != 200 ) {
                throw SourceDeDonnéesException( "Erreur: " + réponse.code )
            }

            val données = réponse.body

            if ( données == null ) {
                throw SourceDeDonnéesException( "Pas de données reçues " )
            }

            return DécodeurJson.décoderListe( données.string() )
        }
        catch ( e: IOException ) {
            throw SourceDeDonnéesException( e.message ?: "Erreur inconnue" )
        }
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirCodesPostalsUniques(
        url: String,
        numero_municipal: String,
        rue: String
    ): List<String> {
        try {
            val url_complet = "${url}/${numero_municipal}/${rue}"
            val client = OkHttpClient()
            val requête = Request.Builder().url( url_complet ).build()
            val réponse = client.newCall( requête ).execute();

            if ( réponse.code != 200 ) {
                throw SourceDeDonnéesException( "Erreur: " + réponse.code )
            }

            val données = réponse.body

            if ( données == null ) {
                throw SourceDeDonnéesException( "Pas de données reçues " )
            }

            return DécodeurJson.décoderListe( données.string() )
        }
        catch ( e: IOException ) {
            throw SourceDeDonnéesException( e.message ?: "Erreur inconnue" )
        }
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementsRayon(
        url: String,
        longitude: Double,
        latitude: Double,
        rayon: String
    ): List<Stationnement> {
        try {
            val url_complet = "${url}/${longitude}/${latitude}/${rayon}"
            val client = OkHttpClient()
            val requête = Request.Builder().url( url_complet ).build()
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
}