package com.example.fundoapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.squareup.picasso.Picasso
import service.Authentication
import service.Firebasedatabase
import util.SharedPref
import viewmodels.ProfileViewModel
import viewmodels.ProfileViewModelFactory
import viewmodels.SharedViewModel
import viewmodels.SharedViewModelFactory


class ProfileFragment : Fragment() {

    lateinit var dialog: Dialog
    lateinit var userIcon: ImageView
    lateinit var  dailog_logout:Button
    lateinit var dialog_profile:ImageView
    lateinit var dailog_edit:ImageView
    lateinit var dailog_username:TextView
    lateinit var dailog_email:TextView
    lateinit var getImage:ActivityResultLauncher<String>


    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var profileViewModel:ProfileViewModel
    var email: String? = null
    var fullName: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        var profilePhot:Uri?=null

        userIcon = requireActivity().findViewById(R.id.userProfile)



        sharedViewModel = ViewModelProvider(
            requireActivity(),
            SharedViewModelFactory()
        )[SharedViewModel::class.java]

        profileViewModel = ViewModelProvider(
            this,
            ProfileViewModelFactory()
        )[ProfileViewModel::class.java]

        getUserDetails()

        profileViewModel.profilePhotoFetch.observe(viewLifecycleOwner){
           // profilePhot = it

            if(it!=null) {
                SharedPref.addString("uri",it.toString())
                Picasso.get().load(it).into(userIcon)
                Picasso.get().load(it).into(dialog_profile)
            }
        }

        profileViewModel.profilePhotoUploadStatus.observe(viewLifecycleOwner){
            if(it) {
                profileViewModel.fetchProfile()
            }
        }

        loadAvatar(userIcon)
        profileViewModel.fetchProfile()

        initialiseDialog()

//        dialog = Dialog(requireContext())
//        dialog.setContentView(R.layout.custom_dailogue)
//        dialog.window?.setBackgroundDrawable(
//            getDrawable(
//                requireContext(),
//                R.drawable.custom_dialog_background
//            )
//        )
//        dialog.window
//            ?.setLayout(800, 700)
//        dialog.setCancelable(true) //Optional
//        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

//        val dailog_logout = dialog.findViewById<Button>(R.id.dailogueLogout)
//        val dialog_profile = dialog.findViewById<ImageView>(R.id.dialogProfile)

//        val dailog_edit=dialog.findViewById<ImageView>(R.id.editProfile)

        getImage=registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                dialog_profile.setImageURI(it)
                userIcon.setImageURI(it)
                val uid=sharedViewModel.getCurrentUid()
                profileViewModel.uploadProfile(uid,it)
            }
        )



//        profileViewModel.profilePhotoUploadStatus.observe(viewLifecycleOwner){
//            if(it) {
//                profileViewModel.fetchProfile()
//            }
//        }


//        profileViewModel.profilePhotoFetch.observe(viewLifecycleOwner){
//            profilePhot = it
//            if(it!=null) {
//                //Picasso.get().load(it).into(userIcon)
//                //Picasso.get().load(it).into(dialog_profile)
//            }
//        }


        onClickListeners()
//        userIcon.setOnClickListener {
////            getUserDetails(profilePhot)
//            dialog.show()
//        }
//
//        dailog_edit.setOnClickListener {
//            getImage.launch("image/*")
//        }
//
//        dailog_logout.setOnClickListener {
//            userIcon.setImageResource(R.drawable.man)
//            dialog_profile.setImageResource(R.drawable.man)
//            dialog.dismiss()
//            sharedViewModel.logout()
//            sharedViewModel.setGoToLoginPageStatus(true)
//        }
        return view
    }

    private fun onClickListeners() {
        userIcon.setOnClickListener {
            dialog.show()
        }

        dailog_edit.setOnClickListener {
            getImage.launch("image/*")
        }

        dailog_logout.setOnClickListener {
            userIcon.setImageResource(R.drawable.man)
            dialog_profile.setImageResource(R.drawable.man)
            SharedPref.clearAll()
            dialog.dismiss()
            sharedViewModel.logout()
            sharedViewModel.setGoToLoginPageStatus(true)
        }
    }

    private fun initialiseDialog() {
        dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.custom_dailogue)
        dailog_logout = dialog.findViewById(R.id.dailogueLogout)
        dialog_profile = dialog.findViewById(R.id.dialogProfile)
        dailog_edit=dialog.findViewById(R.id.editProfile)
        dailog_email=dialog.findViewById(R.id.dailogueEmail)
        dailog_username=dialog.findViewById(R.id.dailogueuserName)
        val sharePrefName=SharedPref.get("name")
        val sharePrefEmail=SharedPref.get("email")
        val sharePrefUriString=SharedPref.get("uri")
        val photoUri=sharePrefUriString?.toUri()

        dailog_username.text=sharePrefName
        dailog_email.text=sharePrefEmail
        if(sharePrefUriString ==""){
            dialog_profile.setImageResource(R.drawable.man)
        }
        else {
            Picasso.get().load(photoUri).into(dialog_profile)
        }


        dialog.window?.setBackgroundDrawable(
            getDrawable(
                requireContext(),
                R.drawable.custom_dialog_background
            )
        )
        dialog.window
            ?.setLayout(800, 700)
        dialog.setCancelable(true) //Optional
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }


    private fun loadAvatar(userIcon: ImageView?) {
        val sharePrefUriString=SharedPref.get("uri")
        val photoUri=sharePrefUriString?.toUri()
        if(sharePrefUriString == ""){
            userIcon?.setImageResource(R.drawable.man)
        }
        else{
            Picasso.get().load(photoUri).into(userIcon)
        }
//        userIcon?.setImageResource(R.drawable.man)
    }




    private fun getUserDetails() {

        profileViewModel.readUserFRomDatabase()

        profileViewModel.databaseReadingStatus.observe(viewLifecycleOwner) {
            email = it.email
            fullName = it.fullName
            SharedPref.addString("email",email!!)
            SharedPref.addString("name",fullName!!)
            dailog_email.text = email
            dailog_username.text = fullName
        }

    }

}