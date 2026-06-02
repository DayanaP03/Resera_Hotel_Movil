package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onLogout: () -> Unit,
    onNavigateToHabitaciones: () -> Unit,
    onNavigateToCategorias: () -> Unit,
    onNavigateToReservas: () -> Unit,
    onNavigateToUsuarios: () -> Unit,
    onNavigateToReportes: () -> Unit,
    onNavigateToConfiguracion: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp, // Cambiado a ExitToApp que es más lógico
                            contentDescription = "Cerrar sesión"
                        )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Gestión del Hotel",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            val menuItems = listOf(
                AdminMenuItem("Habitaciones", Icons.Default.Bed, Accent),
                AdminMenuItem("Reservas", Icons.Default.DateRange, Success),
                AdminMenuItem("Categorías", Icons.Default.Category, Info),
                AdminMenuItem("Usuarios", Icons.Default.Person, Warning),
                AdminMenuItem("Reportes", Icons.Default.BarChart, Accent),
                AdminMenuItem("Configuración", Icons.Default.Settings, TextSecondary)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(menuItems) { item ->
                    AdminCard(item) {
                        when(item.title) {
                            "Habitaciones" -> onNavigateToHabitaciones()
                            "Categorías" -> onNavigateToCategorias()
                            "Reservas" -> onNavigateToReservas()
                            "Usuarios" -> onNavigateToUsuarios()
                            "Reportes" -> onNavigateToReportes()
                            "Configuración" -> onNavigateToConfiguracion()
                        }
                    }
                }
            }
        }
    }
}

data class AdminMenuItem(val title: String, val icon: ImageVector, val color: Color)

@Composable
fun AdminCard(item: AdminMenuItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
        shape = Shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
        }
    }
}