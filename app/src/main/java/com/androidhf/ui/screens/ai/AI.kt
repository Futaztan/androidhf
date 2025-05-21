package com.androidhf.ui.screens.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.*

import java.text.SimpleDateFormat
import java.util.*
import com.androidhf.data.AiMessages

import androidx.lifecycle.viewModelScope
import com.androidhf.ui.reuseable.UIVar
import dev.jeziellago.compose.markdowntext.MarkdownText
import com.androidhf.R
import kotlinx.coroutines.launch

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


import kotlin.text.isNotBlank

data class ChatMessage(
    val sender: String,
    val content: String,
    val timestamp: Long,
    var isVisible: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIScreen(viewModel: AIViewModel) {
    UIVar.topBarTitle = "AI"


    val messages = AiMessages.messages
    var inputText by androidx.compose.runtime.remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    var showDeleteConfirmation by androidx.compose.runtime.remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.defaultPrompt()
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text(stringResource(id = R.string.ai_delete)) },
            text = { Text(stringResource(id = R.string.ai_areyousuredelete)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        messages.clear()
                        viewModel.defaultPrompt()
                        showDeleteConfirmation = false
                    }
                ) {
                    Text(stringResource(id = R.string.general_yes))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text(stringResource(id = R.string.general_cancel))
                }
            }
        )
    }

    Scaffold(
        bottomBar = { // Használd a bottomBar slotot az input mezőhöz
            Column( // Column, hogy a gomb és az input mező egymás alatt legyen, vagy Row ha egymás mellett szeretnéd őket strukturálni
                modifier = Modifier
                    .fillMaxWidth() // Fontos, hogy kitöltse a szélességet
                    .background(UIVar.panelColor())
                    .padding(UIVar.Padding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween, // Elrendezi a gombokat
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
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
                        Text("30 napos report küldése")
                    }
                    IconButton(onClick = { showDeleteConfirmation = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Törlés")
                    }
                }
                Spacer(modifier = Modifier.height(UIVar.Padding))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(id = R.string.ai_messageai)) },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        enabled = !isLoading,
                    )
                    Spacer(modifier = Modifier.width(UIVar.Padding))
                    IconButton(
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
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Küldés",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding -> // Ez az innerPadding automatikusan figyelembe veszi a bottomBar magasságát

        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Alkalmazd az innerPadding-et
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.ai_startchat),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Alkalmazd az innerPadding-et itt is
                reverseLayout = true,
                // A contentPadding-et itt is használhatod további belső térközökhöz,
                // de a Scaffold innerPadding-je a legfontosabb az átfedések elkerülésére.
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 16.dp
                ), // Vagy csak bottom = 16.dp, ha a többi már jó
                verticalArrangement = Arrangement.spacedBy(8.dp)
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