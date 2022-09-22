package com.example.user2



import android.content.Context
import android.widget.Toast

class Repository(private val userDao: userDao) {
    private lateinit var mainActivity: MainActivity
    //    fun loadAllUsers(): Deferred<List<User>> = GlobalScope.async {
//        userDao.readUsers()
//    }
    val loadAllUsers = userDao.readUsers()
    suspend fun addFaceUser(userFace: User){

        userDao.addUser(userFace)

    }


    fun deleteFaceUser(userFace: User){
        userDao.deleteUser(userFace)
    }
    suspend fun deleteAll(){
        userDao.deleteEverything()
    }
}