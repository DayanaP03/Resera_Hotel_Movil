package com.reservahotel.reservasapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.reservahotel.reservasapplication.data.local.TokenDataStore
import com.reservahotel.reservasapplication.domain.repository.CategoryRepository
import com.reservahotel.reservasapplication.presentation.admin.*
import com.reservahotel.reservasapplication.presentation.auth.*
import com.reservahotel.reservasapplication.presentation.navigation.Screen
import com.reservahotel.reservasapplication.theme.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

                    // ViewModels Compartidos
                    val habitacionesSharedViewModel: HabitacionViewModel = hiltViewModel()
                    val categorySharedViewModel: CategoryViewModel = hiltViewModel()
                    val userSharedViewModel: UserViewModel = hiltViewModel()

                    NavHost(navController = navController, startDestination = startDestination) {

                        composable(Screen.Login.route) {
                            LoginScreen(
                                viewModel = hiltViewModel<AuthViewModel>(),
                                onLoginSuccess = { rol ->
                                    val dest = if (rol == "admin" || rol == "staff") Screen.AdminDashboard.route else Screen.UserHome.route
                                    navController.navigate(dest) { popUpTo(Screen.Login.route) { inclusive = true } }
                                },
                                onNavigateToRegister = { navController.navigate("register_route") }
                            )
                        }

                        composable("register_route") {
                            RegisterScreen(onNavigateToLogin = { navController.popBackStack() })
                        }

                        composable(Screen.AdminDashboard.route) {
                            AdminDashboardScreen(
                                onLogout = {
                                    authViewModel.logout()
                                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                                },
                                onNavigateToHabitaciones = { navController.navigate(Screen.Habitaciones.route) },
                                onNavigateToCategorias = { navController.navigate(Screen.Categorias.route) },
                                onNavigateToReservas = { navController.navigate(Screen.Reservas.route) },
                                onNavigateToUsuarios = { navController.navigate(Screen.Usuarios.route) },
                                onNavigateToReportes = { navController.navigate(Screen.Reportes.route) },
                                onNavigateToConfiguracion = { navController.navigate(Screen.Configuracion.route) }
                            )
                        }

                        composable(Screen.Habitaciones.route) {
                            HabitacionesScreen(
                                viewModel = habitacionesSharedViewModel,
                                onBack = { navController.popBackStack() },
                                onAddHabitacion = { navController.navigate(Screen.AddEditHabitacion.createRoute()) },
                                onEditHabitacion = { id -> navController.navigate(Screen.AddEditHabitacion.createRoute(id)) }
                            )
                        }

                        composable(
                            route = Screen.AddEditHabitacion.route,
                            arguments = listOf(navArgument("habitacionId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            AddEditHabitacionScreen(
                                habitacionId = backStackEntry.arguments?.getInt("habitacionId") ?: -1,
                                viewModel = habitacionesSharedViewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.Categorias.route) {
                            CategoriesScreen(
                                viewModel = categorySharedViewModel,
                                onBack = { navController.popBackStack() },
                                onAddCategory = { },
                                onEditCategory = { }
                            )
                        }

                        // Módulos nuevos conectados vía Screen.X
                        composable(Screen.Usuarios.route) {
                            UsersScreen(viewModel = userSharedViewModel, onBack = { navController.popBackStack() })
                        }

                        composable(Screen.Reportes.route) {
                            PlaceholderScreen(title = "Reportes y Estadísticas", onBack = { navController.popBackStack() })
                        }

                        composable(Screen.Configuracion.route) {
                            PlaceholderScreen(title = "Configuraciones", onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Sección en desarrollo", color = Color.Gray, fontSize = 16.sp)
        }
    }
}