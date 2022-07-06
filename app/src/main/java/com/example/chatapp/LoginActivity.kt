package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityLoginBinding
import com.example.chatapp.model.User
import com.example.chatapp.model.firebase.MainActivity
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        var user: FirebaseUser? = null
        var userInfo: User? = null
    }

    private val TAG = ""
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: DatabaseReference
    var employeeInfo: EmployeeInfo? = null

    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null
// ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        init()
        setListener()
        checkUser()
//        addDatatoFirebase("aa","aa","aaa")
//        readData()
    }

    private fun checkUser() {
        user = auth.currentUser
        if (user != null) {
            readData()
        }
    }

    private fun init() {
        employeeInfo = EmployeeInfo()
        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference
        firebaseDatabase = FirebaseDatabase.getInstance();

        databaseReference = firebaseDatabase!!.getReference("users");
    }

    private fun setListener() {
        binding.registerBT.setOnClickListener(this)
        binding.loginBT.setOnClickListener(this)
    }

    private fun loginCall(email: String, password: String) {
        binding.pb.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    user = auth.currentUser
                    readData()

                    var aa = ""
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    binding.pb.visibility = View.GONE
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    private fun validation() {
        if (binding.emailET.text.toString().isEmpty()) {
            binding.emailET.error = "Please Enter the email"
        } else if (binding.passwordET.text.toString().isEmpty()) {
            binding.passwordET.error = "Please Enter the password"
        } else {
            loginCall(binding.emailET.text.toString(), binding.passwordET.text.toString());
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.registerBT ->
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            binding.loginBT ->
                validation()
        }
    }

    private fun readData() {

        if (binding.pb.visibility == View.GONE ) binding.pb.visibility = View.VISIBLE
        databaseReference?.child("users")?.child(user!!.uid)?.get()?.addOnSuccessListener {
            Log.i("firebase", "Got value ${it.value}")

            val userData: Map<String, Object> = it.getValue() as Map<String, Object>
            var userName = userData.get("username") as String;
            var city = userData.get("city") as String;
            var phone = userData.get("phone") as String;
            var bio = userData.get("bio") as String;
            userInfo = User(userName, city, phone, bio)
            binding.pb.visibility = View.GONE
            if (it.value == null) {
                startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
            } else {
                startActivity(Intent(this@LoginActivity, DatabaseSelectionActivity::class.java))
            }
        }?.addOnFailureListener {
            binding.pb.visibility = View.GONE
            Log.e("firebase", "Error getting data", it)
        }
    }

    private fun addDatatoFirebase(name: String, phone: String, address: String) {
        // below 3 lines of code is used to set
        // data in our object class.
        employeeInfo?.employeeName = (name)
        employeeInfo?.employeeContactNumber = (phone)
        employeeInfo?.employeeAddress = (address)

        // we are use add value event listener method
        // which is called with database reference.
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.
                databaseReference?.setValue(employeeInfo)

                // after adding this data we are showing toast message.
                Toast.makeText(this@LoginActivity, "data added", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                Toast.makeText(this@LoginActivity, "Fail to add data $error", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    class EmployeeInfo  // an empty constructor is
    // required when using
    // Firebase Realtime Database.
    {
        // created getter and setter methods
        // for all our variables.
        // string variable for
        // storing employee name.
        var employeeName: String? = null

        // string variable for storing
        // employee contact number
        var employeeContactNumber: String? = null

        // string variable for storing
        // employee address.
        var employeeAddress: String? = null
    }
}