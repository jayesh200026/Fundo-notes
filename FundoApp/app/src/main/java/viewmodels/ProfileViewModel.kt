package viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import service.Authentication
import service.FirebaseStorage
import service.Firebasedatabase
import util.Notes
import util.User
import java.lang.Exception

class ProfileViewModel:ViewModel(){
    private val _profilePhotoFetch= MutableLiveData<Uri>()
    val profilePhotoFetch=_profilePhotoFetch as LiveData<Uri>

    private val _profilePhotoUploadStatus=MutableLiveData<Boolean>()
    val profilePhotoUploadStatus = _profilePhotoUploadStatus as LiveData<Boolean>

    private val _databaseReadingStatus = MutableLiveData<User>()
    val databaseReadingStatus = _databaseReadingStatus as LiveData<User>

    private val _readNotesFromDatabaseStatus = MutableLiveData<MutableList<Notes>>()
    var readNotesFromDatabaseStatus=_readNotesFromDatabaseStatus as LiveData<MutableList<Notes>>

    fun fetchProfile() {
        try {
            FirebaseStorage.fetchPhoto(Authentication.getCurrentUid()) { status, uri ->
                _profilePhotoFetch.value = uri
            }
        }
        catch (e: Exception){
        }
    }
    fun uploadProfile(uid: String?, imageUri: Uri) {
        FirebaseStorage.uploadImage(uid,imageUri){
            _profilePhotoUploadStatus.value=it
        }
    }

    fun readUserFRomDatabase() {
        Firebasedatabase.readUser {
            _databaseReadingStatus.value = it
        }
    }

    fun readNotesFromDatabase(){
        Firebasedatabase.readNotes(){status,list->
            if(status) {
                _readNotesFromDatabaseStatus.value = list
            }
        }
    }

}