package com.chydee.notekeeper.data.dao

import androidx.room.*
import com.chydee.notekeeper.data.model.Trash
import io.reactivex.Completable
import io.reactivex.Single


//Defines the method for using the Note class with room
@Dao
interface TrashDao {

    /*
    * This method inserts the deleted note into the trash table
    */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(trash: Trash): Completable

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(trash: List<Trash>): Completable


    @Delete
    fun delete(trash: Trash): Completable

    /**
     * Deletes all values from the table.
     *
     * This does not delete the table, only its contents.
     */
    @Query("DELETE FROM trash")
    fun clearTrash(): Completable


    @Query("SELECT * FROM trash ORDER BY id DESC")
    fun getAllTrash(): Single<List<Trash>>
}