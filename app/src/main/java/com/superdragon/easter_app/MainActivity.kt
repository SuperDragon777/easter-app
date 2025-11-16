package com.superdragon.easter_app

import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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
            0 -> Greeting(modifier = Modifier.padding(innerPadding))
            1 -> SecondScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
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
            when (inputText.trim().lowercase()) {
                "hello" -> {
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
                    showAuthorDialog = true
                }
                "exit" -> {
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

@Composable
fun SecondScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Words", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text("This is the words tab")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EasterappTheme {
        MainScreen()
    }
}