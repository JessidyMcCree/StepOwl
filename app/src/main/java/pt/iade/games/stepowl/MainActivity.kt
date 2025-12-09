package pt.iade.games.stepowl

import addItem
import android.Manifest
import android.R.attr.onClick
import android.R.attr.padding
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Log.i
import android.view.Surface
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import getItem
import pt.iade.games.stepowl.Components.QuestItem
import pt.iade.games.stepowl.ui.theme.StepOwlTheme
// import to the server works
import com.github.kittinunf.fuel.httpGet
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject



class MainActivity : ComponentActivity(), SensorEventListener {

    private var initialSteps = -1
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null

    private val stepsState = mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        setContent {
            val steps = stepsState

            val sensorsPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        Log.i("PERM", "you have permission")
                        startStepCounting()
                    } else {
                        Log.e("PERM", "YOU DONT HAVE PERMISSION")
                    }
                }
            )
            "http://10.0.2.2:4000/hello".httpGet().response {
                    request, response, result ->
                // Get JSON string from server response.
                val jsonString = String(result.get())

                // Setup GSON and parse JSON.
                // val gson = GsonBuilder().create()
                //   val json = gson.fromJson(jsonString, JsonObject().javaClass)



                Log.i("Hello", jsonString);
            }

            SideEffect {
                sensorsPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
            }

            StepOwlTheme {
                MainView(steps.value)
            }
        }
    }

    private fun startStepCounting() {
        if (stepSensor == null) {
            Log.e("SENSOR", "sensor has not found")
            return
        }
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null && event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val totalSteps = event.values[0]
            if (initialSteps < 0)
                initialSteps = totalSteps.toInt()

            val currentSteps = totalSteps.toInt() - initialSteps
            stepsState.value = currentSteps.toFloat()
            Log.i("STEPS", "Current steps: $currentSteps")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    steps: Float
) {




    // Quests
    var quest1Active by remember { mutableStateOf(false) }
    var quest2Active by remember { mutableStateOf(false) }
    var quest3Active by remember { mutableStateOf(false) }

    var quest1completed by remember { mutableStateOf(false) }
    var quest2completed by remember { mutableStateOf(false) }
    var quest3completed by remember { mutableStateOf(false) }


    LaunchedEffect(steps) {
        if (quest1Active && !quest1completed && steps >= 10) {
            quest1completed = true
           addItem("Gold Coin")
        }
        if (quest2Active && !quest2completed && steps >= 20) {
            quest2completed = true
            addItem("Magic Book")
        }
        if (quest3Active && !quest3completed && steps >= 30) {
            quest3completed = true
            addItem("Mermaid Scale")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Column(Modifier.fillMaxWidth()) {
                        Text("StepOwl")
                        Text("${steps.toInt()} steps")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
        ) {

            Column(modifier = Modifier.padding(15.dp)) {
                // Quests
                QuestItem(
                    title = "Argghhh where me gold?!",
                    description = "Take 10 steps!",
                    goal = 10,
                    currentValue = steps.toInt(),
                    questActive = quest1Active,
                    onGoClick = { quest1Active = true }
                )
                QuestItem(
                    title = "The Books fly!",
                    description = "Take 20 steps!",
                    goal = 20,
                    currentValue = steps.toInt(),
                    questActive = quest2Active,
                    onGoClick = { quest2Active = true }
                )
                QuestItem(
                    title = "Help the Mermaid!",
                    description = "Take 30 steps!",
                    goal = 30,
                    currentValue = steps.toInt(),
                    questActive = quest3Active,
                    onGoClick = { quest3Active = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // inventory
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(10.dp)
                ) {
                    // Slot 1
                    Surface(
                        color = Color.Red,
                        modifier = Modifier.width(100.dp).height(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(getItem(0) ?: "Empty")
                        }
                    }

                    // Slot 2
                    Surface(
                        color = Color.Blue,
                        modifier = Modifier.width(100.dp).height(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(getItem(1) ?: "Empty")
                        }
                    }

                    // Slot 3
                    Surface(
                        color = Color.Yellow,
                        modifier = Modifier.width(100.dp).height(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            for (i in 0..2)
                                Text(getItem(2) ?: "Empty")
                        }
                    }
                }
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun MainViewPreview() {
    StepOwlTheme {
        MainView(48f)
    }
}
