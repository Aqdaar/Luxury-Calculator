package com.example.data.repository

import com.example.data.db.CalculationDao
import com.example.data.db.CalculationEntity
import kotlinx.coroutines.flow.Flow

class CalculationRepository(private val dao: CalculationDao) {
    val allCalculations: Flow<List<CalculationEntity>> = dao.getAllCalculations()

    fun search(query: String): Flow<List<CalculationEntity>> = dao.searchCalculations(query)

    suspend fun save(calculation: CalculationEntity): Long = dao.insert(calculation)

    suspend fun update(calculation: CalculationEntity) = dao.update(calculation)

    suspend fun delete(id: Long) = dao.deleteById(id)

    suspend fun deleteMultiple(ids: Collection<Long>) = dao.deleteByIds(ids.toList())

    suspend fun clearAll() = dao.clearAll()
}
