package app.merp.kmp.talk.chat.app

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.util.logging.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatClient {

    private val client = HttpClient() {
        install(WebSockets)
    }

    private val _message = MutableStateFlow<List<String>>(emptyList())
    val message: StateFlow<List<String>> = _message


    private var session: DefaultClientWebSocketSession? = null

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
                        is Frame.Pong -> TODO()*//*
                            else -> TODO()
                        }
                    }
                } catch (e: Exception) {
                    _message.emit(_message.value + " Connection closed: ${e.message}")
                }
            }
        }
    }*/

    suspend fun connect(url: String) {
        client.webSocket(urlString = url) {
            session = this
            try {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val updated = _message.value + frame.readText()
                        _message.emit(updated)
                    }
                }
            } catch (e: Exception) {
                _message.emit(_message.value + "Connection closed: ${e.message}")
                println("TALK -> Connection error -> ${e.message}")
            }
        }
    }

    suspend fun sendMessage(text: String) {
        try {
            println("TALK -> Sending messages!")
            session?.send(Frame.Text(text))
        } catch (e: Exception) {
            //_message.emit(_message.value + "Connection closed: ${e.message}")
            println("TALK -> ERROR at -> sendMessage: ${e.message}")
        }
    }

    fun close() {
        client.close()
    }
}