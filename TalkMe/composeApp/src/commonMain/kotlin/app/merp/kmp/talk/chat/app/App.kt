package app.merp.kmp.talk.chat.app

import androidx.compose.runtime.Composable

@Composable
fun App(chatClient: ChatClient) {
    ChatScreen(chatClient = chatClient)
}