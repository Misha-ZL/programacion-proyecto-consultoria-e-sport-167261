import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class AÑADIR_JUGADOR {

    private JButton ADD;
    JPanel ADDJUGADOR;
    private JTextField Nombre;
    private JTextField Apellido;
    private JTextField apodo;
    private JTextField Salario;

    static Connection conn = GestorBD.conectar();

    public AÑADIR_JUGADOR() {
        añadirJugador();
    }

    public static boolean validarSalario(double salario) {
        return salario < 200000;
    }

    public void añadirJugador() {

        ADD.addActionListener(new ActionListener() {

            /**
             * @author Alberto
             * Añade un nuevo jugador a la Base de Datos
             * @param e evento a ser ejecutado
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String nombre = Nombre.getText();
                    String apellido = Apellido.getText();
                    String nick = apodo.getText();
                    double salario = Double.parseDouble(Salario.getText());

                    if (nombre.isEmpty() || apellido.isEmpty() || nick.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    if (Salario.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Por favor, ingresa un salario.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);


                        return;
                    }

                    /**
                     * verifica que el salario no supere los 200.000€
                     * @param salario
                     * @return true si el salario es correcto y esta dentro de los parametros
                     * @return false si no se valida el salario
                     */


                    if (validarSalario(salario)) {
                        String sql = "INSERT INTO JUGADOR (NOMBRE, APELLIDO, NICKNAME, SALARIO, ID_EQUIPO) VALUES (?, ?, ?, ?, NULL)";
                        PreparedStatement stmt = conn.prepareStatement(sql);

                        stmt.setString(1, nombre);
                        stmt.setString(2, apellido);
                        stmt.setString(3, nick);
                        stmt.setDouble(4, salario);

                        int filasAfectadas = stmt.executeUpdate();

                        if (filasAfectadas > 0) {
                            JOptionPane.showMessageDialog(null, "Jugador añadido correctamente.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);


                        } else {
                            JOptionPane.showMessageDialog(null, "No se pudo añadir el jugador.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                        }

                        stmt.close();
                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo añadir el jugador. SALARIO SUPERIOR AL LIMTE", "ATENCION.!", JOptionPane.WARNING_MESSAGE);


                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "El salario debe ser un número válido.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error en la base de datos:" + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);

                }
            }
        });
    }


}


