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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest


class SignUpActivity : AppCompatActivity() {

    private lateinit var usernameInput : EditText
    private lateinit var emailAddressInput : EditText
    private lateinit var passwordInput : EditText
    private lateinit var signupBtn : Button
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var btnSignGoogle : LinearLayout
    private var rcSignIn = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()
        usernameInput = findViewById(R.id.usernamefield)
        emailAddressInput = findViewById(R.id.emailaddressfield)
        passwordInput = findViewById(R.id.passwordfield)
        signupBtn = findViewById(R.id.btnsignup)
        btnSignGoogle = findViewById(R.id.Signup_google)
        signupBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val emailAddress = emailAddressInput.text.toString()
            val password = passwordInput.text.toString()

            // Validasi input
            when {
                username.isEmpty() -> {
                    Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                }
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
                    Toast.makeText(this, "Password minimum 8 characters", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Simpan data dan pindah ke halaman berikutnya


                    val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("USER_NAME", username)
                    editor.putString("USER_EMAIL", emailAddress)
                    editor.putString("USER_PASSWORD", password)
                    editor.apply()

                    Log.i("SignUp", "User registered: $username, $emailAddress")
                    signUpUser(username,emailAddress,password)

                }
            }
        }

        val tvSignIn = findViewById<TextView>(R.id.tvsignin)
        tvSignIn.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
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
    private fun signUpUser(username : String,email: String, password: String) {
        // Lakukan pendaftaran pengguna di Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registrasi berhasil, kirim email verifikasi
                    sendEmailVerification(task.result?.user)
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(username)
                        .build()
                    user?.updateProfile(profileUpdates)
                } else {
                    // Registrasi gagal, tampilkan pesan kesalahan
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun sendEmailVerification(user: FirebaseUser?) {
        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Email verifikasi berhasil dikirim
                Toast.makeText(this, "Email Verification Sent", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
            } else {
                // Gagal mengirim email verifikasi
                Toast.makeText(this, "Failed To send Email Verification ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}