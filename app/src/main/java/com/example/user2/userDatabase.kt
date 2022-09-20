package com.example.user2


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converter::class)
@Database(entities = [User::class], version = 1)
abstract class userDatabase: RoomDatabase(){

    abstract fun userdao():userDao

    companion object{
        @Volatile
        private var INSTANCE: userDatabase ?= null

        fun getDatabase(context: Context):userDatabase =
            INSTANCE?: synchronized(this){
                INSTANCE?: buildBatabase(context).also {
                    INSTANCE = it
                }
            }
        fun buildBatabase(context: Context) =
            Room.databaseBuilder(context,
                userDatabase::class.java,
                "face_user_database").build()
    }
}