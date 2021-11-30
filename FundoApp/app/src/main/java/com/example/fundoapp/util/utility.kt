package com.example.fundoapp.util

import android.content.Context
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fundoapp.R
import com.example.fundoapp.ui.adapters.NoteAdapter
import com.example.fundoapp.ui.RemainderAdapter

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

        fun loadRemainderNotesInLayout(context: Context, layout: ImageView, gridrecyclerView: RecyclerView,  adapter: RemainderAdapter){
            var flag: Boolean
            var count = SharedPref.get("counter")
            if (count == "") {
                flag = true
            } else if (count == "true") {
                flag = false
            } else {
                flag = true
            }

            if (flag) {
                layout.setImageResource(R.drawable.ic_baseline_grid_on_24)
                gridrecyclerView.isVisible = false
                gridrecyclerView.layoutManager = LinearLayoutManager(context)
                gridrecyclerView.adapter = adapter
                gridrecyclerView.isVisible = true
                SharedPref.addString("counter", "true")

            } else {
                layout.setImageResource(R.drawable.ic_linear_24)
                //gridrecyclerView.isVisible = false
                gridrecyclerView.layoutManager = GridLayoutManager(context, 2)
                gridrecyclerView.adapter = adapter
                gridrecyclerView.isVisible = true
                SharedPref.addString("counter", "false")

            }
        }

        fun loadNotesInLayoutType(context: Context, layout: ImageView, gridrecyclerView: RecyclerView,  adapter: NoteAdapter) {
            var flag: Boolean
            var count = SharedPref.get(Constants.LAYOUT)
            if (count == "") {
                flag = true
            } else if (count == Constants.LINEAR) {
                flag = false
            } else {
                flag = true
            }

            if (flag) {
                layout.setImageResource(R.drawable.ic_baseline_grid_on_24)
                gridrecyclerView.isVisible = false
                gridrecyclerView.layoutManager = LinearLayoutManager(context)
                //gridrecyclerView.adapter = adapter
                gridrecyclerView.isVisible = true
                SharedPref.addString(Constants.LAYOUT, Constants.LINEAR)

            } else {
                layout.setImageResource(R.drawable.ic_linear_24)
                //gridrecyclerView.isVisible = false
                gridrecyclerView.layoutManager = GridLayoutManager(context, 2)
                //gridrecyclerView.adapter = adapter
                gridrecyclerView.isVisible = true
                SharedPref.addString(Constants.LAYOUT, Constants.GRID)

            }

        }

    }
}