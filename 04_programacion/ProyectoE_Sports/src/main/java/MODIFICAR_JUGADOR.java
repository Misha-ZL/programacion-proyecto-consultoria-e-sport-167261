import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MODIFICAR_JUGADOR {

    private JTextField NOMBREJUGADOR;
    private JTextField APELLIDOJUGADOR;
    private JTextField NICKNAME;
    private JTextField SALARIOJUGADOR;
    // private JList EQUIPOS;
    private JList NOMBREJUGADORES;
    JPanel MODJUGADOR;
    private JButton MODIFICAR;
    private JComboBox<String> Equipos;
    private DefaultListModel<String> modeloJugadores = new DefaultListModel<>();
    private DefaultListModel<String> modeloEquipos = new DefaultListModel<>();
    private List<Jugador> listaJugadores = new ArrayList<>();
    private List<Equipo> listaEquipos = new ArrayList<>();


    /**
     * @throws SQLException si ocurre algun error en la carga de datos
     * @author Misha
     * Carga la informacion de los equipos
     */
    public MODIFICAR_JUGADOR() throws SQLException {

        NOMBREJUGADORES.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        NOMBREJUGADORES.setModel(modeloJugadores);

        Equipos.removeAllItems();
        //CARGAR EQUIPOS

        Connection connn = GestorBD.conectar();
        try {
            String sql = "SELECT * FROM EQUIPO";
            Statement stmt = connn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("ID_EQUIPO");
                String nombre = rs.getString("NOMBRE_EQUIPO");
                double presupuesto = rs.getDouble("PRESUPUESTO");
                int puntuacion = rs.getInt("PUNTUACION");

                Equipo equipo = new Equipo(id, puntuacion, presupuesto, nombre, null, null);
                listaEquipos.add(equipo);
                Equipos.addItem(nombre);
            }

            connn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar equipos.", "ERROR.!", JOptionPane.ERROR_MESSAGE);

        }
        //FIN CARGAR EQUIPOS


        /**
         * @author Misha
         * Carga la informacion de los jugadores
         */
        Connection conn = GestorBD.conectar();
        try {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM JUGADOR");

            while (rs.next()) {
                int id = rs.getInt("ID_JUGADOR");
                String nombre = rs.getString("NOMBRE");
                String apellido = rs.getString("APELLIDO");
                String nickname = rs.getString("NICKNAME");
                double salario = rs.getDouble("SALARIO");
                int idEquipo = rs.getInt("ID_EQUIPO");

                Equipo equipoJugador = null;
                for (Equipo equipo : listaEquipos) {
                    if (equipo.getID_Equipo() == idEquipo) {
                        equipoJugador = equipo;
                        break;
                    }
                }

                try {
                    if (salario < 200000) {
                        Jugador jugador = new Jugador(salario, nickname, apellido, nombre, id, equipoJugador);
                        listaJugadores.add(jugador);
                        modeloJugadores.addElement(nombre + " " + apellido);

                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo añadir el jugador..", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "El salario debe ser un número válido.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                }

            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar jugadores: " + e.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);
        }
        //FIN CARGAR JUGADORES


        NOMBREJUGADORES.addListSelectionListener(e -> {

            int jugadorSeleccionado = NOMBREJUGADORES.getSelectedIndex();

            if (jugadorSeleccionado >= 0) {
                Jugador seleccionado = listaJugadores.get(jugadorSeleccionado);

                NOMBREJUGADOR.setText(seleccionado.getNombre());
                APELLIDOJUGADOR.setText(seleccionado.getApellido());
                NICKNAME.setText(seleccionado.getNickname());
                SALARIOJUGADOR.setText(String.valueOf(seleccionado.getSalario()));


                if (seleccionado.getEquipo() != null) {
                    for (int i = 0; i < listaEquipos.size(); i++) {
                        if (listaEquipos.get(i).getID_Equipo() == seleccionado.getEquipo().getID_Equipo()) {
                            Equipos.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecciona un jugador primero.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
            }

        });


        /**
         * @author Misha
         * Permite la modificacion de los datos de cada jugador,
         * y los guarda en la base de datos
         */
        MODIFICAR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int jugadorSeleccionado = NOMBREJUGADORES.getSelectedIndex();

                if (jugadorSeleccionado < 0) {
                    JOptionPane.showMessageDialog(null, "Selecciona un jugador para modificar.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                Jugador jugador = listaJugadores.get(jugadorSeleccionado);


                String nuevoNombre = NOMBREJUGADOR.getText();
                String nuevoApellido = APELLIDOJUGADOR.getText();
                String nuevoNickname = NICKNAME.getText();
                double nuevoSalario;

                try {
                    String salarioTexto = SALARIOJUGADOR.getText().trim();

                    if (salarioTexto.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "El campo salario no puede estar vacío.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    nuevoSalario = Double.parseDouble(SALARIOJUGADOR.getText());

                    if (nuevoSalario <= 0 || nuevoSalario >= 200000) {
                        JOptionPane.showMessageDialog(null, "El salario debe ser mayor a 0 y menor a 200000.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Salario inválido.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                    return;
                }


                int indexEquipo = Equipos.getSelectedIndex();

                if (indexEquipo < 0) {
                    JOptionPane.showMessageDialog(null, "Selecciona un equipo.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Equipo equipoSeleccionado = listaEquipos.get(indexEquipo);


                Connection conn = GestorBD.conectar();
                String sql = "UPDATE JUGADOR SET NOMBRE = ?, APELLIDO = ?, NICKNAME = ?, SALARIO = ?, ID_EQUIPO = ? WHERE ID_JUGADOR = ?";

                try {
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nuevoNombre);
                    pstmt.setString(2, nuevoApellido);
                    pstmt.setString(3, nuevoNickname);
                    pstmt.setDouble(4, nuevoSalario);
                    pstmt.setInt(5, equipoSeleccionado.getID_Equipo());
                    pstmt.setInt(6, jugador.getID_Jugador());

                    int filasActualizadas = pstmt.executeUpdate();
                    if (filasActualizadas > 0) {
                        JOptionPane.showMessageDialog(null, "Jugador modificado correctamente.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);
                        jugador.setNombre(nuevoNombre);
                        jugador.setApellido(nuevoApellido);
                        jugador.setNickname(nuevoNickname);
                        jugador.setSalario(nuevoSalario);
                        jugador.setEquipo(equipoSeleccionado);

                        // Actualizar modelo de la lista
                        modeloJugadores.set(jugadorSeleccionado, nuevoNombre + " " + nuevoApellido);
                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo modificar el jugador.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                    }
                    conn.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error al modificar jugador: " + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);
                }


            }
        });


    }


}