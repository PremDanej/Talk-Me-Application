package app.merp.kmp.talk.chat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import app.merp.kmp.talk.chat.app.ui.theme.TalkAppTheme
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.net.SocketException
import java.net.SocketTimeoutException

const val PORT = BuildConfig.PORT
const val HOST = BuildConfig.HOST

class MainActivity : ComponentActivity() {

    private val chatClient = ChatClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            var username by remember { mutableStateOf("") }
            var connected by remember { mutableStateOf(false) }

            TalkAppTheme {
                if (!connected) {
                    UsernameInput { gettingUsername ->
                        username = gettingUsername
                        lifecycleScope.launch {
                            try {
                                chatClient.connection(
                                    url = "ws://$HOST:$PORT/ws/chat/$username",
                                    username = username
                                )
                                connected = true
                            } catch (e: SocketException) {
                                println("TALK -> SocketException || Failed to connect -> ${e.message}")
                            } catch (e: SocketTimeoutException) {
                                println("TALK -> SocketTimeOutError || Failed to connect -> ${e.message}")
                            } catch (e: Exception) {
                                println("TALK -> Connection error -> ${e.message}")
                            }
                        }
                    }
                } else {
                    App(chatClient = chatClient)
                }
            }
        }
    }

    override fun onDestroy() {
        chatClient.close()
        super.onDestroy()
    }
}