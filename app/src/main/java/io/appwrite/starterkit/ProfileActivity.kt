package io.appwrite.starterkit

import android.os.Bundle

class ProfileActivity : BaseBottomNavActivity() {

    // Provide the menu ID for this specific activity.
    override val currentNavId = R.id.nav_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set the title for the action bar.
        supportActionBar?.title = "Your Profile"
    }
}
