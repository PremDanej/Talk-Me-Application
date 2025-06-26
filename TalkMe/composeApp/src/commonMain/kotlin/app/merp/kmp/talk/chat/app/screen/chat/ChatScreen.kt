package app.merp.kmp.talk.chat.app.screen.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import app.merp.kmp.talk.chat.app.ChatClient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(chatClient: ChatClient) {

    val messages by chatClient.message.collectAsState()
    var input by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val localFocusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    val isVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val showNewMessageChip = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Chat App") })
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val (_, scrollToBottom) = ChatListWithAutoScroll(
                    messages = messages,
                    modifier = Modifier.weight(1f)
                ) { message ->
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
                            coroutineScope.launch {
                                chatClient.sendMessage(input)
                                input = ""
                                scrollToBottom()
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