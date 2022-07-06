package com.example.chatapp.model.firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.chatapp.LoginActivity
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityCreatePostBinding
import com.example.chatapp.model.Post
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class CreatePostActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = CreatePostActivity::class.java.simpleName
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCreatePostBinding
    var firebaseDatabase: FirebaseDatabase? = null
    lateinit var databaseReference: DatabaseReference
    val db = FirebaseFirestore.getInstance()
    var selectedPost: Post? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_profile)

        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        setListener()

//        addDatatoFirebase("aa","aa","aaa")

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
        binding.loginBT.setOnClickListener(this)
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
                writeNewPost(
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
        db.collection("posts").add(postValues)
            .addOnSuccessListener { documentReference ->
                Log.w(TAG, "DocumentSnapshot added with ID:${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
            .addOnCanceledListener {
                Log.w(TAG, "Error adding document")
            }

    }

    private fun writeNewPost(userId: String, username: String, title: String, body: String) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        var key = databaseReference.child("posts").push().key
        if (key == null) {
            Log.w(TAG, "Couldn't get push key for posts")
            return
        }

        if (selectedPost != null)
            key = selectedPost?.key


        val post = Post(userId, username, title, body)
        val postValues = post.toMap()

        val childUpdates = hashMapOf<String, Any>(
            "/posts/$key" to postValues,
            "/user-posts/$userId/$key" to postValues
        )


        databaseReference.updateChildren(childUpdates).addOnCompleteListener(object :
            OnCompleteListener<Void> {
            override fun onComplete(p0: Task<Void>) {
                finish()
            }
        })


    }


    override fun onClick(view: View?) {
        when (view) {
            binding.loginBT -> {
                validation()
            }
        }
    }


}