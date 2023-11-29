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
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.findNavController
import com.example.project10.ui.theme.Project10Theme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.Dp
import kotlinx.coroutines.coroutineScope


/**
 * Main Activity
 *
 * On app launch, it asks for all necessary permissions.
 * It checks for permissions before showing the location.
 */
class MainActivity : ComponentActivity() {
    private lateinit var sensorsViewModel: SensorsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorsViewModel = ViewModelProvider(this)[SensorsViewModel::class.java]
        sensorsViewModel.initializeSensors(TemperatureSensor(applicationContext),HumiditySensor(applicationContext))

        // asks for permissions
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    Log.d("Permissions","")
                    setContent {
                        Project10Theme {

                            // A surface container using the 'background' color from the theme
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                begin_app()
                            }
                        }
                    }
                } else -> {
                Log.d("No Permissions", "")
            }
            }
        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    // Function gets the location based on lat long
    private fun getLocation(context: Context, lat: Double, long: Double): String {
        val state: String?
        val city: String?
        val geoCoder = Geocoder(context, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat,long,1)

        state = address?.get(0)?.adminArea
        city = address?.get(0)?.locality
        return "City: $city\nState: $state"
    }


    @SuppressLint("CoroutineCreationDuringComposition")
    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )

    // gets lat long
    @Composable
    private fun getCoords(): String {
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
            // if there is a result, use fetchedLocation and assign locationInfo
            result?.let { fetchedLocation ->
                locationInfo =
                    getLocation(context,
                        fetchedLocation.latitude, fetchedLocation.longitude)
            }
        }

        return locationInfo
    }



    @OptIn(ExperimentalFoundationApi::class)
    @RequiresPermission(
        anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
    )

    // Sensors Screen
    @Composable
    fun SensorsView(location: String, navController: NavController) {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val locationClient = remember {
            LocationServices.getFusedLocationProviderClient(context)
        }
        var locationInfo by remember {
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

        // on fling
        var fling = Modifier
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
            }

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
                modifier = fling,
                onClick = { },
            ) {
                Text(text = "Gestures Playground")
            }
        }
    }


    // navigate to gestures
    fun navigateToGestures()
    {
        //navController.navigate("GesturesView")
        val nav = Intent(this@MainActivity, GestureActivity::class.java)
        startActivity(nav)
    }


    /**
     * Main composable, contains navgraph, and permission checking for getCoords()
     * Called in onCreate().
     */
    @Composable
    fun begin_app() {
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
                // Shows info with SensorsView, gets info with getCoords
                composable("SensorsView") { SensorsView(getCoords(),navController = navController) }
            }
        }
    }

    // For showing previews
    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Project10Theme {
        }
    }
}