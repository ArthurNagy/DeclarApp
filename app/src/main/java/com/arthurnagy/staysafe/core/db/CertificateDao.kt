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
    suspend fun getById(id: Int): Certificate

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(certificate: Certificate)

    @Delete
    suspend fun delete(certificate: Certificate)
}