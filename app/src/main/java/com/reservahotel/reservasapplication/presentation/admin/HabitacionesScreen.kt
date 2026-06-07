package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reservahotel.reservasapplication.domain.model.Habitacion
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitacionesScreen(
    viewModel: HabitacionViewModel,
    onBack: () -> Unit,
    onAddHabitacion: () -> Unit,
    onEditHabitacion: (Int) -> Unit
) {
    val state = viewModel.state
    var searchText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Todas") }
    val roomTypes = listOf("Todas", "Sencilla", "Doble", "Suite", "Deluxe")

    // Lógica de filtrado inteligente (Igual que en cliente)
    val filteredHabitaciones = remember(state.habitaciones, searchText, selectedType) {
        state.habitaciones.filter { h ->
            val query = searchText.trim().lowercase()
            val matchesText = query.isEmpty() || 
                             h.numero.lowercase().contains(query) || 
                             h.tipo.lowercase().contains(query)
            
            val matchesType = selectedType == "Todas" || h.tipo.equals(selectedType, ignoreCase = true)
            matchesText && matchesType
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Surface)) {
                TopAppBar(
                    title = { Text("Gestión de Habitaciones", fontWeight = FontWeight.ExtraBold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
                )
                // Barra de búsqueda rápida
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar por número o tipo...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Accent) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Accent)
                )
                // Chips de categorías
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddHabitacion,
                containerColor = Accent,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("Nueva") }
            )
        },
        containerColor = Background
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Accent)
                filteredHabitaciones.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Inbox, null, Modifier.size(64.dp), TextFaint)
                        Text("No se encontraron habitaciones", color = TextSecondary)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(filteredHabitaciones) { habitacion ->
                            HabitacionAdminItem(
                                habitacion = habitacion,
                                onEdit = { onEditHabitacion(habitacion.id) },
                                onDelete = { viewModel.deleteHabitacion(habitacion.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HabitacionAdminItem(
    habitacion: Habitacion,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono visual según tipo
            Surface(
                modifier = Modifier.size(48.dp),
                color = when(habitacion.tipo.lowercase()) {
                    "suite" -> Color(0xFFFFD700).copy(alpha = 0.1f)
                    "deluxe" -> Color(0xFF8A2BE2).copy(alpha = 0.1f)
                    else -> Accent.copy(alpha = 0.1f)
                },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = when(habitacion.tipo.lowercase()) {
                        "suite" -> Icons.Default.KingBed
                        "doble" -> Icons.Default.Bed
                        else -> Icons.Default.MeetingRoom
                    },
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = Accent
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Habitación ${habitacion.numero}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = TextPrimary
                )
                Text(
                    text = habitacion.tipo,
                    fontSize = 13.sp,
                    color = Accent,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "L. ${habitacion.precio_noche} • Cap: ${habitacion.capacidad}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            Row {
                IconButton(onClick = onEdit) { 
                    Icon(Icons.Default.Edit, "Editar", tint = Info, modifier = Modifier.size(20.dp)) 
                }
                IconButton(onClick = onDelete) { 
                    Icon(Icons.Default.Delete, "Borrar", tint = Error, modifier = Modifier.size(20.dp)) 
                }
            }
        }
    }
}
