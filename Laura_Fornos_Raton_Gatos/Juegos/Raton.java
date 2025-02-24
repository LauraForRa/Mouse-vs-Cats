package Juegos;

public class Raton extends Jugadores {
    public Raton(int fila, int columna) {
        super(fila, columna);
    }

    @Override
    public boolean puedeMoverse(Turnos turnos) {
        int[] movimientos = {-1, 1};
        for (int movFila : movimientos) {
            for (int movCol : movimientos) {
                int nuevaFila = this.fila + movFila;
                int nuevaColumna = this.columna + movCol;

                if (turnos.esMovimientoValido(this.fila, this.columna, nuevaFila, nuevaColumna)) {
                    return true;
                }
            }
        }
        return false;
    }
}