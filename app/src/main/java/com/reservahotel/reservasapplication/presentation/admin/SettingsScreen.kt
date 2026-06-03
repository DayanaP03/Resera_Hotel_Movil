package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var nombreHotel by remember { mutableStateOf("Quito Real") }
    var notificaciones by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configuración", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            item { SectionHeader("Perfil del Hotel") }

            // Fila Nombre
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showDialog = true }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Home, null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Nombre del Hotel", fontWeight = FontWeight.Bold)
                        Text(nombreHotel, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item { SectionHeader("Preferencias") }

            // Fila Notificaciones
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Notifications, null)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Notificaciones", modifier = Modifier.weight(1f))
                    Switch(checked = notificaciones, onCheckedChange = { notificaciones = it })
                }
            }

            item { SectionHeader("Sesión") }

            // Fila Cerrar Sesión
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ExitToApp, null, tint = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Diálogo
        if (showDialog) {
            var tempName by remember { mutableStateOf(nombreHotel) }
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Editar Nombre") },
                text = {
                    OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Nombre") })
                },
                confirmButton = {
                    Button(onClick = { nombreHotel = tempName; showDialog = false }) { Text("Guardar") }
                }
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp),
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold
    )
}