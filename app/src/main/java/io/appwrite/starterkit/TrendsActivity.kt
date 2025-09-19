package io.appwrite.starterkit

import android.os.Bundle

class TrendsActivity : BaseBottomNavActivity() {

    // Provide the menu ID for this specific activity.
    override val currentNavId = R.id.nav_trends

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trends)

        // Set the title for the action bar.
        supportActionBar?.title = "Market Trends"

        // All navigation logic is now handled by the base class!
    }
}
