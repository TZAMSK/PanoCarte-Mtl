import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.example.panocartemtl.carte.PrésentateurCarte
import com.example.panocartemtl.carte.VueCarte
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.junit.runner.RunWith


@RunWith(MockitoJUnitRunner::class)
class PrésentateurMontreTest {

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Mock
    private lateinit var mockVue: VueCarte

    @Mock
    lateinit var mockActivityResultLauncher: ActivityResultLauncher<Intent>

    @Mock
    lateinit var mockMapView: MapView

    @Mock
    lateinit var mockMapboxMap: MapboxMap

    @Mock
    lateinit var mockContext: Context

    @Mock
    lateinit var mockTimePickerDialog: TimePickerDialog

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)

        Mockito.`when`(mockMapView.getMapboxMap()).thenReturn(mockMapboxMap)
        Mockito.`when`(mockVue.mapView).thenReturn(mockMapView)

        Mockito.`when`(mockVue.registerForActivityResult(
            Mockito.any<ActivityResultContract<Intent, Intent>>(),
            Mockito.any())
        ).thenReturn(mockActivityResultLauncher)

        Mockito.`when`(mockContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).thenReturn(null)
    }

    @Test
    fun testMontrerMontreDébut() {
        val présentateur = PrésentateurCarte(mockVue)

        Mockito.`when`(mockTimePickerDialog)

        présentateur.montrerMontreDébut()

        Mockito.verify(mockVue).montrerPopup("Sélectionnez l'heure de début")
    }

    @Test
    fun testMontrerMontrePrévu() {
        val présentateur = PrésentateurCarte(mockVue)

        Mockito.`when`(mockTimePickerDialog)

        présentateur.montrerMontrePrévu()

        Mockito.verify(mockVue).montrerPopup("Sélectionnez l'heure prévue")
    }

    @After
    fun tearDown() {
        // Clean up any resources or reset configurations
    }
}
