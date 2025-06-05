package com.example.team.roomDB

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat ORDER BY createdDate ASC")
    fun getAll(): LiveData<List<ChatEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chat: ChatEntity)

    @Update
    fun update(chat: ChatEntity)

    @Delete
    fun delete(chat: ChatEntity)
}