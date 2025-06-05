package com.example.team.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface VocabDao {
    @Query("SELECT * FROM vocab ORDER BY createdDate DESC")
    fun getAllSortedByDate(): LiveData<List<VocabEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vocab: VocabEntity)

    @Update
    fun update(vocab: VocabEntity)

    @Delete
    fun delete(vocab: VocabEntity)
}