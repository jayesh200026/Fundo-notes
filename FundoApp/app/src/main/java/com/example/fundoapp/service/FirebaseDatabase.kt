package service

import com.example.fundoapp.service.Authentication
import com.example.fundoapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.fundoapp.service.model.Notes
import com.example.fundoapp.service.model.NotesKey
import com.example.fundoapp.service.model.User
import kotlin.coroutines.suspendCoroutine

class FirebaseDatabase {
    companion object {
        suspend fun addUser(user: User): Boolean = suspendCoroutine { cont ->
            FirebaseDatabase.getInstance().getReference(Constants.USER_TABLE)
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

        fun readUser(listner: (User) -> Unit) {
            val database = FirebaseDatabase.getInstance().getReference(Constants.USER_TABLE)

            database.child(FirebaseAuth.getInstance().currentUser!!.uid)
                .get().addOnSuccessListener {
                    if (it.exists()) {
                        val fullName = it.child(Constants.COLUMN_FULLNAME).value
                        val email = it.child(Constants.COLUMN_EMAIL).value

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
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference(Constants.NOTES_TABLE)
                        .child(uid)
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
        }

        suspend fun readNotes(): MutableList<NotesKey> {

            return suspendCoroutine { cont ->
                var list = mutableListOf<NotesKey>()
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    val database =
                        FirebaseDatabase.getInstance().getReference(Constants.NOTES_TABLE)
                    database.child(uid)
                        .get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                for (i in it.children) {
                                    val title = i.child(Constants.COLUMN_TITLE).value.toString()
                                    val note = i.child(Constants.COLUMN_NOTE).value.toString()
                                    val key = i.key
                                    val deleted = i.child(Constants.COLUMN_DELETED).value
                                    val deletedStatus = deleted == "true"
                                    val mTime =
                                        i.child(Constants.COLUMN_MODIFIEDTIME).value.toString()
                                    val userNote =
                                        NotesKey(title, note, key!!, deletedStatus, mTime)
                                    list.add(userNote)
                                }
                                cont.resumeWith(
                                    Result.success(list)
                                )
                            }
                        }
                }
            }
        }

        suspend fun updateNote(note: NotesKey): Boolean {
            val userNote = Notes(note.title, note.note, note.deleted, note.mTime)
            return suspendCoroutine { cont ->
                FirebaseDatabase.getInstance().getReference(Constants.NOTES_TABLE)
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
                FirebaseDatabase.getInstance().getReference(Constants.NOTES_TABLE)
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
    }
}