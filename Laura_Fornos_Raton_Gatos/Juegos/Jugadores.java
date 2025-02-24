package Juegos;

public abstract class Jugadores {
    protected int fila, columna;

    public Jugadores(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public abstract boolean puedeMoverse(Turnos turnos);

    public void setPosicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}