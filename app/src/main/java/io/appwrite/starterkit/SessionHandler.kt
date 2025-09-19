package io.appwrite.starterkit

import android.content.Context
import android.content.Intent
import android.util.Log
import io.appwrite.exceptions.AppwriteException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext // <-- Add this import

/**
 * A singleton object to handle user session logic, such as checking for an
 * active session on app startup and handling user logout. This makes the session
 * management logic reusable across the entire application.
 */
object SessionHandler {

    /**
     * Checks if a user session is currently active.
     * If a session exists, it navigates the user directly to the HomeActivity.
     * If not, it executes a provided callback function, allowing the calling
     * activity (e.g., LoginActivity) to display its UI.
     *
     * @param context The application context, used for starting a new activity.
     * @param onNotLoggedIn A nullable lambda function that is invoked only if no active session is found.
     */
    fun checkSession(context: Context, onNotLoggedIn: (() -> Unit)? = null) {
        // Launch a coroutine on a background thread (Dispatchers.IO) to perform the network request
        // without blocking the main UI thread.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Attempt to fetch the current user's data from Appwrite.
                // If this call succeeds, it means there is an active session.
                val user = Appwrite.account.get()
                Log.d("SessionHandler", "User already logged in: ${user.id}")

                // If a user is logged in, switch to the Main thread to start the activity.
                withContext(Dispatchers.Main) {
                    val intent = Intent(context, HomeActivity::class.java).apply {
                        // These flags clear the entire activity stack, so the user cannot press
                        // the back button to return to the login or registration screens.
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                }

            } catch (e: AppwriteException) {
                // This catch block is executed if Appwrite.account.get() fails,
                // which is the expected behavior when there is no active session.
                Log.d("SessionHandler", "No active session found.")

                // CRITICAL FIX: Switch to the Main thread before invoking the UI-related callback.
                withContext(Dispatchers.Main) {
                    // This signals the calling activity (e.g., RegistrationActivity)
                    // that it is safe to set up its own UI.
                    onNotLoggedIn?.invoke()
                }
            }
        }
    }

    /**
     * Logs the current user out by deleting their session and navigates them back to the LoginActivity.
     *
     * @param context The application context, used for starting a new activity.
     */
    fun logout(context: Context) {
        // Launch a coroutine on a background thread for the network request.
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Tell the Appwrite SDK to delete the session identified as "current".
                Appwrite.account.deleteSession("current")
                Log.d("SessionHandler", "Logged out successfully")

                // After successful logout, switch to the main thread to start the activity.
                withContext(Dispatchers.Main) {
                    val intent = Intent(context, LoginActivity::class.java).apply {
                        // These flags are crucial for clearing the user's session history,
                        // preventing them from navigating back into a secured part of the app.
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                }
            } catch (e: AppwriteException) {
                // If the logout process fails (e.g., due to a network error), log the exception.
                Log.e("SessionHandler", "Failed to logout", e)
                // In a production app, you might want to show a Toast message here
                // by switching to the main context first.
            }
        }
    }
}
