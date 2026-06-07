package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reservahotel.reservasapplication.domain.model.Servicio
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    onBack: () -> Unit,
    onAddCategory: () -> Unit,   // kept for nav compat, unused
    onEditCategory: (Int) -> Unit, // kept for nav compat, unused
    viewModel: CategoryViewModel,
) {
    val state = viewModel.state
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Servicios", fontWeight = FontWeight.Bold) },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick  = { showDialog = true },
                containerColor = Accent,
                contentColor   = Color.White,
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Servicio")
            }
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
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Accent)
                }
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Default.WifiOff, null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No se pudieron cargar los servicios", color = TextSecondary)
                        Text(state.error, color = TextFaint, fontSize = 12.sp)
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.loadServicios() },
                            colors  = ButtonDefaults.buttonColors(containerColor = Accent),
                        ) { Text("Reintentar") }
                    }
                }
                state.servicios.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(Icons.Default.RoomService, null, tint = TextFaint, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("No hay servicios registrados", color = TextFaint)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(state.servicios) { servicio ->
                            ServicioCard(
                                servicio      = servicio,
                                onToggle      = { viewModel.toggleActivo(servicio.id) },
                                onDelete      = { viewModel.deleteServicio(servicio.id) },
                            )
                        }
                    }
                }
            }
        }
    }

    // ── Diálogo nuevo servicio ────────────────────────────────────────────────
    if (showDialog) {
        var nombre      by remember { mutableStateOf("") }
        var descripcion by remember { mutableStateOf("") }
        var precio      by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor   = Surface,
            shape            = RoundedCornerShape(16.dp),
            title = {
                Text("Nuevo Servicio", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value         = nombre,
                        onValueChange = { nombre = it },
                        label         = { Text("Nombre del servicio") },
                        modifier      = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value         = descripcion,
                        onValueChange = { descripcion = it },
                        label         = { Text("Descripción") },
                        modifier      = Modifier.fillMaxWidth(),
                        maxLines      = 3,
                    )
                    OutlinedTextField(
                        value          = precio,
                        onValueChange  = { precio = it },
                        label          = { Text("Precio (L.)") },
                        modifier       = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (nombre.isNotBlank() && precio.isNotBlank()) {
                            viewModel.addServicio(nombre, descripcion, precio)
                            showDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Accent),
                ) { Text("Guardar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
        )
    }
}

@Composable
private fun ServicioCard(
    servicio: Servicio,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = Surface),
        border   = BorderStroke(1.dp, Border),
        shape    = Shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icono
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Accent.copy(alpha = 0.12f),
                modifier = Modifier.size(44.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.RoomService,
                        contentDescription = null,
                        tint     = Accent,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = servicio.nombre,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary,
                    fontSize   = 15.sp,
                )
                if (!servicio.descripcion.isNullOrBlank()) {
                    Text(
                        text     = servicio.descripcion,
                        color    = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 2,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text       = "L. ${servicio.precio}",
                    color      = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                )
            }

            // Controles
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Switch(
                    checked         = servicio.activo,
                    onCheckedChange = { onToggle() },
                    colors          = SwitchDefaults.colors(
                        checkedTrackColor = Success,
                        checkedThumbColor = Color.White,
                    ),
                )
                Text(
                    text     = if (servicio.activo) "Activo" else "Inactivo",
                    fontSize = 10.sp,
                    color    = if (servicio.activo) Success else TextFaint,
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Error, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
