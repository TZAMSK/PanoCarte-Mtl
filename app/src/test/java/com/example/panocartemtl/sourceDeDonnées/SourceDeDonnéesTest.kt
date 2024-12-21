package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Adresse
import com.example.panocartemtl.entitées.Coordonnée
import com.example.panocartemtl.entitées.Stationnement
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.test.assertEquals

class SourceDeDonnéesTest {

    val source: SourceDeDonnées = SourceDeDonnéesHTTP()
    var adresse_ip: String = "10.0.0.136:3000"
    val url_stationnements = "http://${adresse_ip}/stationnements"
    val url_stationnement = "http://${adresse_ip}/stationnement"
    val url_host_erreur = "http://${adresse_ip}/..."
    val url_numeros_municipaux = "http://${adresse_ip}/numeros_municipaux"
    val url_rues = "http://${adresse_ip}/rues"
    val url_codes_postals = "http://${adresse_ip}/codes_postals"
    val url_rayon = "http://${adresse_ip}/stationnements/rayon"
    val url_rues_rayon = "http://${adresse_ip}/stationnements/recherche/rayon"
    val url_stationnements_rues: String = "http://${adresse_ip}/stationnements"

    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement avec un id, lorsqu'on cherche le stationnement avec id 1, on obtient un objet Stationnement correspondant avec l'id 1`() {
        // Code: Interprété par ce code par l'utilisation de « runBlocking »
        // Source: https://proandroiddev.com/testing-kotlin-coroutines-d904738b846d
        runBlocking {
            val cobaye_requête = source.obtenirStationnementParId( url_stationnement, 1 )

            val résultat_attendu = Stationnement( 1, Adresse( "3571", "Rue Beaubien", "H1X 1H1" ), Coordonnée( -73.583889, 45.557855 ), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00" )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }


    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement avec un id, lorsqu'on cherche un stationnement avec id inconnu, on obtient l'erreur 500`() {
        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                source.obtenirStationnementParId( url_stationnement, 9999 )
            }
        }

        assertEquals( "Erreur: 500", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements disponibles selon un temps prévu, lorsqu'on cherche les stationnements disponibles entre 1h00 et 16h00, on obtient une liste des stationnements disponibles`() {
        runBlocking {
            val cobaye_requête = source.obtenirStationnementParHeuresDisponibles( url_stationnements, "01:00", "15:00" )

            val résultat_attendu = listOf(
                Stationnement( 30, Adresse( "6507", "10e Avenue", "H1Y 2H8" ), Coordonnée( -73.587287, 45.554431 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 31, Adresse( "6392", "10e Avenue", "H1Y 2H10" ), Coordonnée( -73.585912, 45.554026 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 32, Adresse( "6189", "BD Saint-Michel", "H1Y E30" ), Coordonnée( -73.582823, 45.554063 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 33, Adresse( "6756", "20e Avenue", "H1X 2J9" ), Coordonnée( -73.585744, 45.562648 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 34, Adresse( "6683", "21e Avenue", "H1X 2G8" ), Coordonnée( -73.583365, 45.562824 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" ),
                Stationnement( 35, Adresse( "3626", "Rue Saint-Zotique", "H1X 1E6" ), Coordonnée( -73.587382, 45.559318 ), "/panneaux_images/SB-US_NE-2312.png", "18:00:00", "00:00:00" )
            )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche un stationnement disponibles selon un temps prévu fournie, lorsqu'on cherche le stationnement dispnoble avec de fausses heures comme 25h00, on obtient une erreur`() {

        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                source.obtenirStationnementParHeuresDisponibles( url_stationnements, "01:00", "25:00" )
            }
        }

        assertEquals( "unexpected end of stream on ${url_host_erreur}", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des numéros municipaux uniques avec une rue, lorsqu'on fait une requête valide avec la rue « 10e Avenue », on obtient une liste des numéros municipaux`() {
        runBlocking {
            val cobaye_requête = source.obtenirNumerosMunicipauxUniques( url_numeros_municipaux, "10e Avenue" )

            val résultat_attendu = listOf( "6392", "6507" )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des numéros municipaux uniques avec une rue, lorsqu'on fait une requête invalide avec une rue inconnue, on obtient une liste vide`() {
        runBlocking {
            val cobaye_requête = source.obtenirNumerosMunicipauxUniques( url_numeros_municipaux, "Infinitième Avenue" )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues uniques, lorsqu'on fait une requête valide, on obtient une liste des rues`() {
        runBlocking {
            val cobaye_requête = source.obtenirRuesUniques( url_rues )

            val résultat_attendu = listOf(
                "10e Avenue", "12e Avenue", "20e Avenue", "21e Avenue", "23e Avenue", "24e Avenue", "2e Avenue", "3e Avenue", "6e Avenue", "9e Avenue", "Ave L Archevêque", "Avenue Beaconsfield",
                "Avenue du Mont-Royal", "Avenue du Parc", "Avenue Henri-Julien", "Avenue Louis-Hébert", "Avenue Papineau", "Avenue Saint-Charles", "Avenue Swail", "Avenue Van Horne", "Bb Rosemont", "Bd Keller", "BD Saint-Michel",
                "Boulevard de Acadie", "Boulevard Gouin", "Boulevard Pie-IX", "Boulevard René-Lévesque", "Boulevard Saint-Joseph", "Boulevard Saint-Laurent", "Boulevard Saint-Michel", "Chemin Hudson", "Earnscliffe",
                "Rue Beaubien", "Rue Bélanger", "Rue Charlevoix", "Rue de la Montagne", "Rue de la Visitation", "Rue des Érables", "Rue des Pins", "Rue du Champ-d'Eau", "Rue Jarry", "Rue Laurier",
                "Rue Legendre", "Rue Notre-Dame", "Rue Parc", "Rue Parthenais", "Rue Saint-Antoine", "Rue Saint-Denis",
                "Rue Saint-Hubert", "Rue Saint-Urbain", "Rue Saint-Zotique", "Rue Sherbrooke", "St Louis Square St"
            )

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
            val cobaye_requête = source.obtenirCodesPostalsUniques( url_codes_postals, "999999", "Infinitième Avenue De L'Éternel" )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des codes postals, lorsqu'on fait une requête valide avec des données valides qui ne se relient pas, on obtient aucun code postal`() {
        runBlocking {
            val cobaye_requête = source.obtenirCodesPostalsUniques( url_codes_postals, "3642", "Rue Sherbrooke" )

            val résultat_attendu = emptyList<String>()

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
                Stationnement( 41, Adresse( "6545", "9e Avenue", "H1Y 2K7" ),
                    Coordonnée( -73.588465, 45.554125 ),
                    "/panneaux_images/SS-JC_QE-0530.png",
                    "08:00:00",
                    "17:00:00"),
                Stationnement( 81, Adresse( "6609", "9e Avenue", "H1Y 2K8" ),
                Coordonnée( -73.589473, 45.554418 ),
                    "/panneaux_images/SV-PS_NE-1446.png",
                    "13:00:00",
                    "14:00:00"),
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

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues avec un rayon, lorsqu'on cherche avec le rayon de 150 mètre du point (-73,589473,, 45,554418), on obtient la rue « 9e Avenue »`() {
        runBlocking {
            val longitude = -73.589473
            val latitude = 45.554418
            val rayon = "150"
            val cobaye_requête = source.obtenirRuesUniquesRayon( url_rues_rayon, longitude, latitude, rayon )

            val résultat_attendu = listOf("9e Avenue")

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues avec un rayon, lorsqu'on cherche avec le rayon de 0 mètre du point (-73,589473,, 45,554418), on obtient aucune rue`() {
        runBlocking {
            val longitude = -73.589473
            val latitude = 45.554418
            val rayon = "0"
            val cobaye_requête = source.obtenirRuesUniquesRayon( url_rues_rayon, longitude, latitude, rayon )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues avec un rayon, lorsqu'on cherche avec le rayon de 1000 km du point (28,976829,, 41,005362), La Mosqué Bleu, on obtient aucun stationnement`() {
        runBlocking {
            val longitude = 28.976829
            val latitude = 41.005362
            val rayon = "1000000"
            val cobaye_requête = source.obtenirRuesUniquesRayon( url_rayon, longitude, latitude, rayon )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements avec une rue fournie, lorsqu'on cherche les stationnements avec la rue « Earnscliffe », on obtient une liste des stationnements`() {
        runBlocking {
            val cobaye_requête = source.obtenirStationnementsParRue( url_stationnements_rues, "Earnscliffe" )

            val résultat_attendu = listOf(
                Stationnement( 67, Adresse( "5210", "Earnscliffe", "H3X 2P5" ), Coordonnée( -73.632844, 45.482929 ), "/panneaux_images/SV-JB_QE-0377.png", "08:00:00", "16:00:00" )
            )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des stationnements avec une rue fournie, lorsqu'on cherche les stationnements avec une rue inexistante comme « Rue inconnu », on obtient une liste vide`() {
        runBlocking {
            val cobaye_requête = source.obtenirStationnementsParRue( url_stationnements_rues, "Rue inconnu" )

            val résultat_attendu = emptyList<Stationnement>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }
}