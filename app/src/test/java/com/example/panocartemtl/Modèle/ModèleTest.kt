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
    val adresse_ip = "10.0.0.136:3000"
    val url_host_erreur = "http://${adresse_ip}/..."

    @Test
    fun `étant donné on veut chercher le stationnement avec un id, lorsqu'on cherche un stationnement avec l'id 1 on obtient le stationnement correspondant`() {

        // Code: Interprété par ce code par l'utilisation de « runBlocking »
        // Source: https://proandroiddev.com/testing-kotlin-coroutines-d904738b846d
        runBlocking {
            val résultat_observé = cobaye_instance_modèle.obtenirStationnementParId( 1 )
            val résultat_attendu = Stationnement( 1, Adresse( "3571", "Rue Beaubien", "H1X 1H1" ), Coordonnée( -73.583889, 45.557855 ), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00" )

            assertEquals( résultat_observé, résultat_attendu )
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
    fun `étant donné on veut chercher des stationnements disponibles grâce aux heures fournies, lorsqu'on cherche avec une heure comme 30h00 inexistante on obtient l'erreur 500`() {

        val exception = assertThrows( SourceDeDonnéesException::class.java ) {
            runBlocking {
                cobaye_instance_modèle.obtenirStationnementsParHeuresDisponibles( "01:00", "30:00" )
            }
        }

        assertEquals( "unexpected end of stream on ${url_host_erreur}", exception.message )
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues uniques, on obtient une liste des rues correspondantes`() {
        runBlocking {
            val cobaye_requête = cobaye_instance_modèle.obtenirRuesUniques()

            val résultat_attendu = listOf(
                " BD Saint-Michel", "10e Avenue", "12e Avenue", "20e Avenue", "21e Avenue", "23e Avenue", "24e Avenue",
                "2e Avenue", "3e Avenue", "6e Avenue", "9e Avenue", "Ave L Archevêque", "Avenue Beaconsfield",
                "Avenue du Mont-Royal", "Avenue du Parc", "Avenue Henri-Julien", "Avenue Louis-Hébert", "Avenue Papineau",
                "Avenue Saint-Charles", "Avenue Swail", "Avenue Van Horne", "Bb Rosemont", "Bd Keller", "BD Saint-Michel",
                "Boulevard de Acadie", "Boulevard Gouin", "Boulevard Pie-IX", "Boulevard René-Lévesque", "Boulevard Saint-Joseph",
                "Boulevard Saint-Laurent", "Boulevard Saint-Michel", "Chemin Hudson", "Earnscliffe", "Rue Beaubien",
                "Rue Bélanger", "Rue Charlevoix", "Rue de la Montagne", "Rue de la Visitation", "Rue des Érables", "Rue des Pins",
                "Rue du Champ-d'Eau", "Rue Jarry", "Rue Laurier", "Rue Legendre", "Rue Notre-Dame", "Rue Parc", "Rue Parthenais",
                "Rue Saint-Antoine", "Rue Saint-Denis", "Rue Saint-Hubert", "Rue Saint-Urbain", "Rue Saint-Zotique", "Rue Sherbrooke",
                "St Louis Square St"
            )

            assertEquals( résultat_attendu, cobaye_requête )
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
            val résultat_attendu = Stationnement( 1, Adresse( "3571", "Rue Beaubien", "H1X 1H1" ), Coordonnée( -73.583889, 45.557855 ), "/panneaux_images/SB-AC_NE-181.png", "09:00:00", "12:00:00" )

            assertEquals( résultat_observé, résultat_attendu )
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
    fun `étant donné on veut chercher des stationnements avec un rayon, lorsqu'on cherche avec le rayon de 150 mètre du point (-73,589473,, 45,554418), on obtient des stationnements correspondants`() {
        runBlocking {
            val longitude = -73.589473
            val latitude = 45.554418
            val rayon = "150"
            val cobaye_requête = cobaye_instance_modèle.obtenirStationnementsRayon( longitude, latitude, rayon )

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

    @Test
    fun `étant donné on veut chercher des numéros municipaux uniques, lorsqu'on fait une requête valide avec la rue « 10e Avenue », on obtient une liste vide`() {
        runBlocking {
            val rue = "10e Avenue"
            val cobaye_requête = cobaye_instance_modèle.obtenirNumerosMunicipauxUniques( rue )

            val résultat_attendu = listOf( "6392", "6507" )

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }

    @Test
    fun `étant donné on veut chercher des numéros municipaux uniques, lorsqu'on fait une requête valide avec une rue inexistant, on obtient une liste vide`() {
        runBlocking {
            val rue = "9999"
            val cobaye_requête = cobaye_instance_modèle.obtenirNumerosMunicipauxUniques( rue )

            val résultat_attendu = emptyList<String>()

            assertEquals( cobaye_requête, résultat_attendu )
        }
    }
}