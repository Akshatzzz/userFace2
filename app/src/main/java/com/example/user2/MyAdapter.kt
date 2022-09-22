package com.example.user2


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyAdapter(context: Context):RecyclerView.Adapter<MyAdapter.ViewHolder>(){
    val users = ArrayList<User>()
    val incontext=context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_element, parent, false))
    }

    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {
        Glide.with(incontext)
            .load(users[position].imageUri.toUri())
            .into(holder.imageViewSmall)

        Glide.with(incontext)
            .load(users[position].imageUri.toUri())
            .into(holder.imageViewLarge)
//        holder.imageViewSmall.setImageURI(users[position].imageUri.toUri())
//        holder.imageViewLarge.setImageURI(users[position].imageUri.toUri())
        holder.textViewName.text = users[position].name
        holder.textViewPhone.text = users[position].Email
        holder.textViewAddress.text = users[position].Phone
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateList(newList: List<User>)
    {
        users.clear()
        users.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView)
    {
        val imageViewSmall: ImageView = itemView.findViewById(R.id.imgSmall)
        val imageViewLarge: ImageView = itemView.findViewById(R.id.imgLarge)
        val textViewName: TextView = itemView.findViewById(R.id.tvName)
        val textViewPhone: TextView = itemView.findViewById(R.id.tvEmail)
        val textViewAddress: TextView = itemView.findViewById(R.id.tvPhone)
    }
}