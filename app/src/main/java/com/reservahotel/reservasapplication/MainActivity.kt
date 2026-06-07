package com.reservahotel.reservasapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reservahotel.reservasapplication.data.local.TokenDataStore
import com.reservahotel.reservasapplication.presentation.admin.AddEditHabitacionScreen
import com.reservahotel.reservasapplication.presentation.admin.AdminDashboardScreen
import com.reservahotel.reservasapplication.presentation.admin.CategoriesScreen
import com.reservahotel.reservasapplication.presentation.admin.CategoryViewModel
import com.reservahotel.reservasapplication.presentation.admin.HabitacionViewModel as AdminHabitacionViewModel
import com.reservahotel.reservasapplication.presentation.admin.HabitacionesScreen as AdminHabitacionesScreen
import com.reservahotel.reservasapplication.presentation.admin.ReportsScreen
import com.reservahotel.reservasapplication.presentation.admin.ReservasScreen as AdminReservasScreen
import com.reservahotel.reservasapplication.presentation.admin.SettingsScreen
import com.reservahotel.reservasapplication.presentation.admin.UserViewModel
import com.reservahotel.reservasapplication.presentation.admin.UsersScreen
import com.reservahotel.reservasapplication.presentation.auth.AuthViewModel
import com.reservahotel.reservasapplication.presentation.auth.LoginScreen
import com.reservahotel.reservasapplication.presentation.auth.RegisterScreen
import com.reservahotel.reservasapplication.presentation.client.ReservaViewModel
import com.reservahotel.reservasapplication.presentation.client.UserDashboardScreen
import com.reservahotel.reservasapplication.presentation.navigation.Screen
import com.reservahotel.reservasapplication.theme.HotelAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenDataStore: TokenDataStore

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HotelAppTheme {
                // Observamos el estado de sesión del DataStore
                val isLoggedIn  by tokenDataStore.isLoggedIn.collectAsState(initial = null)
                val userSession by tokenDataStore.userSnapshot.collectAsState(initial = null)
                val scope = rememberCoroutineScope()

                Surface(modifier = Modifier.fillMaxSize()) {

                    // Mientras no sabemos si hay sesión, mostramos loading
                    if (isLoggedIn == null) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        return@Surface
                    }

                    val navController = rememberNavController()

                    // ─── Destino inicial basado en sesión guardada ───────────────
                    // Se calcula UNA sola vez al arrancar (remember con keys)
                    val startDestination = remember(isLoggedIn, userSession) {
                        when {
                            isLoggedIn == false || userSession == null -> {
                                Log.d(TAG, "startDest → LOGIN (sin sesión)")
                                Screen.Login.route
                            }
                            // isStaff se deriva del rol al guardar, pero usamos ROL directamente
                            userSession!!.rol == "administrador" -> {
                                Log.d(TAG, "startDest → ADMIN (rol=administrador)")
                                Screen.AdminDashboard.route
                            }
                            else -> {
                                Log.d(TAG, "startDest → CLIENTE (rol=${userSession!!.rol})")
                                Screen.UserHome.route
                            }
                        }
                    }

                    // ─── Función de logout reutilizable ─────────────────────────
                    val doLogout: () -> Unit = {
                        scope.launch {
                            tokenDataStore.clearSession()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                    NavHost(
                        navController     = navController,
                        startDestination  = startDestination,
                    ) {

                        // ── LOGIN ────────────────────────────────────────────────
                        composable(Screen.Login.route) {
                            LoginScreen(
                                viewModel = hiltViewModel<AuthViewModel>(),
                                onLoginSuccess = { rolDestino ->
                                    Log.d(TAG, "onLoginSuccess → rol='$rolDestino'")
                                    // El backend devuelve "administrador" o "cliente" en rol
                                    val dest = if (rolDestino == "administrador")
                                        Screen.AdminDashboard.route
                                    else
                                        Screen.UserHome.route
                                    Log.d(TAG, "onLoginSuccess → navegando a $dest")
                                    navController.navigate(dest) {
                                        popUpTo(Screen.Login.route) { inclusive = true }
                                    }
                                },
                                onNavigateToRegister = {
                                    navController.navigate(Screen.Register.route)
                                },
                            )
                        }

                        composable(Screen.Register.route) {
                            RegisterScreen(
                                onNavigateToLogin = { navController.popBackStack() },
                            )
                        }

                        // ── PANEL ADMINISTRADOR ──────────────────────────────────
                        composable(Screen.AdminDashboard.route) {
                            AdminDashboardScreen(
                                onLogout                  = doLogout,
                                onNavigateToHabitaciones  = { navController.navigate(Screen.Habitaciones.route) },
                                onNavigateToCategorias    = { navController.navigate(Screen.Categorias.route) },
                                onNavigateToReservas      = { navController.navigate(Screen.Reservas.route) },
                                onNavigateToUsuarios      = { navController.navigate(Screen.Usuarios.route) },
                                onNavigateToReportes      = { navController.navigate(Screen.Reportes.route) },
                                onNavigateToConfiguracion = { navController.navigate(Screen.Configuracion.route) },
                            )
                        }

                        composable(Screen.Habitaciones.route) {
                            val vm: AdminHabitacionViewModel = hiltViewModel()
                            AdminHabitacionesScreen(
                                viewModel        = vm,
                                onBack           = { navController.popBackStack() },
                                onAddHabitacion  = { navController.navigate(Screen.AddEditHabitacion.createRoute()) },
                                onEditHabitacion = { id -> navController.navigate(Screen.AddEditHabitacion.createRoute(id)) },
                            )
                        }

                        composable(Screen.AddEditHabitacion.route) { back ->
                            val vm: AdminHabitacionViewModel = hiltViewModel()
                            val habitacionId = back.arguments?.getString("habitacionId")?.toIntOrNull() ?: -1
                            AddEditHabitacionScreen(
                                habitacionId = habitacionId,
                                onBack       = { navController.popBackStack() },
                                viewModel    = vm,
                            )
                        }

                        composable(Screen.Categorias.route) {
                            val vm: CategoryViewModel = hiltViewModel()
                            CategoriesScreen(
                                onBack        = { navController.popBackStack() },
                                onAddCategory = { },
                                onEditCategory = { },
                                viewModel     = vm,
                            )
                        }

                        composable(Screen.Usuarios.route) {
                            val vm: UserViewModel = hiltViewModel()
                            UsersScreen(
                                onBack    = { navController.popBackStack() },
                                viewModel = vm,
                            )
                        }

                        composable(Screen.Reportes.route) {
                            ReportsScreen(onBack = { navController.popBackStack() })
                        }

                        composable(Screen.Configuracion.route) {
                            SettingsScreen(
                                onBack   = { navController.popBackStack() },
                                onLogout = doLogout,
                            )
                        }

                        composable(Screen.Reservas.route) {
                            val vm: ReservaViewModel = hiltViewModel()
                            AdminReservasScreen(
                                viewModel = vm,
                                onBack    = { navController.popBackStack() },
                            )
                        }

                        // ── PANEL CLIENTE ────────────────────────────────────────
                        composable(Screen.UserHome.route) {
                            UserDashboardScreen(
                                tokenDataStore = tokenDataStore,
                                onLogout       = doLogout,
                            )
                        }
                    }
                }
            }
        }
    }
}
