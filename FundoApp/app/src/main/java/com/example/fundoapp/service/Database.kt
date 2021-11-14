package service

import android.util.Log
import com.example.fundoapp.service.Authentication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import util.Notes
import com.example.fundoapp.util.NotesKey
import com.example.fundoapp.util.SharedPref
import util.User
import kotlin.coroutines.suspendCoroutine

class Firebasedatabase {
    companion object {
        suspend fun addUser(user: User): Boolean {
            return suspendCoroutine { cont ->
                FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .setValue(user)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cont.resumeWith(Result.success(true))
                        } else {
                            cont.resumeWith(Result.failure(task.exception!!))
                        }
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
                        )
                        listner(user)
                    }
                }
        }

        suspend fun addNote(note: NotesKey): Boolean {
            val userNote = Notes(note.title, note.note, note.deleted, note.mTime)
            return suspendCoroutine { cont ->
                FirebaseDatabase.getInstance().getReference("notes")
                    .child(Authentication.getCurrentUid())
                    .child(note.key)
                    .setValue(userNote)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            cont.resumeWith(Result.success(true))
                        } else {
                            cont.resumeWith(Result.failure(it.exception!!))
                        }
                    }
            }

        }

        suspend fun readNotes(): MutableList<NotesKey> {

            return suspendCoroutine { cont ->
                var list = mutableListOf<NotesKey>()
                val database = FirebaseDatabase.getInstance().getReference("notes")
                database.child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            for (i in it.children) {

                                val title = i.child("title").value.toString()
                                val note = i.child("note").value.toString()
                                val key = i.key
                                val deleted = i.child("deleted").value
                                val deletedStatus = deleted == "true"

                                val mTime = i.child("mTime").value.toString()
                                val userNote = NotesKey(title, note, key!!, deletedStatus, mTime)
                                list.add(userNote)
                            }
                            cont.resumeWith(
                                Result.success(list)
                            )
                        }
                    }

            }


        }

        suspend fun updateNote(note: NotesKey): Boolean {
            val userNote = Notes(note.title, note.note, note.deleted, note.mTime)
            return suspendCoroutine { cont ->
                FirebaseDatabase.getInstance().getReference("notes")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(note.key)
                    .setValue(userNote)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            cont.resumeWith(Result.success(true))
                        } else {
                            cont.resumeWith(Result.failure(it.exception!!))
                        }

                    }
            }
        }

        suspend fun deleteNote(key: String): Boolean {
            return suspendCoroutine { cont ->
                FirebaseDatabase.getInstance().getReference("notes")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child(key)
                    .removeValue()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            cont.resumeWith(Result.success(true))
                        } else {
                            cont.resumeWith(Result.failure(it.exception!!))
                        }
                    }
            }
        }

        fun getChildNode(listener: (DataSnapshot) -> Unit) {
            val notePosition = SharedPref.getUpdateNotePosition("position")
            var count = 1
            FirebaseDatabase.getInstance().getReference("notes")
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .get()
                .addOnSuccessListener {
                    for (i in it.children) {
                        if (count == notePosition) {
                            listener(i)
                        }
                        count++
                    }
                }
        }

        suspend fun getNewlyAddedKey(): String {
            var count = 1
            var key = ""
            return suspendCoroutine { cont ->
                Log.d("notekey", "inside newly added key")
                FirebaseDatabase.getInstance().getReference("notes")
                    .child(Authentication.getCurrentUid())
                    .get()
                    .addOnSuccessListener {
                        for (i in it.children) {
                            Log.d("notekey", "inside for" + it.childrenCount)
                            if (count == it.childrenCount.toInt()) {
                                Log.d("notekey", key)
                                key = i.key.toString()
                                cont.resumeWith(Result.success(key))
                            }
                            count++
                        }
                    }
            }
        }
    }
}