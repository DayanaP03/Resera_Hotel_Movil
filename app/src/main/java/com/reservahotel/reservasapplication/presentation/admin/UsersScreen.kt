package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.reservahotel.reservasapplication.domain.model.Usuario
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(onBack: () -> Unit, viewModel: UserViewModel) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestión de Usuarios") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Accent)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.usuarios) { user ->
                        UserCard(user = user, viewModel = viewModel)
                    }
                }
            }

            if (state.error != null) {
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                ) {
                    Text(text = state.error)
                }
            }
        }
    }
}

@Composable
fun UserCard(user: Usuario, viewModel: UserViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var menuRol by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Accent, modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = user.username.first().uppercase(),
                            color = Color.White,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(user.username, fontWeight = FontWeight.Bold)
                    Text(user.email, style = MaterialTheme.typography.bodySmall)
                    Text(
                        text = if (user.isActive) "Activo" else "Inactivo",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (user.isActive) Success else Error,
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box {
                            AssistChip(
                                onClick = { menuRol = true },
                                label = { Text(user.rol.uppercase()) },
                            )
                            DropdownMenu(
                                expanded = menuRol,
                                onDismissRequest = { menuRol = false },
                            ) {
                                listOf("administrador", "recepcionista", "cliente").forEach { r ->
                                    DropdownMenuItem(
                                        text = { Text(r.uppercase()) },
                                        onClick = {
                                            viewModel.updateChangeRol(user.id, r)
                                            menuRol = false
                                        },
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (user.isActive) "Activo" else "Inactivo",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = user.isActive,
                                onCheckedChange = { viewModel.toggleUserStatus(user.id) },
                            )
                        }
                    }

                    if (user.createdAt.isNotBlank()) {
                        Text(
                            text = "Registrado: ${user.createdAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
    }
}