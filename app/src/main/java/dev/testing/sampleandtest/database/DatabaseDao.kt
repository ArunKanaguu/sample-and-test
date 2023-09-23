package dev.testing.sampleandtest.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface DatabaseDao {

    @Insert
    suspend fun insertDog(dog: Dog)

    @Delete
    suspend fun deleteDog(dog: Dog)

    @Query("SELECT * FROM dog")
    fun getAllDogs(): LiveData<List<Dog>>

    @Update
    suspend fun updateDog(dog: Dog)

    @Insert
    suspend fun insertOwner(owner: Owner)

    @Delete
    suspend fun deleteOwner(owner: Owner)

    @Query("SELECT * FROM owner")
    fun getAllOwners(): LiveData<List<Owner>>

    @Update
    suspend fun updateOwner(owner: Owner)

    @Transaction
    @Query("SELECT * FROM Owner")
    fun getDogsAndOwnerList(): List<DogAndOwner>
    /*
        @Transaction
        @Query("SELECT * FROM Owner")
        fun getDogsAndOwnerList(): LiveData<List<DogAndOwner>>*/
}