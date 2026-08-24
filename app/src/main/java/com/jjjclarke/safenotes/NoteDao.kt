package com.jjjclarke.safenotes

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Delete
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("select * from notes order by id desc")
    fun getAllNotes(): Flow<List<NoteEntity>>

    /*
        This is good for both adding new notes, but also
        editing existing notes. No need to separate them
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("select * from notes where id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("delete from notes where id = :id")
    suspend fun deleteById(id: Long)
}