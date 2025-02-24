package Juegos;

import java.util.*;
import javax.swing.JOptionPane;

public class Turnos {
    private final int TAMANO = 8;
    private Jugadores[][] casillas;
    private boolean turnoRaton;
    private int ratonFila, ratonColumna;

    public Turnos() {
        casillas = new Jugadores[TAMANO][TAMANO];
        inicializarTablero();
        turnoRaton = true;
    }

    // Constructor privado para clonar el estado
    private Turnos(Jugadores[][] casillas, boolean turnoRaton, int ratonFila, int ratonColumna) {
        this.casillas = new Jugadores[TAMANO][TAMANO];
        for (int i = 0; i < TAMANO; i++) {
            for (int j = 0; j < TAMANO; j++) {
                if (casillas[i][j] != null) {
                    if (casillas[i][j] instanceof Gato) {
                        this.casillas[i][j] = new Gato(casillas[i][j].getFila(), casillas[i][j].getColumna());
                    } else if (casillas[i][j] instanceof Raton) {
                        this.casillas[i][j] = new Raton(casillas[i][j].getFila(), casillas[i][j].getColumna());
                    }
                }
            }
        }
        this.turnoRaton = turnoRaton;
        this.ratonFila = ratonFila;
        this.ratonColumna = ratonColumna;
    }

    private Turnos cloneState() {
        return new Turnos(this.casillas, this.turnoRaton, this.ratonFila, this.ratonColumna);
    }

    private void inicializarTablero() {
        // Colocar los 4 gatos en la fila 7 (casillas blancas)
        for (int i = 0; i < 4; i++) {
            int columna = (i * 2) + 1;
            casillas[7][columna] = new Gato(7, columna);
        }
        // Colocar el ratón en la fila 0, columna 4
        ratonFila = 0;
        ratonColumna = 4;
        casillas[ratonFila][ratonColumna] = new Raton(ratonFila, ratonColumna);
    }

    public boolean esMovimientoValido(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        if (filaDestino < 0 || filaDestino >= TAMANO || colDestino < 0 || colDestino >= TAMANO) return false;
        if ((filaDestino + colDestino) % 2 != 0) return false;
        return casillas[filaDestino][colDestino] == null;
    }

    public void moverPieza(int filaOrigen, int colOrigen, int filaDestino, int colDestino) {
        Jugadores pieza = casillas[filaOrigen][colOrigen];
        if (pieza != null) {
            casillas[filaOrigen][colOrigen] = null;
            casillas[filaDestino][colDestino] = pieza;
            pieza.setPosicion(filaDestino, colDestino);
            // Si se mueve el ratón, actualizamos sus coordenadas globales:
            if (pieza instanceof Raton) {
                ratonFila = filaDestino;
                ratonColumna = colDestino;
            }
        }
    }

    public void manejarClick(int fila, int col) {
        if (turnoRaton && casillas[ratonFila][ratonColumna] instanceof Raton) {
            // Asegurarse de que el ratón solo se mueva una casilla en diagonal
            if (esMovimientoValido(ratonFila, ratonColumna, fila, col)
                    && Math.abs(fila - ratonFila) == 1
                    && Math.abs(col - ratonColumna) == 1) {
                moverPieza(ratonFila, ratonColumna, fila, col);
                if (esVictoriaRaton()) {
                    mostrarVictoriaRaton();
                } else {
                    turnoRaton = false;
                    turnoIA();
                }
            }
        }
    }

    public void turnoIA() {
        Resultado res = minimaxAlphaBeta(9, true, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if (res != null && res.movimiento != null) {
            moverPieza(res.movimiento.filaOrigen, res.movimiento.colOrigen,
                       res.movimiento.filaDestino, res.movimiento.colDestino);
        }
        if (esVictoriaRaton()) {
            mostrarVictoriaRaton();
        } else if (esVictoriaGatos()) {
            mostrarVictoriaGatos();
        } else {
            turnoRaton = true;
        }
    }

    private boolean esVictoriaGatos() {
        // Si el ratón ya no puede moverse, gana de inmediato.
        if (obtenerMovimientosRaton().isEmpty()) {
            return true;
        }
        // Para cada movimiento posible del ratón...
        for (Movimiento movRaton : obtenerMovimientosRaton()) {
            // Clonamos el estado y aplicamos el movimiento del ratón.
            Turnos clonRaton = this.cloneState();
            clonRaton.moverPieza(movRaton.filaOrigen, movRaton.colOrigen, movRaton.filaDestino, movRaton.colDestino);

            // Ahora verificamos: ¿existe algún movimiento de gato que, aplicado, haga que el ratón no tenga salida?
            List<Movimiento> movimientosGatos = clonRaton.obtenerMovimientosGatos();
            boolean gatoPuedeAtrapar = false;
            for (Movimiento movGato : movimientosGatos) {
                Turnos clonGato = clonRaton.cloneState();
                clonGato.moverPieza(movGato.filaOrigen, movGato.colOrigen, movGato.filaDestino, movGato.colDestino);
                if (clonGato.obtenerMovimientosRaton().isEmpty()) {
                    // Encontramos un movimiento de gato que, tras el movimiento del ratón, lo deja sin salida.
                    gatoPuedeAtrapar = true;
                    break;
                }
            }
            if (!gatoPuedeAtrapar) {
                return false;
            }
        }
        return true;
    }

    private boolean esVictoriaRaton() {
        int filaMasBajaGato = encontrarFilaMasBajaGato();
        // El ratón gana si está por debajo de la fila más baja de los gatos o si llega a la fila 7
        return ratonFila > filaMasBajaGato || ratonFila == 7;
    }

    private int encontrarFilaMasBajaGato() {
        int filaMasBaja = 0;
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int col = 0; col < TAMANO; col++) {
                if (casillas[fila][col] instanceof Gato) {
                    filaMasBaja = Math.max(filaMasBaja, fila);
                }
            }
        }
        return filaMasBaja;
    }

    private void mostrarVictoriaGatos() {
        JOptionPane.showMessageDialog(null, "¡Los gatos han ganado!", "Victoria de los Gatos", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private void mostrarVictoriaRaton() {
        JOptionPane.showMessageDialog(null, "¡El ratón ha ganado!", "Victoria del Ratón", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    private class Resultado {
        int valor;
        Movimiento movimiento;
        public Resultado(int valor, Movimiento movimiento) {
            this.valor = valor;
            this.movimiento = movimiento;
        }
    }

    private boolean esTerminal() {
        return esVictoriaRaton() || esVictoriaGatos();
    }

    // Minimaxi con poda alfa-beta usando clonación del estado para simular movimientos
    private Resultado minimaxAlphaBeta(int profundidad, boolean esTurnoGatos, int alpha, int beta) {
        if (profundidad == 0 || esTerminal()) {
            return new Resultado(evaluarTablero(), null);
        }

        List<Movimiento> movimientos = esTurnoGatos ? obtenerMovimientosGatos() : obtenerMovimientosRaton();
        if (movimientos.isEmpty()) {
            return new Resultado(evaluarTablero(), null);
        }

        Resultado mejorResultado = null;
        if (esTurnoGatos) {
            int maxEval = Integer.MIN_VALUE;
            Movimiento bestMove = null;
            for (Movimiento mov : movimientos) {
                Turnos clon = this.cloneState();
                clon.moverPieza(mov.filaOrigen, mov.colOrigen, mov.filaDestino, mov.colDestino);
                Resultado res = clon.minimaxAlphaBeta(profundidad - 1, false, alpha, beta);
                int eval = res.valor;
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMove = mov;
                }
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) break;
            }
            mejorResultado = new Resultado(maxEval, bestMove);
        } else {
            int minEval = Integer.MAX_VALUE;
            Movimiento bestMove = null;
            for (Movimiento mov : movimientos) {
                Turnos clon = this.cloneState();
                clon.moverPieza(mov.filaOrigen, mov.colOrigen, mov.filaDestino, mov.colDestino);
                Resultado res = clon.minimaxAlphaBeta(profundidad - 1, true, alpha, beta);
                int eval = res.valor;
                if (eval < minEval) {
                    minEval = eval;
                    bestMove = mov;
                }
                beta = Math.min(beta, eval);
                if (beta <= alpha) break;
            }
            mejorResultado = new Resultado(minEval, bestMove);
        }
        return mejorResultado;
    }

    // Método para evaluar la posición en el tablero con heurística mejorada
    private int evaluarTablero() {
        int puntuacion = 0;
        // Penalizar al ratón si tiene pocos movimientos disponibles
        int ratonMovimientosValidos = obtenerMovimientosRaton().size();
        puntuacion += (4 - ratonMovimientosValidos) * 200;
        // Penalización fuerte si el ratón está cerca de ganar
        puntuacion -= (ratonFila - 4) * 500;
        // Evaluar la posición de los gatos y su capacidad de acorralar al ratón
        int gatosCercanos = 0;
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int col = 0; col < TAMANO; col++) {
                if (casillas[fila][col] instanceof Gato) {
                    int distancia = Math.abs(fila - ratonFila) + Math.abs(col - ratonColumna);
                    if (distancia == 0) {
                        puntuacion += 500;  // Captura inmediata
                    } else if (distancia == 1) {
                        puntuacion += 300;  // Bloqueo fuerte
                        gatosCercanos++;
                    } else if (distancia == 2) {
                        puntuacion += 150;  // Presión
                    } else {
                        puntuacion -= (distancia - 2) * 30; // Penalizar si están muy lejos
                    }
                }
            }
        }
        // Bonificación si hay al menos 2 gatos cerca para cooperar
        if (gatosCercanos >= 2) {
            puntuacion += 400;
        }
        return puntuacion;
    }

    // Método mejorado para obtener y ordenar los movimientos de los gatos
    private List<Movimiento> obtenerMovimientosGatos() {
        List<Movimiento> movimientos = new ArrayList<>();
        for (int fila = 0; fila < TAMANO; fila++) {
            for (int col = 0; col < TAMANO; col++) {
                if (casillas[fila][col] instanceof Gato) {
                    int[] direcciones = {-1, 1};
                    for (int dir : direcciones) {
                        int nuevaFila = fila - 1;
                        int nuevaCol = col + dir;
                        if (esMovimientoValido(fila, col, nuevaFila, nuevaCol)) {
                            movimientos.add(new Movimiento(fila, col, nuevaFila, nuevaCol));
                        }
                    }
                }
            }
        }
        movimientos.sort(Comparator.comparingInt(mov -> {
            int d = Math.abs(mov.filaDestino - ratonFila) + Math.abs(mov.colDestino - ratonColumna);
            return d;
        }));
        return movimientos;
    }

    // Método mejorado para obtener los movimientos válidos del ratón
    private List<Movimiento> obtenerMovimientosRaton() {
        List<Movimiento> movimientos = new ArrayList<>();
        int[] diagonales = {-1, 1};
        for (int d1 : diagonales) {
            for (int d2 : diagonales) {
                int nuevaFila = ratonFila + d1;
                int nuevaCol = ratonColumna + d2;
                if (esMovimientoValido(ratonFila, ratonColumna, nuevaFila, nuevaCol)) {
                    movimientos.add(new Movimiento(ratonFila, ratonColumna, nuevaFila, nuevaCol));
                }
            }
        }
        movimientos.sort(Comparator.comparingInt(mov -> {
            int d = Math.abs(mov.filaDestino - ratonFila) + Math.abs(mov.colDestino - ratonColumna);
            return -d;
        }));
        return movimientos;
    }

    public Jugadores getPieza(int fila, int col) {
        return casillas[fila][col];
    }
}
