package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.udacity.project4.locationreminders.RemindersActivity

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var mAuth:  FirebaseAuth
    companion object {
        const val SIGN_IN_RESULT_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//         TODO: Implement the create account and sign in using FirebaseUI, use sign in using email and sign in using Google
        binding = DataBindingUtil.setContentView(this,
        R.layout.activity_authentication)
        binding.lifecycleOwner = this
        mAuth =FirebaseAuth.getInstance()

//          TODO: If the user was authenticated, send him to RemindersActivity
         if(mAuth.currentUser!=null){
             beginReminderActivity()
         }


        binding.signInBtn.setOnClickListener{
            launchSignIn()
        }

//          TODO: a bonus is to customize the sign in flow to look nice using :
        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout

    }

    private fun launchSignIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),AuthUI.IdpConfig.GoogleBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.AuthUI)
                 .build(),
            SIGN_IN_RESULT_CODE
        )

    }

    //listen to the result of signing in
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SIGN_IN_RESULT_CODE){
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK){
                //user successfully signed in
                Toast.makeText(this, "Successfully signed in", Toast.LENGTH_SHORT).show()
                beginReminderActivity()
            }else
            {
                Toast.makeText(this, "Sign in unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun beginReminderActivity() {
        val intent = Intent(this,RemindersActivity::class.java)
         startActivity(intent)
         finish()
    }



}
