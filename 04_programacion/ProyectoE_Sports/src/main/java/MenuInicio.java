import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuInicio {
    private JTextField NombreUsu;
    private JButton Entrar;
    private JPasswordField UsuPass;
    JPanel MenuInicio;
    private JButton registrarseButton;

    private static List<Equipo> listaEquipos = new ArrayList<>();
    private List<Jugador> listaJugadores = new ArrayList<>();

    int filasAfectadas = 0;
    String usupass = new String(UsuPass.getText());
    String nombreusu = NombreUsu.getText();


    public MenuInicio() {
        Entrar.addActionListener(new ActionListener() {
            /**
             * @author Misha
             * Permite acceder a la aplicacion siempre que el usuario(nombreusu) y contraseña(usupass) sean correctos,
             * mostrando la pantalla correspondiente segun el nivel de permisos que poseea ese usuario
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String usupass = new String(UsuPass.getText());
                String nombreusu = NombreUsu.getText();

                String nombre = "";
                String pass = "";
                Tipo_usuario tipo = null;
                int id_equipo = 0;
                try {
                    Connection conexion = GestorBD.conectar();
                    String sql = "SELECT * FROM USUARIOS WHERE NOMBRE = ? AND CONTRASEINA = ?";
                    PreparedStatement ps = conexion.prepareStatement(sql);
                    ps.setString(1, nombreusu);
                    ps.setString(2, usupass);
                    ResultSet rs = ps.executeQuery();

                    obtenerDueño_Equipo(nombreusu);

                    while (rs.next()) {

                        nombre = rs.getString("NOMBRE");
                        pass = rs.getString("CONTRASEINA");
                        id_equipo = rs.getInt("ID_EQUIPO");
                        tipo = Tipo_usuario.valueOf(rs.getString("ENUM_TIPOS"));
                    }

                    if (usupass.equals(pass) && nombre.equals(nombre)) {
                        if (tipo == Tipo_usuario.ADMINISTRADOR) {

                            JFrame frame = new JFrame("ADMIN");
                            frame.setContentPane(new ADMIN().ADMIN);

                            frame.pack();
                            frame.setVisible(true);

                        } else if (tipo == Tipo_usuario.USUARIO) {
                            JFrame frame = new JFrame("USUARIOSResuslt");
                            frame.setContentPane(new USUARIOSResuslt().VerDatosUsu);
                            frame.pack();
                            frame.setVisible(true);

                        } else if (tipo == Tipo_usuario.DUENOS) {
                            obtenerDueño_Equipo(nombreusu);
                            JFrame frame = new JFrame("DUEÑO");
                            frame.setContentPane(new DUEÑO(id_equipo).panelDuenio);

                            frame.pack();
                            frame.setVisible(true);

                        } else {

                            JOptionPane.showMessageDialog(null, "Tipo de usuario desconocido.", "ATENCION!", JOptionPane.WARNING_MESSAGE);
                        }


                    } else {
                        JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos.", "ERROR!", JOptionPane.ERROR_MESSAGE);

                    }
                    rs.close();
                    ps.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }


            }
        });

        /**
         * @author Arnau
         * Muestra la ventana de registro de nuevo usuario
         */
        registrarseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame frame = new JFrame("VRegistrarse");
                frame.setContentPane(new VRegistrarse().panelCrearUsuario);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    /**
     * @param nombreusu el nombre del usuario
     * @return el equipo que pertenece a ese dueño
     * @author Misha
     * Guarda el nombre de usuario.
     * Obtiene y guarda el nombre del usuario que ha entrado como dueño,
     * para pasarle el equipo de dicho dueño a la clase de dueño y modificar_equipo_dueños
     */
    public static Equipo obtenerDueño_Equipo(String nombreusu) {
        Equipo equipoDelDueno = null;
        for (Equipo eq : listaEquipos) {
            if (eq.getUsuario().getNombre().equals(nombreusu)) {
                equipoDelDueno = eq;
                break;
            }
        }
        return equipoDelDueno;
    }


}


