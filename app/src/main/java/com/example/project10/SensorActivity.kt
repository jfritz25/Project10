package com.example.project10

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController

class SensorActivity : ComponentActivity() {
    // Initialize your sensors here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SensorScreen()
        }
    }
}

@Composable
fun SensorScreen() {
    val navController = rememberNavController()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {


        Button(
            onClick = { /* Do nothing on click */ },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    val velocityTracker = VelocityTracker()
                    detectDragGestures(
                        onDragStart = {
                            velocityTracker.resetTracking()
                        },
                        onDrag = { change, _ ->
                            if (change.positionChange() != Offset.Zero) change.consume()
                            velocityTracker.addPosition(change.uptimeMillis, change.position)
                        },
                        onDragEnd = {
                            val velocity = velocityTracker.calculateVelocity()
                            val velocityY = velocity.y
                            if (kotlin.math.abs(velocityY) > 1000f) {
                                navController.navigate("GestureActivity")
                            }
                        }
                    )
                }
        ) {
            Text(text = "Gesture Playground")
        }
    }
}





