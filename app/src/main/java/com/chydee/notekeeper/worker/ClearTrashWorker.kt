package com.chydee.notekeeper.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chydee.notekeeper.data.DBHelperImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ClearTrashWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {


    //Create an instance of DBHelperImpl and CompositeDisposable
    private lateinit var dbHelperImpl: DBHelperImpl
    private val compositeDisposable = CompositeDisposable()

    override fun doWork(): Result {
        val appContext = applicationContext
        dbHelperImpl = DBHelperImpl(appContext)
        return try {
            //Perform the delete operation
            dbHelperImpl.clearTrash().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({}, {}).let {
                        compositeDisposable.add(it)
                    }
            //Return if successful
            Result.success()
        } catch (throwable: Throwable) {
            //Throw exception if anything goes wrong
            Timber.e(throwable)
            Result.failure()
        }
    }
}