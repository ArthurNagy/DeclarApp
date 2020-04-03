package com.arthurnagy.staysafe.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arthurnagy.staysafe.core.model.MotiveConverter
import com.arthurnagy.staysafe.core.model.Statement

@Database(entities = [Statement::class], version = 3, exportSchema = true)
@TypeConverters(MotiveConverter::class)
abstract class StaySafeDatabase : RoomDatabase() {
    abstract fun statementDao(): StatementDao
}