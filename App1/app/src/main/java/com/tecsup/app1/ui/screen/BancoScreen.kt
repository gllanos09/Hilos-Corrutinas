package com.tecsup.app1.ui.screen

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
import com.tecsup.app1.network.RetrofitClient

@Composable
fun BancoScreen() {
    var saldo by remember { mutableDoubleStateOf(1000.0) }
    var monto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("Listo. Presiona un botón para operar.") }
    var mensajeColor by remember { mutableStateOf(Color.Gray) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF3F3))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado de advertencia
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFF0000))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚠ SIN HILOS ⚠",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
        ) {
            Text(
                text = "⚠ ADVERTENCIA EDUCATIVA",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Text(
                text = "Esta app llama a la API en el HILO PRINCIPAL (main thread). " +
                        "Android lanzará NetworkOnMainThreadException y la UI se congelará. " +
                        "Esto demuestra por qué NUNCA debes hacer trabajo pesado en el hilo principal.",
                modifier = Modifier.padding(horizontal = 12.dp, bottom = 8.dp),
                color = Color(0xFFB71C1C),
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("🏦 Banco Tecsup", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("(Sin Hilos)", color = Color.Red, fontWeight = FontWeight.SemiBold)

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

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = monto,
            onValueChange = { monto = it },
            label = { Text("Monto (S/)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    // ⚠ LLAMADA EN EL MAIN THREAD — esto causará NetworkOnMainThreadException
                    // Android prohíbe operaciones de red en el hilo principal desde API 11
                    try {
                        val montoD = monto.toDoubleOrNull() ?: 0.0
                        mensaje = "Llamando API en main thread... (esto va a crashear)"
                        mensajeColor = Color(0xFFFF6F00)

                        // execute() es sincrónico — bloquea el hilo que lo llama
                        // Si ese hilo es el main thread → NetworkOnMainThreadException
                        val response = RetrofitClient.instance.getPost().execute()
                        if (response.isSuccessful) {
                            saldo += montoD
                            mensaje = "✅ Depósito de S/%.2f realizado".format(montoD)
                            mensajeColor = Color(0xFF1B5E20)
                        }
                    } catch (e: Exception) {
                        // Captura NetworkOnMainThreadException u otras
                        mensaje = "❌ ERROR: ${e.javaClass.simpleName}\n${e.message}"
                        mensajeColor = Color.Red
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
            ) {
                Text("Depositar")
            }

            Button(
                onClick = {
                    // ⚠ Mismo problema: llamada de red en el hilo principal
                    try {
                        val montoD = monto.toDoubleOrNull() ?: 0.0
                        mensaje = "Llamando API en main thread... (esto va a crashear)"
                        mensajeColor = Color(0xFFFF6F00)

                        val response = RetrofitClient.instance.getPost().execute()
                        if (response.isSuccessful) {
                            saldo -= montoD
                            mensaje = "✅ Retiro de S/%.2f realizado".format(montoD)
                            mensajeColor = Color(0xFF1B5E20)
                        }
                    } catch (e: Exception) {
                        mensaje = "❌ ERROR: ${e.javaClass.simpleName}\n${e.message}"
                        mensajeColor = Color.Red
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
            ) {
                Text("Retirar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                Text("📚 ¿Por qué crashea?", fontWeight = FontWeight.Bold, color = Color(0xFF5D4037))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Android tiene un sistema llamado StrictMode que detecta operaciones de red en el main thread.\n" +
                            "• El main thread es el hilo de la UI — si lo bloqueas, la app deja de responder.\n" +
                            "• La solución: usar hilos secundarios (App 2) o corrutinas (App 4).",
                    color = Color(0xFF5D4037),
                    fontSize = 13.sp
                )
            }
        }
    }
}
