package io.appwrite.starterkit

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This installs the splash screen and MUST be called before super.onCreate()
        // but for this specific theme issue, we can call it after.
        // The key is to remove setContentView.
        installSplashScreen()


        // A 2.5-second delay to show the splash screen
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an Intent to start RegistrationActivity
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)

            // Finish MainActivity so the user can't navigate back to the splash screen
            finish()
        }, 2500) // 2500 milliseconds = 2.5 seconds
    }
}
