package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Stationnement
import kotlin.jvm.Throws

class SourceDeDonnéesException( message: String) : Exception( message ) {}

interface SourceDeDonnées {

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirTousStationnements( url: String ) : List<Stationnement>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementParId( url: String, id: Int ) : Stationnement

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementParHeuresDisponibles( url: String, heure_début: String, heure_prévu: String ) : List<Stationnement>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementParAdresse( url: String, numero_municipal: String, rue: String, code_postal: String ) : Stationnement

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementImage( url: String, image_url: String ) : Stationnement

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirNumerosMunicipauxUniques( url: String ) : List<String>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirRuesUniques( url: String, numero_municipal: String ) : List<String>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirCodesPostalsUniques( url: String, numero_municipal: String, rue: String ) : List<String>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementsRayon( url: String, longitude: Double, latitude: Double, rayon: String ) : List<Stationnement>
}