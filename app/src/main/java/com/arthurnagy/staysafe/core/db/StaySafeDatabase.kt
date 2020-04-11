package com.arthurnagy.staysafe.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arthurnagy.staysafe.core.model.MotiveConverter
import com.arthurnagy.staysafe.core.model.MotivesConverter
import com.arthurnagy.staysafe.core.model.Statement

@Database(entities = [Statement::class], version = 4, exportSchema = true)
@TypeConverters(MotivesConverter::class, MotiveConverter::class)
abstract class StaySafeDatabase : RoomDatabase() {
    abstract fun statementDao(): StatementDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(
                    """
                    CREATE TABLE statement_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        first_name TEXT NOT NULL, 
                        last_name TEXT NOT NULL, 
                        birth_date INTEGER NOT NULL, 
                        address TEXT NOT NULL,
                        route TEXT NOT NULL, 
                        motives TEXT NOT NULL, 
                        date INTEGER NOT NULL, 
                        signature TEXT NOT NULL, 
                        created_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                // Copy the data
                database.execSQL(
                    """
                    INSERT INTO statement_new (id, first_name, last_name, birth_date, address, route, motives, date, signature, created_at)
                    SELECT id, first_name, last_name, birth_date, address, route, motive, date, signature, created_at
                    FROM statement
                    """.trimIndent()
                )
                // Remove the old table
                database.execSQL("DROP TABLE statement")
                // Change the table name to the correct one
                database.execSQL("ALTER TABLE statement_new RENAME TO statement")
            }
        }
    }
}