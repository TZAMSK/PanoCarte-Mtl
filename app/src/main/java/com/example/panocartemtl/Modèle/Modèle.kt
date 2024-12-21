package com.example.panocartemtl.Modèle

import com.example.panocartemtl.entitées.Stationnement
import com.example.panocartemtl.service.ObtenirRessourceService
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnées
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesException
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesHTTP
import java.time.LocalDate
import kotlin.jvm.Throws

class Modèle private constructor (
    // À mettre votre Wireless LAN de votre ipv4
    // ipconfig dans le terminal
    // Source: Source: https://stackoverflow.com/questions/4779963/how-can-i-access-my-localhost-from-my-android-device
    var adresse_ip: String = "10.0.0.136:3000",
    override var url_stationnements : String = "http://${adresse_ip}/stationnements",
    override var url_stationnement : String = "http://${adresse_ip}/stationnement",
    override var url_image : String = "http://${adresse_ip}/panneaux_images",
    override var url_numéros_municipaux: String = "http://${adresse_ip}/numeros_municipaux",
    override var url_rues: String = "http://${adresse_ip}/rues",
    override var url_codes_postals: String = "http://${adresse_ip}/codes_postals",
    override var url_rayon: String = "http://${adresse_ip}/stationnements/rayon",
    override var url_rues_rayon: String = "http://${adresse_ip}/stationnements/recherche/rayon",
    override var url_stationnements_rues: String = "http://${adresse_ip}/stationnements",


    source: SourceDeDonnées = SourceDeDonnéesHTTP()
) : IModèle {
    private val stationnements = mutableListOf<com.example.panocartemtl.entitées.Stationnement>()
    var source: SourceDeDonnées = source
        set( value ){
            field = value
        }

    companion object {
        var instance = Modèle()
    }

    val obtenirRessourceService = ObtenirRessourceService( source )
    fun getStationnementSimulés(): List<Stationnement> = stationnements.toList()
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
        val stationnement_reçue = obtenirRessourceService.obtenirStationnementParId( url_stationnement, id )

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
    override suspend fun obtenirNumerosMunicipauxUniques( rue: String ): List<String> {
        val liste_numéro_municipaux_reçue = obtenirRessourceService.obtenirNumerosMunicipauxUniques( url_numéros_municipaux, rue )

        return liste_numéro_municipaux_reçue
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirRuesUniques(): List<String> {
        val liste_rues_reçue = obtenirRessourceService.obtenirRuesUniques( url_rues )

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

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenirRuesUniquesRayon(
        longitude: Double,
        latitude: Double,
        rayon: String
    ): List<String> {
        val rues_reçues = obtenirRessourceService.obtenirRuesUniquesRayon( url_rues_rayon, longitude, latitude, rayon )

        return rues_reçues
    }

    override suspend fun obtenirStationnementsParRue(rue: String): List<Stationnement> {
        val stationnements_reçues = obtenirRessourceService.obtenirStationnementsParRue( url_stationnements_rues, rue )

        return stationnements_reçues
    }

    override fun ajouterStationnement(stationnement: Stationnement) {
        stationnements.add(stationnement)
    }

    override fun associerDate(
        index: Int,
        stationnements: MutableList<Stationnement>,
        date: LocalDate
    ) {
        if (index in stationnements.indices) {
            stationnements[index].dateSelectionnee = date
        } else {
            throw IndexOutOfBoundsException("Index de stationnement invalide : $index")
        }
    }

    override fun supprimerStationnement(index: Int, stationnements: MutableList<Stationnement>) {
        if (index in stationnements.indices) {
            stationnements.removeAt(index)
        } else {
            throw IndexOutOfBoundsException("Index de stationnement invalide : $index")
        }
    }

    override fun mettreAJourStationnements(stationnements: List<Stationnement>) {
        this.stationnements.clear()
        this.stationnements.addAll(stationnements)
    }
}

