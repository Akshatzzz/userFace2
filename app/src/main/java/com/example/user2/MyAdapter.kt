package com.example.user2


import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.bouncycastle.jce.provider.symmetric.Grainv1.Base
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


class MyAdapter(context: Context) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    val users = ArrayList<User>()
    val incontext = context
    val SECRET_KEY = "secretKey"
    val SECRET_IV = "secretIV"
    val cryptoManager = CryptoManager()
//    val encryptDecrypt = encryptDecrypt()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.user_element, parent, false)
        )

    }

    override fun onBindViewHolder(holder: MyAdapter.ViewHolder, position: Int) {

//        val email = CryptoManager().decrypt(users[position].Email).decodeToString()
//        val phone = CryptoManager().decrypt(users[position].Phone).decodeToString()
//        val uri = CryptoManager().decrypt(users[position].imageUri).decodeToString()
//        val uri = CryptoManager().decrypt(users[position].imageUri).decodeToString()
        val nameIv = users[position].ivName
        val nameDecrypted = users[position].encryptedName

        val emailIv = users[position].ivEmail
        val emainDecrypted = users[position].encryptedEmail

        val phoneIv = users[position].ivPhone
        val phoneDecrypted = users[position].encryptedPhone

        val uriIv = users[position].ivUri
        val uriDecrypted = users[position].encryptedUri

        val listName: ArrayList<String> = ArrayList()
        listName.add(nameIv)
        listName.add(nameDecrypted)

        val listEmail:ArrayList<String> = ArrayList()
        listEmail.add(emailIv)
        listEmail.add(emainDecrypted)

        val listPhone: ArrayList<String> = ArrayList()
        listPhone.add(phoneIv)
        listPhone.add(phoneDecrypted)

        val listUri: ArrayList<String> = ArrayList()
        listUri.add(uriIv)
        listUri.add(uriDecrypted)

        val name = CryptoManager().decrypt(listName)
        val email = CryptoManager().decrypt(listEmail)
        val phone = CryptoManager().decrypt(listPhone)
        val uri = CryptoManager().decrypt(listUri)
        Glide.with(holder.itemView.context)
            .load(uri.decodeToString().toUri())
            .into(holder.imageViewSmall)

        Glide.with(incontext)
            .load(uri.decodeToString().toUri())
            .into(holder.imageViewLarge)

        holder.textViewName.text = name.decodeToString()
        holder.textViewPhone.text = email.decodeToString()
        holder.textViewAddress.text = phone.decodeToString()
    }


    override fun getItemCount(): Int {
        return users.size
    }

    fun updateList(newList: List<User>) {
        users.clear()
        users.addAll(newList)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewSmall: ImageView = itemView.findViewById(R.id.imgSmall)
        val imageViewLarge: ImageView = itemView.findViewById(R.id.imgLarge)
        val textViewName: TextView = itemView.findViewById(R.id.tvName)
        val textViewPhone: TextView = itemView.findViewById(R.id.tvEmail)
        val textViewAddress: TextView = itemView.findViewById(R.id.tvPhone)

    }

}