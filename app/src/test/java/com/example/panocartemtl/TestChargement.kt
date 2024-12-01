package com.example.panocartemtl

import com.example.panocartemtl.chargement.Chargement
import com.example.panocartemtl.chargement.Modèle
import kotlinx.coroutines.runBlocking

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestChargement {
    @Test
    fun `test étant donné l'application nouvellement instanciée, lorsqu'on démarre, le chargement fait des sauts de 10, on obtient une liste de chiffre divisible par 10`() {

        val cobaye_modèle = Modèle()
        val cobaye_chargement = Chargement( chargement_saut = 10, limite_chargement = 100, temps_entre_saut = 0 )

        val liste_pourcentage_progression_donnée = mutableListOf<Int>()

        runBlocking {
            cobaye_modèle.commencerChargement( cobaye_chargement ) { progrès ->
                if ( progrès % 10 == 0 ) {
                    liste_pourcentage_progression_donnée.add( progrès )
                }
            }
        }

        val liste_pourcentage_progression_voulue = listOf( 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 )
        assertEquals( liste_pourcentage_progression_voulue, liste_pourcentage_progression_donnée )
    }

    @Test
    fun `test étant donné l'application nouvellement instanciée, lorsqu'on démarre, le chargement fait des sauts de 5, on obtient une liste de chiffre divisible par 5`() {

        val cobaye_modèle = Modèle()
        val cobaye_chargement = Chargement( chargement_saut = 5, limite_chargement = 100, temps_entre_saut = 0 )

        val liste_pourcentage_progression_donnée = mutableListOf<Int>()

        runBlocking {
            cobaye_modèle.commencerChargement( cobaye_chargement ) { progrès ->
                if ( progrès % 5 == 0 ) {
                    liste_pourcentage_progression_donnée.add( progrès )
                }
            }
        }

        val liste_pourcentage_progression_voulue = listOf( 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100 )
        assertEquals( liste_pourcentage_progression_voulue, liste_pourcentage_progression_donnée )
    }

    @Test
    fun `test étant donné l'application nouvellement instanciée, lorsqu'on démarre, le chargement fait des sauts de 50 mais avec une limite qui à 1000, on obtient une liste de chiffre divisible par 50 et 20 exactement dans la liste`() {

        val cobaye_modèle = Modèle()
        val cobaye_chargement = Chargement( chargement_saut = 50, limite_chargement = 1000, temps_entre_saut = 0 )

        val liste_pourcentage_progression_donnée = mutableListOf<Int>()

        runBlocking {
            cobaye_modèle.commencerChargement( cobaye_chargement ) { progrès ->
                if ( progrès % 50 == 0 ) {
                    liste_pourcentage_progression_donnée.add( progrès )
                }
            }
        }

        val liste_pourcentage_progression_voulue = listOf( 50, 100, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600, 650, 700, 750, 800, 850, 900, 950, 1000 )
        assertEquals( liste_pourcentage_progression_voulue, liste_pourcentage_progression_donnée )

        val nombreDeChiffres = liste_pourcentage_progression_voulue.size
        assertEquals( 20, nombreDeChiffres )
    }

    @Test
    fun `test étant donné l'application nouvellement instanciée, lorsqu'on démarre, le chargement fait des sauts de -5,un chiffre négatif, on obtient une erreur de message «Le saut de chargement ne peut pas être négatif !»`() {
        val cobaye_modèle = Modèle()
        val cobaye_chargement = Chargement( chargement_saut = -5, limite_chargement = 100, temps_entre_saut = 0 )

        val exception = assertThrows( IllegalArgumentException::class.java ) {
            runBlocking {
                cobaye_modèle.commencerChargement( cobaye_chargement ) { progrès -> }
            }
        }

        assertEquals( "Le saut de chargement ne peut pas être négatif !", exception.message )
    }
}