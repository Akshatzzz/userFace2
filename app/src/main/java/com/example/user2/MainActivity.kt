package com.example.user2


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var myViewModel: viewModel
    lateinit var recyclerView: RecyclerView
    lateinit var adapter : MyAdapter
    lateinit var empty:View
    lateinit var delAll: FloatingActionButton
    private lateinit var fab:FloatingActionButton
    //    private lateinit var userSwipe:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        var flag =0

        myViewModel = viewModel(applicationContext)
        recyclerView = findViewById(R.id.recyclerView)
        adapter = MyAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        empty = findViewById(R.id.empty_data_parent)
        val emptyDataObserver = EmptyDataObserver(recyclerView,empty)
        adapter.registerAdapterDataObserver(emptyDataObserver)

        val cnt=adapter.itemCount
        Log.d("AKSHAT","$cnt")
        myViewModel.loadAllUser.observe(this) { it ->
            it?.let {
                adapter.updateList(it)
            }
        }
        delAll=findViewById(R.id.deleteAll)

        delAll.setOnClickListener{
            deleteAllValues()
        }

        fab=findViewById(R.id.FAB)
        fab.setOnClickListener{
            intent = Intent(this,NewUser::class.java)
            startActivity(intent)
            finish()
        }
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            var flag =0
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val user  = adapter.users.get(viewHolder.adapterPosition)

                val position=viewHolder.adapterPosition

                adapter.users.removeAt(viewHolder.adapterPosition)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)
                myViewModel.deleteUser(user)
                Snackbar.make(recyclerView, "Deleted " + user.name, Snackbar.LENGTH_LONG)
                    .setAction(
                        "Undo",
                        View.OnClickListener {
//                            flag++
                            // adding on click listener to our action of snack bar.
                            // below line is to add our item to array list with a position.
                            adapter.users.add(position, user)
                            myViewModel.insertUser(user)
                            // below line is to notify item isadapter class.
                            // added to our
                            adapter.notifyItemInserted(position)
                        }).show()
            }

        }).attachToRecyclerView(recyclerView)
//        if(flag==0)
//        {
//            myViewModel.deleteUser(userSwipe)
//            flag = 0
//        }
    }

    private fun deleteAllValues() {
        myViewModel.deleteAll()
    }
    fun empty(){
        Toast.makeText(this,"Enter Data Properly",Toast.LENGTH_LONG).show()
    }
}