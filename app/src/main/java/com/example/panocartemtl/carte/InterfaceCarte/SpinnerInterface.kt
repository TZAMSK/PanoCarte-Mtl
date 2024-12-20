package com.example.panocartemtl.carte.InterfaceCarte

interface SpinnerInterface {

    suspend fun récuperListeNumérosMunicipaux( rue: String ): List<String>;
    suspend fun récuperListeRues(): List<String>;
    suspend fun récuperListeCodesPostal( numéro_municipal: String, rue: String ): List<String>;

    suspend fun récuperListeRuesRayon( longitude: Double, latitude: Double, rayon: String ): List<String>;

    fun afficherContenuePourSpinnerNuméroMunicipal();
    fun afficherContenuePourSpinnerCodePostal();
}