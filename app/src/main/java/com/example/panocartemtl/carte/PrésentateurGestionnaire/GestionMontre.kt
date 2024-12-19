package com.example.panocartemtl.carte.PrésentateurGestionnaire

import android.app.TimePickerDialog
import com.example.panocartemtl.carte.InterfaceCarte.MontreInterface
import com.example.panocartemtl.carte.VueCarte
import java.text.SimpleDateFormat
import java.util.Calendar

class GestionMontre( var vue: VueCarte ): MontreInterface {

    // Pour la montre, code copier coller de ce tutoriel
    // Source: https://www.youtube.com/watch?v=BLmFrR13-bs
    override fun montrerMontreDébut() {
        val calendrier = Calendar.getInstance()
        val heureListener = TimePickerDialog.OnTimeSetListener{ heureChoix, heure, minute ->
            calendrier.set( Calendar.HOUR_OF_DAY, heure )
            calendrier.set( Calendar.MINUTE, minute )
            vue.btnChoisirHeureDébut.text = SimpleDateFormat( "HH:mm" ).format( calendrier.time )
        }

        TimePickerDialog( vue.requireContext(), heureListener, calendrier.get( Calendar.HOUR_OF_DAY ), calendrier.get( Calendar.MINUTE ), true ).show()
    }

    override fun montrerMontrePrévu() {
        val calendrier = Calendar.getInstance()
        val heureListener = TimePickerDialog.OnTimeSetListener{ heureChoix, heure, minute ->
            calendrier.set( Calendar.HOUR_OF_DAY, heure )
            calendrier.set( Calendar.MINUTE, minute )
            vue.btnChoisirHeurePrévu.text = SimpleDateFormat( "HH:mm" ).format( calendrier.time )
        }

        TimePickerDialog( vue.requireContext(), heureListener, calendrier.get( Calendar.HOUR_OF_DAY ), calendrier.get( Calendar.MINUTE ), true ).show()
    }
}