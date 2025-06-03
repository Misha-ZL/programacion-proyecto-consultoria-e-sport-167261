import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class AÑADIR_EQUIPOS {


    private JTextField Presupuesto;
    private JButton AÑADIR;
    JPanel AÑADIREQUIPOS;
    private JTextField NombreE;


    public AÑADIR_EQUIPOS() {
        Connection conn = GestorBD.conectar();

        AÑADIR.addActionListener(new ActionListener() {
            /**
             * @author Misha
             * Añade un nuevo equipo a la Base de Datos.
             * verificando que su presupuesto total no supere los 200k€
             * @param e evento a ejecutarse
             */
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String Nombre = NombreE.getText();
                    double presupuesto = Double.parseDouble(Presupuesto.getText());

                    if (Nombre.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El nombre del equipo no puede estar vacío.", "ATENCIÓN.!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }



                    String sql = "INSERT INTO EQUIPO(NOMBRE_EQUIPO,PRESUPUESTO,PUNTUACION) VALUES (?,?,?)";

                    PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    stmt.setString(1, Nombre);
                    stmt.setDouble(2, presupuesto);
                    stmt.setInt(3, 0);

                    int filasAfectadas = stmt.executeUpdate();

                    if (presupuesto <= 200000) {


                        if (filasAfectadas > 0) {
                            JOptionPane.showMessageDialog(null, "Equipo añadido correctamente.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);

                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El salario debe ser un valor entre 0 y 200000.", "ERROR!", JOptionPane.ERROR_MESSAGE);


                    }

                    conn.close();
                    stmt.close();

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                } catch (NumberFormatException ex) {

                    JOptionPane.showMessageDialog(null, "El salario debe ser un número válido.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);

                }
            }
        });
    }


}
