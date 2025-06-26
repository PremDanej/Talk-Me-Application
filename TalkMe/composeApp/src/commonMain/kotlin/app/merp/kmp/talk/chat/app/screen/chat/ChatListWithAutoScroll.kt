package app.merp.kmp.talk.chat.app.screen.chat

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChatListWithAutoScroll(
    messages: List<String>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    messageContent: @Composable (String) -> Unit
): Pair<LazyListState, () -> Unit> {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val showNewMessageChip = remember { mutableStateOf(false) }

    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    // 👇 Auto-scroll when keyboard opens (only scroll, don't touch chip)
    LaunchedEffect(imeVisible) {
        if (imeVisible && messages.isNotEmpty()) {
            delay(200)
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    // 👇 Auto-scroll on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItems = messages.lastIndex
            val isUserNearBottom = lastVisibleIndex >= totalItems - 2

            if (isUserNearBottom) {
                delay(200)
                listState.animateScrollToItem(totalItems)
                showNewMessageChip.value = false
            } else {
                showNewMessageChip.value = true
            }
        }
    }

    // 👇 Hide chip when user scrolls manually to bottom
    LaunchedEffect(Unit) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (index >= messages.lastIndex - 2) {
                    showNewMessageChip.value = false
                }
            }
    }

    // Main content
    Box(modifier = modifier) {
        LazyColumn(
            state = listState,
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(messages) { message ->
                messageContent(message)
            }
        }

        AnimatedVisibility(
            visible = showNewMessageChip.value,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 }),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 72.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(messages.lastIndex)
                        showNewMessageChip.value = false
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3E5FC))
            ) {
                Text("⬇️ New message")
            }
        }
    }

    // Return list state & a function to force scroll
    return Pair(listState) {
        coroutineScope.launch {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
}
