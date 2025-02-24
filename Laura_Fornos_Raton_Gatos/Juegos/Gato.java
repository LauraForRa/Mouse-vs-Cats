package Juegos;

import java.util.ArrayList;
import java.util.List;

public class Gato extends Jugadores {
    public Gato(int fila, int columna) {
        super(fila, columna);
    }

    @Override
    public boolean puedeMoverse(Turnos turnos) {
        int[] movimientos = {-1, 1};
        List<Movimiento> movimientosPosibles = new ArrayList<>();

        for (int movCol : movimientos) {
            int nuevaFila = this.fila + 1;
            int nuevaColumna = this.columna + movCol;

            if (turnos.esMovimientoValido(this.fila, this.columna, nuevaFila, nuevaColumna)) {
                movimientosPosibles.add(new Movimiento(this.fila, this.columna, nuevaFila, nuevaColumna));
            }
        }

        return !movimientosPosibles.isEmpty();
    }
}