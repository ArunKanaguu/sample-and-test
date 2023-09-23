package dev.testing.sampleandtest.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.testing.sampleandtest.api.ApiRepository
import dev.testing.sampleandtest.api.CoinDesk
import dev.testing.sampleandtest.database.DatabaseRepository
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

@HiltWorker
class MyWorker1 @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: DatabaseRepository,
) : Worker(context, params) {

    @Inject
    lateinit var api: ApiRepository

    override fun doWork(): Result {
        Log.i(TAG, "doWork: ")
        apiTest(api)
        return Result.success()
    }
    private fun apiTest(api: ApiRepository) {
        api.gatCoinDetails().enqueue(object : retrofit2.Callback<CoinDesk> {
            override fun onResponse(call: Call<CoinDesk>, response: Response<CoinDesk>) {
                Log.d(TAG, "retrofit onResponse: ${response.body()}",)
            }

            override fun onFailure(call: Call<CoinDesk>, t: Throwable) {
                Log.d(TAG, "retrofit onFailure: ${t.message}")
            }
        })
    }
    companion object {
        const val TAG = "MyWorker"
    }
}