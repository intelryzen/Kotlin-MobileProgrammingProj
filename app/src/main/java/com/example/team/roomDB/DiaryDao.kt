package com.example.team.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DiaryDao {
    @Query("SELECT * FROM diary ORDER BY createdDate DESC")
    fun getAll(): LiveData<List<DiaryEntity>>
    
    @Query("SELECT * FROM diary ORDER BY createdDate DESC")
    suspend fun getAllSync(): List<DiaryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diary: DiaryEntity)

    @Update
    suspend fun update(diary: DiaryEntity)

    @Delete
    suspend fun delete(diary: DiaryEntity)
}
