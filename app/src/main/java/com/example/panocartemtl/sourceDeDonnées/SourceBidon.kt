package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Adresse
import com.example.panocartemtl.entitées.Coordonnée
import com.example.panocartemtl.entitées.Stationnement
import kotlin.jvm.Throws

class SourceBidon: SourceDeDonnées {

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_tous_stationnements( url: String ): List<Stationnement> {
        return listOf(
            Stationnement(1, Adresse("6400", "16e Avenue", "H1X 2S9"), Coordonnée(-73.123456, 45.123456), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00"),
            Stationnement(2, Adresse("6401", "17e Avenue", "H1X 3S9"), Coordonnée(-73.789012, 45.789012), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00")
        )
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnement_par_id( url: String, id: Int ): Stationnement {
        return Stationnement(3, Adresse("6402", "18e Avenue", "H1X 4S9"), Coordonnée(-73.345678, 45.345678), "/panneaux_images/SS-JL_NE-2119.png", "09:00:00", "21:00:00")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnement_par_heures_disponibles(
        url: String,
        heureDébut: String,
        heurePrévu: String
    ): List<Stationnement> {
        TODO("Not yet implemented")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnement_par_adresse(
        url: String,
        numero_municipal: String,
        rue: String,
        code_postal: String
    ): Stationnement {
        TODO("Not yet implemented")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnement_image(
        url: String,
        image_url: String
    ): Stationnement {
        TODO("Not yet implemented")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_numeros_municipaux_uniques(url: String): Array<String> {
        TODO("Not yet implemented")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_rues_uniques(
        url: String,
        numero_municipal: String
    ): Array<String> {
        return arrayOf("1000", "1001", "1002", "1003", "1004")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_codes_postals_uniques(
        url: String,
        numero_municipal: String,
        rue: String
    ): Array<String> {
        return arrayOf("10e Avenue", "11e Avenue", "12e Avenue", "13e Avenue", "14e Avenue")
    }

    @Throws( SourceDeDonnéesException::class )
    override suspend fun obtenir_stationnements_rayon(
        url: String,
        longitude: Double,
        latitude: Double
    ): List<Stationnement> {
        TODO("Not yet implemented")
    }
}