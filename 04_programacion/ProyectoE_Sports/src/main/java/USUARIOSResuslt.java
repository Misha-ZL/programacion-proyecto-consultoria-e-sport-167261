import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class USUARIOSResuslt {
    public JPanel VerDatosUsu;
    private JComboBox<Jornada> Jornadas;

    private JTable table1;
    private JTable table2;

    private ArrayList<Partido> partidos = new ArrayList<>();
    private ArrayList<Jornada> jornadas = new ArrayList<>();

    List<Equipo> equipos = new ArrayList<>();
    DefaultListModel<Equipo> model = new DefaultListModel<>();

    /**
     * @author Misha
     * Carga y almacena los datos relacionados de una jornada y
     * los partidos y equipos en ella
     */
    public USUARIOSResuslt() {

        try {
            Connection conn = GestorBD.conectar();
            String query = "SELECT * FROM JORNADA";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            Jornadas.removeAllItems();
            jornadas.clear();

            while (rs.next()) {
                int id = rs.getInt("ID_JORNADA");
                int puntos = rs.getInt("PUNTOSTOT");
                Timestamp fecha = rs.getTimestamp("FECHA");

                Jornada jornada = new Jornada(id, puntos, fecha);
                jornadas.add(jornada);
                Jornadas.addItem(jornada);

            }

            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Jornadas.addActionListener(e -> {
            Jornada jornadaSeleccionada = (Jornada) Jornadas.getSelectedItem();

            if (jornadaSeleccionada != null) {
                try {
                    Connection conn = GestorBD.conectar();

                    String query = "SELECT P.ID_PARTIDO, P.FECHA, P.PUNTUACION, P.RESULTADO_EQUIPO1, P.RESULTADO_EQUIPO2, " +
                            "E1.ID_EQUIPO AS ID1, E1.NOMBRE_EQUIPO AS NOMBRE1, E2.ID_EQUIPO AS ID2, E2.NOMBRE_EQUIPO AS NOMBRE2 " +
                            "FROM PARTIDO P JOIN EQUIPO E1 ON P.ID_EQUIPO1 = E1.ID_EQUIPO  JOIN EQUIPO E2 ON P.ID_EQUIPO2 = " +
                            "E2.ID_EQUIPO WHERE P.ID_JORNADA = ? ";

                    PreparedStatement stmt = conn.prepareStatement(query);
                    stmt.setInt(1, jornadaSeleccionada.getId_Jornada());
                    ResultSet rs = stmt.executeQuery();

                    partidos.clear();

                    while (rs.next()) {
                        int idPartido = rs.getInt("ID_PARTIDO");
                        Timestamp fecha = rs.getTimestamp("FECHA");
                        int puntuacion = rs.getInt("PUNTUACION");
                        int resultadoE1 = rs.getInt("RESULTADO_EQUIPO1");
                        int resultadoE2 = rs.getInt("RESULTADO_EQUIPO2");

                        Equipo equipo1 = new Equipo(rs.getString("NOMBRE1"), rs.getInt("ID1"));
                        Equipo equipo2 = new Equipo(rs.getString("NOMBRE2"), rs.getInt("ID2"));

                        Partido partido = new Partido(idPartido, fecha, equipo1, equipo2, puntuacion, resultadoE1, resultadoE2);
                        partidos.add(partido);
                    }

                    table1.setModel(new tablaModeloCalendar(partidos));

                    stmt.close();
                    conn.close();
                } catch (SQLException ex2) {
                    ex2.printStackTrace();
                }
            }
        });
        try {
            Connection conn = GestorBD.conectar();
            String sql = "SELECT * FROM EQUIPO ORDER BY PUNTUACION DESC";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();


            while (rs.next()) {
                int id = rs.getInt("ID_EQUIPO");
                String nombre = rs.getString("NOMBRE_EQUIPO");
                int puntos = rs.getInt("PUNTUACION");

                Equipo equipo = new Equipo(id, nombre, puntos);
                equipos.add(equipo);

            }

            table2.setModel(new ClasifiacionEquipoModel(equipos));

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}