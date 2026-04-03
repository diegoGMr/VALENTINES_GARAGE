package com.msn.valentinesgarage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.msn.valentinesgarage.activities.authenticationActivity.LoginActivity
import com.msn.valentinesgarage.theme.ValentinesGarageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ValentinesGarageTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LoginActivity()
                }
            }
        }
    }
}

