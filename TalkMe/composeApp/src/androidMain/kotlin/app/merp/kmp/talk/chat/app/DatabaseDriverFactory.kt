package app.merp.kmp.talk.chat.app

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.merp.kmp.talk.chat.app.db.ChatDatabase

actual class DatabaseDriverFactory(private val context : Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(ChatDatabase.Schema, context, "chat.db")
    }
}
