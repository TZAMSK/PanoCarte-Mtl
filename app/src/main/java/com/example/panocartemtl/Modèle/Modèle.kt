package com.example.panocartemtl.Modèle

import com.example.panocartemtl.entitées.Stationnement
import com.example.panocartemtl.service.ObtenirRessourceService
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnées
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesException
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesHTTP
import kotlin.jvm.Throws

class Modèle private constructor (
    // À mettre votre Wireless LAN de votre ipv4
    // ipconfig dans le terminal
    // Source: Source: https://stackoverflow.com/questions/4779963/how-can-i-access-my-localhost-from-my-android-device
    override var url_stationnements : String = "http://10.0.0.136:3000/stationnements",
    override var url_stationnement : String = "http://10.0.0.136:3000/stationnement",
    override var url_image : String = "http://10.0.0.136:3000/panneaux_images",
    override var url_numéros_municipaux: String = "http://10.0.0.136:3000/numeros_municipaux",
    override var url_rues: String = "http://10.0.0.136:3000/rues",
    override var url_codes_postals: String = "http://10.0.0.136:3000/codes_postals",
    override var url_rayon: String = "http://10.0.0.136:3000/stationnements/rayon",
    source: SourceDeDonnées = SourceDeDonnéesHTTP()
) : IModèle {

    var source: SourceDeDonnées = source
        set( value ){
            field = value
        }

    companion object {
        var instance = Modèle()
    }

    val obtenirRessourceService = ObtenirRessourceService( source )

    /**
     * Obtient une certaine quantité de données de la source
     *
     * @return un bloc de données obtenues.
     * @throws SourceDeDonnéesException en cas de problème à la lecture des données
     */
    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirTousStationnements(): List<Stationnement> {
        val stationnements_reçues = obtenirRessourceService.obtenirTousStationnements( url_stationnements )

        return stationnements_reçues
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementParId( id: Int ): Stationnement {
        val stationnement_reçue = obtenirRessourceService.obtenirStationnementParId( url_stationnements, id )

        return stationnement_reçue
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementsParHeuresDisponibles(
        heure_début: String,
        heure_prévu: String
    ): List<Stationnement> {
        val stationnements_reçues = obtenirRessourceService.obtenirStationnementParHeuresDisponibles( url_stationnements, heure_début, heure_prévu )

        return stationnements_reçues
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementParAdresse(
        numéro_municipal: String,
        rue: String,
        code_postal: String
    ): Stationnement {
        val stationnement_reçue = obtenirRessourceService.obtenirStationnementParAdresse( url_stationnements, numéro_municipal, rue, code_postal )

        return stationnement_reçue
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementImage( url_image: String ): Stationnement {
        return obtenirRessourceService.obtenirStationnementImage( url_stationnements,url_image )
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirNumerosMunicipauxUniques(): List<String> {
        val liste_numéro_municipaux_reçue = obtenirRessourceService.obtenirNumerosMunicipauxUniques( url_numéros_municipaux )

        return liste_numéro_municipaux_reçue
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirRuesUniques( numéro_municipal: String ): List<String> {
        val liste_rues_reçue = obtenirRessourceService.obtenirRuesUniques( url_rues, numéro_municipal )

        return liste_rues_reçue
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirCodesPostalsUniques(
        numéro_municipal: String,
        rue: String
    ): List<String> {
        val liste_codes_postals_reçue = obtenirRessourceService.obtenirCodesPostalsUniques( url_codes_postals, numéro_municipal, rue )

        return liste_codes_postals_reçue
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirStationnementsRayon(
        longitude: Double,
        latitude: Double,
        rayon: String
    ): List<Stationnement> {
        val stationnements_reçues = obtenirRessourceService.obtenirStationnementsRayon( url_rayon, longitude, latitude, rayon )

        return stationnements_reçues
    }
}