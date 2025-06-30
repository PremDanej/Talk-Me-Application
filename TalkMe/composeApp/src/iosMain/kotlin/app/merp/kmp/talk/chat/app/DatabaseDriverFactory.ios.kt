package app.merp.kmp.talk.chat.app

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        //return NativeSqSqliteDriver(ChatDatabase.Schema, "chat.db")
        TODO()
    }
}