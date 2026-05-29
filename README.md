# Demo: Hilos y Corrutinas en Android

Proyecto educativo de **TECSUP**. Dos apps que demuestran de forma práctica
la diferencia entre bloquear el hilo principal y trabajar de forma concurrente,
usando **Jetpack Compose**.

> ⚠️ Solo usar las carpetas **App1** y **App2**. Las carpetas App3 y App4 no forman parte de esta demo.

---

## App 1 — Demo Hilos

**Carpeta:** `App1/` · **Paquete:** `com.tecsup.app1`

Muestra la diferencia entre ejecutar trabajo pesado en el hilo principal versus
hacerlo en un hilo secundario.

### Pantalla

Tiene tres elementos:

1. **Botón contador** — sirve como detector: si puedes tocarlo mientras procesa, la UI está viva.
2. **Botón ❌ SIN hilo** — llama a `Thread.sleep(3000)` directamente en el main thread. La UI se congela por completo: no puedes tocar el contador ni interactuar con nada.
3. **Botón ✅ CON hilo** — lanza un `Thread` secundario que duerme 3 segundos y luego usa `Handler.post{}` para actualizar la UI. El main thread queda libre: el contador sigue funcionando.

### Código clave

```kotlin
// ❌ SIN hilo — bloquea el main thread
Thread.sleep(3000)
estado = "Listo"

// ✅ CON hilo — el main thread queda libre
Thread {
    Thread.sleep(3000)
    handler.post { estado = "Listo" }
}.start()
```

---

## App 2 — Demo Corrutinas

**Carpeta:** `App2/` · **Paquete:** `com.tecsup.app2`

Muestra la diferencia entre bloquear el hilo principal con `Thread.sleep`
versus suspender una corrutina con `delay()`.

### Pantalla

Tiene tres elementos:

1. **Botón contador** — mismo detector que App1.
2. **Botón ❌ SIN corrutinas** — usa `Thread.sleep(3000)` en el main thread. Congela la UI.
3. **Botón ✅ CON corrutinas** — usa `scope.launch { delay(3000) }`. `delay()` *suspende* la corrutina sin bloquear el hilo, la UI sigue respondiendo.

### Código clave

```kotlin
// ❌ SIN corrutinas — bloquea el main thread
Thread.sleep(3000)
estado = "Listo"

// ✅ CON corrutinas — suspende sin bloquear
scope.launch {
    delay(3000)
    estado = "Listo"
}
```

---

## Diferencia principal entre App1 y App2

| | App1 (Hilos) | App2 (Corrutinas) |
|---|---|---|
| Mecanismo | `Thread` + `Handler` | `scope.launch` + `delay()` |
| Código | Más verboso | Más simple y secuencial |
| Uso hoy en día | Legacy | Recomendado en Android moderno |

---

## Cómo abrir en Android Studio

1. `File → Open`
2. Selecciona la carpeta `App1/` o `App2/` (no la carpeta raíz)
3. Sync Gradle → Run

## Stack

- Kotlin 2.2.10 · Jetpack Compose · Material 3
- `kotlinx-coroutines-android:1.10.1` (App2)
- minSdk 24 · targetSdk 36
