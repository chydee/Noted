package com.chydee.notekeeper.utils.worker

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chydee.notekeeper.data.DBHelperImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ClearTrashWorker @WorkerInject constructor(@Assisted ctx: Context, @Assisted params: WorkerParameters, val dbHelperImpl: DBHelperImpl) : Worker(ctx, params) {

    // Create an instance of DBHelperImpl and CompositeDisposable
    private val compositeDisposable = CompositeDisposable()

    override fun doWork(): Result {
        val appContext = applicationContext

        return try {
            // Perform the delete operation
            dbHelperImpl.clearTrash().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({}, {}).let {
                    compositeDisposable.add(it)
                }
            // Return if successful
            Result.success()
        } catch (throwable: Throwable) {
            // Throw exception if anything goes wrong
            Log.e("ClearTrashWorker", "$throwable")
            Timber.e(throwable)
            Result.failure()
        }
    }
}
