@file:Suppress("DEPRECATION")

package com.bangkit.ripad.ui.loginregis

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.ripad.MainActivity
import com.bangkit.ripad.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class SignInActivity : AppCompatActivity() {

    private lateinit var emailAddressInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var signInBtn : Button
    private var rcSignIn = 1
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnSignGoogle : LinearLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)

        auth = FirebaseAuth.getInstance()
        emailAddressInput = findViewById(R.id.emailaddressfield)
        passwordInput = findViewById(R.id.passwordfield)
        signInBtn = findViewById(R.id.btnsignin)
        btnSignGoogle = findViewById(R.id.Signin_google)

        signInBtn.setOnClickListener {
            val emailAddress = emailAddressInput.text.toString()
            val password = passwordInput.text.toString()

            // Cek apakah email dan password terisi
            when {
                emailAddress.isEmpty() -> {
                    Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                }
                !android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches() -> {
                    Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
                password.length < 8 -> {
                    Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    auth.signInWithEmailAndPassword(emailAddress,password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null) {
                                    if(user.isEmailVerified){
                                        Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish() // Tutup SignInActivity agar tidak bisa kembali dengan tombol Back
                                    }else{
                                        Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }

                        }

                    // Jika semua validasi lolos
                    Log.i("Login Success", "Email: $emailAddress, Password: $password")
                    // Pindah ke MainActivity

                }
            }
        }

        val tvSignUp = findViewById<TextView>(R.id.tvsignup)
        tvSignUp.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        btnSignGoogle.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, rcSignIn)
        }
        setupPasswordVisibilityToggle()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPasswordVisibilityToggle() {
        // Default visibility state
        var isPasswordVisible = false

        passwordInput.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Hitung posisi ikon drawable
                val drawableEnd = passwordInput.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (passwordInput.right - drawableEnd.bounds.width())) {
                    // Toggle visibility
                    isPasswordVisible = !isPasswordVisible
                    if (isPasswordVisible) {
                        // Tampilkan password
                        passwordInput.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        passwordInput.transformationMethod = null // Jangan lupa reset!
                        passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_24, 0)
                    } else {
                        // Sembunyikan password
                        passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        passwordInput.transformationMethod = PasswordTransformationMethod.getInstance()
                        passwordInput.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off_24, 0)
                    }
                    // Pindahkan kursor ke akhir teks
                    passwordInput.setSelection(passwordInput.text.length)
                    return@setOnTouchListener true
                }
            }
            false
        }

    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == rcSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                val errorMessage = when (e.statusCode) {
                    GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Sign in cancelled"
                    GoogleSignInStatusCodes.SIGN_IN_FAILED -> "Sign in failed"
                    GoogleSignInStatusCodes.SIGN_IN_REQUIRED -> "Sign in required"
                    else -> "Error code: ${e.statusCode}"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome, ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

}