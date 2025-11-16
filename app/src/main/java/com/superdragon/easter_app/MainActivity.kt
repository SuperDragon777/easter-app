package com.superdragon.easter_app

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.superdragon.easter_app.ui.theme.EasterappTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class PhrasesData(val phrases: List<String> = emptyList())

object PhrasesStorage {
    private const val PREFS_NAME = "phrases_storage"
    private const val PREFS_KEY = "discovered_phrases"

    fun savePhrases(context: Context, phrases: List<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val data = PhrasesData(phrases)
        val json = Json.encodeToString(data)
        prefs.edit().putString(PREFS_KEY, json).apply()
    }

    fun loadPhrases(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(PREFS_KEY, null) ?: return emptyList()
        return try {
            val data = Json.decodeFromString<PhrasesData>(json)
            data.phrases
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clearPhrases(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(PREFS_KEY).apply()
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EasterappTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    var discoveredPhrases by remember { mutableStateOf<List<String>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        discoveredPhrases = PhrasesStorage.loadPhrases(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = "Words") },
                    label = { Text("Words") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> Greeting(
                modifier = Modifier.padding(innerPadding),
                onPhrasDiscovered = { phrase ->
                    if (!discoveredPhrases.contains(phrase)) {
                        discoveredPhrases = discoveredPhrases + phrase
                        PhrasesStorage.savePhrases(context, discoveredPhrases)
                    }
                }
            )
            1 -> SecondScreen(
                modifier = Modifier.padding(innerPadding),
                phrases = discoveredPhrases,
                onClearData = {
                    discoveredPhrases = emptyList()
                    PhrasesStorage.clearPhrases(context)
                }
            )
        }
    }
}

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    onPhrasDiscovered: (String) -> Unit = {}
) {
    var inputText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("") }
    var showAuthorDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                keyboardController?.hide()
                focusRequester.freeFocus()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                label = { Text("Use your imagination") },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusRequester.freeFocus()
                    }
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            keyboardController?.hide()
            focusRequester.freeFocus()
            val trimmedInput = inputText.trim().lowercase()
            
            when (trimmedInput) {
                "hello" -> {
                    onPhrasDiscovered("hello")
                    userName = Settings.Secure.getString(
                        context.contentResolver,
                        "bluetooth_name"
                    ) ?: Settings.Global.getString(
                        context.contentResolver,
                        "device_name"
                    ) ?: "User"
                    showDialog = true
                }
                "author" -> {
                    onPhrasDiscovered("author")
                    showAuthorDialog = true
                }
                "exit" -> {
                    onPhrasDiscovered("exit")
                    (context as? ComponentActivity)?.finish()
                }
            }
        }) {
            Text("Button")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { inputText = "" },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.padding(4.dp))
            Text("Clear")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("«" + AnnotatedString(inputText) + "»") },
            text = {
                Text(
                    buildAnnotatedString {
                        append("Hello, ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.Blue,
                                fontSize = 18.sp
                            )
                        ) {
                            append(userName)
                        }
                        append(" and Android developing world!")
                    }
                )
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Yo!")
                }
            }
        )
    }

    if (showAuthorDialog) {
        AlertDialog(
            onDismissRequest = { showAuthorDialog = false },
            title = { Text("«" + AnnotatedString(inputText) + "»") },
            text = { Text("Developer: SuperDragon777") },
            confirmButton = {
                Button(onClick = { showAuthorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}

fun getPhraseDescription(phrase: String): String {
    return when (phrase.lowercase()) {
        "hello" -> "Greeting command that displays a welcome message with your device name and Android greeting."
        "author" -> "Shows information about the developer of this application."
        "exit" -> "Closes the application and returns to the home screen."
        else -> "Unknown command"
    }
}

@Composable
fun SecondScreen(
    modifier: Modifier = Modifier,
    phrases: List<String> = emptyList(),
    onClearData: () -> Unit = {},
    onDeletePhrase: (String) -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedPhrase by remember { mutableStateOf<String?>(null) }
    var showPhraseDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Words", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu"
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Clear Data") },
                        onClick = {
                            showMenu = false
                            showConfirmDialog = true
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        if (phrases.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("No words discovered yet", fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(phrases) { phrase ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedPhrase = phrase
                                showPhraseDialog = true
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = phrase,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Clear Data") },
            text = { Text("Are you sure you want to delete all words?") },
            confirmButton = {
                Button(onClick = {
                    onClearData()
                    showConfirmDialog = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmDialog = false }) {
                    Text("No")
                }
            }
        )
    }

    if (showPhraseDialog && selectedPhrase != null) {
        AlertDialog(
            onDismissRequest = { showPhraseDialog = false },
            title = { Text("«${selectedPhrase}»") },
            text = {
                Column {
                    Text(
                        text = "Description:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = getPhraseDescription(selectedPhrase!!),
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPhraseDialog = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        onDeletePhrase(selectedPhrase!!)
                        showPhraseDialog = false
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EasterappTheme {
        MainScreen()
    }
}