import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MODIFICAR_USUARIO {
    private JTextField ID_EQUIPO;
    private JTextField NOMBRE;
    private JTextField APELLIDO;
    private JTextField PASS;
    private JButton MODIFICARButton;
    private JList<String> NOMBRE_USUARIOS;
    JPanel MODIFICAR;
    private JComboBox<Tipo_usuario> TipoUsuario;
    public JPanel panelMain;

    private DefaultListModel<String> usuarioModel;
    private List<Usuario> listaUsuario = new ArrayList<>();

    /**
     * @author Misha
     * Carga la informacion de los usarios
     */
    public MODIFICAR_USUARIO() {

        TipoUsuario.removeAllItems();
        for (Tipo_usuario tipo : Tipo_usuario.values()) {
            TipoUsuario.addItem(tipo);
        }

        usuarioModel = new DefaultListModel<>();
        NOMBRE_USUARIOS.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        NOMBRE_USUARIOS.setModel(usuarioModel);

        int id_usu;
        int id_equipo = 0;
        String Nombre;
        String Contraseina;
        String apellido;

        Connection conn = GestorBD.conectar();
        try {
            Statement stmt = conn.createStatement();
            usuarioModel.clear();
            listaUsuario.clear();
            ResultSet rs = stmt.executeQuery("SELECT * FROM USUARIOS");


            while (rs.next()) {
                id_usu = rs.getInt("ID_USUARIO");
                id_equipo = rs.getInt("ID_EQUIPO");
                Nombre = rs.getString("NOMBRE");
                Contraseina = rs.getString("CONTRASEINA");
                apellido = rs.getString("APELLIDO");
                Tipo_usuario tipo = Tipo_usuario.valueOf(rs.getString("ENUM_TIPOS"));


                Usuario usuario = new Usuario(id_usu, Nombre, Contraseina, apellido, tipo, null);


                listaUsuario.add(usuario);


                usuarioModel.addElement(rs.getInt("ID_USUARIO") + "         " + rs.getString("NOMBRE") + "  " + rs.getString("APELLIDO"));


            }
            stmt.close();
            rs.close();
            conn.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar usuarios: " + e.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);


        }

        /**
         * @author Misha
         * Obtiene los datos del usuasrio mediante la conexion a la base de datos,
         * empezando por el id
         */
        NOMBRE_USUARIOS.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = NOMBRE_USUARIOS.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        String nombreSeleccionado = usuarioModel.getElementAt(selectedIndex);

                        // Conexión directa a la BD para obtener el ID_USUARIO
                        Connection conn = GestorBD.conectar();
                        try {
                            String query = "SELECT ID_USUARIO, APELLIDO, CONTRASEINA, ENUM_TIPOS, ID_EQUIPO FROM USUARIOS WHERE NOMBRE = ?";
                            PreparedStatement stmt = conn.prepareStatement(query);
                            stmt.setString(1, nombreSeleccionado);
                            ResultSet rs = stmt.executeQuery();

                            if (rs.next()) {
                                int id_usuario = rs.getInt("ID_USUARIO");
                                String apellido = rs.getString("APELLIDO");
                                String pass = rs.getString("CONTRASEINA");
                                Tipo_usuario tipo = Tipo_usuario.valueOf(rs.getString("ENUM_TIPOS"));
                                TipoUsuario.setSelectedItem(tipo);
                                int id_equipo = rs.getInt("ID_EQUIPO");


                                APELLIDO.setText(apellido);
                                PASS.setText(pass);
                                if (id_equipo != 0) {
                                    ID_EQUIPO.setText(String.valueOf(id_equipo));
                                } else {
                                    ID_EQUIPO.setText("");
                                }

                                NOMBRE.setText(nombreSeleccionado);
                            }

                            rs.close();
                            stmt.close();
                            conn.close();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(null, "Error al obtener datos del usuario: " + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);

                        }
                    }
                }
            }
        });


        /**
         * author Misha
         * Permite modificar la informacion de los usuarios
         * @param e evento a ser ejecutado
         */
        MODIFICARButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int UsuariosSeleccionado = NOMBRE_USUARIOS.getSelectedIndex();

                if (UsuariosSeleccionado < 0) {
                    JOptionPane.showMessageDialog(null, "Selecciona un usuario para modificar.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);

                    return;
                }

                Usuario usuario = listaUsuario.get(UsuariosSeleccionado);

                String nombre = NOMBRE.getText();
                String apellido = APELLIDO.getText();
                String pass = PASS.getText();
                Tipo_usuario tipo = (Tipo_usuario) TipoUsuario.getSelectedItem();
                String idEquipoText = ID_EQUIPO.getText();

                Connection conn = GestorBD.conectar();
                try {
                    String sql = "UPDATE USUARIOS SET NOMBRE = ?, APELLIDO = ?, CONTRASEINA = ?, ENUM_TIPOS = ?, ID_EQUIPO = ? WHERE ID_USUARIO= ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, nombre);
                    stmt.setString(2, apellido);
                    stmt.setString(3, pass);
                    stmt.setString(4, tipo.name());

                    if (idEquipoText.isEmpty()) {
                        stmt.setNull(5, Types.INTEGER);
                    } else {
                        stmt.setInt(5, Integer.parseInt(idEquipoText));
                    }

                    stmt.setInt(6, usuario.getID_usuario());

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        JOptionPane.showMessageDialog(null, "Usuario modificado correctamente.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);

                        usuario.setNombre(nombre);
                        usuario.setApellido(apellido);
                        usuario.setTipoUsuario(tipo);

                    } else {
                        JOptionPane.showMessageDialog(null, "No se encontró el usuario a modificar.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);

                    }

                    stmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al modificar usuario: " + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);

                }
            }
        });

    }


}
