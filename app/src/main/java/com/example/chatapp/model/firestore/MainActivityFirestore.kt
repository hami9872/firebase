package com.example.chatapp.model.firestore

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Adapter
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.databinding.ActivityMainFirestoreBinding
import com.example.chatapp.model.Post
import com.example.chatapp.model.firebase.CreatePostActivity
import com.example.chatapp.model.firebase.MainActivity
import com.example.chatapp.model.interfaces.OnItemClick
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonElement
import java.lang.Exception

class MainActivityFirestore : AppCompatActivity() , View.OnClickListener {
    private val TAG = MainActivity::class.java.simpleName
    lateinit var binding: ActivityMainFirestoreBinding
    lateinit var adapter : Adapter

    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainFirestoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        setListener()

    }

    override fun onResume() {
        super.onResume()
        readData()
    }

    private fun init() {

    }

    private fun setListener() {
        binding.floatingBT.setOnClickListener(this)
    }

    private fun setAdapter(list: List<Post>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
       adapter = Adapter(this, list as MutableList<Post> , object  : OnItemClick{
            override fun onClick(pos: Int, status: Adapter.ClickStatus) {
                var post = list[pos]
                when(status){
                    Adapter.ClickStatus.ITEM_CLICK->{
                        startActivity(
                            Intent(this@MainActivityFirestore, CreatePostFirestor::class.java).putExtra(
                                "POST",
                                post
                            )
                        )
                    }
                    Adapter.ClickStatus.DELETE->{
                        var ref = db.collection("posts").document(post.key.toString())
                        ref.delete().addOnCompleteListener(object : OnCompleteListener<Void>{
                            override fun onComplete(p0: Task<Void>) {
                                list.removeAt(pos)
                                adapter.notifyItemRemoved(pos)
                            }
                        }).addOnFailureListener(object :OnFailureListener{
                            override fun onFailure(p0: Exception) {
                                p0.message?.let { Log.d(TAG , it) }
                            }

                        } )

                    }
                }
            }
        })

        binding.recyclerView.adapter = adapter
    }

    private fun readData() {
        db.collection("posts")
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("", "${document.id}=>${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("", "Error getting documents.", exception)
            }
        val postList: MutableList<Post> = arrayListOf()
        db.collection("posts")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        Log.d(TAG, document.id + " => " + document.data)
                        val gson = Gson()
                        val jsonElement: JsonElement = gson.toJsonTree(document.data.toString())
//                        var aa = document.data.toString()
//                        val post: Post = gson.fromJson(jsonElement, Post::class.java)
                        val post=document.toObject(Post::class.java)
                        post.key = document.id
                        postList.add(post)
                    }
                    setAdapter(postList)
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }


    override fun onClick(p0: View?) {
        when (p0) {
            binding.floatingBT -> {
                startActivity(Intent(this@MainActivityFirestore, CreatePostFirestor::class.java))
            }
        }
    }

}