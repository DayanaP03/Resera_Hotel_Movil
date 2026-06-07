package com.reservahotel.reservasapplication.presentation.client

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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
import com.reservahotel.reservasapplication.domain.model.Habitacion
import com.reservahotel.reservasapplication.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaBottomSheet(
    habitacion: Habitacion,
    clienteId: Int,          // ID del cliente logueado
    isCreating: Boolean,
    error: String?,
    onConfirmar: (fechaEntrada: String, fechaSalida: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var fechaEntrada by remember { mutableStateOf("") }
    var fechaSalida  by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    var showDatePickerEntrada by remember { mutableStateOf(false) }
    var showDatePickerSalida by remember { mutableStateOf(false) }

    val datePickerStateEntrada = rememberDatePickerState()
    val datePickerStateSalida = rememberDatePickerState()

    val dateFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    // Dialog Entradas
    if (showDatePickerEntrada) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerEntrada = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStateEntrada.selectedDateMillis?.let {
                        fechaEntrada = dateFormatter.format(Date(it))
                    }
                    showDatePickerEntrada = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerEntrada = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerStateEntrada)
        }
    }

    // Dialog Salida
    if (showDatePickerSalida) {
        DatePickerDialog(
            onDismissRequest = { showDatePickerSalida = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerStateSalida.selectedDateMillis?.let {
                        fechaSalida = dateFormatter.format(Date(it))
                    }
                    showDatePickerSalida = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerSalida = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerStateSalida)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor   = Surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .navigationBarsPadding(),
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Hotel, null, tint = Accent, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Reservar Habitación", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 18.sp)
                    Text("Habitación ${habitacion.numero} · ${habitacion.tipoDisplay}", color = TextSecondary, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Precio
            Surface(
                color = Accent.copy(alpha = 0.12f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "L. ${habitacion.precio_noche} por noche · Cap. ${habitacion.capacidad} personas",
                    color = Accent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(12.dp),
                )
            }

            Spacer(Modifier.height(20.dp))

            // Fecha entrada
            Text("Fecha de entrada", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value         = fechaEntrada,
                onValueChange = { },
                readOnly      = true,
                placeholder   = { Text("Seleccionar fecha", color = TextFaint) },
                leadingIcon   = { Icon(Icons.Default.CalendarToday, null, tint = Accent) },
                modifier      = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerEntrada = true },
                enabled       = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = TextPrimary,
                    disabledBorderColor = Border,
                    disabledLeadingIconColor = Accent,
                    disabledPlaceholderColor = TextFaint,
                    disabledLabelColor = TextSecondary
                )
            )

            Spacer(Modifier.height(12.dp))

            // Fecha salida
            Text("Fecha de salida", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(4.dp))
            OutlinedTextField(
                value         = fechaSalida,
                onValueChange = { },
                readOnly      = true,
                placeholder   = { Text("Seleccionar fecha", color = TextFaint) },
                leadingIcon   = { Icon(Icons.Default.EventAvailable, null, tint = Accent) },
                modifier      = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePickerSalida = true },
                enabled       = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = TextPrimary,
                    disabledBorderColor = Border,
                    disabledLeadingIconColor = Accent,
                    disabledPlaceholderColor = TextFaint,
                    disabledLabelColor = TextSecondary
                )
            )

            // Errores
            val displayError = validationError ?: error
            if (displayError != null) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Error, null, tint = Error, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(displayError, color = Error, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botón confirmar
            Button(
                onClick = {
                    validationError = when {
                        fechaEntrada.isBlank() -> "Ingresá la fecha de entrada"
                        fechaSalida.isBlank()  -> "Ingresá la fecha de salida"
                        fechaEntrada >= fechaSalida -> "La fecha de salida debe ser posterior a la entrada"
                        else -> null
                    }
                    if (validationError == null) {
                        onConfirmar(fechaEntrada, fechaSalida)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Accent),
                enabled  = !isCreating,
            ) {
                if (isCreating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Confirmar Reserva", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar", color = TextSecondary)
            }
        }
    }
}
