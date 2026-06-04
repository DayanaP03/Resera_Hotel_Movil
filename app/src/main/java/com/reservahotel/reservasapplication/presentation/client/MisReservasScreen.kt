package com.reservahotel.reservasapplication.presentation.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.reservahotel.reservasapplication.domain.model.Reserva
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    viewModel: ReservaViewModel,
    onBack: () -> Unit
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
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
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.reservas) { reserva ->
                    ReservaClienteItem(reserva = reserva)
                }
            }
        }
    }
}

@Composable
fun ReservaClienteItem(reserva: Reserva) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        shape = Shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Reserva #${reserva.id}", fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Habitación: ${reserva.habitacion}", color = TextSecondary)
            Text("Fecha: ${reserva.fecha_entrada} a ${reserva.fecha_salida}", color = TextSecondary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Estado: ${reserva.estado}",
                color = when(reserva.estado) {
                    "Confirmada" -> Success
                    "Pendiente" -> Warning
                    else -> TextSecondary
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}
