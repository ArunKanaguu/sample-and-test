package dev.testing.sampleandtest.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.testing.sampleandtest.api.ApiRepository
import dev.testing.sampleandtest.database.DatabaseRepository
import dev.testing.sampleandtest.database.Dog
import dev.testing.sampleandtest.database.Owner
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val apiRepository: ApiRepository,
) : ViewModel() {


    val ttsState = MutableLiveData<String>()

    //api

    fun getCoinsDetails() =apiRepository.gatCoinDetails()

    //database

    val dogDetails = databaseRepository.dogDetails
    val ownerDetails = databaseRepository.ownerDetails
    val dogsAndOwnerList = databaseRepository.dogsAndOwnerList

    fun insertDog(dog: Dog) {
        viewModelScope.launch {
            databaseRepository.insertDog(dog)
        }
    }

    fun insertOwner(owner: Owner) {
        viewModelScope.launch {
            databaseRepository.insertOwner(owner)
        }
    }

}