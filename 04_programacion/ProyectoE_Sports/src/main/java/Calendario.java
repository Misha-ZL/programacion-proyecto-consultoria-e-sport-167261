import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calendario {

    List<Jornada> jornadas = new ArrayList<>();

    /**
     * @author Saagr
     * Carga la informacion de un equipo
     * @return equipos el equipo con la informacion ya cargada
     */
    public ArrayList<Equipo> cargarEquiposJornada() {
        ArrayList<Equipo> equipos = new ArrayList<>();
        String sql = "SELECT ID_EQUIPO, NOMBRE_EQUIPO, PUNTUACION FROM EQUIPO";

        try (Connection conexion = GestorBD.conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rset = stmt.executeQuery(sql)) {

            while (rset.next()) {
                int id = rset.getInt("ID_EQUIPO");
                String nombre = rset.getString("NOMBRE_EQUIPO");
                int puntuacion = rset.getInt("PUNTUACION");

                Equipo equipo = new Equipo(id, nombre, puntuacion);
                equipos.add(equipo);
            }

        } catch (SQLException e) {
            System.out.println("ERROR al cargar los equipos de la jornada: " + e.getMessage());
        }
        return equipos;
    }


    /**
     * @author Saagr
     * Carga la informacion relacionada a una jornada
     * @return jornadas que es la jornada con los datos ya cargados
     */
    public ArrayList<Jornada> cargarJornadas() {
        ArrayList<Jornada> jornadas = new ArrayList<>();
        Map<Integer, List<Partido>> mapaJornadas = new HashMap<>();

        String sql = "SELECT J.ID_JORNADA, J.FECHA, P.ID_PARTIDO,\n" +
                "       P.ID_EQUIPO1, E1.NOMBRE_EQUIPO AS NOMBRE1,\n" +
                "       P.ID_EQUIPO2, E2.NOMBRE_EQUIPO AS NOMBRE2,\n" +
                "       P.RESULTADO_EQUIPO1, P.RESULTADO_EQUIPO2\n" +
                "FROM JORNADA J\n" +
                "INNER JOIN PARTIDO P ON J.ID_JORNADA = P.ID_JORNADA\n" +
                "INNER JOIN EQUIPO E1 ON P.ID_EQUIPO1 = E1.ID_EQUIPO\n" +
                "INNER JOIN EQUIPO E2 ON P.ID_EQUIPO2 = E2.ID_EQUIPO";

        try (Connection conexion = GestorBD.conectar();
             Statement stmt = conexion.createStatement();
             ResultSet rset = stmt.executeQuery(sql)) {

            while (rset.next()) {
                int idJornada = rset.getInt("ID_JORNADA");
                int idPartido = rset.getInt("ID_PARTIDO");
                Timestamp fecha = rset.getTimestamp("FECHA");
                int resultado1 = rset.getInt("RESULTADO_EQUIPO1");
                int resultado2 = rset.getInt("RESULTADO_EQUIPO2");

                int idEquipo1 = rset.getInt("ID_EQUIPO1");
                int idEquipo2 = rset.getInt("ID_EQUIPO2");

                String nombre1 = rset.getString("NOMBRE1");
                String nombre2 = rset.getString("NOMBRE2");

                Equipo eq1 = new Equipo(idEquipo1, nombre1, 0);
                Equipo eq2 = new Equipo(idEquipo2, nombre2, 0);

                Partido partido = new Partido(idPartido, fecha, eq1, eq2, 0, resultado1, resultado2);

                System.out.println("Fila leída: Jornada ID " + idJornada + ", Partido ID " + idPartido +
                        ", Fecha " + fecha + ", Equipos: " + nombre1 + " vs " + nombre2 +
                        ", Resultados: " + resultado1 + "-" + resultado2);

                mapaJornadas.putIfAbsent(idJornada, new ArrayList<>());
                mapaJornadas.get(idJornada).add(partido);
            }

            for (Map.Entry<Integer, List<Partido>> entry : mapaJornadas.entrySet()) {
                List<Partido> listaPartidos = entry.getValue();
                Timestamp fecha = listaPartidos.get(0).getFecha();
                Jornada jornada = new Jornada(entry.getKey(), fecha, listaPartidos);
                jornadas.add(jornada);
                System.out.println("Jornada " + entry.getKey() + " con " + listaPartidos.size() + " partidos, fecha: " + fecha);
            }

        } catch (SQLException e) {
            System.out.println("ERROR al cargar jornadas: " + e.getMessage());
        }

        return jornadas;
    }

    /**
     * @author Saagr
     * Guarda una jornada creada en la app en la base de datos
     * @param jornadas la jornada a guardar
     * @return false si no se pudo almacenar la jornada por cualquier tipo de fallo
     */
    public boolean almacenarJornada(List<Jornada> jornadas) {
        String sql = "INSERT INTO JORNADA (FECHA, PUNTOSTOT) VALUES (?, ?)";

        try (Connection conexion = GestorBD.conectar();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            for (Jornada jornada : jornadas) {
                stmt.setTimestamp(1, jornada.getFecha());
                stmt.setInt(2, jornada.getPuntosTotal());
                stmt.executeUpdate();
            }

            return true;

        } catch (SQLException e) {
            System.out.println("ERROR al almacenar la jornada: " + e.getMessage());
            return false;
        }
    }

    /**
     * @author Saagr
     * Almacena un resultado de partido en la base de datos
     * @param partido el partido al que pertenece dicho resultado
     * @return false si no se pudo almacenar la jornada por cualquier tipo de fallo
     */
    public boolean almacenaResultado(Partido partido) {
        String sql = "UPDATE PARTIDO SET RESULTADO_EQUIPO1 = ?, RESULTADO_EQUIPO2 = ? WHERE ID_PARTIDO = ?";

        try (Connection conexion = GestorBD.conectar();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setInt(1, partido.getResultadoE1());
            stmt.setInt(2, partido.getResultadoE2());
            stmt.setInt(3, partido.getIdPartido());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("ERROR al actualizar puntuación: " + e.getMessage());
            return false;
        }
    }

    /**
     * @author Saagr
     * Almacena en la base de datos los puntos de unequipo obtenidos por un partido
     * @param equipos los equipos a los que pertenecen los puntos
     * @return false si no se pudo almacenar la jornada por cualquier tipo de fallo
     */
    public boolean almacenarPuntuacion(List<Equipo> equipos) {
        int totalFilasAfectadas = 0;
        String sql = "UPDATE EQUIPO SET PUNTUACION = ? WHERE NOMBRE_EQUIPO = ?";

        try (Connection conexion = GestorBD.conectar();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            for (Equipo equipo : equipos) {
                stmt.setInt(1, equipo.getPuntuacion());
                stmt.setString(2, equipo.getNombreEquipo());
                totalFilasAfectadas += stmt.executeUpdate();
            }

            return totalFilasAfectadas == equipos.size();

        } catch (SQLException e) {
            System.out.println("ERROR al actualizar puntuación: " + e.getMessage());
            return false;
        }
    }

    /**
     * @author Saagr
     * Introduce un resultado en un partido seleccionado, y lo guarda en la base de datos
     * @param equipo1 el primer equipo o local
     * @param equipo2 el segundo equipo o visitante
     * @param resultado1 resultado del local
     * @param resultado2 resultado del visitante
     * @return false si no se pudo almacenar la jornada por cualquier tipo de fallo
     */
    public boolean introducirResultado(List<Equipo> equipo1, List<Equipo> equipo2, int resultado1, int resultado2) {
        boolean puntEquipo1 = almacenarPuntuacion(equipo1);
        boolean puntEquipo2 = almacenarPuntuacion(equipo2);

        if (!puntEquipo1 || !puntEquipo2) {
            System.out.println("No se puede actualizar la lista");
            return false;
        }

        String sql = "UPDATE PARTIDO SET RESULTADO_EQUIPO1 = ?, RESULTADO_EQUIPO2 = ? " +
                "WHERE ID_EQUIPO1 = (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE_EQUIPO = ?) " +
                "AND ID_EQUIPO2 = (SELECT ID_EQUIPO FROM EQUIPO WHERE NOMBRE_EQUIPO = ?)";

        try (Connection conexion = GestorBD.conectar();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setInt(1, resultado1);
            stmt.setInt(2, resultado2);
            stmt.setString(3, equipo1.get(0).getNombreEquipo());
            stmt.setString(4, equipo2.get(0).getNombreEquipo());

            int filasAfectadas = stmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el resultado: " + e.getMessage());
            return false;
        }
    }
}
