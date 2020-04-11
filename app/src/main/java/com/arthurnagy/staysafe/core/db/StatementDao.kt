package com.arthurnagy.staysafe.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arthurnagy.staysafe.core.model.Statement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn

@Dao
abstract class StatementDao {
    @Query("SELECT * FROM statement ORDER BY date DESC")
    protected abstract fun get(): Flow<List<Statement>>

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getDistinct(): Flow<List<Statement>> = get().flowOn(Dispatchers.IO).distinctUntilChanged()

    @Query("SELECT * FROM statement WHERE id LIKE :id")
    abstract suspend fun getById(id: Long): Statement

    @Query("SELECT * FROM statement ORDER BY created_at DESC LIMIT 1")
    abstract suspend fun getLast(): Statement

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insert(statement: Statement): Long

    @Delete
    abstract suspend fun delete(statement: Statement)
}