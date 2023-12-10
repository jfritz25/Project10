package com.example.project10

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.GradientDrawable
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project10.ui.theme.Project10Theme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale


/**
 * Main Activity
 *
 * On app launch, it asks for all necessary permissions.
 * It checks for permissions before showing the location.
 */
class MainActivity : ComponentActivity() {
    private lateinit var sensorsViewModel: SensorsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        /**
         * Initializes the main activity and sets up necessary components.
         */
        super.onCreate(savedInstanceState)
        sensorsViewModel = ViewModelProvider(this)[SensorsViewModel::class.java]
        sensorsViewModel.initializeSensors(TemperatureSensor(applicationContext),HumiditySensor(applicationContext))

        // asks for permissions
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                Log.d("Permissions","")
                setContent {
                    Project10Theme {

                        // A surface container using the 'background' color from the theme
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            start()
                        }
                    }
                }
            } else {
                Log.d("No Permissions", "")
            }

        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    // Function gets the location based on lat long
    private fun getLocation(context: Context, lat: Double, long: Double): String {
        /**
         * Retrieves the location based on latitude and longitude.
         *
         * @param context The context in which the location is retrieved.
         * @param lat The latitude coordinate.
         * @param long The longitude coordinate.
         * @return A string representing the location information.
         */
        val state: String?
        val city: String?
        val geoCoder = Geocoder(context, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat,long,1)

        state = address?.get(0)!!.adminArea
        city = address[0]!!.locality
        return "City: $city\nState: $state"
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )

    @Composable
    private fun latlong(): String {
        /**
         * Composable function that retrieves the latitude and longitude for the current position.
         * @return A string representing the location information.
         */
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val locationClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }
        var locationInfo by remember {
            mutableStateOf("")
        }
        scope.launch(Dispatchers.IO) {
            val priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
            val result = locationClient.getCurrentLocation(
                priority,
                CancellationTokenSource().token,
            ).await()
            // if there is a result, use found and assign locationInfo
            result?.let { found ->
                locationInfo =
                    getLocation(context,
                        found.latitude, found.longitude)
            }
        }

        return locationInfo
    }



    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )



    @Composable
    fun SensorActivityInit(location: String) {
        /**
         * Composable function representing the main UI of the sensors activity.
         *
         * @param location A string representing the location information.
         */        var locationInfo by remember {
            mutableStateOf("")
        }
        var temperature by remember {
            mutableStateOf("")
        }
        var humidity by remember {
            mutableStateOf("")
        }

        locationInfo = location
        temperature = sensorsViewModel.ambientTemperature.observeAsState("").value.toString()
        humidity = sensorsViewModel.humidity.observeAsState("").value.toString()


        var direction by remember { mutableStateOf(-1)}


        Log.d("Temperature Viewing", temperature)
        Log.d("Humid Viewing", humidity)

        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Sensors Playground\n",
                style = TextStyle(fontSize = 30.sp)
            )
            Text(
                text = "Jacob Fritz and Ashley Steitz",
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "Location:\n$locationInfo",
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "\nTemperature: $temperature",
                modifier = Modifier.align(Alignment.Start)
            )
            Text(
                text = "\nHumidity: $humidity",
                modifier = Modifier.align(Alignment.Start)
            )
            Button(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                change.consume()
                                direction = 1
                            },
                            onDragEnd = {
                                if (direction != -1) {
                                    direction = -1
                                    navigateToGestures()
                                }
                            }
                        )
                    },
                onClick = { },
            ) {
                Text(text = "Gestures Playground")
            }
        }

    }

    @Composable
    fun start() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "SensorsView") {
            // Checks for permissions on coarse location
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Current location","Permission Not Granted")
            } else {
                // Shows info with SensorsView, gets info with latlong
                composable("SensorsView") { SensorActivityInit(latlong()) }
            }
        }
    }

    // navigate to gestures
    fun navigateToGestures()
    {
        val nav = Intent(this@MainActivity, GestureActivity::class.java)
        startActivity(nav)
    }


    /**
     * Main composable, contains navgraph, and permission checking for latlong()
     * Called in onCreate().
     */


    // For showing previews
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Project10Theme {
        }
    }
}