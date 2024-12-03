package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Adresse
import com.example.panocartemtl.entitées.Coordonnée
import com.example.panocartemtl.entitées.Stationnement
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class SourceDeDonnéesTest {

    val source: SourceDeDonnées = SourceDeDonnéesHTTP()
    val url_stationnements = "http://10.0.0.136:3000/stationnements"
    val url_host_erreur = "http://10.0.0.136:3000/..."
    val url_numeros_municipaux = "http://10.0.0.136:3000/numeros_municipaux"
    val url_rues = "http://10.0.0.136:3000/rues"
    val url_codes_postals = "http://10.0.0.136:3000/codes_postals"
    val url_rayon = "http://10.0.0.136:3000/stationnements/rayon"

    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement avec un id, lorsqu'on cherche le stationnement avec id 1, on obtient un objet Stationnement correspondant avec l'id 1`() {
        // Code: Interprété par ce code par l'utilisation de « runBlocking »
        // Source: https://proandroiddev.com/testing-kotlin-coroutines-d904738b846d
        runBlocking {
            val cobaye_requête = source.obtenirStationnementParId( url_stationnements, 1 )

            val résultat_attendu = Stationnement( 1, Adresse( "3571", "Rue Beaubien", "H1X 1H1" ), Coordonnée( -73.583856, 45.557873 ), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00" )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }


    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement avec un id, lorsqu'on cherche un stationnement avec id inconnu, on obtient l'erreur 500`() {
        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                source.obtenirStationnementParId( url_stationnements, 9999 )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements discponibles selon un temps prévu, lorsqu'on cherche les stationnements disponibles entre 1h00 et 16h00, on obtient une liste des stationnements disponibles`() {
        runBlocking {
            val cobaye_requête = source.obtenirStationnementParHeuresDisponibles( url_stationnements, "01:00", "15:00" )

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
    fun `étant donné une requête HTTP GET qui cherche un stationnement discponibles selon un temps prévu fournie, lorsqu'on cherche le stationnement dispnoble avec de fausses heures comme 25h00, on obtient une erreur`() {

        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                source.obtenirStationnementParHeuresDisponibles( url_stationnements, "01:00", "25:00" )
            }
        }

        assertEquals( "unexpected end of stream on ${url_host_erreur}", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement par adresse, lorsqu'on cherche un stationnement avec une adresse donnée, on obtient le stationnement correspondant`() {

        runBlocking {
            val cobaye_requête = source.obtenirStationnementParAdresse(
                url_stationnements,
                "3571",
                "Rue Beaubien",
                "H1X 1H1"
            )

            val résultat_attendu = Stationnement(
                1,
                Adresse("3571", "Rue Beaubien", "H1X 1H1"),
                Coordonnée(-73.583856, 45.557873),
                "/panneaux_images/SB-AC_NE-181.png",
                "09:00:00",
                "12:00:00"
            )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des numéros municipaux uniques, lorsqu'on fait une requête valide, on obtient une liste des numéros municipaux`() {
        runBlocking {
            val cobaye_requête = source.obtenirNumerosMunicipauxUniques( url_numeros_municipaux )

            val résultat_attendu = listOf(
                "2661", "2762", "3284", "3299", "3368", "3370", "3425", "3454", "3535", "3561", "3571", "3589", "3603", "3617", "3620",
                "3626", "3642", "3660", "3674", "5187", "5364", "5423", "5448", "5476", "5481", "5600", "5601", "5637", "5678", "5690",
                "5722", "5745", "5764", "5778", "5867", "5892", "5930", "5984", "6001", "6050", "6072", "6115", "6128", "6175", "6189",
                "6293", "6306", "6312", "6320", "6321", "6329", "6333", "6359", "6392", "6401", "6411", "6412", "6414", "6420", "6474",
                "6486", "6498", "6500", "6507", "6513", "6541", "6545", "6609", "6612", "6615", "6681", "6683", "6691", "6708", "6741",
                "6750", "6752", "6756", "6820", "6823", "6976", "6981"
            )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues uniques pour un numéro municipal, lorsqu'on fait une requête valide avec numéro municipal, on obtient une liste des rues`() {
        runBlocking {
            val numero_municipal = "3571"
            val cobaye_requête = source.obtenirRuesUniques( url_rues, numero_municipal )

            val résultat_attendu = listOf(
                "Bb Rosemont", "Rue Beaubien"
            )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues uniques pour un numéro municipal, lorsqu'on fait une requête valide avec un numéro municipal inexistant, on obtient une liste vide`() {
        runBlocking {
            val numero_municipal = "9999"
            val cobaye_requête = source.obtenirRuesUniques( url_rues, numero_municipal )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des codes postals, lorsqu'on fait une requête avec le numéro municipal 6507 et rue 10e Avenue, on obtient le code postal « H1Y 2H8 »`() {
        runBlocking {
            val cobaye_requête = source.obtenirCodesPostalsUniques( url_codes_postals, "6507", "10e Avenue" )

            val résultat_attendu = listOf( "H1Y 2H8" )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des codes postals, lorsqu'on fait une requête valide avec des données invalides qui ne se relie pas, on obtient aucun code postal`() {
        runBlocking {
            val cobaye_requête = source.obtenirCodesPostalsUniques( url_codes_postals, "999999", "Infinième Avenue De L'Éternel" )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des codes postals, lorsqu'on fait une requête valide avec des données valide qui ne se relie pas, on obtient aucun code postal`() {
        runBlocking {
            val cobaye_requête = source.obtenirCodesPostalsUniques( url_codes_postals, "3642", "Rue Sherbrooke" )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement par adresse, lorsqu'on fournit une adresse valide, on obtient un objet Stationnement correspondant`() {
        runBlocking {
            val numero_municipal = "3571"
            val rue = "Rue Beaubien"
            val code_postal = "H1X 1H1"
            val cobaye_requête = source.obtenirStationnementParAdresse( url_stationnements, numero_municipal, rue, code_postal )

            val résultat_attendu = Stationnement(
                1,
                Adresse(numero_municipal, rue, code_postal),
                Coordonnée(-73.583856, 45.557873),
                "/panneaux_images/SB-AC_NE-181.png",
                "09:00:00",
                "12:00:00"
            )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement par adresse, lorsqu'on fournit un adresse avec des données inexistante, on obtient l'erreur 500`() {
        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            val numero_municipal = "1"
            val rue = "1 Rue des Nuages"
            val code_postal = "H0H OHO"

            runBlocking {
                source.obtenirStationnementParAdresse( url_stationnements, numero_municipal, rue, code_postal )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement par adresse, lorsqu'on fournit un adresse avec des données existante, mais qui ne sont pas reliés, on obtient l'erreur 500`() {
        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            val numero_municipal = "3571"
            val rue = "3e Avenue"
            val code_postal = "H3J 1G1"

            runBlocking {
                source.obtenirStationnementParAdresse( url_stationnements, numero_municipal, rue, code_postal )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement par adresse, lorsqu'on fournit une adresse invalide, on obtient une erreur`() {
        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                val numero_municipal = "9999"
                val rue = "Rue Invalide"
                val code_postal = "H1X 9H9"
                source.obtenirStationnementParAdresse( url_stationnements, numero_municipal, rue, code_postal )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements avec un rayon, lorsqu'on cherche avec le rayon de 150 mètre du point (-73,589473,, 45,554418), on obtient des stationnements correspondants`() {
        runBlocking {
            val longitude = -73.589473
            val latitude = 45.554418
            val rayon = "150"
            val cobaye_requête = source.obtenirStationnementsRayon( url_rayon, longitude, latitude, rayon )

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
    fun `étant donné une requête HTTP GET qui cherche des stationnements avec un rayon, lorsqu'on cherche avec le rayon de 0 mètre du point (-73,589473,, 45,554418), on obtient aucune stationnement`() {
        runBlocking {
            val longitude = -73.589473
            val latitude = 45.554418
            val rayon = "0"
            val cobaye_requête = source.obtenirStationnementsRayon( url_rayon, longitude, latitude, rayon )

            val résultat_attendu = emptyList<Stationnement>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements avec un rayon, lorsqu'on cherche avec le rayon de 1000 km du point (28,976829,, 41,005362), La Mosqué Bleu, on obtient aucun stationnement`() {
        runBlocking {
            val longitude = 28.976829
            val latitude = 41.005362
            val rayon = "1000000"
            val cobaye_requête = source.obtenirStationnementsRayon( url_rayon, longitude, latitude, rayon )

            val résultat_attendu = emptyList<Stationnement>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }
}