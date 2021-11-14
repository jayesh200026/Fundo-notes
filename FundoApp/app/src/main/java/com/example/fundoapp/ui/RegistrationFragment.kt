package com.example.fundoapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.fundoapp.R
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory
import util.User
import util.Utillity
import com.example.fundoapp.viewModel.RegistrationViewModel
import com.example.fundoapp.viewModel.RegistrationViewModelFactory


class RegistrationFragment : Fragment(), View.OnClickListener {

    lateinit var register: Button
    lateinit var fullName: EditText
    lateinit var age: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var login: TextView
    lateinit var sharedViewModel: SharedViewModel
    lateinit var registrationViewModel: RegistrationViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        registrationViewModel=ViewModelProvider(this, RegistrationViewModelFactory())[RegistrationViewModel::class.java]

        val view = inflater.inflate(R.layout.fragment_registration, container, false)
        register = view.findViewById(R.id.register)
        login = view.findViewById(R.id.registrationLogin)
        fullName = view.findViewById(R.id.fullName)
        age = view.findViewById(R.id.age)
        email = view.findViewById(R.id.registrationEmail)
        password = view.findViewById(R.id.registrationPass)

        register.setOnClickListener(this)
        login.setOnClickListener(this)
        return view
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.registrationLogin -> {
                sharedViewModel.setGoToLoginPageStatus(true)
            }
            R.id.register -> {
                registerUser(view)
            }
        }
    }

    private fun registerUser(view: View) {
        val fullNameValue = fullName.text.toString().trim()
        val ageValue = age.text.toString().trim()
        val emailValue = email.text.toString().trim()
        val passwordValue = password.text.toString().trim()

        val validName = Utillity.validateName(fullNameValue, fullName)
        val validAge = Utillity.validateAge(ageValue, age)

        val validEmail = Utillity.validateEmail(emailValue, email)
        val validPassword = Utillity.validatePassword(passwordValue, password)

        if (validName && validAge && validEmail && validPassword) {
            registrationViewModel.registerUser(emailValue,passwordValue)

        }
        sharedViewModel.databaseRegistrationStatus.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(
                    requireContext(),
                    "database Registration successful",
                    Toast.LENGTH_LONG
                ).show()
                sharedViewModel.setGotoHomePageStatus(true)
            }
            else{
                Toast.makeText(
                    requireContext(),
                    "database Registration failed",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        registrationViewModel.registrationStatus.observe(viewLifecycleOwner) {
            if (it.status) {
                Toast.makeText(
                    requireContext(),
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
                val user = User(fullNameValue, ageValue, emailValue)
                sharedViewModel.addUserToDatabase(user,requireContext())
                //sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


}