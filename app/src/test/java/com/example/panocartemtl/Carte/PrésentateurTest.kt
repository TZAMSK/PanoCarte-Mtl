package com.example.panocartemtl.Carte

import com.mapbox.geojson.Point
import com.example.panocartemtl.Modèle.Modèle
import com.example.panocartemtl.carte.PrésentateurCarte
import com.example.panocartemtl.carte.VueCarte
import com.example.panocartemtl.entitées.Coordonnée
import com.example.panocartemtl.entitées.Stationnement
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.*
import org.mockito.Mockito
import kotlin.test.assertTrue

@RunWith(MockitoJUnitRunner::class)
class PrésentateurCarteTest {

    @Test
    fun `test recupérerTousStationnements`() = runTest {
        val vueMock = mock(VueCarte::class.java)
        val modèleMock = mock(Modèle::class.java)

        val pointAnnotationManagerMock = mock(PointAnnotationManager::class.java)
        val pointAnnotationMock = mock(PointAnnotation::class.java)

        val stationnementMock = mock(Stationnement::class.java)

        val coordonnéeMock = mock(Point::class.java)
        Mockito.`when`(coordonnéeMock.longitude()).thenReturn(45.0)
        Mockito.`when`(coordonnéeMock.latitude()).thenReturn(-73.0)

        Mockito.`when`(stationnementMock.coordonnée).thenReturn(Coordonnée(coordonnéeMock.longitude(), coordonnéeMock.latitude()))

        Mockito.`when`(vueMock.pointAnnotationManager).thenReturn(pointAnnotationManagerMock)
        Mockito.`when`(modèleMock.obtenirTousStationnements()).thenReturn(listOf(stationnementMock))
        Mockito.`when`(vueMock.pointAnnotationManager.create(Mockito.any(PointAnnotationOptions::class.java)))
            .thenReturn(pointAnnotationMock)

        val présentateur = PrésentateurCarte(vueMock)
        présentateur.modèle = modèleMock

        présentateur.recupérerTousStationnements()

        Mockito.verify(pointAnnotationManagerMock).create(Mockito.any(PointAnnotationOptions::class.java))

        assertTrue(présentateur.markerMap.isNotEmpty())
    }
}
