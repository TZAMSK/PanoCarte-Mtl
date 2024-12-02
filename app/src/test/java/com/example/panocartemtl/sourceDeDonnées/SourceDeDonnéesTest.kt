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
    val url_stationnements = "http://10.0.0.136:3000/stationnements"
    val url_host_erreur = "http://10.0.0.136:3000/..."

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
    fun `étant donné une requête HTTP GET qui cherche des numéros municipaux uniques, lorsqu'on fait une requête valide, on obtient une liste des numéros municipaux`() {
        runBlocking {
            val cobaye_requête = source.obtenirNumerosMunicipauxUniques(url_stationnements)

            val résultat_attendu = listOf(
                "1234", "5678", "91011", "1213", "1415"
            )

            assertEquals(cobaye_requête, résultat_attendu)
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des numéros municipaux uniques, lorsqu'on fait une requête invalide, on obtient une erreur`() {
        val exception = assertThrows(SourceDeDonnéesException::class.java) {
            runBlocking {
                source.obtenirNumerosMunicipauxUniques(url_stationnements)
            }
        }

        assertEquals("Erreur: 404", exception.message)
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues uniques pour un numéro municipal, lorsqu'on fait une requête valide avec numéro municipal, on obtient une liste des rues`() {
        runBlocking {
            val numero_municipal = "1234"
            val cobaye_requête = source.obtenirRuesUniques(url_stationnements, numero_municipal)

            val résultat_attendu = listOf(
                "Rue Beaubien", "Rue Saint-Michel", "Boulevard Saint-Laurent"
            )

            assertEquals(cobaye_requête, résultat_attendu)
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues uniques pour un numéro municipal, lorsqu'on fait une requête avec un numéro municipal invalide, on obtient une erreur`() {
        val exception = assertThrows(SourceDeDonnéesException::class.java) {
            runBlocking {
                val numero_municipal = "0000"
                source.obtenirRuesUniques(url_stationnements, numero_municipal)
            }
        }

        assertEquals("Erreur: 404", exception.message)
    }
}