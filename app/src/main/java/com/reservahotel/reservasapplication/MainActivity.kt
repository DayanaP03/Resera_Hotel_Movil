package com.reservahotel.reservasapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reservahotel.reservasapplication.data.local.TokenDataStore
import com.reservahotel.reservasapplication.presentation.admin.*
import com.reservahotel.reservasapplication.presentation.admin.HabitacionesScreen as AdminHabitacionesScreen
import com.reservahotel.reservasapplication.presentation.admin.HabitacionViewModel as AdminHabitacionViewModel
import com.reservahotel.reservasapplication.presentation.admin.ReservasScreen as AdminReservasScreen
import com.reservahotel.reservasapplication.presentation.client.HabitacionesScreen as ClientHabitacionesScreen
import com.reservahotel.reservasapplication.presentation.client.HabitacionViewModel as ClientHabitacionViewModel
import com.reservahotel.reservasapplication.presentation.client.MisReservasScreen
import com.reservahotel.reservasapplication.presentation.client.ReservaViewModel
import com.reservahotel.reservasapplication.presentation.auth.*
import com.reservahotel.reservasapplication.presentation.navigation.Screen
import com.reservahotel.reservasapplication.theme.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var tokenDataStore: TokenDataStore

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HotelAppTheme {
                val userSession by tokenDataStore.userSnapshot.collectAsState(initial = null)
                val isLoggedIn by tokenDataStore.isLoggedIn.collectAsState(initial = false)
                val scope = rememberCoroutineScope()

                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()

                    LaunchedEffect(userSession) {
                        android.util.Log.d(
                            "ROL",
                            "Usuario=${userSession?.username} Rol=${userSession?.rol}"
                        )
                    }

                    val startDestination = when {
                        !isLoggedIn -> Screen.Login.route
                        userSession?.rol == "administrador" -> Screen.AdminDashboard.route
                        else -> Screen.UserHome.route
                    }

                    // ViewModels diferenciados
                    val adminVM: AdminHabitacionViewModel = hiltViewModel()
                    val clientVM: ClientHabitacionViewModel = hiltViewModel()
                    val reservaVM: ReservaViewModel = hiltViewModel()
                    val categoryVM: CategoryViewModel = hiltViewModel()
                    val userVM: UserViewModel = hiltViewModel()

                    NavHost(navController = navController, startDestination = startDestination) {

                        composable(Screen.Login.route) {
                            LoginScreen(
                                viewModel = hiltViewModel<AuthViewModel>(),
                                onLoginSuccess = { rol ->
                                    val dest =
                                        if (rol == "administrador")
                                            Screen.AdminDashboard.route
                                        else
                                            Screen.UserHome.route

                                    navController.navigate(dest) {
                                        popUpTo(Screen.Login.route) {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateToRegister = { navController.navigate("register_route") }
                            )
                        }

                        composable("register_route") {
                            RegisterScreen(onNavigateToLogin = { navController.popBackStack() })
                        }

                        // --- ADMIN ZONE ---
                        composable(Screen.AdminDashboard.route) {
                            AdminDashboardScreen(
                                onLogout = { scope.launch { tokenDataStore.clearSession(); navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } } },
                                onNavigateToHabitaciones = { navController.navigate(Screen.Habitaciones.route) },
                                onNavigateToCategorias = { navController.navigate(Screen.Categorias.route) },
                                onNavigateToReservas = { navController.navigate(Screen.Reservas.route) },
                                onNavigateToUsuarios = { navController.navigate(Screen.Usuarios.route) },
                                onNavigateToReportes = { navController.navigate(Screen.Reportes.route) },
                                onNavigateToConfiguracion = { navController.navigate(Screen.Configuracion.route) }
                            )
                        }

                        composable(Screen.Habitaciones.route) {
                            AdminHabitacionesScreen(
                                viewModel = adminVM,
                                onBack = { navController.popBackStack() },
                                onAddHabitacion = { navController.navigate(Screen.AddEditHabitacion.createRoute()) },
                                onEditHabitacion = { id -> navController.navigate(Screen.AddEditHabitacion.createRoute(id)) }
                            )
                        }

                        composable(Screen.AddEditHabitacion.route) { backStackEntry ->
                            val habitacionId = backStackEntry.arguments?.getString("habitacionId")?.toIntOrNull() ?: -1
                            AddEditHabitacionScreen(
                                habitacionId = habitacionId,
                                onBack = { navController.popBackStack() },
                                viewModel = adminVM
                            )
                        }

                        composable(Screen.Categorias.route) {
                            CategoriesScreen(
                                onBack = { navController.popBackStack() },
                                onAddCategory = { /* Implementar navegación o diálogo */ },
                                onEditCategory = { id -> /* Implementar */ },
                                viewModel = categoryVM
                            )
                        }

                        composable(Screen.Usuarios.route) {
                            UsersScreen(
                                onBack = { navController.popBackStack() },
                                viewModel = userVM
                            )
                        }

                        composable(Screen.Reportes.route) {
                            ReportsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Configuracion.route) {
                            SettingsScreen(
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Reservas.route) {
                            AdminReservasScreen(
                                viewModel = reservaVM,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- CLIENT ZONE ---
                        composable(Screen.UserHome.route) {
                            ClientHabitacionesScreen(
                                viewModel = clientVM,
                                onBack = { scope.launch { tokenDataStore.clearSession(); navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } } } },
                                onNavigateToMisReservas = { navController.navigate(Screen.MisReservas.route) }
                            )
                        }

                        composable(Screen.MisReservas.route) {
                            MisReservasScreen(
                                viewModel = reservaVM,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}