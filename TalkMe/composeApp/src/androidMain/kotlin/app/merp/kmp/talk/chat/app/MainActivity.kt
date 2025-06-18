package app.merp.kmp.talk.chat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import app.merp.kmp.talk.chat.app.ui.theme.TalkAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val chatClient = ChatClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            chatClient.connect("ws://192.168.196.89:8000/ws/chat")
        }

        enableEdgeToEdge()
        setContent {
            TalkAppTheme {
                App(chatClient = chatClient)
            }
        }
    }

    override fun onDestroy() {
        chatClient.close()
        super.onDestroy()
    }
}