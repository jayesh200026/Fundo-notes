package com.example.fundoapp.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider

import util.Utillity
import androidx.appcompat.app.AppCompatActivity
import com.example.fundoapp.R
import com.example.fundoapp.viewModel.LoginViewModel
import com.example.fundoapp.viewModel.LoginViewModelFactory
import com.example.fundoapp.viewModel.SharedViewModel
import com.example.fundoapp.viewModel.SharedViewModelFactory


open class LoginFragment : Fragment(), View.OnClickListener {

    lateinit var register: TextView
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var login: Button
    lateinit var forgotPassword: TextView
    lateinit var registration: RegistrationFragment
    lateinit var facebookFragment: FacebookFragment
    lateinit var facebookBtn: ImageView
    lateinit var resetPassword: TextView
    lateinit var sharedViewModel: SharedViewModel
    lateinit var loginViewModel: LoginViewModel
    var sharePref = activity?.getSharedPreferences("Mypref", Context.MODE_PRIVATE)
    var editor = sharePref?.edit()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        //(activity as AppCompatActivity?)!!.setSupportActionBar(view.findViewById(R.id.myToolbar))

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        getValues(view)
        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]



        loginViewModel=ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        loginViewModel.loginStatus.observe(viewLifecycleOwner) {
            if (it.status) {
                Toast.makeText(
                    requireContext(),
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
                sharedViewModel.setGotoHomePageStatus(true)
            } else {
                Toast.makeText(
                    requireContext(),
                    it.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }



        return view
    }


    private fun getValues(view: View) {
        register = view.findViewById(R.id.loginRegister)
        email = view.findViewById(R.id.loginEmail)
        password = view.findViewById(R.id.loginPassword)
        login = view.findViewById(R.id.login)
        forgotPassword = view.findViewById(R.id.forgotPassword)
        facebookBtn = view.findViewById(R.id.btnFacebook)
        resetPassword = view.findViewById(R.id.forgotPassword)
        login.setOnClickListener(this)
        register.setOnClickListener(this)
        forgotPassword.setOnClickListener(this)
        facebookBtn.setOnClickListener(this)
        resetPassword.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.loginRegister -> {
                sharedViewModel.setGoToRegisterPageStatus(true)

            }
            R.id.login -> {
                loginUser()
            }
            R.id.btnFacebook -> {
                sharedViewModel.setGoToFacebookLoginPageStatus(true)
            }

            R.id.forgotPassword -> {
                sharedViewModel.setGoToResetPasswordPageStatus(true)
            }
        }

    }

    private fun loginUser() {
        var profileFragment = HomeFragment()
        val emailValue = email.text.toString().trim()
        val passwordValue = password.text.toString().trim()
        val validEmail = Utillity.validateEmail(emailValue, email)
        val validPassword = Utillity.validatePassword(passwordValue, password)

        if (validEmail && validPassword) {
            loginViewModel.loginWithEmailNPassword(emailValue, passwordValue)

        }

    }


}