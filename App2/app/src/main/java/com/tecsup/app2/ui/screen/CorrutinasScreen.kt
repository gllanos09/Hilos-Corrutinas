package com.tecsup.app2.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Pedido(val nombre: String, val emoji: String, val tiempoMs: Long)

@Composable
fun CorrutinasScreen() {
    var contador by remember { mutableIntStateOf(0) }

    // Estado de cada pedido en la cocina
    var estadoPizza    by remember { mutableStateOf("⏳ esperando...") }
    var estadoBurger   by remember { mutableStateOf("⏳ esperando...") }
    var estadoEnsalada by remember { mutableStateOf("⏳ esperando...") }

    var estadoSin by remember { mutableStateOf("Presiona para ejecutar") }
    var cocinando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val pedidos = listOf(
        Pedido("Pizza",       "🍕", 3000),
        Pedido("Hamburguesa", "🍔", 2000),
        Pedido("Ensalada",    "🥗", 1000)
    )

    fun resetPedidos() {
        estadoPizza    = "⏳ esperando..."
        estadoBurger   = "⏳ esperando..."
        estadoEnsalada = "⏳ esperando..."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("🍽️ Cocina Tecsup", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("Corrutinas vs Sin corrutinas", fontSize = 14.sp, color = Color.Gray)

        // Contador — medidor de vida de la UI
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("¿La UI sigue respondiendo?", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = { contador++ }) {
                    Text("Tócame mientras cocina → $contador")
                }
            }
        }

        HorizontalDivider()

        // --- SIN CORRUTINAS: un cocinero para todo, secuencial ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("❌ SIN corrutinas", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 16.sp)
                Text(
                    "Un solo cocinero — termina un plato y recién empieza el otro.\nUI congelada. Total: 6 segundos.",
                    color = Color.Gray, fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // Secuencial y bloqueante: un plato a la vez
                        estadoSin = "🍕 Preparando Pizza..."
                        Thread.sleep(pedidos[0].tiempoMs)
                        estadoSin = "🍔 Preparando Hamburguesa..."
                        Thread.sleep(pedidos[1].tiempoMs)
                        estadoSin = "🥗 Preparando Ensalada..."
                        Thread.sleep(pedidos[2].tiempoMs)
                        estadoSin = "✅ Todos listos — tardó 6s (3+2+1, uno a la vez)"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Cocinar SIN corrutinas")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(estadoSin, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
        }

        // --- CON CORRUTINAS: 3 cocineros en paralelo ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("✅ CON corrutinas", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 16.sp)
                Text(
                    "3 cocineros simultáneos — cada plato en paralelo.\nUI viva. Total: solo 3 segundos.",
                    color = Color.Gray, fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (cocinando) return@Button
                        cocinando = true
                        resetPedidos()

                        // Cada pedido es una corrutina independiente — corren AL MISMO TIEMPO
                        scope.launch {
                            estadoPizza = "🔥 cocinando..."
                            delay(pedidos[0].tiempoMs)
                            estadoPizza = "✅ lista (3s)"
                            if (estadoBurger.startsWith("✅") && estadoEnsalada.startsWith("✅"))
                                cocinando = false
                        }

                        scope.launch {
                            estadoBurger = "🔥 cocinando..."
                            delay(pedidos[1].tiempoMs)
                            estadoBurger = "✅ lista (2s)"
                            if (estadoPizza.startsWith("✅") && estadoEnsalada.startsWith("✅"))
                                cocinando = false
                        }

                        scope.launch {
                            estadoEnsalada = "🔥 cocinando..."
                            delay(pedidos[2].tiempoMs)
                            estadoEnsalada = "✅ lista (1s)"
                            if (estadoPizza.startsWith("✅") && estadoBurger.startsWith("✅"))
                                cocinando = false
                        }
                    },
                    enabled = !cocinando,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text(if (cocinando) "Cocinando..." else "Cocinar CON corrutinas")
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Pedidos con estado individual
                pedidos.forEachIndexed { index, pedido ->
                    val estado = when (index) {
                        0 -> estadoPizza
                        1 -> estadoBurger
                        else -> estadoEnsalada
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "${pedido.emoji} ${pedido.nombre}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                        Text(
                            estado,
                            fontSize = 13.sp,
                            color = if (estado.startsWith("✅")) Color(0xFF2E7D32)
                                    else if (estado.startsWith("🔥")) Color(0xFFE65100)
                                    else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Observa: la Ensalada termina primero (1s), luego la Hamburguesa (2s), luego la Pizza (3s) — todas corriendo a la vez",
                    fontSize = 11.sp,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}
