package com.example.panocartemtl.chargement

import kotlinx.coroutines.delay

data class Chargement( val chargement_saut: Int = 9, val limite_chargement: Int = 100, val temps_entre_saut: Int = 100 )

class Modèle{

    // Source: Fonction Lambda -> https://tugce-aras.medium.com/lambda-expression-and-anonymous-function-in-kotlin-53bf408b5699
    // Sous: «Trailing Lambda»
    suspend fun commencerChargement( chargement: Chargement, progèsÀJour: (Int) -> Unit ) {
        if ( chargement.chargement_saut < 1 ) {
            throw IllegalArgumentException( "Le saut de chargement ne peut pas être négatif !" )
        }

        var progrès = 0
        while ( progrès < chargement.limite_chargement ) {
            progrès += chargement.chargement_saut
            if ( progrès > chargement.limite_chargement ) {
                progrès = chargement.limite_chargement
            }
            progèsÀJour( progrès )
            delay( chargement.temps_entre_saut.toLong() )
        }
    }
}
