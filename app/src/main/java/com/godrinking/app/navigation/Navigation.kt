package com.godrinking.app.navigation

/**
 * Sealed class que representa todas as rotas do app.
 * Adicione novos destinos aqui conforme o app crescer.
 */
sealed class Screen(val route: String) {

    // ── Bottom Nav ────────────────────────────────────────────────────────────
    object Home    : Screen("home")
    object Clients    : Screen("clients")
    object Events     : Screen("events")
    object Reports    : Screen("reports")
    object Stock      : Screen("stock")

    // ── Full-screen overlays ──────────────────────────────────────────────────
    object Notifications : Screen("notifications")
    object Quotations    : Screen("quotations")
    object CreateBudget  : Screen("create_budget")
    object Services      : Screen("services")

    object Profile : Screen("profile/{userName}/{userRole}") {
        fun createRoute(userName: String, userRole: String) =
            "profile/$userName/$userRole"
    }

    object Settings : Screen("settings/{theme}") {
        fun createRoute(theme: String) = "settings/$theme"
    }

    object ClientDetail : Screen("client_detail/{clientId}") {
        fun createRoute(clientId: Int) = "client_detail/$clientId"
    }

    object ClientForm : Screen("client_form")
    object EventForm  : Screen("event_form")
    object HomeCustom : Screen("home_custom")
}
