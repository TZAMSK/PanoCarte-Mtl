package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Adresse
import com.example.panocartemtl.entitées.Coordonnée
import com.example.panocartemtl.entitées.Stationnement
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

        val résultatObservé = DécodeurJson.décoderJsonVersStationnement( json )
        val donnéeAttendu = Stationnement(1, Adresse("3571", "Rue Beaubien", "H1X 1H1"), Coordonnée(-73.583856, 45.557873), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00")

        assertEquals( donnéeAttendu, résultatObservé)
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