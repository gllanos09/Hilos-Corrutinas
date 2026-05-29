package com.tecsup.app4

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Producto(val nombre: String, val precio: Double)

// Estado de la UI modelado en una sola data class — patrón UiState
data class VentasUiState(
    val carrito: List<Producto> = emptyList(),
    val total: Double = 0.0,
    val procesando: Boolean = false,
    val mensaje: String = "Agrega productos y confirma tu pedido.",
    val clickCounter: Int = 0
)

class VentasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VentasUiState())
    // Exponemos solo StateFlow de lectura a la UI
    val uiState: StateFlow<VentasUiState> = _uiState.asStateFlow()

    fun agregarProducto(producto: Producto) {
        _uiState.update { state ->
            val nuevoCarrito = state.carrito + producto
            state.copy(
                carrito = nuevoCarrito,
                total = nuevoCarrito.sumOf { it.precio }
            )
        }
    }

    // viewModelScope cancela automáticamente la corrutina cuando el ViewModel se destruye
    // → No hay memory leaks, no hay callbacks huérfanos
    fun confirmarPedido() {
        if (_uiState.value.carrito.isEmpty()) {
            _uiState.update { it.copy(mensaje = "⚠ El carrito está vacío") }
            return
        }

        // launch inicia la corrutina sin bloquear el hilo actual (non-blocking)
        viewModelScope.launch {
            _uiState.update { it.copy(procesando = true, mensaje = "⏳ Procesando pedido...") }

            // delay suspende la corrutina (no bloquea el hilo)
            // Durante este tiempo el main thread sigue libre → la UI responde
            delay(2000)

            // Después del delay, continuamos en el mismo contexto (main thread por defecto)
            // No necesitamos Handler.post{} — las corrutinas manejan esto automáticamente
            _uiState.update {
                it.copy(
                    procesando = false,
                    carrito = emptyList(),
                    total = 0.0,
                    mensaje = "✅ Pedido confirmado por S/%.2f\n(Procesado con corrutinas — código limpio)".format(it.total)
                )
            }
        }
        // El código después de launch() se ejecuta INMEDIATAMENTE
        // La corrutina corre concurrentemente — eso es todo lo que se necesita
    }

    // Este método puede llamarse mientras confirmarPedido() está corriendo
    // Las corrutinas permiten concurrencia fácil sin bloqueos
    fun incrementarContador() {
        _uiState.update { it.copy(clickCounter = it.clickCounter + 1) }
    }
}
