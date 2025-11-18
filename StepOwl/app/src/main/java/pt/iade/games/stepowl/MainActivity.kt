package pt.iade.games.stepowl

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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import pt.iade.games.stepowl.Components.QuestItem
import pt.iade.games.stepowl.ui.theme.StepOwlTheme


class MainActivity : ComponentActivity() , SensorEventListener {

    // variable gives the running status
    private var running = false
    private var initialSteps = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val steps = remember { mutableFloatStateOf(0f) }

            val sensorsPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        // granted
                        Log.i("PERM", "OK")
                        var sensorManager: SensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
                        val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
                    } else {
                        Log.e("PERM", "YOU DONT HAVE PERMISSION!!!!")
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

    override fun onResume() {

        super.onResume()
        /*
        running = true

        // TYPE_STEP_COUNTER:  A constant describing a step counter sensor
        // Returns the number of steps taken by the user since the last reboot while activated
        // This sensor requires permission android.permission.ACTIVITY_RECOGNITION.
        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            // show toast message, if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // register listener with sensorManager
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
        */
    }

    override fun onPause() {
        super.onPause()
        /*
        running = false
        // unregister listener
        sensorManager?.unregisterListener(this)
         */
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //if (running) {
        if (true) {
            //get the number of steps taken by the user.
            val totalSteps = event!!.values[0]
            if (initialSteps < 0)
                initialSteps = totalSteps.toInt()
            val currentSteps = totalSteps.toInt() - initialSteps
            Log.i("STEPS", "Current steps: $currentSteps")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("onAccuracyChanged: Sensor: $sensor; accuracy: $accuracy")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    steps: Float
) {
    var stepsCounter by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Column(
                        Modifier
                            .fillMaxWidth()

                    ) {
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

            Column(
                modifier = Modifier
                    .padding(15.dp)

            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuestItem(
                        title = "A fun title",
                        description = "Get the gold!",
                        goal = 10,
                        currentValue = stepsCounter
                    )
                    QuestItem(
                        title = "A fun title",
                        description = "Get the Books!",
                        goal = 10,
                        currentValue = 8
                    )
                    QuestItem(
                        title = "A fun title",
                        description = "Get the pearls!",
                        goal = 10,
                        currentValue = 8
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth()
                        .background(Color(0xf6804f78))
                ){
                    Button(
                        onClick = {
                            stepsCounter++
                        }
                    ) {
                        Text("Crafting and Inventory Section")
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = Color.Red,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Inv 1",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }


                            Surface(
                                color = Color.Blue,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Inv 2",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }


                            Surface(
                                color = Color.Green,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Inv 3",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = Color.Red,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Inv 1",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }


                            Surface(
                                color = Color.Blue,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Inv 2",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }


                            Surface(
                                color = Color.Green,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                            ) {
                                Text(
                                    text = "Inv 3",
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
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
