package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Adresse
import com.example.panocartemtl.entitées.Coordonnée
import com.example.panocartemtl.entitées.Stationnement
import com.google.gson.stream.JsonReader
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DécodeurJsonTests {

    // AUCUN TEST AVEC DES SUPERFLUES ET DES DONNÉES NULLES
    @Test
    fun `étant donné un json représentant un stationnement, lorsqu'on le décode on obtient un objet Stationnement correspondant`() {
        val json = """
            {
                "id": "1",
                "adresse": {
                    "numero_municipal": "3571",
                    "rue": "Rue Beaubien",
                    "code_postal": "H1X 1H1"
                },
                "coordonnee": {
                    "longitude": -73.583856,
                    "latitude": 45.557873
                },
                "panneau": "/panneaux_images/SB-AC_NE-181.png",
                "heures_debut": "09:00:00",
                "heures_fin": "12:00:00"
            }
        """.trimIndent()

        val résultat_observé = DécodeurJson.décoderJsonVersStationnement( json )
        val donnée_attendu = Stationnement( 1, Adresse( "3571", "Rue Beaubien", "H1X 1H1" ), Coordonnée( -73.583856, 45.557873), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00" )

        assertEquals( donnée_attendu, résultat_observé )
    }

    @Test
    fun `étant donné un json représentant une liste de stationnements, lorsqu'on le décode on obtient une liste d'objet Stationnement correspondant`() {
        val json = """
            [
                {
                    "id": "1",
                    "adresse": {
                        "numero_municipal": "3571",
                        "rue": "Rue Beaubien",
                        "code_postal": "H1X 1H1"
                    },
                    "coordonnee": {
                        "longitude": -73.583856,
                        "latitude": 45.557873
                    },
                    "panneau": "/panneaux_images/SB-AC_NE-181.png",
                    "heures_debut": "09:00:00",
                    "heures_fin": "12:00:00"
                },
                {
                    "id": "2",
                    "adresse": {
                        "numero_municipal": "3642",
                        "rue": "Rue Beaubien",
                        "code_postal": "H1X 1G2"
                    },
                    "coordonnee": {
                        "longitude": -73.588192,
                        "latitude": 45.557309
                    },
                    "panneau": "/panneaux_images/SB-AC_NE-181.png",
                    "heures_debut": "09:00:00",
                    "heures_fin": "12:00:00"
                }
            ]
        """.trimIndent()

        val résultat_observé = DécodeurJson.décoderJsonVersStationnementsListe( json )
        val donnée_attendu = listOf(
            Stationnement( 1, Adresse( "3571", "Rue Beaubien", "H1X 1H1" ), Coordonnée( -73.583856, 45.557873 ), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00" ),
            Stationnement( 2, Adresse( "3642", "Rue Beaubien", "H1X 1G2" ), Coordonnée( -73.588192, 45.557309 ), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00" )
        )

        assertEquals ( donnée_attendu, résultat_observé )
    }

    @Test
    fun `étant donné un json vide lorsqu'on le décode on obtient une exception de type SourceDeDonnéesException`() {
        val json = ""
        assertFailsWith<SourceDeDonnéesException> {
            DécodeurJson.décoderJsonVersStationnement( json )
        }
    }

    @Test
    fun `étant donné un json invalide lorsqu'on le décode on obtient une exception de type SourceDeDonnéesException`() {
        val json = "{"
        assertFailsWith<SourceDeDonnéesException> {
            DécodeurJson.décoderJsonVersStationnement( json )
        }
    }
}