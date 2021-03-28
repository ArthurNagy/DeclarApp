package com.arthurnagy.staysafe.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.arthurnagy.staysafe.core.model.MotiveConverter
import com.arthurnagy.staysafe.core.model.MotivesConverter
import com.arthurnagy.staysafe.core.model.Statement

@Database(entities = [Statement::class], version = 6, exportSchema = true)
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

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(
                    """
                    CREATE TABLE statement_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        first_name TEXT NOT NULL, 
                        last_name TEXT NOT NULL, 
                        birth_date INTEGER NOT NULL, 
                        location TEXT NOT NULL DEFAULT '',
                        current_location TEXT NOT NULL DEFAULT '',
                        birthday_location TEXT NOT NULL DEFAULT '', 
                        work_location TEXT DEFAULT '', 
                        work_addresses TEXT DEFAULT '', 
                        motives TEXT NOT NULL DEFAULT '', 
                        date INTEGER NOT NULL, 
                        signature TEXT NOT NULL, 
                        created_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                // Copy the data
                database.execSQL(
                    """
                    INSERT INTO statement_new (id, first_name, last_name, birth_date, date, signature, created_at)
                    SELECT id, first_name, last_name, birth_date, date, signature, created_at
                    FROM statement
                    """.trimIndent()
                )
                // Remove the old table
                database.execSQL("DROP TABLE statement")
                // Change the table name to the correct one
                database.execSQL("ALTER TABLE statement_new RENAME TO statement")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(
                    """
                    CREATE TABLE statement_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        first_name TEXT NOT NULL, 
                        last_name TEXT NOT NULL, 
                        birth_date INTEGER NOT NULL, 
                        location TEXT NOT NULL,
                        current_location TEXT NOT NULL,
                        birthday_location TEXT NOT NULL, 
                        work_location TEXT, 
                        work_addresses TEXT, 
                        motives TEXT NOT NULL, 
                        date INTEGER NOT NULL, 
                        signature TEXT NOT NULL, 
                        restriction_start_hour INTEGER NOT NULL DEFAULT 22,
                        created_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                // Copy the data
                database.execSQL(
                    """
                    INSERT INTO statement_new (id, first_name, last_name, birth_date, location, current_location, birthday_location, work_location, work_addresses, motives, date, signature, created_at)
                    SELECT id, first_name, last_name, birth_date, location, current_location, birthday_location, work_location, work_addresses, motives, date, signature, created_at
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