package com.godrinking.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.godrinking.app.data.UserRole
import com.godrinking.app.navigation.Screen
import com.godrinking.app.ui.components.SideMenuContent
import com.godrinking.app.ui.theme.GoDrinkingTheme
import com.godrinking.app.ui.theme.PrimaryRed
import kotlinx.coroutines.launch

// ── Bottom Navigation Items ───────────────────────────────────────────────────

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route,    "Início",     Icons.Default.Home),
    BottomNavItem(Screen.Clients.route, "Clientes",   Icons.Default.Person),
    BottomNavItem(Screen.Events.route,  "Eventos",    Icons.Default.CalendarMonth),
    BottomNavItem(Screen.Reports.route, "Relatórios", Icons.Default.BarChart),
    BottomNavItem(Screen.Stock.route,   "Estoque",    Icons.Default.Inventory),
)

// ── Root Composable ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GodrinkingApp(onThemeChange: (String) -> Unit = {}) {
    val navController   = rememberNavController()
    val coroutineScope  = rememberCoroutineScope()
    val drawerState     = rememberDrawerState(DrawerValue.Closed)

    var isLoggedIn    by remember { mutableStateOf(false) }
    var userRole      by remember { mutableStateOf(UserRole.DEV) }
    var userName      by remember { mutableStateOf("Ana") }
    var currentTheme  by remember { mutableStateOf("dark") }
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    var activeWidgets by remember {
        mutableStateOf(listOf("metrics", "calendar", "upcoming_events"))
    }

    // ── Login gate ────────────────────────────────────────────────────────────
    if (!isLoggedIn) {
        LoginScreen(onLogin = { role ->
            userRole  = role
            userName  = if (role == UserRole.GERENTE) "Carlos" else "Ana"
            isLoggedIn = true
        })
        return
    }

    // ── Telas que escondem TopBar / BottomBar ─────────────────────────────────
    val fullScreenRoutes = setOf(
        Screen.Notifications.route, Screen.Quotations.route, Screen.CreateBudget.route,
        Screen.Services.route,      Screen.Profile.route,    Screen.Settings.route,
        Screen.ClientDetail.route,  Screen.ClientForm.route,
        Screen.EventForm.route,     Screen.HomeCustom.route,
        Screen.StockForm.route,
    )
    val isFullScreen = fullScreenRoutes.any { currentRoute.startsWith(it.substringBefore("{")) }

    ModalNavigationDrawer(
        drawerState   = drawerState,
        drawerContent = {
            SideMenuContent(
                userName  = userName,
                userRole  = userRole,
                onClose   = { coroutineScope.launch { drawerState.close() } },
                onProfile = {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate(Screen.Profile.createRoute(userName, userRole.name))
                },
                onEvents = {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate(Screen.Events.route)
                },
                onQuotations = {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate(Screen.Quotations.route)
                },
                onServices = {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate(Screen.Services.route)
                },
                onSettings = {
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate(Screen.Settings.createRoute(currentTheme))
                },
                onLogout = {
                    coroutineScope.launch { drawerState.close() }
                    isLoggedIn = false
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                if (!isFullScreen) {
                    AppTopBar(
                        currentRoute        = currentRoute,
                        notificationCount   = 5,
                        onNotificationClick = { navController.navigate(Screen.Notifications.route) },
                        onMenuClick         = { coroutineScope.launch { drawerState.open() } }
                    )
                }
            },
            bottomBar = {
                if (!isFullScreen) {
                    AppBottomBar(
                        currentRoute   = currentRoute,
                        onTabSelected  = { route ->
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    )
                }
            },
            floatingActionButton = {
                if (!isFullScreen) {
                    when (currentRoute) {
                        Screen.Home.route -> FloatingActionButton(
                            onClick          = { navController.navigate(Screen.HomeCustom.route) },
                            containerColor   = PrimaryRed,
                            contentColor     = Color.White,
                            shape            = RoundedCornerShape(360.dp)
                        ) { Icon(Icons.Default.Tune, "Personalizar") }

                        Screen.Clients.route -> FloatingActionButton(
                            onClick        = { navController.navigate(Screen.ClientForm.route) },
                            containerColor = PrimaryRed,
                            contentColor   = Color.White,
                            shape          = RoundedCornerShape(360.dp)
                        ) { Icon(Icons.Default.Add, "Novo Cliente") }

                        Screen.Events.route -> FloatingActionButton(
                            onClick        = { navController.navigate(Screen.EventForm.route) },
                            containerColor = PrimaryRed,
                            contentColor   = Color.White,
                            shape          = RoundedCornerShape(360.dp)
                        ) { Icon(Icons.Default.Add, "Novo Evento") }

                        Screen.Stock.route -> FloatingActionButton(
                            onClick        = { navController.navigate(Screen.StockForm.route) },
                            containerColor = PrimaryRed,
                            contentColor   = Color.White,
                            shape          = RoundedCornerShape(360.dp)
                        ) { Icon(Icons.Default.Add, "Novo Item") }
                    }
                }
            }

        ) { innerPadding ->
            NavHost(
                navController    = navController,
                startDestination = Screen.Home.route,
                modifier         = Modifier.padding(innerPadding)
            ) {
                // ── Bottom Nav ─────────────────────────────────────────────
                composable(Screen.Home.route) {
                    HomeScreen(
                        userName      = userName,
                        activeWidgets = activeWidgets,
                        onNavigate    = { route ->
                            if (route == Screen.Events.route || route == Screen.Stock.route || route == Screen.Reports.route || route == Screen.Clients.route) {
                                navController.navigate(route) {
                                    popUpTo(Screen.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } else {
                                navController.navigate(route)
                            }
                        }
                    )
                }
                composable(Screen.Clients.route) {
                    ClientsScreen(onViewDetails = { id ->
                        navController.navigate(Screen.ClientDetail.createRoute(id))
                    })
                }
                composable(Screen.Events.route) {
                    EventsScreen(
                        onBack      = { navController.popBackStack() },
                        onNewEvent  = { navController.navigate(Screen.EventForm.route) }
                    )
                }
                composable(Screen.Reports.route) {
                    ReportsScreen()
                }
                composable(Screen.Stock.route) {
                    StockScreen()
                }

                // ── Full-screen overlays ───────────────────────────────────
                composable(Screen.Notifications.route) {
                    NotificationsScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Quotations.route) {
                    QuotationsScreen(
                        onBack = { navController.popBackStack() },
                        onNewQuotation = { navController.navigate(Screen.CreateBudget.route) }
                    )
                }
                composable(Screen.CreateBudget.route) {
                    CreateBudgetScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Services.route) {
                    ServicesScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Profile.route) { backStack ->
                    val uName = backStack.arguments?.getString("userName") ?: userName
                    val uRole = backStack.arguments?.getString("userRole") ?: userRole.name
                    ProfileScreen(
                        userName = uName,
                        userRole = if (uRole == "GERENTE") UserRole.GERENTE else UserRole.DEV,
                        onBack   = { navController.popBackStack() }
                    )
                }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        currentTheme  = currentTheme,
                        onThemeChange = { newTheme ->
                            currentTheme = newTheme
                            onThemeChange(newTheme)
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.ClientDetail.route) { backStack ->
                    val clientId = backStack.arguments?.getString("clientId")?.toIntOrNull() ?: 1
                    ClientDetailScreen(
                        clientId  = clientId,
                        canDelete = userRole == UserRole.GERENTE,
                        onBack    = { navController.popBackStack() }
                    )
                }
                composable(Screen.ClientForm.route) {
                    ClientFormScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.EventForm.route) {
                    EventFormScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.HomeCustom.route) {
                    HomeCustomScreen(
                        activeWidgets = activeWidgets,
                        onToggleWidget = { widgetId ->
                            activeWidgets = if (activeWidgets.contains(widgetId)) {
                                activeWidgets - widgetId
                            } else {
                                activeWidgets + widgetId
                            }
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.StockForm.route) {
                    StockFormScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

// ── Top App Bar ───────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentRoute: String,
    notificationCount: Int,
    onNotificationClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val title = when (currentRoute) {
        Screen.Home.route    -> "Go Drinking!"
        Screen.Clients.route -> "Clientes"
        Screen.Events.route  -> "Eventos"
        Screen.Reports.route -> "Relatórios"
        Screen.Stock.route   -> "Estoque"
        else                 -> "Go Drinking!"
    }

    TopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (notificationCount > 0) {
                        Badge(containerColor = PrimaryRed) {
                            Text(notificationCount.toString(), color = Color.White, fontSize = 9.sp)
                        }
                    }
                }
            ) {
                IconButton(onClick = onNotificationClick) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notificações")
                }
            }
        },
        windowInsets = TopAppBarDefaults.windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor         = MaterialTheme.colorScheme.surface,
            titleContentColor      = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor     = MaterialTheme.colorScheme.onSurface,
        )
    )
}

// ── Bottom Navigation Bar ─────────────────────────────────────────────────────


@Composable
fun AppBottomBar(currentRoute: String, onTabSelected: (String) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected  = currentRoute == item.route,
                onClick   = { onTabSelected(item.route) },
                icon      = { Icon(item.icon, contentDescription = item.label) },
                label     = { Text(item.label, fontSize = 10.sp) },
                colors    = NavigationBarItemDefaults.colors(
                    selectedIconColor   = PrimaryRed,
                    selectedTextColor   = PrimaryRed,
                    indicatorColor      = PrimaryRed.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    GoDrinkingTheme {
        AppBottomBar(
            currentRoute = Screen.Home.route,
            onTabSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GodrinkingAppPreview() {
    GoDrinkingTheme {
        GodrinkingApp()
    }
}

@Preview(showBackground = true)
@Composable
fun AppTopBarPreview() {
    GoDrinkingTheme {
        AppTopBar(
            currentRoute = Screen.Home.route,
            notificationCount = 5,
            onNotificationClick = {},
            onMenuClick = {}
        )
    }
}
