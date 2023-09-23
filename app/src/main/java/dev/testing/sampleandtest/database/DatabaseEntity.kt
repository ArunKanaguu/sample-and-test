package dev.testing.sampleandtest.database

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Keep
@Entity(tableName = "dog")
data class Dog(
    @PrimaryKey
    val dogId: Long,
    val dogOwnerId: Long,
    val name: String,
    /*val cuteness: Int,
    val barkVolume: Int,
    val breed: String*/
)

@Keep
@Entity(tableName = "owner",
    indices = [Index(value = ["ownerId"], unique = true)]
)
data class Owner(@PrimaryKey val ownerId: Long, val name: String)