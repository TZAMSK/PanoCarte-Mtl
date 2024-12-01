package com.example.panocartemtl.Modèle

import com.example.panocartemtl.entitées.Stationnement
import com.example.panocartemtl.service.ObtenirRessourceService
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnées
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesException
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesHTTP
import kotlin.jvm.Throws

class Modèle private constructor (
    override var url_stationnements : String = "http://localhost:8080/stationnements",
    override var url_stationnement : String = "http://localhost:8080/stationnement",
    override var url_image : String = "http://localhost:8080/panneaux_images",
    override var url_numéros_municipal: String = "http://localhost:8080/numeros_municpaux",
    override var url_rues: String = "http://localhost:8080/rues",
    override var url_codes_postals: String = "http://localhost:8080/codes_postals",
    override var url_rayon: String = "http://localhost:8080/rayon",
    source: SourceDeDonnées = SourceDeDonnéesHTTP()
) : IModèle {

    var source: SourceDeDonnées = source
        set(value){
            field = value
        }

    private var taille_téléchargement : Int = 0

    var stationnements = listOf<Stationnement>()

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
    override suspend fun obtenir_tous_stationnements(): List<Stationnement> {
        val stationnements_reçues = obtenirRessourceService.obtenir_tous_stationnements( url_stationnements )

        return stationnements_reçues
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnement_par_id(id: Int): Stationnement {
        val stationnement_reçue = obtenirRessourceService.obtenir_stationnement_par_id( url_stationnements, id )

        return stationnement_reçue
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnements_par_heures_disponibles(
        heure_début: String,
        heure_prévu: String
    ): List<Stationnement> {
        val stationnements_reçues = obtenirRessourceService.obtenir_stationnement_par_heures_disponibles( url_stationnements, heure_début, heure_prévu)

        return stationnements_reçues
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnement_par_adresse(
        numéro_municipal: String,
        rue: String,
        code_postal: String
    ): Stationnement {
        TODO("Not yet implemented")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnement_image(url_image: String): List<Stationnement> {
        TODO("Not yet implemented")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_numeros_municipaux_uniques(): List<String> {
        TODO("Not yet implemented")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_rues_uniques(numéro_municipal: String): List<String> {
        val stationnements_reçues = obtenirRessourceService.obtenir_rues_uniques( url_rues, numéro_municipal )

        return stationnements_reçues
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_codes_postals_uniques(
        numéro_municipal: String,
        rue: String
    ): List<String> {
        TODO("Not yet implemented")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnements_rayon(
        longitude: String,
        latitude: String
    ): List<Stationnement> {
        TODO("Not yet implemented")
    }
}