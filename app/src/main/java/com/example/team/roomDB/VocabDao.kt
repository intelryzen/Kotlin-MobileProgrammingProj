package com.example.team.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocab ORDER BY id DESC")
    suspend fun getAll(): List<VocabEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM vocab WHERE word = :word LIMIT 1)")
    suspend fun isWordExists(word: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vocab: VocabEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vocabs: List<VocabEntity>): List<Long>

    @Update
    fun update(vocab: VocabEntity)

    @Delete
    fun delete(vocab: VocabEntity)
}