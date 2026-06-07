package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reservahotel.reservasapplication.domain.model.Reserva
import com.reservahotel.reservasapplication.presentation.client.ReservaViewModel
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(
    viewModel: ReservaViewModel,
    onBack: () -> Unit,
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservas", fontWeight = FontWeight.Bold) },
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
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Accent,
                    )
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Default.WifiOff, null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No se pudieron cargar las reservas", color = TextSecondary)
                        Text(state.error, color = TextFaint, fontSize = 12.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.getReservas() },
                            colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        ) { Text("Reintentar") }
                    }
                }
                state.reservas.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Default.DateRange, null, tint = TextFaint, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No hay reservas registradas", color = TextFaint)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.reservas) { reserva ->
                            ReservaAdminCard(
                                reserva  = reserva,
                                onDelete = { viewModel.deleteReserva(reserva.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaAdminCard(reserva: Reserva, onDelete: () -> Unit) {
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
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Encabezado con ID y estado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Reserva #${reserva.id}",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 15.sp,
                    )
                    Surface(
                        color = estadoColor.copy(alpha = 0.15f),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(6.dp),
                    ) {
                        Text(
                            text = reserva.estadoDisplay,
                            color = estadoColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Cliente y habitación
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = Accent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(reserva.clienteNombre, color = TextSecondary, fontSize = 13.sp)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Hotel, null, tint = Accent, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Habitación ${reserva.habitacionNumero}", color = TextSecondary, fontSize = 13.sp)
                }

                Spacer(Modifier.height(6.dp))

                // Fechas
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, null, tint = TextFaint, modifier = Modifier.size(13.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${reserva.fechaEntrada}  →  ${reserva.fechaSalida}  (${reserva.noches} noches)",
                        color = TextSecondary,
                        fontSize = 12.sp,
                    )
                }

                Spacer(Modifier.height(6.dp))

                // Total
                Text(
                    text = "Total: L. ${reserva.total}",
                    color = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                )
            }

            // Botón eliminar
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Error)
            }
        }
    }
}
