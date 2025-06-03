import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MODIFICAR_EQUIPO_DUENIOS {
    private JList listaJugadores_Equipo;
    private JList listaJugadores_Libres;
    private JButton eliminarButton;
    private JButton añadirButton;
    JPanel panel;
    private JLabel PresupestoDisponible;
    private List<Equipo> listaEquipos = new ArrayList<>();
    private DefaultListModel<String> jugadores_sinequipoModel = new DefaultListModel<>();
    private DefaultListModel<String> jugadores_conequipoModel = new DefaultListModel<>();
    private List<Jugador> JugadoresConEquipo = new ArrayList<>();
    private List<Jugador> JugadoresSinEquipo = new ArrayList<>();

    /**
     * @author Misha
     * Carga los datos de los equipos en la ventana
     */
    public MODIFICAR_EQUIPO_DUENIOS(int id_equipoD) throws SQLException {
        listaJugadores_Equipo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJugadores_Libres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        String nombre_equipo = "";
        int id_equipoDuenyo = id_equipoD;

        actualizarPresupuestoDisponible(id_equipoDuenyo);
        if (id_equipoDuenyo <= 0) {
            JOptionPane.showMessageDialog(null, "No tiene equipo asignado. No puede acceder.");

        }
        Connection conn = GestorBD.conectar();
        String query = "SELECT NOMBRE_EQUIPO FROM EQUIPO where ID_EQUIPO=?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id_equipoDuenyo);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            nombre_equipo = rs.getString("NOMBRE_EQUIPO");
        }

        if (nombre_equipo == null || nombre_equipo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tiene equipo asignado. No puede acceder.");

        }

        stmt.close();
        rs.close();
        conn.close();


        jugadores_conequipoModel.clear();
        jugadores_sinequipoModel.clear();
        listaJugadores_Equipo.setModel(jugadores_conequipoModel);
        listaJugadores_Libres.setModel(jugadores_sinequipoModel);

        Connection conne = GestorBD.conectar();
        try {
            String queryy = "SELECT * FROM JUGADOR WHERE ID_EQUIPO=?";

            PreparedStatement stmtt = conne.prepareStatement(queryy);
            stmtt.setInt(1, id_equipoDuenyo);
            ResultSet rss = stmtt.executeQuery();


            while (rss.next()) {
                int id = rss.getInt("ID_JUGADOR");
                String nombre = rss.getString("NOMBRE");
                String apellido = rss.getString("APELLIDO");
                String nickname = rss.getString("NICKNAME");
                double salario = rss.getDouble("SALARIO");
                int idEquipo = rss.getInt("ID_EQUIPO");

                Equipo equipoJugador = null;
                for (Equipo equipo : listaEquipos) {
                    if (equipo.getID_Equipo() == idEquipo) {
                        equipoJugador = equipo;
                        break;
                    }
                }

                try {

                    Jugador jugador = new Jugador(salario, nickname, apellido, nombre, id, equipoJugador);
                    JugadoresConEquipo.add(jugador);
                    jugadores_conequipoModel.addElement(nombre + "  " + apellido + " ( " + salario + " € )");


                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "No se ha podido añadir el jugador a la lista de jugadores con equipo.");
                }

            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar jugadores: " + e.getMessage());
        }


        Connection connSIN = GestorBD.conectar();
        try {
            String queryy = "SELECT * FROM JUGADOR WHERE ID_EQUIPO IS NULL";
            PreparedStatement stmtt = connSIN.prepareStatement(queryy);
            ResultSet rss = stmtt.executeQuery();


            while (rss.next()) {
                int id = rss.getInt("ID_JUGADOR");
                String nombre = rss.getString("NOMBRE");
                String apellido = rss.getString("APELLIDO");
                String nickname = rss.getString("NICKNAME");
                double salario = rss.getDouble("SALARIO");


                try {

                    Jugador jugador = new Jugador(salario, nickname, apellido, nombre, id, null);
                    JugadoresSinEquipo.add(jugador);
                    jugadores_sinequipoModel.addElement(nombre + "  " + apellido + " ( " + salario + " € )");


                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "El salario debe ser un número válido.");
                }

            }
            conn.close();
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar jugadores: " + e.getMessage());
        }
        /**
         * @author Misha
         * Añade un jugador sin equipo al equipo del dueño que este editando su equipo
         */
        añadirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int JugadorSeleccionado = listaJugadores_Libres.getSelectedIndex();

                if (JugadorSeleccionado < 0) {
                    JOptionPane.showMessageDialog(null, "Selecciona un jugador para añadir al equipo.");
                    return;
                }

                Jugador jugador = JugadoresSinEquipo.get(JugadorSeleccionado);

                int get_ID = jugador.getID_Jugador();
                double presupuesto = 0;

                try {
                    Connection conn = GestorBD.conectar();
                    String consultaPresupuesto = "SELECT PRESUPUESTO FROM EQUIPO WHERE ID_EQUIPO = ?";
                    PreparedStatement stmtPresupuesto = conn.prepareStatement(consultaPresupuesto);
                    stmtPresupuesto.setInt(1, id_equipoDuenyo);
                    ResultSet rsPresupuesto = stmtPresupuesto.executeQuery();


                    if (rsPresupuesto.next()) {
                        presupuesto = rsPresupuesto.getDouble("PRESUPUESTO");
                    }

                    rsPresupuesto.close();
                    stmtPresupuesto.close();


                    double totalSalarios = 0;


                    String consultaSalarios = "SELECT SUM(SALARIO) AS totalSalario FROM JUGADOR WHERE ID_EQUIPO = ?";
                    PreparedStatement stmtSalarios = conn.prepareStatement(consultaSalarios);
                    stmtSalarios.setInt(1, id_equipoDuenyo);
                    ResultSet rsSalarios = stmtSalarios.executeQuery();


                    if (rsSalarios.next()) {
                        totalSalarios = rsSalarios.getDouble("totalSalario");
                    }

                    rsSalarios.close();
                    stmtSalarios.close();


                    double disponible = presupuesto - totalSalarios;

                    //  JOptionPane.showMessageDialog(null, "salario disponible " + disponible + "presupuesto" + presupuesto + "totalSalario" + totalSalarios + "JUGADORSalario" + jugador.getSalario());

                    if (disponible >= jugador.getSalario()) {

                        String sql = "UPDATE JUGADOR SET ID_EQUIPO = ? WHERE ID_JUGADOR = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(sql);
                        updateStmt.setInt(1, id_equipoDuenyo);
                        updateStmt.setInt(2, jugador.getID_Jugador());
                        updateStmt.executeUpdate();
                        updateStmt.close();


                        Equipo equipo = new Equipo(id_equipoDuenyo);
                        jugador.setEquipo(equipo);
                        JugadoresConEquipo.add(jugador);
                        JugadoresSinEquipo.remove(JugadorSeleccionado);

                        jugadores_sinequipoModel.remove(JugadorSeleccionado);
                        jugadores_conequipoModel.addElement(jugador.getNombre() + " " + jugador.getApellido() + " ( " + jugador.getSalario() + " € )");

                        actualizarPresupuestoDisponible(id_equipoDuenyo);
                        JOptionPane.showMessageDialog(null, "Jugador añadido correctamente.");
                    } else {
                        JOptionPane.showMessageDialog(null, "No hay presupuesto suficiente para añadir a este jugador.");
                    }

                    conn.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error en la base de datos:" + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);

                }


            }
        });
        /**
         * @author Misha
         * Elimina el jugador seleccionado del equipo del dueño
         */
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int JugadorSeleccionado = listaJugadores_Equipo.getSelectedIndex();

                if (JugadorSeleccionado < 0) {
                    JOptionPane.showMessageDialog(null, "Selecciona un jugador para eliminar del equipo.");
                    return;
                }

                Jugador jugador = JugadoresConEquipo.get(JugadorSeleccionado);

                int getID_Jugador = jugador.getID_Jugador();

                Connection conn = GestorBD.conectar();
                try {

                    String sql = "UPDATE JUGADOR SET ID_EQUIPO = NULL WHERE ID_JUGADOR = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(sql);
                    updateStmt.setInt(1, jugador.getID_Jugador());
                    updateStmt.executeUpdate();
                    updateStmt.close();


                    Equipo equipo = new Equipo(null);
                    jugador.setEquipo(equipo);
                    JugadoresConEquipo.remove(jugador);
                    JugadoresSinEquipo.add(jugador);

                    jugadores_conequipoModel.remove(JugadorSeleccionado);
                    jugadores_sinequipoModel.addElement(jugador.getNombre() + " " + jugador.getApellido() + " ( " + jugador.getSalario() + " € )");
                    actualizarPresupuestoDisponible(id_equipoDuenyo);
                    JOptionPane.showMessageDialog(null, "Se ha eliminado el jugador de equipo correctamente ", "AVISO", JOptionPane.INFORMATION_MESSAGE);

                } catch (SQLException ex) {

                    JOptionPane.showMessageDialog(null, "Error en la base de datos:" + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * @param id_equipoDuenyo el id del equipo que pertenece al dueño que inicio sesion en la aplicacion
     * @author Misha
     * Muestra y actualiza en tiempo real el presupuesto de cada equipo
     */
    private void actualizarPresupuestoDisponible(int id_equipoDuenyo) {
        try {
            Connection conn = GestorBD.conectar();

            double presupuesto = 0;
            double totalSalarios = 0;


            String consultaPresupuesto = "SELECT PRESUPUESTO FROM EQUIPO WHERE ID_EQUIPO = ?";
            PreparedStatement stmtPresupuesto = conn.prepareStatement(consultaPresupuesto);
            stmtPresupuesto.setInt(1, id_equipoDuenyo);
            ResultSet rsPresupuesto = stmtPresupuesto.executeQuery();

            if (rsPresupuesto.next()) {
                presupuesto = rsPresupuesto.getDouble("PRESUPUESTO");
            }

            rsPresupuesto.close();
            stmtPresupuesto.close();


            String consultaSalarios = "SELECT SUM(SALARIO) AS totalSalario FROM JUGADOR WHERE ID_EQUIPO = ?";
            PreparedStatement stmtSalarios = conn.prepareStatement(consultaSalarios);
            stmtSalarios.setInt(1, id_equipoDuenyo);
            ResultSet rsSalarios = stmtSalarios.executeQuery();

            if (rsSalarios.next()) {
                totalSalarios = rsSalarios.getDouble("totalSalario");
            }

            rsSalarios.close();
            stmtSalarios.close();

            conn.close();

            double disponible = presupuesto - totalSalarios;

            PresupestoDisponible.setText("Presupuesto disponible: " + disponible + " €");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al calcular presupuesto: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

}

