package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.chatapp.databinding.ActivityProfileBinding
import com.example.chatapp.model.User
import com.example.chatapp.model.firebase.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class ProfileActivity : AppCompatActivity() , View.OnClickListener {
    private val TAG = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: DatabaseReference
    var firebaseDatabase: FirebaseDatabase? = null
    lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        setListener()
//        addDatatoFirebase("aa","aa","aaa")

    }

    private fun init(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase!!.getReference("users");
    }

    private fun setListener(){
        binding.loginBT.setOnClickListener(this)
    }

    private fun validation() {
        if (binding.nameET.text.toString().isEmpty()) {
            binding.nameET.error = "Please Enter the Name"
        } else if (binding.cityET.text.toString().isEmpty()) {
            binding.cityET.error = "Please Enter the CityName"
        } else if (binding.phoneET.text.toString().isEmpty()) {
            binding.phoneET.error = "Please Enter the Phone No"
        } else if (binding.bioET.text.toString().isEmpty()) {
            binding.bioET.error = "Please Enter the Bio"
        }  else {
            binding.pb.visibility = View.VISIBLE
            var user = User(binding.nameET.text.toString() , binding.cityET.text.toString() , binding.phoneET.text.toString(),binding.bioET.text.toString())
//            databaseReference = firebaseDatabase!!.getReference("users").child(LoginActivity.user!!.uid).setValue();
            databaseReference.child("users").child(LoginActivity.user!!.uid).setValue(user).addOnCompleteListener(object :
                OnCompleteListener<Void> {
                override fun onComplete(p0: Task<Void>) {
                    binding.pb.visibility = View.GONE
                    startActivity(Intent(this@ProfileActivity , MainActivity::class.java))
                    Log.d(TAG , "Updated")
                }

            }).addOnFailureListener(object : OnFailureListener{
                override fun onFailure(p0: Exception) {
                    binding.pb.visibility = View.GONE
                    Log.d(TAG , "Failed")
                }
            })
        }
    }

    override fun onClick(view: View?) {
        when(view){
            binding.loginBT ->{
                validation()
            }
        }
    }


}