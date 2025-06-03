import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ADMIN {
    private JButton SIGUIENTE;
    JPanel ADMIN;

    private JButton SIGUIENTE_P;
    private JButton MODEQUI;
    private JButton SIGUIENTEButton;

    private JComboBox USUARIOS;
    private JComboBox JUGADORES;
    private JComboBox EQUIPOS;
    private JButton CALNEDARbUTTON1;
    private JButton siguienteButton;


    public ADMIN() {
        /**
         * @author Misha
         * Seleccion de atributos.
         * Permite seleccionar que accion realizar en cada atributo de los combobox para modificar
         * las clases de usuarios, equipos y jugadorees
         */

        String[] jugadoresOpciones = {"AÑADIR JUGADOR", "ELIMINAR JUGADOR", "MODIFICAR JUGADOR"};
        for (String opcion : jugadoresOpciones) {
            JUGADORES.addItem(opcion);
        }

        String[] jugadoresOpcion = {"AÑADIR JUGADOR", "ELIMINAR JUGADOR", "MODIFICAR JUGADOR"};
        for (String opcion : jugadoresOpcion) {
            JUGADORES.addItem(opcion);
        }

        String[] equiposOpciones = {"AÑADIR EQUIPO", "ELIMINAR EQUIPO", "MODIFICAR EQUIPO"};
        for (String opcion : equiposOpciones) {
            EQUIPOS.addItem(opcion);
        }

        String[] usuariosOpciones = {"AÑADIR USUARIO", "ELIMINAR USUARIO", "MODIFICAR USUARIO"};
        for (String opcion : usuariosOpciones) {
            USUARIOS.addItem(opcion);
        }
        SIGUIENTE.addActionListener(new ActionListener() {
            /**
             * Genera las ventanas para añadir, Eliminar y modificar usuarios
             * @param e evento a ejecutarse
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) USUARIOS.getSelectedItem();
                if (seleccion != null) {
                    switch (seleccion.trim()) {
                        case "AÑADIR USUARIO":
                            JFrame frameAdd = new JFrame("AÑADIR_USUARIO");
                            frameAdd.setContentPane(new AÑADIR_USUARIOS().panel1);
                            frameAdd.pack();
                            frameAdd.setVisible(true);
                            break;
                        case "ELIMINAR USUARIO":
                            JFrame frameDel = new JFrame("ELIMINAR_USUARIO");
                            frameDel.setContentPane(new ELIMINAR_USUARIO().ELIMINAR);
                            frameDel.pack();
                            frameDel.setVisible(true);
                            break;
                        case "MODIFICAR USUARIO":
                            JFrame frameMod = new JFrame("MODIFICAR_USUARIO");
                            frameMod.setContentPane(new MODIFICAR_USUARIO().MODIFICAR);
                            frameMod.pack();
                            frameMod.setVisible(true);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Opción no reconocida.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona una opción de usuarios.");
                }
            }
        });


        SIGUIENTE_P.addActionListener(new ActionListener() {
            /**
             * @author Misha
             * Genera las ventanas.
             * para añadir, Eliminar y modificar jugadores
             * @param e evento a ejecutarse
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) JUGADORES.getSelectedItem();
                if (seleccion != null) {
                    switch (seleccion.trim()) {
                        case "AÑADIR JUGADOR":
                            JFrame frame = new JFrame("AÑADIR_JUGADOR");
                            frame.setContentPane(new AÑADIR_JUGADOR().ADDJUGADOR);
                            frame.pack();
                            frame.setVisible(true);
                            break;
                        case "ELIMINAR JUGADOR":
                            JFrame frameEL = new JFrame("ELIMINAR_JUGADOR");
                            frameEL.setContentPane(new ELIMINAR_JUGADOR().ELIMINARJUGADOR);
                            frameEL.pack();
                            frameEL.setVisible(true);
                            break;
                        case "MODIFICAR JUGADOR":
                            JFrame frameMOD = new JFrame("MODIFICAR_JUGADOR");
                            try {
                                frameMOD.setContentPane(new MODIFICAR_JUGADOR().MODJUGADOR);
                                frameMOD.pack();
                                frameMOD.setVisible(true);
                            } catch (SQLException ex) {
                                throw new RuntimeException(ex);
                            }
                            break;

                        default:
                            JOptionPane.showMessageDialog(null, "Opción no reconocida.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona una opción de jugadores.");
                }
            }
        });
        MODEQUI.addActionListener(new ActionListener() {
            /**
             * @author Misha
             * Genera las ventanas
             * para añadir, Eliminar y modificar equipos
             * @param e evento a ejecutarse
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String seleccion = (String) EQUIPOS.getSelectedItem();
                if (seleccion != null) {
                    switch (seleccion.trim()) {
                        case "AÑADIR EQUIPO":

                            JFrame frame = new JFrame("AÑADIR_EQUIPOS");
                            frame.setContentPane(new AÑADIR_EQUIPOS().AÑADIREQUIPOS);
                            frame.pack();
                            frame.setVisible(true);
                            break;
                        case "ELIMINAR EQUIPO":
                            JFrame frameEL = new JFrame("ELIMINAR_EQUIPO");
                            frameEL.setContentPane(new ELIMINAR_EQUIPO().EliminarEquipo);
                            frameEL.pack();
                            frameEL.setVisible(true);

                            break;
                        case "MODIFICAR EQUIPO":
                            JFrame frameMod = new JFrame("MODIFICAR_EQUIPO");
                            frameMod.setContentPane(new MODIFICAR_EQUIPO().MODEQUIPOS);
                            frameMod.pack();
                            frameMod.setVisible(true);


                            break;

                        default:
                            JOptionPane.showMessageDialog(null, "Opción no reconocida.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecciona una opción de jugadores.");
                }

            }
        });
        SIGUIENTEButton.addActionListener(new ActionListener() {
            /**
             * @author Alberto
             * Genera la ventana
             * de calendario y permite modificarla
             * @param e evento a ejecutarse
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("VENTANACALENDARIO");
                frame.setContentPane(new IntroducirResultadosJornadas().panelnew);
                frame.pack();
                frame.setVisible(true);

            }
        });



        CALNEDARbUTTON1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("GenerarCALENDARIOfinal");
                frame.setContentPane(new GenerarCALENDARIOfinal().panel);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("ADMIN");
        frame.setContentPane(new ADMIN().ADMIN);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
