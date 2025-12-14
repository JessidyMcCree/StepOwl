package pt.iade.games.stepowl

import addItem
import android.Manifest
import android.content.Context
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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import getItem
import pt.iade.games.stepowl.Components.QuestItem
import pt.iade.games.stepowl.ui.theme.StepOwlTheme
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import pt.iade.games.stepowl.Data.InventoryPayload
import pt.iade.games.stepowl.Data.ItemPayload

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
                MainView(steps.value, this@MainActivity)
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

    fun sendInventoryToServer(playerId: String, items: List<ItemPayload>) {
        if (items.isEmpty()) return

        val payload = InventoryPayload(playerId, items)
        val json = Gson().toJson(payload)

        "https://stepowl.onrender.com/inventory/add"
            .httpPost()
            .body(json)
            .header("Content-Type" to "application/json")
            .response { _, _, result ->
                result.fold(
                    success = { data ->
                        Log.i("INVENTORY", "Server response: ${String(data)}")
                    },
                    failure = { error ->
                        Log.e("INVENTORY", "Error: ${error.message}")
                    }
                )
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(
    steps: Float,
    mainActivity: MainActivity
) {
    // Dialog e campo para Unity ID
    var showIdDialog by remember { mutableStateOf(false) }
    var unityIdInput by remember { mutableStateOf("") }

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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                title = {
                    Column(Modifier.fillMaxWidth()) {
                        Text("StepOwl")
                        Text("${steps.toInt()} steps")
                    }
                },
                actions = {
                    // Botão Sync Unity
                    Button(onClick = { showIdDialog = true }) {
                        Text("Sync Unity")
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

            // ==================== DIALOG UNITY ID ====================
            if (showIdDialog) {
                AlertDialog(
                    onDismissRequest = { showIdDialog = false },
                    title = { Text("Digite seu Unity ID") },
                    text = {
                        OutlinedTextField(
                            value = unityIdInput,
                            onValueChange = { unityIdInput = it },
                            label = { Text("Unity ID") }
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (unityIdInput.isNotBlank()) {
                                // Exemplo de envio (podes substituir pelos itens reais)
                                mainActivity.sendInventoryToServer(
                                    unityIdInput,
                                    listOf(
                                        ItemPayload(1,1),
                                        ItemPayload(2,1),
                                        ItemPayload(3,1)
                                    )
                                )
                                showIdDialog = false
                            }
                        }) {
                            Text("Enviar")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showIdDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun MainViewPreview() {
    StepOwlTheme {
        MainView(48f, MainActivity())
    }
}
