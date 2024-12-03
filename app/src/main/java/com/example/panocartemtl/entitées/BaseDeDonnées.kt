package com.example.panocartemtl.entitées

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class BaseDeDonnées( context: Context ): SQLiteOpenHelper( context, DATABASE_NAME, null, DATABASE_VERSION ) {

    // Code interpréter du projet Pendu du cours Application Native 2
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Stationnement.db"

        private const val TABLE_STATIONNEMENT = "Stationnement"

        private const val COLONNE_ID = "id"
        private const val COLONNE_NUMÉRO_MUNICIPAL = "numéro_municipal"
        private const val COLONNE_RUE = "rue"
        private const val COLONNE_CODE_POSTAL = "code_postal"
        private const val COLONNE_LONGITUDE = "longitude"
        private const val COLONNE_LATITUDE = "latitude"
        private const val COLONNE_PANNEAU = "panneau"
        private const val COLONNE_HEURE_DÉBUT = "heure_début"
        private const val COLONNE_HEURE_PRÉVU = "heure_prévu"

    }

    override fun onCreate( db: SQLiteDatabase? ) {
        val CREATE_TABLE_STATIONNEMENT = "CREATE TABLE $TABLE_STATIONNEMENT( $COLONNE_ID INTEGER PRIMARY KEY," +
                "$COLONNE_NUMÉRO_MUNICIPAL TEXT, $COLONNE_RUE TEXT, $COLONNE_CODE_POSTAL TEXT, $COLONNE_LONGITUDE DOUBLE," +
                " $COLONNE_LATITUDE DOUBLE, $COLONNE_PANNEAU TEXT, $COLONNE_HEURE_DÉBUT TEXT, $COLONNE_HEURE_PRÉVU TEXT )"
        db?.execSQL( CREATE_TABLE_STATIONNEMENT )
    }

    override fun onUpgrade( db: SQLiteDatabase?, oldVersion: Int, newVersion: Int ) {
        val DROP_TABLE_HISTORIQUE = "DROP TABLE IF EXISTS $TABLE_STATIONNEMENT"
        db?.execSQL( DROP_TABLE_HISTORIQUE )

        onCreate(db)
    }

    fun insérerStationnement( stationnemnt: Stationnement ){
        val db = writableDatabase
        val values = ContentValues().apply {
            put( COLONNE_NUMÉRO_MUNICIPAL, stationnemnt.adresse.code_postal )
            put( COLONNE_RUE, stationnemnt.adresse.rue )
            put( COLONNE_CODE_POSTAL, stationnemnt.adresse.code_postal )
        }
        db.insert( TABLE_STATIONNEMENT, null, values )
        db.close()
    }

    fun obtenirTousStationnementBD(): List<Stationnement>{
        val stationnementListe = mutableListOf<Stationnement>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_STATIONNEMENT"
        val cursor = db.rawQuery( query, null )
        while ( cursor.moveToNext() ){
            val id = cursor.getInt( cursor.getColumnIndexOrThrow( COLONNE_ID ) )
            val numéro_municipal = cursor.getString( cursor.getColumnIndexOrThrow( COLONNE_NUMÉRO_MUNICIPAL ) )
            val rue = cursor.getString( cursor.getColumnIndexOrThrow( COLONNE_RUE ) )
            val code_postal = cursor.getString( cursor.getColumnIndexOrThrow( COLONNE_CODE_POSTAL ) )
            val longitude = cursor.getDouble( cursor.getColumnIndexOrThrow( COLONNE_LONGITUDE ) )
            val latitude = cursor.getDouble( cursor.getColumnIndexOrThrow( COLONNE_LATITUDE ) )
            val panneau = cursor.getString( cursor.getColumnIndexOrThrow( COLONNE_PANNEAU ) )
            val heure_début = cursor.getString( cursor.getColumnIndexOrThrow( COLONNE_HEURE_DÉBUT ) )
            val heure_prévu = cursor.getString( cursor.getColumnIndexOrThrow( COLONNE_HEURE_PRÉVU ) )

            val adresse = Adresse( numéro_municipal, rue, code_postal )
            val coordonnée = Coordonnée( longitude, latitude )
            val stationnement = Stationnement( id, adresse, coordonnée, panneau, heure_début, heure_prévu )
            stationnementListe.add( stationnement )
        }
        cursor.close()
        db.close()
        return stationnementListe
    }

    fun insérerStationnements(stationnements: List<Stationnement>) {
        val db = writableDatabase
        db.beginTransaction() // Start a transaction for batch insert
        try {
            for (stationnement in stationnements) {
                val values = ContentValues().apply {
                    put(COLONNE_NUMÉRO_MUNICIPAL, stationnement.adresse.code_postal)
                    put(COLONNE_RUE, stationnement.adresse.rue)
                    put(COLONNE_CODE_POSTAL, stationnement.adresse.code_postal)
                    // Add other columns as needed
                }
                db.insert(TABLE_STATIONNEMENT, null, values)
            }
            db.setTransactionSuccessful() // Mark the transaction as successful
        } finally {
            db.endTransaction() // End the transaction
            db.close() // Close the database
        }
    }

}