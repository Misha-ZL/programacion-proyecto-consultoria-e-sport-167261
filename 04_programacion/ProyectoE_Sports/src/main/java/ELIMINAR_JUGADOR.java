import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ELIMINAR_JUGADOR {

    private JList<String> JUGADORES;
    public JPanel ELIMINARJUGADOR;
    private JButton Eliminar;
    private DefaultListModel<String> modeloJugadores = new DefaultListModel<>();
    private List<Integer> listaid = new ArrayList<>();

    /**
     * @author Alberto
     * Crea la ventana y carga los datos para ver los jugadores
     */
    public ELIMINAR_JUGADOR() {
        // Inicializar componentes
        JUGADORES = new JList<>();
        Eliminar = new JButton("Eliminar jugador");
        ELIMINARJUGADOR = new JPanel(new BorderLayout()); // Inicializa el panel principal

        // Configurar la lista
        JUGADORES.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JUGADORES.setModel(modeloJugadores);

        // Crear scroll para la lista
        JScrollPane scrollPane = new JScrollPane(JUGADORES);

        // Panel inferior con el botón
        JPanel panelBoton = new JPanel();
        panelBoton.add(Eliminar);

        // Agregar componentes al panel principal
        ELIMINARJUGADOR.add(scrollPane, BorderLayout.CENTER);
        ELIMINARJUGADOR.add(panelBoton, BorderLayout.SOUTH);

        // Cargar los datos
        cargarJugadores();

        // Configurar el botón
        Eliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarJugador();
            }
        });
    }

    /**
     * @author Misha
     * Carga los datos de los jugadores
     */
    private void cargarJugadores() {
        modeloJugadores.clear();
        listaid.clear();

        try {
            Connection conn = GestorBD.conectar();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM JUGADOR");

            while (rs.next()) {
                int id = rs.getInt("ID_JUGADOR");
                String nombre = rs.getString("NOMBRE");
                String apellido = rs.getString("APELLIDO");
                String nickname = rs.getString("NICKNAME");

                String jugadorCompleto = nombre + " " + apellido + " (" + nickname + ")";
                listaid.add(id);
                modeloJugadores.addElement(jugadorCompleto);
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al cargar jugadores: " + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);

        }
    }

    /**
     * @author Misha
     * Elimina el jugador seleccionado
     */
    private void eliminarJugador() {
        int seleccionado = JUGADORES.getSelectedIndex();

        if (seleccionado == -1) {
            JOptionPane.showMessageDialog(null, "Selecciona un Jugador para eliminar.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);

            return;
        }

        int idJugador = listaid.get(seleccionado);

        try {
            Connection conn = GestorBD.conectar();
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM JUGADOR WHERE ID_JUGADOR = ?");
            stmt.setInt(1, idJugador);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(null, "Jugador eliminado correctamente.", "INFO.!", JOptionPane.INFORMATION_MESSAGE);

                modeloJugadores.removeElementAt(seleccionado);
                listaid.remove(seleccionado);
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el jugador.", "ATENCION.!", JOptionPane.WARNING_MESSAGE);

            }

            stmt.close();
            conn.close();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al eliminar jugador: " + ex.getMessage(), "ERROR.!", JOptionPane.ERROR_MESSAGE);

        }
    }


}

