package com.arthurnagy.staysafe.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arthurnagy.staysafe.core.model.Statement
import kotlinx.coroutines.flow.Flow

@Dao
interface StatementDao {
    @Query("SELECT * FROM statement")
    fun get(): Flow<List<Statement>>

    @Query("SELECT * FROM statement WHERE id LIKE :id")
    suspend fun getById(id: Int): Statement

    @Query("SELECT * FROM statement ORDER BY date DESC LIMIT 1")
    suspend fun getLast(): Statement

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(statement: Statement): Long

    @Delete
    suspend fun delete(statement: Statement)
}