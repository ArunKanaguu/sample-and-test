package dev.testing.sampleandtest.database

import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    private val databaseDao: DatabaseDao
) {

    val dogDetails = databaseDao.getAllDogs()
    val ownerDetails = databaseDao.getAllOwners()
    val dogsAndOwnerList = databaseDao.getDogsAndOwnerList()


    suspend fun insertDog(dog: Dog) {
        databaseDao.insertDog(dog)
    }

    suspend fun deleteDog(dog: Dog) {
        databaseDao.deleteDog(dog)
    }

    suspend fun updateDog(dog: Dog) {
        databaseDao.updateDog(dog)
    }

    suspend fun insertOwner(owner: Owner) {
        databaseDao.insertOwner(owner)
    }

    suspend fun deleteOwner(owner: Owner)  {
        databaseDao.deleteOwner(owner)
    }

    suspend fun updateOwner(owner: Owner)  {
        databaseDao.updateOwner(owner)
    }

}