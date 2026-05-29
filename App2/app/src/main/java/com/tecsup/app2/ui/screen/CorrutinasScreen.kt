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
    var contador by remember { mutableIntStateOf(0) }

    // Estado de cada tarea individual (para mostrar progreso simultáneo)
    var tareaA by remember { mutableStateOf("⏳ esperando...") }
    var tareaB by remember { mutableStateOf("⏳ esperando...") }
    var tareaC by remember { mutableStateOf("⏳ esperando...") }

    // Estado para el bloque sin corrutinas
    var estadoSin by remember { mutableStateOf("Presiona para ejecutar") }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Demo: Corrutinas vs Hilos", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        // Contador — medidor de vida de la UI
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("¿La UI sigue respondiendo?", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Button(onClick = { contador++ }) {
                    Text("Tócame mientras procesa → $contador")
                }
            }
        }

        HorizontalDivider()

        // --- SIN CORRUTINAS: tareas secuenciales y bloqueantes ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("❌ SIN corrutinas", fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 16.sp)
                Text(
                    "3 tareas secuenciales con Thread.sleep\nUI congelada, una tarea a la vez",
                    color = Color.Gray, fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // Las 3 tareas se ejecutan UNA DESPUÉS DE OTRA bloqueando la UI
                        estadoSin = "Tarea 1 corriendo..."
                        Thread.sleep(1500)
                        estadoSin = "Tarea 2 corriendo..."
                        Thread.sleep(1500)
                        estadoSin = "Tarea 3 corriendo..."
                        Thread.sleep(1500)
                        estadoSin = "✅ Listo — tardó 4.5s en total (secuencial)"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Ejecutar 3 tareas SIN corrutinas")
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(estadoSin, fontWeight = FontWeight.Medium, fontSize = 13.sp)
            }
        }

        // --- CON CORRUTINAS: 3 tareas simultáneas, UI viva ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("✅ CON corrutinas", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32), fontSize = 16.sp)
                Text(
                    "3 corrutinas corriendo AL MISMO TIEMPO\nUI viva, cada tarea progresa independiente",
                    color = Color.Gray, fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        // Resetear
                        tareaA = "🔄 corriendo..."
                        tareaB = "🔄 corriendo..."
                        tareaC = "🔄 corriendo..."

                        // Las 3 corrutinas se lanzan SIMULTÁNEAMENTE con scope.launch
                        // Cada una corre independiente sin bloquear a las otras ni al main thread

                        scope.launch {
                            delay(1000) // solo 1 segundo
                            tareaA = "✅ lista (1s)"
                        }

                        scope.launch {
                            delay(2000) // 2 segundos
                            tareaB = "✅ lista (2s)"
                        }

                        scope.launch {
                            delay(3000) // 3 segundos
                            tareaC = "✅ lista (3s)"
                        }

                        // Las 3 corren en paralelo → total ~3s, no 6s
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Text("Lanzar 3 corrutinas simultáneas")
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Progreso individual de cada corrutina
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tarea A", fontSize = 12.sp, color = Color.Gray)
                        Text(tareaA, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tarea B", fontSize = 12.sp, color = Color.Gray)
                        Text(tareaB, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tarea C", fontSize = 12.sp, color = Color.Gray)
                        Text(tareaC, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Observa: A termina a los 1s, B a los 2s, C a los 3s — simultáneas",
                    fontSize = 11.sp,
                    color = Color(0xFF2E7D32)
                )
            }
        }
    }
}
