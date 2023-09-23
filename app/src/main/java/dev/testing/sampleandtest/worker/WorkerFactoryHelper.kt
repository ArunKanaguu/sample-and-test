package dev.testing.sampleandtest.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dev.testing.sampleandtest.api.ApiRepository
import dev.testing.sampleandtest.database.DatabaseRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerFactoryHelper @Inject constructor(private val api: ApiRepository,private val databaseRepository: DatabaseRepository) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val workerKlass =
            try {
                Class.forName(workerClassName).asSubclass(MyWorker1::class.java)
            } catch (ex: Exception) {
                Class.forName(workerClassName).asSubclass(MyWorker2::class.java)
            }

        val constructor =
            workerKlass.getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)

        val instance = constructor.newInstance(appContext, workerParameters)

        when (instance) {
            is MyWorker1 -> {
                instance.api = api
            }
            is MyWorker2 -> {
                instance.api = api
            }
        }

        return instance
    }
}