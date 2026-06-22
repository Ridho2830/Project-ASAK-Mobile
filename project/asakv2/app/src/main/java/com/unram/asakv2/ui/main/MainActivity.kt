package com.unram.asakv2.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unram.asakv2.R
import com.unram.asakv2.achievement.AchievementUnlockManager
import com.unram.asakv2.achievement.AchievementViewModel
import com.unram.asakv2.ui.achievement.AchievementUnlockDialog

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private val achievementVm: AchievementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setupWithNavController(navController)
        
        bottomNav.menu.findItem(R.id.arFragment)?.let { arItem ->
            arItem.icon?.let { icon ->
                val wrapDrawable = androidx.core.graphics.drawable.DrawableCompat.wrap(icon).mutate()
                androidx.core.graphics.drawable.DrawableCompat.setTintList(wrapDrawable, null)
                arItem.icon = wrapDrawable
            }
        }
        
        bottomNav.post {
            val arMenuItem = bottomNav.findViewById<android.view.View>(R.id.arFragment)
            if (arMenuItem != null) {
                arMenuItem.scaleX = 1.3f
                arMenuItem.scaleY = 1.3f
            }
        }

        AchievementUnlockManager.pendingUnlock.observe(this) { id ->
            if (id == null || AchievementUnlockManager.isInQuiz) return@observe
            val currentFragment = navHostFragment.childFragmentManager.primaryNavigationFragment
            if (currentFragment != null && currentFragment.isAdded) {
                AchievementUnlockDialog.newInstance(id)
                    .show(currentFragment.childFragmentManager, "unlock_dialog")
            }
        }

        achievementVm.hasUnclaimed.observe(this) { has ->
            setAchievementDot(has)
        }
    }

    fun setAchievementDot(show: Boolean) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav) ?: return
        val badge = bottomNav.getOrCreateBadge(R.id.achievementFragment)
        badge.isVisible = show
        if (show) {
            badge.backgroundColor = android.graphics.Color.RED
            badge.clearNumber()   
        }
    }
}
