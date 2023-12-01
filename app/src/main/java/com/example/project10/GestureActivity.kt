package com.example.project10

import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import com.example.project10.ui.theme.Project10Theme
import java.lang.Math.abs

class GestureActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // setting the themes
            Project10Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting2()
                }
                GestureCanvas()
            }
        }
    }

}



enum class GestureType
{
    SWIPE_UP,
    SWIPE_DOWN,
    SWIPE_LEFT,
    SWIPE_RIGHT,
    DOUBLE_TAP,
}

fun GestureTypeToString(gestureType: GestureType) : String
{
    /**
     * Depending on the type of swipe indicated by the user we will display the following recorded
     * gestures which utilizes a switch case format
     * @params gestureType: type of gesture
     * @returns a string
     */
    return when (gestureType)
    {
        GestureType.SWIPE_UP -> {
            "Swipe Up Recorded"
        }
        GestureType.SWIPE_DOWN -> {
            "Swipe Down Recorded"
        }
        GestureType.SWIPE_LEFT -> {
            "Swipe Left Recorded"
        }
        GestureType.SWIPE_RIGHT -> {
            "Swipe Right Recorded"
        }
        GestureType.DOUBLE_TAP -> {
            "Double Tap Recorded"
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GestureCanvas(modifier: Modifier = Modifier.fillMaxSize()) {
    var ballLocation = remember { mutableStateOf(Offset(150F, 150F))}
    var direction by remember { mutableStateOf(-1)}
    var gesturesHistory: List<GestureType> by remember { mutableStateOf(listOf()) }
    val ballSize = 20F
    var mod = Modifier
        .fillMaxWidth()
        .height(Dp(300F))
        .clipToBounds()
        .background(Color.White)
        .border(color = Color.Black, width = Dp(2F))
        .pointerInput(Unit) {
            detectTapGestures(
                onDoubleTap = {
                    gesturesHistory = listOf(GestureType.DOUBLE_TAP) + gesturesHistory
                    //Log.i("gestures", "double tapped")
                }
            )
        }
        .pointerInput(Unit) {
            detectDragGestures(
                onDrag = { change, dragAmount ->
                    change.consume()
                    val (x, y) = dragAmount
                    if (kotlin.math.abs(x) > kotlin.math.abs(y)) {
                        when {
                            x > 0 -> {
                                //right
                                direction = 0
                            }

                            x < 0 -> {
                                // left
                                direction = 1
                            }
                        }
                    } else {
                        when {
                            y > 0 -> {
                                // down
                                direction = 2
                            }

                            y < 0 -> {
                                // up
                                direction = 3
                            }
                        }
                    }

                },
                onDragEnd = {
                    val (width, height) = size
                    when (direction) {
                        0 -> {
                            //right
                            ballLocation.value =
                                Offset(width.toFloat() - ballSize, ballLocation.value.y)
                            gesturesHistory = listOf(GestureType.SWIPE_RIGHT) + gesturesHistory
                            //Log.i("gestures", "right")
                        }

                        1 -> {
                            // left
                            ballLocation.value = Offset(ballSize, ballLocation.value.y)
                            gesturesHistory = listOf(GestureType.SWIPE_LEFT) + gesturesHistory
                            //Log.i("gestures", "left")
                        }

                        2 -> {
                            // down
                            ballLocation.value = Offset(ballLocation.value.x, height - ballSize)
                            gesturesHistory = listOf(GestureType.SWIPE_DOWN) + gesturesHistory
                            //Log.i("gestures", "down")
                        }

                        3 -> {
                            // up
                            ballLocation.value = Offset(ballLocation.value.x, ballSize)
                            gesturesHistory = listOf(GestureType.SWIPE_UP) + gesturesHistory
                            //Log.i("gestures", "up")
                        }
                    }
                }

            )
        }

    Column {
        Canvas(modifier = mod, onDraw = {
            drawCircle(
                color = Color.Companion.Red,
                ballSize,
                ballLocation.value
            )
        })
        for (i in gesturesHistory.indices) {
            MessageRow(gesturesHistory[i], i)
        }
    }
}



@Composable
fun MessageRow(gestureType: GestureType, index: Int)
{
    val rowHeight = 50F
    var mod = Modifier
        .fillMaxWidth()
        .height(Dp(rowHeight))
        .clipToBounds()
        .background(Color.White)
        .border(color = Color.Black, width = Dp(2F))
    Text(
        text = GestureTypeToString(gestureType),
        modifier = mod
    )
}

@Composable
fun Greeting2(modifier: Modifier = Modifier) {
    Text(
        text = "This is Greeting 2",
        modifier = modifier
    )
}

@Composable
fun GreetingPreview2() {
    Project10Theme {
        Greeting2()
    }
}