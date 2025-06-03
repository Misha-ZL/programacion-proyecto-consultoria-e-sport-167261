

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Main {
    /**
     * @author Misha
     * Titulo del proyecto: ProyectoE_Sports
     * Proyecto sobre la creacion de una aplicacion de consultoria sobre un torneo de E_Sports.
     * Manual de Usuario: Para arrancar el proyecto ejecutar el main de la clase main
     * Autores: Alberto Ortiz, Arnau Rubio, Jon Sagar, Misha Zugazua
     * @param args array de texto
     * @throws IOException en caso de un error E/S
     */
    public static void main(String[] args) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));


        JFrame frame = new JFrame("MenuInicio");
        frame.setContentPane(new MenuInicio().MenuInicio);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);


    }
}
