package io.appwrite.starterkit

import android.app.Application
import android.util.Log
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.starterkit.constants.AppwriteConfig

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // THIS TRY-CATCH BLOCK IS OUR DEBUGGING TOOL
        try {
            // Create a new Appwrite client
            val client = Client(this)
                .setEndpoint(AppwriteConfig.APPWRITE_PUBLIC_ENDPOINT)
                .setProject(AppwriteConfig.APPWRITE_PROJECT_ID)
                .setSelfSigned(true) // Use only for development against a local Appwrite server

            // Initialize the global Appwrite object with the configured services
            Appwrite.client = client
            Appwrite.account = Account(client)
            Appwrite.databases = Databases(client)

            Log.i("AppwriteInit", "Appwrite SDK initialized successfully.")

        } catch (e: Throwable) {
            // If anything goes wrong during initialization, this will catch it
            // and print the error to logcat, which will finally tell us why it's crashing.
            Log.e("AppwriteInit", "Failed to initialize Appwrite SDK: ${e.message}", e)
        }
    }
}
