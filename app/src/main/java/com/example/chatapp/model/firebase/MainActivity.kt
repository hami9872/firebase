package com.example.chatapp.model.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Adapter
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.model.Post
import com.example.chatapp.model.interfaces.OnItemClick
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.JsonElement


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = MainActivity::class.java.simpleName
    lateinit var binding: ActivityMainBinding
    var firebaseDatabase: FirebaseDatabase? = null
    lateinit var databaseReference: DatabaseReference
    lateinit var adapter : Adapter

    val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
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
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase!!.getReference("users");

    }

    private fun setListener() {
        binding.floatingBT.setOnClickListener(this)
    }

    private fun setAdapter(list: List<Post>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = Adapter(this, list as MutableList<Post> , object : OnItemClick{
            override fun onClick(pos: Int, status: Adapter.ClickStatus) {
                var post = list[pos]
                    when(status){
                        Adapter.ClickStatus.ITEM_CLICK->{
                           startActivity(
                                Intent(this@MainActivity, CreatePostActivity::class.java).putExtra(
                                    "POST",
                                    post
                                )
                            )
                        }
                        Adapter.ClickStatus.DELETE->{
                            val ref = FirebaseDatabase.getInstance().getReference("users")
                            val applesQuery: Query =
                                ref.child("posts").child(post.key.toString()).orderByChild("title")
                                    .equalTo(post.title)

                            val rootRef = FirebaseDatabase.getInstance().reference
                            ref.child("posts").child(post.key.toString()).removeValue()  .addOnCompleteListener(object :
                                OnCompleteListener<Void> {
                                override fun onComplete(p0: Task<Void>) {
                                    list.removeAt(pos)
                                    adapter.notifyItemRemoved(pos)
                                }
                            })
                        }
                    }
            }
        })

        binding.recyclerView.adapter = adapter
    }

    private fun readDtataa() {
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
                        postList.add(post)
                    }
                    setAdapter(postList)
                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                }
            }
    }

    private fun readData() {
        val key = databaseReference.child("posts").push().key
        if (key == null) {
            Log.w("", "Couldn't get push key for posts")
            return
        }

        databaseReference?.child("posts").get()
            ?.addOnSuccessListener {
                Log.i("firebase", "Got value ${it.value}")
                val postList: MutableList<Post> = arrayListOf()
                val map: Map<String, Object> = it.getValue() as Map<String, Object>
                for ((key, value) in map) {
                    val map: Map<String, Object> = map.get(key) as Map<String, Object>
                    val gson = Gson()
                    val jsonElement: JsonElement = gson.toJsonTree(map)
                    val post: Post = gson.fromJson(jsonElement, Post::class.java)
                    post.key = key
                    postList.add(post)
                }

                setAdapter(postList)

//                var userName = userData.get("username") as String;
//                var city = userData.get("city") as String;
//                var phone = userData.get("phone") as String;
//                var bio = userData.get("bio") as String;
//                LoginActivity.userInfo = User(userName, city, phone, bio)
                binding.pb.visibility = View.GONE

            }?.addOnFailureListener {
                binding.pb.visibility = View.GONE
                Log.e("firebase", "Error getting data", it)
            }
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.floatingBT -> {
                startActivity(Intent(this@MainActivity, CreatePostActivity::class.java))
            }
        }
    }
}