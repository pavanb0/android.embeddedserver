/*
 * Created by nphau on 11/19/22, 4:16 PM
 * Copyright (c) 2022 . All rights reserved.
 * Last modified 11/19/22, 3:58 PM
 */

package com.nphausg.app.embeddedserver.activities

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nphausg.app.embeddedserver.EmbeddedServer
import com.nphausg.app.embeddedserver.R
import com.nphausg.app.ui.ImsApp
import com.nphausg.app.ui.components.theme.ImsTheme
import com.nphausg.app.ui.components.ThemePreviews
import com.nphausg.app.ui.components.button.ImsButton
import com.nphausg.app.ui.components.button.ImsOutlinedButton
import com.nphausg.app.ui.components.icon.ImsIcons
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Keep the splash screen on-screen until the UI state is loaded. This condition is
        // evaluated each time the app needs to be redrawn so it should be fast to avoid blocking
        // the UI.
        splashScreen.setKeepOnScreenCondition {
            false
        }
        // Turn off the decor fitting system windows, which allows us to handle insets,
        // including IME animations, and go edge-to-edge
        // This also sets up the initial system bar style based on the platform theme
        // enableEdgeToEdge()
        setContent {
            CompositionLocalProvider() {
                ImsTheme {
                    ImsApp {
                        MainScreen()
                    }
                }
            }
        }
    }
}

@Composable
private fun Logo() {

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    Card(
        modifier = Modifier
            .size(96.dp)
            .offset {
                IntOffset(
                    offsetX.value.toInt(),
                    offsetY.value.toInt()
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragEnd = {
                        coroutineScope.launch {
                            offsetY.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = 1000,
                                    delayMillis = 0
                                )
                            )
                        }
                        coroutineScope.launch {
                            offsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = 1000,
                                    delayMillis = 0
                                )
                            )
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            offsetY.snapTo(offsetY.value + dragAmount.y)
                        }
                        coroutineScope.launch {
                            offsetX.snapTo(offsetX.value + dragAmount.x)
                        }
                    }
                )
            },
        shape = CircleShape
    ) {
        Image(
            painterResource(R.drawable.logo),
            contentDescription = "",
            contentScale = ContentScale.Inside
        )
    }
}

@Composable
private fun MainScreen() {

    var ticks by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            ticks++
        }
    }

    var hasStarted by remember { mutableStateOf(false) }

    val value by rememberInfiniteTransition().animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 20.dp,
            alignment = Alignment.CenterVertically
        )
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Logo()
        Spacer(modifier = Modifier.weight(0.1f))
        Column(
            verticalArrangement = Arrangement.spacedBy(
                space = 20.dp,
                alignment = Alignment.CenterVertically
            )
        ) {
            Row {
                Icon(imageVector = ImsIcons.PlayArrow, contentDescription = null)
                Text(
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    text = String.format("GET: %s", EmbeddedServer.host),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Row {
                Icon(imageVector = ImsIcons.PlayArrow, contentDescription = null)
                Text(
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    text = String.format("GET: %s/fruits", EmbeddedServer.host),
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            Row(modifier = Modifier) {
                Icon(imageVector = ImsIcons.PlayArrow, contentDescription = null)
                Text(
                    color = Color.Black,
                    textAlign = TextAlign.Start,
                    text = String.format("GET: %s/fruits/{id}", EmbeddedServer.host),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dp(36f))
        ) {
            ImsButton(
                enabled = !hasStarted,
                modifier = Modifier.weight(1f),
                onClick = {
                    hasStarted = true
                    EmbeddedServer.start()
                },
                text = { Text("Start") })
            Spacer(modifier = Modifier.weight(0.1f))
            ImsOutlinedButton(
                enabled = hasStarted,
                modifier = Modifier.weight(1f),
                onClick = {
                    hasStarted = false
                    EmbeddedServer.stop()
                },
                text = { Text("Stop") })
        }

        Column(modifier = Modifier.height(8.dp)) {
            if (hasStarted) {
                LinearProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
        Text(
            modifier = Modifier.graphicsLayer {
                if (hasStarted) {
                    scaleX = value
                    scaleY = value
                }
            },
            color = Color.Black,
            textAlign = TextAlign.Center,
            text = if (hasStarted) {
                "The server is running on: ${Build.DEVICE} (${ticks}s ....)"
            } else {
                "Please click 'Start' to start the embedded server"
            },
            style = MaterialTheme.typography.labelMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@ThemePreviews
@Composable
fun MainScreenPreview() {
    ImsTheme {
        ImsApp {
            MainScreen()
        }
    }
}