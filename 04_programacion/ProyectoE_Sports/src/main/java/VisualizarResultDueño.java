import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VisualizarResultDueño {
    public JPanel VisualizarResult;
    private JTable table1;

    private JScrollPane VisualizarTabla;
    private JTable table2;
    private JList list1;
    List<Partido> partidos = new ArrayList<>();
    List<Equipo> equipos = new ArrayList<>();
    DefaultListModel<Equipo> model = new DefaultListModel<>();

    /**
     * @param idEquipo del equipo asociado al dueño que accedio a la aplicacion y el cual sera visualizado
     * @author Misha
     * Permite ver los resultados de un determinado equipo
     */
    public VisualizarResultDueño(int idEquipo) {

        if (idEquipo <= 0) {
            JOptionPane.showMessageDialog(null, "El usuario no tiene equipo asignado.");
            return;
        }
        try {
            Connection conn = GestorBD.conectar();
            String sql = "SELECT P.ID_PARTIDO, P.FECHA, P.RESULTADO_EQUIPO1, P.RESULTADO_EQUIPO2, E1.ID_EQUIPO AS ID1, E1.NOMBRE_EQUIPO AS NOMBRE1, E2.ID_EQUIPO AS ID2, E2.NOMBRE_EQUIPO AS NOMBRE2 FROM PARTIDO P JOIN EQUIPO E1 ON P.ID_EQUIPO1 = E1.ID_EQUIPO JOIN EQUIPO E2 ON P.ID_EQUIPO2 = E2.ID_EQUIPO WHERE E1.ID_EQUIPO = ? OR E2.ID_EQUIPO = ? ORDER BY P.FECHA DESC LIMIT 1";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, idEquipo);
            stmt.setInt(2, idEquipo);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {

                Equipo equipo1 = new Equipo(rs.getString("NOMBRE1"), rs.getInt("ID1"));
                Equipo equipo2 = new Equipo(rs.getString("NOMBRE2"), rs.getInt("ID2"));


                Partido partido = new Partido(
                        rs.getTimestamp("FECHA"), equipo1, equipo2,
                        rs.getInt("RESULTADO_EQUIPO1"),
                        rs.getInt("RESULTADO_EQUIPO2")

                );
                partidos.add(partido);

                table1.setModel(new tablaModeloCalendar(partidos));


            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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
