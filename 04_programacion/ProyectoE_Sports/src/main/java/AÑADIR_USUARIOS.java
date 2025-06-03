import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class AÑADIR_USUARIOS {
    JPanel panel1;
    private JButton add;
    private JTextField NombreUsu;
    private JTextField Apellido;
    private JTextField pass;
    private JComboBox<Tipo_usuario> TipoUsuarios;


    public AÑADIR_USUARIOS() {


        TipoUsuarios.removeAllItems();
        for (Tipo_usuario tipo : Tipo_usuario.values()) {
            TipoUsuarios.addItem(tipo);
        }


        add.addActionListener(new ActionListener() {

            /**
             * @author Misha
             * Añade un nuevo usuario.
             * Añade un usuario a la Base de Datos
             * @param e evento a ser ejecutado
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                String nombre = NombreUsu.getText();
                String ape = Apellido.getText();
                String contraseña = pass.getText();
                Tipo_usuario seleccionado = (Tipo_usuario) TipoUsuarios.getSelectedItem();

                if (nombre.isEmpty() || ape.isEmpty() || contraseña.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Por favor, rellena todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (seleccionado == null) {
                    JOptionPane.showMessageDialog(null, "Selecciona un tipo de usuario.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String valor = seleccionado.name();
                Connection conexion = GestorBD.conectar();

                try {
                    int filasAfectadas = 0;


                    String sql = "INSERT INTO USUARIOS (ID_EQUIPO, NOMBRE, APELLIDO, CONTRASEINA, ENUM_TIPOS) VALUES (NULL, ?, ?, ?, ?)";
                    PreparedStatement st = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    st.setString(1, nombre);
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
