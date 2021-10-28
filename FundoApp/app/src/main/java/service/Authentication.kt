package service

import android.util.Log
import android.widget.Toast
import com.example.fundoapp.FacebookFragment
import com.example.fundoapp.LoginFragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import util.AuthStatus
import util.FirebaseUserToken
import util.User
import util.UserLoginResult


class Authentication {
    companion object {
        fun signIn(email: String, password: String, listener: (AuthStatus) -> Unit) {
            Log.d("signin", "Inside sign of firebase")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val user = AuthStatus(true, "Login successful")
                        listener(user)
                    } else {
                        val user = AuthStatus(false, it.exception?.message.toString())
                        listener(user)
                    }

                }

        }

        fun registerUser(email: String, password: String, listener: (AuthStatus) -> Unit) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //listener(true,"Registration successful")
                        listener(AuthStatus(true, "Registration successful"))
                    } else {
                        //listener(false,it.exception?.message.toString())
                        listener(AuthStatus(false, it.exception?.message.toString()))

                    }
                }
        }

        fun getCurrentUser() = FirebaseAuth.getInstance().currentUser
        fun getAuth() = FirebaseAuth.getInstance()
        fun getAccessToken() = AccessToken.getCurrentAccessToken()

        fun logOut() {
            if (AccessToken.getCurrentAccessToken() != null && AccessToken.getCurrentAccessToken()?.isExpired == false) {
                LoginManager.getInstance().logOut()
                FirebaseAuth.getInstance().signOut()
            } else {
                FirebaseAuth.getInstance().signOut()
            }

        }

        fun getCallBackManager() = CallbackManager.Factory.create()
        fun loginWithReadPermision(facebookFragment: FacebookFragment) {
            LoginManager.getInstance()
                .logInWithReadPermissions(facebookFragment, arrayListOf("email", "public_profile"))

        }

        fun registerCallBack(
            callbackManager: CallbackManager,
            listener: (UserLoginResult) -> Unit
        ) {
            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onCancel() {
                        listener(UserLoginResult(false, null))
                    }

                    override fun onError(error: FacebookException) {
                        listener(UserLoginResult(false, null))
                    }

                    override fun onSuccess(result: LoginResult?) {
                        listener(UserLoginResult(true, result))
                    }

                })
        }

        fun handletoken(
            token: AccessToken,
            auth: FirebaseAuth,
            listner: (FirebaseUserToken) -> Unit
        ) {
            val credential = FacebookAuthProvider.getCredential(token.token)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        listner(FirebaseUserToken(true, user))
                    } else {
                        listner(FirebaseUserToken(false, null))
                    }
                }

        }

        fun resetPassword(emailValue: String, listener: (AuthStatus) -> Unit) {
            FirebaseAuth.getInstance().sendPasswordResetEmail(emailValue)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val authStatus =
                            AuthStatus(true, "Check your mail,reset password link has been sent")
                        //listener(true,"Check your mail,reset password link has been sent")
                        listener(authStatus)
                    } else {
                        val authStatus = AuthStatus(false, task.exception?.message.toString())
                        listener(authStatus)
                        //listener(false,task.exception?.message.toString())
                    }

                }
        }

        fun getCurrentUid() = FirebaseAuth.getInstance().currentUser!!.uid



    }


}



