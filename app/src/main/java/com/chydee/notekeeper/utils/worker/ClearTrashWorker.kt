package com.chydee.notekeeper.utils.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chydee.notekeeper.data.DBHelperImpl
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

@HiltWorker
class ClearTrashWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    val dbHelperImpl: DBHelperImpl
) : Worker(appContext, params) {
    // create an instance of DBHelperImpl and CompositeDisposable
    private val compositeDisposable = CompositeDisposable()

    override fun doWork(): Result {
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
            Timber.e(throwable)
            Result.failure()
        }
    }
}