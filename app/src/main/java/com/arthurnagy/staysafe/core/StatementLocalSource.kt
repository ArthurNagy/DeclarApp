package com.arthurnagy.staysafe.core

import com.arthurnagy.staysafe.core.db.StatementDao
import com.arthurnagy.staysafe.core.model.Statement
import kotlinx.coroutines.flow.Flow

class StatementLocalSource(private val statementDao: StatementDao) {
    fun getStatementsFlow(): Flow<List<Statement>> = statementDao.getDistinct()

    suspend fun getById(statementId: Long): ResultWrapper<Statement> = ResultWrapper {
        statementDao.getById(statementId)
    }

    suspend fun getLast(): ResultWrapper<Statement> = ResultWrapper {
        statementDao.getLast()
    }

    suspend fun save(statement: Statement): ResultWrapper<Long> = ResultWrapper {
        statementDao.insert(statement)
    }

    suspend fun update(statement: Statement): ResultWrapper<Unit> = ResultWrapper {
        statementDao.update(statement)
    }

    suspend fun delete(statement: Statement): ResultWrapper<Unit> = ResultWrapper {
        statementDao.delete(statement)
    }
}