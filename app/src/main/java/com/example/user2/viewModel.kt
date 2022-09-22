package com.example.user2



import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class viewModel(context: Context) : ViewModel() {
    private val dao:userDao=userDatabase.getDatabase(context).userdao()
    private val repository=Repository(dao)
    val loadAllUser = repository.loadAllUsers

    fun insertUser(user:User)
    {

            viewModelScope.launch(Dispatchers.IO){
            repository.addFaceUser(user)
        }
    }

    fun deleteUser(user: User)
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFaceUser(user)
        }
    }

    fun deleteAll()
    {
        viewModelScope.launch {
            repository.deleteAll()
        }
    }


}