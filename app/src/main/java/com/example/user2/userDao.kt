package com.example.user2



import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface userDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user:User)

    @Query("select * from user_details order by id asc")
    fun readUsers():LiveData<List<User>>

    @Delete
    fun deleteUser(user:User)

    @Query("delete from user_details")
    suspend fun deleteEverything()
}