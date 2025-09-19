package io.appwrite.starterkit

import android.os.Bundle

class InsightsActivity : BaseBottomNavActivity() {

    // Provide the menu ID for this specific activity.
    override val currentNavId = R.id.nav_insights

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insights)

        // Set the title for the action bar.
        supportActionBar?.title = "Market Insights"

        // All navigation logic (bottom bar, back button, up button)
        // is now handled by the BaseBottomNavActivity.
    }
}
