package com.example.submission2.data.source.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submission2.data.source.local.entity.StoryEntity

@Dao
interface StoryWithLocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: List<StoryEntity>)

    @Query("SELECT * FROM story")
    fun getAllQuote(): PagingSource<Int, StoryEntity>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}