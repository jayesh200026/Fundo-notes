package service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import util.Notes
import util.SharedPref
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

        fun addNote(note:Notes,listner: (Boolean) -> Unit) {
            val numberOfNotes=SharedPref.getNoteSize("noteSize")
            val nextNote=numberOfNotes+1
            FirebaseDatabase.getInstance().getReference("notes")
                .child(Authentication.getCurrentUid())
                .child("note"+nextNote.toString())
                .setValue(note)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        listner(true)
                    }
                    else{
                        listner(false)
                    }
                }
        }

        fun readNotes(listner: (Boolean,MutableList<Notes>) -> Unit) {
            var list= mutableListOf<Notes>()
            val database = FirebaseDatabase.getInstance().getReference("notes")

            database.child(FirebaseAuth.getInstance().currentUser!!.uid)
                .get()
                .addOnSuccessListener {
                    if(it.exists()){
                       for(i in it.children){
                           val title=i.child("title").value.toString()
                           val note=i.child("note").value.toString()
                           val userNote=Notes(title, note)
                           list.add(userNote)
                       }
                        listner(true,list)
//                        val title=it.child("title").value
//                        val note=it.child("note").value
//                        val userNote=Notes(title.toString(),note.toString())
//                        listner(userNote)

                    }
                }


        }


    }
}