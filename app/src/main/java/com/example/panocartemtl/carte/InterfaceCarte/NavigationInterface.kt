package com.example.panocartemtl.carte.InterfaceCarte

interface NavigationInterface {

    fun changerÉcranCliqueMenu(itemId: Int ): Boolean;
    fun changerContenuPopupRechercheHeure( cliqué: Boolean );
    fun changerContenuPopupRechercheAdresse( cliqué: Boolean );
    fun changerContenuPopupRecherchePrèsDeMoi( cliqué: Boolean );
    fun vérifierContenuEtAfficherStationnementParHeure();
}