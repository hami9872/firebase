package com.example.chatapp.model.firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.chatapp.LoginActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityCreatePostBinding
import com.example.chatapp.databinding.ActivityCreatePostFirestorBinding
import com.example.chatapp.model.Post
import com.example.chatapp.model.firebase.CreatePostActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostFirestor : AppCompatActivity(), View.OnClickListener {
    private val TAG = CreatePostFirestor::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCreatePostFirestorBinding
    var firebaseDatabase: FirebaseDatabase? = null
    lateinit var databaseReference: DatabaseReference
    val db = FirebaseFirestore.getInstance()
    var selectedPost: Post? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreatePostFirestorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        setListener()
    }

    private fun init() {
        if (intent.hasExtra("POST")) {
            selectedPost = intent.getSerializableExtra("POST") as Post
            setData()
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase!!.getReference("users");
    }

    private fun setListener() {
        binding.createPostBT.setOnClickListener(this)
    }

    private fun setData() {
        binding.titleET.setText(selectedPost?.title)
        binding.descET.setText(selectedPost?.body)
    }

    private fun validation() {
        if (binding.titleET.text.toString().isEmpty()) {
            binding.titleET.error = "Please Enter the Name"
        } else if (binding.descET.text.toString().isEmpty()) {
            binding.descET.error = "Please Enter the CityName"
        } else {
            binding.pb.visibility = View.VISIBLE
            LoginActivity.userInfo?.username?.let {
                writeNewPostData(
                    LoginActivity.user!!.uid,
                    it, binding.titleET.text.toString(), binding.descET.text.toString()
                )
            }

        }
    }

    private fun writeNewPostData(userId: String, username: String, title: String, body: String) {
        val post = Post(userId, username, title, body)
        val postValues = post.toMap()
//Adding a new document with generated ID
        if (selectedPost != null) {
            db.collection("posts").document(selectedPost?.key.toString()).update(postValues).addOnCompleteListener(object :
                OnCompleteListener<Void>{
                override fun onComplete(p0: Task<Void>) {
                    binding.pb.visibility = View.GONE
                }

            })
            finish()
            return
        }
        db.collection("posts").add(postValues)
            .addOnSuccessListener { documentReference ->
                Log.w(TAG, "DocumentSnapshot added with ID:${documentReference.id}")
                finish()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
                binding.pb.visibility = View.GONE
            }
            .addOnCanceledListener {
                Log.w(TAG, "Error adding document")
                binding.pb.visibility = View.GONE
            }

    }

    override fun onClick(view: View?) {
        when (view) {
            binding.createPostBT -> {
                validation()
            }
        }
    }


}