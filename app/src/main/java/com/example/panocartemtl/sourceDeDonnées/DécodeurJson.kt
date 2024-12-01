package com.example.panocartemtl.sourceDeDonnées

import com.example.panocartemtl.entitées.Adresse
import com.example.panocartemtl.entitées.Coordonnée
import com.example.panocartemtl.entitées.Stationnement
import com.google.gson.stream.JsonReader
import com.google.gson.stream.MalformedJsonException
import java.io.EOFException
import java.io.StringReader

class DécodeurJson {

    companion object {

         /**
          * Crée un objet Donnée à partir de sa représentation JSON
          *
          * @params json Une chaîne de caractères en format JSON représentant une Donnée
          * @return La Donnée créée
          */

         fun décoderJsonVersStationnementsListe( json: String ): List<Stationnement> {
             val reader = JsonReader( StringReader( json ) )
             var stationnements = mutableListOf<Stationnement>()

             try{
                 reader.beginArray()
                 while ( reader.hasNext() ) {
                     val stationnement = décoderStationnementObjet( reader )
                     println("Stationnement ajouté: $stationnement")
                     stationnements.add(stationnement)
                 }

                 reader.endArray()
             }
             catch (exc: EOFException) {
                 throw SourceDeDonnéesException("Format JSON invalide")
             }
             catch (exc: MalformedJsonException) {
                 throw SourceDeDonnéesException("Format JSON invalide")
             }

             return stationnements
         }

         fun décoderJsonVersStationnement( json: String ): Stationnement {
             val reader = JsonReader( StringReader( json ) )
             var id: Int = 0
             var adresse = Adresse()
             var coordonnée = Coordonnée()
             var panneau: String = ""
             var heures_début: String = ""
             var heures_fin: String = ""

             try{
                 reader.beginObject()
                 while ( reader.hasNext() ) {
                     when ( reader.nextName() ) {
                         "id" -> {
                             id = reader.nextInt()
                         }

                         "adresse" -> {
                             adresse = décoderAdresseObjet( reader )
                         }

                         "coordonnee" -> {
                             coordonnée = décoderCoordonnéeObjet( reader )
                         }

                         "panneau" -> {
                             panneau = reader.nextString()
                         }

                         "heures_debut" -> {
                             heures_début = reader.nextString()
                         }

                         "heures_fin" -> {
                             heures_fin = reader.nextString()
                         }

                         else -> {
                             reader.skipValue()
                         }
                     }
                 }
                 reader.endObject()
             }
             catch ( exc: EOFException ) {
                 throw SourceDeDonnéesException( "Format JSON invalide")
             }
             catch ( exc: MalformedJsonException ) {
                 throw SourceDeDonnéesException("Format JSON invalide" )
             }

             return Stationnement( id, adresse, coordonnée, panneau, heures_début, heures_fin )
         }

        fun décoderStationnementObjet( reader: JsonReader ): Stationnement {
            var id: Int = 0
            var adresse = Adresse()
            var coordonnée = Coordonnée()
            var panneau: String = ""
            var heures_début: String = ""
            var heures_fin: String = ""

            reader.beginObject()

            while ( reader.hasNext() ) {
                when ( reader.nextName() ) {
                    "id" -> {
                        id = reader.nextInt()
                    }

                    "adresse" -> {
                        adresse = décoderAdresseObjet( reader )
                    }

                    "coordonnee" -> {
                        coordonnée = décoderCoordonnéeObjet( reader )
                    }

                    "panneau" -> {
                        panneau = reader.nextString()
                    }

                    "heures_debut" -> {
                        heures_début = reader.nextString()
                    }

                    "heures_fin" -> {
                        heures_fin = reader.nextString()
                    }

                    else -> {
                        reader.skipValue()
                    }
                }
            }
            reader.endObject()

            return Stationnement( id, adresse, coordonnée, panneau, heures_début, heures_fin )
        }

        private fun décoderAdresseObjet( reader: JsonReader ): Adresse {
            var numero_municipal: String = ""
            var rue: String = ""
            var code_postal: String = ""

            reader.beginObject()

            while ( reader.hasNext() ) {
                when( reader.nextName() ) {
                    "numero_municipal" -> {
                        numero_municipal = reader.nextString()
                    }

                    "rue" -> {
                        rue = reader.nextString()
                    }

                    "code_postal" -> {
                        code_postal = reader.nextString()
                    }

                    else -> {
                        reader.skipValue()
                    }
                }
            }
            reader.endObject()

            return Adresse( numero_municipal, rue, code_postal )
        }

        private fun décoderCoordonnéeObjet( reader: JsonReader ): Coordonnée {
            var coordonnée = Coordonnée()
            var longitude = 0.0
            var latitude = 0.0

            reader.beginObject()

            while ( reader.hasNext() ) {
                when( reader.nextName() ) {
                    "longitude" -> {
                        longitude = reader.nextDouble()
                    }

                    "latitude" -> {
                        latitude = reader.nextDouble()
                    }

                    else -> {
                        reader.skipValue()
                    }
                }
            }
            reader.endObject()

            return Coordonnée( longitude, latitude )
        }

        // Quand on va recevoir une liste uniques de numéros municipaux, de rues et de codes postals
        private fun décoderListe( json: String ) : List<String> {
            val reader = JsonReader( StringReader( json ) )
            val listeMotsUniques = mutableListOf<String>()

            try{
                reader.beginArray()
                while ( reader.hasNext() ) {
                    listeMotsUniques.add(reader.nextString())
                }
                reader.endArray()
            }

            catch (exc: EOFException) {
                throw SourceDeDonnéesException( "Format JSON invalide" )
            }
            catch (exc: MalformedJsonException) {
                throw SourceDeDonnéesException( "Format JSON invalide" )
            }

            return listeMotsUniques
        }
    }

}