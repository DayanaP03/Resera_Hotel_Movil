package com.reservahotel.reservasapplication.presentation.client

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reservahotel.reservasapplication.domain.model.Habitacion
import com.reservahotel.reservasapplication.theme.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitacionesScreen(
    onBack: () -> Unit,
    onNavigateToMisReservas: () -> Unit,
    viewModel: HabitacionViewModel,
    reservaViewModel: ReservaViewModel,
    clienteId: Int
) {
    val state = viewModel.state
    val reservaState = reservaViewModel.state
    var selectedHabitacion by remember { mutableStateOf<Habitacion?>(null) }
    
    // Estados de búsqueda y filtros
    var searchText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Todas") }
    var maxPrice by remember { mutableFloatStateOf(5000f) }
    var minCapacity by remember { mutableIntStateOf(1) }
    var showFilters by remember { mutableStateOf(false) }

    val roomTypes = listOf("Todas", "Sencilla", "Doble", "Suite", "Deluxe")

    // Lógica de filtrado súper flexible
    val filteredHabitaciones = remember(state.habitaciones, searchText, selectedType, maxPrice, minCapacity) {
        state.habitaciones.filter { h ->
            val query = searchText.trim().lowercase()
            
            // 1. Buscamos en número, tipo y descripción
            val matchesText = query.isEmpty() || 
                             h.numero.lowercase().contains(query) || 
                             h.tipo.lowercase().contains(query) ||
                             (h.descripcion?.lowercase()?.contains(query) == true)
            
            // 2. Filtro por chips de categoría
            val matchesType = selectedType == "Todas" || h.tipo.trim().equals(selectedType.trim(), ignoreCase = true)
            
            // 3. Limpiamos el precio por si tiene comas o símbolos y comparamos
            val cleanPrice = h.precio_noche.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
            val matchesPrice = cleanPrice <= maxPrice
            
            // 4. Capacidad mínima
            val matchesCapacity = h.capacidad >= minCapacity
            
            matchesText && matchesType && matchesPrice && matchesCapacity
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Surface(
            color = Surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        placeholder = { Text("¿A dónde vamos?") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Accent) },
                        modifier = Modifier.weight(1f),
                        shape = Shapes.medium,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Accent,
                            unfocusedBorderColor = Border
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showFilters = true },
                        modifier = Modifier.background(Accent.copy(alpha = 0.1f), Shapes.medium)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = "Filtros", tint = Accent)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(roomTypes) { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Accent,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Accent)
                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Error al cargar habitaciones", color = TextSecondary)
                        Button(onClick = { viewModel.getHabitaciones() }) { Text("Reintentar") }
                    }
                }
                filteredHabitaciones.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.SearchOff, null, Modifier.size(64.dp), TextFaint)
                        Text("No hay resultados con esos filtros", color = TextSecondary)
                        TextButton(onClick = { 
                            searchText = ""; selectedType = "Todas"; maxPrice = 5000f; minCapacity = 1 
                        }) {
                            Text("Limpiar filtros", color = Accent)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(filteredHabitaciones) { habitacion ->
                            HabitacionClienteItem(
                                habitacion = habitacion,
                                onReservar = { selectedHabitacion = habitacion },
                            )
                        }
                    }
                }
            }
        }
    }

    // Panel de Filtros Avanzados (BottomSheet)
    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = { showFilters = false },
            containerColor = Surface
        ) {
            Column(modifier = Modifier.padding(24.dp).padding(bottom = 32.dp)) {
                Text("Filtros Avanzados", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                
                // Filtro de Precio
                Text("Precio máximo por noche", fontWeight = FontWeight.Medium)
                Text("L. ${maxPrice.roundToInt()}", color = Accent, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Slider(
                    value = maxPrice,
                    onValueChange = { maxPrice = it },
                    valueRange = 500f..5000f,
                    colors = SliderDefaults.colors(thumbColor = Accent, activeTrackColor = Accent)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Filtro de Capacidad
                Text("Capacidad mínima (Personas)", fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    (1..5).forEach { num ->
                        FilterChip(
                            selected = minCapacity == num,
                            onClick = { minCapacity = num },
                            label = { Text("$num+") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { showFilters = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = Shapes.medium
                ) {
                    Text("Aplicar Filtros")
                }
            }
        }
    }

    if (selectedHabitacion != null) {
        ReservaBottomSheet(
            habitacion = selectedHabitacion!!,
            clienteId = clienteId,
            isCreating = reservaState.isCreating,
            error = reservaState.errorCrear,
            onDismiss = { selectedHabitacion = null },
            onConfirmar = { entrada, salida ->
                reservaViewModel.crearReserva(
                    clienteId = clienteId,
                    habitacionId = selectedHabitacion!!.id,
                    fechaEntrada = entrada,
                    fechaSalida = salida
                )
            }
        )
    }

    LaunchedEffect(reservaState.successMessage) {
        if (reservaState.successMessage != null) {
            selectedHabitacion = null
            reservaViewModel.clearSuccess()
        }
    }
}

@Composable
fun HabitacionClienteItem(
    habitacion: Habitacion,
    onReservar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column {
            // Cabecera Visual (Simulando una Foto con Iconos y Colores)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        when (habitacion.tipo.lowercase()) {
                            "suite" -> Color(0xFFFFD700).copy(alpha = 0.1f)
                            "deluxe" -> Color(0xFF8A2BE2).copy(alpha = 0.1f)
                            else -> Accent.copy(alpha = 0.1f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (habitacion.tipo.lowercase()) {
                        "suite" -> Icons.Default.KingBed
                        "doble" -> Icons.Default.Bed
                        else -> Icons.Default.MeetingRoom
                    },
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    tint = Accent
                )
                
                // Badge de Calificación flotante
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    shape = CircleShape
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB400))
                        Spacer(Modifier.width(4.dp))
                        Text("4.8", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Información de la Habitación
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Habitación ${habitacion.numero}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = habitacion.tipo,
                            fontSize = 14.sp,
                            color = Accent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "L. ${habitacion.precio_noche}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = habitacion.descripcion ?: "Disfruta de una estancia de lujo con todas las comodidades que nuestro hotel ofrece para ti.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AmenityIcon(Icons.Default.Person, "${habitacion.capacidad}")
                        AmenityIcon(Icons.Default.Wifi, "")
                        AmenityIcon(Icons.Default.AcUnit, "")
                    }

                    Button(
                        onClick = onReservar,
                        colors = ButtonDefaults.buttonColors(containerColor = Accent),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Reservar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AmenityIcon(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = TextSecondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 12.sp, color = TextSecondary)
    }
}
