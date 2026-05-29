package com.tecsup.app4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.app4.ui.theme.App4Theme

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

@Composable
fun VentasScreen(viewModel: VentasViewModel) {
    // collectAsStateWithLifecycle observa el StateFlow y se cancela automáticamente
    // cuando el Composable no está visible — manejo de lifecycle automático
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val productosDisponibles = remember {
        listOf(
            Producto("Pan", 2.50),
            Producto("Leche", 4.00),
            Producto("Arroz", 3.50),
            Producto("Huevos", 6.00)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // Encabezado positivo
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E7D32))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✅ CON CORRUTINAS ✅",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Text(
                text = "✅ ViewModel + StateFlow + Corrutinas",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Text(
                text = "viewModelScope.launch { delay(2000) } reemplaza todo el boilerplate " +
                        "de Thread + Handler. El código es conciso, seguro y lifecycle-aware.",
                modifier = Modifier.padding(horizontal = 12.dp, bottom = 8.dp),
                color = Color(0xFF1B5E20),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("🛒 Tienda Tecsup", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("(Con Corrutinas)", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(12.dp))

        // Botón demo — funciona durante el proceso porque delay() no bloquea el main thread
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "👇 ¡Presióname MIENTRAS confirma!",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                Text(
                    "A diferencia de App 3, este botón SÍ funciona durante el proceso.\n" +
                            "delay() suspende la corrutina sin bloquear el main thread.",
                    fontSize = 11.sp,
                    color = Color(0xFF1565C0)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    // Siempre habilitado — incluso cuando procesando = true
                    onClick = { viewModel.incrementarContador() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("UI ACTIVA — Clicks: ${uiState.clickCounter}")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Productos disponibles
        Text("Productos disponibles:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(6.dp))

        productosDisponibles.forEach { producto ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(producto.nombre, fontWeight = FontWeight.Medium)
                    Text("S/ %.2f".format(producto.precio), color = Color(0xFF1B5E20))
                    IconButton(
                        onClick = { viewModel.agregarProducto(producto) },
                        enabled = !uiState.procesando
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color(0xFF2E7D32))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Carrito
        Text("Carrito (${uiState.carrito.size} items):", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(6.dp))

        if (uiState.carrito.isEmpty()) {
            Text("(vacío)", color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
        } else {
            uiState.carrito.forEach { producto ->
                Text(
                    "• ${producto.nombre} — S/ %.2f".format(producto.precio),
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total:", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("S/ %.2f".format(uiState.total), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B5E20))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { viewModel.confirmarPedido() },
            enabled = !uiState.procesando,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
        ) {
            if (uiState.procesando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (uiState.procesando) "Procesando..." else "Confirmar Pedido")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                text = uiState.mensaje,
                modifier = Modifier.padding(12.dp),
                color = if (uiState.mensaje.startsWith("✅")) Color(0xFF1B5E20)
                        else if (uiState.mensaje.startsWith("⚠")) Color(0xFFE65100)
                        else if (uiState.mensaje.startsWith("⏳")) Color(0xFF1565C0)
                        else Color.Gray,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("📚 Ventajas de Corrutinas:", fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "✅ Código secuencial y legible (sin callbacks)\n" +
                            "✅ delay() no bloquea el hilo → UI siempre responde\n" +
                            "✅ viewModelScope cancela automáticamente (sin memory leaks)\n" +
                            "✅ collectAsStateWithLifecycle → lifecycle-aware\n" +
                            "✅ Manejo de errores con try/catch normal\n" +
                            "✅ Fácil encadenar operaciones asíncronas",
                    color = Color(0xFF5D4037),
                    fontSize = 13.sp
                )
            }
        }
    }
}
