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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(diary: DiaryEntity)

    @Update
    fun update(diary: DiaryEntity)

    @Delete
    fun delete(diary: DiaryEntity)
}
