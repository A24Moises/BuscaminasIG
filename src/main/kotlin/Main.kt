import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import motorbuscaminas.EstadoJuego

fun main() = application {
    var mostrarJuego by remember { mutableStateOf(false) }

    Window(onCloseRequest = ::exitApplication, title = "Buscaminas") {
        if (mostrarJuego) {
            val estadoJuego = remember { EstadoJuego(8, 8, 10) }
            PantallaJuego(estado = estadoJuego, onSalir = { mostrarJuego = false })
        } else {
            PantallaInicio(onIniciarJuego = { mostrarJuego = true })
        }
    }
}

@Composable
fun PantallaInicio(onIniciarJuego: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("💣 🚩 Buscaminas 🚩💣", fontSize = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onIniciarJuego) {
            Text("Iniciar Juego")
        }
    }
}

@Composable
fun PantallaJuego(estado: EstadoJuego, onSalir: () -> Unit) {
    LaunchedEffect(estado.juegoTerminado) {
        if (!estado.juegoTerminado) {
            while (!estado.juegoTerminado) {
                delay(1000)
                estado.incrementarTiempo()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("\u23F1\uFE0F Tiempo: ${estado.tiempo} segundos", fontSize = 20.sp)
        Spacer(modifier = Modifier.height(16.dp))

        Tablero(estado)

        if (estado.juegoTerminado) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "💥 ¡Has perdido! 💥",
                color = Color.Red,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { estado.reiniciarJuego() }) {
            Text("Reiniciar Juego")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onSalir) {
            Text("Salir del juego")
        }
    }
}

@Composable
fun Tablero(estado: EstadoJuego) {
    val tablero = estado.getTablero()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        for ((filaIndex, fila) in tablero.withIndex()) {
            Row {
                for ((colIndex, celda) in fila.withIndex()) {
                    Casilla(
                        contenido = celda,
                        onClick = { estado.alDestapar(filaIndex, colIndex) },
                        onRightClick = { estado.alCambiarBandera(filaIndex, colIndex) }
                    )
                }
            }
        }
    }
}

@Composable
fun Casilla(
    contenido: String,
    onClick: () -> Unit,
    onRightClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .border(1.dp, Color.DarkGray)
            .background(Color.LightGray)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onClick() },
                    onLongPress = { onRightClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = contenido, fontSize = 16.sp)
    }
}

