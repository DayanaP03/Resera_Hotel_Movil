package com.reservahotel.reservasapplication.presentation.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reservahotel.reservasapplication.domain.model.Habitacion
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitacionesScreen(
    onBack: () -> Unit,
    onNavigateToMisReservas: () -> Unit,
    viewModel: HabitacionViewModel
) {
    val state = viewModel.state
    var mostrarReserva by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Habitaciones Disponibles", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cerrar Sesión")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToMisReservas) {
                        Icon(Icons.Default.List, contentDescription = "Mis Reservas")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = Background
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.habitaciones) { habitacion ->
                    HabitacionClienteItem(
                        habitacion = habitacion,
                        onReservar = { mostrarReserva = true }
                    )
                }
            }

            if (mostrarReserva) {
                ReservaBottomSheet(
                    onDismiss = { mostrarReserva = false },
                    onConfirmar = { mostrarReserva = false }
                )
            }
        }
    }
}

@Composable
fun HabitacionClienteItem(
    habitacion: Habitacion,
    onReservar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        shape = Shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Habitación ${habitacion.numero}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(habitacion.tipo, fontSize = 14.sp, color = TextSecondary)
                Text("L. ${habitacion.precio_noche} / noche", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Accent)
            }

            Button(
                onClick = onReservar,
                colors = ButtonDefaults.buttonColors(containerColor = Accent)
            ) {
                Text("Reservar")
            }
        }
    }
}
