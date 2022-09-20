package com.example.user2



import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class Repository(private val userDao: userDao) {
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