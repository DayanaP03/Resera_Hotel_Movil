package com.reservahotel.reservasapplication.presentation.navigation

sealed class Screen(val route: String) {
    object Login         : Screen("login_route")
    object Register      : Screen("register_route")
    object AdminDashboard: Screen("admin_dashboard_route")
    object UserHome      : Screen("user_home_route")

    // Admin sub-screens
    object Habitaciones  : Screen("habitaciones_route")
    object Categorias    : Screen("categorias_route")
    object Reservas      : Screen("reservas_route")
    object Usuarios      : Screen("usuarios_route")
    object Reportes      : Screen("reportes_route")
    object Configuracion : Screen("config_route")

    // Client sub-screen (kept for backwards compat, not used directly)
    object MisReservas   : Screen("mis_reservas_route")

    object AddEditHabitacion : Screen("add_edit_habitacion_route/{habitacionId}") {
        fun createRoute(habitacionId: Int = -1) = "add_edit_habitacion_route/$habitacionId"
    }
}
