package com.jjjclarke.safenotes

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity (
    @PrimaryKey
    val id: Long = System.currentTimeMillis(), // Creation timestamp

    val title: String,
    val content: ByteArray,
    val iv: ByteArray // GCM nonce to encrypt contents
) {
    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (other !is NoteEntity)
            return false

        return id == other.id &&
                title == other.title &&
                content.contentEquals(other.content) &&
                iv.contentEquals(other.iv)
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        return result
    }
}