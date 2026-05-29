# Hilos y Corrutinas en Android — Proyecto Educativo

Proyecto educativo de **TECSUP** compuesto por 4 mini-apps Android que demuestran de forma
práctica la evolución de la concurrencia en Android: desde el problema en el hilo principal,
pasando por hilos clásicos, hasta las corrutinas modernas con Jetpack Compose.

---

## Estructura del proyecto

```
Hilos-Corrutinas/
├── App1/   → Banco SIN hilos     (🔴 problema)
├── App2/   → Banco CON hilos     (🟡 solución clásica)
├── App3/   → Ventas SIN corrutinas (🟠 código verboso)
└── App4/   → Ventas CON corrutinas (🟢 solución moderna)
```

---

## App 1 — Banco SIN Hilos

**Paquete:** `com.tecsup.app1`

### ¿Qué hace?
Simula un sistema bancario simple donde el usuario deposita o retira dinero.
La llamada HTTP a la API se realiza directamente en el **main thread** usando
`Retrofit.execute()` de forma sincrónica.

### ¿Qué demuestra?
Android lanza `NetworkOnMainThreadException` al intentar hacer red en el hilo principal.
La UI se congela y la app crashea. Esto ilustra por qué **nunca** debes hacer trabajo
pesado en el hilo de la UI.

### Estructura de archivos
```
app1/
├── MainActivity.kt               ← Entry point, solo llama a BancoScreen
├── network/
│   └── ApiService.kt             ← Interface Retrofit + RetrofitClient + data class Post
└── ui/
    ├── screen/
    │   └── BancoScreen.kt        ← Toda la UI: saldo, campo de monto, botones
    └── theme/
```

### Concepto clave
```kotlin
// ❌ ESTO CRASHEA: llamada de red en el main thread
val response = RetrofitClient.instance.getPost().execute()
```

---

## App 2 — Banco CON Hilos

**Paquete:** `com.tecsup.app2`

### ¿Qué hace?
El mismo sistema bancario pero resuelto correctamente con `Thread` + `Handler`.
La llamada HTTP se ejecuta en un hilo secundario y el resultado vuelve al
main thread mediante `Handler(Looper.getMainLooper()).post {}`.

### ¿Qué demuestra?
- La UI **no se bloquea** mientras se procesa la petición.
- Un botón extra de contador permanece activo durante la operación, demostrando
  que el main thread sigue libre.
- El patrón funciona pero produce **código verboso** (ver App 4 para la mejora).

### Estructura de archivos
```
app2/
├── MainActivity.kt               ← Entry point, solo llama a BancoScreen
├── network/
│   └── ApiService.kt             ← Interface Retrofit + RetrofitClient + data class Post
└── ui/
    ├── screen/
    │   └── BancoScreen.kt        ← UI con Thread, Handler y CircularProgressIndicator
    └── theme/
```

### Concepto clave
```kotlin
// ✅ Llamada en hilo secundario
Thread {
    val response = RetrofitClient.instance.getPost().execute()
    // Regresar al main thread para actualizar la UI
    mainHandler.post {
        saldo += montoD
    }
}.start()
```

---

## App 3 — Ventas SIN Corrutinas

**Paquete:** `com.tecsup.app3`

### ¿Qué hace?
Sistema de ventas donde el usuario agrega productos a un carrito y confirma
el pedido. El procesamiento simula trabajo pesado con `Thread.sleep(2000)`.
Se usa la misma técnica de `Thread` + `Handler` del App 2.

### ¿Qué demuestra?
El código necesario con `Thread` + `Handler` es **largo, verboso y propenso a errores**:
- Cada actualización de UI requiere un bloque `Handler.post {}` separado.
- No hay cancelación automática si el Activity se destruye (posible memory leak).
- El botón contador queda **deshabilitado** durante el proceso.
- Difícil encadenar múltiples operaciones asíncronas.

### Estructura de archivos
```
app3/
├── MainActivity.kt               ← Entry point, solo llama a VentasScreen
├── model/
│   └── Producto.kt               ← Data class Producto(nombre, precio)
└── ui/
    ├── screen/
    │   └── VentasScreen.kt       ← UI + lógica Thread/Handler con comentarios educativos
    └── theme/
```

### Concepto clave
```kotlin
// ❌ Verboso: cada estado requiere su propio Handler.post{}
Thread {
    Thread.sleep(2000)
    mainHandler.post {
        mensaje = "✅ Pedido confirmado"   // estado 1
        carrito = emptyList()              // estado 2
        procesando = false                 // estado 3
        // N estados → N líneas → difícil de mantener
    }
}.start()
```

---

## App 4 — Ventas CON Corrutinas

**Paquete:** `com.tecsup.app4`

### ¿Qué hace?
El mismo sistema de ventas que App 3, reescrito con **Kotlin Coroutines** y
arquitectura **ViewModel + StateFlow**. El código es notablemente más simple
y seguro.

### ¿Qué demuestra?
- `viewModelScope.launch { delay(2000) }` reemplaza todo el boilerplate de
  `Thread` + `Handler`.
- `delay()` **suspende** la corrutina sin bloquear el hilo → la UI sigue respondiendo.
- El botón contador **sí funciona** durante el proceso (diferencia clave vs App 3).
- El ViewModel cancela automáticamente las corrutinas cuando se destruye → sin memory leaks.
- `collectAsStateWithLifecycle` es lifecycle-aware → sin actualizaciones en background.

### Estructura de archivos
```
app4/
├── MainActivity.kt               ← Entry point, crea ViewModel y llama a VentasScreen
├── model/
│   └── Producto.kt               ← Data class Producto(nombre, precio)
├── viewmodel/
│   └── VentasViewModel.kt        ← ViewModel con StateFlow, confirmarPedido(), agregarProducto()
└── ui/
    ├── screen/
    │   └── VentasScreen.kt       ← UI reactiva que observa el StateFlow del ViewModel
    └── theme/
```

### Concepto clave
```kotlin
// ✅ Limpio: una corrutina, código secuencial, sin callbacks
viewModelScope.launch {
    _uiState.update { it.copy(procesando = true) }
    delay(2000)                    // suspende sin bloquear
    _uiState.update { it.copy(procesando = false, carrito = emptyList()) }
}
// viewModelScope cancela esto automáticamente si el ViewModel se destruye
```

---

## Comparativa general

| Característica              | App 1 (Sin hilos) | App 2 (Con hilos) | App 3 (Sin corrutinas) | App 4 (Con corrutinas) |
|-----------------------------|:-----------------:|:-----------------:|:---------------------:|:---------------------:|
| UI no se bloquea            | ❌                | ✅                | ✅                    | ✅                    |
| Código conciso              | ✅                | ❌                | ❌                    | ✅                    |
| Lifecycle-aware             | ❌                | ❌                | ❌                    | ✅                    |
| Sin memory leaks            | ❌                | ❌                | ❌                    | ✅                    |
| Botón contador activo       | ❌                | ✅                | ❌                    | ✅                    |
| Fácil manejo de errores     | ❌                | ❌                | ❌                    | ✅                    |

---

## Stack tecnológico

- **Lenguaje:** Kotlin 2.2.10
- **UI:** Jetpack Compose + Material 3
- **Red (App1 y App2):** Retrofit 2.11.0 + Gson Converter
- **Arquitectura (App4):** ViewModel + StateFlow + Corrutinas
- **minSdk:** 24 · **targetSdk:** 36
- **AGP:** 9.1.1

---

## Orden de estudio recomendado

1. **App 1** → Entiende el problema: red en el main thread crashea
2. **App 2** → Solución con Thread + Handler: funciona pero es verboso
3. **App 3** → El mismo problema de verbosidad escalado a un caso real
4. **App 4** → La solución moderna: corrutinas limpias, seguras y concisas
