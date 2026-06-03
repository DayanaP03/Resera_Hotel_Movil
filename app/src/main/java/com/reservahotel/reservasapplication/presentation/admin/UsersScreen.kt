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
import com.reservahotel.reservasapplication.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(onBack: () -> Unit, viewModel: UserViewModel) {
    var showDialog by remember { mutableStateOf(false) }
    val state = viewModel.state

    Scaffold(
        topBar = { TopAppBar(title = { Text("Gestión de Usuarios") }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }) },
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = true }, containerColor = Accent) { Icon(Icons.Default.Add, null) } }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.usuarios) { user -> UserCard(user, viewModel) }
        }
        if (showDialog) {
            var name by remember { mutableStateOf("") }
            var email by remember { mutableStateOf("") }
            AlertDialog(onDismissRequest = { showDialog = false }, title = { Text("Nuevo Usuario") }, text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
                }
            }, confirmButton = { Button(onClick = { viewModel.addUser(
                name, email,
                rol = TODO()
            ); showDialog = false }) { Text("Guardar") } })
        }
    }
}

@Composable
fun UserCard(user: LocalUser, viewModel: UserViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var menuRol by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth().animateContentSize().clickable { expanded = !expanded }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = Accent, modifier = Modifier.size(40.dp)) { Box(contentAlignment = Alignment.Center) { Text(user.username.first().toString(), color = Color.White) } }
                Spacer(modifier = Modifier.width(16.dp))
                Column { Text(user.username, fontWeight = FontWeight.Bold); Text(user.email, style = MaterialTheme.typography.bodySmall) }
            }
            AnimatedVisibility(visible = expanded) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Box {
                        AssistChip(onClick = { menuRol = true }, label = { Text(user.rol.uppercase()) })
                        DropdownMenu(expanded = menuRol, onDismissRequest = { menuRol = false }) {
                            listOf("admin", "staff", "cliente").forEach { r -> DropdownMenuItem(text = { Text(r) }, onClick = { viewModel.updateChangeRol(user.id, r); menuRol = false }) }
                        }
                    }
                    Switch(checked = user.isActive, onCheckedChange = { viewModel.toggleUserStatus(user.id) })
                }
            }
        }
    }
}