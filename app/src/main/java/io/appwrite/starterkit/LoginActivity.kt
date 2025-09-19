package io.appwrite.starterkit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var loadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SessionHandler.checkSession(this) {
            setContentView(R.layout.activity_login)

            emailEditText = findViewById(R.id.emailEditText)
            passwordEditText = findViewById(R.id.passwordEditText)
            loginButton = findViewById(R.id.loginButton)
            loadingProgressBar = findViewById(R.id.loadingProgressBar)
            val googleLoginButton: Button = findViewById(R.id.googleLoginButton)
            val registerLink: TextView = findViewById(R.id.registerLink)

            loginButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (validateInputs(email, password)) {
                    loginUser(email, password)
                }
            }

            googleLoginButton.setOnClickListener {
                Toast.makeText(this, "Google SSO coming soon!", Toast.LENGTH_SHORT).show()
            }

            registerLink.setOnClickListener {
                navigateToRegistration()
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        loadingProgressBar.visibility = View.VISIBLE
        loginButton.isEnabled = false

        lifecycleScope.launch {
            try {
                Appwrite.account.createEmailPasswordSession(email, password)
                Log.d("LoginActivity", "Login successful!")

                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    navigateToHomeActivity()
                }

            } catch (e: AppwriteException) {
                Log.e("LoginActivity", "Failed to login", e)
                runOnUiThread {
                    Toast.makeText(this@LoginActivity, "Login failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    loginButton.isEnabled = true
                }
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "A valid email is required"
            return false
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            return false
        }
        return true
    }

    private fun navigateToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToRegistration() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
    }
}
