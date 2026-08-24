package com.jjjclarke.safenotes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class DecryptedNote(
    val id: Long,
    val title: String,
    val content: String
)

class NoteRepository(
    private val dao: NoteDao,
    private val ksm: KeystoreManager
) {
    fun getAllNotes(): Flow<List<DecryptedNote>> {
        return dao.getAllNotes().map { noteEntities ->
            noteEntities.map { noteEntity ->
                DecryptedNote(
                    id = noteEntity.id,
                    title = noteEntity.title,
                    content = ksm.decryptNote(
                        EncryptedPayload(noteEntity.content, noteEntity.iv)
                    )
                )
            }
        }
    }

    suspend fun saveNote(id: Long, title: String, content: String) {
        val payload = ksm.encryptNote(content)
        dao.upsert(
            NoteEntity(
                id = id,
                title = title,
                content = payload.encryptedContent,
                iv = payload.iv
            )
        )
    }

    suspend fun deleteNote(id: Long) {
        dao.deleteById(id)
    }
}