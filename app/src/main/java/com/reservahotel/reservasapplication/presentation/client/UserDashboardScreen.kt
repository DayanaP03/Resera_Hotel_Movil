package com.reservahotel.reservasapplication.presentation.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reservahotel.reservasapplication.data.local.TokenDataStore
import com.reservahotel.reservasapplication.domain.model.Reserva
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDashboardScreen(
    tokenDataStore: TokenDataStore,
    onLogout: () -> Unit,
    habitacionViewModel: HabitacionViewModel = hiltViewModel(),
    reservaViewModel: ReservaViewModel = hiltViewModel(),
) {
    val userSession by tokenDataStore.userSnapshot.collectAsState(initial = null)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Explorar", "Mis Viajes", "Mi Perfil")

    var showEditProfile by remember { mutableStateOf(false) }
    var selectedReservaForFactura by remember { mutableStateOf<Reserva?>(null) }

    Scaffold(
        bottomBar = {
            if (userSession != null) {
                NavigationBar(
                    containerColor = Surface,
                    tonalElevation = 8.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = when (index) {
                                        0 -> Icons.Default.Search
                                        1 -> Icons.Default.ConfirmationNumber
                                        else -> Icons.Default.PersonOutline
                                    },
                                    contentDescription = title,
                                )
                            },
                            label = { Text(title, fontWeight = FontWeight.Medium) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Accent,
                                selectedTextColor = Accent,
                                indicatorColor = Accent.copy(alpha = 0.1f),
                            ),
                        )
                    }
                }
            }
        },
        containerColor = Background,
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            val session = userSession
            if (session == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Accent)
                }
            } else {
                when (selectedTab) {
                    0 -> HabitacionesScreen(
                        onBack = onLogout,
                        onNavigateToMisReservas = { selectedTab = 1 },
                        viewModel = habitacionViewModel,
                        reservaViewModel = reservaViewModel,
                        clienteId = session.clienteId
                    )
                    1 -> ReservasTab(
                        viewModel = reservaViewModel,
                        onViewFactura = { selectedReservaForFactura = it }
                    )
                    2 -> PerfilTab(
                        username = session.username,
                        email = session.email,
                        rol = session.rol,
                        onLogout = onLogout,
                        onEditProfile = { showEditProfile = true }
                    )
                }
            }

            if (selectedReservaForFactura != null) {
                FacturaDialog(
                    reserva = selectedReservaForFactura!!,
                    onDismiss = { selectedReservaForFactura = null }
                )
            }

            if (showEditProfile) {
                EditProfileDialog(
                    currentUsername = userSession?.username ?: "",
                    onDismiss = { showEditProfile = false },
                    onConfirm = { name, pass -> showEditProfile = false }
                )
            }
        }
    }
}

@Composable
private fun ReservasTab(
    viewModel: ReservaViewModel,
    onViewFactura: (Reserva) -> Unit
) {
    val state = viewModel.state
    
    // Agrupamos reservas: Activas vs El resto
    val proximas = state.reservas.filter { it.estado.lowercase() == "activa" }
    val pasadas = state.reservas.filter { it.estado.lowercase() != "activa" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Tus Reservas",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        if (state.reservas.isEmpty() && !state.isLoading) {
            item {
                Box(Modifier.fillParentMaxHeight(0.7f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.EventBusy, null, Modifier.size(80.dp), tint = TextFaint)
                        Text("Aún no tienes aventuras planeadas", color = TextSecondary, modifier = Modifier.padding(top = 16.dp))
                    }
                }
            }
        }

        if (proximas.isNotEmpty()) {
            item { SectionHeader("Próximas estancias") }
            items(proximas) { reserva ->
                ReservaClienteCard(
                    reserva = reserva,
                    onCancel = { viewModel.cancelarReserva(reserva.id) },
                    onViewFactura = { onViewFactura(reserva) }
                )
            }
        }

        if (pasadas.isNotEmpty()) {
            item { SectionHeader("Historial") }
            items(pasadas) { reserva ->
                ReservaClienteCard(
                    reserva = reserva,
                    onCancel = { },
                    onViewFactura = { onViewFactura(reserva) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = TextSecondary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun ReservaClienteCard(
    reserva: Reserva,
    onCancel: () -> Unit,
    onViewFactura: () -> Unit
) {
    val (color, icon) = when (reserva.estado.lowercase()) {
        "activa" -> Success to Icons.Default.CheckCircle
        "cancelada" -> Error to Icons.Default.Cancel
        else -> TextFaint to Icons.Default.History
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Habitación ${reserva.habitacionNumero}", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text("Reserva #${reserva.id}", fontSize = 12.sp, color = TextFaint)
                }
                StatusBadge(reserva.estadoDisplay, color)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, Modifier.size(16.dp), tint = Accent)
                Spacer(Modifier.width(8.dp))
                Text("${reserva.fechaEntrada} - ${reserva.fechaSalida}", fontSize = 14.sp, color = TextSecondary)
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Border.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Total pagado", fontSize = 11.sp, color = TextFaint)
                    Text("L. ${reserva.total}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Accent)
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (reserva.estado.lowercase() == "activa") {
                        FilledTonalButton(
                            onClick = onCancel,
                            colors = ButtonDefaults.filledTonalButtonColors(containerColor = Error.copy(alpha = 0.1f), contentColor = Error),
                            shape = Shapes.medium
                        ) {
                            Text("Cancelar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    IconButton(
                        onClick = onViewFactura,
                        modifier = Modifier.background(Accent.copy(alpha = 0.1f), CircleShape)
                    ) {
                        Icon(Icons.Default.ReceiptLong, null, tint = Accent, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun PerfilTab(
    username: String,
    email: String,
    rol: String,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = Accent,
                shadowElevation = 4.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(username.take(1).uppercase(), color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                }
            }
            Surface(
                Modifier.size(32.dp).offset(x = (-4).dp, y = (-4).dp),
                shape = CircleShape,
                color = Surface,
                border = BorderStroke(2.dp, Background)
            ) {
                Icon(Icons.Default.CameraAlt, null, Modifier.padding(6.dp), tint = Accent)
            }
        }

        Text(username, Modifier.padding(top = 16.dp), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text(email, color = TextSecondary, fontSize = 14.sp)

        Spacer(Modifier.height(32.dp))

        // Opciones de Perfil tipo Lista Moderna
        ProfileOptionItem(Icons.Default.Person, "Información Personal", "Gestiona tu nombre y datos", onEditProfile)
        ProfileOptionItem(Icons.Default.Security, "Seguridad", "Cambia tu contraseña", onEditProfile)
        ProfileOptionItem(Icons.Default.Settings, "Preferencias", "Idioma y notificaciones") { }
        
        Spacer(Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Error),
            border = BorderStroke(1.dp, Error),
            shape = Shapes.large
        ) {
            Icon(Icons.Default.Logout, null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProfileOptionItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(Modifier.size(40.dp), CircleShape, Accent.copy(alpha = 0.1f)) {
                Icon(icon, null, Modifier.padding(10.dp), tint = Accent)
            }
            Column(Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(subtitle, fontSize = 12.sp, color = TextFaint)
            }
            Icon(Icons.Default.ChevronRight, null, tint = TextFaint)
        }
    }
}

// ── DIÁLOGOS ADICIONALES ─────────────────────────────────────────────────────

@Composable
fun FacturaDialog(reserva: Reserva, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Entendido", color = Accent, fontWeight = FontWeight.Bold) }
        },
        title = { Text("Recibo de Reserva", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(Modifier.fillMaxWidth()) {
                ReceiptRow("Habitación", reserva.habitacionNumero)
                ReceiptRow("Estancia", "${reserva.noches} noches")
                ReceiptRow("Fecha", reserva.fechaEntrada)
                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Border)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Pagado", fontWeight = FontWeight.Bold)
                    Text("L. ${reserva.total}", fontWeight = FontWeight.ExtraBold, color = Accent, fontSize = 20.sp)
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = Surface
    )
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextSecondary)
        Text(value, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EditProfileDialog(currentUsername: String, onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var name by remember { mutableStateOf(currentUsername) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre de usuario") })
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, "") }, colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
                Text("Guardar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}
