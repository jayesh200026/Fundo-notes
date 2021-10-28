package viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fundoapp.FacebookFragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.google.firebase.auth.FirebaseAuth
import service.Authentication
import util.FirebaseUserToken
import util.UserLoginResult

class FacebookViewModel :ViewModel(){

    private val _callbackStatus = MutableLiveData<UserLoginResult>()
    val callbackStatus = _callbackStatus as LiveData<UserLoginResult>

    private val _facebookLoginStatus = MutableLiveData<FirebaseUserToken>()
    val facebookLoginStatus = _facebookLoginStatus as LiveData<FirebaseUserToken>

    fun getCallBackManager(): CallbackManager {
        return Authentication.getCallBackManager()
    }

    fun getAuth(): FirebaseAuth {
        return Authentication.getAuth()
    }

    fun loginWithReadPermision(facebookFragment: FacebookFragment) {
        Authentication.loginWithReadPermision(facebookFragment)
    }

    fun registerCallBack(callbackManager: CallbackManager) {
        Authentication.registerCallBack(callbackManager) {
            _callbackStatus.value = it
        }
    }

    fun handleToken(token: AccessToken, auth: FirebaseAuth) {
        Authentication.handletoken(token, auth) {
            _facebookLoginStatus.value = it
        }
    }
}