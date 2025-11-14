package pt.iade.games.stepowl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.iade.games.stepowl.ui.theme.StepOwlTheme

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
fun MainView(steps: Float) {
    Scaffold(
        topBar = {
            TopAppBar(
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
                Box(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                        .background(Color(0xfff04f78))
                ) {
                    Column {
                        Button(onClick = {}) { Text("Quest 1") }
                        Button(onClick = {}) { Text("Quest 2") }
                        Button(onClick = {}) { Text("Quest 3") }
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                        .background(Color(0xf6804f78))
                ) {
                    Button(onClick = {}) { Text("Crafting and Inventory Section") }
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
