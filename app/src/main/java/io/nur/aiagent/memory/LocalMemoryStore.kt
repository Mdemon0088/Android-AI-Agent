package io.nur.aiagent.memory

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import io.nur.aiagent.agent.TaskPlan

/**
 * Local, opt-in memory. Records are stored only in the app's private SQLite database.
 * Saving is never automatic: the UI calls savePlan only after the user taps Save.
 */
class LocalMemoryStore(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_SAVED_PLANS (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                request TEXT NOT NULL,
                summary TEXT NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // There are no older persistent schemas yet. Add explicit migrations
        // here before increasing DATABASE_VERSION in a future release.
    }

    fun savePlan(plan: TaskPlan, createdAtMillis: Long = System.currentTimeMillis()): Long {
        val values = ContentValues().apply {
            put(COLUMN_REQUEST, plan.request)
            put(COLUMN_SUMMARY, plan.summary)
            put(COLUMN_CREATED_AT, createdAtMillis)
        }
        return writableDatabase.insertOrThrow(TABLE_SAVED_PLANS, null, values)
    }

    fun getSavedPlans(limit: Int = DEFAULT_LIMIT): List<SavedPlan> {
        val safeLimit = limit.coerceIn(1, MAX_LIMIT)
        val result = mutableListOf<SavedPlan>()
        readableDatabase.rawQuery(
            "SELECT id, request, summary, created_at FROM $TABLE_SAVED_PLANS ORDER BY id DESC LIMIT ?",
            arrayOf(safeLimit.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += SavedPlan(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    request = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_REQUEST)),
                    summary = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SUMMARY)),
                    createdAtMillis = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                )
            }
        }
        return result
    }

    fun deleteSavedPlan(id: Long): Boolean {
        return writableDatabase.delete(
            TABLE_SAVED_PLANS,
            "$COLUMN_ID = ?",
            arrayOf(id.toString())
        ) > 0
    }

    companion object {
        private const val DATABASE_NAME = "nur_ai_memory.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_SAVED_PLANS = "saved_plans"
        private const val COLUMN_ID = "id"
        private const val COLUMN_REQUEST = "request"
        private const val COLUMN_SUMMARY = "summary"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val DEFAULT_LIMIT = 20
        private const val MAX_LIMIT = 100
    }
}

data class SavedPlan(
    val id: Long,
    val request: String,
    val summary: String,
    val createdAtMillis: Long
)
