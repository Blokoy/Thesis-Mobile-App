package com.blokoy.brooch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AlertEntry(val date: String, val label: String, val isSos: Boolean)

data class BroochUiState(
    val connected: Boolean = true,
    val chestPinBattery: Int = 87,
    val waistUnitBattery: Int = 64,
    val earphonesPaired: Boolean = true,
    val audioVolume: Float = 0.7f,
    val hapticStrength: Float = 0.6f,
    val warningDistanceM: Float = 2.0f,
    val sosSent: Boolean = false,
    val activeAlertTime: String? = null,
    val caregiverName: String = "Maria",
    val phoneOnline: Boolean = true,
    val history: List<AlertEntry> = listOf(
        AlertEntry("Oct 24", "SOS", true),
        AlertEntry("Oct 19", "Low battery", false)
    )
)

/**
 * Shared state for both screens so the SOS demo works end to end.
 * Replace the TODOs with real BLE / backend calls later.
 */
class BroochViewModel : ViewModel() {
    private val _state = MutableStateFlow(BroochUiState())
    val state: StateFlow<BroochUiState> = _state.asStateFlow()

    fun setAudioVolume(v: Float) = _state.update { it.copy(audioVolume = v) }
    fun setHapticStrength(v: Float) = _state.update { it.copy(hapticStrength = v) }
    fun setWarningDistance(v: Float) = _state.update { it.copy(warningDistanceM = v) }

    fun pairDevice() {
        // TODO: start BLE scan / pairing flow
        _state.update { it.copy(connected = true) }
    }

    fun triggerSos() {
        // TODO: send SOS to backend / push notification to caregiver
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        _state.update { it.copy(sosSent = true, activeAlertTime = time) }
    }

    fun confirmAlert() {
        val day = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date())
        _state.update {
            it.copy(
                sosSent = false,
                activeAlertTime = null,
                history = listOf(AlertEntry(day, "SOS", true)) + it.history
            )
        }
    }
}
