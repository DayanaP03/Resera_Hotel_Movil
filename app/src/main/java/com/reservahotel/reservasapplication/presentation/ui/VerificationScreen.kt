package com.reservahotel.reservasapplication.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.reservahotel.reservasapplication.domain.model.Category
import com.reservahotel.reservasapplication.theme.*
import com.reservahotel.reservasapplication.BuildConfig

@Composable
fun VerificationScreen(
    connectionStatus: String = "Sin conectar",
    categories: List<Category> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp, top = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Accent, Shapes.small),
                contentAlignment = Alignment.Center
            ) {
                Text("H", color = AccentOnDark, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "HotelApp",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Sistema de Reservas de Hotel",
            color = Accent,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Módulo 2 · Conexión con Backend",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // API Status Card
        Surface(
            color = Surface,
            shape = Shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Border, Shapes.medium)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ESTADO DE CONEXIÓN", color = TextFaint, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = connectionStatus,
                    color = if (connectionStatus.startsWith("✅")) Success else if (connectionStatus.startsWith("❌")) Color.Red else Info,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
                Text(
                    text = BuildConfig.API_BASE_URL,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Categorías Disponibles",
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay categorías para mostrar", color = TextFaint, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    ModelItem(name = "${category.name} (${category.totalProducts} productos)")
                }
            }
        }
    }
}

@Composable
fun ModelItem(name: String) {
    Surface(
        color = Surface2,
        shape = Shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Accent, androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = name, color = TextPrimary, fontSize = 14.sp)
        }
    }
}
