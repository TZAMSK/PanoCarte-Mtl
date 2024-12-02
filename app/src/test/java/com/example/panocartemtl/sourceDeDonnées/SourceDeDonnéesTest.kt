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
                "3571", "3642", "3561", "3370", "6411", "3454", "3535", "3425", "3589", "3617", "2762", "6823", "3603", "3674", "3620", "3660", "3284", "6312",
                "5778", "5364", "6293", "3299", "5984", "6612", "6708", "5892", "6072", "6500", "6507", "6392", "6189", "6756", "6683", "3626", "6050", "6691",
                "5187", "5476", "5601", "6545", "5423", "5678", "6615", "5764", "6128", "6333", "6359", "6474", "5867", "6541", "6115", "6001", "6681", "5600",
                "6498", "5722", "6321", "6752", "5690", "2661", "5448", "5930", "5745", "6420", "6486", "6329", "6175", "5481", "6401", "6412", "5637", "6513",
                "6320", "6414", "6820", "3368", "6981", "6750", "6976", "6609", "6741", "6306"
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
            val numero_municipal = "3571"
            val cobaye_requête = source.obtenirRuesUniques(url_stationnements, numero_municipal)

            val résultat_attendu = listOf(
                "Rue Beaubien", "Bb Rosemont"
            )

            assertEquals(cobaye_requête, résultat_attendu)
        }
    }

    @Test
    fun `étant donné une requête HTTP GET qui cherche des rues uniques pour un numéro municipal, lorsqu'on fait une requête avec un numéro municipal invalide, on obtient une erreur`() {
        val exception = assertThrows(SourceDeDonnéesException::class.java) {
            runBlocking {
                val numero_municipal = "9999"
                source.obtenirRuesUniques(url_stationnements, numero_municipal)
            }
        }

        assertEquals("Erreur: 404", exception.message)
    }
}