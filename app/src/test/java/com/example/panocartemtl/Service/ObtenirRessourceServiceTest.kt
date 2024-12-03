package com.example.panocartemtl.Service

import com.example.panocartemtl.entitées.Adresse
import com.example.panocartemtl.entitées.Coordonnée
import com.example.panocartemtl.entitées.Stationnement
import com.example.panocartemtl.service.ObtenirRessourceService
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnées
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesException
import com.example.panocartemtl.sourceDeDonnées.SourceDeDonnéesHTTP
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.test.assertEquals

class ObtenirRessourceServiceTest {

    val service_cobaye = ObtenirRessourceService()
    val url_stationnements = "http://10.0.0.136:3000/stationnements"
    val url_codes_postals = "http://10.0.0.136:3000/codes_postals"
    val url_host_erreur = "http://10.0.0.136:3000/..."
    val url_rayon = "http://10.0.0.136:3000/stationnements/rayon"
    val url_rues = "http://10.0.0.136:3000/rues"
    val url_numéros_municipaux = "http://10.0.0.136:3000/numeros_municpaux"

    @Test
    fun `étant donné une recherche d'un stationnement avec un id, lorsqu'on cherche le stationnement avec id 1, on obtient un objet Stationnement correspondant avec l'id 1`() {
        // Code: Interprété par ce code par l'utilisation de « runBlocking »
        // Source: https://proandroiddev.com/testing-kotlin-coroutines-d904738b846d
        runBlocking {
            val cobaye_requête = service_cobaye.obtenirStationnementParId( url_stationnements, 1 )

            val résultatAttendu = Stationnement( 1, Adresse( "3571", "Rue Beaubien", "H1X 1H1" ), Coordonnée( -73.583856, 45.557873 ), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00" )

            assertEquals( cobaye_requête, résultatAttendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement avec un id, lorsqu'on cherche un stationnement avec id inconnu, on obtient l'erreur 500`() {

        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                service_cobaye.obtenirStationnementParId( url_stationnements, 9999 )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des codes postals, lorsqu'on fait une requête avec le numéro municipal 6507 et rue 10e Avenue, on obtient le code postal « H1Y 2H8 »`() {
        runBlocking {
            val cobaye_requête = service_cobaye.obtenirCodesPostalsUniques( url_codes_postals, "6507", "10e Avenue" )

            val résultat_attendu = listOf( "H1Y 2H8" )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des codes postals, lorsqu'on fait une requête valide avec des données invalides qui ne se relie pas, on obtient aucun code postal`() {
        runBlocking {
            val cobaye_requête = service_cobaye.obtenirCodesPostalsUniques( url_codes_postals, "999999", "Infinième Avenue De L'Éternel" )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des codes postals, lorsqu'on fait une requête valide avec des données valide qui ne se relie pas, on obtient aucun code postal`() {
        runBlocking {
            val cobaye_requête = service_cobaye.obtenirCodesPostalsUniques( url_codes_postals, "3642", "Rue Sherbrooke" )

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
            val cobaye_requête = service_cobaye.obtenirStationnementParAdresse( url_stationnements, numero_municipal, rue, code_postal )

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
                service_cobaye.obtenirStationnementParAdresse( url_stationnements, numero_municipal, rue, code_postal )
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
                service_cobaye.obtenirStationnementParAdresse( url_stationnements, numero_municipal, rue, code_postal )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements discponibles selon un temps prévu, lorsqu'on cherche entre 1h00 et 16h00, on obtient une liste des stationnements disponibles`() {

        runBlocking {
            val cobaye_requête = service_cobaye.obtenirStationnementParHeuresDisponibles( url_stationnements, "01:00", "15:00" )

            val résultatAttendu = listOf(
                Stationnement( 30, Adresse( "6507", "10e Avenue", "H1Y 2H8" ), Coordonnée( -73.58783, 45.5546 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 31, Adresse( "6392", "10e Avenue", "H1Y 2H10" ), Coordonnée( -73.588532, 45.553961 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 32, Adresse( "6189", "BD Saint-Michel", "H1Y E30" ), Coordonnée( -73.582765, 45.553116 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 33, Adresse( "6756", "20e Avenue", "H1X 2J9" ), Coordonnée( -73.586228, 45.562443 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 34, Adresse( "6683", "21e Avenue", "H1X 2G8" ), Coordonnée( -73.584062, 45.562188 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 35, Adresse( "3626", "Rue Saint-Zotique", "H1X 1E6" ), Coordonnée( -73.588248, 45.559659 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" )
            )

            assertEquals( cobaye_requête, résultatAttendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements discponibles selon un temps prévu fournie, lorsqu'on cherche avec de fausses heures comme 25h00, on obtient une erreur`() {

        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                service_cobaye.obtenirStationnementParHeuresDisponibles( url_stationnements, "01:00", "25:00" )
            }
        }

        assertEquals( "unexpected end of stream on ${url_host_erreur}", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements avec un rayon, lorsqu'on cherche avec le rayon de 150 mètre du point (-73,589473,, 45,554418), on obtient des stationnements correspondants`() {
        runBlocking {
            val longitude = -73.589473
            val latitude = 45.554418
            val rayon = "150"
            val cobaye_requête = service_cobaye.obtenirStationnementsRayon( url_rayon, longitude, latitude, rayon )

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
            val cobaye_requête = service_cobaye.obtenirStationnementsRayon( url_rayon, longitude, latitude, rayon )

            val résultat_attendu = emptyList<Stationnement>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements avec un rayon, lorsqu'on cherche avec le rayon de 1000 km du point (28,976829,, 41,005362), La Mosqué Bleu, on obtient aucun stationnement`() {
        runBlocking {
            val longitude = 28.976829
            val latitude = 41.005362
            val rayon = "100000"
            val cobaye_requête = service_cobaye.obtenirStationnementsRayon( url_rayon, longitude, latitude, rayon )

            val résultat_attendu = emptyList<Stationnement>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues uniques, lorsqu'on fournit un numéro municipal valide, on obtient une liste des rues correspondantes`() {
        runBlocking {
            val numero_municipal = "3571"
            val résultat_observé = service_cobaye.obtenirRuesUniques(url_rues, numero_municipal)

            val résultat_attendu = listOf("Rue Beaubien", "Bb Rosemont")

            assertEquals(résultat_attendu, résultat_observé)
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues uniques, lorsqu'on fournit un numéro municipal inexistant, on obtient une erreur 500`() {
        val exception = assertThrows(SourceDeDonnéesException::class.java) {
            runBlocking {
                val numero_municipal = "9999"
                service_cobaye.obtenirRuesUniques(url_rues, numero_municipal)
            }
        }

        assertEquals("Erreur: 500", exception.message)
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des numéros municipaux uniques, lorsqu'on fournit la bonne requête avec un lien valide, on obtient la liste des numéros municipaux`() {
        runBlocking {
            val résultat_observé = service_cobaye.obtenirNumerosMunicipauxUniques(url_numéros_municipaux)

            val résultat_attendu = listOf(
                "3571", "3642", "3561", "3370", "6411", "3454", "3535", "3425", "3589", "3617", "2762", "6823",
                "3603", "3674", "3620", "3660", "3284", "6312", "5778", "5364", "6293", "3299", "5984", "6612",
                "6708", "5892", "6072", "6500", "6507", "6392", "6189", "6756", "6683", "3626", "6050", "6691",
                "5187", "5476", "5601", "6545", "5423", "5678", "6615", "5764", "6128", "6333", "6359", "6474",
                "5867", "6541", "6115", "6001", "6681", "5600", "6498", "5722", "6321", "6752", "5690", "2661",
                "5448", "5930", "5745", "6420", "6486", "6329", "6175", "5481", "6401", "6412", "5637", "6513",
                "6320", "6414", "6820", "3368", "6981", "6750", "6976", "6609", "6741", "6306"
            )

            assertEquals(résultat_attendu, résultat_observé)
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des numéros municipaux uniques, lorsqu'on fournit une mauvaise requête avec un lien invalide, on obtient une erreur 500`() {
        val exception = assertThrows(SourceDeDonnéesException::class.java) {
            runBlocking {
                val url_numéros_municipaux_exemple = "http://10.0.0.136:3000/exemple_numeros_municpaux_invalide"
                service_cobaye.obtenirNumerosMunicipauxUniques(url_numéros_municipaux_exemple)
            }
        }

        assertEquals("Erreur: 500", exception.message)
    }

}