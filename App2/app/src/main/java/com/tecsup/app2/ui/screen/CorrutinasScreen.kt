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

@Composable
fun CorrutinasScreen() {
    // Contador que demuestra si la UI sigue viva o no
    var contador by remember { mutableIntStateOf(0) }
    var estado by remember { mutableStateOf("Presiona un botón para ver la diferencia") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Demo: Corrutinas en Android", fontSize = 22.sp, fontWeight = FontWeight.Bold)

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

        // --- SIN CORRUTINAS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("❌ SIN corrutinas", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 18.sp)
                Text("Thread.sleep bloquea el main thread", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        estado = "Procesando SIN corrutinas... (UI congelada)"
                        // Thread.sleep en el main thread congela todo
                        Thread.sleep(3000)
                        estado = "Listo (sin corrutinas). ¿Pudiste clickear el contador?"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Ejecutar SIN corrutinas (3s)")
                }
            }
        }

        // --- CON CORRUTINAS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("✅ CON corrutinas", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 18.sp)
                Text("delay() suspende sin bloquear el hilo", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            estado = "Procesando CON corrutinas... (toca el contador)"
                            // delay() suspende esta corrutina pero NO bloquea el main thread
                            // La UI sigue respondiendo por completo
                            delay(3000)
                            estado = "Listo (con corrutinas). ¿Pudiste clickear el contador?"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Ejecutar CON corrutinas (3s)")
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
