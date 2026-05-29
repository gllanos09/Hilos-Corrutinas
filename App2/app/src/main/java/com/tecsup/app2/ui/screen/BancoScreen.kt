package com.tecsup.app2.ui.screen

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.app2.network.RetrofitClient

@Composable
fun BancoScreen() {
    var saldo by remember { mutableDoubleStateOf(1000.0) }
    var monto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("Listo. Presiona un botón para operar.") }
    var mensajeColor by remember { mutableStateOf(Color.Gray) }
    var procesando by remember { mutableStateOf(false) }

    // Contador que demuestra que la UI sigue respondiendo mientras el hilo trabaja
    var clickCounter by remember { mutableIntStateOf(0) }

    // Handler para regresar al main thread desde el hilo secundario
    val mainHandler = remember { Handler(Looper.getMainLooper()) }

    fun operarConHilo(esDeposito: Boolean) {
        val montoD = monto.toDoubleOrNull() ?: 0.0
        if (montoD <= 0) {
            mensaje = "⚠ Ingresa un monto válido"
            mensajeColor = Color(0xFFFF6F00)
            return
        }

        procesando = true
        mensaje = "⏳ Procesando en hilo secundario..."
        mensajeColor = Color(0xFF1565C0)

        // Lanzamos un hilo secundario para la llamada de red
        // Esto libera el main thread → la UI sigue respondiendo
        Thread {
            try {
                // execute() es sincrónico, pero aquí estamos en un hilo secundario → está bien
                val response = RetrofitClient.instance.getPost().execute()

                // No podemos tocar la UI desde un hilo secundario
                // Usamos Handler para encolar el resultado en el main thread
                mainHandler.post {
                    if (response.isSuccessful) {
                        if (esDeposito) {
                            saldo += montoD
                            mensaje = "✅ Depósito de S/%.2f realizado\n(API respondió en hilo secundario)".format(montoD)
                        } else {
                            saldo -= montoD
                            mensaje = "✅ Retiro de S/%.2f realizado\n(API respondió en hilo secundario)".format(montoD)
                        }
                        mensajeColor = Color(0xFF1B5E20)
                    } else {
                        mensaje = "❌ Error de API: ${response.code()}"
                        mensajeColor = Color.Red
                    }
                    procesando = false
                }
            } catch (e: Exception) {
                mainHandler.post {
                    mensaje = "❌ Error: ${e.message}"
                    mensajeColor = Color.Red
                    procesando = false
                }
            }
        }.start() // .start() lanza el hilo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8E9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2E7D32))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✅ CON HILOS ✅",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
        ) {
            Text(
                text = "✅ SOLUCIÓN: Thread + Handler",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
            Text(
                text = "La llamada HTTP se hace en un Thread secundario. " +
                        "El resultado regresa al main thread con Handler(Looper.getMainLooper()).post{}. " +
                        "La UI nunca se bloquea.",
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                color = Color(0xFF1B5E20),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("🏦 Banco Tecsup", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("(Con Hilos)", color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Saldo Actual", fontSize = 14.sp, color = Color.Gray)
                Text(
                    text = "S/ %.2f".format(saldo),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1B5E20)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón demo que prueba que la UI sigue respondiendo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
        ) {
            Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "👇 ¡Presióname MIENTRAS procesa!",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0)
                )
                Text(
                    "Este botón sigue funcionando porque el hilo secundario no bloquea la UI",
                    fontSize = 12.sp,
                    color = Color(0xFF1565C0)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { clickCounter++ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
                ) {
                    Text("UI ACTIVA — Clicks: $clickCounter")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it },
            label = { Text("Monto (S/)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            enabled = !procesando
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { operarConHilo(esDeposito = true) },
                enabled = !procesando,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) {
                Text("Depositar")
            }

            Button(
                onClick = { operarConHilo(esDeposito = false) },
                enabled = !procesando,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Text("Retirar")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (procesando) {
            CircularProgressIndicator(color = Color(0xFF2E7D32))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Llamando a la API en hilo secundario...", color = Color(0xFF1565C0), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

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

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("📚 ¿Cómo funciona?", fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "1. Thread { ... }.start() → lanza un hilo secundario\n" +
                            "2. Dentro del hilo: llamada HTTP sincrónica (permitida)\n" +
                            "3. Handler(Looper.getMainLooper()).post { ... } → regresa al main thread\n" +
                            "4. Problema: código verboso. Solución elegante: corrutinas (App 4)",
                    color = Color(0xFF5D4037),
                    fontSize = 13.sp
                )
            }
        }
    }
}
