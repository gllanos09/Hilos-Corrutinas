package com.tecsup.app1.ui.screen

import android.os.Handler
import android.os.Looper
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

// Paleta industrial / fábrica
private val AceroOscuro  = Color(0xFF1C1C2E)
private val Acero        = Color(0xFF2D2D44)
private val AceroClaro   = Color(0xFF3D3D5C)
private val Plata        = Color(0xFFB0BEC5)
private val PlataClara   = Color(0xFFECEFF1)
private val Naranja      = Color(0xFFFF6F00)
private val NaranjaClaro = Color(0xFFFFE0B2)
private val Verde        = Color(0xFF00C853)
private val Rojo         = Color(0xFFD50000)

@Composable
fun HilosScreen() {
    var contador  by remember { mutableIntStateOf(0) }
    var estado    by remember { mutableStateOf("") }
    var procesando by remember { mutableStateOf(false) }

    val handler = remember { Handler(Looper.getMainLooper()) }

    // Fondo industrial oscuro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(AceroOscuro, Acero, AceroClaro))
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

            // ── ENCABEZADO ────────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("⚙️ Fábrica de Hilos", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PlataClara)
                Text("Main thread vs Hilo secundario", fontSize = 13.sp, color = Plata)
            }

            // ── PANEL DE CONTROL (medidor de UI) ──────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AceroClaro)
                    .border(2.dp, Plata, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Luz indicadora
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (procesando) Rojo else Verde)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (procesando) "PANEL BLOQUEADO" else "PANEL ACTIVO",
                            fontWeight = FontWeight.Bold,
                            color = if (procesando) Rojo else Verde,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("🖥️ Panel de control de la fábrica", fontWeight = FontWeight.Bold, color = PlataClara, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = { contador++ },
                        colors = ButtonDefaults.buttonColors(containerColor = Naranja)
                    ) {
                        Text("Presionar botón de control → $contador veces", color = Color.White)
                    }
                    Text(
                        "Si puedes presionarlo mientras opera la máquina, el panel no está bloqueado",
                        fontSize = 11.sp, color = Plata, textAlign = TextAlign.Center
                    )
                }
            }

            // ── SEPARADOR ────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Plata)
                Text("  ⚙️ MÁQUINAS ⚙️  ", color = PlataClara, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Plata)
            }

            // ── SIN HILO: máquina bloquea la fábrica ─────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF4A0000).copy(alpha = 0.9f))
                    .border(2.dp, Rojo, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏭", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("SIN hilo secundario", fontWeight = FontWeight.Bold, color = Color(0xFFFFCDD2), fontSize = 16.sp)
                            Text("Máquina en el hilo principal · bloquea todo",
                                color = Color(0xFFEF9A9A), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Diagrama de bloqueo
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("📋 Línea de producción:", color = Color(0xFFEF9A9A), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EstacionMaquina("🖥️", "Main\nThread", Color(0xFFEF9A9A))
                                Text("⟶", color = Rojo, fontSize = 20.sp)
                                EstacionMaquina("⚙️", "Thread\n.sleep()", Color(0xFFEF9A9A))
                                Text("⟶", color = Rojo, fontSize = 20.sp)
                                EstacionMaquina("🔒", "UI\nFrozen", Rojo)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            procesando = true
                            estado = "⚙️ Máquina operando en el hilo principal... UI congelada"
                            Thread.sleep(3000)
                            estado = "🏁 Operación terminada — ¿pudiste usar el panel de control?"
                            procesando = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Rojo)
                    ) {
                        Text("🔴 Operar SIN hilo secundario (3s)", color = Color.White)
                    }
                }
            }

            // ── CON HILO: máquina en línea separada ──────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF003300).copy(alpha = 0.9f))
                    .border(2.dp, Verde, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏗️", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("CON hilo secundario", fontWeight = FontWeight.Bold, color = Color(0xFFC8E6C9), fontSize = 16.sp)
                            Text("Máquina en línea aparte · panel siempre activo",
                                color = Color(0xFFA5D6A7), fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Diagrama de hilos paralelos
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text("📋 Líneas de producción:", color = Color(0xFFA5D6A7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            // Hilo principal
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Hilo 1:", color = PlataClara, fontSize = 10.sp, modifier = Modifier.width(48.dp))
                                EstacionMaquina("🖥️", "Main\nThread", Color(0xFFA5D6A7))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("→ UI libre ✅", color = Verde, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Hilo secundario
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Hilo 2:", color = PlataClara, fontSize = 10.sp, modifier = Modifier.width(48.dp))
                                EstacionMaquina("⚙️", "Thread\nsecund.", Color(0xFFA5D6A7))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("→ Handler.post ↗", color = Verde, fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            estado = "⚙️ Máquina corriendo en hilo secundario... usa el panel"
                            Thread {
                                Thread.sleep(3000)
                                handler.post {
                                    estado = "🏁 Operación terminada — ¿pudiste usar el panel de control?"
                                }
                            }.start()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Verde)
                    ) {
                        Text("🟢 Operar CON hilo secundario (3s)", color = Color.White)
                    }
                }
            }

            // ── PANTALLA DE ESTADO ────────────────────────────────────────
            if (estado.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AceroClaro)
                        .border(1.dp, Plata, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📟", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(estado, color = PlataClara, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // ── PIE ───────────────────────────────────────────────────────
            Text(
                "⚙️ Fábrica operando 24/7 — con los hilos correctos ⚙️",
                fontSize = 11.sp, color = Plata, textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EstacionMaquina(emoji: String, label: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Text(label, fontSize = 9.sp, color = color, textAlign = TextAlign.Center)
    }
}
