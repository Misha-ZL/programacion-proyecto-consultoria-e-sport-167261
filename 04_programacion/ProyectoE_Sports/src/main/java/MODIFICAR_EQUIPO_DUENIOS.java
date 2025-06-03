import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase para modificar el equipo de un dueño, permitiendo añadir y eliminar jugadores.
 * Gestiona la visualización y actualización del presupuesto disponible y las listas de jugadores con y sin equipo.
 *
 * @author Misha
 */
public class MODIFICAR_EQUIPO_DUENIOS {
    private JList listaJugadores_Equipo;  // Lista visual de jugadores asignados al equipo
    private JList listaJugadores_Libres;  // Lista visual de jugadores sin equipo
    private JButton eliminarButton;       // Botón para eliminar jugador del equipo
    private JButton añadirButton;         // Botón para añadir jugador al equipo
    JPanel panel;                         // Panel principal de la interfaz
    private JLabel PresupestoDisponible;  // Label que muestra presupuesto disponible

    // Listas para manejo interno de datos
    private List<Equipo> listaEquipos = new ArrayList<>();
    private DefaultListModel<String> jugadores_sinequipoModel = new DefaultListModel<>();
    private DefaultListModel<String> jugadores_conequipoModel = new DefaultListModel<>();
    private List<Jugador> JugadoresConEquipo = new ArrayList<>();
    private List<Jugador> JugadoresSinEquipo = new ArrayList<>();


    /**
     * Constructor que carga y muestra la información del equipo y sus jugadores,
     * y configura la funcionalidad de los botones añadir y eliminar.
     *
     * @param id_equipoD Identificador del equipo asignado al dueño que inicia sesión.
     * @throws SQLException En caso de errores en la conexión o consulta a la base de datos.
     */
    public MODIFICAR_EQUIPO_DUENIOS(int id_equipoD) throws SQLException {
        // Configura selección simple en las listas para evitar seleccionar múltiples jugadores
        listaJugadores_Equipo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaJugadores_Libres.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        String nombre_equipo = "";
        int id_equipoDuenyo = id_equipoD;

        // Actualiza y muestra el presupuesto disponible para el equipo
        actualizarPresupuestoDisponible(id_equipoDuenyo);

        // Validación: Si no hay equipo asignado, muestra mensaje y evita acceso
        if (id_equipoDuenyo <= 0) {
            JOptionPane.showMessageDialog(null, "No tiene equipo asignado. No puede acceder.");
        }

        // Conecta a la BD para obtener el nombre del equipo con el ID dado
        Connection conn = GestorBD.conectar();
        String query = "SELECT NOMBRE_EQUIPO FROM EQUIPO where ID_EQUIPO=?";

        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, id_equipoDuenyo);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            nombre_equipo = rs.getString("NOMBRE_EQUIPO");
        }

        // Si no se encontró nombre, indica que no tiene equipo asignado
        if (nombre_equipo == null || nombre_equipo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tiene equipo asignado. No puede acceder.");
        }

        // Cierra recursos de BD
        stmt.close();
        rs.close();
        conn.close();

        // Limpia los modelos de las listas y las asigna para visualización
        jugadores_conequipoModel.clear();
        jugadores_sinequipoModel.clear();
        listaJugadores_Equipo.setModel(jugadores_conequipoModel);
        listaJugadores_Libres.setModel(jugadores_sinequipoModel);

        // Carga los jugadores que ya pertenecen al equipo desde la BD
        Connection conne = GestorBD.conectar();
        try {
            String queryy = "SELECT * FROM JUGADOR WHERE ID_EQUIPO=?";

            PreparedStatement stmtt = conne.prepareStatement(queryy);
            stmtt.setInt(1, id_equipoDuenyo);
            ResultSet rss = stmtt.executeQuery();

            while (rss.next()) {
                // Extrae datos de cada jugador
                int id = rss.getInt("ID_JUGADOR");
                String nombre = rss.getString("NOMBRE");
                String apellido = rss.getString("APELLIDO");
                String nickname = rss.getString("NICKNAME");
                double salario = rss.getDouble("SALARIO");
                int idEquipo = rss.getInt("ID_EQUIPO");

                // Busca el equipo correspondiente al jugador en la lista de equipos
                Equipo equipoJugador = null;
                for (Equipo equipo : listaEquipos) {
                    if (equipo.getID_Equipo() == idEquipo) {
                        equipoJugador = equipo;
                        break;
                    }
                }

                try {
                    // Crea objeto jugador y lo añade a la lista de jugadores con equipo
                    Jugador jugador = new Jugador(salario, nickname, apellido, nombre, id, equipoJugador);
                    JugadoresConEquipo.add(jugador);
                    // Añade el nombre y salario al modelo para que se muestre en la lista visual
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

        // Carga los jugadores que NO tienen equipo asignado (libres)
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
                    // Crea jugador sin equipo y lo añade a la lista y modelo visual
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
         * Acción para el botón "añadir" que asigna un jugador libre al equipo del dueño.
         */
        añadirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtiene índice del jugador seleccionado en la lista de libres
                int JugadorSeleccionado = listaJugadores_Libres.getSelectedIndex();

                if (JugadorSeleccionado < 0) {
                    JOptionPane.showMessageDialog(null, "Selecciona un jugador para añadir al equipo.");
                    return;
                }

                // Obtiene el objeto jugador seleccionado
                Jugador jugador = JugadoresSinEquipo.get(JugadorSeleccionado);

                double presupuesto = 0;

                try {
                    Connection conn = GestorBD.conectar();

                    // Obtiene el presupuesto total del equipo
                    String consultaPresupuesto = "SELECT PRESUPUESTO FROM EQUIPO WHERE ID_EQUIPO = ?";
                    PreparedStatement stmtPresupuesto = conn.prepareStatement(consultaPresupuesto);
                    stmtPresupuesto.setInt(1, id_equipoDuenyo);
                    ResultSet rsPresupuesto = stmtPresupuesto.executeQuery();

                    if (rsPresupuesto.next()) {
                        presupuesto = rsPresupuesto.getDouble("PRESUPUESTO");
                    }
                    rsPresupuesto.close();
                    stmtPresupuesto.close();

                    // Calcula la suma de salarios actuales en el equipo
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

                    // Calcula presupuesto disponible restando salarios al presupuesto total
                    double disponible = presupuesto - totalSalarios;

                    // Verifica si hay suficiente presupuesto para añadir al jugador
                    if (disponible >= jugador.getSalario()) {
                        // Actualiza BD para asignar jugador al equipo
                        String sql = "UPDATE JUGADOR SET ID_EQUIPO = ? WHERE ID_JUGADOR = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(sql);
                        updateStmt.setInt(1, id_equipoDuenyo);
                        updateStmt.setInt(2, jugador.getID_Jugador());
                        updateStmt.executeUpdate();
                        updateStmt.close();

                        // Actualiza lista y modelo en memoria y en la interfaz
                        Equipo equipo = new Equipo(id_equipoDuenyo);
                        jugador.setEquipo(equipo);
                        JugadoresConEquipo.add(jugador);
                        JugadoresSinEquipo.remove(JugadorSeleccionado);

                        jugadores_sinequipoModel.remove(JugadorSeleccionado);
                        jugadores_conequipoModel.addElement(jugador.getNombre() + " " + jugador.getApellido() + " ( " + jugador.getSalario() + " € )");

                        actualizarPresupuestoDisponible(id_equipoDuenyo);
                        JOptionPane.showMessageDialog(null, "Jugador añadido correctamente.");
                    } else {
                        // Mensaje si no hay suficiente presupuesto
                        JOptionPane.showMessageDialog(null, "No hay presupuesto suficiente para añadir a este jugador.");
                    }

                    conn.close();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error en la base de datos:" + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        /**
         * Acción para el botón "eliminar" que quita un jugador del equipo.
         */
        eliminarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Obtiene índice del jugador seleccionado en la lista de equipo
                int JugadorSeleccionado = listaJugadores_Equipo.getSelectedIndex();

                if (JugadorSeleccionado < 0) {
                    JOptionPane.showMessageDialog(null, "Selecciona un jugador para eliminar del equipo.");
                    return;
                }

                // Obtiene el objeto jugador seleccionado
                Jugador jugador = JugadoresConEquipo.get(JugadorSeleccionado);

                Connection conn = GestorBD.conectar();
                try {
                    // Actualiza BD para quitar jugador del equipo (ID_EQUIPO = NULL)
                    String sql = "UPDATE JUGADOR SET ID_EQUIPO = NULL WHERE ID_JUGADOR = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(sql);
                    updateStmt.setInt(1, jugador.getID_Jugador());
                    updateStmt.executeUpdate();
                    updateStmt.close();

                    // Actualiza listas y modelos internos e interfaz
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
     * Actualiza y muestra el presupuesto disponible para el equipo en tiempo real,
     * restando la suma de salarios al presupuesto total.
     *
     * @param id_equipoDuenyo El ID del equipo cuyo presupuesto se va a actualizar.
     */
    private void actualizarPresupuestoDisponible(int id_equipoDuenyo) {
        try {
            Connection conn = GestorBD.conectar();

            double presupuesto = 0;
            double totalSalarios = 0;

            // Consulta presupuesto total del equipo
            String consultaPresupuesto = "SELECT PRESUPUESTO FROM EQUIPO WHERE ID_EQUIPO = ?";
            PreparedStatement stmtPresupuesto = conn.prepareStatement(consultaPresupuesto);
            stmtPresupuesto.setInt(1, id_equipoDuenyo);
            ResultSet rsPresupuesto = stmtPresupuesto.executeQuery();

            if (rsPresupuesto.next()) {
                presupuesto = rsPresupuesto.getDouble("PRESUPUESTO");
            }
            rsPresupuesto.close();
            stmtPresupuesto.close();

            // Consulta suma total de salarios de jugadores en el equipo
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

            // Calcula presupuesto disponible y actualiza el label
            double disponible = presupuesto - totalSalarios;
            PresupestoDisponible.setText("Presupuesto disponible: " + disponible + " €");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al calcular presupuesto: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
