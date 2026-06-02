package com.reservahotel.reservasapplication.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object AdminDashboard : Screen("admin_dashboard")
    object Habitaciones : Screen("habitaciones")
    object AddEditHabitacion : Screen("add_edit_habitacion/{habitacionId}") {
        fun createRoute(habitacionId: Int = -1) = "add_edit_habitacion/$habitacionId"
    }
    object Categorias : Screen("categorias")
    object Reservas : Screen("reservas")
    object UserHome : Screen("user_home")
}
