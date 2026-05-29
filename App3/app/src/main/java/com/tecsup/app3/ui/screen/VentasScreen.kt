package com.tecsup.app3.ui.screen

import android.os.Handler
import android.os.Looper
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
import com.tecsup.app3.model.Producto

@Composable
fun VentasScreen() {
    // Lista de productos disponibles en la tienda
    val productosDisponibles = remember {
        listOf(
            Producto("Pan", 2.50),
            Producto("Leche", 4.00),
            Producto("Arroz", 3.50),
            Producto("Huevos", 6.00)
        )
    }

    var carrito by remember { mutableStateOf(listOf<Producto>()) }
    var procesando by remember { mutableStateOf(false) }
    var mensaje by remember { mutableStateOf("Agrega productos y confirma tu pedido.") }
    var mensajeColor by remember { mutableStateOf(Color.Gray) }

    // Contador que demostrará que la UI se BLOQUEA mientras el Thread trabaja
    // (a diferencia de App2 con Handler, este no puede actualizarse durante el proceso
    //  porque el botón queda deshabilitado y el state no cambia visualmente)
    var clickCounter by remember { mutableIntStateOf(0) }

    val total = carrito.sumOf { it.precio }

    // Handler necesario para volver al main thread desde el Thread
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    fun confirmarPedido() {
        if (carrito.isEmpty()) {
            mensaje = "⚠ El carrito está vacío"
            mensajeColor = Color(0xFFFF6F00)
            return
        }

        // PROBLEMA: necesitamos deshabilitar todo mientras procesamos
        // Con Thread clásico no hay manera elegante de manejar el estado
        procesando = true
        mensaje = "⏳ Procesando pedido... (código verboso)"
        mensajeColor = Color(0xFFE65100)

        // Enfoque verboso con Thread clásico:
        // 1. Crear el Thread manualmente
        // 2. Manejar try/catch dentro del Thread
        // 3. Recordar llamar a Handler para volver al main thread
        // 4. Recordar actualizar TODOS los estados necesarios en Handler.post{}
        // 5. Sin manejo de cancelación, sin lifecycle awareness
        Thread {
            // Simulación de procesamiento pesado (guardado en base de datos, validaciones, etc.)
            try {
                // Thread.sleep bloquea este hilo secundario por 2 segundos
                // Si estuviera en el main thread, congelaría la UI completamente
                Thread.sleep(2000)

                // VERBOSO: Tenemos que usar Handler manualmente para actualizar la UI
                // No podemos simplemente escribir "mensaje = ..." aquí porque
                // estamos en un hilo secundario y Compose no lo permite
                mainHandler.post {
                    // Actualizar estado 1
                    mensaje = "✅ Pedido confirmado por S/%.2f\n(Procesado en Thread secundario)".format(total)
                    mensajeColor = Color(0xFF1B5E20)
                    // Actualizar estado 2
                    carrito = emptyList()
                    // Actualizar estado 3
                    procesando = false
                    // PROBLEMA: Si hubiera más estados que actualizar,
                    // este bloque se volvería cada vez más largo y difícil de mantener
                }
            } catch (e: InterruptedException) {
                // VERBOSO: Manejar excepciones también requiere Handler
                mainHandler.post {
                    mensaje = "❌ Error: procesamiento interrumpido"
                    mensajeColor = Color.Red
                    procesando = false
                }
            } catch (e: Exception) {
                // Cualquier otro error también necesita su propio Handler.post{}
                mainHandler.post {
                    mensaje = "❌ Error inesperado: ${e.message}"
                    mensajeColor = Color.Red
                    procesando = false
                }
            }
            // Nota: Si el Activity se destruye mientras este Thread corre,
            // el Handler.post{} intentará actualizar una UI que ya no existe → memoria leak
            // Las corrutinas (App4) manejan esto automáticamente con lifecycle
        }.start() // No olvidemos llamar .start() — otro detalle manual
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3E0))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        // Encabezado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE65100))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚠ SIN CORRUTINAS ⚠",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2))
        ) {
            Text(
                text = "⚠ Código verboso con Thread clásico",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
            )
            Text(
                text = "Usamos Thread + Handler para operaciones asíncronas. " +
                        "El código es largo, propenso a errores y difícil de mantener. " +
                        "Compara este código con App 4 (corrutinas).",
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                color = Color(0xFFBF360C),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("🛒 Tienda Tecsup", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("(Sin Corrutinas)", color = Color(0xFFE65100), fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(12.dp))

        // Sección: botón de demostración de UI bloqueada
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2))
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "🔒 Contador bloqueado durante proceso",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFC62828),
                    fontSize = 13.sp
                )
                Text(
                    "Sin corrutinas, este botón queda deshabilitado durante el proceso.\n" +
                            "En App 4 con corrutinas, sí puedes clickearlo.",
                    fontSize = 11.sp,
                    color = Color(0xFFC62828)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    // LIMITACIÓN: deshabilitamos el botón porque no podemos
                    // garantizar thread-safety sin corrutinas fácilmente
                    onClick = { clickCounter++ },
                    enabled = !procesando,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC62828),
                        disabledContainerColor = Color(0xFFEF9A9A)
                    )
                ) {
                    Text(if (procesando) "🔒 BLOQUEADO: $clickCounter" else "Clicks: $clickCounter")
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
                        onClick = { carrito = carrito + producto },
                        enabled = !procesando
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color(0xFFE65100))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Carrito
        Text("Carrito (${carrito.size} items):", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(6.dp))

        if (carrito.isEmpty()) {
            Text("(vacío)", color = Color.Gray, modifier = Modifier.padding(vertical = 4.dp))
        } else {
            carrito.forEach { producto ->
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
                Text("S/ %.2f".format(total), fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B5E20))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { confirmarPedido() },
            enabled = !procesando,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
        ) {
            if (procesando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (procesando) "Procesando..." else "Confirmar Pedido")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Text(
                text = mensaje,
                modifier = Modifier.padding(12.dp),
                color = mensajeColor,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nota educativa sobre el código verboso
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("📚 Problemas del Thread clásico:", fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "❌ Código verboso: Thread{}.start() + Handler.post{}\n" +
                            "❌ Sin cancelación automática\n" +
                            "❌ Sin lifecycle awareness (posible memory leak)\n" +
                            "❌ Difícil encadenar operaciones asíncronas\n" +
                            "❌ Manejo de errores complicado\n\n" +
                            "✅ Solución: Corrutinas (ver App 4)",
                    color = Color(0xFF5D4037),
                    fontSize = 13.sp
                )
            }
        }
    }
}
