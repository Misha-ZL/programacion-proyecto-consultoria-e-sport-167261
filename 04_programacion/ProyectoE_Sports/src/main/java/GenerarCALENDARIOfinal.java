import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Clase que gestiona la generación y desarrollo de un torneo
 * con fases de cuartos, semifinales y final.
 *
 * Carga los equipos desde la base de datos, permite iniciar el torneo,
 * avanzar fases y registrar resultados en la base de datos.
 */
public class GenerarCALENDARIOfinal {

    // Constantes que representan las diferentes fases del torneo
    private static final int FASE_NO_INICIADA = 0;
    private static final int FASE_CUARTOS = 1;
    private static final int FASE_SEMIFINALES = 2;
    private static final int FASE_FINAL = 3;

    public JPanel panel;
    private JScrollPane scrollInfo;
    private JTable tableInformacion;
    private JTextArea torneoTextArea;
    private JButton siguienteFaseButton;

    // Variable que controla en qué fase está el torneo actualmente
    private int faseActual = FASE_NO_INICIADA;

    // Listas para manejar equipos y ganadores en diferentes rondas
    private ArrayList<Equipo> equipos;
    private ArrayList<Equipo> ganadoresRonda1;
    private ArrayList<Equipo> ganadoresRonda2;

    // Modelo para la tabla que muestra los equipos y sus datos
    private DefaultTableModel tableModel;

    /**
     * Constructor: carga datos desde la base de datos y configura la interfaz.
     * Añade un listener para el botón que avanza las fases del torneo.
     */
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

    /**
     * Carga los datos de los equipos desde la base de datos,
     * ordenados por puntuación descendente, y actualiza el modelo de la tabla.
     */
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
     * Inicia el torneo si no está iniciado.
     * Selecciona 8 equipos aleatoriamente para competir.
     * Cambia la fase actual a cuartos de final.
     */
    public void iniciarTorneo() {
        if (faseActual != FASE_NO_INICIADA) {
            torneoTextArea.append("El torneo ya está en curso.\n");
            return;
        }

        equipos = new ArrayList<>();
        // Carga los equipos desde la tabla a la lista 'equipos'
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String nombre = (String) tableModel.getValueAt(i, 1);
            int puntuacion = (int) tableModel.getValueAt(i, 3);
            equipos.add(new Equipo(nombre, puntuacion));
        }

        // Verifica que haya al menos 8 equipos
        if (equipos.size() < 8) {
            torneoTextArea.append("Se necesitan al menos 8 equipos para el torneo.\n");
            return;
        }

        // Mezcla aleatoriamente los equipos y selecciona los primeros 8
        Collections.shuffle(equipos);
        equipos = new ArrayList<>(equipos.subList(0, 8));

        faseActual = FASE_CUARTOS;
        torneoTextArea.append("=== TORNEO INICIADO ===\nParticipantes:\n");
        equipos.forEach(e -> torneoTextArea.append("- " + e.getNombreEquipo() + "\n"));
    }

    /**
     * Avanza a la siguiente fase del torneo dependiendo
     * de la fase actual, jugando los partidos correspondientes.
     */
    public void avanzarFase() {
        if (faseActual == FASE_NO_INICIADA) {
            torneoTextArea.append("No hay torneo en curso.\n");
            return;
        }

        switch (faseActual) {
            case FASE_CUARTOS:
                torneoTextArea.append("\n--- CUARTOS DE FINAL ---\n");
                ganadoresRonda1 = jugarRonda(equipos);  // Juega cuartos
                faseActual = FASE_SEMIFINALES;
                break;
            case FASE_SEMIFINALES:
                torneoTextArea.append("\n--- SEMIFINALES ---\n");
                ganadoresRonda2 = jugarRonda(ganadoresRonda1);  // Juega semifinales
                faseActual = FASE_FINAL;
                break;
            case FASE_FINAL:
                torneoTextArea.append("\n--- FINAL ---\n");
                Equipo f1 = ganadoresRonda2.get(0);
                Equipo f2 = ganadoresRonda2.get(1);
                Partido partido = new Partido(f1, f2);
                Equipo campeon = jugarPartido(partido);  // Juega la final
                Equipo subcampeon = (campeon == f1) ? f2 : f1;
                torneoTextArea.append(String.format("%s vs %s -> CAMPEÓN: %s, Subcampeón: %s%n",
                        f1.getNombreEquipo(), f2.getNombreEquipo(),
                        campeon.getNombreEquipo(), subcampeon.getNombreEquipo()));
                faseActual = FASE_NO_INICIADA;  // Reinicia torneo
                break;
        }
    }

    /**
     * Juega una ronda (lista de partidos entre equipos por pares).
     *
     * @param participantes lista de equipos participantes en la ronda.
     * @return lista de equipos ganadores de la ronda.
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
     * Simula el resultado de un partido entre dos equipos,
     * genera resultados aleatorios y asegura que haya un ganador.
     * Además, registra el resultado en la base de datos.
     *
     * @param partido partido a jugar.
     * @return equipo ganador del partido.
     */
    private Equipo jugarPartido(Partido partido) {
        Random rand = new Random();
        int resultado1 = rand.nextInt(5);  // Goles equipo 1 (0-4)
        int resultado2 = rand.nextInt(5);  // Goles equipo 2 (0-4)

        // Si empate, se fuerza a que uno gane sumando 1 a uno de los equipos
        if (resultado1 == resultado2) {
            if (rand.nextBoolean()) resultado1++;
            else resultado2++;
        }

        partido.setResultadoE1(resultado1);
        partido.setResultadoE2(resultado2);

        // Obtiene los IDs de los equipos para registrar el partido
        int id1 = obtenerIdEquipoPorNombre(partido.getEquipo1().getNombreEquipo());
        int id2 = obtenerIdEquipoPorNombre(partido.getEquipo2().getNombreEquipo());

        if (id1 == -1 || id2 == -1) {
            System.err.println("No se encontró el ID de uno o ambos equipos.");
            return partido.getEquipo1();  // Devuelve el primer equipo si hay error
        }

        // Registra el resultado en la base de datos
        registrarResultadoPartido(id1, id2, resultado1, resultado2);

        // Devuelve el ganador según los goles
        return (resultado1 > resultado2) ? partido.getEquipo1() : partido.getEquipo2();
    }

    /**
     * Registra el resultado de un partido en la base de datos.
     * Actualiza las puntuaciones de los equipos según el resultado.
     *
     * @param id1 ID del equipo 1.
     * @param id2 ID del equipo 2.
     * @param g1  Goles del equipo 1.
     * @param g2  Goles del equipo 2.
     */
    private void registrarResultadoPartido(int id1, int id2, int g1, int g2) {
        try (Connection conn = GestorBD.conectar()) {
            conn.setAutoCommit(false);  // Control manual de transacciones

            // Obtiene la jornada actual o crea una nueva si no existe
            int jornadaDelDia = obtenerOJornadaDelDia(conn);

            // Inserta el partido con resultados y jornada
            String insertSQL = "INSERT INTO PARTIDO (PUNTUACION, FECHA, RESULTADO_EQUIPO1, RESULTADO_EQUIPO2, ID_JORNADA, ID_EQUIPO1, ID_EQUIPO2) " +
                    "VALUES (?, NOW(), ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                stmt.setInt(1, Math.abs(g1 - g2));  // Diferencia de goles
                stmt.setInt(2, g1);
                stmt.setInt(3, g2);
                stmt.setInt(4, jornadaDelDia);
                stmt.setInt(5, id1);
                stmt.setInt(6, id2);
                stmt.executeUpdate();
            }

            // Actualiza la puntuación de cada equipo (3 puntos al ganador)
            actualizarPuntuacion(id1, g1 > g2 ? 3 : 0, conn);
            actualizarPuntuacion(id2, g2 > g1 ? 3 : 0, conn);

            conn.commit();  // Confirma cambios

            // Actualiza la tabla para reflejar las nuevas puntuaciones
            actualizarTabla();

        } catch (SQLException e) {
            System.err.println("Error registrando partido: " + e.getMessage());
        }
    }

    /**
     * Actualiza la puntuación de un equipo en la base de datos.
     *
     * @param id     ID del equipo.
     * @param puntos puntos a añadir (por ejemplo, 3 por victoria).
     * @param conn   conexión activa a la base de datos.
     * @throws SQLException en caso de error SQL.
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
     * Busca en la base de datos el ID del equipo según su nombre.
     *
     * @param nombre nombre del equipo.
     * @return ID del equipo, o -1 si no se encuentra.
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
     * Recarga los datos de la tabla desde la base de datos
     * y actualiza la vista de la tabla.
     */
    public void actualizarTabla() {
        cargarDatosDesdeBD();
        tableInformacion.setModel(tableModel);
        tableModel.fireTableDataChanged();
    }

    /**
     * Obtiene la jornada del día actual de la base de datos.
     * Si no existe, crea una nueva jornada con la fecha actual.
     *
     * @param conn conexión a la base de datos.
     * @return ID de la jornada actual.
     * @throws SQLException en caso de error.
     */
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
            e.getMessage();  // Ignora error, sigue para crear nueva jornada
        }

        // Inserta nueva jornada con fecha actual
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
