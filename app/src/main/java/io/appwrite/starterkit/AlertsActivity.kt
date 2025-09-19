package io.appwrite.starterkit

import android.os.Bundle

// 1. Inherit from BaseBottomNavActivity
class AlertsActivity : BaseBottomNavActivity() {

    // 2. Specify the current page's navigation ID
    override val currentNavId = R.id.nav_alerts

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 3. Set the content view, which will be injected into the base layout
        setContentView(R.layout.activity_alerts)

        // 4. All duplicated navigation and back-press logic has been removed.
        //    It is now handled automatically by the base class.

        // 5. Keep only the logic specific to this screen
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Market Alerts"
    }

    // The handleBottomNavigation, OnBackPressedCallback, and onSupportNavigateUp
    // methods have all been deleted as they are now centralized in the base class.
}
