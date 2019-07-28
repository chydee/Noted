# Note-Keeper App
 A Note taking app, using the Android Architecture Component libraries (Room, ViewModel, LiveData and LifeCycle), a RecyclerView, Java, and SQLite
The data will be stored in an SQLite database and supports insert, read, update and delete operations. For this project I followed the official recommendations from the "Guide to App Architecture" 
https://developer.android.com/jetpack/docs/guide

# For The more curious:
The sole purpose of building this project is to implement what I've learned so far and also test my knowledge of Android Architecture which encircles best practices and recommended architecture for building robust, production-quality apps.

# 
**ViewModels** store and manage UI related data, they survive configuration changes and can be used seemlessly by the newly created activity. **LiveData** is an observable dataholder and it is life-cycle aware, which means it automatically starts and stops updating the UI-controller at the right times in it's lifecycle.
For the backend of the app I make use of the **"Room Persistence Library"**, which works as a wrapper around SQLite and helps to reduce boilerplate code by making extensive use of Annotations. Instead of creating an SQLiteOpenDbHelper, I simply turn Java classes into "entities" to create tables, and use "Data Access Objects" (DAO) to query these tables and make operations on them. **Room** also provides compile time verification for SQL statements, so we run into fewer runtime exceptions, caused by typos and invalid queries.
I will also be using a "Repository" class that works as another abstraction layer between the ViewModel and the underlying data model.
Together, this whole structure constitues an **"MVVM" (Model-View-ViewModel) architecture**, which follows the single responsibility and separation of concerns principles.

#
The **DAO** is an interface that defines all the database operations we want to do on our entity. For this we declare methods without a method body and annotate them with **@Insert**, **@Update**, **@Delete** or the generic **@Query**, where we can pass an SQLite query.
Instead of a Cursor, we can let these queries return instances of our own Java objects, which we can also wrap into LiveData, so our activity or fragment gets notified as soon as a row in the queried database table changes.
The RoomDatabase is an abstract class that ties all the pieces together and connects the entities to their corresponding DAO. Just as in an SQLiteOpenHelper, we have to define a version number and a migration strategy. With **fallbackToDestructiveMigration** we can let Room recreate our database if we increase the version number.
We create our database in form of a static singleton with the databaseBuilder, where we have to pass our database class and a file name.
# The Repository
The repository is not part of the Android architecture components but it is considered est practice provides or serves as an abstraction layer on top of the different data sources
