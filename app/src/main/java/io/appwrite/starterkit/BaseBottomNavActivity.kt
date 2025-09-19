package io.appwrite.starterkit

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
// 1. CORRECT the import to your app's HomeActivity
import io.appwrite.starterkit.HomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * A base activity for handling common bottom navigation logic.
 * Activities for tabs other than Home should inherit from this.
 */
abstract class BaseBottomNavActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    /**
     * Subclasses must override this to provide their corresponding menu item ID.
     */
    protected abstract val currentNavId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 2. CRITICAL: The base activity now loads the master layout that CONTAINS the navbar.
        super.setContentView(R.layout.activity_base)

        // 3. Now that the layout is set, we can safely find the view.
        bottomNavigation = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            handleBottomNavigation(item)
        }

        // Centralized handler for the hardware back button
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Navigate to HomeActivity and clear the entire back stack.
                val intent = Intent(this@BaseBottomNavActivity, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
        })
    }

    // 4. CRITICAL: This special override intercepts the child's setContentView call.
    // Instead of replacing the screen, it injects the child's layout into our FrameLayout.
    override fun setContentView(@LayoutRes layoutResID: Int) {
        val contentFrame = findViewById<FrameLayout>(R.id.content_frame)
        layoutInflater.inflate(layoutResID, contentFrame, true)
    }

    override fun onStart() {
        super.onStart()
        // Highlight the correct menu item for the current screen.
        bottomNavigation.menu.findItem(currentNavId)?.isChecked = true
    }

    private fun handleBottomNavigation(item: MenuItem): Boolean {
        // If the selected item is the current one, do nothing.
        if (item.itemId == currentNavId) {
            return false
        }

        val intent = when (item.itemId) {
            R.id.nav_home -> Intent(this, HomeActivity::class.java)
            R.id.nav_trends -> Intent(this, TrendsActivity::class.java)
            R.id.nav_insights -> Intent(this, InsightsActivity::class.java)
            R.id.nav_alerts -> Intent(this, AlertsActivity::class.java)
            R.id.nav_profile -> Intent(this, ProfileActivity::class.java)
            else -> null
        }

        intent?.let {
            // These flags ensure that pressing back doesn't go through a long history of tab switches.
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }

        return true
    }

    /**
     * Centralized handler for the action bar's "Up" button.
     */
    override fun onSupportNavigateUp(): Boolean {
        // Trigger the same behavior as the hardware back button.
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
