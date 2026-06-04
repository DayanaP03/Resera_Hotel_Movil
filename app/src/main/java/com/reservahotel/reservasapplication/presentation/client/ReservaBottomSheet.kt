package com.reservahotel.reservasapplication.presentation.client

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaBottomSheet(
    onDismiss: () -> Unit,
    onConfirmar: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
            Text("Resumen de tu Reserva", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))
            // Aquí iría tu lógica de fechas y precio total
            Text("Habitación: Suite Real")
            Text("Total: $150.00")

            Button(
                onClick = onConfirmar,
                modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text("Confirmar Reserva")
            }
        }
    }
}