package com.tecsup.app1.ui.screen

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HilosScreen() {
    // Contador que demuestra si la UI sigue viva o no
    var contador by remember { mutableIntStateOf(0) }
    var estado by remember { mutableStateOf("Presiona un botón para ver la diferencia") }

    val handler = remember { Handler(Looper.getMainLooper()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Demo: Hilos en Android", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        // Contador — si la UI se congela, este botón deja de responder
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("¿La UI responde?", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { contador++ }) {
                    Text("Toca aquí → $contador")
                }
                Text(
                    "Si puedes clickear mientras procesa, la UI está viva",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        HorizontalDivider()

        // --- SIN HILO ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("❌ SIN hilo", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 18.sp)
                Text("Thread.sleep en el main thread", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        estado = "Procesando SIN hilo... (UI congelada)"
                        // Bloqueamos el main thread directamente
                        // La UI se congela: no puedes tocar el contador arriba
                        Thread.sleep(3000)
                        estado = "Listo (sin hilo). ¿Pudiste clickear el contador?"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Ejecutar SIN hilo (3s)")
                }
            }
        }

        // --- CON HILO ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("✅ CON hilo", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 18.sp)
                Text("Thread secundario + Handler", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        estado = "Procesando CON hilo... (toca el contador)"
                        // Lanzamos un hilo secundario — el main thread queda libre
                        Thread {
                            Thread.sleep(3000)
                            // Volvemos al main thread solo para actualizar la UI
                            handler.post {
                                estado = "Listo (con hilo). ¿Pudiste clickear el contador?"
                            }
                        }.start()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Ejecutar CON hilo (3s)")
                }
            }
        }

        // Estado actual
        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = estado,
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
