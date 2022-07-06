package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.chatapp.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() , View.OnClickListener {
    private val TAG = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        setListener()
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
    }

    private fun setListener(){
        binding.registerBT.setOnClickListener(this)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {

        }
    }

    private fun createUser(email: String, password: String) {
        binding.pb.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.pb.visibility = View.GONE
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    binding.pb.visibility = View.GONE
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    private fun validation(){
        if(binding.emailET.text.toString().isEmpty()){
            binding.emailET.error = "Please Enter the email"
        }else if(binding.passwordET.text.toString().isEmpty()){
            binding.passwordET.error = "Please Enter the password"
        }else{
            createUser(binding.emailET.text.toString() , binding.passwordET.text.toString());
        }
    }

    override fun onClick(view: View?) {
        when(view){
            binding.registerBT ->
                validation()
        }
    }
}