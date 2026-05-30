package com.example.quicknotes.data.repository

import com.example.quicknotes.data.mapper.toDomain
import com.example.quicknotes.data.mapper.toEntity
import com.example.quicknotes.data.study.StudyDao
import com.example.quicknotes.domain.model.StudyHistory
import com.example.quicknotes.domain.repository.StudyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StudyRepositoryImpl(
    private val dao: StudyDao
) : StudyRepository {

    override fun getAllHistory(): Flow<List<StudyHistory>> {
        return dao.getAllHistory().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertHistory(item: StudyHistory) {
        dao.insertHistory(item.toEntity())
    }

    override suspend fun getById(id: Int): StudyHistory? {
        return dao.getById(id)?.toDomain()
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override suspend fun deleteById(id: Int) {
        dao.deleteById(id)
    }
}