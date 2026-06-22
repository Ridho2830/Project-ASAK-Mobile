package com.unram.asakv2.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.unram.asakv2.R
import com.unram.asakv2.ui.auth.LoginActivity
import com.unram.asakv2.ui.main.MainActivity
import com.unram.asakv2.utils.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    companion object {
        private const val SPLASH_DELAY_MS = 2000L
    }

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sessionManager = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            checkAuthAndNavigate()
        }, SPLASH_DELAY_MS)
    }

    private fun checkAuthAndNavigate() {
        lifecycleScope.launch {
            val isLoggedIn = sessionManager.isLoggedIn.first()
            if (isLoggedIn) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }
}
