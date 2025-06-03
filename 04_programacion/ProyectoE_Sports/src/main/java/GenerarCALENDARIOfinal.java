import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GenerarCALENDARIOfinal {

    private static final int FASE_NO_INICIADA = 0;
    private static final int FASE_CUARTOS = 1;
    private static final int FASE_SEMIFINALES = 2;
    private static final int FASE_FINAL = 3;
    public JPanel panel;
    private JScrollPane scrollInfo;
    private JTable tableInformacion;
    private JTextArea torneoTextArea;
    private JButton siguienteFaseButton;
    private int faseActual = FASE_NO_INICIADA;
    private ArrayList<Equipo> equipos;
    private ArrayList<Equipo> ganadoresRonda1;
    private ArrayList<Equipo> ganadoresRonda2;
    private DefaultTableModel tableModel;

    public GenerarCALENDARIOfinal() {
        cargarDatosDesdeBD();
        tableInformacion.setModel(tableModel);
        torneoTextArea.setEditable(false);
        siguienteFaseButton.addActionListener(e -> {
            if (faseActual == FASE_NO_INICIADA) {
                iniciarTorneo();
                torneoTextArea.append("Torneo iniciado con 8 equipos seleccionados aleatoriamente.\n");
            } else {
                avanzarFase();
            }
        });
    }

    private void cargarDatosDesdeBD() {
        String sql = "SELECT ID_EQUIPO, NOMBRE_EQUIPO, PRESUPUESTO, PUNTUACION FROM EQUIPO ORDER BY PUNTUACION DESC";
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nombre", "Presupuesto", "Puntuación"}, 0);

        try (Connection conn = GestorBD.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("ID_EQUIPO"),
                        rs.getString("NOMBRE_EQUIPO"),
                        rs.getDouble("PRESUPUESTO"),
                        rs.getInt("PUNTUACION")
                });
            }

        } catch (SQLException e) {
            torneoTextArea.append("Error cargando datos desde BD: " + e.getMessage() + "\n");
        }
    }

    /**
     * carga la informacion para iniciar el torneo
     */

    public void iniciarTorneo() {
        if (faseActual != FASE_NO_INICIADA) {
            torneoTextArea.append("El torneo ya está en curso.\n");
            return;
        }

        equipos = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nombre = (String) tableModel.getValueAt(i, 1);
            int puntuacion = (int) tableModel.getValueAt(i, 3);
            equipos.add(new Equipo(nombre, puntuacion));
        }

        if (equipos.size() < 8) {
            torneoTextArea.append("Se necesitan al menos 8 equipos para el torneo.\n");
            return;
        }

        Collections.shuffle(equipos);
        equipos = new ArrayList<>(equipos.subList(0, 8));

        faseActual = FASE_CUARTOS;
        torneoTextArea.append("=== TORNEO INICIADO ===\nParticipantes:\n");
        equipos.forEach(e -> torneoTextArea.append("- " + e.getNombreEquipo() + "\n"));
    }

    /**
     * se avanza a la siguiente ronda con los resusltados obtenidos
     */

    public void avanzarFase() {
        if (faseActual == FASE_NO_INICIADA) {
            torneoTextArea.append("No hay torneo en curso.\n");
            return;
        }

        switch (faseActual) {
            case FASE_CUARTOS:
                torneoTextArea.append("\n--- CUARTOS DE FINAL ---\n");
                ganadoresRonda1 = jugarRonda(equipos);
                faseActual = FASE_SEMIFINALES;
                break;
            case FASE_SEMIFINALES:
                torneoTextArea.append("\n--- SEMIFINALES ---\n");
                ganadoresRonda2 = jugarRonda(ganadoresRonda1);
                faseActual = FASE_FINAL;
                break;
            case FASE_FINAL:
                torneoTextArea.append("\n--- FINAL ---\n");
                Equipo f1 = ganadoresRonda2.get(0);
                Equipo f2 = ganadoresRonda2.get(1);
                Partido partido = new Partido(f1, f2);
                Equipo campeon = jugarPartido(partido);
                Equipo subcampeon = (campeon == f1) ? f2 : f1;
                torneoTextArea.append(String.format("%s vs %s -> CAMPEÓN: %s, Subcampeón: %s%n",
                        f1.getNombreEquipo(), f2.getNombreEquipo(),
                        campeon.getNombreEquipo(), subcampeon.getNombreEquipo()));
                faseActual = FASE_NO_INICIADA;
                break;
        }
    }

    /**
     * Author Alberto, Misha
     *
     * @param participantes es la lista de los equipos que se enfrentaran entre si
     * @return devuelve la lista de los ganadores
     */
    private ArrayList<Equipo> jugarRonda(ArrayList<Equipo> participantes) {
        ArrayList<Equipo> ganadores = new ArrayList<>();
        for (int i = 0; i < participantes.size(); i += 2) {
            Equipo e1 = participantes.get(i);
            Equipo e2 = participantes.get(i + 1);
            Partido partido = new Partido(e1, e2);
            Equipo ganador = jugarPartido(partido);
            ganadores.add(ganador);
            torneoTextArea.append(String.format("%s vs %s -> Ganador: %s%n", e1.getNombreEquipo(), e2.getNombreEquipo(), ganador.getNombreEquipo()));
        }
        return ganadores;
    }

    /**
     * Author Alberto, Misha
     *
     * @param partido contiene la infromacion referente al objeto partido
     * @return devolverá resultado que han obtenido los equipos
     */

    private Equipo jugarPartido(Partido partido) {
        Random rand = new Random();
        int resultado1 = rand.nextInt(5);
        int resultado2 = rand.nextInt(5);

        if (resultado1 == resultado2) {
            if (rand.nextBoolean()) resultado1++;
            else resultado2++;
        }

        partido.setResultadoE1(resultado1);
        partido.setResultadoE2(resultado2);

        int id1 = obtenerIdEquipoPorNombre(partido.getEquipo1().getNombreEquipo());
        int id2 = obtenerIdEquipoPorNombre(partido.getEquipo2().getNombreEquipo());

        if (id1 == -1 || id2 == -1) {
            System.err.println("No se encontró el ID de uno o ambos equipos.");
            return partido.getEquipo1();
        }

        registrarResultadoPartido(id1, id2, resultado1, resultado2);
        return (resultado1 > resultado2) ? partido.getEquipo1() : partido.getEquipo2();
    }

    /**
     * Author Alberto, Misha
     *
     * @param id1 identificador de equipo 1
     * @param id2 identificador de equipo 2
     * @param g1  resultado de equipo 1
     * @param g2  resultado de equipo 2
     */

    private void registrarResultadoPartido(int id1, int id2, int g1, int g2) {
        try (Connection conn = GestorBD.conectar()) {
            conn.setAutoCommit(false);

            int jornadaDelDia = obtenerOJornadaDelDia(conn);

            String insertSQL = "INSERT INTO PARTIDO (PUNTUACION, FECHA, RESULTADO_EQUIPO1, RESULTADO_EQUIPO2, ID_JORNADA, ID_EQUIPO1, ID_EQUIPO2) " +
                    "VALUES (?, NOW(), ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                stmt.setInt(1, Math.abs(g1 - g2));
                stmt.setInt(2, g1);
                stmt.setInt(3, g2);
                stmt.setInt(4, jornadaDelDia);
                stmt.setInt(5, id1);
                stmt.setInt(6, id2);
                stmt.executeUpdate();
            }

            actualizarPuntuacion(id1, g1 > g2 ? 3 : 0, conn);
            actualizarPuntuacion(id2, g2 > g1 ? 3 : 0, conn);

            conn.commit();

            actualizarTabla();

        } catch (SQLException e) {
            System.err.println("Error registrando partido: " + e.getMessage());
        }
    }

    /**
     * Author Alberto, Misha
     *
     * @param id     identificador del equipo
     * @param puntos los puntos que tiene el equipo segun sus victorias y derrotas
     * @param conn   conexion a la base de datos
     * @throws SQLException
     */

    private void actualizarPuntuacion(int id, int puntos, Connection conn) throws SQLException {
        String sql = "UPDATE EQUIPO SET PUNTUACION = PUNTUACION + ? WHERE ID_EQUIPO = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, puntos);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Author Alberto, Misha
     *
     * @param nombre contiene la informacion del nombre del equipo
     * @return Numero si no se encuentra el id del equipo correcto
     */

    private int obtenerIdEquipoPorNombre(String nombre) {
        String sql = "SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE_EQUIPO = ?";
        try (Connection conn = GestorBD.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID_EQUIPO");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar equipo: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Actualiza la informacion de la tabla
     */


    public void actualizarTabla() {
        cargarDatosDesdeBD();
        tableInformacion.setModel(tableModel);
        tableModel.fireTableDataChanged();
    }

    private int obtenerOJornadaDelDia(Connection conn) throws SQLException {
        String selectSQL = "SELECT ID_JORNADA FROM JORNADA WHERE TRUNC(FECHA) = CURDATE()";


        PreparedStatement stmt = conn.prepareStatement(selectSQL);
        try {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID_JORNADA");
                }
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        String insertSQL = "INSERT INTO JORNADA (FECHA) VALUES (NOW())";
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.executeUpdate();
            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException("No se pudo obtener o crear la jornada del día.");
    }
}
