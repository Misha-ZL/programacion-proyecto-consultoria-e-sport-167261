import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class VRegistrarse {
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton button1;
    private JTextField textField2;
    JPanel panelCrearUsuario;
    private JPasswordField passwordField2;

    /**
     * @author arnau
     * Genera la ventana para crear un nuevo usuario,
     * y lo guarda en la base de datos
     * @see AÑADIR_USUARIOS
     */
    public VRegistrarse() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombreUsu = textField1.getText();
                String contraseña = passwordField1.getText();
                String contraseña2 = passwordField2.getText();
                String ape = textField2.getText();
                String valor = "USUARIO";


                if (nombreUsu.isEmpty() || ape.isEmpty() || contraseña.isEmpty() || contraseña2.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, rellena todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (!contraseña2.equals(contraseña)) {
                    JOptionPane.showMessageDialog(null, "Contraseñas no coinciden.", "contraseñas diferentes", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                Connection conexion = GestorBD.conectar();

                try {
                    int filasAfectadas = 0;


                    String sql = "INSERT INTO USUARIOS (ID_EQUIPO, NOMBRE, APELLIDO, CONTRASEINA, ENUM_TIPOS) VALUES (NULL, ?, ?, ?, ?)";
                    PreparedStatement st = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    st.setString(1, nombreUsu);
                    st.setString(2, ape);
                    st.setString(3, contraseña);
                    st.setString(4, valor);

                    filasAfectadas = st.executeUpdate();
                    if (filasAfectadas > 0) {

                        JOptionPane.showMessageDialog(null, "Se ha añadido correctamente el usuario.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);

                    }


                    st.close();
                    conexion.close();


                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al insertar: " + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

    }
}

