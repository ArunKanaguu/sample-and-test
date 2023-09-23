package dev.testing.sampleandtest.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.testing.sampleandtest.api.ApiRepository
import dev.testing.sampleandtest.database.DatabaseRepository
import javax.inject.Inject

@HiltWorker
class MyWorker2 @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: DatabaseRepository,
) : Worker(context, params) {

    @Inject
    lateinit var api: ApiRepository

    override fun doWork(): Result {
        Log.i(TAG, "doWork: ${repository.dogsAndOwnerList}")
        return Result.success()
    }

    companion object {
        const val TAG = "MyWorker"
    }
}