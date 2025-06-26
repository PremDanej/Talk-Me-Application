package app.merp.kmp.talk.chat.app

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ChatClient {

    private val client = HttpClient {
        install(WebSockets)
    }

    private val _message = MutableStateFlow<List<String>>(emptyList())
    val message: StateFlow<List<String>> = _message


    private var session: WebSocketSession? = null
    private var receiveJob: Job? = null

    /*suspend fun connect(url: String = "ws://10.0.2.2:8000/ws/chat") {
        client.webSocket(urlString = url) {
            launch {
                try {
                    for (message in incoming) {
                        when (message) {
                            is Frame.Text -> {
                                val current = _message.value.toMutableList()
                                current.add(message.readText())
                                _message.emit(current)
                            }
                            *//*is Frame.Binary -> TODO()
                        is Frame.Close -> TODO()
                        is Frame.Ping -> TODO()
                        is Frame.Pong -> TODO()
                            else -> TODO()
                        }
                    }
                } catch (e: Exception) {
                    _message.emit(_message.value + " Connection closed: ${e.message}")
                }
            }
        }
    }*/

    suspend fun connection(url: String, username: String) {
        try {
            session = client.webSocketSession {
                url(url)
            }

            session?.let { wsSession ->
                // 1. Send username as initial message
                wsSession.send(Frame.Text(username))

                // 2. Start listening in coroutine
                receiveJob = CoroutineScope(Dispatchers.IO).launch {
                    try {
                        for (frame in wsSession.incoming) {
                            if (frame is Frame.Text) {
                                val newMessage = frame.readText()
                                _message.update { it + newMessage }
                            }
                        }
                    } catch (e: Exception) {
                        _message.update { it + "❌ Error receiving: ${e.message}" }
                        println("TALK -> Error receiving -> ${e.message}")
                    }
                }
            }
        } catch (e: Exception) {
            _message.update { it + "❌ Failed to connect: ${e.message}" }
            println("TALK -> Failed to connect -> ${e.message}")
        }
    }

    suspend fun sendMessage(text: String) {
        try {
            session?.send(Frame.Text(text))
        } catch (e: Exception) {
            _message.emit(_message.value + "Connection issue while sending message: ${e.message}")
            println("TALK -> ERROR at -> sendMessage: ${e.message}")
        }
    }

    fun close() {
        receiveJob?.cancel()
        client.close()
    }
}