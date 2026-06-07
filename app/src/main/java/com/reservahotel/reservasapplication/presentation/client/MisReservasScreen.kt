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
import androidx.compose.ui.unit.sp
import com.reservahotel.reservasapplication.domain.model.Reserva
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    viewModel: ReservaViewModel,
    onBack: () -> Unit,
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
                    titleContentColor = TextPrimary,
                ),
            )
        },
        containerColor = Background,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Accent)
                state.reservas.isEmpty() -> {
                    Text(
                        "No tenés reservas aún",
                        modifier = Modifier.align(Alignment.Center),
                        color = TextFaint,
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.reservas) { reserva ->
                            ReservaClienteItem(reserva = reserva)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaClienteItem(reserva: Reserva) {
    val estadoColor = when (reserva.estado.lowercase()) {
        "activa"     -> Success
        "cancelada"  -> Error
        "completada" -> Info
        else         -> TextSecondary
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Border),
        shape = Shapes.medium,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Reserva #${reserva.id}", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    text = reserva.estadoDisplay,
                    color = estadoColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Habitación ${reserva.habitacionNumero}", color = TextSecondary, fontSize = 14.sp)
            Text("Entrada: ${reserva.fechaEntrada}  —  Salida: ${reserva.fechaSalida}", color = TextSecondary, fontSize = 13.sp)
            Text("${reserva.noches} noches", color = TextFaint, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Total: L. ${reserva.total}", color = Accent, fontWeight = FontWeight.Bold)
        }
    }
}
