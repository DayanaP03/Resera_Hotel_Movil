package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

// 1. Definimos el modelo aquí mismo para que no marque error
data class HotelMetric(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(onBack: () -> Unit) {
    // 2. Datos sin imágenes, solo iconos y colores
    // Estos son tus hoteles en el reporte
    val reportData = listOf(
        HotelMetric("Hotel Quito Real", "Ocupación: 92%", Icons.Default.LocationCity, Color(0xFF673AB7)),
        HotelMetric("Hotel Playa Azul", "Ocupación: 75%", Icons.Default.BeachAccess, Color(0xFF03A9F4)),
        HotelMetric("Hotel Montaña", "Ocupación: 40%", Icons.Default.Terrain, Color(0xFF4CAF50)),
        HotelMetric("Hotel Ejecutivo", "Ocupación: 88%", Icons.Default.Business, Color(0xFFFF9800))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes Ejecutivos", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(reportData) { metric ->
                ReportCard(metric)
            }
        }
    }
}

@Composable
fun ReportCard(metric: HotelMetric) {
    Card(
        modifier = Modifier.fillMaxWidth().height(140.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = metric.icon,
                contentDescription = null,
                tint = metric.color,
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(metric.value, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                Text(metric.title, color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}