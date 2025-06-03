import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MODIFICAR_EQUIPO {
    private JTextField NombreE;
    private JTextField Presupuesto;
    private JTextField Puntuacion;
    private JButton Modificar;
    private JList Equipos;
    JPanel MODEQUIPOS;
    private DefaultListModel<String> modeloEquipos = new DefaultListModel<>();
    private List<Equipo> listaEquipos = new ArrayList<>();

    /**
     * @author Misha
     * Carga los datos de los equipos en la ventana
     */
    public MODIFICAR_EQUIPO() {
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

        Equipos.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {

                int EquipoSeleccionado = Equipos.getSelectedIndex();

                if (EquipoSeleccionado >= 0) {
                    Equipo seleccionado = listaEquipos.get(EquipoSeleccionado);
                    NombreE.setText(seleccionado.getNombreEquipo());
                    Presupuesto.setText(String.valueOf(seleccionado.getPresupuesto()));
                    Puntuacion.setText(String.valueOf(seleccionado.getPuntuacion()));


                }

            }
        });

        /**
         * @author Misha
         * Permite modificar los parametros de los jugadores
         * @param e evento a ser ejecutado
         */
        Modificar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int EquipoSeleccionado = Equipos.getSelectedIndex();
                if (EquipoSeleccionado < 0) {
                    JOptionPane.showMessageDialog(null, "Selecciona un jugador para modificar.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                }

                Equipo equipo = listaEquipos.get(EquipoSeleccionado);

                String nuevoNombre = NombreE.getText();

                Double nuevoPresupuesto;
                int nuevaPuntuacion;
                try {

                    nuevoPresupuesto = Double.valueOf(Presupuesto.getText());
                    nuevaPuntuacion = Integer.parseInt(Puntuacion.getText());

                    if (nuevoPresupuesto > 200000) {
                        JOptionPane.showMessageDialog(null, "El presupuesto no puede superar los 200000€.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "ERROR. Preupuesto o puntuacion inválido.", "ERROR.!", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Connection conn = GestorBD.conectar();
                String sql = "UPDATE EQUIPO SET NOMBRE_EQUIPO = ?, PRESUPUESTO = ?, PUNTUACION = ? WHERE ID_EQUIPO = ?";


                try {
                    if (nuevoPresupuesto <= 0) {
                        JOptionPane.showMessageDialog(null, "El presupuesto debe ser mayor a 0.", "ATENCIÓN.!", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (nuevoPresupuesto > 200000) {
                        JOptionPane.showMessageDialog(null, "ERROR. El presupuesto no puede ser mayor a 200,000.", "ERROR.!", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, nuevoNombre);
                    pstmt.setDouble(2, nuevoPresupuesto);
                    pstmt.setInt(3, nuevaPuntuacion);
                    pstmt.setDouble(4, equipo.getID_Equipo());
                    int filasActualizadas = pstmt.executeUpdate();
                    if (filasActualizadas > 0) {
                        JOptionPane.showMessageDialog(null, "Equipo modificado correctamente.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);
                        equipo.setNombreEquipo(nuevoNombre);
                        equipo.setPresupuesto(Double.valueOf(nuevaPuntuacion));
                        equipo.setPresupuesto(nuevoPresupuesto);
                        modeloEquipos.set(EquipoSeleccionado, nuevoNombre);

                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo modificar el Equipo.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);
                    }

                    conn.close();
                    pstmt.close();

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }


}
