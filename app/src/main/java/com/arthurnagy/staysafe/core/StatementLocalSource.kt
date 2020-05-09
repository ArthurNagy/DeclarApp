package com.arthurnagy.staysafe.core

import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Statement
import kotlinx.coroutines.flow.Flow

class StatementLocalSource(private val statementDao: StatementDao) {
    fun getStatementsFlow(): Flow<List<Statement>> = statementDao.getDistinct()

    suspend fun getById(statementId: Long): Result<Statement> = Result {
        statementDao.getById(statementId)
    }

    suspend fun getLast(): Result<Statement> = Result {
        statementDao.getLast()
    }

    suspend fun save(statement: Statement): Result<Long> = Result {
        statementDao.insert(statement)
    }

    suspend fun update(statement: Statement): Result<Unit> = Result {
        statementDao.update(statement)
    }

    suspend fun delete(statement: Statement): Result<Unit> = Result {
        statementDao.delete(statement)
    }
}