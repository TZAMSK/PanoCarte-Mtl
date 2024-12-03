package com.example.panocartemtl.Modèle

import com.example.panocartemtl.entitées.Adresse
import com.example.panocartemtl.entitées.Coordonnée
import com.example.panocartemtl.entitées.Stationnement
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.test.assertEquals

class ModèleTest {

    val cobaye_instance_modèle = Modèle.instance
    val url_host_erreur = "http://10.0.0.136:3000/..."

    @Test
    fun `étant donné on veut chercher le stationnement avec un id, lorsqu'on cherche un stationnement avec l'id 1 on obtient le stationnement correspondant`() {

        // Code: Interprété par ce code par l'utilisation de « runBlocking »
        // Source: https://proandroiddev.com/testing-kotlin-coroutines-d904738b846d
        runBlocking {
            val résultat_observé = cobaye_instance_modèle.obtenirStationnementParId( 1 )
            val donnée_attendu = Stationnement( 1, Adresse( "3571", "Rue Beaubien", "H1X 1H1" ), Coordonnée( -73.583856, 45.557873 ), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00" )

            assertEquals( donnée_attendu, résultat_observé )
        }
    }

    @Test
    fun `étant donné on veut chercher le stationnement avec un id, lorsqu'on cherche un stationnement avec un id inexistant comme 9999 on obtient l'erreur 500`() {

        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                cobaye_instance_modèle.obtenirStationnementParId( 9999 )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné on veut chercher des stationnements disponibles grâce aux heures fournies, lorsqu'on cherche entre 1h et 16h on obtient la listes des stationnements discponibles`() {

        runBlocking {
            val cobaye_requête = cobaye_instance_modèle.obtenirStationnementsParHeuresDisponibles( "01:00", "15:00" )

            val résultat_attendu = listOf(
                Stationnement( 30, Adresse( "6507", "10e Avenue", "H1Y 2H8" ), Coordonnée( -73.58783, 45.5546 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 31, Adresse( "6392", "10e Avenue", "H1Y 2H10" ), Coordonnée( -73.588532, 45.553961 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 32, Adresse( "6189", "BD Saint-Michel", "H1Y E30" ), Coordonnée( -73.582765, 45.553116 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 33, Adresse( "6756", "20e Avenue", "H1X 2J9" ), Coordonnée( -73.586228, 45.562443 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 34, Adresse( "6683", "21e Avenue", "H1X 2G8" ), Coordonnée( -73.584062, 45.562188 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 35, Adresse( "3626", "Rue Saint-Zotique", "H1X 1E6" ), Coordonnée( -73.588248, 45.559659 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" )
            )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné on veut chercher des stationnements disponibles grâce aux heures fournies, lorsqu'on cherche avec une heure comme 30h00 inexistante on obtient l'erreur 500`() {

        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                cobaye_instance_modèle.obtenirStationnementsParHeuresDisponibles( "01:00", "30:00" )
            }
        }

        assertEquals( "unexpected end of stream on ${url_host_erreur}", exception.message )
    }

    @Test
    fun `étant donné on veut chercher toutes les rues correspondates à un numéro municipal fournie, lorsqu'on cherche les rues qui partagent le code municipal 3571 on obtient Rue Beaubien et Bb Rosemont`() {
        runBlocking {
            val cobaye_requête = cobaye_instance_modèle.obtenirRuesUniques( "3571" )

            val résultat_attendu = listOf( "Bb Rosemont", "Rue Beaubien" )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné on veut chercher des codes postals, lorsqu'on fait une requête avec le numéro municipal 6507 et rue 10e Avenue, on obtient le code postal « H1Y 2H8 »`() {
        runBlocking {
            val cobaye_requête = cobaye_instance_modèle.obtenirCodesPostalsUniques( "6507", "10e Avenue" )

            val résultat_attendu = listOf( "H1Y 2H8" )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné on veut chercher des codes postals, lorsqu'on fait une requête valide avec des données invalides qui ne se relie pas, on obtient aucun code postal`() {
        runBlocking {
            val cobaye_requête = cobaye_instance_modèle.obtenirCodesPostalsUniques( "999999", "Infinième Avenue De L'Éternel" )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné on veut chercher des codes postals, lorsqu'on fait une requête valide avec des données valide qui ne se relie pas, on obtient aucun code postal`() {
        runBlocking {
            val cobaye_requête = cobaye_instance_modèle.obtenirCodesPostalsUniques( "3642", "Rue Sherbrooke" )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une adresse valide, lorsqu'on cherche un stationnement par adresse, on obtient le stationnement correspondant`() {
        runBlocking {
            val résultat_observé = cobaye_instance_modèle.obtenirStationnementParAdresse("3571", "Rue Beaubien", "H1X 1H1")
            val donnée_attendu = Stationnement(1, Adresse("3571", "Rue Beaubien", "H1X 1H1"), Coordonnée(-73.583856, 45.557873), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00")

            assertEquals(donnée_attendu, résultat_observé)
        }
    }

    @Test
    fun `étant donné on veut chercher un stationnement par adresse, lorsqu'on fournit un adresse avec des données inexistante, on obtient l'erreur 500`() {
        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            val numero_municipal = "1"
            val rue = "1 Rue des Nuages"
            val code_postal = "H0H OHO"

            runBlocking {
                cobaye_instance_modèle.obtenirStationnementParAdresse( numero_municipal, rue, code_postal )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné on veut chercher un stationnement par adresse, lorsqu'on fournit un adresse avec des données existante, mais qui ne sont pas reliés, on obtient l'erreur 500`() {
        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            val numero_municipal = "3571"
            val rue = "3e Avenue"
            val code_postal = "H3J 1G1"

            runBlocking {
                cobaye_instance_modèle.obtenirStationnementParAdresse( numero_municipal, rue, code_postal )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné une URL d'image valide, lorsqu'on cherche un stationnement par image, on obtient le stationnement correspondant`() {
        runBlocking {
            val urlImageValide = "/panneaux_images/SB-US_NE-2312.png"
            val résultat_observé = cobaye_instance_modèle.obtenirStationnementImage(urlImageValide)
            val donnée_attendu = Stationnement(
                id = 1,
                adresse = Adresse("3571", "Rue Beaubien", "H1X 1H1"),
                coordonnée = Coordonnée(-73.583856, 45.557873),
                panneau = "/panneaux_images/SB-US_NE-2312.png",
                heures_début = "09:00:00",
                heures_fin = "12:00:00"
            )

            assertEquals(donnée_attendu, résultat_observé)
        }
    }

    @Test
    fun `étant donné une URL d'image invalide, lorsqu'on cherche un stationnement par image, une exception est levée`() {
        val urlImageInvalide = "/panneaux_images/image_invalide.png"

        val exception = assertThrows(SourceDeDonnéesException::class.java) {
            runBlocking {
                cobaye_instance_modèle.obtenirStationnementImage(urlImageInvalide)
            }
        }

        assertEquals("Erreur: Ressource introuvable pour l'image $urlImageInvalide", exception.message)
    }

    @Test
    fun `étant donné on veut chercher des stationnements avec un rayon, lorsqu'on cherche avec le rayon de 150 mètre du point (-73,589473,, 45,554418), on obtient des stationnements correspondants`() {
        runBlocking {
            val longitude = -73.589473
            val latitude = 45.554418
            val rayon = "150"
            val cobaye_requête = cobaye_instance_modèle.obtenirStationnementsRayon( longitude, latitude, rayon )

            val résultat_attendu = listOf(
                Stationnement( 6, Adresse( "3454", "Rue Beaubien", "H1X 1G1"),
                    Coordonnée(-73.589946, 45.556087),
                    "/panneaux_images/SB-DB_NE-223.png",
                    "13:00:00",
                    "15:30:00"),
                Stationnement( 30, Adresse( "6507", "10e Avenue", "H1Y 2H8"),
                    Coordonnée(-73.58783, 45.5546),
                    "/panneaux_images/SB-US_NE-2312.png",
                    "18:00:00",
                    "00:00:00"),
                Stationnement( 31, Adresse( "6392", "10e Avenue", "H1Y 2H10"),
                    Coordonnée(-73.588532, 45.553961),
                    "/panneaux_images/SB-US_NE-2312.png",
                    "18:00:00",
                    "00:00:00"),
            )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné on veut chercher des stationnements avec un rayon, lorsqu'on cherche avec le rayon de 0 mètre du point (-73,589473,, 45,554418), on obtient aucune stationnement`() {
        runBlocking {
            val longitude = -73.589473
            val latitude = 45.554418
            val rayon = "0"
            val cobaye_requête = cobaye_instance_modèle.obtenirStationnementsRayon( longitude, latitude, rayon )

            val résultat_attendu = emptyList<Stationnement>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné on veut chercher des stationnements avec un rayon, lorsqu'on cherche avec le rayon de 1000 km du point (28,976829,, 41,005362), La Mosqué Bleu, on obtient aucun stationnement`() {
        runBlocking {
            val longitude = 28.976829
            val latitude = 41.005362
            val rayon = "100000"
            val cobaye_requête = cobaye_instance_modèle.obtenirStationnementsRayon( longitude, latitude, rayon )

            val résultat_attendu = emptyList<Stationnement>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }
}