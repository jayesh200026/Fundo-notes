package service

import android.util.Log
import com.example.fundoapp.roomdb.entity.LabelEntity
import com.example.fundoapp.roomdb.entity.NoteLabelEntity
import com.example.fundoapp.service.Authentication
import com.example.fundoapp.service.model.*
import com.example.fundoapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.resumeCancellableWith
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
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
            val userNote = Notes(note.title, note.note, note.deleted, note.archived, note.mTime)
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
            return suspendCancellableCoroutine { cont ->
                var list = mutableListOf<NotesKey>()
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference(Constants.NOTES_TABLE)
                        .child(uid)
                        .orderByChild(Constants.COLUMN_MODIFIEDTIME)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(it: DataSnapshot) {

                                Log.d("READNOTE", "reading notes")
                                if (it.exists()) {
                                    for (i in it.children) {
                                        val title = i.child(Constants.COLUMN_TITLE).value.toString()
                                        val note = i.child(Constants.COLUMN_NOTE).value.toString()
                                        val key = i.key
                                        val deleted = i.child(Constants.COLUMN_DELETED).value
                                        val archived =
                                            i.child(Constants.COLUMN_ARCHIVED).value == true
                                        val deletedStatus = deleted == true
                                        val mTime =
                                            i.child(Constants.COLUMN_MODIFIEDTIME).value.toString()
                                        val remainder =
                                            i.child(Constants.COLUMN_REMAINDER).value.toString()
                                        val userNote =
                                            NotesKey(
                                                title,
                                                note,
                                                key!!,
                                                deletedStatus,
                                                archived,
                                                mTime,
                                                remainder.toLong()
                                            )
                                        list.add(userNote)
                                    }
                                }
                                if (cont.isActive) {
                                    cont.resumeWith(Result.success(list))
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.d("READNOTE", "failed reading notes")
                                cont.resumeWith(Result.failure(error.toException()))
                            }
                        })
                }


            }
        }

//        suspend fun readNotes(): MutableList<NotesKey> {
//
//            return suspendCoroutine { cont ->
//                var list = mutableListOf<NotesKey>()
//                val uid = Authentication.getCurrentUid()
//                if (uid != null) {
//                    val database =
//                        FirebaseDatabase.getInstance().getReference(Constants.NOTES_TABLE)
//                    database.child(uid)
//                        .get()
//                        .addOnSuccessListener {
//                            if (it.exists()) {
//                                for (i in it.children) {
//                                    val title = i.child(Constants.COLUMN_TITLE).value.toString()
//                                    val note = i.child(Constants.COLUMN_NOTE).value.toString()
//                                    val key = i.key
//                                    val deleted = i.child(Constants.COLUMN_DELETED).value
//                                    val archived=i.child(Constants.COLUMN_ARCHIVED).value == true
//                                    val deletedStatus = deleted == true
//                                    val mTime =
//                                        i.child(Constants.COLUMN_MODIFIEDTIME).value.toString()
//                                    val userNote =
//                                        NotesKey(title, note, key!!, deletedStatus,archived, mTime)
//                                    list.add(userNote)
//                                }
//                                cont.resumeWith(
//                                    Result.success(list)
//                                )
//                            }
//                        }
//                }
//            }
//        }

        suspend fun updateNote(note: NotesKey): Boolean {
            val userNote = Notes(note.title, note.note, note.deleted, note.archived, note.mTime,note.remainder)
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

        suspend fun createLabel(label: String, time: String): Label {
            return suspendCoroutine { cont ->
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference(Constants.LABEL_TABLE)
                        .child(uid)
                        .child(time)
                        .setValue(label)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                cont.resumeWith(Result.success(Label(labelId = time,labelName = label)))
                            } else {
                                cont.resumeWith(Result.failure(it.exception!!))
                            }
                        }
                }

            }

        }

        suspend fun addLabelsToNote(note: NoteLabels, time: String): Boolean {
            return suspendCoroutine { cont ->
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference(Constants.NOTE_LABEL_TABLE)
                        .child(uid)
                        .child(note.key + note.label)
                        .setValue(note)
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

        suspend fun deleteLabel(labelId: String): Boolean {
            return suspendCoroutine { cont ->
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference(Constants.LABEL_TABLE)
                        .child(uid)
                        .child(labelId)
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

        suspend fun updateLabel(labelID: String, newLabel: String): Boolean {
            return suspendCoroutine { cont ->
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference(Constants.LABEL_TABLE)
                        .child(uid)
                        .child(labelID)
                        .setValue(newLabel)
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

        suspend fun getKeys(pattern: String): MutableList<String> {
            return suspendCoroutine { cont ->
                var list = mutableListOf<String>()
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference(Constants.NOTE_LABEL_TABLE)
                        .child(uid)
                        .get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                for (i in it.children) {
                                    val key = i.key
                                    if (key != null) {
                                        if (key.contains(pattern)) {
                                            list.add(key)
                                        }
                                    }
                                }
                                cont.resumeWith(Result.success(list))
                            }
                        }
                }
            }
        }

        suspend fun deleteNoteLabel(labelId: String): Boolean {
            val list = getKeys(labelId)
            return suspendCoroutine { cont ->
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    val dbRef =
                        FirebaseDatabase.getInstance().getReference(Constants.NOTE_LABEL_TABLE)
                            .child(uid)

                    for (i in list) {
                        dbRef.child(i)
                            .removeValue()
                            .addOnCompleteListener {

                            }
                    }
                    cont.resumeWith(Result.success(true))
                }
            }

        }

        suspend fun readLabels(): MutableList<Label> {
            return suspendCoroutine { cont ->
                var list = mutableListOf<Label>()

                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference(Constants.LABEL_TABLE)
                        .child(uid)
                        .get()
                        .addOnSuccessListener {
                            if (it.exists()) {
                                for (i in it.children) {
                                    Log.d("label", i.key!!)
                                    val key = i.key!!
                                    val label = i.value.toString()
                                    Log.d("label", label)
                                    list.add(Label(labelId = key, labelName = label))
                                }
                                cont.resumeWith(Result.success(list))

                            }
                        }
                }
            }

        }

        suspend fun getNoteLabelRel(): MutableList<NoteLabelEntity> {
            return suspendCoroutine { cont ->
                val list = mutableListOf<NoteLabelEntity>()
                val uid = Authentication.getCurrentUid()
                if (uid != null) {
                    FirebaseDatabase.getInstance().getReference(Constants.NOTE_LABEL_TABLE)
                        .child(uid)
                        .get()
                        .addOnSuccessListener {
                            for (i in it.children) {
                                val key = i.child(Constants.COLUMN_KEY).value.toString()
                                val label = i.child(Constants.COLUMN_LABEL).value.toString()
                                val noteLabel = NoteLabelEntity(key, label)
                                list.add(noteLabel)
                            }
                            cont.resumeWith(Result.success(list))


                        }

                }
            }
        }

        suspend fun readLimitedNotes(key : String):MutableList<NotesKey>{
            val list = mutableListOf<NotesKey>()
            val query = getQuery(key)
            Log.d("Limited",query.toString())
            return suspendCancellableCoroutine {cont ->
                query.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(it: DataSnapshot) {
                        if (it.exists()) {

                            for (i in it.children) {
                                val title = i.child(Constants.COLUMN_TITLE).value.toString()
                                val note = i.child(Constants.COLUMN_NOTE).value.toString()
                                val key = i.key
                                val deleted = i.child(Constants.COLUMN_DELETED).value
                                val archived =
                                    i.child(Constants.COLUMN_ARCHIVED).value == true
                                val deletedStatus = deleted == true
                                val mTime =
                                    i.child(Constants.COLUMN_MODIFIEDTIME).value.toString()
                                val remainder =
                                    i.child(Constants.COLUMN_REMAINDER).value.toString()
                                val userNote =
                                    NotesKey(
                                        title,
                                        note,
                                        key!!,
                                        deletedStatus,
                                        archived,
                                        mTime,
                                        remainder.toLong()
                                    )
                                list.add(userNote)
                            }
                            if(cont.isActive) {
                                cont.resumeWith(Result.success(list))
                            }
                            else{
                                Log.d("Limited","cont is not active")
                            }
                        }
                        else{
                            cont.resumeWith(Result.success(mutableListOf<NotesKey>()))
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }
        }

        suspend fun getQuery(key : String):Query{
            val reference= FirebaseDatabase.getInstance().getReference(Constants.NOTES_TABLE)
            val uid = Authentication.getCurrentUid()
            return suspendCoroutine {cont->
                if(uid!=null){
                    if(key == ""){
                         cont.resumeWith(Result.success(reference.child(uid)
                             .orderByChild(Constants.COLUMN_MODIFIEDTIME).limitToLast(10)))
                    }
                    else{
                        Log.d("limited","inside else")
                        cont.resumeWith(Result.success(reference.child(uid).orderByChild(Constants.COLUMN_MODIFIEDTIME)
                            .endBefore(key).limitToLast(10)))
                    }
                }
            }
        }
    }
}