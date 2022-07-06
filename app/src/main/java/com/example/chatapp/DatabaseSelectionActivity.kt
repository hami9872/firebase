package com.example.chatapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chatapp.databinding.ActivityCreatePostFirestorBinding
import com.example.chatapp.databinding.ActivityDatabaseSelectionBinding
import com.example.chatapp.model.firebase.MainActivity
import com.example.chatapp.model.firestore.MainActivityFirestore

class DatabaseSelectionActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityDatabaseSelectionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDatabaseSelectionBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setListener()
    }

    private fun setListener() {
        binding.firebaseDataStore.setOnClickListener(this)
        binding.firebaseRealDataBase.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.firebaseDataStore-> {
                startActivity(Intent(this@DatabaseSelectionActivity , MainActivityFirestore::class.java))
            }

            binding.firebaseRealDataBase-> {
                startActivity(Intent(this@DatabaseSelectionActivity , MainActivity::class.java))
            }
        }
    }
}