package com.arthurnagy.staysafe.core.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arthurnagy.staysafe.core.model.Certificate
import kotlinx.coroutines.flow.Flow

@Dao
interface CertificateDao {
    @Query("SELECT * FROM certificate")
    fun get(): Flow<List<Certificate>>

    @Query("SELECT * FROM certificate WHERE id LIKE :id")
    suspend fun getById(id: Long): Certificate

    @Query("SELECT * FROM certificate ORDER BY from_date DESC LIMIT 1")
    suspend fun getLast(): Certificate

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(certificate: Certificate): Long

    @Delete
    suspend fun delete(certificate: Certificate)
}