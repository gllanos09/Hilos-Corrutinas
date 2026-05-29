package com.tecsup.app2.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Colores temáticos de cocina
private val Madera       = Color(0xFF5D4037)
private val MaderaClara  = Color(0xFF8D6E63)
private val Crema        = Color(0xFFFFF8E1)
private val CremaOscura  = Color(0xFFFFECB3)
private val Fuego        = Color(0xFFFF6F00)
private val Verde        = Color(0xFF2E7D32)
private val Rojo         = Color(0xFFC62828)

data class Pedido(val nombre: String, val emoji: String, val tiempoMs: Long)

@Composable
fun CorrutinasScreen() {
    var contador       by remember { mutableIntStateOf(0) }
    var estadoPizza    by remember { mutableStateOf("esperando") }
    var estadoBurger   by remember { mutableStateOf("esperando") }
    var estadoEnsalada by remember { mutableStateOf("esperando") }
    var estadoSin      by remember { mutableStateOf("") }
    var cocinando      by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val pedidos = listOf(
        Pedido("Pizza",       "🍕", 3000),
        Pedido("Hamburguesa", "🍔", 2000),
        Pedido("Ensalada",    "🥗", 1000)
    )

    fun resetPedidos() {
        estadoPizza    = "esperando"
        estadoBurger   = "esperando"
        estadoEnsalada = "esperando"
    }

    // Fondo con textura de cocina (gradiente cálido)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF3E2723), Color(0xFF5D4037), Color(0xFF4E342E)))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── ENCABEZADO ──────────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("👨‍🍳 Restaurante Tecsup", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Crema)
                Text("Corrutinas en la cocina", fontSize = 13.sp, color = MaderaClara)
            }

            // ── BOTÓN MEDIDOR DE UI ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CremaOscura)
                    .border(2.dp, MaderaClara, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔 ¿El restaurante sigue atendiendo?", fontWeight = FontWeight.Bold, color = Madera, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { contador++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Madera)
                    ) {
                        Text("Llamar al mesero 🛎️ → $contador veces", color = Crema)
                    }
                    Text("Si puedes presionar mientras se cocina, la cocina no bloqueó al mesero",
                        fontSize = 11.sp, color = MaderaClara, textAlign = TextAlign.Center)
                }
            }

            // ── DIVISOR DECORATIVO ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaderaClara)
                Text("  🍽️ MESA DE PEDIDOS 🍽️  ", color = Crema, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaderaClara)
            }

            // ── SIN CORRUTINAS: un cocinero, cola de pedidos ────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF7F1D1D).copy(alpha = 0.9f))
                    .border(2.dp, Rojo, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🧑‍🍳", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("SIN corrutinas", fontWeight = FontWeight.Bold, color = Color(0xFFFFCDD2), fontSize = 16.sp)
                            Text("Un cocinero solo · cola de pedidos · UI congelada",
                                color = Color(0xFFEF9A9A), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cola de pedidos visual
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        pedidos.forEachIndexed { i, p ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(p.emoji, fontSize = 26.sp)
                                Text("#${i + 1}", fontSize = 10.sp, color = Color(0xFFEF9A9A))
                                Text("${p.tiempoMs / 1000}s", fontSize = 10.sp, color = Color(0xFFEF9A9A))
                            }
                            if (i < pedidos.size - 1)
                                Text("→", color = Color(0xFFEF9A9A), fontSize = 18.sp,
                                    modifier = Modifier.padding(top = 8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            estadoSin = "🍕 Cocinando Pizza..."
                            Thread.sleep(pedidos[0].tiempoMs)
                            estadoSin = "🍔 Cocinando Hamburguesa..."
                            Thread.sleep(pedidos[1].tiempoMs)
                            estadoSin = "🥗 Cocinando Ensalada..."
                            Thread.sleep(pedidos[2].tiempoMs)
                            estadoSin = "✅ Listos — 6 segundos (3+2+1 en cola)"
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Rojo)
                    ) {
                        Text("🔴 Cocinar en cola (6s total)", color = Color.White)
                    }

                    if (estadoSin.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.3f))
                                .padding(8.dp)
                        ) {
                            Text(estadoSin, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 13.sp)
                        }
                    }
                }
            }

            // ── CON CORRUTINAS: 3 cocineros en paralelo ─────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B5E20).copy(alpha = 0.9f))
                    .border(2.dp, Verde, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👨‍🍳👩‍🍳🧑‍🍳", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("CON corrutinas", fontWeight = FontWeight.Bold, color = Color(0xFFC8E6C9), fontSize = 16.sp)
                            Text("3 cocineros simultáneos · UI viva · solo 3s",
                                color = Color(0xFFA5D6A7), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Estaciones de cocina individuales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("🍕", "Pizza\n3s", estadoPizza),
                            Triple("🍔", "Burger\n2s", estadoBurger),
                            Triple("🥗", "Ensalada\n1s", estadoEnsalada)
                        ).forEach { (emoji, label, estado) ->
                            val bgColor = when {
                                estado == "cocinando" -> Color(0xFFFF6F00).copy(alpha = 0.8f)
                                estado.startsWith("listo") -> Verde.copy(alpha = 0.8f)
                                else -> Color.Black.copy(alpha = 0.3f)
                            }
                            val estadoEmoji = when {
                                estado == "cocinando"      -> "🔥"
                                estado.startsWith("listo") -> "✅"
                                else                       -> "⏳"
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bgColor)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(emoji, fontSize = 28.sp)
                                    Text(label, fontSize = 10.sp, color = Color.White, textAlign = TextAlign.Center)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(estadoEmoji, fontSize = 18.sp)
                                    Text(
                                        when {
                                            estado == "cocinando"      -> "cocinando"
                                            estado.startsWith("listo") -> estado
                                            else                       -> "esperando"
                                        },
                                        fontSize = 9.sp, color = Color.White, textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (cocinando) return@Button
                            cocinando = true
                            resetPedidos()

                            scope.launch {
                                estadoPizza = "cocinando"
                                delay(3000)
                                estadoPizza = "listo (3s)"
                                if (estadoBurger.startsWith("listo") && estadoEnsalada.startsWith("listo"))
                                    cocinando = false
                            }
                            scope.launch {
                                estadoBurger = "cocinando"
                                delay(2000)
                                estadoBurger = "listo (2s)"
                                if (estadoPizza.startsWith("listo") && estadoEnsalada.startsWith("listo"))
                                    cocinando = false
                            }
                            scope.launch {
                                estadoEnsalada = "cocinando"
                                delay(1000)
                                estadoEnsalada = "listo (1s)"
                                if (estadoPizza.startsWith("listo") && estadoBurger.startsWith("listo"))
                                    cocinando = false
                            }
                        },
                        enabled = !cocinando,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Verde,
                            disabledContainerColor = MaderaClara
                        )
                    ) {
                        Text(
                            if (cocinando) "🔥 Cocinando los 3 platos..." else "🟢 Cocinar en paralelo (3s total)",
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "🥗 termina al 1s · 🍔 al 2s · 🍕 al 3s — todos al mismo tiempo",
                        fontSize = 11.sp, color = Color(0xFFA5D6A7), textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ── PIE DECORATIVO ───────────────────────────────────────────────
            Text(
                "🍴 Buen provecho — y buenas corrutinas 🍴",
                fontSize = 12.sp, color = MaderaClara, textAlign = TextAlign.Center
            )
        }
    }
}
