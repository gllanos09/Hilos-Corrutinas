package com.tecsup.app2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.app2.ui.screen.BancoScreen
import com.tecsup.app2.ui.theme.App2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App2Theme {
                BancoScreen()
            }
        }
    }
}
