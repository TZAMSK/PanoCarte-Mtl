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
    suspend fun obtenirTousStationnements(): List<Stationnement>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementParId( id : Int ): Stationnement

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementsParHeuresDisponibles( heure_début : String, heure_prévu : String ) : List<Stationnement>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementParAdresse( numéro_municipal : String, rue : String, code_postal: String ) : Stationnement

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementImage( url_image : String ) : Stationnement

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirNumerosMunicipauxUniques() : List<String>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirRuesUniques( numéro_municipal: String ) : List<String>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirCodesPostalsUniques( numéro_municipal: String, rue: String ) : List<String>

    @Throws( SourceDeDonnéesException::class )
    suspend fun obtenirStationnementsRayon( longitude: Double, latitude: Double, rayon: String ) : List<Stationnement>
}