import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ELIMINAR_USUARIO {
    private JList Usuarios;
    JPanel ELIMINAR;
    private JButton ELIMINARButton;
    private DefaultListModel<String> UsuarioModel;

    /**
     * @author Misha
     * Carga los usuarios en la ventana
     */
    public ELIMINAR_USUARIO() {
        UsuarioModel = new DefaultListModel<>();
        Usuarios.setModel(UsuarioModel);
        Usuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Connection conn = GestorBD.conectar();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM USUARIOS");

            UsuarioModel.clear();
            while (rs.next()) {
                UsuarioModel.addElement(rs.getInt("ID_USUARIO") + "         " + rs.getString("NOMBRE") + "  " + rs.getString("APELLIDO"));
            }

            conn.close();
            stmt.close();
            rs.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios:" + e.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);
        }

        /**
         * @author Misha
         * Elimina un usuario de la BD
         * @param e evento a ser ejecutado
         */
        ELIMINARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String seleccionado = (String) Usuarios.getSelectedValue();

                if (seleccionado == null) {
                    JOptionPane.showMessageDialog(null, "Selecciona un usuario para eliminar.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Connection conn = GestorBD.conectar();
                try {
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM USUARIOS WHERE NOMBRE = ?");
                    stmt.setString(1, seleccionado);
                    int rows = stmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(null, "Usuario eliminado correctamente.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);
                        UsuarioModel.removeElement(seleccionado);
                    } else {
                        JOptionPane.showMessageDialog(null, "No se encontró el usuario.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                    }

                    conn.close();
                    stmt.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al eliminar: " + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

}
