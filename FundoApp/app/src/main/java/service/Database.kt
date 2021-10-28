package service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import util.User

class Firebasedatabase {
    companion object {
        fun addUser(user: User, listner: (Boolean) -> Unit) {
            FirebaseDatabase.getInstance().getReference("users")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .setValue(user)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        listner(true)
                    } else {
                        listner(false)
                    }
                }

        }

        fun readUser(listner: (User) -> Unit) {
            val database = FirebaseDatabase.getInstance().getReference("users")

            database.child(FirebaseAuth.getInstance().currentUser!!.uid)
                .get().addOnSuccessListener {
                    if (it.exists()) {
                        val fullName = it.child("fullName").value
                        val email = it.child("email").value

                        val user = User(
                            fullName = fullName.toString(),
                            email = email.toString(),
                            status = true
                        )
                        listner(user)
                    }
                }
        }
    }
}