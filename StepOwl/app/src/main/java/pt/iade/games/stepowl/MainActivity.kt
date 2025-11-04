package pt.iade.games.stepowl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import pt.iade.games.stepowl.ui.theme.StepOwlTheme


class MainActivity : ComponentActivity() , SensorEventListener {

    // variable gives the running status
    private var running = false
    private var initialSteps = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val steps = remember { mutableStateOf(0f) }

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

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StepOwlTheme {
        Greeting("Android")
    }
}
