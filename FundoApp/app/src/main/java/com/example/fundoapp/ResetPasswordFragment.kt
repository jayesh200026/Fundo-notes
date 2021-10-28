package com.example.fundoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import service.Authentication
import util.Utillity
import viewmodels.ResetPasswordViewModel
import viewmodels.ResetPasswordViewModelFactory
import viewmodels.SharedViewModel
import viewmodels.SharedViewModelFactory

class ResetPasswordFragment : Fragment(), View.OnClickListener {

    lateinit var resetEmail:EditText
    lateinit var resetBtn:Button
    lateinit var sharedViewModel: SharedViewModel
    lateinit var resetPasswordViewModel: ResetPasswordViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view=inflater.inflate(R.layout.fragment_reset_password, container, false)
        sharedViewModel= ViewModelProvider(requireActivity(), SharedViewModelFactory())[SharedViewModel::class.java]
        resetPasswordViewModel=ViewModelProvider(this,ResetPasswordViewModelFactory())[ResetPasswordViewModel::class.java]
        resetEmail=view.findViewById(R.id.resetEmail)
        resetBtn=view.findViewById(R.id.resetPassword)
        resetBtn.setOnClickListener(this)
        resetPasswordViewModel.resetPasswordStatus.observe(viewLifecycleOwner){
            if(it.status){
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
                sharedViewModel.setGoToLoginPageStatus(true)
            }
            else{
                Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
            }
        }
        return view
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.resetPassword->{
                resetPassword()
            }
        }
    }

    private fun resetPassword() {
        val emailValue=resetEmail.text.toString().trim()
        if(Utillity.validateEmail(emailValue,resetEmail))
        {
            resetPasswordViewModel.resetPassword(emailValue)
        }

//        resetPasswordViewModel.resetPasswordStatus.observe(viewLifecycleOwner){
//            if(it.status){
//                Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
//                sharedViewModel.setGoToLoginPageStatus(true)
//            }
//            else{
//                Toast.makeText(requireContext(),it.message,Toast.LENGTH_LONG).show()
//            }
//        }
    }


}