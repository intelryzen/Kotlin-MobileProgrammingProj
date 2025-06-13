package com.example.team.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(chat: ChatEntity)

    @Update
    fun update(chat: ChatEntity)

    @Delete
    fun delete(chat: ChatEntity)
}