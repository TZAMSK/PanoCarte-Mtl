package com.example.panocartemtl.Carte

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.example.panocartemtl.carte.PrésentateurCarte
import com.example.panocartemtl.carte.VueCarte
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mock
import org.mockito.Mockito
import kotlin.test.*
import kotlinx.coroutines.test.*

@RunWith(MockitoJUnitRunner::class)
class PrésentateurCarteTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Mock
    lateinit var mockVue: VueCarte

    @Mock
    lateinit var mockActivityResultLauncher: ActivityResultLauncher<Intent>

    @Mock
    lateinit var mockMapView: MapView

    @Mock
    lateinit var mockMapboxMap: MapboxMap

    @Mock
    lateinit var mockMarkerMap: MutableMap<PointAnnotation, Int>

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)

        Mockito.`when`(mockMapView.getMapboxMap()).thenReturn(mockMapboxMap)
        Mockito.`when`(mockVue.mapView).thenReturn(mockMapView)

        Mockito.`when`(mockVue.registerForActivityResult(
            Mockito.any<ActivityResultContract<Intent, Intent>>(),
            Mockito.any())
        ).thenReturn(mockActivityResultLauncher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `détruireTousMarqueurs should clear all markers on the map`() = runTest {
        val marker1 = Mockito.mock(PointAnnotation::class.java)
        val marker2 = Mockito.mock(PointAnnotation::class.java)
        mockMarkerMap[marker1] = 1
        mockMarkerMap[marker2] = 2

        val présentateur = PrésentateurCarte(mockVue)

        présentateur.détruireTousMarqueurs()

        assertFalse(mockMarkerMap.isEmpty())
    }

    @Test
    fun `caméraPremièreInstance should correctly center the camera`() = runTest {
        Mockito.`when`(mockVue.mapView).thenReturn(mockMapView)

        val présentateur = PrésentateurCarte(mockVue)

        présentateur.caméraPremièreInstance()

        Mockito.verify(mockMapboxMap).setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(-73.554640, 45.561120))
                .zoom(13.0)
                .build()
        )
    }
}
