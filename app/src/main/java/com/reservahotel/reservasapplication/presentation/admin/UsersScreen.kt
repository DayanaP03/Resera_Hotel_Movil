package com.reservahotel.reservasapplication.presentation.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    onBack: () -> Unit,
    viewModel: UserViewModel = hiltViewModel()
) {
    val state = viewModel.state
    var searchQuery by remember { mutableStateOf("") }

    val usuariosFiltrados = remember(state.usuarios, searchQuery) {
        state.usuarios.filter {
            it.username.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Control de Usuarios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface, titleContentColor = TextPrimary)
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Buscador interactivo
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar usuario por nombre o correo...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (usuariosFiltrados.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No hay usuarios que coincidan.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(usuariosFiltrados) { usuario ->
                        UserItem(
                            user = usuario,
                            onRoleChange = { nuevoRol -> viewModel.updateChangeRol(usuario.id, nuevoRol) },
                            onToggleActive = { viewModel.toggleUserStatus(usuario.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(
    user: LocalUser,
    onRoleChange: (String) -> Unit,
    onToggleActive: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded }
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Surface),
        shape = CardDefaults.elevatedShape
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(user.username, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                    Text(user.email, color = TextSecondary, fontSize = 13.sp)
                }

                // Chip del Rol actual
                Surface(
                    color = when(user.rol) {
                        "admin" -> Color(0xFFE91E63).copy(alpha = 0.1f)
                        "staff" -> Color(0xFF2196F3).copy(alpha = 0.1f)
                        else -> TextSecondary.copy(alpha = 0.1f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = user.rol.uppercase(),
                        color = when(user.rol) {
                            "admin" -> Color(0xFFE91E63)
                            "staff" -> Color(0xFF2196F3)
                            else -> TextPrimary
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // ====== ZONA DESPLEGABLE ======
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = TextSecondary.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("ID de Usuario: #${user.id}", color = TextSecondary, fontSize = 13.sp)
                    Text("Miembro desde: ${user.fechaRegistro}", color = TextSecondary, fontSize = 13.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Controladores dinámicos internos
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Asignar Rol (DropdownMenu)
                        Box {
                            Button(
                                onClick = { showMenu = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Accent.copy(alpha = 0.1f), contentColor = Accent),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text("Asignar Rol", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(text = { Text("Administrador") }, onClick = { onRoleChange("admin"); showMenu = false })
                                DropdownMenuItem(text = { Text("Staff / Recepción") }, onClick = { onRoleChange("staff"); showMenu = false })
                                DropdownMenuItem(text = { Text("Cliente") }, onClick = { onRoleChange("cliente"); showMenu = false })
                            }
                        }

                        // 2. Switch de acceso a la app
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (user.isActive) "Acceso Permitido" else "Cuenta Bloqueada",
                                color = if (user.isActive) Color(0xFF4CAF50) else Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = user.isActive,
                                onCheckedChange = { _ -> onToggleActive() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF4CAF50)
                                )
                            )
                        }
                    }
                }
            }

            if(!isExpanded) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("Ver opciones de cuenta ▼", color = TextSecondary.copy(alpha = 0.4f), fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}