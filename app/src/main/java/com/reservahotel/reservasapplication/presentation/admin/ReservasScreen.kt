package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.reservahotel.reservasapplication.domain.model.Reserva
import com.reservahotel.reservasapplication.presentation.client.ReservaViewModel
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(
    viewModel: ReservaViewModel,
    onBack: () -> Unit
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión Admin - Reservas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = com.reservahotel.reservasapplication.theme.Surface,
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
                    ReservaAdminItem(
                        reserva = reserva,
                        onDelete = { viewModel.deleteReserva(reserva.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ReservaAdminItem(
    reserva: Reserva,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = com.reservahotel.reservasapplication.theme.Surface),
        border = BorderStroke(1.dp, Border),
        shape = Shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Reserva #${reserva.id}", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Habitación: ${reserva.habitacion}", color = TextSecondary)
                Text("Cliente ID: ${reserva.cliente}", color = TextSecondary)
                Text("Desde: ${reserva.fecha_entrada} Hasta: ${reserva.fecha_salida}", color = TextSecondary)
                Text("Estado: ${reserva.estado}", color = Accent, fontWeight = FontWeight.Medium)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = com.reservahotel.reservasapplication.theme.Error)
            }
        }
    }
}
