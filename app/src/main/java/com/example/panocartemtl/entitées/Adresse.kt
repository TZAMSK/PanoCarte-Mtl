package com.example.panocartemtl.entitées

class Adresse(
    var numero_municipal: String = "",
    var rue: String = "",
    var code_postal: String = "",
){
    // Source: exemple code chargeur
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Adresse) return false

        return this.numero_municipal ==  other.numero_municipal &&
                this.rue == other.rue &&
                this.code_postal == other.code_postal
    }
}