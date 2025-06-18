package app.merp.kmp.talk.chat.app

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatClient: ChatClient) {
    val messages by chatClient.message.collectAsState()
    var input by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val localFocusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chat App") })
        },
    ) { padding ->
        Surface(modifier = Modifier.padding(padding)) {
            Column {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                ) {
                    items(messages) { message ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(2.dp)
                        ) {
                            Text(
                                text = message,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Enter message") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                localFocusManager.clearFocus()
                                keyboard?.hide()
                                return@KeyboardActions
                            }
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Button(onClick = {
                        if (input.isNotBlank()) {
                            scope.launch {
                                chatClient.sendMessage(input)
                                input = ""
                            }
                        }
                    }) {
                        Text("Send")
                    }
                }
            }
        }
    }
}