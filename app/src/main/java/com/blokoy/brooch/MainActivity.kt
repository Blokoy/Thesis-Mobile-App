package com.blokoy.brooch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BroochTheme {
                val vm: BroochViewModel = viewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                // Dev convenience: switch between the user view and caregiver view.
                // In production, pick the screen based on the signed-in role instead.
                var tab by rememberSaveable { mutableIntStateOf(0) }

                Scaffold(
                    containerColor = BroochColors.Background,
                    bottomBar = {
                        NavigationBar(containerColor = BroochColors.Card) {
                            listOf("My Brooch", "Caregiver").forEachIndexed { i, title ->
                                NavigationBarItem(
                                    selected = tab == i,
                                    onClick = { tab = i },
                                    icon = {},
                                    label = { Text(title) },
                                    alwaysShowLabel = true,
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedTextColor = BroochColors.Green,
                                        indicatorColor = BroochColors.RedSoft.copy(alpha = 0f)
                                    )
                                )
                            }
                        }
                    }
                ) { inner ->
                    val m = Modifier.padding(inner)
                    androidx.compose.foundation.layout.Box(m) {
                        if (tab == 0) {
                            MyBroochScreen(
                                state = state,
                                onVolume = vm::setAudioVolume,
                                onHaptic = vm::setHapticStrength,
                                onDistance = vm::setWarningDistance,
                                onPair = vm::pairDevice,
                                onSos = vm::triggerSos
                            )
                        } else {
                            CaregiverScreen(state, onConfirm = vm::confirmAlert)
                        }
                    }
                }
            }
        }
    }
}
