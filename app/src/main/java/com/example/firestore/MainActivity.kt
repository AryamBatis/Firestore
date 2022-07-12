package com.example.firestore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val userCollectionRef = Firebase.firestore.collection("users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val addButton = findViewById<Button>(R.id.addButton)
        val retrieveButton = findViewById<Button>(R.id.retrieveButton)
        val updatebutton = findViewById<Button>(R.id.updateButton)
        val deleteButton = findViewById<Button>(R.id.deletebutton)


        addButton.setOnClickListener {
          var user = getOldUserDate()
                saveUser(user)
            }
        retrieveButton.setOnClickListener {
            retrieveData()
        }
        updatebutton.setOnClickListener {
            var user = getOldUserDate()
            var newUserMap = getNewUserData()
            updateUserData(user,newUserMap)
        }
        deleteButton.setOnClickListener {
             var user = getOldUserDate()
            deletUser(user)
        }
    }
    private fun getOldUserDate():User {
        val firstName = findViewById<EditText>(R.id.etFirstName).text.toString()
        val lastname = findViewById<EditText>(R.id.etLastName).text.toString()

        return User(firstName,lastname)
    }

    private fun getNewUserData():Map<String,Any>{
        val firstName = findViewById<EditText>(R.id.etNewfirstname).text.toString()
        val lastname = findViewById<EditText>(R.id.etNewLastName).text.toString()
        var map = mutableMapOf<String,Any>()
        if(firstName.isNotEmpty()){
            map["firstName"] = firstName
        }
        if(lastname.isNotEmpty()){
            map["lastName"] = lastname
        }
        return map
    }
    private fun deletUser(user: User){
        userCollectionRef
            .whereEqualTo("firstName",user.firstName)
            .whereEqualTo("lastName",user.lastName)
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    if(task.result!!.documents.isNotEmpty()){
                        for(document in task.result!!.documents){
                            userCollectionRef.document(document.id).delete()
                        }
                    }else{
                        Toast.makeText(this@MainActivity,
                            "No matching documents",Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this@MainActivity,
                    it.message,Toast.LENGTH_LONG).show()
            }
    }

    private fun updateUserData(user: User,newUserMap: Map<String,Any>){
        userCollectionRef
            .whereEqualTo("firstName",user.firstName)
            .whereEqualTo("lastName",user.lastName)
            .get()
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    if(task.result!!.documents.isNotEmpty()){
                        for(document in task.result!!.documents){
                            userCollectionRef.document(document.id).set(
                                newUserMap, SetOptions.merge()
                            )
                        }
                    }else{
                        Toast.makeText(this@MainActivity,
                            "No matching documents",Toast.LENGTH_LONG).show()
                    }
                }
            }.addOnFailureListener{
                Toast.makeText(this@MainActivity,
                    it.message,Toast.LENGTH_LONG).show()
            }
    }

    private fun retrieveData(){
        userCollectionRef.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val sb = StringBuilder()
                for (document in task.result!!.documents){
                    val user = document.toObject<User>()
                    sb.append("$user \n")
                }
                findViewById<TextView>(R.id.TVUsers).text = sb
                Toast.makeText(this@MainActivity,"Done",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            println(it.message)
        }
    }
    private fun saveUser(user:User){
        userCollectionRef.add(user).addOnCompleteListener{task ->
            if(task.isSuccessful){
                Toast.makeText(this@MainActivity,"Successfully added user",Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener{
            println(it.message)
        }
    }
}