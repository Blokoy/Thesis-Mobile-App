package com.blokoy.brooch

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.Locale

// ---------- Shared bits ----------

@Composable
private fun BroochCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BroochColors.Card),
        border = BorderStroke(1.dp, BroochColors.CardBorder)
    ) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), content = content)
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = BroochColors.Ink,
    divider: Boolean = true
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 15.sp, color = BroochColors.Ink, modifier = Modifier.weight(1f))
        Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
    if (divider) HorizontalDivider(color = BroochColors.CardBorder)
}

@Composable
private fun LabeledSlider(
    label: String,
    valueText: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit
) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(label, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text(valueText, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = BroochColors.Green)
        }
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = range,
            colors = SliderDefaults.colors(
                thumbColor = BroochColors.Green,
                activeTrackColor = BroochColors.Green,
                inactiveTrackColor = BroochColors.Track
            )
        )
    }
}

// ---------- My Brooch ----------

@Composable
fun MyBroochScreen(
    state: BroochUiState,
    onVolume: (Float) -> Unit,
    onHaptic: (Float) -> Unit,
    onDistance: (Float) -> Unit,
    onPair: () -> Unit,
    onSos: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "My Brooch",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                val pillColor = if (state.connected) BroochColors.Green else BroochColors.Muted
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, pillColor)
                ) {
                    Text(
                        (if (state.connected) "● Connected" else "● Disconnected"),
                        color = pillColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            BroochCard {
                InfoRow("Chest pin", "${state.chestPinBattery}%")
                InfoRow("Waist unit", "${state.waistUnitBattery}%")
                InfoRow(
                    "Earphones",
                    if (state.earphonesPaired) "Paired" else "Not paired",
                    BroochColors.Green,
                    divider = false
                )
            }

            BroochCard {
                Text(
                    "Calibration",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
                LabeledSlider(
                    "Audio cue volume", "${(state.audioVolume * 100).toInt()}%",
                    state.audioVolume, 0f..1f, onVolume
                )
                LabeledSlider(
                    "Haptic strength", "${(state.hapticStrength * 100).toInt()}%",
                    state.hapticStrength, 0f..1f, onHaptic
                )
                LabeledSlider(
                    "Warning distance",
                    String.format(Locale.US, "%.1f m", state.warningDistanceM),
                    state.warningDistanceM, 0.5f..5f, onDistance
                )
            }

            OutlinedButton(
                onClick = onPair,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, BroochColors.Green)
            ) {
                Text(
                    "Pair or re-pair device",
                    color = BroochColors.Green,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        if (state.sosSent) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = BroochColors.RedSoft,
                border = BorderStroke(1.dp, BroochColors.RedBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Caregiver alerted",
                    color = BroochColors.Red,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 18.dp)
                )
            }
            Text(
                "Confirmation after holding SOS for 3 seconds.",
                fontSize = 12.sp,
                color = BroochColors.Muted,
                modifier = Modifier.padding(top = 6.dp)
            )
        } else {
            Text(
                "Hold to notify your caregiver.",
                fontSize = 13.sp,
                color = BroochColors.Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            SosHoldButton(onSos)
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun SosHoldButton(onTriggered: () -> Unit) {
    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    val currentOnTriggered = rememberUpdatedState(onTriggered)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(BroochColors.Red)
            .drawBehind {
                // darker fill grows left-to-right while holding
                drawRect(
                    Color.Black.copy(alpha = 0.28f),
                    size = Size(size.width * progress.value, size.height)
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(onPress = {
                    val job = scope.launch {
                        progress.animateTo(1f, tween(3000, easing = LinearEasing))
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        currentOnTriggered.value()
                        progress.snapTo(0f)
                    }
                    tryAwaitRelease()
                    if (job.isActive) {
                        job.cancel()
                        scope.launch { progress.snapTo(0f) }
                    }
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Text("SOS: hold 3 seconds", color = Color.White, fontWeight = FontWeight.Bold)
    }
}

// ---------- Caregiver ----------

@Composable
fun CaregiverScreen(state: BroochUiState, onConfirm: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Column {
            Text("Caregiver", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("Monitoring: ${state.caregiverName}", color = BroochColors.Muted, fontSize = 15.sp)
        }

        state.activeAlertTime?.let { time ->
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = BroochColors.RedSoft,
                border = BorderStroke(1.dp, BroochColors.RedBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "SOS alert, $time",
                        color = BroochColors.Red,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BroochColors.Red)
                    ) {
                        Text("Confirm", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 6.dp))
                    }
                }
            }
        }

        BroochCard {
            Text(
                "Device status",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            InfoRow("Pin battery", "${state.chestPinBattery}%")
            InfoRow(
                "Phone",
                if (state.phoneOnline) "Online" else "Offline",
                if (state.phoneOnline) BroochColors.Green else BroochColors.Red,
                divider = false
            )
        }

        BroochCard {
            Text(
                "Alert history",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            state.history.forEachIndexed { i, entry ->
                InfoRow(
                    entry.date,
                    entry.label,
                    if (entry.isSos) BroochColors.Red else BroochColors.Ink,
                    divider = i != state.history.lastIndex
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
