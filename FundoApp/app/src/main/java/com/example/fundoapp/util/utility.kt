package util

import android.util.Patterns
import android.widget.EditText

class Utillity{
    companion object{
        fun validateEmail(emailValue: String, email: EditText): Boolean {
            if(emailValue.isEmpty()){
                email.error="Email is needed"
                email.requestFocus()
                return false
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()){
                email.error="Enter valid email"
                email.requestFocus()
                return false
            }
            return true

        }

        fun validatePassword(passwordValue: String, password: EditText): Boolean{
            if(passwordValue.isEmpty()){
                password.error="Enter password"
                password.requestFocus()
                return false
            }
            if(passwordValue.length<6){
                password.error="Password is too small"
                password.requestFocus()
                return false
            }
            return true
        }

        fun validateName(fullNameValue: String, fullName: EditText): Boolean {

            if(fullNameValue.isEmpty()){
                fullName.error = "Full name is needed"
                fullName.requestFocus()
                return false
            }
            return true
        }

        fun validateAge(ageValue: String, age: EditText): Boolean {
            if(ageValue.isEmpty()){
                age.error="Age is required"
                age.requestFocus()
                return false
            }
            return true

        }

    }
}