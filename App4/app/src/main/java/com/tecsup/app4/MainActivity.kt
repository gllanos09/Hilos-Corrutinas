package com.tecsup.app4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.app4.ui.screen.VentasScreen
import com.tecsup.app4.ui.theme.App4Theme
import com.tecsup.app4.viewmodel.VentasViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App4Theme {
                // viewModel() crea o recupera el ViewModel con el lifecycle correcto
                val ventasViewModel: VentasViewModel = viewModel()
                VentasScreen(viewModel = ventasViewModel)
            }
        }
    }
}
