package com.example.campasscompass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// ============================================================
// DATA MODEL
// ============================================================

data class CampusLocation(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val floor: String,
    val walkingTime: String
)

// ============================================================
// CAMPUS LOCATIONS
// ============================================================
val campusLocations = listOf(
    CampusLocation(
        id = "bathroom",
        name = "Bathroom",
        description = "Student bathroom facilities",
        category = "Facilities",
        floor = "Ground Floor",
        walkingTime = "2 min"
    ),
    CampusLocation(
        id = "computer_lab",
        name = "Computer Lab",
        description = "Computers and study technology",
        category = "Academic",
        floor = "1st Floor",
        walkingTime = "4 min"
    ),
    CampusLocation(
        id = "library",
        name = "Library",
        description = "Books, study areas and research support",
        category = "Academic",
        floor = "Ground Floor",
        walkingTime = "3 min"
    ),
    CampusLocation(
        id = "it",
        name = "IT Department",
        description = "Technical and computer assistance",
        category = "Support",
        floor = "1st Floor",
        walkingTime = "5 min"
    ),
    CampusLocation(
        id = "reception",
        name = "Reception",
        description = "General campus information",
        category = "Support",
        floor = "Ground Floor",
        walkingTime = "1 min"
    ),
    CampusLocation(
        id = "cafeteria",
        name = "Cafeteria",
        description = "Food, drinks and student seating",
        category = "Food",
        floor = "Ground Floor",
        walkingTime = "3 min"
    ),
    CampusLocation(
        id = "auditorium",
        name = "Auditorium",
        description = "Large lecture and event venue",
        category = "Academic",
        floor = "Ground Floor",
        walkingTime = "5 min"
    ),
    CampusLocation(
        id = "front_desk",
        name = "Front Desk",
        description = "Student assistance and enquiries",
        category = "Support",
        floor = "Ground Floor",
        walkingTime = "1 min"
    ),
    CampusLocation(
        id = "lecture_room",
        name = "Lecture Room",
        description = "General teaching and lectures",
        category = "Academic",
        floor = "1st Floor",
        walkingTime = "4 min"
    ),
    CampusLocation(
        id = "main_entrance",
        name = "Main Entrance",
        description = "Main entrance and exit",
        category = "Entrance",
        floor = "Ground Floor",
        walkingTime = "2 min"
    )
)

// ============================================================
// VIEW MODEL
// ============================================================

class CampusViewModel : ViewModel() {
    var isLoggedIn by mutableStateOf(false)
        private set

    var username by mutableStateOf("")
        private set

    var selectedLocation by mutableStateOf<CampusLocation?>(null)
        private set

    fun login(usernameInput: String, password: String): Boolean {
        if (usernameInput.isNotBlank() && password.isNotBlank()) {
            username = usernameInput
            isLoggedIn = true
            return true
        }
        return false
    }

    fun logout() {
        username = ""
        selectedLocation = null
        isLoggedIn = false
    }

    fun selectLocation(location: CampusLocation) {
        selectedLocation = location
    }
}

// ============================================================
// THEME
// ============================================================

@Composable
fun CampusCompassTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(),
        content = content
    )
}

// ============================================================
// MAIN ACTIVITY
// ============================================================

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CampusCompassTheme {
                CampusCompassApp()
            }
        }
    }
}

// ============================================================
// APPLICATION
// ============================================================

@Composable
fun CampusCompassApp(
    viewModel: CampusViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val navController = rememberNavController()

    if (!viewModel.isLoggedIn) {
        LoginScreen(
            onLogin = { username, password ->
                viewModel.login(username, password)
            }
        )
    } else {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController)
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") {
                    HomeScreen(
                        username = viewModel.username,
                        onFindLocation = {
                            navController.navigate("locations")
                        },
                        onHelp = {
                            navController.navigate("resources")
                        }
                    )
                }

                composable("locations") {
                    LocationResultsScreen(
                        viewModel = viewModel,
                        onLocationSelected = {
                            navController.navigate("location_detail")
                        }
                    )
                }

                composable("location_detail") {
                    LocationDetailScreen(
                        viewModel = viewModel,
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable("resources") {
                    ResourcesScreen()
                }

                composable("profile") {
                    ProfileScreen(
                        username = viewModel.username,
                        onLogout = {
                            viewModel.logout()
                            navController.navigate("home") {
                                popUpTo(0)
                            }
                        }
                    )
                }
            }
        }
    }
}

// ============================================================
// LOGIN SCREEN
// ============================================================

@Composable
fun LoginScreen(
    onLogin: (String, String) -> Boolean
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = Color(0xFF247A5B),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CC",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "CampusCompass",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Your private campus assistant",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(30.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it
                showError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Student account") },
            placeholder = { Text("Enter your username") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                showError = false
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(Modifier.height(18.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = {
                val success = onLogin(username, password)
                showError = !success
            }
        ) {
            Text(
                text = "Log In",
                fontSize = 16.sp
            )
        }

        if (showError) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Please enter your account and password.",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(25.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Privacy",
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    text = "Your account information is displayed only inside your logged-in CampusCompass session."
                )
            }
        }
    }
}

// ============================================================
// BOTTOM NAVIGATION
// ============================================================

@Composable
fun BottomNavigationBar(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "home"

    NavigationBar {
        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    popUpTo("home")
                    launchSingleTop = true
                }
            },
            icon = { Text("⌂", fontSize = 24.sp) },
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = currentRoute == "locations" || currentRoute == "location_detail",
            onClick = {
                navController.navigate("locations") {
                    launchSingleTop = true
                }
            },
            icon = { Text("⌖", fontSize = 24.sp) },
            label = { Text("Find") }
        )

        NavigationBarItem(
            selected = currentRoute == "resources",
            onClick = {
                navController.navigate("resources") {
                    launchSingleTop = true
                }
            },
            icon = { Text("▦", fontSize = 24.sp) },
            label = { Text("Help") }
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    launchSingleTop = true
                }
            },
            icon = { Text("●", fontSize = 21.sp) },
            label = { Text("Account") }
        )
    }
}

// ============================================================
// HOME
// ============================================================

@Composable
fun HomeScreen(
    username: String,
    onFindLocation: () -> Unit,
    onHelp: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Welcome 👋",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = username,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    Text(
                        text = "Where do you need to go?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Find important places around campus quickly."
                    )
                    Spacer(Modifier.height(18.dp))
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onFindLocation
                    ) {
                        Text(
                            text = "Find a Location",
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Quick access",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            QuickLocationGrid(
                onClick = onFindLocation
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHelp() }
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💡",
                        fontSize = 28.sp
                    )
                    Spacer(Modifier.width(14.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Need help?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Find campus services and assistance."
                        )
                    }
                    Text(
                        text = "›",
                        fontSize = 28.sp
                    )
                }
            }
        }
    }
}

// ============================================================
// QUICK LOCATION GRID
// ============================================================

@Composable
fun QuickLocationGrid(
    onClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickLocationButton(
                emoji = "🚪",
                text = "Main Entrance",
                modifier = Modifier.weight(1f),
                onClick = onClick
            )
            QuickLocationButton(
                emoji = "📚",
                text = "Library",
                modifier = Modifier.weight(1f),
                onClick = onClick
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickLocationButton(
                emoji = "💻",
                text = "Computer Lab",
                modifier = Modifier.weight(1f),
                onClick = onClick
            )
            QuickLocationButton(
                emoji = "🍴",
                text = "Cafeteria",
                modifier = Modifier.weight(1f),
                onClick = onClick
            )
        }
    }
}

@Composable
fun QuickLocationButton(
    emoji: String,
    text: String,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(105.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emoji,
                fontSize = 25.sp
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============================================================
// LOCATION RESULTS
// ============================================================

@Composable
fun LocationResultsScreen(
    viewModel: CampusViewModel,
    onLocationSelected: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    val filteredLocations = campusLocations.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Find a Location",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Choose where you need to go.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(15.dp))

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search campus") },
                placeholder = { Text("Library, bathroom, IT...") },
                singleLine = true
            )

            Spacer(Modifier.height(15.dp))

            Text(
                text = "${filteredLocations.size} locations",
                fontWeight = FontWeight.Bold
            )
        }

        items(filteredLocations) { location ->
            LocationResultCard(
                location = location,
                onClick = {
                    viewModel.selectLocation(location)
                    onLocationSelected()
                }
            )
        }
    }
}

// ============================================================
// LOCATION RESULT CARD
// ============================================================

@Composable
fun LocationResultCard(
    location: CampusLocation,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(17.dp)
    ) {
        Row(
            modifier = Modifier.padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        Color(0xFFE1F2EA),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = locationIcon(location.id),
                    fontSize = 22.sp
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = location.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    text = location.category,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
                Text(
                    text = location.description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = location.walkingTime,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "›",
                    fontSize = 26.sp
                )
            }
        }
    }
}

// ============================================================
// LOCATION ICONS
// ============================================================

fun locationIcon(id: String): String {
    return when (id) {
        "bathroom" -> "🚻"
        "computer_lab" -> "💻"
        "library" -> "📚"
        "it" -> "🖥️"
        "reception" -> "ℹ️"
        "cafeteria" -> "🍴"
        "auditorium" -> "🎭"
        "front_desk" -> "🛎️"
        "lecture_room" -> "🎓"
        "main_entrance" -> "🚪"
        else -> "📍"
    }
}

// ============================================================
// LOCATION DETAIL
// ============================================================

@Composable
fun LocationDetailScreen(
    viewModel: CampusViewModel,
    onBack: () -> Unit
) {
    val location = viewModel.selectedLocation

    if (location == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No location selected.")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        TextButton(
            onClick = onBack
        ) {
            Text("‹ Back to locations")
        }

        Spacer(Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    color = Color(0xFFE8F2ED),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = locationIcon(location.id),
                    fontSize = 55.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = location.name,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = location.name,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = location.description,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(18.dp))

        DetailRow(
            title = "Category",
            value = location.category
        )

        DetailRow(
            title = "Floor",
            value = location.floor
        )

        DetailRow(
            title = "Walking time",
            value = location.walkingTime
        )

        Spacer(Modifier.height(20.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            onClick = {}
        ) {
            Text(
                text = "Start Navigation",
                fontSize = 16.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = onBack
        ) {
            Text("Choose Another Location")
        }
    }
}

// ============================================================
// DETAIL ROW
// ============================================================

@Composable
fun DetailRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============================================================
// RESOURCES / HELP
// ============================================================

@Composable
fun ResourcesScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Campus Help",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Services that can help when something unexpected happens."
            )
        }

        item {
            HelpCard(
                title = "IT Department",
                description = "Get help with computers, Wi-Fi and student technology."
            )
        }

        item {
            HelpCard(
                title = "Reception",
                description = "Get general campus information and directions."
            )
        }

        item {
            HelpCard(
                title = "Front Desk",
                description = "Ask for student assistance."
            )
        }

        item {
            HelpCard(
                title = "Library",
                description = "Find study space, research support and computers."
            )
        }
    }
}

// ============================================================
// HELP CARD
// ============================================================

@Composable
fun HelpCard(
    title: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
// PROFILE / PRIVACY
// ============================================================

@Composable
fun ProfileScreen(
    username: String,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        item {
            Text(
                text = "My Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Signed in as",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = username,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Privacy & Security",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Your profile data is strictly contained within your active session and not shared publicly.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = "Log Out")
            }
        }
    }
}