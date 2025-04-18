package motorbuscaminas

import kotlin.random.Random

/**
 * Lógica de negocio de Buscaminas.
 */
class Buscaminas(
    private val filas: Int,
    private val columnas: Int,
    private val minas: Int
) {
    // Tablero mutable para reinicios
    private var tablero: Array<Array<Celda>>

    // Indica si la partida ha terminado (mina explotada)
    var juegoTerminado: Boolean = false
        private set

    init {
        require(filas > 0 && columnas > 0) { "Dimensiones inválidas" }
        require(minas < filas * columnas)   { "Demasiadas minas" }
        // Inicialización del juego
        tablero = Array(filas) { Array(columnas) { Celda() } }
        colocarMinas()
        calcularAdyacentes()
    }

    /**
     * Reinicia completamente la partida.
     */
    fun reiniciar() {
        juegoTerminado = false
        // Reconstruir tablero vacío
        tablero = Array(filas) { Array(columnas) { Celda() } }
        colocarMinas()
        calcularAdyacentes()
    }

    /** Destapa la celda; si es mina, termina el juego. */
    fun destapar(fila: Int, columna: Int) {
        val celda = tablero.getOrNull(fila)?.getOrNull(columna) ?: return
        if (!celda.tapada || celda.bandera || juegoTerminado) return

        celda.tapada = false
        if (celda.mina) {
            juegoTerminado = true
            return
        }
        // Si no hay minas alrededor, propaga
        if (celda.valor == 0) {
            for (df in -1..1) for (dc in -1..1) {
                val nf = fila + df
                val nc = columna + dc
                if ((df != 0 || dc != 0) && nf in 0 until filas && nc in 0 until columnas) {
                    destapar(nf, nc)
                }
            }
        }
    }

    /** Coloca o quita bandera en una celda tapada. */
    fun cambiarBandera(fila: Int, columna: Int) {
        val celda = tablero.getOrNull(fila)?.getOrNull(columna) ?: return
        if (!celda.tapada || juegoTerminado) return
        celda.bandera = !celda.bandera
    }

    /** Devuelve el tablero para mostrar al usuario. */
    fun getTableroVisible(): List<List<String>> {
        return tablero.map { fila ->
            fila.map { celda ->
                when {
                    celda.tapada && celda.bandera -> "🚩"
                    celda.tapada                 -> "■"
                    celda.mina                   -> "💣"
                    celda.valor > 0             -> celda.valor.toString()
                    else                         -> " "
                }
            }
        }
    }

    private fun colocarMinas() {
        var colocadas = 0
        while (colocadas < minas) {
            val f = Random.nextInt(filas)
            val c = Random.nextInt(columnas)
            if (!tablero[f][c].mina) {
                tablero[f][c].mina = true
                colocadas++
            }
        }
    }

    private fun calcularAdyacentes() {
        for (f in 0 until filas) {
            for (c in 0 until columnas) {
                val celda = tablero[f][c]
                if (celda.mina) continue
                // Cuenta minas vecinas
                celda.valor = vecinas(f, c).count { (vf, vc) -> tablero[vf][vc].mina }
            }
        }
    }

    private fun vecinas(fila: Int, columna: Int): List<Pair<Int, Int>> {
        val lista = mutableListOf<Pair<Int, Int>>()
        for (df in -1..1) for (dc in -1..1) {
            val nf = fila + df
            val nc = columna + dc
            if ((df != 0 || dc != 0) && nf in 0 until filas && nc in 0 until columnas) {
                lista.add(nf to nc)
            }
        }
        return lista
    }
}

/** Representa cada celda del tablero. */
data class Celda(
    var tapada: Boolean = true,
    var bandera: Boolean = false,
    var mina: Boolean = false,
    var valor: Int = 0             // Minas adyacentes
)

