package com.example.panocartemtl.chargement

import kotlinx.coroutines.delay

data class Chargement(val chargementSaut: Int = 9, val limiteChargement: Int = 100, val tempsEntreSaut: Int = 100)

class Modèle{

    // Source: Fonction Lambda -> https://tugce-aras.medium.com/lambda-expression-and-anonymous-function-in-kotlin-53bf408b5699
    // Sous: «Trailing Lambda»
    suspend fun commencerChargement(chargement: Chargement, progèsÀJour: (Int) -> Unit) {
        if (chargement.chargementSaut < 1) {
            throw IllegalArgumentException("Le saut de chargement ne peut pas être négatif !")
        }

        var progrès = 0
        while (progrès < chargement.limiteChargement) {
            progrès += chargement.chargementSaut
            if (progrès > chargement.limiteChargement) {
                progrès = chargement.limiteChargement
            }
            progèsÀJour(progrès)
            delay(chargement.tempsEntreSaut.toLong())
        }
    }
}
