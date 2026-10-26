package dev.app.gitbrowse.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

// 1. Sample data for our cards.
private data class DemoResult(
    val title: String,
    val description: String
)

private val repositories = listOf(
    DemoResult(
        "skibidi toilet",
        "skkibidi toilet"
    ),
    DemoResult(
        "task",
        "6767676767"
    ),
    DemoResult(
        "weather",
        "weather app"
    )
)

private val people = listOf(
    DemoResult("the rizzler", "he rizzes"),
    DemoResult("goat hanayama kaoru", "12 years old"),
    DemoResult("pener", "pener maker")
)

// 2. Android starts this activity.
@Composable
fun GitBrowseApp() {
    PracticeTheme {
        HomeScreen()
    }
}

// 3. Define the black-and-white colors.
@Composable
private fun PracticeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color(0xFF121212),
            onSurface = Color.White,
            onSurfaceVariant = Color.LightGray,
            outline = Color.Gray,
            surfaceTint = Color.White
        ),
        content = content
    )
}

// 4. Build the screen and store its state.
@Composable
private fun HomeScreen() {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedTab by rememberSaveable {
        mutableStateOf("Repositories")
    }
    var submittedQuery by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val focusManager = LocalFocusManager.current

    fun submitSearch() {
        if (query.isNotBlank()) {
            submittedQuery = query.trim()
            focusManager.clearFocus()
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .imePadding()
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search field.
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text("Search ${selectedTab.lowercase()}…")
                },
                singleLine = true,
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { submitSearch() }
                )
            )

            // Repositories / People controls.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Repositories", "People").forEach { tab ->
                    val selected = selectedTab == tab

                    OutlinedButton(
                        onClick = {
                            selectedTab = tab
                            submittedQuery = null
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp),
                        shape = RectangleShape,
                        border = BorderStroke(
                            1.dp,
                            if (selected) Color.White else Color.Gray
                        ),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor =
                                if (selected) Color.White else Color.Black,
                            contentColor =
                                if (selected) Color.Black else Color.White
                        )
                    ) {
                        Text(tab)
                    }
                }
            }

            // Search button.
            Button(
                onClick = { submitSearch() },
                enabled = query.isNotBlank(),
                shape = RectangleShape,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text("Search")
            }

            // Results appear only after submitting.
            submittedQuery?.let { submitted ->
                Text(
                    text = "Sample results for \"$submitted\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray
                )

                val results =
                    if (selectedTab == "Repositories") {
                        repositories
                    } else {
                        people
                    }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = results,
                        key = { result -> result.title }
                    ) { result ->
                        ResultCard(result)
                    }
                }
            }
        }
    }
}

// 5. A reusable rectangular result card.
@Composable
private fun ResultCard(result: DemoResult) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        border = BorderStroke(1.dp, Color.DarkGray),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = result.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = result.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray
            )
        }
    }
}