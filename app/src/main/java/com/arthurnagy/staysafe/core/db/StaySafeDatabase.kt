package com.arthurnagy.staysafe.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.arthurnagy.staysafe.core.model.Certificate
import com.arthurnagy.staysafe.core.model.MotiveConverter
import com.arthurnagy.staysafe.core.model.Statement

@Database(entities = [Certificate::class, Statement::class], version = 1, exportSchema = false)
@TypeConverters(MotiveConverter::class)
abstract class StaySafeDatabase : RoomDatabase() {
    abstract fun certificateDao(): CertificateDao
    abstract fun statementDao(): StatementDao
}