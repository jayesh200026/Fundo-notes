package service

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class FirebaseStorage {
    companion object {

        fun uploadImage(uid: String?, imageUri: Uri, listener: (Boolean) -> Unit) {
            val storageRef = FirebaseStorage.getInstance().reference
            if (uid != null) {
                val fileRef = storageRef.child("users/" + uid + ".jpg")
                if (imageUri != null) {
                    fileRef.putFile(imageUri).addOnSuccessListener {
                        listener(true)
                    }
                }
                listener(false)
            }
        }

        fun fetchPhoto(currentUid: String?, listener: (Boolean, Uri?) -> Unit) {
            if (currentUid != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                try {
                    val fileRef = storageRef.child("users/" + currentUid + ".jpg")

                    fileRef.downloadUrl.addOnSuccessListener {
                        listener(true, it)
                    }
                        .addOnFailureListener {
                            listener(false, null)
                        }
                } catch (e: Exception) {
                    listener(false, null)
                }
            }
        }
    }
}