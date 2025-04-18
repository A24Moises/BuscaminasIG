package motorbuscaminas

import androidx.compose.runtime.*
import kotlinx.coroutines.*

class EstadoJuego(filas: Int, columnas: Int, minas: Int) {
    private val _juego = Buscaminas(filas, columnas, minas)
    private val _tablero = mutableStateListOf<MutableList<String>>()

    var tiempo by mutableStateOf(0)
        private set

    var juegoTerminado by mutableStateOf(false)
        private set

    init {
        actualizarTableroVisible()
    }

    private fun actualizarTableroVisible() {
        _tablero.clear()
        _tablero.addAll(_juego.getTableroVisible().map { it.toMutableList() })
    }

    fun getTablero(): List<List<String>> = _tablero

    fun alDestapar(fila: Int, columna: Int) {
        if (juegoTerminado) return
        _juego.destapar(fila, columna)
        juegoTerminado = _juego.juegoTerminado
        actualizarTableroVisible()
    }

    fun alCambiarBandera(fila: Int, columna: Int) {
        if (juegoTerminado) return
        _juego.cambiarBandera(fila, columna)
        actualizarTableroVisible()
    }

    fun incrementarTiempo() {
        tiempo++
    }

    fun reiniciarJuego() {
        tiempo = 0
        juegoTerminado = false
        _juego.reiniciar()
        actualizarTableroVisible()
    }
}