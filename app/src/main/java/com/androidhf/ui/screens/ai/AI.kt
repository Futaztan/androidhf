package com.androidhf.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
import com.androidhf.data.AiMessages
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.androidhf.ui.reuseable.UIVar
import dev.jeziellago.compose.markdowntext.MarkdownText
import com.androidhf.R
import kotlinx.coroutines.launch


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


import kotlin.text.isNotBlank

data class ChatMessage(
    val sender: String,
    val content: String,
    val timestamp: Long,
    var isVisible: Boolean = true
)

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@androidx.compose.runtime.Composable
fun AIScreen(viewModel: AIViewModel) {
    UIVar.topBarTitle = "AI"


    val messages = AiMessages.messages
    var inputText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    var showDeleteConfirmation by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    androidx.compose.runtime.LaunchedEffect(key1 = Unit) {
        viewModel.defaultPrompt()
    }

    if (showDeleteConfirmation) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { androidx.compose.material3.Text(androidx.compose.ui.res.stringResource(id = R.string.ai_delete)) },
            text = { androidx.compose.material3.Text(androidx.compose.ui.res.stringResource(id = R.string.ai_areyousuredelete)) },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        messages.clear()
                        viewModel.defaultPrompt()
                        showDeleteConfirmation = false
                    }
                ) {
                    androidx.compose.material3.Text(androidx.compose.ui.res.stringResource(id = R.string.general_yes))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    androidx.compose.material3.Text(androidx.compose.ui.res.stringResource(id = R.string.general_cancel))
                }
            }
        )
    }

    androidx.compose.material3.Scaffold(
        bottomBar = { // Használd a bottomBar slotot az input mezőhöz
            androidx.compose.foundation.layout.Column( // Column, hogy a gomb és az input mező egymás alatt legyen, vagy Row ha egymás mellett szeretnéd őket strukturálni
                modifier = androidx.compose.ui.Modifier
                    .fillMaxWidth() // Fontos, hogy kitöltse a szélességet
                    .background(UIVar.panelColor())
                    .padding(UIVar.Padding)
            ) {
                androidx.compose.foundation.layout.Row(
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween, // Elrendezi a gombokat
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Button(
                        onClick = {
                            viewModel.viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                                val message = ChatMessage(
                                    "user",
                                    viewModel.dataToAIPrompt(),
                                    System.currentTimeMillis()
                                )
                                message.isVisible = false
                                messages.add(message)
                                viewModel.sendMessage(inputText, messages, true)

                                val message2 = ChatMessage(
                                    "user",
                                    "30 napos rekord beküldve",
                                    System.currentTimeMillis()
                                )
                                messages.add(message2)
                            }
                        }
                    ) {
                        androidx.compose.material3.Text("30 napos report küldése")
                    }
                    androidx.compose.material3.IconButton(onClick = { showDeleteConfirmation = true }) {
                        androidx.compose.material3.Icon(Icons.Default.Delete, contentDescription = "Törlés")
                    }
                }
                androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.height(UIVar.Padding))
                androidx.compose.foundation.layout.Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    androidx.compose.material3.TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = androidx.compose.ui.Modifier.weight(1f),
                        placeholder = { androidx.compose.material3.Text(androidx.compose.ui.res.stringResource(id = R.string.ai_messageai)) },
                        colors = androidx.compose.material3.TextFieldDefaults.colors(
                            unfocusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                            focusedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
                        ),
                        enabled = !isLoading,
                    )
                    androidx.compose.foundation.layout.Spacer(modifier = androidx.compose.ui.Modifier.width(UIVar.Padding))
                    androidx.compose.material3.IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isLoading) {
                                val userMessage =
                                    ChatMessage("user", inputText, System.currentTimeMillis())
                                messages.add(userMessage)
                                viewModel.sendMessage(inputText, messages, true)
                                inputText = ""
                            }
                        },
                        enabled = !isLoading && inputText.isNotBlank()
                    ) {
                        if (isLoading) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = androidx.compose.ui.Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Küldés",
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding -> // Ez az innerPadding automatikusan figyelembe veszi a bottomBar magasságát

        if (messages.isEmpty()) {
            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Alkalmazd az innerPadding-et
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = androidx.compose.ui.res.stringResource(id = R.string.ai_startchat),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Alkalmazd az innerPadding-et itt is
                reverseLayout = true,
                // A contentPadding-et itt is használhatod további belső térközökhöz,
                // de a Scaffold innerPadding-je a legfontosabb az átfedések elkerülésére.
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ), // Vagy csak bottom = 16.dp, ha a többi már jó
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                items(messages.reversed()) { message ->
                    if (message.isVisible) {
                        MessageItem(message)
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage) {
    val isUserMessage = message.sender == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUserMessage) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isUserMessage)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.secondaryContainer,
            //modifier = Modifier.widthIn(max = 300.dp)
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                if (isUserMessage) {
                    Text(
                        text = message.content,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Column(modifier = Modifier){
                        Text(text = "AI", color = Color.Gray)
                        MarkdownText(
                            markdown = message.content,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isUserMessage)
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}