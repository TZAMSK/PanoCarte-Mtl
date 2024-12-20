package com.example.panocartemtl.chargement

import com.example.panocartemtl.VueChargement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrésentateurChargement(val vue: VueChargement) {
    private val modèle = Modèle()

    fun commencerChargement() {
        val chargement = Chargement()

        CoroutineScope( Dispatchers.Main ).launch {

            // La fonction lambda ici est à l'éxterieur de la méthode commencerChargement qui est le dernier paramètre.
                // À chaque appel le progrès met à jour la progression à chaque chagement recu
            modèle.commencerChargement( chargement ) { progrès ->
                vue.mettreÀjourCercleProgression( progrès )
            }

            vue.naviguerVersCarte()
        }
    }
}