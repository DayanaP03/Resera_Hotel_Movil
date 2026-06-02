package com.reservahotel.reservasapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.reservahotel.reservasapplication.domain.model.Category
import com.reservahotel.reservasapplication.domain.repository.CategoryRepository
import com.reservahotel.reservasapplication.presentation.ui.VerificationScreen
import com.reservahotel.reservasapplication.theme.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

// Imports de Compose indispensables
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState

import com.reservahotel.reservasapplication.data.local.TokenDataStore
import com.reservahotel.reservasapplication.presentation.admin.AddEditHabitacionScreen
import com.reservahotel.reservasapplication.presentation.admin.AdminDashboardScreen
import com.reservahotel.reservasapplication.presentation.admin.HabitacionesScreen
import com.reservahotel.reservasapplication.presentation.admin.HabitacionViewModel
import com.reservahotel.reservasapplication.presentation.admin.ReservaViewModel // NUEVO IMPORT
import com.reservahotel.reservasapplication.presentation.auth.AuthViewModel
import com.reservahotel.reservasapplication.presentation.auth.LoginScreen
import com.reservahotel.reservasapplication.presentation.auth.RegisterScreen
import com.reservahotel.reservasapplication.presentation.navigation.Screen
import androidx.hilt.navigation.compose.hiltViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var categoryRepository: CategoryRepository
    @Inject lateinit var tokenDataStore: TokenDataStore

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HotelAppTheme {
                val userSession by tokenDataStore.userSnapshot.collectAsState(initial = null)
                val isLoggedIn by tokenDataStore.isLoggedIn.collectAsState(initial = false)

                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    val startDestination = when {
                        !isLoggedIn -> Screen.Login.route
                        userSession?.rol == "admin" || userSession?.rol == "staff" -> Screen.AdminDashboard.route
                        else -> Screen.UserHome.route
                    }

                    // =========================================================================
                    // VIEWMODELS COMPARTIDOS (Ubicados correctamente fuera del NavHost)
                    // =========================================================================
                    val habitacionesSharedViewModel: HabitacionViewModel = hiltViewModel()
                    val reservasSharedViewModel: ReservaViewModel = hiltViewModel() // Instancia para Reservas

                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {
                        // PANTALLA: Login
                        composable(Screen.Login.route) {
                            val loginViewModel: AuthViewModel = hiltViewModel()
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { rol ->
                                    val destination = if (rol == "admin" || rol == "staff") Screen.AdminDashboard.route else Screen.UserHome.route
                                    navController.navigate(destination) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register_route")
                                }
                            )
                        }

                        // PANTALLA: Registro
                        composable("register_route") {
                            RegisterScreen(
                                onNavigateToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // PANTALLA: Dashboard Administrator
                        composable(Screen.AdminDashboard.route) {
                            AdminDashboardScreen(
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                onNavigateToHabitaciones = {
                                    navController.navigate(Screen.Habitaciones.route)
                                },
                                onNavigateToCategorias = {
                                    navController.navigate(Screen.Categorias.route)
                                },
                                onNavigateToReservas = {
                                    navController.navigate(Screen.Reservas.route)
                                }
                            )
                        }

                        // PANTALLA: Lista de Habitaciones (Usa el ViewModel compartido)
                        composable(Screen.Habitaciones.route) {
                            HabitacionesScreen(
                                viewModel = habitacionesSharedViewModel,
                                onBack = { navController.popBackStack() },
                                onAddHabitacion = {
                                    navController.navigate(Screen.AddEditHabitacion.createRoute())
                                },
                                onEditHabitacion = { id ->
                                    navController.navigate(Screen.AddEditHabitacion.createRoute(id))
                                }
                            )
                        }

                        // PANTALLA: Crear / Editar Habitación (Usa el MISMO ViewModel compartido)
                        composable(
                            route = Screen.AddEditHabitacion.route,
                            arguments = listOf(navArgument("habitacionId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val habitacionId = backStackEntry.arguments?.getInt("habitacionId") ?: -1
                            AddEditHabitacionScreen(
                                habitacionId = habitacionId,
                                viewModel = habitacionesSharedViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // PANTALLA TEMPORAL: Categorías (Creada aquí para evitar errores)
                        composable(Screen.Categorias.route) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Background),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Pantalla de Categorías", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
                                        Text("Atrás")
                                    }
                                }
                            }
                        }

                        // =========================================================================
                        // ¡MODIFICADO AQUÍ!: PANTALLA DE RESERVAS CON LISTA DE PRUEBA REAL
                        // =========================================================================
                        composable(Screen.Reservas.route) {
                            val state = reservasSharedViewModel.state

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Background)
                                    .padding(top = 36.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Gestión de Reservas", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                        Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = Accent)) {
                                            Text("Volver")
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Lista interactiva que pinta las tarjetas simuladas
                                    androidx.compose.foundation.lazy.LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(state.reservas.size) { index ->
                                            val reserva = state.reservas[index]
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                colors = CardDefaults.cardColors(containerColor = Surface),
                                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                            ) {
                                                Column(modifier = Modifier.padding(16.dp)) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween
                                                    ) {
                                                        Text("Reserva #${reserva.id}", fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 16.sp)
                                                        Text(
                                                            text = reserva.estado,
                                                            color = if (reserva.estado == "Confirmada") Color(0xFF4CAF50) else Color(0xFFFF9800),
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text("Habitación asignada: ${reserva.habitacion}", color = TextSecondary, fontSize = 14.sp)
                                                    Text("ID del Cliente: ${reserva.cliente}", color = TextSecondary, fontSize = 14.sp)
                                                    Text("Fechas: ${reserva.fecha_entrada} hasta ${reserva.fecha_salida}", color = TextSecondary, fontSize = 14.sp)

                                                    reserva.observaciones?.let { obs ->
                                                        if (obs.isNotEmpty()) {
                                                            Spacer(modifier = Modifier.height(6.dp))
                                                            Text("Nota: $obs", color = Accent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // =========================================================================

                        // PANTALLA: Home de Usuario Regular
                        composable(Screen.UserHome.route) {
                            var categories by remember { mutableStateOf<List<Category>>(emptyList()) }
                            var status by remember { mutableStateOf("Conectando...") }

                            LaunchedEffect(Unit) {
                                categoryRepository.getCategories()
                                    .onSuccess {
                                        categories = it
                                        status = "✅ Sesión activa: ${userSession?.username}"
                                    }
                                    .onFailure {
                                        status = "❌ ${it.message}"
                                    }
                            }

                            VerificationScreen(
                                connectionStatus = status,
                                categories = categories
                            )
                        }
                    }
                }
            }
        }
    }
}