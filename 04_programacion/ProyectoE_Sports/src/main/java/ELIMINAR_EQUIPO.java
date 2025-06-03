import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ELIMINAR_EQUIPO {
    private JList Equipos;
    JPanel EliminarEquipo;
    private JButton ELIMINAR;
    private DefaultListModel<String> modeloEquipos = new DefaultListModel<>();
    private List<Equipo> listaEquipos = new ArrayList<>();

    /**
     * @author Misha
     * Carga los equipos desde la Base de Datos
     */
    public ELIMINAR_EQUIPO() {

        Equipos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        Equipos.setModel(modeloEquipos);

        Connection conn = GestorBD.conectar();
        try {
            String sql = "SELECT * FROM EQUIPO";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("ID_EQUIPO");
                String nombre = rs.getString("NOMBRE_EQUIPO");
                double presupuesto = rs.getDouble("PRESUPUESTO");
                int puntuacion = rs.getInt("PUNTUACION");

                Equipo equipo = new Equipo(id, puntuacion, presupuesto, nombre, null, null);
                listaEquipos.add(equipo);
                modeloEquipos.addElement(nombre);
            }

            conn.close();
            stmt.close();
            rs.close();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar equipos", "ERROR.!", JOptionPane.ERROR_MESSAGE);
        }

        /**
         * @author Misha
         * Elimina el equipo seleccionado.
         * Elimina tambien la relacion con los jugadores y partidos asociados a ese equipo
         */
        ELIMINAR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int seleccionado = Equipos.getSelectedIndex();

                if (seleccionado == -1) {
                    JOptionPane.showMessageDialog(null, "Selecciona un Equipo para eliminar.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);

                    return;
                }
                Equipo idEquipo = listaEquipos.get(seleccionado);
                try {
                    Connection conn = GestorBD.conectar();
                    //ELiminar los jugadores asociados al equipo
                    PreparedStatement stmtNull = conn.prepareStatement("UPDATE USUARIOS SET ID_EQUIPO = NULL WHERE ID_EQUIPO = ?");
                    stmtNull.setInt(1, idEquipo.getID_Equipo());
                    stmtNull.executeUpdate();


                    //Hay que eliminar los partidos en los que esta relacionado el equipo
                    PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM PARTIDO WHERE ID_EQUIPO1 = ? OR ID_EQUIPO2 = ?");
                    stmt1.setInt(1, idEquipo.getID_Equipo());
                    stmt1.setInt(2, idEquipo.getID_Equipo());
                    stmt1.executeUpdate();

                    //Eliminar el equipo
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM EQUIPO WHERE ID_EQUIPO = ?");
                    stmt.setInt(1, idEquipo.getID_Equipo());

                    int rows = stmt.executeUpdate();

                    if (rows > 0) {
                        JOptionPane.showMessageDialog(null, "Equipo eliminado correctamente.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);
                        modeloEquipos.removeElementAt(seleccionado);
                        listaEquipos.remove(seleccionado);
                    } else {
                        JOptionPane.showMessageDialog(null, "No se encontró el Equipo.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                    }

                    stmt.close();
                    stmt1.close();
                    conn.close();


                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
    }
}
