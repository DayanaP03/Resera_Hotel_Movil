package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reservahotel.reservasapplication.domain.model.Habitacion
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHabitacionScreen(
    habitacionId: Int,
    onBack: () -> Unit,
    viewModel: HabitacionViewModel
) {
    val isEdit = habitacionId != -1
    val habitacionToEdit = remember(habitacionId) { viewModel.getHabitacionById(habitacionId) }

    var numero by remember { mutableStateOf(habitacionToEdit?.numero ?: "") }
    var tipo by remember { mutableStateOf(habitacionToEdit?.tipo ?: "") }
    var precio by remember { mutableStateOf(habitacionToEdit?.precio_noche ?: "") }
    var estado by remember { mutableStateOf(habitacionToEdit?.estado ?: "disponible") }
    var capacidad by remember { mutableStateOf(habitacionToEdit?.capacidad?.toString() ?: "2") }
    var descripcion by remember { mutableStateOf(habitacionToEdit?.descripcion ?: "") }

    val state = viewModel.state

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.resetSuccess()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Editar Habitación" else "Nueva Habitación", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                    titleContentColor = TextPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val habitacion = Habitacion(
                        id = if (isEdit) habitacionId else 0,
                        numero = numero,
                        tipo = tipo.trim().lowercase(),
                        precio_noche = precio,
                        estado = when (estado.trim().lowercase()) {
                            "ocupado" -> "ocupada"
                            else -> estado.trim().lowercase()
                        },
                        descripcion = descripcion,
                        capacidad = capacidad.toIntOrNull() ?: 2
                    )
                    viewModel.saveHabitacion(habitacion, isEdit)
                },
                containerColor = Accent,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Save, contentDescription = "Guardar")
            }
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.error != null) {
                Text(text = state.error, color = Error)
            }

            OutlinedTextField(
                value = numero,
                onValueChange = { numero = it },
                label = { Text("Número de Habitación") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Tipo (Simple, Doble, Suite)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio por Noche") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = capacidad,
                onValueChange = { capacidad = it },
                label = { Text("Capacidad (personas)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = estado,
                onValueChange = { estado = it },
                label = { Text("Estado (Disponible, Ocupado, Mantenimiento)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
