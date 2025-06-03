import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class GenerarJornada {
    private JTextField FJornada;
    private JButton crearJornadaButton;
    JPanel GenerarJornada;

    /**
     * @author Misha
     * Permite la creacion de una jornada con todos sus datos asociados
     */
    public GenerarJornada() {
        crearJornadaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fechaTexto = FJornada.getText();

                if (fechaTexto.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor introduce una fecha.");
                    return;
                }

                try {
                    java.sql.Date fechaSQL = java.sql.Date.valueOf(fechaTexto); // Debe estar en formato YYYY-MM-DD

                    Connection conn = GestorBD.conectar();
                    String sql = "INSERT INTO JORNADA (FECHA,PUNTOSTOT) VALUES (?,0)";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setDate(1, fechaSQL);
                    int filas = ps.executeUpdate();

                    if (filas > 0) {
                        JOptionPane.showMessageDialog(null, "Jornada creada exitosamente.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al crear la jornada.");
                    }

                    ps.close();
                    conn.close();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(null, "Fecha inválida. Usa formato YYYY-MM-DD.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al insertar la jornada.");
                }
            }
        });
    }


}
