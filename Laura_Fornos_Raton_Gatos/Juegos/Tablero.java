package Juegos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tablero extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton[][] botones;
    private Turnos turnos;
    private ImageIcon iconoGato, iconoRaton;

    public Tablero() {
        turnos = new Turnos();
        botones = new JButton[8][8];

        try {
            iconoGato = new ImageIcon(getClass().getResource("/Juegos/gato.png"));
            iconoRaton = new ImageIcon(getClass().getResource("/Juegos/raton.png"));
            iconoGato = new ImageIcon(iconoGato.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
            iconoRaton = new ImageIcon(iconoRaton.getImage().getScaledInstance(60, 60, Image.SCALE_DEFAULT));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar las imágenes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        setTitle("Ratón y Gatos");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(8, 8));

        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                JButton boton = new JButton();
                botones[fila][col] = boton;

                if ((fila + col) % 2 == 0) {
                    boton.setBackground(Color.WHITE);
                } else {
                    boton.setBackground(Color.BLACK);
                }

                int finalFila = fila;
                int finalCol = col;
                boton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        turnos.manejarClick(finalFila, finalCol);
                        actualizarTablero();
                    }
                });

                add(boton);
            }
        }

        setVisible(true);
        actualizarTablero();
    }

    private void actualizarTablero() {
        for (int fila = 0; fila < 8; fila++) {
            for (int col = 0; col < 8; col++) {
                JButton boton = botones[fila][col];
                Jugadores pieza = turnos.getPieza(fila, col);

                boton.setIcon(pieza instanceof Gato ? iconoGato : pieza instanceof Raton ? iconoRaton : null);
            }
        }
    }
}
