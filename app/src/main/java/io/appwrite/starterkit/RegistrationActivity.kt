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
import io.appwrite.ID
import io.appwrite.Permission
import io.appwrite.Role
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {

    // Define UI elements at the class level for easy access throughout the activity.
    private lateinit var nameEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var registerButton: Button
    private lateinit var loadingProgressBar: ProgressBar

    // --- Appwrite Configuration Constants ---
    // Store Appwrite-specific IDs as constants to avoid typos and make them easy to update.
    // These values must match the IDs in your Appwrite Cloud console.
    private val DATABASE_ID = "68c7e23d0028e08f405e"
    private val PROFILES_COLLECTION_ID = "Profiles" // Note: Collection IDs are case-sensitive.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Before setting up the UI, check if a user session already exists.
        // If a session is found, SessionHandler will redirect to HomeActivity automatically.
        // The code inside this lambda only runs if there is NO active session.
        SessionHandler.checkSession(this) {
            // Set the layout for this activity.
            setContentView(R.layout.activity_registration)

            // Initialize all the UI elements from the layout file.
            nameEditText = findViewById(R.id.nameEditText)
            emailEditText = findViewById(R.id.emailEditText)
            passwordEditText = findViewById(R.id.passwordEditText)
            confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText)
            registerButton = findViewById(R.id.registerButton)
            loadingProgressBar = findViewById(R.id.loadingProgressBar)
            val googleRegisterButton: Button = findViewById(R.id.googleRegisterButton)
            val loginLink: TextView = findViewById(R.id.loginLink)

            // Set a click listener for the main registration button.
            registerButton.setOnClickListener {
                // Retrieve the user's input from the text fields.
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                // If all inputs are valid, proceed with the user registration process.
                if (validateInputs(name, email, password, confirmPassword)) {
                    registerUser(name, email, password)
                }
            }

            // Set a click listener for the Google Sign-In button (placeholder).
            googleRegisterButton.setOnClickListener {
                Toast.makeText(this, "Google SSO coming soon!", Toast.LENGTH_SHORT).show()
            }

            // Set a click listener for the text link to navigate to the Login screen.
            loginLink.setOnClickListener {
                navigateToLogin()
            }
        }
    }

    /**
     * Handles the multi-step user registration process with Appwrite.
     * This function is executed within a coroutine to handle network operations off the main thread.
     */
    private fun registerUser(name: String, email: String, password: String) {
        // Provide immediate visual feedback: show the progress bar and disable the button.
        loadingProgressBar.visibility = View.VISIBLE
        registerButton.isEnabled = false

        // Launch a coroutine using the activity's lifecycle scope.
        lifecycleScope.launch {
            try {
                // Step 1: Create a new user in the Appwrite Authentication service.
                val user = Appwrite.account.create(
                    userId = ID.unique(), // Let Appwrite generate a unique ID for the user.
                    email = email,
                    password = password,
                    name = name
                )
                Log.d("RegistrationActivity", "Step 1/3: User created successfully: ${user.id}")

                // Step 2: Immediately create a session for the new user.
                // This is crucial because creating the database document requires an authenticated user.
                Appwrite.account.createEmailPasswordSession(email, password)
                Log.d("RegistrationActivity", "Step 2/3: Session created.")

                // Prepare the data to be stored in the user's profile document.
                val profileData = mapOf(
                    "userId" to user.id, // Link this document to the authenticated user's ID.
                    "name" to user.name
                )

                // Define document-level permissions.
                // This ensures that only the user themselves can access or modify their own profile data.
                val permissions = listOf(
                    Permission.read(Role.user(user.id)),   // The user can read their own document.
                    Permission.update(Role.user(user.id)), // The user can update their own document.
                    Permission.delete(Role.user(user.id))  // The user can delete their own document.
                )

                // Step 3: Create the profile document in the 'Profiles' collection.
                Appwrite.databases.createDocument(
                    databaseId = DATABASE_ID,
                    collectionId = PROFILES_COLLECTION_ID,
                    documentId = ID.unique(), // It's best practice to use a unique ID for the document itself.
                    data = profileData,
                    permissions = permissions // Apply the defined permissions.
                )
                Log.d("RegistrationActivity", "Step 3/3: Profile document created.")

                // If all steps succeed, switch to the UI thread to show a success message and navigate.
                runOnUiThread {
                    Toast.makeText(this@RegistrationActivity, "Registration successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegistrationActivity, HomeActivity::class.java).apply {
                        // Clear the activity stack to prevent the user from navigating back to the auth flow.
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish() // Close the RegistrationActivity.
                }

            } catch (e: AppwriteException) {
                // Catch any errors from Appwrite (e.g., user already exists, network issue).
                Log.e("RegistrationActivity", "Failed to register user", e)
                runOnUiThread {
                    // Show a descriptive error message to the user.
                    Toast.makeText(this@RegistrationActivity, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                // This block always executes, regardless of whether the try block succeeded or failed.
                runOnUiThread {
                    // Hide the progress bar and re-enable the button, returning the UI to its normal state.
                    loadingProgressBar.visibility = View.GONE
                    registerButton.isEnabled = true
                }
            }
        }
    }

    /**
     * Validates the user input fields before making a network request.
     * @return `true` if all inputs are valid, `false` otherwise.
     */
    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            nameEditText.error = "Name is required"
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Valid email is required"
            return false
        }
        // Appwrite has a minimum password length requirement.
        if (password.length < 8) {
            passwordEditText.error = "Password must be at least 8 characters"
            return false
        }
        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            return false
        }
        // If all checks pass, return true.
        return true
    }

    /**
     * Navigates the user to the LoginActivity.
     */
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        // We don't finish() here, so the user can press 'back' to return to registration.
    }
}
